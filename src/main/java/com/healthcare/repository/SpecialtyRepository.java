package com.healthcare.repository;

import com.healthcare.model.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
    // Các phương thức cơ bản findAll, findById đã được hỗ trợ sẵn
}