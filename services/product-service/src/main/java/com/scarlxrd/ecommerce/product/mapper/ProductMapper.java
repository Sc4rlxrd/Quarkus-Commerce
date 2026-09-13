package com.scarlxrd.ecommerce.product.mapper;

import com.scarlxrd.ecommerce.contracts.product.request.CreateProductRequest;
import com.scarlxrd.ecommerce.contracts.product.response.ProductResponse;
import com.scarlxrd.ecommerce.product.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sellerId", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProductEntity toEntity(CreateProductRequest request);

    ProductResponse toResponse(ProductEntity product);
}