package com.unimag.ecomerce.Services;

import com.unimag.ecomerce.dto.ReportDTO;

import java.util.List;

public interface ReportService {

    List<ReportDTO.BestSellingProductResponse> getBestSellingProducts();
    List<ReportDTO.MonthlyIncomeResponse> getMonthlyIncome();
    List<ReportDTO.TopCustomerResponse> getTopCustomers();
    List<ReportDTO.LowStockProductResponse> getLowStockProducts();
}
