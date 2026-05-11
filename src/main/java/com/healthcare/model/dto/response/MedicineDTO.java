package com.healthcare.model.dto.response;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicineDTO {

    private Long id;

    @NotBlank(message = "Tên thuốc không được để trống")
    @Size(min = 2, max = 100, message = "Tên thuốc phải từ 2 đến 100 ký tự")
    private String name;

    @NotBlank(message = "Đơn vị không được để trống")
    private String unit;

    @NotNull(message = "Giá không được để trống")
    @Positive(message = "Giá phải lớn hơn 0")
    private Double price;

    @NotNull(message = "Số lượng tồn kho không được để trống")
    @Min(value = 0, message = "Tồn kho không được âm")
    private Integer stockQuantity;
}