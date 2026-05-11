package com.healthcare.model.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class MedicalHistoryResponseDTO {

    // Thông tin tổng quan lịch khám
    private Long appointmentId;
    private LocalDate appointmentDate;

    // Thông tin Bác sĩ (Lấy từ bảng Users + Specialties)
    private String doctorName;
    private String specialtyName;

    // Kết quả khám (Lấy từ bảng Medical_Records)
    private String symptoms;
    private String diagnosis;

    // Chi tiết thuốc (Lấy từ bảng Prescriptions + Prescription_Details + Medicines)
    private List<PrescriptionItemDTO> medicines;

    // Lớp DTO nội bộ (Nested DTO) để biểu diễn từng dòng thuốc
    @Getter
    @Setter
    @Builder
    public static class PrescriptionItemDTO {
        private String medicineName;
        private String usageInstruction; // Hướng dẫn sử dụng (VD: "Sáng 1 viên, tối 1 viên")
        private Integer quantity;
    }
}
