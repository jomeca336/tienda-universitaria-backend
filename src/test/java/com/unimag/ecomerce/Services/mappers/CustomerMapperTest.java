package com.unimag.ecomerce.services.mappers;
import com.unimag.ecomerce.dto.CustomerDTO;
import com.unimag.ecomerce.entities.Customer;
import com.unimag.ecomerce.enums.CustomerStatus;
import com.unimag.ecomerce.mappers.CustomerMapper;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class CustomerMapperTest {
    private final CustomerMapper mapper = Mappers.getMapper(CustomerMapper.class);

    @Test
    void toEntity_ShouldMapFields_AndSetStatusActive() {
        var req = new CustomerDTO.CreateCustomerRequest("Juan", "juan@email.com");

        Customer entity = mapper.toEntity(req);

        assertThat(entity.getName()).isEqualTo("Juan");
        assertThat(entity.getEmail()).isEqualTo("juan@email.com");
        assertThat(entity.getStatus()).isEqualTo(CustomerStatus.ACTIVE);
        assertThat(entity.getId()).isNull();
        assertThat(entity.getAddresses()).isNull();
    }

    @Test
    void updateEntity_ShouldUpdateFields() {
        Customer customer = Customer.builder()
                .id(1L)
                .name("Old Name")
                .email("old@email.com")
                .status(CustomerStatus.INACTIVE)
                .build();

        var req = new CustomerDTO.UpdateCustomerRequest(
                "New Name",
                "new@email.com",
                CustomerStatus.ACTIVE
        );

        mapper.updateEntity(req, customer);

        assertThat(customer.getName()).isEqualTo("New Name");
        assertThat(customer.getEmail()).isEqualTo("new@email.com");
        assertThat(customer.getStatus()).isEqualTo(CustomerStatus.ACTIVE);
    }

    @Test
    void toDTO_ShouldMapFields() {
        Customer customer = Customer.builder()
                .id(1L)
                .name("Juan")
                .email("juan@email.com")
                .status(CustomerStatus.ACTIVE)
                .build();

        var response = mapper.toDTO(customer);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Juan");
        assertThat(response.email()).isEqualTo("juan@email.com");
        assertThat(response.status()).isEqualTo(CustomerStatus.ACTIVE);
    }
}
