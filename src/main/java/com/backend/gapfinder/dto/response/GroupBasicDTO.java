package com.backend.gapfinder.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GroupBasicDTO {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
}
