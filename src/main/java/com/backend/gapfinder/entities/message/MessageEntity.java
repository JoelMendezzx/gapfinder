package com.backend.gapfinder.entities.message;

import com.backend.gapfinder.BaseEntity;
import com.backend.gapfinder.entities.user.UserEntity;
import com.backend.gapfinder.entities.match.MatchEntity;
import com.backend.gapfinder.entities.opentable.OpenTableEntity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

// Entidad que representa un mensaje de chat, perteneciente a un Match o a una Open Table
@Entity
@Table(name = "messages")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class MessageEntity extends BaseEntity {

    @Column(length = 2000)
    private String content;

    private LocalDateTime sentAt;

    // Usuario que envió el mensaje
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserEntity sender;

    // Match al que pertenece este mensaje (nullable si pertenece a una Open Table)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private MatchEntity match;

    // Open Table a la que pertenece este mensaje (nullable si pertenece a un Match)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "open_table_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private OpenTableEntity openTable;

}