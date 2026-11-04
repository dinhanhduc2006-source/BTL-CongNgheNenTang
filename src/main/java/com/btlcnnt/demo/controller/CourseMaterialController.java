package com.btlcnnt.demo.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/materials")
public class CourseMaterialController {

  private final String BASE_PATH = Paths.get("demo", "data").toAbsolutePath().toString() + File.separator;

  @GetMapping("/video")
  public ResponseEntity<ResourceRegion> streamVideo(@RequestHeader HttpHeaders headers) throws IOException {
    File file = new File(BASE_PATH + "lesson.mp4");
    if (!file.exists()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    Resource video = new FileSystemResource(file);
    long contentLength = video.contentLength();
    HttpRange range = headers.getRange().isEmpty() ? null : headers.getRange().get(0);
    long chunkSize = Math.min(1024 * 1024L, contentLength); // Khối 1MB

    if (range != null) {
      long start = range.getRangeStart(contentLength);
      long end = range.getRangeEnd(contentLength);
      long rangeLength = Math.min(chunkSize, end - start + 1);
      return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
          .contentType(MediaType.valueOf("video/mp4"))
          .body(new ResourceRegion(video, start, rangeLength));
    } else {
      return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
          .contentType(MediaType.valueOf("video/mp4"))
          .body(new ResourceRegion(video, 0, chunkSize));
    }
  }

  @GetMapping("/document")
  public ResponseEntity<Resource> downloadDocument() {
    File file = new File(BASE_PATH + "document.pdf");
    if (!file.exists()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_PDF)
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"document.pdf\"")
        .body(new FileSystemResource(file));
  }

  @GetMapping("/quiz")
  public ResponseEntity<String> getQuiz() {
    File file = new File(BASE_PATH + "quiz.json");
    if (!file.exists()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("[]");
    }

    try {
      String jsonContent = Files.readString(file.toPath());
      return ResponseEntity.ok()
          .contentType(MediaType.APPLICATION_JSON)
          .body(jsonContent);
    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}