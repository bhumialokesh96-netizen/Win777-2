package com.win777.backend.service;

import com.win777.backend.entity.SMSJob;
import com.win777.backend.entity.SMSRateConfig;
import com.win777.backend.entity.User;
import com.win777.backend.entity.WalletLedger;
import com.win777.backend.enums.SMSJobStatus;
import com.win777.backend.enums.TransactionType;
import com.win777.backend.repository.SMSJobRepository;
import com.win777.backend.repository.SMSRateConfigRepository;
import com.win777.backend.repository.UserRepository;
import com.win777.backend.repository.WalletLedgerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SMSJobServiceTest {

    @Mock
    private SMSJobRepository smsJobRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletLedgerRepository walletLedgerRepository;

    @Mock
    private SMSRateConfigRepository smsRateConfigRepository;

    @InjectMocks
    private SMSJobService smsJobService;

    private User user;
    private SMSJob smsJob;
    private SMSRateConfig rateConfig;
    private UUID userId;
    private UUID jobId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        jobId = UUID.randomUUID();

        // Setup user
        user = new User();
        user.setId(userId);
        user.setUsername("testuser");

        // Setup SMS job
        smsJob = new SMSJob();
        smsJob.setId(jobId);
        smsJob.setUser(user);
        smsJob.setStatus(SMSJobStatus.CLAIMED);

        // Setup rate config
        rateConfig = new SMSRateConfig();
        rateConfig.setSmsEarningRate(new BigDecimal("10.00"));
        rateConfig.setIsActive(true);
    }

    @Test
    void testCompleteSmsJob_Success_NoReferrals() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(smsJobRepository.findById(jobId)).thenReturn(Optional.of(smsJob));
        when(smsRateConfigRepository.findByIsActive(true)).thenReturn(Optional.of(rateConfig));
        when(smsJobRepository.save(any(SMSJob.class))).thenReturn(smsJob);
        when(walletLedgerRepository.save(any(WalletLedger.class))).thenReturn(new WalletLedger());

        // Act
        smsJobService.completeSmsJob(userId, jobId);

        // Assert
        verify(smsJobRepository).save(argThat(job ->
                job.getStatus() == SMSJobStatus.COMPLETED &&
                job.getCompletedAt() != null
        ));

        verify(walletLedgerRepository, times(1)).save(argThat(ledger ->
                ledger.getUser().equals(user) &&
                ledger.getAmount().compareTo(new BigDecimal("10.00")) == 0 &&
                ledger.getTransactionType() == TransactionType.SMS_EARNING &&
                ledger.getReferenceId().equals(jobId)
        ));
    }

    @Test
    void testCompleteSmsJob_Success_WithOneReferral() {
        // Arrange
        User referrer1 = new User();
        referrer1.setId(UUID.randomUUID());
        referrer1.setUsername("referrer1");
        user.setReferrer(referrer1);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(smsJobRepository.findById(jobId)).thenReturn(Optional.of(smsJob));
        when(smsRateConfigRepository.findByIsActive(true)).thenReturn(Optional.of(rateConfig));
        when(smsJobRepository.save(any(SMSJob.class))).thenReturn(smsJob);
        when(walletLedgerRepository.save(any(WalletLedger.class))).thenReturn(new WalletLedger());

        // Act
        smsJobService.completeSmsJob(userId, jobId);

        // Assert
        ArgumentCaptor<WalletLedger> ledgerCaptor = ArgumentCaptor.forClass(WalletLedger.class);
        verify(walletLedgerRepository, times(2)).save(ledgerCaptor.capture());

        List<WalletLedger> capturedLedgers = ledgerCaptor.getAllValues();

        // First entry: SMS earning for user
        WalletLedger smsEarning = capturedLedgers.get(0);
        assertEquals(user, smsEarning.getUser());
        assertEquals(new BigDecimal("10.00"), smsEarning.getAmount());
        assertEquals(TransactionType.SMS_EARNING, smsEarning.getTransactionType());

        // Second entry: Level 1 referral bonus (10%)
        WalletLedger referralBonus = capturedLedgers.get(1);
        assertEquals(referrer1, referralBonus.getUser());
        assertEquals(new BigDecimal("1.00"), referralBonus.getAmount());
        assertEquals(TransactionType.REFERRAL_BONUS, referralBonus.getTransactionType());
    }

    @Test
    void testCompleteSmsJob_Success_WithThreeReferrals() {
        // Arrange
        User referrer1 = new User();
        referrer1.setId(UUID.randomUUID());
        referrer1.setUsername("referrer1");

        User referrer2 = new User();
        referrer2.setId(UUID.randomUUID());
        referrer2.setUsername("referrer2");

        User referrer3 = new User();
        referrer3.setId(UUID.randomUUID());
        referrer3.setUsername("referrer3");

        user.setReferrer(referrer1);
        referrer1.setReferrer(referrer2);
        referrer2.setReferrer(referrer3);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(smsJobRepository.findById(jobId)).thenReturn(Optional.of(smsJob));
        when(smsRateConfigRepository.findByIsActive(true)).thenReturn(Optional.of(rateConfig));
        when(smsJobRepository.save(any(SMSJob.class))).thenReturn(smsJob);
        when(walletLedgerRepository.save(any(WalletLedger.class))).thenReturn(new WalletLedger());

        // Act
        smsJobService.completeSmsJob(userId, jobId);

        // Assert
        ArgumentCaptor<WalletLedger> ledgerCaptor = ArgumentCaptor.forClass(WalletLedger.class);
        verify(walletLedgerRepository, times(4)).save(ledgerCaptor.capture());

        List<WalletLedger> capturedLedgers = ledgerCaptor.getAllValues();

        // First entry: SMS earning
        assertEquals(TransactionType.SMS_EARNING, capturedLedgers.get(0).getTransactionType());
        assertEquals(new BigDecimal("10.00"), capturedLedgers.get(0).getAmount());

        // Second entry: Level 1 referral bonus (10%)
        assertEquals(referrer1, capturedLedgers.get(1).getUser());
        assertEquals(new BigDecimal("1.00"), capturedLedgers.get(1).getAmount());
        assertEquals(TransactionType.REFERRAL_BONUS, capturedLedgers.get(1).getTransactionType());

        // Third entry: Level 2 referral bonus (2%)
        assertEquals(referrer2, capturedLedgers.get(2).getUser());
        assertEquals(new BigDecimal("0.20"), capturedLedgers.get(2).getAmount());
        assertEquals(TransactionType.REFERRAL_BONUS, capturedLedgers.get(2).getTransactionType());

        // Fourth entry: Level 3 referral bonus (1%)
        assertEquals(referrer3, capturedLedgers.get(3).getUser());
        assertEquals(new BigDecimal("0.10"), capturedLedgers.get(3).getAmount());
        assertEquals(TransactionType.REFERRAL_BONUS, capturedLedgers.get(3).getTransactionType());
    }

    @Test
    void testCompleteSmsJob_ThrowsException_UserNotFound() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                smsJobService.completeSmsJob(userId, jobId)
        );

        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(smsJobRepository, never()).save(any());
        verify(walletLedgerRepository, never()).save(any());
    }

    @Test
    void testCompleteSmsJob_ThrowsException_JobNotFound() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(smsJobRepository.findById(jobId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                smsJobService.completeSmsJob(userId, jobId)
        );

        assertEquals("SMS job not found with id: " + jobId, exception.getMessage());
        verify(smsJobRepository, never()).save(any());
        verify(walletLedgerRepository, never()).save(any());
    }

    @Test
    void testCompleteSmsJob_ThrowsException_OwnershipValidationFails() {
        // Arrange
        User differentUser = new User();
        differentUser.setId(UUID.randomUUID());
        smsJob.setUser(differentUser);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(smsJobRepository.findById(jobId)).thenReturn(Optional.of(smsJob));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                smsJobService.completeSmsJob(userId, jobId)
        );

        assertEquals("User does not own this SMS job", exception.getMessage());
        verify(smsJobRepository, never()).save(any());
        verify(walletLedgerRepository, never()).save(any());
    }

    @Test
    void testCompleteSmsJob_ThrowsException_JobNotInClaimedStatus() {
        // Arrange
        smsJob.setStatus(SMSJobStatus.PENDING);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(smsJobRepository.findById(jobId)).thenReturn(Optional.of(smsJob));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                smsJobService.completeSmsJob(userId, jobId)
        );

        assertTrue(exception.getMessage().contains("SMS job must be in CLAIMED status"));
        verify(smsJobRepository, never()).save(any());
        verify(walletLedgerRepository, never()).save(any());
    }

    @Test
    void testCompleteSmsJob_ThrowsException_NoActiveRateConfig() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(smsJobRepository.findById(jobId)).thenReturn(Optional.of(smsJob));
        when(smsRateConfigRepository.findByIsActive(true)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                smsJobService.completeSmsJob(userId, jobId)
        );

        assertEquals("No active SMS rate configuration found", exception.getMessage());
        verify(smsJobRepository, never()).save(any());
        verify(walletLedgerRepository, never()).save(any());
    }

    @Test
    void testCompleteSmsJob_ThrowsException_JobUserIsNull() {
        // Arrange
        smsJob.setUser(null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(smsJobRepository.findById(jobId)).thenReturn(Optional.of(smsJob));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                smsJobService.completeSmsJob(userId, jobId)
        );

        assertEquals("User does not own this SMS job", exception.getMessage());
        verify(smsJobRepository, never()).save(any());
        verify(walletLedgerRepository, never()).save(any());
    }
}
