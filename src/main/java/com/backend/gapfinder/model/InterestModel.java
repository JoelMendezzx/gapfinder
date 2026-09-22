package com.backend.gapfinder.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

// Entidad que representa un interés/hobby de un usuario (Gaming, Coffee, etc.)
@Entity
@Table(name = "interests")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class InterestModel extends BaseModel {

    @Column(unique = true, nullable = false)
    private String name;

    // Usuarios que tienen este interés (lado inverso de la relación)
    @ManyToMany(mappedBy = "interests")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<UserModel> users = new ArrayList<>();

}