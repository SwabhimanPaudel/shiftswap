package com.swabhiman.shiftswap.service;

import java.time.Instant;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.swabhiman.shiftswap.domain.model.Swap;
import com.swabhiman.shiftswap.domain.repository.SwapRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledTaskService {
    private final SwapRepository swapRepository;

    // Run every hour
    @Scheduled(cron = "0 0 * * * *")
    public void expireOldSwaps() {
        List<Swap> expired = swapRepository.findExpiredSwaps(Instant.now());
        if (!expired.isEmpty()) {
            log.info("Expiring {} swaps", expired.size());
            expired.forEach(sw -> sw.setExpired(true));
            swapRepository.saveAll(expired);
        }
    }
}


