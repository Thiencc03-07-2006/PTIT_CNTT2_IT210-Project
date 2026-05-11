package com.healthcare.controller;

import com.healthcare.model.entity.Role;
import com.healthcare.model.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        // 1. Lấy user từ session
        User user = (User) session.getAttribute("user");

        // 2. Nếu session mất (do timeout hoặc chưa login) thì đá về login
        if (user == null) {
            return "redirect:/login";
        }

        // 3. Đưa user vào model để hiển thị tên/role trên giao diện
//        model.addAttribute("user", user);

        if (user.getRole() == Role.PATIENT) {
            return "redirect:/appointments/book";
        }

        if (user.getRole() == Role.DOCTOR) {
            return "redirect:/doctor/appointments";
        }

        if (user.getRole() == Role.ADMIN) {
            return "redirect:/admin/dashboard";
        }

        // 4. Trả về file templates/dashboard.html
        return "redirect:/login";
    }
}