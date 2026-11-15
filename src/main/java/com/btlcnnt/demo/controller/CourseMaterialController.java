package com.btlcnnt.demo.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/materials")
public class CourseMaterialController {

  private static final long CHUNK_SIZE = 1024 * 1024;

  @GetMapping("/video")
  public ResponseEntity<ResourceRegion> streamVideo(@RequestHeader HttpHeaders headers) throws IOException {
    Path videoPath = resolveStorageDirectory().resolve("lesson.mp4");
    if (!Files.isRegularFile(videoPath)) {
      return ResponseEntity.notFound().build();
    }

    Resource video = new FileSystemResource(videoPath);
    ResourceRegion region = getResourceRegion(video, headers, video.contentLength());
    return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
        .contentType(MediaTypeFactory.getMediaType(video).orElse(MediaType.parseMediaType("video/mp4")))
        .header(HttpHeaders.ACCEPT_RANGES, "bytes")
        .body(region);
  }

  private ResourceRegion getResourceRegion(Resource video, HttpHeaders headers, long contentLength) {
    List<HttpRange> ranges = headers.getRange();
    if (!ranges.isEmpty()) {
      HttpRange range = ranges.get(0);
      long start = range.getRangeStart(contentLength);
      long end = range.getRangeEnd(contentLength);
      long rangeLength = Math.min(CHUNK_SIZE, end - start + 1);
      return new ResourceRegion(video, start, rangeLength);
    }
    return new ResourceRegion(video, 0, Math.min(CHUNK_SIZE, contentLength));
  }

  @GetMapping("/document")
  public ResponseEntity<Resource> downloadDocument() {
    Path pdfPath = resolveStorageDirectory().resolve("document.pdf");
    if (!Files.isRegularFile(pdfPath)) {
      return ResponseEntity.notFound().build();
    }

    Resource pdf = new FileSystemResource(pdfPath);
    ContentDisposition disposition = ContentDisposition.attachment()
        .filename("GiaoTrinh_BaiGiang.pdf", StandardCharsets.UTF_8)
        .build();
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_PDF)
        .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
        .body(pdf);
  }

  @GetMapping(value = "/quiz", produces = "application/json;charset=UTF-8")
  public ResponseEntity<String> getQuiz() throws IOException {
    Path quizPath = resolveStorageDirectory().resolve("quiz.json");
    if (!Files.isRegularFile(quizPath)) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(Files.readString(quizPath, StandardCharsets.UTF_8));
  }

  private static Path resolveStorageDirectory() {
    Path storageDirectory = Paths.get("storage").toAbsolutePath().normalize();
    if (Files.isDirectory(storageDirectory)) {
      return storageDirectory;
    }

    Path projectStorageDirectory = Paths.get("demo", "storage").toAbsolutePath().normalize();
    return Files.isDirectory(projectStorageDirectory) ? projectStorageDirectory : storageDirectory;
  }
}
