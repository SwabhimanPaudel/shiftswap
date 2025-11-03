package com.swabhiman.shiftswap.rules;

import org.springframework.stereotype.Component;

import com.swabhiman.shiftswap.domain.model.Staff;
import com.swabhiman.shiftswap.domain.model.Swap;

@Component
public class SameRoleRule implements SwapRule {
    @Override
    public ValidationResult validate(Swap swap, Staff claimer) {
        if (!swap.getShift().getRoleRequired().equals(claimer.getRole())) {
            return ValidationResult.fail("Claimer role does not match required role for this shift.");
        }
        return ValidationResult.ok();
    }
}


