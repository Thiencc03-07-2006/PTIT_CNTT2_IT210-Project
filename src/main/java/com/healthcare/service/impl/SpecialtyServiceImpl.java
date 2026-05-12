package com.healthcare.service.impl;

import com.healthcare.model.entity.Specialty;
import com.healthcare.repository.SpecialtyRepository;
import com.healthcare.service.SpecialtyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;

    @Override
    public List<Specialty> getAllSpecialties() {
        return specialtyRepository.findAll();
    }

    @Override
    public Specialty getById(Long id) {
        return specialtyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyên khoa với ID: " + id));
    }

    @Override
    public Specialty createSpecialty(Specialty specialty) {
        // Kiểm tra trùng tên khi thêm mới hoặc cập nhật
        if (specialtyRepository.existsByName(specialty.getName())) {
            throw new RuntimeException("Tên chuyên khoa '" + specialty.getName() + "' đã tồn tại!");
        }
        return specialtyRepository.save(specialty);
    }

    @Override
    public Specialty updateSpecialty(Long id, Specialty specialtyDetails) {
        Specialty existingSpecialty = getById(id);
        // Cập nhật các trường thông tin
        if (isNameExists(specialtyDetails.getName(), existingSpecialty.getId())) {
            throw new RuntimeException("Tên chuyên khoa '" + specialtyDetails.getName() + "' đã tồn tại!");
        }
        existingSpecialty.setName(specialtyDetails.getName());
        existingSpecialty.setDescription(specialtyDetails.getDescription());
        return specialtyRepository.save(existingSpecialty);
    }

    @Override
    public void deleteSpecialty(Long id) {
        Specialty existingSpecialty = getById(id);

        // Kiểm tra nếu chuyên khoa đang có bác sĩ thì không cho xóa (Tùy thuộc vào business logic của bạn)
        if (existingSpecialty.getDoctors() != null && !existingSpecialty.getDoctors().isEmpty()) {
            throw new RuntimeException("Không thể xóa chuyên khoa đã có bác sĩ!");
        }

        specialtyRepository.delete(existingSpecialty);
    }

    private boolean isNameExists(String name, Long id) {
        return specialtyRepository.findAll().stream()
                .anyMatch(s -> s.getName().equalsIgnoreCase(name) && !s.getId().equals(id));
    }
}