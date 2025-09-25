package com.Bank.Paycrest.service;


import com.Bank.Paycrest.model.Account;
import com.Bank.Paycrest.model.User;
import com.Bank.Paycrest.repository.AccountRepository;
import com.Bank.Paycrest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("User already exists: " + username);
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        User savedUser = userRepository.save(user);

        Account account = new Account();
        account.setBalance(BigDecimal.ZERO);
        account.setUser(savedUser);
        accountRepository.save(account);

        return savedUser;
    }

    /**
     * Hashes and saves a new transaction PIN for the user.
     * ADD THIS METHOD.
     */
    @Transactional
    public void setTransactionPin(User user, String newPin) {
        // Find the managed user entity to ensure the update persists
        User userToUpdate = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        userToUpdate.setTransactionPin(passwordEncoder.encode(newPin));
        userRepository.save(userToUpdate);
    }

    /**
     * Verifies if a raw PIN matches the user's stored, hashed PIN.
     * ADD THIS METHOD.
     */
    public boolean verifyTransactionPin(User user, String rawPin) {
        if (user.getTransactionPin() == null) {
            return false; // User has not set a PIN yet
        }
        return passwordEncoder.matches(rawPin, user.getTransactionPin());
    }

    /**
     * Updates the user's profile information.
     */
    @Transactional
    public void updateUserProfile(Long userId, String fullName) {
        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userToUpdate.setFullName(fullName);
        userRepository.save(userToUpdate);

    }
}