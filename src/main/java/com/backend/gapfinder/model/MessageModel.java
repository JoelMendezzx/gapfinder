package com.backend.gapfinder.model;

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
public class MessageModel extends BaseModel {

    @Column(length = 2000)
    private String content;

    private LocalDateTime sentAt;

    // Usuario que envió el mensaje
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserModel sender;

    // Match al que pertenece este mensaje (nullable si pertenece a una Open Table)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private MatchModel match;

    // Open Table a la que pertenece este mensaje (nullable si pertenece a un Match)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "open_table_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private OpenTableModel openTable;

}