package com.swabhiman.shiftswap.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swabhiman.shiftswap.domain.enums.RegistrationStatus;
import com.swabhiman.shiftswap.domain.enums.Unit;
import com.swabhiman.shiftswap.domain.enums.UserRole;
import com.swabhiman.shiftswap.domain.model.Staff;
import com.swabhiman.shiftswap.domain.model.StaffRegistration;
import com.swabhiman.shiftswap.domain.model.User;
import com.swabhiman.shiftswap.domain.repository.StaffRegistrationRepository;
import com.swabhiman.shiftswap.domain.repository.StaffRepository;
import com.swabhiman.shiftswap.domain.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApprovalService {

    private final StaffRegistrationRepository registrationRepository;
    private final UserRepository userRepository;
    private final StaffRepository staffRepository;
    private final AuditService auditService;

    @Transactional
    public void approveRegistration(Long registrationId, User manager) {
        StaffRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new EntityNotFoundException("Registration not found"));

        if (registration.getStatus() != RegistrationStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Registration is not pending approval");
        }

        // Create User entity
        User newUser = User.builder()
                .email(registration.getEmail())
                .password(registration.getPassword()) // Already hashed
                .firstName(registration.getFirstName())
                .lastName(registration.getLastName())
                .userRole(UserRole.STAFF) // All new registrations are STAFF
                .facility(registration.getFacility())
                .enabled(true)
                .build();

        newUser = userRepository.save(newUser);

        // Create Staff entity
        Staff newStaff = Staff.builder()
                .user(newUser)
                .facility(registration.getFacility())
                .role(registration.getRole())
                .primaryUnit(Unit.MED_SURG) // Default unit, can be changed later
                .maxWeeklyHours(registration.getFacility().getDefaultMaxWeeklyHours())
                .reliabilityScore(100.0)
                .phoneNumber(registration.getPhoneNumber())
                .build();

        staffRepository.save(newStaff);

        // Update registration status
        registration.setStatus(RegistrationStatus.APPROVED);
        registrationRepository.save(registration);

        // Log the approval
        auditService.logAction(
                "STAFF_REGISTRATION_APPROVED",
                manager,
                "Staff approved by manager: " + registration.getFullName() +
                        " (Employee ID: " + registration.getEmployeeId() + ")");
    }

    @Transactional
    public void rejectRegistration(Long registrationId, User manager, String reason) {
        StaffRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new EntityNotFoundException("Registration not found"));

        if (registration.getStatus() != RegistrationStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Registration is not pending approval");
        }

        // Update registration status
        registration.setStatus(RegistrationStatus.REJECTED);
        registration.setRejectionReason(reason);
        registrationRepository.save(registration);

        // Log the rejection
        auditService.logAction(
                "STAFF_REGISTRATION_REJECTED",
                manager,
                "Staff registration rejected by manager: " + registration.getFullName() +
                        " (Employee ID: " + registration.getEmployeeId() + "). Reason: " + reason);
    }
}
