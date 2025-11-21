package com.swabhiman.shiftswap.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;  // ✅ ADD THIS
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.swabhiman.shiftswap.domain.model.Swap;
import com.swabhiman.shiftswap.domain.model.User;
import com.swabhiman.shiftswap.domain.repository.SwapRepository;
import com.swabhiman.shiftswap.service.SwapService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/manager")
@RequiredArgsConstructor
public class ManagerController {
    private final SwapService swapService;
    private final SwapRepository swapRepository;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal User user, Model model) {
        Long facilityId = user.getFacility().getId();  // ✅ Now works!
        List<Swap> pending = swapRepository.findPendingManagerApproval(facilityId);
        model.addAttribute("pendingCount", pending.size());
        return "manager/dashboard";
    }

    @GetMapping("/pending-approvals")
    public String pendingApprovals(@AuthenticationPrincipal User user, Model model) {
        Long facilityId = user.getFacility().getId();  // ✅ Now works!
        List<Swap> approvals = swapRepository.findPendingManagerApproval(facilityId);
        model.addAttribute("approvals", approvals);
        return "manager/pending-approvals";
    }

    @PostMapping("/approve/{id}")
    public String approve(@PathVariable Long id, @AuthenticationPrincipal User user, RedirectAttributes ra) {
        swapService.approveByManager(id, user);
        ra.addFlashAttribute("success", "Swap approved by manager.");
        return "redirect:/manager/pending-approvals";
    }

    @PostMapping("/reject/{id}")
    public String reject(@PathVariable Long id, @AuthenticationPrincipal User user, RedirectAttributes ra) {
        swapService.rejectByManager(id, user, "Rejected by manager");
        ra.addFlashAttribute("success", "Swap rejected by manager.");
        return "redirect:/manager/pending-approvals";
    }
}