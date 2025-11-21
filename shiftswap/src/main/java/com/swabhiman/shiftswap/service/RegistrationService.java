package com.swabhiman.shiftswap.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swabhiman.shiftswap.domain.enums.RegistrationStatus;
import com.swabhiman.shiftswap.domain.model.Facility;
import com.swabhiman.shiftswap.domain.model.StaffRegistration;
import com.swabhiman.shiftswap.domain.repository.FacilityRepository;
import com.swabhiman.shiftswap.domain.repository.StaffRegistrationRepository;
import com.swabhiman.shiftswap.domain.repository.UserRepository;
import com.swabhiman.shiftswap.dto.PendingStaffDto;
import com.swabhiman.shiftswap.dto.StaffRegistrationRequest;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final StaffRegistrationRepository registrationRepository;
    private final UserRepository userRepository;
    private final FacilityRepository facilityRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    @Transactional
    public void submitRegistration(StaffRegistrationRequest request) {
        // Validate email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists in the system");
        }

        if (registrationRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("A registration with this email is already pending");
        }

        // Validate employee ID uniqueness
        if (registrationRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new IllegalArgumentException("Employee ID already exists");
        }

        // Get facility
        Facility facility = facilityRepository.findById(request.getFacilityId())
                .orElseThrow(() -> new EntityNotFoundException("Facility not found"));

        // Create registration
        StaffRegistration registration = StaffRegistration.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // Hash password
                .role(request.getRole())
                .employeeId(request.getEmployeeId())
                .dateOfBirth(request.getDateOfBirth())
                .phoneNumber(request.getPhoneNumber())
                .jobTitle(request.getJobTitle())
                .yearsOfExperience(request.getYearsOfExperience())
                .certifications(request.getCertifications())
                .facility(facility)
                .status(RegistrationStatus.PENDING_APPROVAL)
                .build();

        registrationRepository.save(registration);

        // Log the registration
        auditService.logAction(
                "STAFF_REGISTRATION_SUBMITTED",
                null, // No user yet
                "New staff registration pending approval: " + registration.getFullName() +
                        " (Employee ID: " + registration.getEmployeeId() + ")");
    }

    public List<PendingStaffDto> getPendingRegistrations() {
        return registrationRepository.findByStatusOrderByCreatedAtDesc(RegistrationStatus.PENDING_APPROVAL)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public StaffRegistration getRegistrationById(Long id) {
        return registrationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Registration not found"));
    }

    private PendingStaffDto convertToDto(StaffRegistration registration) {
        return PendingStaffDto.builder()
                .id(registration.getId())
                .fullName(registration.getFullName())
                .email(registration.getEmail())
                .role(registration.getRole())
                .employeeId(registration.getEmployeeId())
                .dateOfBirth(registration.getDateOfBirth())
                .phoneNumber(registration.getPhoneNumber())
                .jobTitle(registration.getJobTitle())
                .yearsOfExperience(registration.getYearsOfExperience())
                .certifications(registration.getCertifications())
                .facilityName(registration.getFacility().getName())
                .status(registration.getStatus())
                .createdAt(registration.getCreatedAt())
                .build();
    }
}
