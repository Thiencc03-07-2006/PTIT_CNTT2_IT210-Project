package com.healthcare.controller.admin;

import com.healthcare.model.dto.request.SpecialtyDTO;
import com.healthcare.model.entity.Specialty;
import com.healthcare.service.SpecialtyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/specialties")
@RequiredArgsConstructor
public class AdminSpecialtyController {

    private final SpecialtyService specialtyService;

    @GetMapping
    public String listSpecialties(Model model) {
        model.addAttribute("specialties", specialtyService.getAllSpecialties());
        return "admin/specialties-list";
    }

    // Form thêm
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("specialty", new SpecialtyDTO());
        return "admin/specialties-form";
    }

    // Form sửa
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {

        Specialty specialty = specialtyService.getById(id);

        SpecialtyDTO dto = SpecialtyDTO.builder()
                .id(specialty.getId())
                .name(specialty.getName())
                .description(specialty.getDescription())
                .build();

        model.addAttribute("specialty", dto);

        return "admin/specialties-form";
    }

    // Save
    @PostMapping("/save")
    public String saveSpecialty(
            @Valid @ModelAttribute("specialty") SpecialtyDTO specialtyDTO,
            BindingResult result,
            RedirectAttributes ra,
            Model model
    ) {

        // Nếu validate lỗi
        if (result.hasErrors()) {
            return "admin/specialties-form";
        }

        try {
            Specialty specialty = Specialty.builder()
                    .id(specialtyDTO.getId())
                    .name(specialtyDTO.getName())
                    .description(specialtyDTO.getDescription())
                    .build();

            specialtyService.createSpecialty(specialty);

            ra.addFlashAttribute("message", "Đã lưu thông tin chuyên khoa thành công!");
            return "redirect:/admin/specialties";

        } catch (RuntimeException e) {
            // Bắt lỗi trùng tên từ Service và gửi lại Form
            model.addAttribute("error", e.getMessage());
            return "admin/specialties-form";
        }
    }

    // Delete
    @GetMapping("/delete/{id}")
    public String deleteSpecialty(@PathVariable Long id, RedirectAttributes ra) {

        try {
            specialtyService.deleteSpecialty(id);
            ra.addFlashAttribute("message", "Đã xóa chuyên khoa thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Không thể xóa: " + e.getMessage());
        }

        return "redirect:/admin/specialties";
    }
}