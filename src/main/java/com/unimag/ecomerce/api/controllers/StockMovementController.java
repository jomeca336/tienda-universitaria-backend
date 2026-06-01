package com.unimag.ecomerce.api.controllers;

import com.unimag.ecomerce.api.dto.StockMovementDTO;
import com.unimag.ecomerce.services.StockMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/stock-history")
@RequiredArgsConstructor
public class StockMovementController {

    private final StockMovementService service;

    @GetMapping
    public ResponseEntity<List<StockMovementDTO.StockMovementResponse>> list(@PathVariable Long productId) {
        return ResponseEntity.ok(service.getByProduct(productId));
    }
}
