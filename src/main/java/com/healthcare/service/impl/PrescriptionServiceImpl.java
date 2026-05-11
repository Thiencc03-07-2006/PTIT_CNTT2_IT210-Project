package com.healthcare.service.impl;

import com.healthcare.model.entity.Medicine;
import com.healthcare.model.entity.Prescription;
import com.healthcare.model.entity.PrescriptionDetail;
import com.healthcare.model.entity.PrescriptionStatus;
import com.healthcare.repository.MedicineRepository;
import com.healthcare.repository.PrescriptionRepository;
import com.healthcare.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepo;
    private final MedicineRepository medicineRepo;

    @Override
    @Transactional
    public void confirmDispenseMedicine(Long prescriptionId) {
        // 1. Tìm đơn thuốc
        Prescription prescription = prescriptionRepo.findById(prescriptionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn thuốc"));

        // 2. Kiểm tra trạng thái đơn thuốc (Chỉ phát thuốc khi đơn đang ở trạng thái PENDING)
        if (prescription.getStatus() != PrescriptionStatus.PENDING) {
            throw new RuntimeException("Đơn thuốc này đã được xử lý hoặc đã hủy!");
        }

        // 3. Duyệt qua từng loại thuốc trong đơn để kiểm tra và trừ kho
        for (PrescriptionDetail detail : prescription.getPrescriptionDetails()) {
            Medicine medicine = detail.getMedicine();

            // Kiểm tra lại lần cuối xem kho có đủ thuốc không (vì có thể thuốc đã xuất cho đơn khác)
            if (medicine.getStockQuantity() < detail.getQuantity()) {
                throw new RuntimeException("Thuốc " + medicine.getName() + " trong kho không đủ để phát!");
            }

            // Thực hiện trừ kho
            medicine.setStockQuantity(medicine.getStockQuantity() - detail.getQuantity());
            medicineRepo.save(medicine);
        }

        // 4. Cập nhật trạng thái đơn thuốc thành DISPENSED
        prescription.setStatus(PrescriptionStatus.DISPENSED);
        prescriptionRepo.save(prescription);
    }
}