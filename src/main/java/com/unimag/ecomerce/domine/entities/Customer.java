package com.unimag.ecomerce.domine.entities;

import com.unimag.ecomerce.enums.CustomerStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "customers")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter

public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    String name;
    String email;
    CustomerStatus status;

    @Builder.Default
    @Column(columnDefinition = "boolean not null default false")
    private Boolean deleted = false;

    @OneToMany
    List <Address> addresses;

}
