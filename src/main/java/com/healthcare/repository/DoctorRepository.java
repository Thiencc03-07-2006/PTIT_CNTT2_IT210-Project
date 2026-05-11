package com.healthcare.repository;

import com.healthcare.model.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    // Tìm danh sách bác sĩ dựa trên ID chuyên khoa
    List<Doctor> findBySpecialtyId(Long specialtyId);
}