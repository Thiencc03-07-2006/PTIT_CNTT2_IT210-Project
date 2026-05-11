package com.healthcare.service.impl;

import com.healthcare.model.entity.Doctor;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;

    @Override
    public List<Doctor> findBySpecialty(Long specialtyId) {
        return doctorRepository.findBySpecialtyId(specialtyId);
    }
}
