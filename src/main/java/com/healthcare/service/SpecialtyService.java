package com.healthcare.service;

import com.healthcare.model.entity.Specialty;

import java.util.List;

public interface SpecialtyService {
    List<Specialty> getAllSpecialties();

    Specialty getById(Long id);

    Specialty createSpecialty(Specialty specialty);

    Specialty updateSpecialty(Long id, Specialty specialtyDetails);

    void deleteSpecialty(Long id);
}