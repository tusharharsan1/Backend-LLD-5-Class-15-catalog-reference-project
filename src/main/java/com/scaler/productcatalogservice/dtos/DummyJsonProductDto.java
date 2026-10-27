package com.scaler.productcatalogservice.dtos;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DummyJsonProductDto {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private String category;
    private String thumbnail;
}
