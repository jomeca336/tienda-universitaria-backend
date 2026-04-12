package com.unimag.ecomerce.services;

import com.unimag.ecomerce.api.dto.ReportDTO;
import com.unimag.ecomerce.domine.entities.Customer;
import com.unimag.ecomerce.domine.entities.Product;
import com.unimag.ecomerce.domine.repositories.CustomerRepository;
import com.unimag.ecomerce.domine.repositories.OrderItemRepository;
import com.unimag.ecomerce.domine.repositories.OrderRepository;
import com.unimag.ecomerce.domine.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    @Override
    public List<ReportDTO.BestSellingProductResponse> getBestSellingProducts() {
        return orderItemRepository.findTopSellingProducts().stream()
                .map(row -> new ReportDTO.BestSellingProductResponse(
                        ((Product) row[0]).getName(),
                        (Long) row[1]
                ))
                .toList();
    }

    @Override
    public List<ReportDTO.MonthlyIncomeResponse> getMonthlyIncome() {
        return orderRepository.getMonthlyIncome().stream()
                .map(row -> {
                    LocalDateTime date = ((Timestamp) row[0]).toLocalDateTime();
                    return new ReportDTO.MonthlyIncomeResponse(
                            date.getYear(),
                            date.getMonthValue(),
                            ((Number) row[1]).doubleValue()
                    );
                })
                .toList();
    }

    @Override
    public List<ReportDTO.TopCustomerResponse> getTopCustomers() {
        return customerRepository.findTopCustomers().stream()
                .map(row -> new ReportDTO.TopCustomerResponse(
                        ((Customer) row[0]).getId(),
                        ((Customer) row[0]).getName(),
                        ((Number) row[1]).doubleValue()
                ))
                .toList();
    }

    @Override
    public List<ReportDTO.LowStockProductResponse> getLowStockProducts() {
        return productRepository.findProductsWithLowStock().stream()
                .map(p -> new ReportDTO.LowStockProductResponse(
                        p.getId(),
                        p.getName(),
                        p.getInventory().getStock(),
                        p.getInventory().getMinStock()
                ))
                .toList();
    }
}
