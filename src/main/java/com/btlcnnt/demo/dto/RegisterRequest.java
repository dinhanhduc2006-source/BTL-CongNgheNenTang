package com.btlcnnt.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

  @NotBlank(message = "Username không được để trống")
  @Size(min = 4, max = 30, message = "Username phải từ 4 đến 30 ký tự")
  @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username chỉ gồm chữ cái, số, chấm, gạch dưới/ngang")
  private String username;

  @NotBlank(message = "Họ và tên không được để trống")
  @Size(min = 2, max = 100, message = "Họ và tên từ 2 đến 100 ký tự")
  private String fullName;

  @NotBlank(message = "Mật khẩu không được để trống")
  @Size(min = 8, max = 64, message = "Mật khẩu từ 8 đến 64 ký tự")
  @Pattern(
      regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
      message = "Mật khẩu phải có ít nhất 8 ký tự, gồm chữ hoa, số và ký tự đặc biệt (@$!%*?&)"
  )
  private String password;
}