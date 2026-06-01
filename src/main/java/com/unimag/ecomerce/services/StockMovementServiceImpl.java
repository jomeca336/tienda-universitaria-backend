package com.unimag.ecomerce.services;

import com.unimag.ecomerce.api.dto.StockMovementDTO;
import com.unimag.ecomerce.domine.entities.Product;
import com.unimag.ecomerce.domine.entities.StockMovement;
import com.unimag.ecomerce.domine.repositories.StockMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StockMovementServiceImpl implements StockMovementService {

    private final StockMovementRepository repository;

    @Override
    public void record(Product product, int quantity, String type, int stockAfter) {
        repository.save(StockMovement.builder()
                .product(product)
                .quantity(quantity)
                .type(type)
                .stockAfter(stockAfter)
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockMovementDTO.StockMovementResponse> getByProduct(Long productId) {
        return repository.findByProductIdOrderByDateDesc(productId).stream()
                .map(m -> new StockMovementDTO.StockMovementResponse(
                        m.getId(),
                        m.getProduct().getId(),
                        m.getProduct().getName(),
                        m.getQuantity(),
                        m.getType(),
                        m.getStockAfter(),
                        m.getDate()
                ))
                .toList();
    }
}
