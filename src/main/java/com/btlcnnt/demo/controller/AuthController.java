package com.btlcnnt.demo.controller;


import com.btlcnnt.demo.dto.RegisterRequest;
import com.btlcnnt.demo.entity.User;
import com.btlcnnt.demo.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Locale;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
    String username = request.getUsername().trim().toLowerCase(Locale.ROOT);
    if (userRepository.existsByUsername(username)) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .body(Map.of("username", "Tên đăng nhập đã tồn tại trong hệ thống"));
    }

    User user = User.builder()
        .username(username)
        .fullName(request.getFullName().trim())
        .password(passwordEncoder.encode(request.getPassword()))
        .role("ROLE_STUDENT")
        .build();

    userRepository.save(user);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(Map.of("message", "Đăng ký thành công!", "username", user.getUsername()));
  }
}