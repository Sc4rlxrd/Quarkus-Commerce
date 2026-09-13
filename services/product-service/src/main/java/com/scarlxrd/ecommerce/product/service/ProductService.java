package com.scarlxrd.ecommerce.product.service;

import com.scarlxrd.ecommerce.contracts.product.event.ProductValidatedEvent;
import com.scarlxrd.ecommerce.contracts.product.event.ProductValidationRequest;
import com.scarlxrd.ecommerce.contracts.product.request.CreateProductRequest;
import com.scarlxrd.ecommerce.contracts.product.request.UpdateProductRequest;
import com.scarlxrd.ecommerce.contracts.product.response.ProductResponse;
import com.scarlxrd.ecommerce.product.entity.ProductEntity;
import com.scarlxrd.ecommerce.product.mapper.ProductMapper;
import com.scarlxrd.ecommerce.product.repository.ProductRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ProductService {

    private static final Logger log =  Logger.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Transactional
    public ProductResponse create(CreateProductRequest request, UUID sellerId) {
        log.infof("Creating product sku=%s sellerId=%s", request.sku(), sellerId);

        if (productRepository.existsBySku(request.sku())) {
            log.warnf("Product creation rejected because SKU already exists sku=%s", request.sku());

            throw new IllegalArgumentException("SKU already exists");
        }

        ProductEntity product = productMapper.toEntity(request);

        product.sellerId = sellerId;
        product.active = true;

        productRepository.persist(product);

        log.infof("Product created successfully id=%s sku=%s sellerId=%s", product.id, product.sku, product.sellerId);

        return productMapper.toResponse(product);
    }

    public ProductResponse findById(UUID id) {
        log.debugf("Searching active product id=%s", id);

        ProductEntity product = productRepository.findActiveById(id).orElseThrow(() -> {
            log.warnf("Active product not found id=%s", id);

            return new IllegalArgumentException("Product not found");
        });

        log.debugf("Active product found id=%s sku=%s", product.id, product.sku);

        return productMapper.toResponse(product);
    }

    public List<ProductResponse> findAll() {
        log.debug("Listing active products");

        List<ProductResponse> products = productRepository.listAll().stream().filter(product -> product.active).map(productMapper::toResponse).toList();

        log.debugf("Active products listed count=%d", products.size());

        return products;
    }

    @Transactional
    public ProductResponse update(UUID id, UpdateProductRequest request, UUID sellerId, boolean admin) {
        log.infof("Updating product id=%s sellerId=%s admin=%s", id, sellerId, admin);

        ProductEntity product = productRepository.findByIdOptional(id).orElseThrow(() -> {
            log.warnf("Product update failed because product was not found id=%s", id);

            return new IllegalArgumentException("Product not found");
        });

        validateOwnership(product, sellerId, admin);

        if (request.name() != null) {
            product.name = request.name();
        }

        if (request.description() != null) {
            product.description = request.description();
        }

        if (request.sku() != null && !request.sku().equalsIgnoreCase(product.sku)) {

            if (productRepository.existsBySku(request.sku())) {
                log.warnf("Product update rejected because SKU already exists id=%s sku=%s", id, request.sku());

                throw new IllegalArgumentException("SKU already exists");
            }

            product.sku = request.sku();
        }

        if (request.price() != null) {
            product.price = request.price();
        }

        if (request.stock() != null) {
            product.stock = request.stock();
        }

        if (request.category() != null) {
            product.category = request.category();
        }

        if (request.active() != null) {
            product.active = request.active();
        }

        log.infof("Product updated successfully id=%s sku=%s", product.id, product.sku);

        return productMapper.toResponse(product);
    }

    @Transactional
    public void deactivate(UUID id, UUID sellerId, boolean admin) {
        log.infof("Deactivating product id=%s sellerId=%s admin=%s", id, sellerId, admin);

        ProductEntity product = productRepository.findByIdOptional(id).orElseThrow(() -> {
            log.warnf("Product deactivation failed because product was not found id=%s", id);

            return new IllegalArgumentException("Product not found");
        });

        validateOwnership(product, sellerId, admin);

        product.active = false;

        log.infof("Product deactivated successfully id=%s", product.id);
    }

    public ProductValidatedEvent validate(ProductValidationRequest request) {
        log.infof("Validating product for order orderId=%s productId=%s quantity=%d", request.orderId(), request.productId(), request.quantity());

        return productRepository.findByIdOptional(request.productId()).map(product -> {

            if (!product.active) {
                log.warnf("Product validation failed because product is inactive orderId=%s productId=%s", request.orderId(), request.productId());

                return new ProductValidatedEvent(request.orderId(), request.productId(), request.quantity(), product.price, false, "PRODUCT_INACTIVE");
            }

            if (product.stock < request.quantity()) {
                log.warnf("Product validation failed because stock is insufficient orderId=%s productId=%s requested=%d available=%d", request.orderId(), request.productId(), request.quantity(), product.stock);

                return new ProductValidatedEvent(request.orderId(), request.productId(), request.quantity(), product.price, false, "INSUFFICIENT_STOCK");
            }

            log.infof("Product validated successfully orderId=%s productId=%s quantity=%d", request.orderId(), request.productId(), request.quantity());

            return new ProductValidatedEvent(request.orderId(), request.productId(), request.quantity(), product.price, true, "AVAILABLE");
        }).orElseGet(() -> {
            log.warnf("Product validation failed because product was not found orderId=%s productId=%s", request.orderId(), request.productId());

            return new ProductValidatedEvent(request.orderId(), request.productId(), request.quantity(), null, false, "PRODUCT_NOT_FOUND");
        });
    }

    private void validateOwnership(ProductEntity product, UUID sellerId, boolean admin) {
        if (admin) {
            log.debugf("Ownership validation bypassed for admin productId=%s sellerId=%s", product.id, sellerId);

            return;
        }

        if (!product.sellerId.equals(sellerId)) {
            log.warnf("Product ownership validation failed productId=%s ownerId=%s requesterId=%s", product.id, product.sellerId, sellerId);

            throw new SecurityException("Seller does not own this product");
        }

        log.debugf("Product ownership validated productId=%s sellerId=%s", product.id, sellerId);
    }
}