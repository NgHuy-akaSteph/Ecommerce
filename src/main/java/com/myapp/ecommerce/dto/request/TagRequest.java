package com.myapp.ecommerce.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request payload for creating or updating a tag.")
public class TagRequest {

    @Schema(description = "Tag name; must be at least 3 characters.")
    @Size(min = 3, message= "INVALID_TAG_NAME")
    String name;

    @Schema(description = "Optional human-readable description for the tag.")
    String description;

}
