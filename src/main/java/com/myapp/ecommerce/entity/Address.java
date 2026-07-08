package com.myapp.ecommerce.entity;

import com.myapp.ecommerce.entity.enums.AddressLabel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "addresses")
@SQLDelete(sql = "UPDATE addresses SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Address extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(name = "full_name")
    String fullName;

    String phone;

    String street;

    String ward;

    String district;

    String city;

    @Column(name = "postal_code")
    String postalCode;

    @Builder.Default
    String country = "Vietnam";

    @Enumerated(EnumType.STRING)
    AddressLabel label;

    String note;

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    boolean isDefault = false;
}
