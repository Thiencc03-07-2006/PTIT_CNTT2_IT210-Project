package com.healthcare.service.impl;

import com.healthcare.model.entity.UserProfile;
import com.healthcare.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserProfileRepository profileRepository;

    public UserProfile getProfileByUserId(Long userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ người dùng"));
    }

    @Transactional
    public void updateProfile(UserProfile updatedProfile) {
        // Tìm hồ sơ cũ trong DB
        UserProfile existingProfile = profileRepository.findById(updatedProfile.getId())
                .orElseThrow(() -> new RuntimeException("Hồ sơ không tồn tại"));

        // Cập nhật các trường (CORE-03)
        existingProfile.setFullName(updatedProfile.getFullName());
        existingProfile.setPhone(updatedProfile.getPhone());
        existingProfile.setAddress(updatedProfile.getAddress());
        existingProfile.setDob(updatedProfile.getDob());
        existingProfile.setGender(updatedProfile.getGender());

        profileRepository.save(existingProfile);
    }
}