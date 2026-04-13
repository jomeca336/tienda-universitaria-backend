package com.unimag.ecomerce.services;

import com.unimag.ecomerce.api.dto.InventoryDTO;
import com.unimag.ecomerce.domine.entities.Inventory;
import com.unimag.ecomerce.services.mappers.InventoryMapper;
import com.unimag.ecomerce.domine.repositories.InventoryRepository;
import com.unimag.ecomerce.domine.repositories.ProductRepository;
import com.unimag.ecomerce.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock private InventoryRepository inventoryRepository;
    @Mock private ProductRepository productRepository;
    @Mock private InventoryMapper inventoryMapper;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    // ─── update ───────────────────────────────────────────────────────────────

    @Test
    void update_ShouldUpdateStockAndMinStock_WhenProductExists() {
        Inventory inventory = Inventory.builder().id(1L).stock(10).minStock(2).build();
        var request = new InventoryDTO.UpdateInventoryRequest(50, 5);
        var expectedResponse = new InventoryDTO.InventoryResponse(1L, 50, 5);

        when(productRepository.existsById(1L)).thenReturn(true);
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(inventory)).thenReturn(inventory);
        when(inventoryMapper.toDTO(inventory)).thenReturn(expectedResponse);

        InventoryDTO.InventoryResponse response = inventoryService.update(1L, request);

        verify(inventoryMapper).updateEntity(request, inventory);
        assertThat(response.stock()).isEqualTo(50);
        assertThat(response.minStock()).isEqualTo(5);
    }

    @Test
    void update_ShouldApplyChangesViaMapper() {
        Inventory inventory = Inventory.builder().id(1L).stock(10).minStock(2).build();
        var request = new InventoryDTO.UpdateInventoryRequest(100, 10);

        when(productRepository.existsById(5L)).thenReturn(true);
        when(inventoryRepository.findByProductId(5L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any())).thenReturn(inventory);
        when(inventoryMapper.toDTO(any())).thenReturn(mock(InventoryDTO.InventoryResponse.class));

        inventoryService.update(5L, request);

        ArgumentCaptor<Inventory> captor = ArgumentCaptor.forClass(Inventory.class);
        verify(inventoryRepository).save(captor.capture());
        assertThat(captor.getValue()).isEqualTo(inventory);
    }

    // ─── getByProductId ───────────────────────────────────────────────────────

    @Test
    void getByProductId_ShouldReturnInventoryResponse_WhenProductExists() {
        Inventory inventory = Inventory.builder().id(1L).stock(20).minStock(3).build();
        var expectedResponse = new InventoryDTO.InventoryResponse(1L, 20, 3);

        when(productRepository.existsById(1L)).thenReturn(true);
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(inventory));
        when(inventoryMapper.toDTO(inventory)).thenReturn(expectedResponse);

        InventoryDTO.InventoryResponse response = inventoryService.getByProductId(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.stock()).isEqualTo(20);
        assertThat(response.minStock()).isEqualTo(3);
    }

    // ─── errores ──────────────────────────────────────────────────────────────

    @Test
    void update_ShouldThrow_WhenProductNotFound() {
        when(productRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> inventoryService.update(99L, new InventoryDTO.UpdateInventoryRequest(10, 1)))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void update_ShouldThrow_WhenInventoryNotFound() {
        when(productRepository.existsById(1L)).thenReturn(true);
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryService.update(1L, new InventoryDTO.UpdateInventoryRequest(10, 1)))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Inventory");
    }

    @Test
    void getByProductId_ShouldThrow_WhenProductNotFound() {
        when(productRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> inventoryService.getByProductId(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");
    }
}
