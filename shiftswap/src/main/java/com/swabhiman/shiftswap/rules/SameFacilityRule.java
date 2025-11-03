package com.swabhiman.shiftswap.rules;

import org.springframework.stereotype.Component;

import com.swabhiman.shiftswap.domain.model.Staff;
import com.swabhiman.shiftswap.domain.model.Swap;

@Component
public class SameFacilityRule implements SwapRule {
    @Override
    public ValidationResult validate(Swap swap, Staff claimer) {
        if (!swap.getShift().getFacility().getId().equals(claimer.getFacility().getId())) {
            return ValidationResult.fail("Claimer must be in the same facility.");
        }
        return ValidationResult.ok();
    }
}


