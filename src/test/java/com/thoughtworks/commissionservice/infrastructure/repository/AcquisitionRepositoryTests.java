package com.thoughtworks.commissionservice.infrastructure.repository;

import com.thoughtworks.commissionservice.infrasturcture.repository.AcquisitionRepository;
import com.thoughtworks.commissionservice.service.model.AcquisitionHistory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
public class AcquisitionRepositoryTests {
    @Container
    private static final MySQLContainer MY_SQL_CONTAINER = new MySQLContainer("mysql:8.0")
            .withUsername("test")
            .withPassword("123456")
            .withDatabaseName("commission_service");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MY_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.password", MY_SQL_CONTAINER::getPassword);
        registry.add("spring.datasource.username", MY_SQL_CONTAINER::getUsername);
    }

    @Autowired
    private AcquisitionRepository acquisitionRepository;

    @Test
    public void testFindAcquisitionHistoryByOrderNoAndCreateAcquisitionHistory() {
        String orderNo = "1";

        Optional<AcquisitionHistory> opt = acquisitionRepository.findAcquisitionHistoryByOrderNo(orderNo);

        assertTrue(opt.isEmpty());

        AcquisitionHistory history = new AcquisitionHistory();
        history.setId("any");
        history.setOrderNo("1");
        history.setSalesPersonId("any");
        history.setCommissionAmount(100D);
        history.setCreatedTime(LocalDateTime.now());
        acquisitionRepository.createAcquisitionHistory(history);

        opt = acquisitionRepository.findAcquisitionHistoryByOrderNo(orderNo);

        assertTrue(opt.isPresent());
        assertEquals(100D, opt.get().getCommissionAmount());
    }
}
