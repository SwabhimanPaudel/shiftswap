package com.swabhiman.shiftswap.domain.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.swabhiman.shiftswap.domain.model.AuditLog;
import com.swabhiman.shiftswap.domain.model.Swap;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findBySwapOrderByTimestampDesc(Swap swap);

    List<AuditLog> findBySwapIdOrderByTimestampDesc(Long swapId);

    @Query("SELECT al FROM AuditLog al WHERE al.reasonCode = :reasonCode " +
           "AND al.timestamp BETWEEN :start AND :end")
    List<AuditLog> findByReasonCodeAndDateRange(String reasonCode, Instant start, Instant end);

    @Query("SELECT al FROM AuditLog al WHERE al.actor.id = :actorId " +
           "ORDER BY al.timestamp DESC")
    List<AuditLog> findByActorId(Long actorId);
}