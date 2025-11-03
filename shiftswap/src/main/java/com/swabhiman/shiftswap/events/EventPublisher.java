package com.swabhiman.shiftswap.events;

public interface EventPublisher {
    void publish(DomainEvent event);
}


