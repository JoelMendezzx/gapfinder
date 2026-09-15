package com.backend.gapfinder.entities.activity;

import lombok.Data;

@Data
public class ActivityBasicDTO {
    private Long id;
    private String title;
    private String description;
    private Integer durationMinutes;
}