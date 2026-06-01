package com.unimag.ecomerce.api.dto;

import java.io.Serializable;

public class ReportDTO {

    //Nota 1: Productos mas vendidos por periodo
    public record BestSellingProductResponse(
            String productName,
            Long totalQuantitySold
    ) implements Serializable {}

    //Nota 2:Ingresos mensuales agrupados
    public record MonthlyIncomeResponse(
            Integer year,
            Integer month,
            Double totalIncome
    ) implements Serializable {}

    //Nota 3: Clientes con mayor facturacion

    public record TopCustomerResponse(
            Long customerId,
            String customerName,
            Double totalSpent
    ) implements Serializable{}

    //Nota 4: Productos con stock insuficiente
    public record LowStockProductResponse(
            Long productId,
            String productName,
            Integer availableStock,
            Integer minimumStock
    ) implements Serializable{}
}
