package com.healthcare.config;

import com.healthcare.model.entity.Role;
import com.healthcare.model.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        String path = request.getRequestURI();

        // 1. Kiểm tra đăng nhập
        if (user == null) {
            response.sendRedirect("/login?error=timeout");
            return false;
        }

        // 2. Kiểm soát truy cập
        if (path.startsWith("/doctor") && user.getRole() != Role.DOCTOR) {
            response.sendRedirect("/403"); // Trang báo lỗi không có quyền
            return false;
        }

        if (path.startsWith("/admin") && user.getRole() != Role.ADMIN) {
            response.sendRedirect("/403");
            return false;
        }

        return true; // Cho phép đi tiếp vào Controller
    }
}