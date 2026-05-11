package com.healthcare.model.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class BookingRequestDTO {
    @NotNull(message = "Vui lòng chọn chuyên khoa")
    private Long specialtyId; // Chuyên khoa

    @NotNull(message = "Vui lòng chọn bác sĩ")
    private Long doctorId;    // Bác sĩ được chọn

    @NotNull(message = "Vui lòng chọn ngày khám")
    @FutureOrPresent(message = "Ngày khám phải từ hôm nay trở đi")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate appointmentDate; // Ngày khám

    @NotNull(message = "Vui lòng chọn giờ khám")
    private LocalTime appointmentTime; // Giờ khám

    @Size(max = 500, message = "Ghi chú không được vượt quá 500 ký tự")
    private String notes; // Ghi chú triệu chứng ban đầu
}
