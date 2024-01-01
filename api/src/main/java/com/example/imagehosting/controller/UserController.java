package com.example.imagehosting.controller;

import com.example.imagehosting.dto.ImageReadDTO;
import com.example.imagehosting.dto.UserReadDTO;
import com.example.imagehosting.entity.Image;
import com.example.imagehosting.entity.User;
import com.example.imagehosting.exception.InvalidInputException;
import com.example.imagehosting.services.ImageService;
import com.example.imagehosting.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.imagehosting.services.ImageService.constructS3ImageUrl;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final ImageService imageService;
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserReadDTO> getCurrentUser(){
        User currentUser = userService.getCurrentUser();

        return new ResponseEntity<>(UserReadDTO.builder()
                .username(currentUser.getUsername())
                .email(currentUser.getEmail())
                .freeSpaceAvailable(currentUser.getFreeSpaceAvailaible())
                .assignedCloudStorage(currentUser.getAssignedCloudStorage())
                .build(), HttpStatus.OK);
    }

    @GetMapping("/me/images")
    public ResponseEntity<List<ImageReadDTO>> getMyImages() {
        User currentUser = userService.getCurrentUser();
        List<Image> imagesByUser = imageService.getImagesByUser(currentUser.getId());

        List<ImageReadDTO> result = imagesByUser.stream().map(image -> ImageReadDTO.builder()
                .id(image.getId())
                .title(image.getTitle())
                .uploadedOn(image.getUploadedOn())
                .size(image.getSize())
                .visibility(image.getVisibility())
                .uploadedBy(image.getUploadedBy().getUsername())
                .urlFullRes(constructS3ImageUrl(image.getS3Identifier()))
                .urlThumbnail(constructS3ImageUrl(image.getS3Identifier())+"_thumbnail")
                .build()).collect(Collectors.toList());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
