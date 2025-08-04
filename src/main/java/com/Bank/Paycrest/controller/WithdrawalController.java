package com.Bank.Paycrest.controller;


import com.Bank.Paycrest.model.Account;
import com.Bank.Paycrest.model.User;
import com.Bank.Paycrest.repository.AccountRepository;
import com.Bank.Paycrest.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.math.BigDecimal;


@Controller
class WithdrawalController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountRepository accountRepository;

    @PostMapping("/withdraw")
    public String withdraw(@AuthenticationPrincipal User user,
                           @RequestParam BigDecimal amount,
                           @RequestParam String pin,
                           Model model) {
        Account account = accountRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Account not found."));
        try {
            transactionService.withdraw(account, amount, pin);
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("account", account);
            return "dashboard";
        }
        return "redirect:/dashboard";
    }
}