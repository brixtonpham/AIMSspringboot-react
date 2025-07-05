package com.itss.ecommerce.service.notification;

import com.itss.ecommerce.service.notification.type.Notification;
import com.itss.ecommerce.service.notification.type.NotificationServiceProvider;
import com.itss.ecommerce.entity.Order;
import com.itss.ecommerce.entity.User;
import java.util.List;

/**
 * Service for managing notifications across multiple channels
 */
public interface INotificationService {

    /**
     * Sends a notification using all available providers
     *
     * @param notification the notification to send
     * @return list of provider types that successfully sent the notification
     */
    List<NotificationServiceProvider> sendNotificationToAllChannels(Notification notification);

    /**
     * Sends order confirmation notification
     *
     * @param order the order to confirm
     * @return true if sent successfully, false otherwise
     */
    boolean sendPaymentConfirmation(Order order, NotificationServiceProvider provider);

    /**
     * Sends user blocked notification
     *
     * @param user the user that was blocked
     * @param reason the reason for blocking
     * @param blockedBy who blocked the user
     * @return true if sent successfully, false otherwise
     */
    boolean sendUserBlockedNotification(User user, String reason, String blockedBy, NotificationServiceProvider provider);

    /**
     * Sends user unblocked notification
     *
     * @param user the user that was unblocked
     * @param unblockedBy who unblocked the user
     * @return true if sent successfully, false otherwise
     */
    boolean sendUserUnblockedNotification(User user, String unblockedBy, NotificationServiceProvider provider);

    /**
     * Sends user registration welcome notification
     *
     * @param user the newly registered user
     * @param provider the notification service provider to use
     * @return true if sent successfully, false otherwise
     */
    boolean sendUserRegistrationNotification(User user, NotificationServiceProvider provider);

    /**
     * Sends password update notification
     *
     * @param user the user whose password was updated
     * @param provider the notification service provider to use
     * @return true if sent successfully, false otherwise
     */
    boolean sendPasswordUpdateNotification(User user, NotificationServiceProvider provider);

    /**
     * Gets all available notification providers
     *
     * @return list of available provider types
     */
    List<NotificationServiceProvider> getAvailableProviders();
}
