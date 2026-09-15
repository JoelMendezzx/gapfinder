package com.backend.gapfinder.entities.group;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GroupBasicDTO {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
}
