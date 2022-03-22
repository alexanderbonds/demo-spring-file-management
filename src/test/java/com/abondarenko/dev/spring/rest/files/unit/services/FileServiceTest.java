package com.abondarenko.dev.spring.rest.files.unit.services;

import com.abondarenko.dev.spring.rest.files.entities.FileInfo;
import com.abondarenko.dev.spring.rest.files.exceptions.implementations.ResourceNotFoundException;
import com.abondarenko.dev.spring.rest.files.repositories.FileRepository;
import com.abondarenko.dev.spring.rest.files.services.FileService;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import static com.abondarenko.dev.spring.rest.files.factory.MultipartFileFactory.*;
import static com.abondarenko.dev.spring.rest.files.unit.services.FileServiceTest.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@DisplayName("FileService unit tests")
@SpringBootTest(classes = {FileService.class})
@ContextConfiguration(initializers = {Initializer.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FileServiceTest {

    @TempDir
    static File directory;

    @MockBean
    FileRepository fileRepository;

    @Autowired
    FileService fileService;

    private static final UUID uuid = UUID.randomUUID();
    private static final MockMultipartFile file = createMultipartFile(1);

    @Test
    @Order(1)
    @DisplayName("store() with multipart file should create file and return UUID")
    void store_withMultipartFile_shouldCreateFileAndReturnUuid() {
        // Config
        val targetFileInfo = FileInfo.builder()
                        .uuid(uuid)
                        .filename(file.getOriginalFilename())
                        .mimetype(file.getContentType())
                        .build();

        // try-with-resources to scope static mocking within current class
        try (MockedStatic<UUID> uuidMock = Mockito.mockStatic(UUID.class)) {
            uuidMock.when(UUID::randomUUID).thenReturn(uuid);
            when(fileRepository.save(any(FileInfo.class))).thenReturn(targetFileInfo);

            // Call
            val result = fileService.store(file);

            // Verify
            assertEquals(uuid, result);
            assertTrue(Files.exists(Path.of(directory.getAbsolutePath(), uuid.toString())));
        }
    }

    @Test
    @Order(2)
    @DisplayName("get() with valid UUID should return ResourceWrapper")
    void get_withValidUuid_shouldReturnResourceWrapper() throws Exception {
        // Config
        val targetFileInfo = FileInfo.builder()
                .uuid(uuid)
                .filename(file.getOriginalFilename())
                .mimetype(file.getContentType())
                .build();

        when(fileRepository.findById(uuid)).thenReturn(Optional.of(targetFileInfo));

        // Call
        val result = fileService.get(uuid);

        // Verify
        assertEquals(targetFileInfo, result.getMetaInfo());
        assertArrayEquals(file.getBytes(), Files.readAllBytes(Path.of(result.getResource().getURI())));
    }

    @Test
    @Order(3)
    @DisplayName("get() with invalid UUID should throw ResourceNotFoundException")
    void get_withInvalidUuid_shouldThrowResourceNotFoundException() {
        // Config
        val invalidUuid = UUID.randomUUID();

        when(fileRepository.findById(invalidUuid)).thenReturn(Optional.empty());

        // Call and verify
        assertThrows(ResourceNotFoundException.class, () -> fileService.get(invalidUuid));
    }

    @Test
    @Order(4)
    @DisplayName("delete() with valid UUID should delete file")
    void delete_withValidUuid_shouldDeleteFile() {
        // Config
        val targetFileInfo = FileInfo.builder()
                .uuid(uuid)
                .filename(file.getOriginalFilename())
                .mimetype(file.getContentType())
                .build();

        doNothing().when(fileRepository).delete(any(FileInfo.class));
        when(fileRepository.findById(uuid)).thenReturn(Optional.of(targetFileInfo));

        // Call
        fileService.delete(uuid);

        // Verify
        assertFalse(Files.exists(Path.of(directory.getAbsolutePath(), uuid.toString())));
    }

    @Test
    @Order(5)
    @DisplayName("delete() with invalid UUID throw ResourceNotFoundException")
    void delete_withInvalidUuid_shouldThrowResourceNotFoundException() {
        // Config
        val invalidUuid = UUID.randomUUID();

        when(fileRepository.findById(invalidUuid)).thenReturn(Optional.empty());

        // Call and verify
        assertThrows(ResourceNotFoundException.class, () -> fileService.delete(invalidUuid));
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(@NotNull ConfigurableApplicationContext context) {
            TestPropertyValues.of("storage.directory=" + directory).applyTo(context);
        }
    }
}
