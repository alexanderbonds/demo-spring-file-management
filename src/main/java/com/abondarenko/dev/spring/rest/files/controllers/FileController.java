package com.abondarenko.dev.spring.rest.files.controllers;


import com.abondarenko.dev.spring.rest.files.models.ResourceWrapper;
import com.abondarenko.dev.spring.rest.files.services.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @PostMapping
    public ResponseEntity<UUID> upload(@RequestParam final MultipartFile file) {
        log.info("New upload request for file {} ({} bytes)", file.getOriginalFilename(), file.getSize());

        final UUID link = fileService.store(file);
        return ResponseEntity.ok().body(link);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Resource> download(@PathVariable final UUID uuid) {
        log.info("New download request for UUID {}", uuid);

        final ResourceWrapper resource = fileService.get(uuid);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resource.getMetaInfo().getMimetype()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION, String
                                .format("inline; filename=\"%s\"", resource.getMetaInfo().getFilename())
                )
                .body(resource.getResource());
    }

    @DeleteMapping("/{uuid}")
    public void delete(@PathVariable final UUID uuid) {
        log.info("New delete request for file {}", uuid);

        fileService.delete(uuid);
    }
}
