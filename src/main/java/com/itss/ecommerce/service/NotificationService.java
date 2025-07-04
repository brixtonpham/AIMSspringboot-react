package com.itss.ecommerce.service;

public interface NotificationService {

    /**
     * Sends a notification to the user.
     *
     * @param userId the ID of the user to notify
     * @param message the message to send
     */
    void sendUpdatePasswordNotification(Long userId, String message);

    /**
     * Sends an email notification to the user.
     *
     * @param email the email address of the user
     * @param subject the subject of the email
     * @param body the body of the email
     */
    void sendPaymentNotification(String email, String subject, String body);
}
