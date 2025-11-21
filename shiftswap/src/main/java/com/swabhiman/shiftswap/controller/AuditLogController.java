package com.swabhiman.shiftswap.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.swabhiman.shiftswap.domain.model.AuditLog;
import com.swabhiman.shiftswap.service.AuditService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/manager")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditService auditService;

    @GetMapping("/audit-log")
    public String viewAuditLog(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) Long actorId,
            Model model) {

        // Create sort
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // Create pageable
        Pageable pageable = PageRequest.of(page, size, sort);

        // Get audit logs
        Page<AuditLog> auditLogs;
        if (action != null || actorId != null) {
            auditLogs = auditService.getFilteredAuditLogs(action, actorId, pageable);
        } else {
            auditLogs = auditService.getAuditLogs(pageable);
        }

        // Add to model
        model.addAttribute("auditLogs", auditLogs);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", auditLogs.getTotalPages());
        model.addAttribute("totalItems", auditLogs.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("action", action);
        model.addAttribute("actorId", actorId);

        return "manager/audit-log";
    }
}
