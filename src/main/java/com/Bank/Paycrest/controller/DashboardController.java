package com.Bank.Paycrest.controller;



import com.Bank.Paycrest.model.Account;
import com.Bank.Paycrest.model.User;
import com.Bank.Paycrest.repository.AccountRepository;

import com.Bank.Paycrest.repository.UserRepository;
import com.Bank.Paycrest.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
class DashboardController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal User currentUser, Model model, RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(currentUser.getId()).orElseThrow();
        if (user.getTransactionPin() == null) {
            redirectAttributes.addFlashAttribute("error_message", "Please set your Transaction PIN before proceeding.");
            return "redirect:/profile";
        }
        Account account = accountRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Account could not be found."));
        model.addAttribute("account", account);
        return "dashboard";

    }
}
