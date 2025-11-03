package com.swabhiman.shiftswap.rules;

import org.springframework.stereotype.Component;

import com.swabhiman.shiftswap.domain.model.Staff;
import com.swabhiman.shiftswap.domain.model.Swap;
import com.swabhiman.shiftswap.domain.repository.ShiftRepository;

@Component
public class ScheduleConflictRule implements SwapRule {

    private final ShiftRepository shiftRepository;

    public ScheduleConflictRule(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

    @Override
    public ValidationResult validate(Swap swap, Staff claimer) {
        boolean conflict = shiftRepository.hasConflict(
                claimer,
                swap.getShift().getDate(),
                swap.getShift().getStartTime(),
                swap.getShift().getEndTime()
        );
        if (conflict) {
            return ValidationResult.fail("Claimer has a conflicting shift.");
        }
        return ValidationResult.ok();
    }
}


