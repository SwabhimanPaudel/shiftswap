package com.swabhiman.shiftswap.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.swabhiman.shiftswap.domain.model.AuditLog;
import com.swabhiman.shiftswap.domain.model.Swap;
import com.swabhiman.shiftswap.domain.model.User;
import com.swabhiman.shiftswap.domain.repository.AuditLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditService {
    private final AuditLogRepository auditLogRepository;

    public void logSwapAction(Swap swap, String action, User actor, String details) {
        AuditLog log = AuditLog.builder()
                .action(action)
                .reasonDetail(details) // <-- This line is corrected from .details(details)
                .timestamp(Instant.now())
                .actor(actor)
                .swap(swap)
                .build();
        auditLogRepository.save(log);
    }
}