package com.scarlxrd.ecommerce.product.repository;

import com.scarlxrd.ecommerce.product.entity.ProductEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ProductRepository implements PanacheRepositoryBase<ProductEntity, UUID> {

    public Optional<ProductEntity> findBySku(String sku) {
        return find("lower(sku) = lower(?1)", sku)
                .firstResultOptional();
    }

    public boolean existsBySku(String sku) {
        return count("lower(sku) = lower(?1)", sku) > 0;
    }

    public Optional<ProductEntity> findActiveById(UUID id) {
        return find("id = ?1 and active = true", id)
                .firstResultOptional();
    }

    public List<ProductEntity> findAllActive() {
        return list("active = true");
    }
}