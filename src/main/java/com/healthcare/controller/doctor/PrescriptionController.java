package com.healthcare.controller.doctor;

import com.healthcare.model.entity.Prescription;
import com.healthcare.model.entity.PrescriptionStatus;
import com.healthcare.repository.PrescriptionRepository;
import com.healthcare.service.impl.PrescriptionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/doctor/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionServiceImpl prescriptionService;
    private final PrescriptionRepository prescriptionRepo;

    @GetMapping("")
    public String listPendingPrescriptions(Model model) {
        // Bạn có thể viết thêm method findByStatus trong Repository
        List<Prescription> prescriptions = prescriptionRepo.findByStatus(PrescriptionStatus.PENDING);
        model.addAttribute("prescriptions", prescriptions);
        return "doctor/prescription-list";
    }

    @GetMapping("/{id}")
    public String viewPrescriptionDetail(@PathVariable Long id, Model model) {
        Prescription prescription = prescriptionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn thuốc"));

        model.addAttribute("prescription", prescription);
        return "doctor/prescription-detail";
    }

    @PostMapping("/dispense/{id}")
    public String dispenseMedicine(@PathVariable Long id, RedirectAttributes ra) {
        try {
            // Gọi hàm xử lý phát thuốc và trừ kho
            prescriptionService.confirmDispenseMedicine(id);

            ra.addFlashAttribute("message", "Xác nhận phát thuốc và trừ kho thành công!");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        // Trở về trang danh sách đơn thuốc cần phát
        return "redirect:/doctor/prescriptions";
    }
}