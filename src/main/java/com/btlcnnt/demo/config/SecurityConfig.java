package com.btlcnnt.demo.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            // 1. Khách (Chưa xác thực): Chỉ vào được trang Login & API Auth
            .requestMatchers("/login.html", "/api/auth/**", "/css/**", "/js/**").permitAll()

            // 2. Quản trị viên (ROLE_ADMIN): Toàn quyền quản trị
            .requestMatchers("/admin.html", "/api/admin/**").hasRole("ADMIN")

            // 3. Sinh viên & Admin: Được vào phòng học, xem video 206, tải PDF, làm Quiz
            .requestMatchers("/classroom.html", "/api/materials/**").hasAnyRole("STUDENT", "ADMIN")

            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/login.html")
            .loginProcessingUrl("/perform_login")
            .successHandler((request, response, authentication) -> {
              boolean isAdmin = authentication.getAuthorities().stream()
                  .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
              response.setStatus(HttpServletResponse.SC_OK);
              response.setContentType("application/json;charset=UTF-8");
              String targetUrl = isAdmin ? "/admin.html" : "/classroom.html";
              response.getWriter().write("{\"redirect\": \"" + targetUrl + "\"}");
            })
            .failureHandler((request, response, exception) -> {
              response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
              response.setContentType("application/json;charset=UTF-8");
              response.getWriter().write("{\"error\": \"Sai tên đăng nhập hoặc mật khẩu!\"}");
            })
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login.html")
            .permitAll()
        );

    return http.build();
  }
}