package com.swabhiman.shiftswap.dto;

import java.time.LocalDate;
import java.util.List;

import com.swabhiman.shiftswap.domain.enums.Role;
import com.swabhiman.shiftswap.domain.enums.Unit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffDetailDto {

    // Basic Info
    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;

    // Professional Info
    private Role role;
    private Unit primaryUnit;
    private String facilityName;
    private String phoneNumber;
    private Integer maxWeeklyHours;
    private Double reliabilityScore;

    // Additional Info (from User if available)
    private LocalDate dateOfBirth;
    private String employeeId;
    private String certifications;
    private Integer yearsOfExperience;
    private String jobTitle;

    // Upcoming Shifts
    private List<UpcomingShiftDto> upcomingShifts;
}
