package com.example.imagehosting.services;

import com.example.imagehosting.config.AppConstants;
import com.example.imagehosting.dto.ImageReadDTO;
import com.example.imagehosting.dto.ImageUploadDTO;
import com.example.imagehosting.entity.Image;
import com.example.imagehosting.entity.User;
import com.example.imagehosting.exception.InvalidInputException;
import com.example.imagehosting.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.imagehosting.config.AppConstants.S3_BUCKET_NAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    private final S3Client s3;
    private final ImageRepository imageRepository;
    private final UserService userService;

    public Image uploadImageToS3(ImageUploadDTO imageUploadDTO){
        String uuid = UUID.randomUUID().toString();
        Image image = new Image();
        User currentUser = userService.getCurrentUser();

        PutObjectRequest request = PutObjectRequest.builder()
                .key(uuid)
                .contentType(imageUploadDTO.getFile().getContentType())
                .bucket("spring-image-bucket-2922")
                .build();

        log.info("PutObjectRequest = {}", request);
        PutObjectResponse putObjectResponse;

        try{
            putObjectResponse = s3.putObject(request, RequestBody.fromBytes(imageUploadDTO.getFile().getBytes()));
        } catch (Exception e){
            throw new RuntimeException(e);
        }

        log.info("PutObjectResponse = {}", putObjectResponse);

        image.setS3Identifier(uuid);
        image.setTitle(imageUploadDTO.getFile().getOriginalFilename());
        image.setSize((int) imageUploadDTO.getFile().getSize());
        image.setVisibility(imageUploadDTO.getVisibility());
        image.setUploadedBy(currentUser);

        log.info("New image object = {}", image);

        userService.updateUserStorage(image.getSize());
        return saveImageMetadata(image);
    }

    private Image saveImageMetadata(Image image){
        return imageRepository.save(image);
    }

    public List<Image> getAllPublicImages() {
        return imageRepository.getAllPublicImages();
    }

    public List<Image> getImagesByUser(Integer userId){
        return imageRepository.findByUserId(userId);
    }

    public static String constructS3ImageUrl(String key){
        return "https://"+ S3_BUCKET_NAME +".s3.amazonaws.com/" + key;
    }

    public Image getImageById(Integer id) {
        Optional<Image> imgOpt = imageRepository.findById(id);
        imgOpt.orElseThrow(() -> new InvalidInputException("Cannot find image with ID: " + id));
        return imgOpt.get();
    }
}
