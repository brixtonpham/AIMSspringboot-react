package com.itss.ecommerce.service;

import com.itss.ecommerce.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {
    
    @Mock
    private JavaMailSender mailSender;
    
    @InjectMocks
    private EmailService emailService;
    
    @Captor
    private ArgumentCaptor<SimpleMailMessage> messageCaptor;
    
    private Order sampleOrder;
    private DeliveryInformation deliveryInfo;
    private User sampleUser;
    
    @BeforeEach
    void setUp() {
        // Sample Delivery Information
        deliveryInfo = new DeliveryInformation();
        deliveryInfo.setDeliveryId(1L);
        deliveryInfo.setName("John Doe");
        deliveryInfo.setPhone("0123456789");
        deliveryInfo.setEmail("john.doe@example.com");
        deliveryInfo.setProvince("Ho Chi Minh City");
        deliveryInfo.setDistrict("District 1");
        deliveryInfo.setWard("Ward 1");
        deliveryInfo.setAddress("123 Main Street");
        deliveryInfo.setDeliveryMessage("Please call before delivery");
        
        // Sample Product for Order Items
        Product sampleProduct = new Book();
        sampleProduct.setProductId(1L);
        sampleProduct.setTitle("The Great Gatsby");
        sampleProduct.setPrice(150000);
        
        // Sample Order Item
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderItemId(1L);
        orderItem.setProduct(sampleProduct);
        orderItem.setQuantity(2);
        orderItem.setTotalFee(300000);
        orderItem.setStatus(OrderItem.OrderItemStatus.PENDING);
        
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);
        
        // Sample Order
        sampleOrder = new Order();
        sampleOrder.setOrderId(12345L);
        sampleOrder.setStatus(Order.OrderStatus.CONFIRMED);
        sampleOrder.setDeliveryInformation(deliveryInfo);
        sampleOrder.setTotalBeforeVat(300000);
        sampleOrder.setTotalAfterVat(330000);
        sampleOrder.setVatPercentage(10);
        sampleOrder.setOrderItems(orderItems);
        
        // Sample User
        sampleUser = new User();
        sampleUser.setUserId(1L);
        sampleUser.setName("Jane Smith");
        sampleUser.setEmail("jane.smith@example.com");
        sampleUser.setPhone("0987654321");
        sampleUser.setRole(User.UserRole.MANAGER);
        sampleUser.setIsActive(true);
    }
    
    @Test
    @DisplayName("Test Send Order Confirmation Email - UT027")
    void testSendOrderConfirmation() {
        // Given - Arrange test data
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        
        // When - Act on the method under test
        assertThatCode(() -> emailService.sendOrderConfirmationEmail(sampleOrder))
            .doesNotThrowAnyException();
        
        // Then - Assert expected results
        verify(mailSender, times(1)).send(messageCaptor.capture());
        
        // Verify email content
        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertThat(sentMessage.getTo()).contains("john.doe@example.com");
        assertThat(sentMessage.getSubject()).contains("Order Confirmation");
        assertThat(sentMessage.getSubject()).contains("12345");
        assertThat(sentMessage.getText()).contains("John Doe");
        assertThat(sentMessage.getText()).contains("The Great Gatsby");
        assertThat(sentMessage.getText()).contains("330000");
    }
    
    @Test
    @DisplayName("Test Send User Blocked Email")
    void testSendUserBlockedEmail() {
        // Given - Arrange test data
        String reason = "Violation of terms of service";
        String blockedBy = "Admin";
        
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        
        // When - Act on the method under test
        assertThatCode(() -> emailService.sendUserBlockedEmail(sampleUser, reason, blockedBy))
            .doesNotThrowAnyException();
        
        // Then - Assert expected results
        verify(mailSender, times(1)).send(messageCaptor.capture());
        
        // Verify email content
        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertThat(sentMessage.getTo()).contains("jane.smith@example.com");
        assertThat(sentMessage.getSubject()).contains("Account Access Suspended");
        assertThat(sentMessage.getText()).contains("Jane Smith");
        assertThat(sentMessage.getText()).contains(reason);
    }
    
    @Test
    @DisplayName("Test Send User Unblocked Email")
    void testSendUserUnblockedEmail() {
        // Given - Arrange test data
        String unblockedBy = "Admin";
        
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        
        // When - Act on the method under test
        assertThatCode(() -> emailService.sendUserUnblockedEmail(sampleUser, unblockedBy))
            .doesNotThrowAnyException();
        
        // Then - Assert expected results
        verify(mailSender, times(1)).send(messageCaptor.capture());
        
        // Verify email content
        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertThat(sentMessage.getTo()).contains("jane.smith@example.com");
        assertThat(sentMessage.getSubject()).contains("Account Access Restored");
        assertThat(sentMessage.getText()).contains("Jane Smith");
        assertThat(sentMessage.getText()).contains("restored");
    }
    
    @Test
    @DisplayName("Test Email Service Handles Mail Sender Exception")
    void testEmailServiceHandlesMailSenderException() {
        // Given - Arrange test data to simulate email sending failure
        doThrow(new RuntimeException("Mail server unavailable"))
            .when(mailSender).send(any(SimpleMailMessage.class));
        
        // When & Then - Act and assert no exception is thrown (service should handle gracefully)
        assertThatCode(() -> emailService.sendOrderConfirmationEmail(sampleOrder))
            .doesNotThrowAnyException();
        
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
    
    @Test
    @DisplayName("Test Order Confirmation Email Content Structure")
    void testOrderConfirmationEmailContentStructure() {
        // Given - Arrange test data
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        
        // When - Act on the method under test
        emailService.sendOrderConfirmationEmail(sampleOrder);
        
        // Then - Verify email structure and content
        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();
        String text = sentMessage.getText();
        
        // Verify essential order information is included
        assertThat(text).contains("Dear John Doe");
        assertThat(text).contains("Order ID: 12345");
        assertThat(text).contains("Total Amount: $330000");
        assertThat(text).contains("The Great Gatsby");
        assertThat(text).contains("Quantity: 2");
        assertThat(text).contains("Name: John Doe");
        assertThat(text).contains("Phone: 0123456789");
        assertThat(text).contains("Address: 123 Main Street");
        assertThat(text).contains("Thank you for shopping with us");
    }
    
    @Test
    @DisplayName("Test User Blocked Email Content Structure")
    void testUserBlockedEmailContentStructure() {
        // Given - Arrange test data
        String reason = "Multiple failed login attempts";
        String blockedBy = "Security System";
        
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        
        // When - Act on the method under test
        emailService.sendUserBlockedEmail(sampleUser, reason, blockedBy);
        
        // Then - Verify email structure and content
        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();
        String text = sentMessage.getText();
        
        // Verify essential information is included
        assertThat(text).contains("Dear Jane Smith");
        assertThat(text).contains("account access has been temporarily suspended");
        assertThat(text).contains("Email: jane.smith@example.com");
        assertThat(text).contains("Account ID: 1");
        assertThat(text).contains("Reason for suspension: " + reason);
        assertThat(text).contains("support@itss-ecommerce.com");
        assertThat(text).contains("+84 123 456 789");
    }
    
    @Test
    @DisplayName("Test Email Sending With Null Values Handles Gracefully")
    void testEmailSendingWithNullValuesHandlesGracefully() {
        // Given - Arrange test data with null order
        Order nullOrder = null;
        
        // When & Then - Act and verify service handles null gracefully
        assertThatCode(() -> emailService.sendOrderConfirmationEmail(nullOrder))
            .doesNotThrowAnyException();
        
        // Verify no email is sent when order is null
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }
}
