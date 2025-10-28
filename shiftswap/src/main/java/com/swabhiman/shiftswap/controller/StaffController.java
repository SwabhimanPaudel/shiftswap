package com.swabhiman.shiftswap.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // Import the new service
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.swabhiman.shiftswap.domain.model.Shift;
import com.swabhiman.shiftswap.domain.model.Staff;
import com.swabhiman.shiftswap.domain.model.Swap; // For success/error messages
import com.swabhiman.shiftswap.domain.model.User;
import com.swabhiman.shiftswap.service.StaffService;
import com.swabhiman.shiftswap.service.SwapService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;
    private final SwapService swapService; // Inject SwapService

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal User user, Model model) {
        try {
            Staff staff = staffService.getStaffByUser(user);
            List<Swap> availableSwaps = swapService.getAvailableSwaps(user);
            List<Swap> myPostedSwaps = swapService.getMyPostedSwaps(user);
            List<Swap> myClaimedSwaps = swapService.getMyClaimedSwaps(user);

            model.addAttribute("staff", staff);
            // Add counts to the model for the dashboard cards
            model.addAttribute("availableSwapsCount", availableSwaps.size());
            model.addAttribute("myPostedSwapsCount", myPostedSwaps.size());
            model.addAttribute("myClaimedSwapsCount", myClaimedSwaps.size());

            return "staff/dashboard";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", "Could not find your staff profile.");
            return "error"; // You still need to create error.html
        }
    }

    /**
     * Handles requests to show the available swaps page.
     */
    @GetMapping("/available-swaps")
    public String availableSwaps(@AuthenticationPrincipal User user, Model model) {
        List<Swap> swaps = swapService.getAvailableSwaps(user);
        model.addAttribute("swaps", swaps); // Add the list of swaps to the model
        return "staff/available-swaps"; // Tells Thymeleaf to find "staff/available-swaps.html"
    }

    /**
     * Shows the form for posting a new swap request.
     */
    @GetMapping("/post-swap")
    public String showPostSwapForm(@AuthenticationPrincipal User user, Model model) {
        Staff staff = staffService.getStaffByUser(user);
        List<Shift> upcomingShifts = staffService.getUpcomingShifts(staff);
        model.addAttribute("shifts", upcomingShifts);
        // We'll add a DTO later for validation, just pass empty object for now
        // model.addAttribute("swapRequest", new SwapRequest());
        return "staff/post-swap"; // Tells Thymeleaf to find "staff/post-swap.html"
    }

    /**
     * Handles the submission of the post swap form.
     */
    @PostMapping("/post-swap")
    public String postSwap(@AuthenticationPrincipal User user,
                           @RequestParam Long shiftId, // Get shiftId from form
                           @RequestParam String reason, // Get reason from form
                           RedirectAttributes redirectAttributes) {
        try {
            Swap swap = swapService.postSwap(shiftId, user, reason);
            redirectAttributes.addFlashAttribute("success", // Add a success message
                    "Swap posted successfully! It will expire in 48 hours if not claimed.");
            return "redirect:/staff/dashboard"; // Redirect to dashboard after posting
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage()); // Show error message
            return "redirect:/staff/post-swap"; // Go back to the form if error
        }
    }

    /**
     * Handles the request to claim an available swap.
     */
    @PostMapping("/claim-swap/{swapId}") // swapId comes from the URL path
    public String claimSwap(@AuthenticationPrincipal User user,
                           @PathVariable Long swapId, // Get swapId from URL
                           RedirectAttributes redirectAttributes) {
        try {
            swapService.claimSwap(swapId, user);
            redirectAttributes.addFlashAttribute("success",
                    "Swap claimed successfully! The shift is now yours.");
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/staff/available-swaps"; // Redirect back to the available swaps list
    }

     /**
     * Handles requests to show the user's posted and claimed swaps.
     */
     @GetMapping("/my-swaps")
     public String mySwaps(@AuthenticationPrincipal User user, Model model) {
         List<Swap> posted = swapService.getMyPostedSwaps(user);
         List<Swap> claimed = swapService.getMyClaimedSwaps(user);

         model.addAttribute("postedSwaps", posted);
         model.addAttribute("claimedSwaps", claimed);
         return "staff/my-swaps"; // Tells Thymeleaf to find "staff/my-swaps.html"
     }
}