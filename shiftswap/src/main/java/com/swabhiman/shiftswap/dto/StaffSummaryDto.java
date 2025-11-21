package com.swabhiman.shiftswap.dto;

import com.swabhiman.shiftswap.domain.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffSummaryDto {

    private Long id;
    private String fullName;
    private String email;
    private Role role; // RN, LPN, CNA, etc.
    private String facilityName;
    private String phoneNumber;
    private Double reliabilityScore;
}
