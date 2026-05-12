package com.healthcare.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpecialtyDTO {
    private Long id;

    @NotBlank(message = "Tên chuyên khoa không được để trống")
    @Size(min = 2, max = 100, message = "Tên chuyên khoa từ 2-100 ký tự")
    private String name;

    @NotBlank(message = "Mô tả không được để trống")
    @Size(min = 10, message = "Mô tả phải có ít nhất 10 ký tự")
    private String description;
}