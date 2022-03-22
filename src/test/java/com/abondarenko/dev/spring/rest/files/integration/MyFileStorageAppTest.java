package com.abondarenko.dev.spring.rest.files.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static com.abondarenko.dev.spring.rest.files.factory.MultipartFileFactory.*;
import static com.abondarenko.dev.spring.rest.files.integration.MyFileStorageAppTest.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("MyFileStorageApp integration test")
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(initializers = {Initializer.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MyFileStorageAppTest {

    @TempDir
    static File directory;

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    private static String uuid = "";
    private static MockMultipartFile file = createMultipartFile(1);

    @Test
    @Order(1)
    @DisplayName("upload() file should create a file and return UUID")
    void upload_withMultipartFile_shouldCreateFileAndReturnUUID() throws Exception {
        // Call
        val result = mvc.perform(multipart("/files").file(file));
        uuid = mapper.readValue(result.andReturn().getResponse().getContentAsString(), String.class);

        // Verify
        result.andExpect(status().isOk());
        assertDoesNotThrow(() -> UUID.fromString(uuid));
        assertTrue(Files.exists(Path.of(directory.getAbsolutePath(), uuid)));
    }

    @Test
    @Order(2)
    @DisplayName("download() with valid UUID should return uploaded file")
    void download_withValidUuid_shouldReturnFile() throws Exception {
        // Call
        val result = mvc.perform(get("/files/" + uuid));

        // Verify
        result.andExpect(status().isOk())
                .andExpect(content().contentType(file.getContentType()))
                .andExpect(content().bytes(file.getBytes()));
    }

    @Test
    @Order(3)
    @DisplayName("download() with invalid UUID should return FileNotFoundException")
    void download_withInvalidUuid_shouldReturnFile() throws Exception {
        // Call
        val result = mvc.perform(get("/files/" + UUID.randomUUID()));

        // Verify
        result.andExpect(status().isNotFound());
    }

    @Test
    @Order(4)
    @DisplayName("delete() with valid UUID should delete uploaded file")
    void delete_withValidUuid_shouldDeleteFile() throws Exception {
        // Call
        val result = mvc.perform(delete("/files/" + uuid));

        // Verify
        result.andExpect(status().isOk());
        assertFalse(Files.exists(Path.of(directory.getAbsolutePath(), uuid)));
    }

    @Test
    @Order(5)
    @DisplayName("delete() with invalid UUID should delete uploaded file")
    void delete_withInvalidUuid_shouldDeleteFile() throws Exception {
        // Call
        val result = mvc.perform(delete("/files/" + UUID.randomUUID()));

        // Verify
        result.andExpect(status().isNotFound());
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(@NotNull ConfigurableApplicationContext context) {
            TestPropertyValues.of("storage.directory=" + directory).applyTo(context);
        }
    }
}
