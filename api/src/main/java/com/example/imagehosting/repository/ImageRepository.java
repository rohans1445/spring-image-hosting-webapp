package com.example.imagehosting.repository;

import com.example.imagehosting.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {
    @Query("SELECT i FROM Image i WHERE i.uploadedBy.id = :userId")
    List<Image> findByUserId(Integer userId);

    @Query("SELECT i from Image i WHERE i.visibility = com.example.imagehosting.entity.Visibility.PUBLIC")
    List<Image> getAllPublicImages();
}
