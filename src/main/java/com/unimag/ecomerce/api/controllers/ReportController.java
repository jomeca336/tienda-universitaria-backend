package com.unimag.ecomerce.api.controllers;

import com.unimag.ecomerce.api.dto.ReportDTO.*;
import com.unimag.ecomerce.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
@Validated
public class ReportController {
    private final ReportService service;

    @GetMapping("/best-selling-products")
    public ResponseEntity<List<BestSellingProductResponse>> bestSellingProducts(){
        return ResponseEntity.ok(service.getBestSellingProducts());
    }

    @GetMapping("/monthly-income")
    public ResponseEntity<List<MonthlyIncomeResponse>> monthlyIncome(){
        return ResponseEntity.ok(service.getMonthlyIncome());
    }

    @GetMapping("/top-customers")
    public ResponseEntity<List<TopCustomerResponse>> topCustomers(){
        return ResponseEntity.ok(service.getTopCustomers());
    }

    @GetMapping("/low-stock-products")
    public ResponseEntity<List<LowStockProductResponse>> lowStockProducts(){
        return ResponseEntity.ok(service.getLowStockProducts());
    }

}
