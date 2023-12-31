package com.example.imagehosting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserReadDTO {
    private String username;
    private String email;
    private Integer assignedCloudStorage;
    private Integer freeSpaceAvailable;
}
