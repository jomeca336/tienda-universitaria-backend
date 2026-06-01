package com.unimag.ecomerce.api.controllers;

import com.unimag.ecomerce.api.dto.CustomerDTO.*;
import com.unimag.ecomerce.api.dto.OrderDTO;
import com.unimag.ecomerce.services.CustomerService;
import com.unimag.ecomerce.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Validated
public class CustomerController {

    private final CustomerService service;
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CreateCustomerRequest req,
                                                   UriComponentsBuilder uriBuilder) {
        var created = service.create(req);
        var location = uriBuilder.path("/api/customers/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> list() {
        return ResponseEntity.ok(service.list());
    }

    @GetMapping("/deleted")
    public ResponseEntity<List<CustomerResponse>> listDeleted() {
        return ResponseEntity.ok(service.listDeleted());
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<CustomerResponse> restore(@PathVariable Long id) {
        return ResponseEntity.ok(service.restore(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody UpdateCustomerRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @GetMapping("/{id}/orders")
    public ResponseEntity<List<OrderDTO.OrderResponse>> getOrders(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getByCustomer(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
