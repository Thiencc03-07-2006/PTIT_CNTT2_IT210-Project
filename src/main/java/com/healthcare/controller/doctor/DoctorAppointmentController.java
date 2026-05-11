package com.healthcare.controller.doctor;

import com.healthcare.model.dto.request.CompleteAppointmentDTO;
import com.healthcare.model.dto.request.PrescriptionDetailDTO;
import com.healthcare.model.entity.Appointment;
import com.healthcare.model.entity.User;
import com.healthcare.service.AppointmentService;
import com.healthcare.service.DoctorService;
import com.healthcare.service.MedicineService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/doctor/appointments")
@RequiredArgsConstructor
public class DoctorAppointmentController {

    private final AppointmentService appointmentService;
    private final DoctorService doctorService;
    private final MedicineService medicineService;

    @GetMapping("")
    public String showTodaySchedule(Model model, HttpSession session) {
        User doctorUser = (User) session.getAttribute("user");
        if (doctorUser == null) return "redirect:/login";

        // Lấy danh sách lịch khám của bác sĩ trong ngày hôm nay
        // Giả sử bạn đã có phương thức này trong Service
        List<Appointment> todayAppointments = appointmentService.findTodayAppointmentsByDoctor(doctorUser.getId());

        model.addAttribute("appointments", todayAppointments);
        return "doctor/schedule";
    }

    @GetMapping("/complete/{id}")
    public String showCompleteForm(@PathVariable Long id, Model model, HttpSession session) {
        User doctorUser = (User) session.getAttribute("user");
        if (doctorUser == null) return "redirect:/login";

        // 1. Lấy thông tin lịch khám
        Appointment appointment = appointmentService.findById(id);

        // 2. Chuẩn bị DTO cho Form (nếu chưa có)
        CompleteAppointmentDTO completeRequest = new CompleteAppointmentDTO();
        List<PrescriptionDetailDTO> medicines = new ArrayList<>();
        medicines.add(new PrescriptionDetailDTO());
        completeRequest.setMedicines(medicines);
//        if (completeRequest.getMedicines() == null) {
//            completeRequest.setMedicines(new ArrayList<>());
//        }

        // 3. Đưa dữ liệu ra view
        model.addAttribute("appointmentId", id);
        model.addAttribute("patientName", appointment.getPatient().getProfile().getFullName());
        model.addAttribute("patientNotes", appointment.getNotes());
        model.addAttribute("allMedicines", medicineService.getAllMedicines());
        model.addAttribute("completeRequest", completeRequest);

        return "doctor/complete-appointment";
    }

    @PostMapping("/complete/{id}")
    public String completeAppointment(
            @PathVariable Long id,
            @Valid @ModelAttribute("completeRequest")
            CompleteAppointmentDTO request,
            BindingResult result,
            HttpSession session,
            RedirectAttributes ra,
            Model model
    ) {
        User doctor = (User) session.getAttribute("user");
        if (doctor == null) {
            return "redirect:/login";
        }
        Appointment appointment = appointmentService.findById(id);
        // VALIDATE FAIL
        if (result.hasErrors()) {
            model.addAttribute("appointmentId", id);
            model.addAttribute(
                    "patientName",
                    appointment.getPatient()
                            .getProfile()
                            .getFullName()
            );
            model.addAttribute(
                    "patientNotes",
                    appointment.getNotes()
            );
            model.addAttribute(
                    "allMedicines",
                    medicineService.getAllMedicines()
            );
            return "doctor/complete-appointment";
        }
        try {
            appointmentService.completeAppointment(
                    id,
                    doctor.getId(),
                    request
            );
            ra.addFlashAttribute(
                    "message",
                    "Khám bệnh thành công"
            );
        } catch (RuntimeException e) {
            ra.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }
        return "redirect:/doctor/appointments";
    }
}
