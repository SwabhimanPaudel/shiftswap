package com.swabhiman.shiftswap.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledTaskService {
    
    // Inject SwapService, not SwapRepository
    private final SwapService swapService; 

    // Run every hour
    @Scheduled(cron = "0 0 * * * *")
    public void expireOldSwaps() {
        log.info("Running scheduled job: expireOldSwaps");
        // Call the correct method from SwapService
        int count = swapService.expireOldSwaps(); 
        if (count > 0) {
            log.info("Expired {} old swaps", count);
        }
    }
}