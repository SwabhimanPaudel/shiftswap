package com.swabhiman.shiftswap.rules;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import com.swabhiman.shiftswap.domain.model.Shift;
import com.swabhiman.shiftswap.domain.model.Staff;
import com.swabhiman.shiftswap.domain.model.Swap;
import com.swabhiman.shiftswap.domain.repository.ShiftRepository;

@Component
public class MaxWeeklyHoursRule implements SwapRule {
    private final ShiftRepository shiftRepository;

    public MaxWeeklyHoursRule(ShiftRepository shiftRepository) { this.shiftRepository = shiftRepository; }

    @Override
    public ValidationResult validate(Swap swap, Staff claimer) {
        LocalDate weekStart = swap.getShift().getDate().minusDays(swap.getShift().getDate().getDayOfWeek().getValue() - 1);
        LocalDate weekEnd = weekStart.plusDays(6);
        List<Shift> weekShifts = shiftRepository.findByStaffAndWeek(claimer, weekStart, weekEnd);
        long currentHours = weekShifts.stream().mapToLong(Shift::getHours).sum();
        long newTotal = currentHours + swap.getShift().getHours();
        if (newTotal > claimer.getMaxWeeklyHours()) {
            return ValidationResult.fail("Accepting this shift exceeds max weekly hours.");
        }
        return ValidationResult.ok();
    }
}


