package com.scaler.productcatalogservice.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(indexes = @Index(name = "idx_product_name", columnList = "name"))
public class Product extends BaseModel {
    private String name;
    private String description;
    @Column(precision = 19, scale = 2)
    private BigDecimal price;
    private String imageUrl;
    @ManyToOne(cascade = CascadeType.ALL)
    private Category category;
}
