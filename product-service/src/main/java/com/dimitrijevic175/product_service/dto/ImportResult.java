package com.dimitrijevic175.product_service.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
public class ImportResult {
    private int totalRows;
    private int successfulImports;
    private int failedImports;
    private List<String> errors;
}
