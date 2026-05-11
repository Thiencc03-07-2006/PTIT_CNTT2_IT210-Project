package com.healthcare.service;

import com.healthcare.model.dto.request.BookingRequestDTO;
import com.healthcare.model.dto.request.CompleteAppointmentDTO;
import com.healthcare.model.entity.Appointment;

import java.util.List;

public interface AppointmentService {
    void bookAppointment(Long patientId, BookingRequestDTO request);
//    List<Appointment> findByPatientIdOrderByAppointmentDateDescAppointmentTimeDesc(Long patientId);
//
//    // Tìm lịch sử cho bác sĩ
//    List<Appointment> findByDoctorIdOrderByAppointmentDateDesc(Long doctorId);

    List<Appointment> findByPatientId(Long id);

    void cancelAppointment(Long appointmentId, Long patientId);

    Appointment findById(Long id);

    void completeAppointment(
            Long appointmentId,
            Long doctorUserId,
            CompleteAppointmentDTO request
    );

    List<Appointment> findTodayAppointmentsByDoctor(Long doctorUserId);

}
