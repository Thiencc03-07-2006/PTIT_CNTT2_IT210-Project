package com.healthcare.service.impl;

import com.healthcare.model.dto.request.BookingRequestDTO;
import com.healthcare.model.dto.request.CompleteAppointmentDTO;
import com.healthcare.model.dto.request.PrescriptionDetailDTO;
import com.healthcare.model.entity.*;
import com.healthcare.repository.*;
import com.healthcare.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepo;
    private final DoctorRepository doctorRepo; // Để lấy thông tin User từ ID Doctor
    private final UserRepository userRepo;
    private final MedicalRecordRepository medicalRecordRepo;
    private final PrescriptionRepository prescriptionRepo;
    private final PrescriptionDetailRepository prescriptionDetailRepo;
    private final MedicineRepository medicineRepo;

    @Override
    @Transactional
    public void bookAppointment(Long patientId, BookingRequestDTO request) {

        // 1. Kiểm tra ngày quá khứ
        LocalDateTime appointmentDateTime = LocalDateTime.of(request.getAppointmentDate(), request.getAppointmentTime());
        if (appointmentDateTime.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Thời gian hẹn không hợp lệ (phải ở tương lai).");
        }

        // 2. Tìm thông tin Doctor (thực thể Doctor chứa User và Specialty)
        Doctor doctorInfo = doctorRepo.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ."));

        // 3. Chống xung đột (Dùng User ID của bác sĩ để check trong bảng Appointment)
        boolean isConflict = appointmentRepo.existsByDoctorIdAndAppointmentDateAndTimeSlotAndStatusNot(
                doctorInfo.getUser().getId(), // Lấy ID của User đóng vai trò bác sĩ
                request.getAppointmentDate(),
                request.getAppointmentTime(),
                AppointmentStatus.CANCELLED
        );

        if (isConflict) {
            throw new RuntimeException("Bác sĩ đã có lịch vào khung giờ này.");
        }

        // 4. Lưu lịch hẹn
        User patient = userRepo.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Bệnh nhân không tồn tại."));

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctorInfo) // Lưu User đóng vai trò bác sĩ
                .appointmentDate(request.getAppointmentDate())
                .timeSlot(request.getAppointmentTime()) // Khớp với field timeSlot của bạn
                .status(AppointmentStatus.PENDING)
                .notes(request.getNotes())
                .build();

        appointmentRepo.save(appointment);
    }

    @Override
    public List<Appointment> findByPatientId(Long id) {
        return appointmentRepo.findByPatientId(id);
    }

    @Override
    @Transactional
    public void cancelAppointment(Long appointmentId, Long patientId) {

        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy lịch hẹn."));

        // Kiểm tra lịch này có thuộc bệnh nhân hiện tại không
        if (!appointment.getPatient().getId().equals(patientId)) {
            throw new RuntimeException("Bạn không có quyền hủy lịch này.");
        }

        // Không cho hủy nếu đã hoàn thành
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new RuntimeException("Không thể hủy lịch đã hoàn thành.");
        }

        // Không cho hủy nếu đã hủy rồi
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new RuntimeException("Lịch này đã được hủy trước đó.");
        }

        LocalDateTime appointmentDateTime = LocalDateTime.of(
                appointment.getAppointmentDate(),
                appointment.getTimeSlot()
        );
        LocalDateTime now = LocalDateTime.now();


        if (now.plusHours(24).isAfter(appointmentDateTime)) {
            throw new RuntimeException(
                    "Chỉ được hủy lịch trước giờ khám ít nhất 24 giờ."
            );
        }


        // Hủy lịch
        appointment.setStatus(AppointmentStatus.CANCELLED);

        appointmentRepo.save(appointment);
    }

    @Override
    public Appointment findById(Long id) {
        return appointmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch khám"));
    }

    @Override
    @Transactional
    public void completeAppointment(
            Long appointmentId,
            Long doctorUserId,
            CompleteAppointmentDTO request
    ) {

        // 1. Tìm appointment
        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy lịch khám"));

        // 2. Kiểm tra đúng bác sĩ
        if (!appointment.getDoctor()
                .getUser()
                .getId()
                .equals(doctorUserId)) {

            throw new RuntimeException(
                    "Bạn không có quyền xử lý lịch khám này");
        }

        // 3. Kiểm tra trạng thái
        if (appointment.getStatus() != AppointmentStatus.PENDING
                && appointment.getStatus() != AppointmentStatus.IN_PROGRESS) {

            throw new RuntimeException(
                    "Ca khám không hợp lệ");
        }

        // 4. Tạo hồ sơ khám
        MedicalRecord medicalRecord = MedicalRecord.builder()
                .appointment(appointment)
                .symptoms(
                        request.getSymptoms() != null
                                ? request.getSymptoms()
                                : appointment.getNotes() // 👈 fallback từ booking notes
                )
                .diagnosis(request.getDiagnosis())
                .build();

        medicalRecordRepo.save(medicalRecord);

        // 5. Tạo đơn thuốc
        Prescription prescription = Prescription.builder()
                .medicalRecord(medicalRecord)
                .status(PrescriptionStatus.PENDING)
                .build();

        prescriptionRepo.save(prescription);

        // 6. Duyệt danh sách thuốc
        for (PrescriptionDetailDTO dto : request.getMedicines()) {

            Medicine medicine = medicineRepo.findById(dto.getMedicineId())
                    .orElseThrow(() ->
                            new RuntimeException("Không tìm thấy thuốc"));

            // Kiểm tra tồn kho
            if (medicine.getStockQuantity() < dto.getQuantity()) {

                throw new RuntimeException(
                        "Thuốc " + medicine.getName()
                                + " không đủ số lượng");
            }

            // Trừ kho
//            medicine.setStockQuantity(
//                    medicine.getStockQuantity()
//                            - dto.getQuantity()
//            );

            medicineRepo.save(medicine);

            // Tạo chi tiết đơn thuốc
            PrescriptionDetail detail =
                    PrescriptionDetail.builder()
                            .prescription(prescription)
                            .medicine(medicine)
                            .quantity(dto.getQuantity())
                            .usageInstruction(dto.getUsageInstruction())
                            .build();

            prescriptionDetailRepo.save(detail);
        }

        // 7. Hoàn tất lịch khám
        appointment.setStatus(AppointmentStatus.COMPLETED);

        appointmentRepo.save(appointment);
    }

    public List<Appointment> findTodayAppointmentsByDoctor(Long doctorUserId) {
        return appointmentRepo.findTodayAppointments(doctorUserId);
    }
}
