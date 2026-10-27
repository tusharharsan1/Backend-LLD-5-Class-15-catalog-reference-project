package com.scaler.productcatalogservice.repositories;

import com.scaler.productcatalogservice.models.Product;
import com.scaler.productcatalogservice.models.State;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {

    Optional<Product> findByIdAndState(Long id, State state);

    List<Product> findAllByState(State state);

    @EntityGraph(attributePaths = "category")
    Page<Product> findAllByState(State state, Pageable pageable);

    @EntityGraph(attributePaths = "category")
    Page<Product> findByPriceBetweenAndState(BigDecimal minPrice, BigDecimal maxPrice, State state, Pageable pageable);

    @Query("select avg(p.price) from Product p where p.category.name = :categoryName and p.state = :state")
    Double findAveragePriceInCategory(String categoryName, State state);
}
