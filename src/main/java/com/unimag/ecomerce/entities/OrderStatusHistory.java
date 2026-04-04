package com.unimag.ecomerce.entities;

import com.unimag.ecomerce.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "order_status_history")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class
OrderStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "previous_status")
    private OrderStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "new_status")
    private OrderStatus newStatus;

    @JoinColumn(name = "change_date")
    private Instant changeDate;
}
