package com.example.imagehosting.services;

import com.example.imagehosting.dto.ImageUploadDTO;
import com.example.imagehosting.entity.Image;
import com.example.imagehosting.entity.User;
import com.example.imagehosting.exception.InvalidInputException;
import com.example.imagehosting.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.imagehosting.config.AppConstants.S3_BUCKET_NAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    private final S3Client s3;
    private final ImageRepository imageRepository;
    private final UserService userService;

    public Image saveImage(ImageUploadDTO imageUploadDTO){
        String uuid = UUID.randomUUID().toString();
        Image image = new Image();
        User currentUser = userService.getCurrentUser();

        uploadImageToS3(uuid, imageUploadDTO);

        image.setS3Identifier(uuid);
        image.setTitle(imageUploadDTO.getFile().getOriginalFilename());
        image.setSize((int) imageUploadDTO.getFile().getSize());
        image.setVisibility(imageUploadDTO.getVisibility());
        image.setUploadedBy(currentUser);

        log.info("New image object = {}", image);

        userService.updateUserStorage(image.getSize());
        return saveImageMetadata(image);
    }

    private void uploadImageToS3(String uuid, ImageUploadDTO imageUploadDTO){

        uploadThumbnailToS3(uuid, imageUploadDTO);

        PutObjectRequest request = PutObjectRequest.builder()
                .key(uuid)
                .contentType(imageUploadDTO.getFile().getContentType())
                .bucket(S3_BUCKET_NAME)
                .build();

        log.info("imageRequest = {}", request);
        PutObjectResponse putObjectResponse;

        try{
            putObjectResponse = s3.putObject(request, RequestBody.fromBytes(imageUploadDTO.getFile().getBytes()));
        } catch (Exception e){
            throw new RuntimeException(e);
        }

        log.info("PutObjectResponse = {}", putObjectResponse);


    }

    private void uploadThumbnailToS3(String uuid, ImageUploadDTO imageUploadDTO){
        byte[] thumbnail;

        try{
            thumbnail = generateThumbnail(imageUploadDTO);
        } catch (Exception e){
            throw new RuntimeException("Something went wrong while generating thumbnails. E = " + e);
        }

        PutObjectRequest thumbnailReq = PutObjectRequest.builder()
                .key(uuid+"_thumbnail")
                .contentType("image/jpg")
                .bucket(S3_BUCKET_NAME)
                .build();

        log.info("thumbnailReq = {}", thumbnailReq);
        PutObjectResponse putObjectResponseThumbnail;

        try{
            putObjectResponseThumbnail = s3.putObject(thumbnailReq, RequestBody.fromBytes(thumbnail));
        } catch (Exception e){
            throw new RuntimeException(e);
        }

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

    // Convert user uploaded image into a thumbnail, return resulting image as byte array
    public byte[] generateThumbnail(ImageUploadDTO imageUploadDTO) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Thumbnails.of(imageUploadDTO.getFile().getInputStream())
                .size(300, 300)
                .keepAspectRatio(true)
                .outputQuality(0.3)
                .outputFormat("jpg")
                .toOutputStream(baos);
        byte[] bytes = baos.toByteArray();

        baos.close();

        return bytes;
    }

    public static String constructS3ImageUrl(String key){
        return "https://"+ S3_BUCKET_NAME +".s3.amazonaws.com/" + key;
    }

    public Image getImageById(Integer id) {
        Optional<Image> imgOpt = imageRepository.findById(id);
        imgOpt.orElseThrow(() -> new InvalidInputException("Cannot find image with ID: " + id));
        return imgOpt.get();
    }

    public void deleteImage(Integer id){
        imageRepository.deleteById(id);
    }

    public void updateImage(Image i){
        imageRepository.save(i);
    }
}
