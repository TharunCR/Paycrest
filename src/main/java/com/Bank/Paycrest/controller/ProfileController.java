package com.Bank.Paycrest.controller;

import com.Bank.Paycrest.model.User;
import com.Bank.Paycrest.repository.UserRepository;
import com.Bank.Paycrest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Displays the user's profile page.
     */
    @GetMapping("/profile")
    public String showProfilePage(@AuthenticationPrincipal User currentUser, Model model) {
        // Fetch the full user object from the database to ensure all data is current
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        return "profile";
    }

    /**
     * Sets or changes the user's 4-digit transaction PIN.
     */
    @PostMapping("/set-pin")
    public String setTransactionPin(@AuthenticationPrincipal User user,
                                    @RequestParam String newPin,
                                    @RequestParam String confirmPin,
                                    RedirectAttributes redirectAttributes) {

        // Check if the two entered PINs match
        if (!newPin.equals(confirmPin)) {
            redirectAttributes.addFlashAttribute("error_message", "PINs do not match. Please try again.");
            return "redirect:/profile";
        }

        // Validate that the PIN is exactly 4 digits
        if (!newPin.matches("\\d{4}")) {
            redirectAttributes.addFlashAttribute("error_message", "PIN must be exactly 4 digits.");
            return "redirect:/profile";
        }

        // Call the service to hash and save the new PIN
        userService.setTransactionPin(user, newPin);

        redirectAttributes.addFlashAttribute("success_message", "Your transaction PIN has been set successfully!");
        return "redirect:/profile";
    }

    /**
     * Updates the user's profile information (e.g., full name)
     */
    @PostMapping("/profile/update")
    public String updateUserProfile(@AuthenticationPrincipal User currentUser,
                                    @RequestParam String fullName,
                                    RedirectAttributes redirectAttributes) {

        userService.updateUserProfile(currentUser.getId(), fullName);
        redirectAttributes.addFlashAttribute("success_message", "Profile updated successfully!");
        return "redirect:/profile";
    }
}