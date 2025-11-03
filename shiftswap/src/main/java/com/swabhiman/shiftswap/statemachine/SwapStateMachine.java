package com.swabhiman.shiftswap.statemachine;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.swabhiman.shiftswap.domain.enums.SwapStatus;
import com.swabhiman.shiftswap.domain.model.Swap;
import com.swabhiman.shiftswap.exception.IllegalStateTransitionException;

@Component
public class SwapStateMachine {

    private final Map<SwapStatus, Set<SwapStatus>> transitions = new EnumMap<>(SwapStatus.class);

    public SwapStateMachine() {
        transitions.put(SwapStatus.POSTED, EnumSet.of(SwapStatus.CLAIMED, SwapStatus.CANCELLED, SwapStatus.EXPIRED));
        transitions.put(SwapStatus.CLAIMED, EnumSet.of(SwapStatus.OWNER_APPROVED, SwapStatus.CANCELLED));
        transitions.put(SwapStatus.OWNER_APPROVED, EnumSet.of(SwapStatus.MANAGER_APPROVED, SwapStatus.CANCELLED));
        transitions.put(SwapStatus.MANAGER_APPROVED, EnumSet.of(SwapStatus.ACTIVE, SwapStatus.CANCELLED));
        transitions.put(SwapStatus.ACTIVE, EnumSet.of(SwapStatus.COMPLETED, SwapStatus.CANCELLED));
        transitions.put(SwapStatus.COMPLETED, EnumSet.noneOf(SwapStatus.class));
        transitions.put(SwapStatus.CANCELLED, EnumSet.noneOf(SwapStatus.class));
        transitions.put(SwapStatus.EXPIRED, EnumSet.noneOf(SwapStatus.class));
    }

    public void transitionTo(Swap swap, SwapStatus newStatus) {
        SwapStatus current = swap.getStatus();
        Set<SwapStatus> allowed = transitions.getOrDefault(current, EnumSet.noneOf(SwapStatus.class));
        if (!allowed.contains(newStatus)) {
            throw new IllegalStateTransitionException("Invalid transition: " + current + " -> " + newStatus);
        }
        swap.setStatus(newStatus);
    }
}


