package com.unimag.ecomerce.api.dto;

import java.io.Serializable;

public class AddressDTO {

    public record CreateAddressRequest(
            String addressLine,
            String city
    ) implements Serializable {}

    public record AddressResponse(
            Long id,
            String addressLine,
            String city
    ) implements Serializable{}


}
