package com.swabhiman.shiftswap.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.swabhiman.shiftswap.domain.enums.Role;
import com.swabhiman.shiftswap.domain.model.Facility;
import com.swabhiman.shiftswap.domain.repository.FacilityRepository;
import com.swabhiman.shiftswap.dto.StaffRegistrationRequest;
import com.swabhiman.shiftswap.service.RegistrationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;
    private final FacilityRepository facilityRepository;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationRequest", new StaffRegistrationRequest());
        model.addAttribute("roles", Role.values());

        // Get all facilities for the dropdown
        List<Facility> facilities = facilityRepository.findAll();
        model.addAttribute("facilities", facilities);

        return "register";
    }

    @PostMapping("/register")
    public String submitRegistration(
            @Valid @ModelAttribute("registrationRequest") StaffRegistrationRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            // Re-populate form data
            model.addAttribute("roles", Role.values());
            model.addAttribute("facilities", facilityRepository.findAll());
            return "register";
        }

        try {
            registrationService.submitRegistration(request);
            redirectAttributes.addFlashAttribute("success",
                    "Registration submitted successfully! Your application is pending manager approval.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("roles", Role.values());
            model.addAttribute("facilities", facilityRepository.findAll());
            return "register";
        }
    }
}
