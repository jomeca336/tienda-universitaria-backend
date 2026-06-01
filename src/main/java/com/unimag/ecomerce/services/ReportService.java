package com.unimag.ecomerce.services;

import com.unimag.ecomerce.api.dto.ReportDTO.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ReportService {

    List<BestSellingProductResponse> getBestSellingProducts();
    List<MonthlyIncomeResponse> getMonthlyIncome();
    List<TopCustomerResponse> getTopCustomers();
    List<LowStockProductResponse> getLowStockProducts();
}
