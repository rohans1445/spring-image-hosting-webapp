package com.example.imagehosting.dto;

import com.example.imagehosting.entity.Visibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageReadDTO {
    private Integer id;
    private String title;
    private String uploadedBy;
    private LocalDateTime uploadedOn;
    private String url;
    private Visibility visibility;
    private Integer size;
}
