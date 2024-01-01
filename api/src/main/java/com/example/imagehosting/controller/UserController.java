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
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @DeleteMapping("/me/images/{id}")
    public ResponseEntity<Map<String, String>> deleteImage(@PathVariable("id") Integer id){
        Map<String, String> result = new HashMap<>();
        userService.deleteImage(id);
        result.put("message", "Image deleted");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("/me/images/{id}")
    public ResponseEntity<Map<String, String>> updateImage(@PathVariable("id") Integer id, @RequestBody Map<String, String> map){
        Map<String, String> result = new HashMap<>();
        userService.updateImage(id, map);
        result.put("message", "Image updated");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
