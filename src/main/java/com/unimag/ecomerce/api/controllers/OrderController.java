package com.unimag.ecomerce.api.controllers;


import com.unimag.ecomerce.api.dto.OrderDTO.*;
import com.unimag.ecomerce.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;



@RestController
@RequestMapping("/api/Order")
@RequiredArgsConstructor
@Validated
public class OrderController {

    private final OrderService service;

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest req,
                                                UriComponentsBuilder uriBuilder){
        var created = service.create(req);
        var location = uriBuilder.path("/api/Order/{id}").buildAndExpand(created.id()).toUri();

                return ResponseEntity.created(location).body(created);
    }
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> get(@PathVariable Long id){
        return ResponseEntity.ok(service.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> list(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size ){
        var result = service.list(PageRequest.of(page, size, Sort.by("id").ascending()));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/pay")
    public ResponseEntity<OrderResponse> pay(@PathVariable Long id){
        return ResponseEntity.ok(service.pay(id));
    }

    @GetMapping("/{id}/ship")
    public ResponseEntity<OrderResponse> ship(@PathVariable Long id){
        return ResponseEntity.ok(service.ship(id));
    }

    @GetMapping("/{id}/deliver")
    public ResponseEntity<OrderResponse> deliver(@PathVariable Long id){
        return ResponseEntity.ok(service.deliver(id));
    }

    @GetMapping("{id}/cancel")
    public ResponseEntity<OrderResponse> cancel(@PathVariable Long id){
        return ResponseEntity.ok(service.cancel(id));
    }

}
