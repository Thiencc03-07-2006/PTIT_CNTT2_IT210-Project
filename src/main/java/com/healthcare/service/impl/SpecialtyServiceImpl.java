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
}