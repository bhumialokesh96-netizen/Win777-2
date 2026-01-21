package com.win777.backend.service;

import com.win777.backend.entity.SMSRateConfig;
import com.win777.backend.repository.SMSRateConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Service for SMS rate management.
 * Provides read-only access to the active SMS rate configuration.
 */
@Service
public class SmsRateService {

    private final SMSRateConfigRepository smsRateConfigRepository;

    public SmsRateService(SMSRateConfigRepository smsRateConfigRepository) {
        this.smsRateConfigRepository = smsRateConfigRepository;
    }

    /**
     * Gets the current active SMS earning rate.
     * 
     * @return the current SMS earning rate
     * @throws IllegalStateException if no active rate configuration is found
     */
    @Transactional(readOnly = true)
    public BigDecimal getCurrentRate() {
        SMSRateConfig activeConfig = smsRateConfigRepository.findByIsActive(true)
                .orElseThrow(() -> new IllegalStateException("No active SMS rate configuration found"));
        return activeConfig.getSmsEarningRate();
    }

    /**
     * Gets the current active SMS rate configuration.
     * 
     * @return the active SMS rate configuration
     * @throws IllegalStateException if no active rate configuration is found
     */
    @Transactional(readOnly = true)
    public SMSRateConfig getCurrentRateConfig() {
        return smsRateConfigRepository.findByIsActive(true)
                .orElseThrow(() -> new IllegalStateException("No active SMS rate configuration found"));
    }
}
