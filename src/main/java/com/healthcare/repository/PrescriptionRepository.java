package com.healthcare.repository;

import com.healthcare.model.entity.Prescription;
import com.healthcare.model.entity.PrescriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionRepository
        extends JpaRepository<Prescription, Long> {
    List<Prescription> findByStatus(PrescriptionStatus prescriptionStatus);
}