package com.Bank.Paycrest.service;

import com.Bank.Paycrest.model.Account;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class FraudDetectionService {

    private final RestTemplate restTemplate = new RestTemplate();

    // Fetches the URL from application.properties
    @Value("${ml.api.url}")
    private String mlApiUrl;

    // Data structure to send to Python API (must match api.py's TransactionFeatures)
    // Inside FraudDetectionService.java

    private static class PredictionRequest {
        // ⚠️ CRITICAL: Change to double and ensure keys are correct ⚠️
        public double Amount_INR;
        public int DayOfWeek;
        public int Hour;

        // Constructor fix: Convert BigDecimal to a double
        public PredictionRequest(BigDecimal amount, int dayOfWeek, int hour) {
            this.Amount_INR = amount.doubleValue(); // Use doubleValue() for conversion
            this.DayOfWeek = dayOfWeek;
            this.Hour = hour;
        }
    }

    // Data structure expected back from Python API: {"is_unauthorized": 1 or 0}
    private static class PredictionResponse {
        private int is_unauthorized;

        // Needed for RestTemplate to map the JSON response
        public void setIs_unauthorized(int is_unauthorized) {
            this.is_unauthorized = is_unauthorized;
        }
    }

    /**
     * Calls the external Python AI service to check if the transfer is unauthorized.
     * @return true if AI flags the transaction as unauthorized (anomaly), false otherwise.
     */
    public boolean isTransferUnauthorized(Account fromAccount, BigDecimal amount) {
        // 1. Prepare Features (using current time, as the transaction hasn't happened yet)
        LocalDateTime now = LocalDateTime.now();
        PredictionRequest request = new PredictionRequest(
                amount, // This is still a BigDecimal, but the constructor handles conversion
                LocalDateTime.now().getDayOfWeek().getValue(),
                LocalDateTime.now().getHour()
        );

        try {
            // 2. Call the Python API via REST
            String fullUrl = mlApiUrl + "/predict_fraud";
            PredictionResponse response = restTemplate.postForObject(
                    fullUrl,
                    request,
                    PredictionResponse.class
            );

            // 3. Check the prediction result (1 means unauthorized/anomaly)
            return response != null && response.is_unauthorized == 1;

        } catch (Exception e) {
            // ⚠️ SECURITY FAILSAFE: If the AI service is down or fails, BLOCK the transaction.
            System.err.println("🚨 ML Service Error. Blocking transaction: " + e.getMessage());
            return true;
        }
    }
}
