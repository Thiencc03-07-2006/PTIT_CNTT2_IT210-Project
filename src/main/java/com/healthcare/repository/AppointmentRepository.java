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
import java.util.Map;

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

    /**
     * Thống kê doanh thu theo tháng trong năm hiện tại.
     * Sử dụng JOIN từ Appointment -> MedicalRecord -> Prescription -> PrescriptionDetail -> Medicine
     */
    @Query(value = "SELECT " +
            "  MONTH(a.appointment_date) as month, " +
            "  (COUNT(DISTINCT a.id) * 200000) + SUM(COALESCE(pd.quantity * m.price, 0)) as revenue " +
            "FROM appointments a " +
            "LEFT JOIN medical_records mr ON a.id = mr.appointment_id " +
            "LEFT JOIN prescriptions p ON mr.id = p.medical_record_id " +
            "LEFT JOIN prescription_details pd ON p.id = pd.prescription_id " +
            "LEFT JOIN medicines m ON pd.medicine_id = m.id " +
            "WHERE YEAR(a.appointment_date) = YEAR(CURDATE()) " +
            "  AND a.status = 'COMPLETED' " +
            "GROUP BY MONTH(a.appointment_date) " +
            "ORDER BY month ASC", nativeQuery = true)
    List<Map<String, Object>> getMonthlyRevenueReport();

    /**
     * Top 5 bác sĩ có lượt khám nhiều nhất.
     * Thống kê dựa trên số lượng Appointment có trạng thái COMPLETED.
     */
    @Query(value = "SELECT up.full_name as doctorName, COUNT(a.id) as visitCount " +
            "FROM appointments a " +
            "JOIN doctors d ON a.doctor_id = d.id " +
            "JOIN users u ON d.user_id = u.id " +
            "JOIN user_profiles up ON u.id = up.user_id " +
            "WHERE a.status = 'COMPLETED' " +
            "GROUP BY d.id, up.full_name " +
            "ORDER BY visitCount DESC LIMIT 5", nativeQuery = true)
    List<Map<String, Object>> getTop5DoctorsByVisits();
}