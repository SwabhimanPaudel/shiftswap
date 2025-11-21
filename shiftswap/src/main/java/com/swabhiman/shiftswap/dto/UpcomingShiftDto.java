package com.swabhiman.shiftswap.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.swabhiman.shiftswap.domain.enums.ShiftType;
import com.swabhiman.shiftswap.domain.enums.Unit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpcomingShiftDto {

    private Long shiftId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private ShiftType shiftType;
    private Unit unit;
    private String facilityName;
    private String status; // "Assigned", "Pending Swap", "Swap Approved", etc.
    private Long swapId; // If there's an active swap
    private boolean hasActiveSwap;
}
