package com.scaler.productcatalogservice.controllers;

import com.scaler.productcatalogservice.services.CategoryService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories/{name}/average-price")
    public ResponseEntity<Double> getAveragePrice(@PathVariable("name") String name) {
        Double average = categoryService.getAveragePrice(name);
        if (average == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(average);
    }

    @GetMapping("/categories")
    public List<String> getCategories() {
        return categoryService.getCategoryReport();
    }
}
