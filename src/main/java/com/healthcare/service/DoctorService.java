package com.healthcare.service;

import com.healthcare.model.entity.Doctor;

import java.util.List;

public interface DoctorService {
    public List<Doctor> findBySpecialty(Long specialtyId);
}
