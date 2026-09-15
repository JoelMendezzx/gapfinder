package com.backend.gapfinder.entities.userlocationlog;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserLocationLogBasicDTO {
    private Long id;
    private double latitude;
    private double longitude;
    private LocalDateTime recordedAt;

}