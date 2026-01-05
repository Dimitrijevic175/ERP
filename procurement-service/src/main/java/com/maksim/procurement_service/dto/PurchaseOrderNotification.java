package com.maksim.procurement_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class PurchaseOrderNotification {
    private String email;
    private String subject;
    private String body;
    private Long receiverId; // supplier contact id
    private String confirmUrl;
    private String closeUrl;
    private List<byte[]> attachmentsBytes;// putanje do PDF fajlova
}
