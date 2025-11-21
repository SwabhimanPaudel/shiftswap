package com.swabhiman.shiftswap.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.swabhiman.shiftswap.domain.enums.SwapStatus;
import com.swabhiman.shiftswap.domain.model.Shift;
import com.swabhiman.shiftswap.domain.model.Staff;
import com.swabhiman.shiftswap.domain.model.Swap;
import com.swabhiman.shiftswap.domain.repository.ShiftRepository;
import com.swabhiman.shiftswap.domain.repository.StaffRegistrationRepository;
import com.swabhiman.shiftswap.domain.repository.StaffRepository;
import com.swabhiman.shiftswap.domain.repository.SwapRepository;
import com.swabhiman.shiftswap.dto.StaffDetailDto;
import com.swabhiman.shiftswap.dto.UpcomingShiftDto;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StaffDetailService {

    private final StaffRepository staffRepository;
    private final ShiftRepository shiftRepository;
    private final SwapRepository swapRepository;
    private final StaffRegistrationRepository staffRegistrationRepository;

    public StaffDetailDto getStaffDetail(Long staffId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new EntityNotFoundException("Staff not found"));

        // Get upcoming shifts
        List<UpcomingShiftDto> upcomingShifts = getUpcomingShifts(staff);

        // Try to find additional info from staff registration (if they were registered
        // through the system)
        var registration = staffRegistrationRepository.findByEmail(staff.getUser().getEmail());

        return StaffDetailDto.builder()
                .id(staff.getId())
                .firstName(staff.getUser().getFirstName())
                .lastName(staff.getUser().getLastName())
                .fullName(staff.getUser().getFullName())
                .email(staff.getUser().getEmail())
                .role(staff.getRole())
                .primaryUnit(staff.getPrimaryUnit())
                .facilityName(staff.getFacility() != null ? staff.getFacility().getName() : "N/A")
                .phoneNumber(staff.getPhoneNumber())
                .maxWeeklyHours(staff.getMaxWeeklyHours())
                .reliabilityScore(staff.getReliabilityScore())
                // Additional info from registration if available
                .dateOfBirth(registration.map(r -> r.getDateOfBirth()).orElse(null))
                .employeeId(registration.map(r -> r.getEmployeeId()).orElse(null))
                .certifications(registration.map(r -> r.getCertifications()).orElse(null))
                .yearsOfExperience(registration.map(r -> r.getYearsOfExperience()).orElse(null))
                .jobTitle(registration.map(r -> r.getJobTitle()).orElse(null))
                .upcomingShifts(upcomingShifts)
                .build();
    }

    private List<UpcomingShiftDto> getUpcomingShifts(Staff staff) {
        LocalDate today = LocalDate.now();
        List<Shift> shifts = shiftRepository.findUpcomingShifts(staff, today);

        return shifts.stream()
                .map(shift -> convertToUpcomingShiftDto(shift))
                .collect(Collectors.toList());
    }

    private UpcomingShiftDto convertToUpcomingShiftDto(Shift shift) {
        // Check if there's an active swap for this shift
        List<Swap> swaps = swapRepository.findByShiftId(shift.getId());
        Swap activeSwap = swaps.isEmpty() ? null : swaps.get(0);

        String status = determineShiftStatus(activeSwap);
        boolean hasActiveSwap = activeSwap != null;
        Long swapId = activeSwap != null ? activeSwap.getId() : null;

        return UpcomingShiftDto.builder()
                .shiftId(shift.getId())
                .date(shift.getDate())
                .startTime(shift.getStartTime())
                .endTime(shift.getEndTime())
                .shiftType(shift.getShiftType())
                .unit(shift.getUnit())
                .facilityName(shift.getFacility().getName())
                .status(status)
                .swapId(swapId)
                .hasActiveSwap(hasActiveSwap)
                .build();
    }

    private String determineShiftStatus(Swap swap) {
        if (swap == null) {
            return "Assigned";
        }

        SwapStatus status = swap.getStatus();
        switch (status) {
            case POSTED:
                return "Pending Swap (Posted)";
            case CLAIMED:
                return "Pending Swap (Claimed)";
            case OWNER_APPROVED:
                return "Pending Manager Approval";
            case MANAGER_APPROVED:
                return "Swap Approved";
            case ACTIVE:
                return "Swap Active";
            case COMPLETED:
                return "Swap Completed";
            case EXPIRED:
                return "Swap Expired";
            case CANCELLED:
                return "Swap Cancelled";
            default:
                return "Assigned";
        }
    }
}
