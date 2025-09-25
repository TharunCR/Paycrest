package com.Bank.Paycrest.controller;


import com.Bank.Paycrest.model.Account;
import com.Bank.Paycrest.model.User;
import com.Bank.Paycrest.repository.AccountRepository;
import com.Bank.Paycrest.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.Optional;

@Controller
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/transactions")
    public String transactionHistory(@AuthenticationPrincipal User user, Model model) {
        // Find the account associated with the logged-in user
        Optional<Account> accountOpt = accountRepository.findByUser(user);

        if (accountOpt.isPresent()) {
            // If the account exists, get its transaction history.
            model.addAttribute("transactions", transactionService.getTransactionHistory(accountOpt.get()));
        } else {
            // If no account is found, return an empty list to avoid errors
            model.addAttribute("error", "No bank account found for this user.");
            model.addAttribute("transactions", Collections.emptyList());
        }

        return "transactions";
    }
}
