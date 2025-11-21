package com.swabhiman.shiftswap.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.swabhiman.shiftswap.domain.enums.UserRole;
import com.swabhiman.shiftswap.domain.model.Staff;
import com.swabhiman.shiftswap.domain.model.User;
import com.swabhiman.shiftswap.domain.repository.StaffRepository;
import com.swabhiman.shiftswap.dto.StaffDetailDto;
import com.swabhiman.shiftswap.service.StaffDetailService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffDetailController {

    private final StaffDetailService staffDetailService;
    private final StaffRepository staffRepository;

    @GetMapping("/{id}")
    public String viewStaffDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser,
            Model model) {

        // Access control: Staff can only view their own page, managers can view all
        if (!canAccessStaffDetail(id, currentUser)) {
            model.addAttribute("error", "You do not have permission to view this staff member's details.");
            return "error/403"; // You can create this template or redirect to a generic error page
        }

        try {
            StaffDetailDto staffDetail = staffDetailService.getStaffDetail(id);
            model.addAttribute("staffDetail", staffDetail);
            model.addAttribute("canEdit", currentUser.getUserRole() == UserRole.MANAGER);
            return "staff-detail";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", "Staff member not found.");
            return "error/404";
        }
    }

    private boolean canAccessStaffDetail(Long staffId, User currentUser) {
        // Managers can view all staff
        if (currentUser.getUserRole() == UserRole.MANAGER || currentUser.getUserRole() == UserRole.ADMIN) {
            return true;
        }

        // Staff can only view their own page
        Staff currentStaff = staffRepository.findByUserId(currentUser.getId()).orElse(null);
        if (currentStaff == null) {
            return false;
        }

        return currentStaff.getId().equals(staffId);
    }
}
