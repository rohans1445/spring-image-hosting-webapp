package com.example.imagehosting.controller;

import com.example.imagehosting.dto.ImageReadDTO;
import com.example.imagehosting.dto.ImageUploadDTO;
import com.example.imagehosting.entity.Image;
import com.example.imagehosting.entity.Visibility;
import com.example.imagehosting.exception.InvalidInputException;
import com.example.imagehosting.services.ImageService;
import com.example.imagehosting.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.imagehosting.services.ImageService.constructS3ImageUrl;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;
    private final UserService userService;

    @PostMapping(path = "/images/upload",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> uploadFile(@RequestParam(name = "file") MultipartFile file,
                                            @RequestParam(name = "visibility") Visibility visibility) {


        if(!userService.userHasFreeSpace(file.getSize())){
            throw new InvalidInputException("You do not have enough storage available. Current free space available: "+ (userService.getCurrentUser().getFreeSpaceAvailaible()/1024)/1024 +" MB" );
        }

        Image savedImage = imageService.uploadImageToS3(new ImageUploadDTO(file, visibility));

        return new ResponseEntity<>("UPLOADED " + file.getOriginalFilename(), HttpStatus.CREATED);
    }

    @GetMapping("/images")
    public ResponseEntity<List<ImageReadDTO>> getAllImages(){
        List<Image> allImages = imageService.getAllPublicImages();
        List<ImageReadDTO> result = allImages.stream().map(image -> ImageReadDTO.builder()
                .id(image.getId())
                .title(image.getTitle())
                .size(image.getSize())
                .visibility(image.getVisibility())
                .uploadedOn(image.getUploadedOn())
                .uploadedBy(image.getUploadedBy().getUsername())
                .url(constructS3ImageUrl(image.getS3Identifier()))
                .build()).collect(Collectors.toList());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/images/{id}")
    public ResponseEntity<ImageReadDTO> getImageById(@PathVariable("id") Integer id){
        Image image = imageService.getImageById(id);
        ImageReadDTO result = ImageReadDTO.builder()
                .id(image.getId())
                .title(image.getTitle())
                .size(image.getSize())
                .visibility(image.getVisibility())
                .uploadedBy(image.getUploadedBy().getUsername())
                .url(constructS3ImageUrl(image.getS3Identifier()))
                .build();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
