package com.scaler.productcatalogservice.services;

import com.scaler.productcatalogservice.dtos.ProductDto;
import com.scaler.productcatalogservice.models.Product;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IProductService {

    Product getProductById(Long id);

    List<Product> getAllProducts();

    Product createProduct(Product input);

    Product replaceProduct(Product input, Long id);

    Product updateProduct(ProductDto input, Long id);

    boolean deleteProduct(Long id);

    Page<Product> searchProducts(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
}
