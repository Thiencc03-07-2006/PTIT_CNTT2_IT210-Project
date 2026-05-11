package com.healthcare.repository;

import com.healthcare.model.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalRecordRepository
        extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByAppointmentPatientIdOrderByAppointment_AppointmentDateDesc(Long patientId);
}