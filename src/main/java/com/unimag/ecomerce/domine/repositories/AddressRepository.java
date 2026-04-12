package com.unimag.ecomerce.domine.repositories;

import com.unimag.ecomerce.domine.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
