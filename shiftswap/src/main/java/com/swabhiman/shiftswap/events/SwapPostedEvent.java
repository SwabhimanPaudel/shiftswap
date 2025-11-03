package com.swabhiman.shiftswap.events;

import com.swabhiman.shiftswap.domain.model.Swap;

public class SwapPostedEvent extends DomainEvent {
    private final Swap swap;
    public SwapPostedEvent(Swap swap) { this.swap = swap; }
    public Swap getSwap() { return swap; }
}


