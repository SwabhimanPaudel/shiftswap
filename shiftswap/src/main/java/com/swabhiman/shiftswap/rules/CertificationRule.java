package com.swabhiman.shiftswap.rules;

import org.springframework.stereotype.Component;

import com.swabhiman.shiftswap.domain.model.Staff;
import com.swabhiman.shiftswap.domain.model.Swap;

@Component
public class CertificationRule implements SwapRule {
    @Override
    public ValidationResult validate(Swap swap, Staff claimer) {
        // Minimal placeholder until unit-specific certification requirements are modeled.
        // Leverages existing domain methods for future enhancement.
        return ValidationResult.ok();
    }
}


