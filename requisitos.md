# Especificaciones Técnicas y Requisitos: API E-commerce Tienda Universitaria

## 1. Contexto del Proyecto
[cite_start]El objetivo es diseñar e implementar una API REST para la digitalización de la tienda institucional, gestionando productos (sudaderas, kits académicos, etc.), inventarios y pedidos[cite: 7, 9]. [cite_start]El sistema debe resolver problemas de sobreventa y falta de trazabilidad[cite: 8].

## 2. Stack Tecnológico Obligatorio
* [cite_start]**Lenguaje:** Java 21[cite: 4, 13].
* [cite_start]**Framework:** Spring Boot 4[cite: 4, 13].
* [cite_start]**Base de Datos:** PostgreSQL[cite: 4, 14].
* [cite_start]**Pruebas:** JUnit 5, Mockito y Testcontainers (para base de datos real en integración)[cite: 4, 14, 91].
* [cite_start]**Mapeo:** MapStruct (para aislamiento de entidades con DTOs)[cite: 90].

## 3. Entidades y Reglas de Negocio (Capa Service)
### Productos e Inventario
* [cite_start]Cada producto debe tener un SKU único y pertenecer a una categoría existente[cite: 39, 40].
* [cite_start]El precio debe ser mayor a cero[cite: 41].
* [cite_start]El stock disponible y mínimo no pueden ser negativos[cite: 42].

### Pedidos (Orders)
* [cite_start]**Cálculos:** El subtotal de cada ítem es `cantidad * precioUnitario`, y el total del pedido es la suma de los subtotales[cite: 51, 52].
* [cite_start]**Estado Inicial:** Todo pedido nuevo debe crearse en estado `CREATED`[cite: 54].
* [cite_start]**Pago y Stock:** Solo pedidos en `CREATED` pueden pasar a `PAID`[cite: 56]. [cite_start]Al pagar, se debe validar el stock y descontar el inventario disponible[cite: 57, 59].
* [cite_start]**Cancelación:** Si se cancela un pedido `PAID`, se debe revertir el stock al inventario[cite: 68].

## 4. Estructura de Clases Obligatorias (Excluyendo Controllers)

### Entidades y Enums
[cite_start]`Customer`, `Address`, `Category`, `Product`, `Inventory`, `Order`, `OrderItem`, `OrderStatusHistory`, `OrderStatus` (Enum), `CustomerStatus` (Enum)[cite: 90].

### Repositorios (Repositories)
[cite_start]`CustomerRepository`, `AddressRepository`, `CategoryRepository`, `ProductRepository`, `InventoryRepository`, `OrderRepository`, `OrderItemRepository`, `OrderStatusHistoryRepository`[cite: 90].

### DTOs (Data Transfer Objects)
* [cite_start]**Requests:** `CreateCustomerRequest`, `UpdateCustomerRequest`, `CreateAddressRequest`, `CreateCategoryRequest`, `CreateProductRequest`, `UpdateProductRequest`, `UpdateInventoryRequest`, `CreateOrderRequest`, `CreateOrderItemRequest`, `CancelOrderRequest`[cite: 90].
* [cite_start]**Responses:** `CustomerResponse`, `AddressResponse`, `CategoryResponse`, `ProductResponse`, `InventoryResponse`, `OrderItemResponse`, `OrderResponse`, `BestSellingProductResponse`, `MonthlyIncomeResponse`, `TopCustomerResponse`, `LowStockProductResponse`[cite: 90].

### Mappers e Infraestructura
[cite_start]`CustomerMapper`, `AddressMapper`, `CategoryMapper`, `ProductMapper`, `OrderMapper`, `GlobalExceptionHandler`[cite: 90].

### Servicios (Interfaces e Implementaciones)
[cite_start]`CustomerService`, `AddressService`, `CategoryService`, `ProductService`, `InventoryService`, `OrderService`, `ReportService`[cite: 90].

## 5. Plan de Pruebas Obligatorio (Excluyendo Controllers)

### Pruebas de Integración (Repository con Testcontainers)
* [cite_start]**Clases:** `ProductRepositoryIntegrationTest`, `InventoryRepositoryIntegrationTest`, `OrderRepositoryIntegrationTest`, `CustomerRepositoryIntegrationTest`[cite: 92].
* [cite_start]**Alcance:** Búsqueda por SKU, productos con bajo stock, pedidos por filtros compuestos (cliente, fecha, estado), y reportes de agregación (ingresos mensuales, productos más vendidos)[cite: 92].

### Pruebas Unitarias (Service con Mockito)
* [cite_start]**Clases:** `OrderServiceImplTest`, `InventoryServiceImplTest`, `ProductServiceImplTest`[cite: 92].
* [cite_start]**Alcance:** Validación de stock insuficiente, cálculo automático de totales, transiciones de estado de pedidos, y descuento/reversión de inventario[cite: 92].