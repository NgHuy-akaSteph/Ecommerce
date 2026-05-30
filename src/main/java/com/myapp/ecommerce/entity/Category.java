package com.myapp.ecommerce.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
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
@Table(name="categories")
@SQLDelete(sql = "UPDATE categories SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Category extends BaseEntity {

    @Column(unique = true)
    String name;

    @OneToMany(mappedBy = "category")
    List<Product> products;

}
