package com.healthcare.service;

import com.healthcare.model.dto.request.MedicineDTO;

import java.util.List;

public interface MedicineService {
    List<MedicineDTO> getAllMedicines();

    MedicineDTO getMedicineById(Long id);

    MedicineDTO createMedicine(MedicineDTO medicineDTO);

    MedicineDTO updateMedicine(Long id, MedicineDTO medicineDTO);

    void deleteMedicine(Long id);
}