package com.example.foodordersystem.model.dto.request;

import lombok.Data;

@Data
public class BulkAvailabilityRequest {
    private Long id;
    private boolean available;
}
