package com.swabhiman.shiftswap.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.swabhiman.shiftswap.domain.model.Staff;
import com.swabhiman.shiftswap.domain.repository.StaffRepository;
import com.swabhiman.shiftswap.dto.StaffSummaryDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StaffDirectoryService {

    private final StaffRepository staffRepository;

    public List<StaffSummaryDto> getAllStaff() {
        return staffRepository.findAll()
                .stream()
                .map(this::convertToSummaryDto)
                .collect(Collectors.toList());
    }

    public List<StaffSummaryDto> searchStaff(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllStaff();
        }

        String lowerQuery = query.toLowerCase();
        return staffRepository.findAll()
                .stream()
                .filter(staff -> matchesQuery(staff, lowerQuery))
                .map(this::convertToSummaryDto)
                .collect(Collectors.toList());
    }

    private boolean matchesQuery(Staff staff, String query) {
        return staff.getUser().getFirstName().toLowerCase().contains(query) ||
                staff.getUser().getLastName().toLowerCase().contains(query) ||
                staff.getUser().getEmail().toLowerCase().contains(query) ||
                staff.getRole().getDisplayName().toLowerCase().contains(query) ||
                (staff.getFacility() != null && staff.getFacility().getName().toLowerCase().contains(query));
    }

    private StaffSummaryDto convertToSummaryDto(Staff staff) {
        return StaffSummaryDto.builder()
                .id(staff.getId())
                .fullName(staff.getUser().getFullName())
                .email(staff.getUser().getEmail())
                .role(staff.getRole())
                .facilityName(staff.getFacility() != null ? staff.getFacility().getName() : "N/A")
                .phoneNumber(staff.getPhoneNumber())
                .reliabilityScore(staff.getReliabilityScore())
                .build();
    }
}
