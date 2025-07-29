package io.github.ironslayer.spring_boot_starter_template.payment.domain.service;

import io.github.ironslayer.spring_boot_starter_template.payment.domain.entity.Payment;
import io.github.ironslayer.spring_boot_starter_template.payment.domain.enums.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 🕐 Payment Status Management Service
 * 
 * Handles the business logic for payment status transitions and timeouts.
 */
@Slf4j
@Service
public class PaymentStatusService {

    /**
     * Payment timeout configuration (in minutes)
     */
    private static final int PAYMENT_TIMEOUT_MINUTES = 15;

    /**
     * 🔄 Determine if a payment should be automatically cancelled due to timeout
     */
    public boolean shouldCancelDueToTimeout(Payment payment) {
        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            return false;
        }

        LocalDateTime createdAt = payment.getCreatedAt();
        LocalDateTime now = LocalDateTime.now();
        long minutesElapsed = ChronoUnit.MINUTES.between(createdAt, now);

        boolean shouldCancel = minutesElapsed >= PAYMENT_TIMEOUT_MINUTES;
        
        if (shouldCancel) {
            log.info("Payment {} should be cancelled due to timeout. Created: {}, Minutes elapsed: {}", 
                    payment.getId(), createdAt, minutesElapsed);
        }

        return shouldCancel;
    }

    /**
     * 📝 Get payment status description for UI/API responses
     */
    public String getStatusDescription(PaymentStatus status) {
        return switch (status) {
            case PENDING -> "Payment is pending completion";
            case PAID -> "Payment completed successfully";
            case CANCELLED -> "Payment was cancelled or expired";
        };
    }

    /**
     * 🎯 Get next available actions for a payment based on current status
     */
    public String[] getAvailableActions(PaymentStatus status) {
        return switch (status) {
            case PENDING -> new String[]{"complete_payment", "cancel_payment"};
            case PAID -> new String[]{"refund_payment"};
            case CANCELLED -> new String[]{}; // No actions available
        };
    }

    /**
     * ✅ Validate if status transition is allowed
     */
    public boolean isValidStatusTransition(PaymentStatus from, PaymentStatus to) {
        return switch (from) {
            case PENDING -> to == PaymentStatus.PAID || to == PaymentStatus.CANCELLED;
            case PAID -> false; // Paid payments can't change status (refund would create new record)
            case CANCELLED -> false; // Cancelled payments can't change status
        };
    }

    /**
     * 🕒 Get remaining time for pending payment (in minutes)
     */
    public long getRemainingTimeMinutes(Payment payment) {
        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            return 0;
        }

        LocalDateTime createdAt = payment.getCreatedAt();
        LocalDateTime now = LocalDateTime.now();
        long minutesElapsed = ChronoUnit.MINUTES.between(createdAt, now);

        return Math.max(0, PAYMENT_TIMEOUT_MINUTES - minutesElapsed);
    }

    /**
     * 🔔 Check if payment needs urgent attention (close to timeout)
     */
    public boolean isNearTimeout(Payment payment) {
        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            return false;
        }

        long remainingMinutes = getRemainingTimeMinutes(payment);
        return remainingMinutes <= 5 && remainingMinutes > 0; // Alert when 5 minutes or less remain
    }
}
