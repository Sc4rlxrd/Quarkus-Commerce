package com.scarlxrd.ecommerce.auth.repository;

import com.scarlxrd.ecommerce.auth.entity.UserEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserRepository implements PanacheRepository<UserEntity> {

    public Optional<UserEntity> findByEmail(String email) {
        return find("lower(email) = lower(?1)", email).firstResultOptional();
    }

    public boolean existsByEmail(String email) {
        return count("lower(email) = lower(?1)", email) > 0;
    }

    public Optional<UserEntity> findByIdOptional(UUID id) {
        return find("id", id).firstResultOptional();
    }
}
