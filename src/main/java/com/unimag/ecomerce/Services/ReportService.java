package com.unimag.ecomerce.services;

import com.unimag.ecomerce.api.dto.ReportDTO;

import java.util.List;

public interface ReportService {

    List<ReportDTO.BestSellingProductResponse> getBestSellingProducts();
    List<ReportDTO.MonthlyIncomeResponse> getMonthlyIncome();
    List<ReportDTO.TopCustomerResponse> getTopCustomers();
    List<ReportDTO.LowStockProductResponse> getLowStockProducts();
}
