package com.healthcare.model.dto.request;

import lombok.Data;

@Data
public class UpgradeDoctorRequest {
    private Long userId;
    private Long specialtyId;
    private Integer experienceYears;
    private String bio;
}
