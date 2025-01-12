package com.example.org.application.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ImageUploadResponseDto {
    private Long id;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private LocalDateTime uploadDate;
}
