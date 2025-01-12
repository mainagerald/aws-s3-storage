package com.example.org.application.dto;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ImageDetailsDto {
    private Long id;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private LocalDateTime uploadDate;
}