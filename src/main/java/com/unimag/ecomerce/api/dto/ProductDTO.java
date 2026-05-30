package com.unimag.ecomerce.api.dto;

import java.io.Serializable;

public class ProductDTO {

    public record CreateProductRequest(
            String sku,
            String name,
            String description,
            Double price,
            Boolean active,
            Long categoryId

    ) implements Serializable{}

    public record UpdateProductRequest(
            String sku,
            String name,
            String description,
            Double price,
            Boolean active,
            Long categoryId

    ) implements Serializable{}

    public record ProductResponse(
            Long id,
            Long categoryId,
            String sku,
            String name,
            String description,
            Double price,
            Boolean active,
            Integer stock,
            Integer minStock

    ) implements Serializable {}
}
