package com.maksim.procurement_service.listener;

import com.maksim.procurement_service.dto.PurchaseOrderNotification;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;


@Component
public class NotificationSender {

    private final JmsTemplate jmsTemplate;
    private final MessageHelper messageHelper;

    public NotificationSender(JmsTemplate jmsTemplate, MessageHelper messageHelper) {
        this.jmsTemplate = jmsTemplate;
        this.messageHelper = messageHelper;
    }

    public void sendPurchaseOrderEmail(PurchaseOrderNotification notification) {
        String jsonMessage = messageHelper.createTextMessage(notification);
        jmsTemplate.send("queue.erp", session -> session.createTextMessage(jsonMessage));
    }
}

