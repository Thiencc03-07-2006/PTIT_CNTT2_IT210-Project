package com.healthcare.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CompleteAppointmentDTO {
    @NotBlank(message = "Triệu chứng không được để trống")
    private String symptoms;

    @NotBlank(message = "Chẩn đoán không được để trống")
    private String diagnosis;

    @NotEmpty(message = "Vui lòng kê ít nhất một loại thuốc")
    @Valid // Quan trọng: Để kích hoạt validate các field trong PrescriptionDetailDTO
    private List<PrescriptionDetailDTO> medicines;
}