package com.unimag.ecomerce.dto;

import java.io.Serializable;

public class CategoryDTO {

    public record CreateCategoryRequest(
            String name
    ) implements Serializable {}

    public record CategoryResponse(
            long id,
            String name
    ) implements Serializable {}

}
