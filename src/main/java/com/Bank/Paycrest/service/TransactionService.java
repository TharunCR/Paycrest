package com.Bank.Paycrest.service;

import com.Bank.Paycrest.model.Account;
import com.Bank.Paycrest.model.Transaction;
import com.Bank.Paycrest.model.User;
import com.Bank.Paycrest.repository.AccountRepository;
import com.Bank.Paycrest.repository.TransactionRepository;
import com.Bank.Paycrest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;



@Service
public class TransactionService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private FraudDetectionService fraudDetectionService;

    @Transactional
    public void deposit(Account account, BigDecimal amount, String pin) {
        if (!userService.verifyTransactionPin(account.getUser(), pin)) {
            throw new RuntimeException("Invalid Transaction PIN");
        }
        // Update balance
        account.setBalance(account.getBalance().add(amount));

        // Create transaction and add it to the account's list
        Transaction transaction = new Transaction(amount, "Deposit", LocalDateTime.now(), account);
        account.getTransactions().add(transaction);

        // Save the account, which will cascade and save the new transaction
        accountRepository.save(account);
    }

    @Transactional
    public void withdraw(Account account, BigDecimal amount, String pin) {
        if (!userService.verifyTransactionPin(account.getUser(), pin)) {
            throw new RuntimeException("Invalid Transaction PIN");
        }
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        // Update balance
        account.setBalance(account.getBalance().subtract(amount));

        // Create transaction and add it to the account's list
        Transaction transaction = new Transaction(amount, "Withdrawal", LocalDateTime.now(), account);
        account.getTransactions().add(transaction);

        // Save the account, which will cascade and save the new transaction
        accountRepository.save(account);
    }

    @Transactional
    public void transferAmount(Account fromAccount, String toUsername, BigDecimal amount, String pin) {

        // 1. RECIPIENT VALIDITY CHECK (MOVED UP)
        User toUser = userRepository.findByUsername(toUsername)
                .orElseThrow(() -> new RuntimeException("Recipient user not found"));
        Account toAccount = accountRepository.findByUser(toUser)
                .orElseThrow(() -> new RuntimeException("Recipient account not found"));

        // 2. PIN CHECK
        if (!userService.verifyTransactionPin(fromAccount.getUser(), pin)) {
            throw new RuntimeException("Invalid Transaction PIN");
        }

        // 3. BALANCE CHECK
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        // 4. AI FRAUD CHECK (MOVED DOWN - Only check risk if everything else is valid)
        if (fraudDetectionService.isTransferUnauthorized(fromAccount, amount)) {
            throw new RuntimeException("Transaction flagged as UNAUTHORIZED by AI system. Cannot proceed.");
        }

        // --- Execution continues here if all 4 checks pass ---

        // Update balances
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        Transaction debit = new Transaction(amount, "Transfer Out to " + toUsername, LocalDateTime.now(), fromAccount);
        Transaction credit = new Transaction(amount, "Transfer In from " + fromAccount.getUser().getUsername(), LocalDateTime.now(), toAccount);

        fromAccount.getTransactions().add(debit);
        toAccount.getTransactions().add(credit);

        // Save both accounts to persist all changes and new transactions
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionHistory(Account account) {
        // This method remains the same and is correct.
        return transactionRepository.findByAccountId(account.getId());

    }
}