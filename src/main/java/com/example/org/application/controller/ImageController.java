package com.example.org.application.controller;

import com.example.org.application.dto.ImageDetailsDto;
import com.example.org.application.dto.ImageUploadResponseDto;
import com.example.org.application.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/images")
@Slf4j
public class ImageController {

    @Autowired
    private StorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<ImageUploadResponseDto> uploadImage(@RequestParam("file") MultipartFile file) {
//        log.info("Received file upload request: {}", file.getOriginalFilename());
        ImageUploadResponseDto response = storageService.uploadImage(file);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {
        storageService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{imageId}")
    public ResponseEntity<ImageDetailsDto> getImage(@PathVariable Long imageId) {
        ImageDetailsDto image = storageService.getImage(imageId);
        return ResponseEntity.ok(image);
    }

    @GetMapping
    public ResponseEntity<List<ImageDetailsDto>> getAllImages() {
        List<ImageDetailsDto> images = storageService.getAllImages();
        return ResponseEntity.ok(images);
    }
}