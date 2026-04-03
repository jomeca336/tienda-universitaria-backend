package com.unimag.ecomerce;

import org.springframework.boot.SpringApplication;

public class TestEcomerceApplication {

	public static void main(String[] args) {
		SpringApplication.from(EcomerceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
