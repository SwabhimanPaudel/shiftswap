package com.swabhiman.shiftswap.rules;

import org.springframework.stereotype.Component;

import com.swabhiman.shiftswap.domain.model.Staff;
import com.swabhiman.shiftswap.domain.model.Swap;
import com.swabhiman.shiftswap.domain.repository.ShiftRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OneShiftPerDayRule implements SwapRule {

    private final ShiftRepository shiftRepository;

    @Override
    public ValidationResult validate(Swap swap, Staff claimer) {
        boolean hasShiftOnDate = shiftRepository.existsByAssignedStaffAndDate(
                claimer,
                swap.getShift().getDate());

        if (hasShiftOnDate) {
            return ValidationResult.fail("You already have a shift on this day. Cannot claim another.");
        }
        return ValidationResult.ok();
    }
}
