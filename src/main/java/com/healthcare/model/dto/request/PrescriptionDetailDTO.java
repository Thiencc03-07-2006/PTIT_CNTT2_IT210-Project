package com.healthcare.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PrescriptionDetailDTO {

    @NotNull(message = "Vui lòng chọn thuốc")
    private Long medicineId;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải >= 1")
    private Integer quantity;

    @NotBlank(message = "Hướng dẫn sử dụng không được để trống")
    private String usageInstruction;
}