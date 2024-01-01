package com.example.imagehosting.config;

import org.springframework.beans.factory.annotation.Value;

public class AppConstants {

//    @Value("${S3_BUCKET_NAME}")
    public static String S3_BUCKET_NAME = "spring-image-bucket-2922";
    public static Integer DEFAULT_STORAGE_IN_BYTES = 10_485_760; // 10 MB

}
