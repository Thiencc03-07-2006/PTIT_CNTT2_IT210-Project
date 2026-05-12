package com.healthcare.service.impl;

import com.healthcare.model.dto.request.MedicineDTO;
import com.healthcare.model.entity.Medicine;
import com.healthcare.repository.MedicineRepository;
import com.healthcare.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicineServiceImpl implements MedicineService {

    @Autowired
    private MedicineRepository medicineRepository;

    @Override
    public List<MedicineDTO> getAllMedicines() {
        return medicineRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MedicineDTO getMedicineById(Long id) {
        return medicineRepository.findById(id).map(this::mapToDTO).orElse(null);
    }

    @Override
    public MedicineDTO createMedicine(MedicineDTO dto) {
        if (medicineRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Tên thuốc đã tồn tại trong hệ thống!");
        }
        Medicine medicine = mapToEntity(dto);
        Medicine savedMedicine = medicineRepository.save(medicine);
        return mapToDTO(savedMedicine);
    }

    @Override
    public MedicineDTO updateMedicine(Long id, MedicineDTO dto) {
        Medicine existingMedicine = medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thuốc với ID: " + id));

        existingMedicine.setName(dto.getName());
        existingMedicine.setUnit(dto.getUnit());
        existingMedicine.setPrice(dto.getPrice());
        existingMedicine.setStockQuantity(dto.getStockQuantity());

        return mapToDTO(medicineRepository.save(existingMedicine));
    }

    @Override
    public void deleteMedicine(Long id) {
        if (!medicineRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy thuốc để xóa!");
        }
        medicineRepository.deleteById(id);
    }

    // Các hàm Helper để chuyển đổi qua lại giữa Entity và DTO
    private MedicineDTO mapToDTO(Medicine entity) {
        MedicineDTO dto = new MedicineDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setUnit(entity.getUnit());
        dto.setPrice(entity.getPrice());
        dto.setStockQuantity(entity.getStockQuantity());
        return dto;
    }

    private Medicine mapToEntity(MedicineDTO dto) {
        Medicine entity = new Medicine();
        entity.setName(dto.getName());
        entity.setUnit(dto.getUnit());
        entity.setPrice(dto.getPrice());
        entity.setStockQuantity(dto.getStockQuantity());
        return entity;
    }

    public List<Medicine> findAllAvailable() {
        return medicineRepository.findByStockQuantityGreaterThan(0);
    }
}
