package com.example.imagehosting.services;

import com.example.imagehosting.config.AppConstants;
import com.example.imagehosting.config.ApplicationUserDetails;
import com.example.imagehosting.dto.RegistrationRequest;
import com.example.imagehosting.entity.Image;
import com.example.imagehosting.entity.User;
import com.example.imagehosting.entity.Visibility;
import com.example.imagehosting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.imagehosting.config.AppConstants.DEFAULT_STORAGE_IN_BYTES;

@Service
public class UserService implements UserDetailsService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private ImageService imageService;
    private S3Client s3;

    public UserService(UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        @Lazy ImageService imageService,
        S3Client s3){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.imageService = imageService;
        this.s3 = s3;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUsername(username);
        userOptional.orElseThrow(() -> new UsernameNotFoundException("User name [" + username + "] not found."));
        return new ApplicationUserDetails(userOptional.get());
    }

    public User processRegistrationRequest(RegistrationRequest registrationRequest) {
        User user = new User();
        user.setUsername(registrationRequest.getUsername());
        user.setEmail(registrationRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        user.setAssignedCloudStorage(DEFAULT_STORAGE_IN_BYTES);
        user.setFreeSpaceAvailaible(DEFAULT_STORAGE_IN_BYTES);
        user.setRoles("ROLE_USER");

        return userRepository.save(user);
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional.orElseThrow(() -> new UsernameNotFoundException("No user found with username: "+ username));
    }

    public boolean userHasFreeSpace(long size) {
        User currentUser = getCurrentUser();
        return (int) size < currentUser.getFreeSpaceAvailaible();
    }

    public void updateUserStorage(Integer size) {
        User currentUser = getCurrentUser();
        currentUser.setFreeSpaceAvailaible(currentUser.getFreeSpaceAvailaible() - size);
        userRepository.save(currentUser);
    }

    public void deleteImage(Integer id){

        User currentUser = getCurrentUser();
        Image imageById = imageService.getImageById(id);

        if(!imageById.getUploadedBy().equals(currentUser))
            throw new AccessDeniedException("Unauthorized operation");

        //Delete thumbnail
        s3.deleteObject(DeleteObjectRequest.builder()
                        .key(imageById.getS3Identifier()+"_thumbnail")
                        .bucket(AppConstants.S3_BUCKET_NAME)
                .build());

        //Delete image
        s3.deleteObject(DeleteObjectRequest.builder()
                        .key(imageById.getS3Identifier())
                        .bucket(AppConstants.S3_BUCKET_NAME)
                .build());

        currentUser.setFreeSpaceAvailaible(currentUser.getFreeSpaceAvailaible() + imageById.getSize());

        userRepository.save(currentUser);
        imageService.deleteImage(id);

    }

    public void updateImage(Integer id, Map<String, String> update) {
        Image imageById = imageService.getImageById(id);
        imageById.setVisibility(Visibility.valueOf(update.get("visibility")));
        imageService.updateImage(imageById);

        if(imageById.getVisibility().equals(Visibility.PRIVATE)){
            List.of(imageById.getS3Identifier(), imageById.getS3Identifier()+"_thumbnail").forEach(object -> {
                s3.putObjectAcl(PutObjectAclRequest.builder()
                                .key(object)
                                .bucket(AppConstants.S3_BUCKET_NAME)
                                .acl(ObjectCannedACL.PRIVATE)
                            .build());
            });
        } else {
            List.of(imageById.getS3Identifier(), imageById.getS3Identifier()+"_thumbnail").forEach(object -> {
                s3.putObjectAcl(PutObjectAclRequest.builder()
                        .key(object)
                        .bucket(AppConstants.S3_BUCKET_NAME)
                        .acl(ObjectCannedACL.PUBLIC_READ)
                        .build());
            });
        }
    }
}
