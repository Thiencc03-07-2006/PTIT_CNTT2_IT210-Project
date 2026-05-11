package com.healthcare.controller.admin;

import com.healthcare.model.dto.response.MedicineDTO;
import com.healthcare.service.MedicineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/medicines")
@RequiredArgsConstructor
public class AdminMedicineController {

    private final MedicineService medicineService;

    // ===== LIST + FORM =====
    @GetMapping
    public String listPage(Model model,
                           @RequestParam(value = "id", required = false) Long id) {

        model.addAttribute("medicines", medicineService.getAllMedicines());

        // Nếu có id -> edit, không có -> create mới
        if (id != null) {
            model.addAttribute("medicine",
                    medicineService.getMedicineById(id));
        } else {
            model.addAttribute("medicine", new MedicineDTO());
        }

        return "admin/admin-medicines";
    }

    // ===== RESET FORM =====
    @GetMapping("/new")
    public String createNew() {
        return "redirect:/admin/medicines";
    }

    // ===== SAVE (CREATE + UPDATE) =====
    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("medicine") MedicineDTO dto, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("medicines", medicineService.getAllMedicines());
            return "admin/admin-medicines";
        }
        try {
            if (dto.getId() == null) {
                medicineService.createMedicine(dto);
                redirectAttributes.addFlashAttribute("message", "Thêm thuốc thành công!");
            } else {
                medicineService.updateMedicine(dto.getId(), dto);
                redirectAttributes.addFlashAttribute("message", "Cập nhật thuốc thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/medicines";
    }

    // ===== DELETE =====
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id,
                         RedirectAttributes ra) {
        try {
            medicineService.deleteMedicine(id);
            ra.addFlashAttribute("message", "Xóa thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/medicines";
    }

    // ===== EDIT (đổ dữ liệu lên form) =====
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id) {
        return "redirect:/admin/medicines?id=" + id;
    }
}