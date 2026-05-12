package com.healthcare.service.impl;

import com.healthcare.model.dto.request.UpgradeDoctorRequestDTO;
import com.healthcare.model.dto.request.UserRegistrationDTO;
import com.healthcare.model.entity.*;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.SpecialtyRepository;
import com.healthcare.repository.UserProfileRepository;
import com.healthcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final SpecialtyRepository specialtyRepository;
    private final DoctorRepository doctorRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public void register(UserRegistrationDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại!");
        }
        if (userProfileRepository.existsByPhone(dto.getPhone())) {
            throw new RuntimeException("Số điện thoại đã tồn tại!");
        }

        // Băm mật khẩu bảo mật
        // passwordEncoder.encode() sẽ tạo ra chuỗi băm kèm Salt ngẫu nhiên
        String hashedPw = passwordEncoder.encode(dto.getPassword());

        // Tạo User mới
        User user = User.builder()
                .username(dto.getUsername())
                .password(hashedPw)
                .role(Role.PATIENT)
                .build();

        // Khởi tạo Profile (CORE-03)
        // Việc lưu User sẽ tự động lưu Profile nhờ CascadeType.ALL trong Entity
        UserProfile profile = UserProfile.builder()
                .fullName(dto.getFullName())
                .phone(dto.getPhone())
                .gender(dto.getGender())
                .user(user)
                .build();

        user.setProfile(profile);

        // 5. Lưu vào Database
        userRepository.save(user);
    }

    /**
     * Thực hiện logic Đăng nhập thủ công (Sử dụng cho Interceptor phân quyền)
     */
    public User login(String username, String password) {
        // Chú ý: findByUsername phải trả về Optional<User> trong UserRepository
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));

        // CORE-01: Kiểm tra mật khẩu đã băm
        // Không dùng equals() vì chuỗi băm thay đổi mỗi lần encode
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Mật khẩu không chính xác");
        }

        return user;
    }

    @Transactional
    public void upgradeToDoctor(UpgradeDoctorRequestDTO request) {
        // 1. Tìm User
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // 2. Tìm Chuyên khoa
        Specialty specialty = specialtyRepository.findById(request.getSpecialtyId())
                .orElseThrow(() -> new RuntimeException("Chuyên khoa không hợp lệ"));

        // 3. Cập nhật Role
        user.setRole(Role.DOCTOR);
        userRepository.save(user);

        // 4. Tạo bản ghi Doctor
        Doctor doctor = Doctor.builder()
                .user(user)
                .specialty(specialty)
                .experienceYears(request.getExperienceYears())
                .bio(request.getBio())
                .build();

        doctorRepository.save(doctor);
    }
}