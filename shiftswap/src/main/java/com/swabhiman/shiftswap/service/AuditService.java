package com.swabhiman.shiftswap.service;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
                .reasonDetail(details)
                .timestamp(Instant.now())
                .actor(actor)
                .swap(swap)
                .build();
        auditLogRepository.save(log);
    }

    /**
     * Log a general action (not swap-related)
     * For staff registration, approvals, etc.
     */
    public void logAction(String action, User actor, String details) {
        AuditLog log = AuditLog.builder()
                .action(action)
                .reasonDetail(details)
                .timestamp(Instant.now())
                .actor(actor)
                .swap(null) // No swap for general actions
                .build();
        auditLogRepository.save(log);
    }

    /**
     * Get paginated audit logs
     */
    public Page<AuditLog> getAuditLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByTimestampDesc(pageable);
    }

    /**
     * Get filtered audit logs
     */
    public Page<AuditLog> getFilteredAuditLogs(String action, Long actorId, Pageable pageable) {
        return auditLogRepository.findByFilters(action, actorId, pageable);
    }
}