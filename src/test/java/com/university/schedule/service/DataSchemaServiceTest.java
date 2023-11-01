package com.university.schedule.service;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
public class DataSchemaServiceTest {

    private DataSchemaService dataSchemaService;

    @Mock
    private Flyway flyway;

    @BeforeEach
    public void setUp() {
        dataSchemaService = new DataSchemaService();
        dataSchemaService.setFlyway(flyway);
    }

    @Test
    public void clean() {
        dataSchemaService.clean();
        verify(flyway).clean();
        verify(flyway).migrate();
    }

}
