package com.example.org.application.service;


import com.example.org.application.dto.ImageDetailsDto;
import com.example.org.application.dto.ImageUploadResponseDto;
import com.example.org.application.exceptions.ResourceNotFoundException;
import com.example.org.application.model.Image;
import com.example.org.application.repository.ImageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StorageService {
    @Value("${application.bucket.name}")
    private String bucketName;

    @Autowired
    private S3Client s3Client;

    @Autowired
    private ImageRepository imageRepository;

    public ImageUploadResponseDto uploadImage(MultipartFile file) {
        try {
            String fileName = generateFileName(file);
            String s3Key = "images/" + fileName;

            // Upload to S3
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // Create file URL
            String fileUrl = String.format("https://%s.s3.amazonaws.com/%s", bucketName, s3Key);

            // Save to database
            Image image = new Image();
            image.setFileName(fileName);
            image.setFileUrl(fileUrl);
            image.setS3Key(s3Key);
            image.setFileSize(file.getSize());
            image.setFileType(file.getContentType());
            image.setUploadDate(LocalDateTime.now());

            Image savedImage = imageRepository.save(image);

            return mapToResponseDto(savedImage);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    public void deleteImage(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found"));

        // Delete from S3
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(image.getS3Key())
                .build();

        s3Client.deleteObject(deleteObjectRequest);

        // Delete from database
        imageRepository.delete(image);
    }

    public ImageDetailsDto getImage(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found"));
        return mapToDetailsDto(image);
    }

    public List<ImageDetailsDto> getAllImages() {
        List<Image> images = imageRepository.findAll();
        return images.stream()
                .map(this::mapToDetailsDto)
                .collect(Collectors.toList());
    }

    private String generateFileName(MultipartFile file) {
        return UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
    }

    private ImageUploadResponseDto mapToResponseDto(Image image) {
        ImageUploadResponseDto dto = new ImageUploadResponseDto();
        dto.setId(image.getId());
        dto.setFileName(image.getFileName());
        dto.setFileUrl(image.getFileUrl());
        dto.setFileType(image.getFileType());
        dto.setFileSize(image.getFileSize());
        dto.setUploadDate(image.getUploadDate());
        return dto;
    }

    private ImageDetailsDto mapToDetailsDto(Image image) {
        ImageDetailsDto dto = new ImageDetailsDto();
        dto.setId(image.getId());
        dto.setFileName(image.getFileName());
        dto.setFileUrl(image.getFileUrl());
        dto.setFileType(image.getFileType());
        dto.setUploadDate(image.getUploadDate());
        return dto;
    }
}
