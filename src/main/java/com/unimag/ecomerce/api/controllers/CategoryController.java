package com.unimag.ecomerce.api.controllers;


import com.unimag.ecomerce.api.dto.CategoryDTO.*;
import com.unimag.ecomerce.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Validated
    public class CategoryController {

        private final CategoryService service;

            @PostMapping
            public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CreateCategoryRequest req,
                                                           UriComponentsBuilder uriBuilder){
                var categoryCreated = service.create(req);
                var location = uriBuilder
                        .path("/api/categories/{id}")
                        .buildAndExpand(categoryCreated.id())
                        .toUri();
                return ResponseEntity.created(location).body(categoryCreated);

            }

            @GetMapping
            public ResponseEntity<List<CategoryResponse>> list( ){
                    return ResponseEntity.ok(service.list());


            }
    }
