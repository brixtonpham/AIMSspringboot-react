package com.itss.ecommerce.service.notification;

import com.itss.ecommerce.service.notification.strategy.INotificationStrategy;
import com.itss.ecommerce.service.notification.type.Notification;
import com.itss.ecommerce.service.notification.type.NotificationServiceProvider;
import com.itss.ecommerce.service.notification.type.NotificationType;
import com.itss.ecommerce.service.notification.utils.NotificationMessageBuilder;
import com.itss.ecommerce.entity.Order;
import com.itss.ecommerce.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class NotificationService implements INotificationService {

    private final Map<NotificationServiceProvider, INotificationStrategy> providers;

    @Autowired
    public NotificationService(List<INotificationStrategy> providerList) {
        this.providers = providerList.stream()
                .collect(Collectors.toMap(
                        INotificationStrategy::getProvider,
                        Function.identity()
                ));
    }

    /**
     * Sends a notification using the specified provider type
     *
     * @param notification the notification to send
     * @param provider the provider type to use
     * @return true if sent successfully, false otherwise
     */
    private boolean sendNotification(Notification notification, NotificationServiceProvider provider) {
        INotificationStrategy strategy = providers.get(provider);
        if (provider == null) {
            System.err.println("No provider found for type: " + provider);
            return false;
        }
        
        if (!strategy.isAvailable()) {
            System.err.println("Provider not available: " + provider);
            return false;
        }
        
        return strategy.send(notification);
    }

    @Override
    public List<NotificationServiceProvider> sendNotificationToAllChannels(Notification notification) {
        List<NotificationServiceProvider> successfulProviders = new ArrayList<>();
        
        for (INotificationStrategy provider : providers.values()) {
            if (provider.isAvailable() && provider.send(notification)) {
                successfulProviders.add(provider.getProvider());
            }
        }
        
        return successfulProviders;
    }

    @Override
    public boolean sendPaymentConfirmation(Order order, NotificationServiceProvider provider) {
        if (order == null || order.getDeliveryInformation() == null) {
            System.err.println("Cannot send payment notification: Order or delivery information is null");
            return false;
        }
        
        String content = NotificationMessageBuilder.buildPaymentNotificationMessage(order);
        Notification notification = new Notification(
                order.getDeliveryInformation().getEmail(),
                "Payment Confirmation & Invoice - Order #" + order.getOrderId(),
                content,
                NotificationType.PAYMENT_NOTIFICATION
        );
        
        return sendNotification(notification, provider);
    }

    @Override
    public boolean sendUserBlockedNotification(User user, String reason, String blockedBy, NotificationServiceProvider provider) {
        if (user == null) {
            System.err.println("Cannot send account status notification: User is null");
            return false;
        }
        
        String content = NotificationMessageBuilder.buildAccountBlockedMessage(user, reason, blockedBy);
        Notification notification = new Notification(
                user.getEmail(),
                "Account Access Suspended - ITSS E-commerce",
                content,
                NotificationType.ACCOUNT_STATUS_UPDATE
        );
        
        return sendNotification(notification, NotificationServiceProvider.EMAIL);
    }

    @Override
    public boolean sendUserUnblockedNotification(User user, String unblockedBy, NotificationServiceProvider provider) {
        if (user == null) {
            System.err.println("Cannot send account status notification: User is null");
            return false;
        }
        
        String content = NotificationMessageBuilder.buildAccountUnblockedMessage(user, unblockedBy);
        Notification notification = new Notification(
                user.getEmail(),
                "Account Access Restored - ITSS E-commerce",
                content,
                NotificationType.ACCOUNT_STATUS_UPDATE
        );
        
        return sendNotification(notification, NotificationServiceProvider.EMAIL);
    }

    /**
     * Sends password update notification
     */
    public boolean sendPasswordUpdateNotification(User user, NotificationServiceProvider provider) {
        if (user == null) {
            System.err.println("Cannot send password update notification: User is null");
            return false;
        }
        
        String content = NotificationMessageBuilder.buildPasswordUpdateMessage(user);
        Notification notification = new Notification(
                user.getEmail(),
                "Password Updated Successfully - ITSS E-commerce",
                content,
                NotificationType.PASSWORD_UPDATE
        );
        
        return sendNotification(notification, NotificationServiceProvider.EMAIL);
    }

    @Override
    public List<NotificationServiceProvider> getAvailableProviders() {
        return providers.values().stream()
                .filter(INotificationStrategy::isAvailable)
                .map(INotificationStrategy::getProvider)
                .collect(Collectors.toList());
    }
}