package com.healthcare.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpgradeDoctorRequestDTO {
    @NotNull(message = "ID người dùng không được để trống")
    private Long userId;

    @NotNull(message = "Vui lòng chọn chuyên khoa")
    private Long specialtyId;

    @NotNull(message = "Vui lòng nhập số năm kinh nghiệm")
    @Min(value = 0, message = "Kinh nghiệm không được là số âm")
    private Integer experienceYears;

    @NotBlank(message = "Vui lòng nhập tiểu sử bác sĩ")
    private String bio;
}