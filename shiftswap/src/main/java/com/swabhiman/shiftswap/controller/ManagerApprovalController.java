package com.swabhiman.shiftswap.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.swabhiman.shiftswap.domain.model.User;
import com.swabhiman.shiftswap.dto.PendingStaffDto;
import com.swabhiman.shiftswap.service.ApprovalService;
import com.swabhiman.shiftswap.service.RegistrationService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/manager")
@RequiredArgsConstructor
public class ManagerApprovalController {

    private final RegistrationService registrationService;
    private final ApprovalService approvalService;

    @GetMapping("/pending-staff")
    public String viewPendingStaff(Model model) {
        List<PendingStaffDto> pendingStaff = registrationService.getPendingRegistrations();
        model.addAttribute("pendingStaff", pendingStaff);
        return "manager/pending-staff";
    }

    @PostMapping("/approve-staff/{id}")
    public String approveStaff(
            @PathVariable Long id,
            @AuthenticationPrincipal User manager,
            RedirectAttributes redirectAttributes) {
        try {
            approvalService.approveRegistration(id, manager);
            redirectAttributes.addFlashAttribute("success",
                    "Staff registration approved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/manager/pending-staff";
    }

    @PostMapping("/reject-staff/{id}")
    public String rejectStaff(
            @PathVariable Long id,
            @RequestParam(required = false) String reason,
            @AuthenticationPrincipal User manager,
            RedirectAttributes redirectAttributes) {
        try {
            String rejectionReason = (reason != null && !reason.isBlank())
                    ? reason
                    : "No reason provided";
            approvalService.rejectRegistration(id, manager, rejectionReason);
            redirectAttributes.addFlashAttribute("success",
                    "Staff registration rejected.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/manager/pending-staff";
    }
}
