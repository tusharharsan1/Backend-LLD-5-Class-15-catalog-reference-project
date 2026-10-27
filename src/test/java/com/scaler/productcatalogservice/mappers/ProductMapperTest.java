package com.scaler.productcatalogservice.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import com.scaler.productcatalogservice.dtos.CategoryDto;
import com.scaler.productcatalogservice.dtos.ProductDto;
import com.scaler.productcatalogservice.models.Category;
import com.scaler.productcatalogservice.models.Product;
import com.scaler.productcatalogservice.models.State;
import java.math.BigDecimal;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductMapperTest {

    private ProductMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProductMapper();
    }

    @Test
    @DisplayName("toDto copies the fields and the category")
    void toDto_product_copiesFieldsAndCategory() {
        Category category = new Category();
        category.setName("beauty");
        Product product = new Product();
        product.setId(1L);
        product.setName("Mascara");
        product.setPrice(new BigDecimal("9.99"));
        product.setCategory(category);

        ProductDto dto = mapper.toDto(product);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Mascara");
        assertThat(dto.getPrice()).isEqualTo(new BigDecimal("9.99"));
        assertThat(dto.getCategory().getName()).isEqualTo("beauty");
    }

    @Test
    void toDto_noCategory_keepsCategoryNull() {
        Product product = new Product();
        product.setName("Mascara");

        ProductDto dto = mapper.toDto(product);

        assertThat(dto.getCategory()).isNull();
    }

    @Test
    void toDto_nullProduct_returnsNull() {
        assertThat(mapper.toDto(null)).isNull();
    }

    @Test
    void toEntity_dto_copiesFieldsAndCategory() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("home");
        ProductDto dto = new ProductDto();
        dto.setName("Desk Lamp");
        dto.setPrice(new BigDecimal("499.50"));
        dto.setCategory(categoryDto);

        Product product = mapper.toEntity(dto);

        assertThat(product.getName()).isEqualTo("Desk Lamp");
        assertThat(product.getPrice()).isEqualTo(new BigDecimal("499.50"));
        assertThat(product.getCategory().getName()).isEqualTo("home");
    }

    @Test
    void applyPatch_nameOnly_changesNameAndKeepsTheRest() {
        Date createdAt = new Date(0);
        Category category = new Category();
        category.setName("beauty");
        Product existing = new Product();
        existing.setId(7L);
        existing.setName("Mascara");
        existing.setDescription("Volumising");
        existing.setPrice(new BigDecimal("9.99"));
        existing.setImageUrl("mascara.png");
        existing.setCategory(category);
        existing.setCreatedAt(createdAt);
        existing.setState(State.ACTIVE);

        ProductDto patch = new ProductDto();
        patch.setName("Lash Primer");
        patch.setId(999L);

        Product patched = mapper.applyPatch(existing, patch);

        assertThat(patched.getName()).isEqualTo("Lash Primer");
        assertThat(patched.getDescription()).isEqualTo("Volumising");
        assertThat(patched.getPrice()).isEqualTo(new BigDecimal("9.99"));
        assertThat(patched.getImageUrl()).isEqualTo("mascara.png");
        assertThat(patched.getCategory().getName()).isEqualTo("beauty");
        assertThat(patched.getId()).isEqualTo(7L);
        assertThat(patched.getCreatedAt()).isEqualTo(createdAt);
        assertThat(patched.getState()).isEqualTo(State.ACTIVE);
    }

    @Test
    void applyPatch_priceAndDescription_changesBoth() {
        Product existing = new Product();
        existing.setName("Mascara");
        existing.setDescription("Volumising");
        existing.setPrice(new BigDecimal("9.99"));

        ProductDto patch = new ProductDto();
        patch.setDescription("Waterproof");
        patch.setPrice(new BigDecimal("12.50"));

        Product patched = mapper.applyPatch(existing, patch);

        assertThat(patched.getDescription()).isEqualTo("Waterproof");
        assertThat(patched.getPrice()).isEqualTo(new BigDecimal("12.50"));
        assertThat(patched.getName()).isEqualTo("Mascara");
    }

    @Test
    void applyPatch_nullFields_doNotClearExistingValues() {
        Product existing = new Product();
        existing.setName("Mascara");
        existing.setDescription("Volumising");
        existing.setPrice(new BigDecimal("9.99"));
        existing.setImageUrl("mascara.png");

        Product patched = mapper.applyPatch(existing, new ProductDto());

        assertThat(patched.getName()).isEqualTo("Mascara");
        assertThat(patched.getDescription()).isEqualTo("Volumising");
        assertThat(patched.getPrice()).isEqualTo(new BigDecimal("9.99"));
        assertThat(patched.getImageUrl()).isEqualTo("mascara.png");
    }

    @Test
    void applyPatch_categorySent_replacesCategory() {
        Category existingCategory = new Category();
        existingCategory.setName("beauty");
        Product existing = new Product();
        existing.setName("Mascara");
        existing.setCategory(existingCategory);

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("grooming");
        ProductDto patch = new ProductDto();
        patch.setCategory(categoryDto);

        Product patched = mapper.applyPatch(existing, patch);

        assertThat(patched.getCategory().getName()).isEqualTo("grooming");
    }
}
