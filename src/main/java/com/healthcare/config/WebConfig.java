package com.healthcare.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private RoleInterceptor roleInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Áp dụng cho các trang yêu cầu quyền hạn, loại trừ trang login/register/static
        registry.addInterceptor(roleInterceptor)
                .addPathPatterns("/admin/**", "/doctor/**", "/patient/**", "/profile/**")
                .excludePathPatterns("/login", "/register", "/css/**", "/js/**");
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
