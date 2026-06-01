package com.unimag.ecomerce.api.dto;

import com.unimag.ecomerce.enums.CustomerStatus;

import java.io.Serializable;

public class CustomerDTO {

   public record CreateCustomerRequest(
           String name,
           String email

   ) implements Serializable{}

    public record UpdateCustomerRequest(
            String name,
            String email,
            CustomerStatus status
    ) implements Serializable {}

    public record CustomerResponse(
            Long id,
            String name,
            String email,
            CustomerStatus status
    ) implements Serializable{}
}
