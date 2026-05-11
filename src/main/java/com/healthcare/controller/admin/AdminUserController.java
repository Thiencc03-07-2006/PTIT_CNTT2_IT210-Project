package com.healthcare.controller.admin;

import com.healthcare.model.dto.request.UpgradeDoctorRequest;
import com.healthcare.repository.SpecialtyRepository;
import com.healthcare.repository.UserRepository;
import com.healthcare.service.impl.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final AuthService authService;
    private final SpecialtyRepository specialtyRepository;
    private final UserRepository userRepository;

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/user-list";
    }

    // Trang hiển thị form bổ sung thông tin bác sĩ
    @GetMapping("/upgrade/{id}")
    public String showUpgradeForm(@PathVariable Long id, Model model) {
        model.addAttribute("userId", id);
        model.addAttribute("specialties", specialtyRepository.findAll());
        model.addAttribute("upgradeRequest", new UpgradeDoctorRequest());
        return "admin/upgrade-doctor";
    }

    @PostMapping("/upgrade")
    public String processUpgrade(@ModelAttribute UpgradeDoctorRequest request, RedirectAttributes ra) {
        try {
            authService.upgradeToDoctor(request);
            ra.addFlashAttribute("message", "Nâng cấp bác sĩ thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
}