package ru.flamexander.reactive.service.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.flamexander.reactive.service.dtos.ProductDetailsDto;
import ru.flamexander.reactive.service.integrations.ProductDetailsServiceIntegration;

import java.util.List;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class ProductDetailsService {
    private final ProductDetailsServiceIntegration productDetailsServiceIntegration;

    public Mono<ProductDetailsDto> getProductDetailsById(Long id) {
        return productDetailsServiceIntegration.getProductDetailsById(id)
                .onErrorComplete(TimeoutException.class);
    }

    public Flux<ProductDetailsDto> getAllProductDetailsById(List<Long> ids) {
        return productDetailsServiceIntegration.getAllProductDetailsById(ids)
                .onErrorComplete(TimeoutException.class);
    }

    public Flux<ProductDetailsDto> getAllProductDetails() {
        return productDetailsServiceIntegration.getAllProductDetails()
                .onErrorComplete(TimeoutException.class);
    }
}
