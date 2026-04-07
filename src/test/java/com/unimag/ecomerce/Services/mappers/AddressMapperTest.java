package com.unimag.ecomerce.Services.mappers;

import com.unimag.ecomerce.dto.AddressDTO;
import com.unimag.ecomerce.entities.Address;
import com.unimag.ecomerce.mappers.AddressMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

public class AddressMapperTest {
    private final AddressMapper mapper = Mappers.getMapper(AddressMapper.class);

    @Test
    void toEntity_ShouldMapFieldsCorrectly_AndIgnoreIdAndCustomer() {
        var request = new AddressDTO.CreateAddressRequest("Calle 123", "Santa Marta");
        Address entity = mapper.toEntity(request);

        assertThat(entity).isNotNull();
        assertThat(entity.getAddressLine()).isEqualTo("Calle 123");
        assertThat(entity.getCity()).isEqualTo("Santa Marta");
        assertThat(entity.getId()).isNull();
        assertThat(entity.getCustomer()).isNull();
    }

    @Test
    void toDTO_ShouldMapFieldsCorrectly() {
        Address address = Address.builder()
                .id(1L)
                .addressLine("Calle 123")
                .city("Santa Marta")
                .build();

        AddressDTO.AddressResponse response = mapper.toDTO(address);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.addressLine()).isEqualTo("Calle 123");
        assertThat(response.city()).isEqualTo("Santa Marta");
    }
}