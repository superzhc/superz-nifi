package com.github.superzhc.nifi.services.jdbi3;

import org.apache.nifi.util.NoOpProcessor;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class Jdbi3Test {

    private TestRunner runner;

    private Jdbi3 service;

    @BeforeEach
    public void setUp() throws Exception {
        runner = TestRunners.newTestRunner(NoOpProcessor.class);
        service = new Jdbi3();

        Map<String, String> serviceProperties = new HashMap<>();
        serviceProperties.put(Jdbi3.DRIVER.getName(), Jdbi3.DRIVER_SQLITE3.getValue());
        serviceProperties.put(Jdbi3.URL.getName(), "jdbc:sqlite:D:\\notebook\\life.db");

        runner.addControllerService(service.getClass().getName(), service, serviceProperties);
        runner.assertValid(service);

        runner.enableControllerService(service);
    }

    @AfterEach
    public void tearDown() {
        if (service.isEnabled()) {
            runner.disableControllerService(service);
        }
    }

    @Test
    public void test() {
        List<Map<String, Object>> data = service.getJdbi().withHandle(handle ->
                handle.createQuery("SELECT * FROM brand_info where id=:id")
                        .bind(0,"1")
                        .bind("id","1")
                        .bind("xxx","2")
                        .mapToMap()
                        .list()
        );

        System.out.println(data);
    }
}