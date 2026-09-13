package com.scarlxrd.ecommerce.product.messaging;

import com.scarlxrd.ecommerce.contracts.product.event.ProductValidatedEvent;
import com.scarlxrd.ecommerce.contracts.product.event.ProductValidationRequest;
import com.scarlxrd.ecommerce.product.service.ProductService;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ProductValidationConsumer {

    private static final Logger log = Logger.getLogger(ProductValidationConsumer.class);

    private final ProductService productService;

    public ProductValidationConsumer(ProductService productService) {
        this.productService = productService;
    }

    @Incoming("product-validation")
    @Outgoing("product-validated")
    @Blocking
    public ProductValidatedEvent consume(ProductValidationRequest request) {
        log.infof("Validating product productId=%s orderId=%s quantity=%d",
                request.productId(),
                request.orderId(),
                request.quantity());

        ProductValidatedEvent result = productService.validate(request);

        log.infof("Product validation result productId=%s available=%s reason=%s",
                result.productId(),
                result.available(),
                result.reason());

        return result;
    }
}