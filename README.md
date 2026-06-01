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

---

## 📦 4. DTOs (Data Transfer Objects)

Los DTOs aíslan la capa de transporte de las entidades JPA. Se implementan como **Java Records** (`implements Serializable`) agrupados por clase contenedora, siguiendo el patrón `NombreDTO.TipoRecord`.

### Estructura por módulo

| Clase contenedora | Records internos |
|---|---|
| `AddressDTO` | `CreateAddressRequest`, `AddressResponse` |
| `CategoryDTO` | `CreateCategoryRequest`, `CategoryResponse` |
| `CustomerDTO` | `CreateCustomerRequest`, `UpdateCustomerRequest`, `CustomerResponse` |
| `ProductDTO` | `CreateProductRequest`, `UpdateProductRequest`, `ProductResponse` |
| `InventoryDTO` | `UpdateInventoryRequest`, `InventoryResponse` |
| `OrderDTO` | `CreateOrderRequest`, `CancelOrderRequest`, `OrderResponse` |
| `OrderItemDTO` | `CreateOrderItemRequest`, `OrderItemResponse` |
| `ReportDTO` | `BestSellingProductResponse`, `MonthlyIncomeResponse`, `TopCustomerResponse`, `LowStockProductResponse` |

### Reglas de diseño
- Los **Requests** solo exponen los campos necesarios para la operación — nunca IDs generados ni campos calculados.
- Las **Responses** son de solo lectura; ningún controlador ni servicio modifica un record una vez creado.
- `OrderResponse` aplana las relaciones: expone `customerId` y `shippingAddressId` en lugar de los objetos completos.

---

## 🔄 5. Mappers (MapStruct)

Los mappers traducen entre entidades JPA y DTOs usando **MapStruct** con `componentModel = "spring"`, lo que los convierte en beans inyectables por Spring.

| Mapper | Métodos |
|---|---|
| `AddressMapper` | `toEntity(CreateAddressRequest)`, `toDTO(Address)` |
| `CategoryMapper` | `toEntity(CreateCategoryRequest)`, `toDTO(Category)` |
| `CustomerMapper` | `toEntity(CreateCustomerRequest)`, `updateEntity(UpdateCustomerRequest, @MappingTarget Customer)`, `toDTO(Customer)` |
| `ProductMapper` | `toEntity(CreateProductRequest)`, `updateEntity(UpdateProductRequest, @MappingTarget Product)`, `toDTO(Product)` |
| `InventoryMapper` | `updateEntity(UpdateInventoryRequest, @MappingTarget Inventory)`, `toDTO(Inventory)` |
| `OrderMapper` | `toDTO(Order)` |
| `OrderItemMapper` | `toDTO(OrderItem)` |

### Anotaciones clave usadas
- `@Mapping(target = "id", ignore = true)` — evita que MapStruct sobreescriba la PK al crear entidades.
- `@Mapping(target = "category", ignore = true)` — las relaciones complejas se asignan manualmente en el servicio para respetar la integridad referencial.
- `@Mapping(target = "categoryId", source = "category.id")` — aplana relaciones anidadas en la respuesta.
- `@Mapping(target = "status", constant = "ACTIVE")` — el estado inicial del cliente se fija en el mapper, no en el servicio.

---

## ⚙️ 6. Capa de Servicios

Cada módulo de negocio tiene una **interfaz** y su **implementación** (`@Service`, `@RequiredArgsConstructor`, `@Transactional`). Las operaciones de lectura usan `@Transactional(readOnly = true)` para optimización.

### Servicios implementados

#### `AddressService` / `CategoryService` / `CustomerService`
CRUD estándar con el patrón:
- `create(Request)` → guarda entidad y retorna DTO.
- `get(Long id)` → retorna DTO; lanza `NotFoundException` si no existe.
- `getObjectById(Long id)` → retorna la entidad JPA (usado internamente por otros servicios).
- `list()` → retorna todos como lista de DTOs.
- `delete(Long id)` → verifica existencia antes de eliminar.
- `update(Long id, Request)` — solo en `CustomerService`; aplica cambios via mapper sobre la entidad existente.

#### `ProductService`
Extiende el CRUD con lógica de negocio:
- `create` valida que el **precio sea mayor a cero** antes de persistir.
- Al crear, asigna la `Category` obtenida de `CategoryService` e inicializa automáticamente un `Inventory` con `stock = 0` y `minStock = 0`.
- `update` permite cambiar la categoría si se envía un nuevo `categoryId`.

#### `InventoryService`
- `update(Long productId, UpdateInventoryRequest)` — valida que `stock` y `minStock` **no sean negativos** antes de actualizar.
- `getByProductId(Long productId)` — recupera el inventario asociado a un producto.

#### `OrderService`
Contiene la lógica más rica del sistema:

| Método | Descripción |
|---|---|
| `create(CreateOrderRequest)` | Crea pedido en estado `CREATED` con total `0.0`. |
| `addItem(Long orderId, CreateOrderItemRequest)` | Agrega un ítem al pedido. Valida que `quantity > 0` y que el pedido esté en `CREATED`. Calcula `subtotal = precio × cantidad` y actualiza el total del pedido. **No descuenta stock.** |
| `pay(Long orderId)` | Transición `CREATED → PAID`. Valida que el pedido tenga ítems y que haya **stock suficiente** para todos. Descuenta el inventario y registra el cambio en `OrderStatusHistory`. |
| `cancel(Long orderId)` | Cancela el pedido. Solo **restaura el stock** si el pedido estaba en `PAID`. Registra el cambio en `OrderStatusHistory`. |
| `get` / `list` | Lectura estándar. |

#### `ReportService`
Consultas de agregación sobre datos históricos, usando los queries JPQL de los repositorios:
- `getBestSellingProducts()` — productos ordenados por cantidad total vendida.
- `getMonthlyIncome()` — ingresos agrupados por año/mes de pedidos `PAID`.
- `getTopCustomers()` — clientes con mayor facturación acumulada.
- `getLowStockProducts()` — productos cuyo `stock < minStock`.

---

## 🛡️ 7. Manejo de Excepciones

Se implementó una capa centralizada de manejo de errores HTTP:

### `NotFoundException`
```java
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) { super(message); }
}
```
Lanzada por todos los servicios cuando un recurso no se encuentra por ID. Reemplaza el uso directo de `EntityNotFoundException` de JPA.

### `GlobalExceptionHandler`
`@RestControllerAdvice` que intercepta excepciones y retorna respuestas JSON uniformes:

| Excepción | HTTP Status | Uso |
|---|---|---|
| `NotFoundException` | `404 Not Found` | Recurso no encontrado por ID |
| `IllegalStateException` | `409 Conflict` | Violación de regla de negocio (estado inválido, stock insuficiente) |
| `IllegalArgumentException` | `400 Bad Request` | Datos de entrada inválidos (precio negativo, cantidad cero) |

---

## 🧪 8. Pruebas Unitarias de Servicios (Mockito)

Se implementó una suite de pruebas unitarias con **JUnit 5 + Mockito** (`@ExtendWith(MockitoExtension.class)`), usando `@Mock` para todas las dependencias e `@InjectMocks` para el servicio bajo prueba.

### `OrderServiceImplTest`
Cubre los criterios más críticos del negocio:

| Test | Criterio validado |
|---|---|
| `addItem_ShouldThrow_WhenQuantityIsZero` | No agregar ítems con cantidad cero |
| `addItem_ShouldThrow_WhenQuantityIsNegative` | No agregar ítems con cantidad inválida |
| `addItem_ShouldCalculateSubtotalAndTotalCorrectly` | `subtotal = precio × cantidad`; total del pedido acumulado |
| `addItem_ShouldNotTouchInventory_WhenItemAdded` | El stock no se altera al agregar ítems |
| `addItem_ShouldThrow_WhenOrderStatusIsNotCreated` | Transición de estado: solo `CREATED` acepta ítems |
| `pay_ShouldDecrementInventoryStock_WhenOrderPaid` | Descuento de inventario al pagar |
| `pay_ShouldThrow_WhenInsufficientStock` | Rechazo de pago por stock insuficiente |
| `pay_ShouldThrow_WhenOrderHasNoItems` | No pagar pedido sin ítems |
| `pay_ShouldSaveStatusHistory_WhenOrderPaid` | Registro de historial `CREATED → PAID` |
| `cancel_ShouldRestoreInventoryStock_WhenOrderWasPaid` | Reversión de stock al cancelar un pedido `PAID` |
| `cancel_ShouldNotRestoreStock_WhenOrderWasCreated` | No restaurar stock si el pedido no había pagado |
| `cancel_ShouldThrow_WhenOrderIsAlreadyCancelled` | Validación de transición: no cancelar dos veces |
| `cancel_ShouldThrow_WhenOrderIsDelivered` | Validación de transición: no cancelar entregado |

### `ProductServiceImplTest`
| Test | Criterio validado |
|---|---|
| `create_ShouldSetCategoryAndSaveProduct` | Asignación correcta de categoría al crear |
| `create_ShouldInitializeInventoryWithZeroStock` | Inventario inicializado con `stock = 0` |
| `update_ShouldUpdateFieldsViaMapper` | Actualización de campos via MapStruct |
| `update_ShouldChangeCategory_WhenCategoryIdProvided` | Cambio de categoría en actualización |
| `get_ShouldReturnProductResponse_WhenProductExists` | Lectura y mapeo correcto |
| `getObjectById_ShouldThrow_WhenProductNotFound` | `NotFoundException` al no encontrar producto |
| `list_ShouldReturnAllProductsAsDTOs` | Lista completa mapeada a DTOs |
| `delete_ShouldThrow_WhenProductNotFound` | Validación de existencia antes de eliminar |

### `InventoryServiceImplTest`
| Test | Criterio validado |
|---|---|
| `update_ShouldUpdateStockAndMinStock_WhenProductExists` | Actualización correcta de stock |
| `update_ShouldApplyChangesViaMapper` | Delegación a mapper para la actualización |
| `getByProductId_ShouldReturnInventoryResponse_WhenProductExists` | Lectura de inventario por producto |
| `update_ShouldThrow_WhenProductNotFound` | `NotFoundException` si el producto no existe |
| `update_ShouldThrow_WhenInventoryNotFound` | `NotFoundException` si el inventario no existe |
| `getByProductId_ShouldThrow_WhenProductNotFound` | Validación en lectura |

---

## 🗂️ 9. Pruebas Unitarias de Mappers

Cada mapper tiene su clase de prueba unitaria pura (sin Spring context, usando `Mappers.getMapper(...)`):

| Clase de test | Verifica |
|---|---|
| `AddressMapperTest` | Mapeo de campos y que `id` y `customer` sean ignorados en `toEntity` |
| `CategoryMapperTest` | Mapeo de campos e ignorado de `id` y `products` |
| `CustomerMapperTest` | `toEntity` fija `status = ACTIVE`; `updateEntity` modifica campos sin tocar `id` |
| `ProductMapperTest` | `toDTO` aplana `category.id → categoryId`; `updateEntity` ignora `id`, `category` e `inventory` |
| `OrderMapperTest` | `toDTO` aplana `customer.id` y `shippingAddress.id` |
| `InventoryMapperTest` | `updateEntity` modifica `stock`/`minStock` e ignora `id` y `product` |
| `OrderItemMapperTest` | `toDTO` aplana `product.id → productId` |

---

## 🏗️ 10. Stack Tecnológico

| Tecnología | Uso |
|---|---|
| Java 21 | Lenguaje principal |
| Spring Boot 3.4.0 | Framework base |
| Spring Data JPA | Persistencia |
| Spring Web | API REST y manejo de excepciones HTTP |
| PostgreSQL | Base de datos en producción |
| MapStruct 1.6.3 | Mapeo entre entidades y DTOs |
| Lombok | Reducción de boilerplate |
| JUnit 5 | Framework de pruebas |
| Mockito | Mocking en pruebas unitarias |
| Testcontainers | Base de datos real en pruebas de integración |
| H2 | Base de datos en memoria para pruebas |
