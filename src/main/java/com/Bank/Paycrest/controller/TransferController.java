package com.Bank.Paycrest.controller;

import com.Bank.Paycrest.model.Account;
import com.Bank.Paycrest.model.User;
import com.Bank.Paycrest.repository.AccountRepository;
import com.Bank.Paycrest.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.math.BigDecimal;

@Controller
public class TransferController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountRepository accountRepository;

    @PostMapping("/transfer")
    public String transfer(@AuthenticationPrincipal User user,
                           @RequestParam String toUsername,
                           @RequestParam BigDecimal amount,
                           @RequestParam String pin, // <-- FIX: Add the PIN parameter
                           Model model) {
        Account fromAccount = accountRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Account not found for user."));
        try {
            // FIX: Pass the raw PIN from the request to the service
            transactionService.transferAmount(fromAccount, toUsername, amount, pin);
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("account", fromAccount); // Re-add account to render page correctly
            return "dashboard";
        }
        return "redirect:/dashboard";

    }
}