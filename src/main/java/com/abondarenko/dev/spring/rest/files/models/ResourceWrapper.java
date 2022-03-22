package com.abondarenko.dev.spring.rest.files.models;

import com.abondarenko.dev.spring.rest.files.entities.FileInfo;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;

@Data
@RequiredArgsConstructor
public class ResourceWrapper {
    private final Resource resource;
    private final FileInfo metaInfo;
}
