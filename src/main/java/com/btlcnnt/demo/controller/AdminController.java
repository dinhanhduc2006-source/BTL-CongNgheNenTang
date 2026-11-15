package com.btlcnnt.demo.controller;

import com.btlcnnt.demo.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

  private final UserRepository userRepository;

  public AdminController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @GetMapping("/users")
  public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
    List<Map<String, Object>> users = userRepository.findAll().stream()
        .map(user -> Map.<String, Object>of(
            "id", user.getId(),
            "username", user.getUsername(),
            "fullName", user.getFullName(),
            "role", user.getRole()
        ))
        .toList();
    return ResponseEntity.ok(users);
  }
}
