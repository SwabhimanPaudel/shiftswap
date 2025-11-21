package com.swabhiman.shiftswap.dto;

import java.time.Instant;
import java.time.LocalDate;

import com.swabhiman.shiftswap.domain.enums.RegistrationStatus;
import com.swabhiman.shiftswap.domain.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingStaffDto {

    private Long id;
    private String fullName;
    private String email;
    private Role role;
    private String employeeId;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String jobTitle;
    private Integer yearsOfExperience;
    private String certifications;
    private String facilityName;
    private RegistrationStatus status;
    private Instant createdAt;
}
