package com.coopcredit.credit_application_service;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import com.coopcredit.credit_application_service.domain.port.output.AffiliateRepository;
import static org.mockito.Mockito.mock;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

	@Bean
	@ServiceConnection
	PostgreSQLContainer postgresContainer() {
		return new PostgreSQLContainer(DockerImageName.parse("postgres:latest"));
	}

	@Bean
	AffiliateRepository affiliateRepository() {
		return mock(AffiliateRepository.class);
	}

}
