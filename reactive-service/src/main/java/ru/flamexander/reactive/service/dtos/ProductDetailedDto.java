package ru.flamexander.reactive.service.dtos;

import lombok.Data;

@Data
public class ProductDetailedDto {
    private final Long id;
    private final String name;
    private final String description;
}
