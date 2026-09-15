package com.backend.gapfinder.entities.message;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageBasicDTO {
    private Long id;
    private String content;
    private LocalDateTime sentAt;
    private String senderName;
}
