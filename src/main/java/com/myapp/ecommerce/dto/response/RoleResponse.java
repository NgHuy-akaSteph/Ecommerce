package com.myapp.ecommerce.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.myapp.ecommerce.entity.Permission;
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
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@JsonPropertyOrder(alphabetic = true)
public class RoleResponse {
    @JsonProperty("_id")
    String id;
    String name;
    boolean active;
    String description;
    List<Permission> permissions;
}
