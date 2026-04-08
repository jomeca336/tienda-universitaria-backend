# Proyecto E-Commerce (Spring Boot + Testcontainers)

Este proyecto es una API y sistema de gestión en Spring Boot para un E-Commerce, con una capa de persistencia sólida usando Spring Data JPA y pruebas de integración automatizadas habilitadas a través de Testcontainers y PostgreSQL.

## 📚 1. Entidades del Dominio

El dominio del sistema abarca fundamentalmente compras, clientes y control de stock, gestionado con JPA:

- **`Product` y `Category`**: Productos de venta atados a categorías descriptivas.
- **`Inventory`**: Relación 1:1 con un producto; maneja el `stock` actual frente a un `minStock`.
- **`Customer` y `Address`**: Administración de clientes y sus múltiples direcciones mediante relación 1 a N.
- **`Order` y `OrderItem`**: Representan las órdenes de compra. Una orden pertenece a un cliente, es despachada a una dirección, cuenta con un histórico de estados, y agrupa en items la metadata de los productos adquiridos.
- **`OrderStatusHistory`**: Registra los cambios de estado (e.g. `PENDING`, `PAID`, `SHIPPED`, `CANCELLED`) a lo largo del tiempo.

Todas estas clases utilizan dependencias de **Lombok** (`@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`) para una infraestructura limpia y escalable.

---

## 🛠️ 2. Capa de Repositorios

El acceso a la base de datos es logrado mediante interfaces de **Spring Data JPA** (`JpaRepository`):

- **`ProductRepository`**: Incluye métodos clave para buscar bajo disponibilidad de la categoría (`findByActiveTrueAndCategoryId()`) y con la verificación directa de los mínimos (`findProductsWithLowStock()`).
- **`CustomerRepository`**: Consulta de agregación usando JPQL para rankear clientes (`findTopCustomers()`).
- **`OrderRepository`**: Repositorio central de gestión capaz de filtrar transacciones por clientes, rangos de costo y fecha simultáneamente (`findOrdersByFilters()`) o agrupar y visualizar sumas de ingresos agrupadas por temporalidad (`getMonthlyIncome()`).
- **`OrderItemRepository`**: Añade capacidades informativas para identificar productos líderes en mercado mediante la suma global de su factor de cantidad (`findTopSellingProducts()`).
- **Otros repositorios (`AddressRepository`, `InventoryRepository`, `CategoryRepository`, `OrderStatusHistoryRepository`)**: Brindan la lógica CRUD estándar.

---

## 🧪 3. Pruebas de Integración (Integration Tests)

Se incluyó una suite estructurada de tests robustos bajo ambiente controlado sobre la persistencia usando **Testcontainers con PostgreSQL**, abarcando flujos exigentes por asignación:

* La clase central **`AbstractRepositoryIT`** administra la orquestación, logrando el arranque (y posterior caching) de una sola instancia compartida (`postgres:16-alpine`) para la JVM bajo el perfil "test". De esta forma la suite es sumamente ágil y se eliminan caídas erráticas de Connection Pools.

Y fueron implementados con éxito:

- **`CustomerRepositoryIntegrationTest`**: Valida a través del historial de los `Order`s atados si la cuantificación y rankeo del Query `findTopCustomers` organiza contundentemente al cliente de *Mayor Facturación* de primero.
- **`InventoryRepositoryIntegrationTest`**: Fija manualmente variables que provocan faltante de stock en productos e indaga que las consultas alerten correctamente los catálogos en riesgo.
- **`OrderRepositoryIntegrationTest`**: Demuestra de un solo despliegue una cadena compleja que verifica filtros cruzados sobre pedidos, el cálculo numérico por meses pagados y la fiabilidad de venta y rotación de items (`findTopSellingProducts`).
- **`ProductRepositoryIntegrationTest`**: Persiste productos que intencionalmente se asignan a clasificaciones deshabilitadas para revisar la pulcritud con la que excluye de la búsqueda principal según categoría.

