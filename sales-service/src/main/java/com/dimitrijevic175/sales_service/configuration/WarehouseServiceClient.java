package com.dimitrijevic175.sales_service.configuration;


import com.dimitrijevic175.sales_service.dto.CheckAvailabilityRequestDto;
import com.dimitrijevic175.sales_service.dto.CheckAvailabilityResponseDto;
import com.dimitrijevic175.sales_service.dto.DispatchNoteDto;
import com.dimitrijevic175.sales_service.dto.DispatchNoteRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class WarehouseServiceClient {

    private final WebClient warehouseWebClient;
    private static final Logger logger = LoggerFactory.getLogger(WarehouseServiceClient.class);


    public WarehouseServiceClient(WebClient warehouseWebClient) {
        this.warehouseWebClient = warehouseWebClient;
    }

    public CheckAvailabilityResponseDto checkAvailability(CheckAvailabilityRequestDto request) {
        logger.debug("Checking availability in warehouse service");

        CheckAvailabilityResponseDto response = warehouseWebClient.post()
                .uri("/warehouses/checkAvailability")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(CheckAvailabilityResponseDto.class)
                .block();

        if (response == null || response.getWarehouseId() == null) {
            logger.warn("No warehouse can fulfill the order");
            return null;
        }

        return response;
    }

    public void createDispatchNote(DispatchNoteRequestDto request) {
        warehouseWebClient.post()
                .uri("/dispatch-notes")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }


    public DispatchNoteDto getDispatchNoteBySalesOrderId(Long salesOrderId) {
        try {
            return warehouseWebClient
                    .get()
                    .uri("/dispatch-notes/by-sales-order/{orderId}", salesOrderId)
                    .retrieve()
                    .bodyToMono(DispatchNoteDto.class)
                    .block();
        } catch (Exception e) {
            logger.warn("Could not retrieve dispatch note for salesOrder {}: {}", salesOrderId, e.getMessage());
            return null;
        }
    }

    public void rollbackDispatchNote(Long dispatchNoteId) {
        warehouseWebClient
                .post()
                .uri("/dispatch-notes/{id}/rollback", dispatchNoteId)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .map(body -> {
                                    logger.error("Warehouse rollback failed for dispatchNote {}: {}", dispatchNoteId, body);
                                    return new RuntimeException("Warehouse rollback failed: " + body);
                                })
                )
                .bodyToMono(Void.class)
                .block();
    }

}
