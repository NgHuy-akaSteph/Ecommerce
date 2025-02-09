package com.myapp.ecommerce.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "permissions")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @NotBlank(message = "Name is mandatory")
    String name;

    @NotBlank(message = "API Path is mandatory")
    String apiPath;

    @NotBlank(message = "Method is mandatory")
    String method;

    @NotBlank(message = "Module is mandatory")
    String module;

    boolean active;

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    @JsonIgnore
    List<Role> roles;

    public Permission(@NotBlank(message = "Name is mandatory") String name,
                      @NotBlank(message = "API Path is mandatory") String apiPath,
                      @NotBlank(message = "Method is mandatory") String method,
                      @NotBlank(message = "Module is mandatory") String module, boolean active) {
        this.name = name;
        this.apiPath = apiPath;
        this.method = method;
        this.module = module;
        this.active = active;
    }
}
