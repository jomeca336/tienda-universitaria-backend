package com.unimag.ecomerce.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "addresses")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    String addressline;
    String city;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    Customer customer;
}
