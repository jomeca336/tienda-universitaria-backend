package com.unimag.ecomerce.api.controllers;

import com.unimag.ecomerce.api.dto.InventoryDTO.*;
import com.unimag.ecomerce.api.dto.ProductDTO.*;
import com.unimag.ecomerce.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final ProductService service;

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest req,
                                                  UriComponentsBuilder uriBuilder) {
        var created = service.create(req);
        var location = uriBuilder.path("/api/products/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> list() {
        return ResponseEntity.ok(service.list());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody UpdateProductRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @PutMapping("/{id}/inventory")
    public ResponseEntity<ProductResponse> updateInventory(@PathVariable Long id,
                                                           @Valid @RequestBody UpdateInventoryRequest req) {
        return ResponseEntity.ok(service.updateInventory(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/deleted")
    public ResponseEntity<List<ProductResponse>> listDeleted() {
        return ResponseEntity.ok(service.listDeleted());
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<ProductResponse> restore(@PathVariable Long id) {
        return ResponseEntity.ok(service.restore(id));
    }
}
