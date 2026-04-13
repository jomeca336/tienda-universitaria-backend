package com.unimag.ecomerce.api;

import com.unimag.ecomerce.services.CustomerService;
import com.unimag.ecomerce.api.dto.CustomerDTO.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Validated

public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CreateCustomerRequest request,
                                                    UriComponentsBuilder uriBuilder){

        var customerCreated = customerService.create(request);
        var location = uriBuilder.path("/api/v1/customers/{id}").buildAndExpand(customerCreated.id()).toUri();
        return ResponseEntity.created(location).body(customerCreated);
    }

    @GetMapping ("/{id}")
    public ResponseEntity<CustomerResponse> get(@PathVariable Long id){

        return ResponseEntity.ok(customerService.get(id));
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> list() {
        return ResponseEntity.ok(customerService.list());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody UpdateCustomerRequest request){
        return ResponseEntity.ok(customerService.update(id,request));
    }

}






