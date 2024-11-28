package com.ecyce.karma;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class JsonNullableMapperTest {
    @Autowired
    private JsonNullableMapper jsonNullableMapper;

    @Test
    public void testBeanRegistration() {
        assertNotNull(jsonNullableMapper, "JsonNullableMapper should be registered as a Spring Bean");
    }
}
