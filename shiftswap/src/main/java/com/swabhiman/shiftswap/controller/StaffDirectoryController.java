package com.swabhiman.shiftswap.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.swabhiman.shiftswap.dto.StaffSummaryDto;
import com.swabhiman.shiftswap.service.StaffDirectoryService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffDirectoryController {

    private final StaffDirectoryService staffDirectoryService;

    @GetMapping
    public String viewStaffDirectory(
            @RequestParam(required = false) String search,
            Model model) {

        List<StaffSummaryDto> staffList;

        if (search != null && !search.trim().isEmpty()) {
            staffList = staffDirectoryService.searchStaff(search);
            model.addAttribute("search", search);
        } else {
            staffList = staffDirectoryService.getAllStaff();
        }

        model.addAttribute("staffList", staffList);
        model.addAttribute("totalStaff", staffList.size());

        return "staff-directory";
    }
}
