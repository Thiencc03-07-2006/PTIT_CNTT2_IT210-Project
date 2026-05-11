package com.healthcare.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "medicines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String unit; // Đơn vị tính (Viên, Lọ, Vỉ...)

    @Column(nullable = false)
    private Integer stockQuantity;

    private Double price;
}