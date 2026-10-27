package com.scaler.productcatalogservice.services;

import com.scaler.productcatalogservice.dtos.ProductDto;
import com.scaler.productcatalogservice.mappers.ProductMapper;
import com.scaler.productcatalogservice.models.Category;
import com.scaler.productcatalogservice.models.Product;
import com.scaler.productcatalogservice.models.State;
import com.scaler.productcatalogservice.repositories.CategoryRepo;
import com.scaler.productcatalogservice.repositories.ProductRepo;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Primary
public class StorageProductService implements IProductService {

    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;
    private final ProductMapper productMapper;

    public StorageProductService(ProductRepo productRepo, CategoryRepo categoryRepo, ProductMapper productMapper) {
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
        this.productMapper = productMapper;
    }

    @Override
    public Product getProductById(Long id) {
        return productRepo.findByIdAndState(id, State.ACTIVE).orElse(null);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepo.findAllByState(State.ACTIVE);
    }

    @Override
    @Transactional
    public Product createProduct(Product input) {
        input.setId(null);
        input.setCategory(findOrCreateCategory(input.getCategory()));

        Date now = new Date();
        input.setCreatedAt(now);
        input.setLastUpdatedAt(now);
        return productRepo.save(input);
    }

    @Override
    @Transactional
    public Product replaceProduct(Product input, Long id) {
        Optional<Product> optionalProduct = productRepo.findByIdAndState(id, State.ACTIVE);
        if (optionalProduct.isEmpty()) {
            return null;
        }

        input.setId(id);
        input.setCategory(findOrCreateCategory(input.getCategory()));
        input.setCreatedAt(optionalProduct.get().getCreatedAt());
        input.setLastUpdatedAt(new Date());
        return productRepo.save(input);
    }

    // PATCH: only the fields present in the request body change. The generated id and createdAt never change.
    @Override
    @Transactional
    public Product updateProduct(ProductDto input, Long id) {
        Optional<Product> optionalProduct = productRepo.findByIdAndState(id, State.ACTIVE);
        if (optionalProduct.isEmpty()) {
            return null;
        }

        Product product = productMapper.applyPatch(optionalProduct.get(), input);
        if (input.getCategory() != null && input.getCategory().getName() != null) {
            product.setCategory(findOrCreateCategory(product.getCategory()));
        }
        product.setLastUpdatedAt(new Date());
        return productRepo.save(product);
    }

    @Override
    @Transactional
    public boolean deleteProduct(Long id) {
        Optional<Product> optionalProduct = productRepo.findByIdAndState(id, State.ACTIVE);
        if (optionalProduct.isEmpty()) {
            return false;
        }

        Product product = optionalProduct.get();
        product.setState(State.INACTIVE);
        product.setLastUpdatedAt(new Date());
        productRepo.save(product);
        return true;
    }

    @Override
    public Page<Product> searchProducts(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        if (minPrice != null && maxPrice != null) {
            return productRepo.findByPriceBetweenAndState(minPrice, maxPrice, State.ACTIVE, pageable);
        }
        return productRepo.findAllByState(State.ACTIVE, pageable);
    }

    private Category findOrCreateCategory(Category category) {
        if (category == null) {
            return null;
        }

        Optional<Category> existing = categoryRepo.findByName(category.getName());
        if (existing.isPresent()) {
            return existing.get();
        }

        Date now = new Date();
        category.setId(null);
        category.setCreatedAt(now);
        category.setLastUpdatedAt(now);
        return category;
    }
}
