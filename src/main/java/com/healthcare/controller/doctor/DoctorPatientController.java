package com.healthcare.controller.doctor;

import com.healthcare.model.entity.MedicalRecord;
import com.healthcare.model.entity.User;
import com.healthcare.repository.MedicalRecordRepository;
import com.healthcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/doctor/patients")
@RequiredArgsConstructor
public class DoctorPatientController {

    private final UserRepository userRepo;
    private final MedicalRecordRepository medicalRecordRepo;

    // 1. Danh sách bệnh nhân
    @GetMapping("")
    public String listPatients(@RequestParam(required = false) String query, Model model) {
        List<User> patients = (query != null && !query.isEmpty())
                ? userRepo.searchPatients(query)
                : userRepo.findAllPatients();

        model.addAttribute("patients", patients);
        model.addAttribute("query", query);
        return "doctor/patient-list";
    }

    // 2. Chi tiết lịch sử khám
    @GetMapping("/detail/{id}")
    public String patientDetail(@PathVariable Long id, Model model) {
        User patient = userRepo.findById(id).orElseThrow();
        // Lấy lịch sử khám thông qua Patient ID (là User ID của người bệnh)
        List<MedicalRecord> history = medicalRecordRepo.findByAppointmentPatientIdOrderByAppointment_AppointmentDateDesc(id);

        model.addAttribute("patient", patient);
        model.addAttribute("history", history);
        return "doctor/patient-detail";
    }
}