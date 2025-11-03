package com.swabhiman.shiftswap.rules;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.swabhiman.shiftswap.domain.model.Staff;
import com.swabhiman.shiftswap.domain.model.Swap;

@Component
public class CompositeRule implements SwapRule {
    private final List<SwapRule> rules = new ArrayList<>();

    public CompositeRule(List<SwapRule> rules) {
        if (rules != null) {
            this.rules.addAll(rules);
        }
    }

    public void addRule(SwapRule rule) { this.rules.add(rule); }

    @Override
    public ValidationResult validate(Swap swap, Staff claimer) {
        for (SwapRule rule : rules) {
            ValidationResult result = rule.validate(swap, claimer);
            if (!result.isValid()) {
                return result;
            }
        }
        return ValidationResult.ok();
    }
}


