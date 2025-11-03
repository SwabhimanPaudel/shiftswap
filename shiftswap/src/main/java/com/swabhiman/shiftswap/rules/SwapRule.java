package com.swabhiman.shiftswap.rules;

import com.swabhiman.shiftswap.domain.model.Staff;
import com.swabhiman.shiftswap.domain.model.Swap;

public interface SwapRule {
    ValidationResult validate(Swap swap, Staff claimer);
}


