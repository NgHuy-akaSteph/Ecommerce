package com.myapp.ecommerce.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "variant_options")
@SQLDelete(sql = "UPDATE variant_options SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class VariantOption extends BaseEntity {

    @Column(nullable = false, length = 100)
    String name;

    @Column(nullable = false, unique = true, length = 50)
    String code;

    @OneToMany(mappedBy = "option", fetch = FetchType.LAZY)
    List<VariantValue> values;
}
