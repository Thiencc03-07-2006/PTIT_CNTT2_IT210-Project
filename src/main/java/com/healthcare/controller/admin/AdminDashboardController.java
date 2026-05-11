package com.healthcare.controller.admin;

import com.healthcare.model.entity.User;
import com.healthcare.repository.AppointmentRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AppointmentRepository appointmentRepository;

    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        List<Map<String, Object>> revenueData = appointmentRepository.getMonthlyRevenueReport();
        List<Map<String, Object>> topDoctors = appointmentRepository.getTop5DoctorsByVisits();

        // Đảm bảo không gửi null xuống view
        model.addAttribute("revenueData", revenueData != null ? revenueData : new ArrayList<>());
        model.addAttribute("topDoctors", topDoctors != null ? topDoctors : new ArrayList<>());
        model.addAttribute("user", user);
        return "admin/dashboard";
    }
}