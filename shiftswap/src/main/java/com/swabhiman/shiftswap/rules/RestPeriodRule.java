package com.swabhiman.shiftswap.rules;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import com.swabhiman.shiftswap.domain.model.Shift;
import com.swabhiman.shiftswap.domain.model.Staff;
import com.swabhiman.shiftswap.domain.model.Swap;
import com.swabhiman.shiftswap.domain.repository.ShiftRepository;

@Component
public class RestPeriodRule implements SwapRule {
    private final ShiftRepository shiftRepository;
    private static final int MIN_REST_HOURS = 8;

    public RestPeriodRule(ShiftRepository shiftRepository) { this.shiftRepository = shiftRepository; }

    @Override
    public ValidationResult validate(Swap swap, Staff claimer) {
        LocalDate date = swap.getShift().getDate();
        List<Shift> nearby = shiftRepository.findUpcomingShifts(claimer, date.minusDays(1));
        for (Shift s : nearby) {
            long restBefore = java.time.Duration.between(s.getEndDateTime(), swap.getShift().getStartDateTime()).toHours();
            long restAfter = java.time.Duration.between(swap.getShift().getEndDateTime(), s.getStartDateTime()).toHours();
            if ((restBefore > 0 && restBefore < MIN_REST_HOURS) || (restAfter > 0 && restAfter < MIN_REST_HOURS)) {
                return ValidationResult.fail("Minimum rest period not satisfied (" + MIN_REST_HOURS + "h).");
            }
        }
        return ValidationResult.ok();
    }
}


