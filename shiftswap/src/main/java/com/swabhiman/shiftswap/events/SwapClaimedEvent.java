package com.swabhiman.shiftswap.events;

import com.swabhiman.shiftswap.domain.model.Swap;

public class SwapClaimedEvent extends DomainEvent {
    private final Swap swap;
    public SwapClaimedEvent(Swap swap) { this.swap = swap; }
    public Swap getSwap() { return swap; }
}


