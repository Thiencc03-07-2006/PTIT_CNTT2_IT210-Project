package com.healthcare.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "medical_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "appointment_id", referencedColumnName = "id", nullable = false)
    private Appointment appointment;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String symptoms; // Triệu chứng

    @Column(columnDefinition = "TEXT", nullable = false)
    private String diagnosis; // Chẩn đoán bệnh

    @OneToOne(mappedBy = "medicalRecord", cascade = CascadeType.ALL)
    private Prescription prescription;
}