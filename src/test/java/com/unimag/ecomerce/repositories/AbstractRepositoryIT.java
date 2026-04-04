package com.unimag.ecomerce.repositories;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;

import com.unimag.ecomerce.TestcontainersConfiguration;
import org.springframework.context.annotation.Import;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
public abstract class AbstractRepositoryIT {
   
}
