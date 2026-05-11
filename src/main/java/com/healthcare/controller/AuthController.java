package com.healthcare.controller;

import com.healthcare.model.dto.request.LoginDTO;
import com.healthcare.model.dto.request.UserRegistrationDTO;
import com.healthcare.model.entity.Role;
import com.healthcare.model.entity.User;
import com.healthcare.service.impl.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginDto", new LoginDTO());
        return "login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginDto") LoginDTO dto,
                        BindingResult bindingResult,
                        HttpSession session,
                        Model model) {
        if (bindingResult.hasErrors()) {
            return "login";
        }
        try {
            User user = authService.login(dto.getUsername(), dto.getPassword());
            session.setAttribute("user", user); // Lưu session để phân quyền

            // Điều hướng dựa trên vai trò
//            if (user.getRole().name().equals("ADMIN")) return "redirect:/admin/dashboard";
//            if (user.getRole().name().equals("DOCTOR")) return "redirect:/doctor/dashboard";
//            return "redirect:/patient/dashboard";
            return "redirect:/dashboard";
        } catch (Exception e) {
            return "redirect:/login?error";
        }
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO();
        userRegistrationDTO.setRole(Role.PATIENT);
        model.addAttribute("userDto", userRegistrationDTO);
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("userDto") UserRegistrationDTO dto, BindingResult bindingResult,Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        try {
            authService.register(dto);
            return "redirect:/login?success";
        } catch (RuntimeException e) {
            // Gửi thông báo lỗi cụ thể từ Service (e.getMessage()) ra view
            model.addAttribute("errorMessage", e.getMessage());
            return "register"; // Trả về trang register
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Đã xảy ra lỗi hệ thống!");
            return "register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // xoá toàn bộ session
        }
        return "redirect:/login";
    }
}
