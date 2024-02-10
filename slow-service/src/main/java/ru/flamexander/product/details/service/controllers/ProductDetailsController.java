package ru.flamexander.product.details.service.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.flamexander.product.details.service.dtos.ProductDetailsDto;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/details")
public class ProductDetailsController {

    @GetMapping("/all")
    public ResponseEntity<List<ProductDetailsDto>> getAllProductDetails() throws InterruptedException {
        List<ProductDetailsDto> list = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            ProductDetailsDto productDetailsDto = createStubProductDetailsDto((long) i);
            if (productDetailsDto != null) {
                list.add(productDetailsDto);
            }
        }

        Thread.sleep(2500 + (int)(Math.random() * 2500));
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/multiple/{ids}")
    public ResponseEntity<List<ProductDetailsDto>> getMultipleProductDetails(@PathVariable Long[] ids)
            throws InterruptedException {
        List<ProductDetailsDto> list = new ArrayList<>();
        for (int i = 0; i < ids.length; i++) {
            Long id = ids[i];
            ProductDetailsDto productDetailsDto = createStubProductDetailsDto(id);
            if (productDetailsDto != null) {
                list.add(productDetailsDto);
            }
        }

        Thread.sleep(2500 + (int)(Math.random() * 2500));
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailsDto> getProductDetailsById(@PathVariable Long id)
            throws InterruptedException {
        ProductDetailsDto productDetailsDto = createStubProductDetailsDto(id);

        if (productDetailsDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Thread.sleep(2500 + (int)(Math.random() * 2500));
        return new ResponseEntity<>(productDetailsDto, HttpStatus.OK);
    }


    private ProductDetailsDto createStubProductDetailsDto(Long id) {
        if (id > 100) {
            return null;
        }
        return new ProductDetailsDto(id, String.format("Product %d description...", id));
    }
}
