package com.unimag.ecomerce.services.mappers;

import com.unimag.ecomerce.dto.InventoryDTO;
import com.unimag.ecomerce.entities.Inventory;
import com.unimag.ecomerce.mappers.InventoryMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

public class InventoryMapperTest {

    private final InventoryMapper mapper = Mappers.getMapper(InventoryMapper.class);

    @Test
    void toDTO_ShouldMapFieldsCorrectly() {
        Inventory inventory = Inventory.builder()
                .id(1L)
                .stock(50)
                .minStock(5)
                .build();

        InventoryDTO.InventoryResponse response = mapper.toDTO(inventory);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.stock()).isEqualTo(50);
        assertThat(response.minStock()).isEqualTo(5);
    }

    @Test
    void updateEntity_ShouldUpdateFields_AndIgnoreIdAndProduct() {
        Inventory inventory = Inventory.builder()
                .id(1L)
                .stock(10)
                .minStock(2)
                .build();

        var request = new InventoryDTO.UpdateInventoryRequest(100, 10);

        mapper.updateEntity(request, inventory);

        assertThat(inventory.getStock()).isEqualTo(100);
        assertThat(inventory.getMinStock()).isEqualTo(10);
        assertThat(inventory.getId()).isEqualTo(1L);
        assertThat(inventory.getProduct()).isNull();
    }
}
