package com.myapp.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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

import java.math.BigDecimal;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name="products")
@SQLDelete(sql = "UPDATE products SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Product extends BaseEntity {

    String name;
    String shortDescription;
    String thumbnail;
    @ElementCollection
    @CollectionTable(name = "products_sliders", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "sliders")
    List<String> sliders;
    BigDecimal price;
    long quantity;
    BigDecimal discount;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_tags",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    List<Tag> tags;

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    List<OrderDetail> orderDetails;

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    List<CartDetail> cartDetails;

}
