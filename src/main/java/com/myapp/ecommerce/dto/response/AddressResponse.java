package com.myapp.ecommerce.dto.response;

import com.myapp.ecommerce.entity.enums.AddressLabel;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
@JsonPropertyOrder(alphabetic = true)
public class AddressResponse {

    @JsonProperty("_id")
    String id;
    String userId;
    String fullName;
    String phone;
    String street;
    String ward;
    String district;
    String city;
    String postalCode;
    String country;
    AddressLabel label;
    String note;
    boolean isDefault;
}
