package com.abondarenko.dev.spring.rest.files.unit.controllers;

import com.abondarenko.dev.spring.rest.files.controllers.FileController;
import com.abondarenko.dev.spring.rest.files.entities.FileInfo;
import com.abondarenko.dev.spring.rest.files.models.ResourceWrapper;
import com.abondarenko.dev.spring.rest.files.repositories.FileRepository;
import com.abondarenko.dev.spring.rest.files.services.FileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.abondarenko.dev.spring.rest.files.factory.MultipartFileFactory.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("FileController unit tests")
@WebMvcTest(FileController.class)
@MockBean(FileRepository.class)
class FileControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    FileService fileService;

    @Autowired
    ObjectMapper mapper;

    @Test
    @DisplayName("upload() with multipart file should return UUID")
    void upload_multipartFile_shouldReturnUuid() throws Exception {
        // Config
        val uuid = UUID.randomUUID();
        val file = createMultipartFile(1);

        when(fileService.store(file)).thenReturn(uuid);

        // Call and verify
        mvc.perform(multipart("/files").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(uuid)));
    }

    @Test
    @DisplayName("download() with UUID should return Resource")
    void download_withUuid_shouldReturnResource() throws Exception {
        // Config
        val uuid = UUID.randomUUID();
        val file = createMultipartFile(1);
        val fileInfo = FileInfo.builder()
                .uuid(uuid)
                .filename(file.getOriginalFilename())
                .mimetype(file.getContentType())
                .size(file.getSize())
                .build();
        val resource = new ResourceWrapper(file.getResource(),fileInfo);

        when(fileService.get(uuid)).thenReturn(resource);

        // Call and verify
        mvc.perform(get("/files/" + uuid))
                .andExpect(status().isOk())
                .andExpect(content().bytes(file.getBytes()));
    }

    @Test
    @DisplayName("delete() with UUID should invoke FileService once")
    void delete_withUuid_shouldReturnStatusOk() throws Exception {
        // Config
        val uuid = UUID.randomUUID();

        doNothing().when(fileService).delete(uuid);

        // Call and verify
        mvc.perform(delete("/files/" + uuid))
                .andExpect(status().isOk());
        verify(fileService, times(1)).delete(uuid);
    }
}
