package com.healthcare.repository;

import com.healthcare.model.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    boolean existsByName(String name);

    List<Medicine> findByStockQuantityGreaterThan(Integer quantity);
}
