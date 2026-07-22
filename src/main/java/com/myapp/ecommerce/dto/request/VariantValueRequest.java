package com.myapp.ecommerce.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request payload for creating a variant value (e.g., 'S', 'M', 'Red').")
public class VariantValueRequest {

    @Schema(description = "ID of the parent option this value belongs to.")
    @NotBlank(message = "INVALID_OPTION_ID")
    UUID optionId;

    @Schema(description = "Value display text, e.g. 'Small', 'Red'.")
    @NotBlank(message = "INVALID_VALUE")
    @Size(min = 1, max = 100, message = "INVALID_VALUE")
    String value;

    @Schema(description = "Optional short code, e.g. 'sm', 'rd'.")
    @Size(max = 50, message = "INVALID_VALUE_CODE")
    String code;
}
