package com.swabhiman.shiftswap.events;

import java.time.Instant;

public abstract class DomainEvent {
    private final Instant occurredAt = Instant.now();
    public Instant getOccurredAt() { return occurredAt; }
}


