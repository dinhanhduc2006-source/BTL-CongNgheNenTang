package com.btlcnnt.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
    String error = ex.getBindingResult().getAllErrors().stream()
        .map(ObjectError::getDefaultMessage)
        .filter(Objects::nonNull)
        .collect(Collectors.joining("; "));
    if (error.isBlank()) {
      error = "Yêu cầu không hợp lệ";
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", error));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
    String error = ex.getMessage() == null ? "Tham số không hợp lệ" : ex.getMessage();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", error));
  }
}