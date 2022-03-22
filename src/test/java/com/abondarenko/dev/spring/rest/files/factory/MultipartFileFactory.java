package com.abondarenko.dev.spring.rest.files.factory;

import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;

public class MultipartFileFactory {
    public static MockMultipartFile createMultipartFile(int index) {
        return new MockMultipartFile(
                "file",
                String.format("test_%d.txt", index),
                "text/plain",
                String.format("Test Content %d", index).getBytes(StandardCharsets.UTF_8)
        );
    }
}
