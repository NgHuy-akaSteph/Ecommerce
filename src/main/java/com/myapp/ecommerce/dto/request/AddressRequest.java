package com.myapp.ecommerce.dto.request;

import com.myapp.ecommerce.entity.enums.AddressLabel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request payload for creating or updating an address.")
public class AddressRequest {

    @NotBlank(message = "INVALID_FULL_NAME")
    @Size(max = 255)
    String fullName;

    @Size(max = 20)
    String phone;

    @NotBlank(message = "INVALID_STREET")
    @Size(max = 500)
    String street;

    @Size(max = 255)
    String ward;

    @Size(max = 255)
    String district;

    @NotBlank(message = "INVALID_CITY")
    @Size(max = 255)
    String city;

    @Size(max = 20)
    String postalCode;

    @Size(max = 100)
    String country;

    AddressLabel label;

    String note;

    Boolean isDefault;
}
