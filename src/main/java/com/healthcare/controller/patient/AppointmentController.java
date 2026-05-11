package com.healthcare.controller.patient;

import com.healthcare.model.dto.request.BookingRequestDTO;
import com.healthcare.model.entity.Appointment;
import com.healthcare.model.entity.User;
import com.healthcare.service.AppointmentService;
import com.healthcare.service.DoctorService;
import com.healthcare.service.SpecialtyService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final SpecialtyService specialtyService;
    private final DoctorService doctorService;

    // 1. Hiển thị trang đặt lịch
    @GetMapping("/book")
    public String showBookingForm(
            @RequestParam(required = false) Long specialtyId,
            Model model,
            HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        model.addAttribute("specialties", specialtyService.getAllSpecialties());

        // KHỞI TẠO DTO
        BookingRequestDTO bookingRequest = new BookingRequestDTO();

        // Gán specialtyId từ RequestParam vào DTO để Form nhận diện được
        if (specialtyId != null) {
            bookingRequest.setSpecialtyId(specialtyId);
            model.addAttribute("doctors", doctorService.findBySpecialty(specialtyId));
            model.addAttribute("selectedSpecialty", specialtyId);
        }

        model.addAttribute("bookingRequest", bookingRequest);

        return "patient/appointments-book";
    }

    // 2. Xử lý gửi Form đặt lịch (Dùng POST)
    @PostMapping("/book")
    public String bookAppointment(@Valid @ModelAttribute("bookingRequest") BookingRequestDTO bookingRequest, BindingResult bindingResult,
                                  HttpSession session,
                                  RedirectAttributes ra, Model model) {
        // KIỂM TRA SESSION THỦ CÔNG
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            // 1. Nạp lại danh sách chuyên khoa
            model.addAttribute("specialties", specialtyService.getAllSpecialties());

            // 2. Nạp lại danh sách bác sĩ dựa trên chuyên khoa đang chọn trong form
            if (bookingRequest.getSpecialtyId() != null) {
                model.addAttribute("doctors", doctorService.findBySpecialty(bookingRequest.getSpecialtyId()));
            }
            return "patient/appointments-book";
        }

        try {
            // Lấy ID từ User trong session để lưu người đặt
            appointmentService.bookAppointment(user.getId(), bookingRequest);

            ra.addFlashAttribute("message", "Đặt lịch khám thành công!");
            return "redirect:/dashboard"; // Hoặc trang danh sách lịch hẹn

        } catch (RuntimeException e) {
            // Nếu có lỗi nghiệp vụ (trùng lịch, bác sĩ bận...)
            ra.addFlashAttribute("error", e.getMessage());
            // Quay lại trang book kèm theo tham số cũ để user chọn lại
            return "redirect:/appointments/book?specialtyId=" + bookingRequest.getSpecialtyId();
        }
    }

    @GetMapping("/history")
    public String viewHistory(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }
        // Lấy danh sách cuộc hẹn của bệnh nhân hiện tại
        List<Appointment> appointments = appointmentService.findByPatientId(currentUser.getId());
        model.addAttribute("appointments", appointments);
        return "patient/appointments-history";
    }

    @GetMapping("/cancel/{id}")
    public String cancelAppointment(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        try {
            appointmentService.cancelAppointment(id, user.getId());
            ra.addFlashAttribute("message",
                    "Hủy lịch khám thành công!");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error",
                    e.getMessage());
        }
        return "redirect:/appointments/history";
    }
}