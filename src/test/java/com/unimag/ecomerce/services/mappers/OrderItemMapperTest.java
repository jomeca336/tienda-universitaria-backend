package com.unimag.ecomerce.services.mappers;

import com.unimag.ecomerce.api.dto.OrderItemDTO;
import com.unimag.ecomerce.domine.entities.OrderItem;
import com.unimag.ecomerce.domine.entities.Product;
import com.unimag.ecomerce.services.mappers.OrderItemMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderItemMapperTest {

    private final OrderItemMapper mapper = Mappers.getMapper(OrderItemMapper.class);

    @Test
    void toDTO_ShouldMapFieldsCorrectly_AndExtractProductId() {
        Product product = Product.builder()
                .id(5L)
                .build();

        OrderItem item = OrderItem.builder()
                .id(1L)
                .product(product)
                .quantity(3)
                .unitPrice(5000.0)
                .subtotal(15000.0)
                .build();

        OrderItemDTO.OrderItemResponse response = mapper.toDTO(item);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.productId()).isEqualTo(5L);
        assertThat(response.quantity()).isEqualTo(3);
        assertThat(response.unitPrice()).isEqualTo(5000.0);
        assertThat(response.subtotal()).isEqualTo(15000.0);
    }
}
