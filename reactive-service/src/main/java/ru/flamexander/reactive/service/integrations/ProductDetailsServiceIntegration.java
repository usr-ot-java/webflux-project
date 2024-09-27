package ru.flamexander.reactive.service.integrations;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.flamexander.reactive.service.dtos.ProductDetailsDto;
import ru.flamexander.reactive.service.exceptions.AppException;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductDetailsServiceIntegration {
    private static final Long TIMEOUT_IN_MILLIS = 500L;
    private static final Logger logger = LoggerFactory.getLogger(ProductDetailsServiceIntegration.class.getName());

    private final WebClient productDetailsServiceWebClient;

    public Mono<ProductDetailsDto> getProductDetailsById(Long id) {
        logger.info("SEND REQUEST FOR PRODUCT_DETAILS-ID: {}", id);
        return productDetailsServiceWebClient.get()
                .uri("/api/v1/details/{id}", id)
                .retrieve()
                .onStatus(
                        httpStatus -> httpStatus.NOT_FOUND.equals(httpStatus),
                        clientResponse -> Mono.empty()
                )
                .onStatus(
                        httpStatus -> httpStatus.is5xxServerError(),
                        clientResponse -> Mono.error(new AppException("PRODUCT_DETAILS_SERVICE_INTEGRATION_ERROR"))
                )
                .bodyToMono(ProductDetailsDto.class)
                .timeout(Duration.ofMillis(TIMEOUT_IN_MILLIS))
                .log();
    }

    public Flux<ProductDetailsDto> getAllProductDetailsById(List<Long> ids) {
        logger.info("SEND REQUEST FOR PRODUCT_DETAILS-IDS: {}", ids);
        String idsParam = ids.stream().map(Object::toString).collect(Collectors.joining(","));
        return productDetailsServiceWebClient.get()
                .uri("/api/v1/details/multiple/{ids}", idsParam)
                .retrieve()
                .onStatus(
                        httpStatus -> httpStatus.is5xxServerError(),
                        clientResponse -> Mono.error(new AppException("PRODUCT_DETAILS_SERVICE_INTEGRATION_ERROR"))
                )
                .bodyToFlux(ProductDetailsDto.class)
                .timeout(Duration.ofMillis(TIMEOUT_IN_MILLIS))
                .log();
    }

    public Flux<ProductDetailsDto> getAllProductDetails() {
        logger.info("SEND REQUEST FOR PRODUCT_DETAILS-ALL");
        return productDetailsServiceWebClient.get()
                .uri("/api/v1/details/all")
                .retrieve()
                .onStatus(
                        httpStatus -> httpStatus.is5xxServerError(),
                        clientResponse -> Mono.error(new AppException("PRODUCT_DETAILS_SERVICE_INTEGRATION_ERROR"))
                )
                .bodyToFlux(ProductDetailsDto.class)
                .timeout(Duration.ofMillis(TIMEOUT_IN_MILLIS))
                .log();
    }
}
