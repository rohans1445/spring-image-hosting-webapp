package com.example.imagehosting.dto;

import com.example.imagehosting.entity.Visibility;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadDTO {
    private MultipartFile file;
    private Visibility visibility;
}
