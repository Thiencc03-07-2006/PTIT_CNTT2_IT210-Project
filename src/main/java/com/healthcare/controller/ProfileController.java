package com.healthcare.controller;

import com.healthcare.model.entity.User;
import com.healthcare.model.entity.UserProfile;
import com.healthcare.service.impl.ProfileService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public String showProfile(HttpSession session, Model model) {
        // Lấy User từ Session (đã lưu lúc Login)
        User user = (User) session.getAttribute("user");

        // Nếu dùng Interceptor tốt thì không bao giờ user bị null ở đây
        UserProfile profile = profileService.getProfileByUserId(user.getId());
        model.addAttribute("user", user);
        model.addAttribute("profile", profile);
        return "profile";
    }

    @PostMapping("/update")
    public String updateProfile(@Valid @ModelAttribute("profile") UserProfile profile,
                                BindingResult bindingResult,
                                HttpSession session,
                                Model model) {
        // 1. Luôn lấy user từ session để sẵn sàng nạp lại cho View nếu có lỗi
        User user = (User) session.getAttribute("user");

        if (bindingResult.hasErrors()) {
            // 2. Nạp lại user để Sidebar/Navbar hiển thị được role/tên
            model.addAttribute("user", user);

            // 3. Quan trọng: @ModelAttribute("profile") phía trên đã đảm bảo
            // đối tượng profile (đang chứa lỗi) được giữ lại trong Model với tên "profile"
            return "profile";
        }

        try {
            // 4. Gán lại User ID cho profile trước khi update (để tránh mất liên kết DB)
            if (user != null) {
                profile.setUser(user);
            }

            profileService.updateProfile(profile);

            // 5. Đồng bộ lại dữ liệu trong Session để hiển thị đúng ở mọi trang
            user.setProfile(profile);
            session.setAttribute("user", user);

            return "redirect:/profile?success";
        } catch (Exception e) {
            return "redirect:/profile?error";
        }
    }
}