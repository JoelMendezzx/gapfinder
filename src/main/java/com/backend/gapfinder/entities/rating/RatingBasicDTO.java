package com.backend.gapfinder.entities.rating;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RatingBasicDTO {
    private Long id;
    private int rating;
    private Boolean wouldRepeat;
    private LocalDateTime createdAt;

}
