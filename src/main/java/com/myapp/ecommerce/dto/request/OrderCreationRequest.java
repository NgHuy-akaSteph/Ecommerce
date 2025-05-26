package com.myapp.ecommerce.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderCreationRequest {
    @Min(value = 1, message = "INVALID_PRICE_ORDER")
    double totalPrice;
    String reciverName;
    String reciverAddress;
    @Pattern(regexp = "(^$|[0-9]{10})")
    String reciverPhone;
    String status;
    String userId;
    List<DetailRequest> detail;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class DetailRequest {
        @JsonProperty("_id")
        String id;
    }
}
