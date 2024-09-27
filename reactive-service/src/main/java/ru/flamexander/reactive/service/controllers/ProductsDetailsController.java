package ru.flamexander.reactive.service.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.flamexander.reactive.service.dtos.ProductDetailedDto;
import ru.flamexander.reactive.service.dtos.ProductDetailsDto;
import ru.flamexander.reactive.service.entities.Product;
import ru.flamexander.reactive.service.services.ProductDetailsService;
import ru.flamexander.reactive.service.services.ProductsService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@RestController
@RequestMapping("/api/v1/detailed")
@RequiredArgsConstructor
@Slf4j
public class ProductsDetailsController {
    private final ProductsService productsService;
    private final ProductDetailsService productDetailsService;

    @GetMapping("/demo")
    public Flux<ProductDetailsDto> getManySlowProducts() {
        Mono<ProductDetailsDto> p1 = productDetailsService.getProductDetailsById(1L);
        Mono<ProductDetailsDto> p2 = productDetailsService.getProductDetailsById(2L);
        Mono<ProductDetailsDto> p3 = productDetailsService.getProductDetailsById(3L);
        return p1.mergeWith(p2).mergeWith(p3);
    }

    @GetMapping("/all")
    public Flux<ProductDetailedDto> getAllDetailedProducts() {
        Flux<Product> products = productsService.findAll();
        Flux<ProductDetailsDto> productDetails = productDetailsService.getAllProductDetails();
        return joinProductWithDetails(products, productDetails);
    }

    @GetMapping("/multiple/{ids}")
    public Flux<ProductDetailedDto> getMultipleDetailedProducts(@PathVariable Long[] ids) {
        List<Long> idsList = List.of(ids);
        Flux<Product> products = productsService.findAllById(idsList);
        Flux<ProductDetailsDto> productDetails = productDetailsService.getAllProductDetailsById(idsList);
        return joinProductWithDetails(products, productDetails);
    }

    @GetMapping("/{id}")
    public Mono<ProductDetailedDto> getDetailedProduct(@PathVariable Long id) {
        Mono<Product> product =  productsService.findById(id);
        Mono<ProductDetailsDto> productDetail = productDetailsService.getProductDetailsById(id);
        return product.flatMap(p ->
                productDetail
                        .switchIfEmpty(Mono.just(new ProductDetailsDto(p.getId(), null)))
                        .map(pd -> new ProductDetailedDto(p.getId(), p.getName(), pd.getDescription()))
        );
    }

    private Flux<ProductDetailedDto> joinProductWithDetails(Flux<Product> products,
                                                            Flux<ProductDetailsDto> productDetails) {
        Mono<Map<Long, ProductDetailsDto>> productsDetailsMap = productDetails.collectMap(ProductDetailsDto::getId, Function.identity());
        Mono<List<Product>> productsList = products.collectList();
        return Flux.zip(productsDetailsMap, productsList)
                .flatMap(t -> mergeProductWithDetails(t.getT1(), t.getT2()));
    }

    private Flux<ProductDetailedDto> mergeProductWithDetails(Map<Long, ProductDetailsDto> productsDetailsMap,
                                                             List<Product> products) {
        return Flux.fromIterable(products)
                .map(p -> new ProductDetailedDto(p.getId(),
                        p.getName(),
                        Optional.ofNullable(productsDetailsMap.get(p.getId())).map(ProductDetailsDto::getDescription).orElse(null)
                    )
                );
    }

}
