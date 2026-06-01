package com.unimag.ecomerce.domine.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "stock_movements")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private String type; // ENTRADA, VENTA, CANCELACION

    @Column(nullable = false)
    private Integer stockAfter;

    @Builder.Default
    @Column(nullable = false)
    private Instant date = Instant.now();
}
