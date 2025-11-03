package com.swabhiman.shiftswap.events.listeners;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.swabhiman.shiftswap.events.SwapApprovedEvent;
import com.swabhiman.shiftswap.events.SwapClaimedEvent;
import com.swabhiman.shiftswap.events.SwapPostedEvent;
import com.swabhiman.shiftswap.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationListener {
    private final NotificationService notificationService;

    @EventListener
    public void onSwapPosted(SwapPostedEvent event) {
        notificationService.notify("Swap posted for shift " + event.getSwap().getShift().getId());
    }

    @EventListener
    public void onSwapClaimed(SwapClaimedEvent event) {
        notificationService.notify("Swap claimed by " + event.getSwap().getClaimer().getUser().getFullName());
    }

    @EventListener
    public void onSwapApproved(SwapApprovedEvent event) {
        notificationService.notify("Swap approval stage reached: " + event.getStage());
    }
}


