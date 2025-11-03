package com.swabhiman.shiftswap.events;

import com.swabhiman.shiftswap.domain.enums.SwapStatus;
import com.swabhiman.shiftswap.domain.model.Swap;

public class SwapApprovedEvent extends DomainEvent {
    private final Swap swap;
    private final SwapStatus stage;
    public SwapApprovedEvent(Swap swap, SwapStatus stage) { this.swap = swap; this.stage = stage; }
    public Swap getSwap() { return swap; }
    public SwapStatus getStage() { return stage; }
}


