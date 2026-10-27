package com.scaler.productcatalogservice.services;

import com.scaler.productcatalogservice.models.Category;
import com.scaler.productcatalogservice.models.State;
import com.scaler.productcatalogservice.repositories.CategoryRepo;
import com.scaler.productcatalogservice.repositories.ProductRepo;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;

    public CategoryService(ProductRepo productRepo, CategoryRepo categoryRepo) {
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
    }

    public Double getAveragePrice(String categoryName) {
        return productRepo.findAveragePriceInCategory(categoryName, State.ACTIVE);
    }

    public List<String> getCategoryReport() {
        List<String> result = new ArrayList<>();
        for (Category category : categoryRepo.findAllWithProducts()) {
            result.add(category.getName() + ": " + category.getProducts().size());
        }
        return result;
    }
}
