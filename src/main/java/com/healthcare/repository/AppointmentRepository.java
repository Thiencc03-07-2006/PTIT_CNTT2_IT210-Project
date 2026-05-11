package com.healthcare.repository;

import com.healthcare.model.entity.Appointment;
import com.healthcare.model.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsByDoctorIdAndAppointmentDateAndTimeSlotAndStatusNot(
            Long doctorId,
            LocalDate date,
            LocalTime time,
            AppointmentStatus status // Dùng String hoặc Enum (VD: "CANCELED")
    );

    List<Appointment> findByPatientId(Long patientId);

    // Tìm các ca khám của bác sĩ cụ thể trong ngày hôm nay
    @Query("SELECT a FROM Appointment a WHERE a.doctor.user.id = :doctorId " +
            "AND a.appointmentDate = CURRENT_DATE")
    List<Appointment> findTodayAppointments(@Param("doctorId") Long doctorId);
}