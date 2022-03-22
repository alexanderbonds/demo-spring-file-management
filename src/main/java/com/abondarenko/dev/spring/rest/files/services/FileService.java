package com.abondarenko.dev.spring.rest.files.services;

import com.abondarenko.dev.spring.rest.files.entities.FileInfo;
import com.abondarenko.dev.spring.rest.files.exceptions.implementations.FileServerException;
import com.abondarenko.dev.spring.rest.files.exceptions.implementations.ResourceNotFoundException;
import com.abondarenko.dev.spring.rest.files.models.ResourceWrapper;
import com.abondarenko.dev.spring.rest.files.repositories.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static java.lang.String.format;


@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;

    @Value("${storage.directory}")
    private String storageDirectory;

    public UUID store(final MultipartFile file) {
        final UUID uuid = UUID.randomUUID();

        try {
            Files.copy(file.getInputStream(), Paths.get(storageDirectory, uuid.toString()));
        } catch (IOException e) {
            throw new FileServerException("Something went wrong on server side.");
        }

        final FileInfo fileInfo = FileInfo.builder()
                .uuid(uuid)
                .filename(file.getOriginalFilename())
                .mimetype(getCorrectMimeType(file))
                .size(file.getSize())
                .build();

        fileRepository.save(fileInfo);
        return uuid;
    }

    public ResourceWrapper get(final UUID uuid) {
        final Path fileToGet = getPathFromUuid(uuid);

        if (Files.exists(fileToGet)) {
            final FileInfo fileInfo = getFileByUuid(uuid);

            try {
                return new ResourceWrapper(new UrlResource(fileToGet.toUri()), fileInfo);
            } catch (MalformedURLException e) {
                throw new FileServerException("Something went wrong on server side.");
            }
        } else {
            throw new ResourceNotFoundException(format("File %s does not exist!", uuid));
        }
    }

    public void delete(final UUID uuid) {
        final FileInfo fileInfo = getFileByUuid(uuid);

        try {
            Files.deleteIfExists(getPathFromUuid(uuid));
            fileRepository.delete(fileInfo);
        } catch (IOException e) {
            throw new FileServerException("Something went wrong on server side.");
        }
    }

    @PostConstruct
    private void setup() throws IOException {
        final Path pathToCreate = Paths.get(storageDirectory);
        if (Files.notExists(pathToCreate)) Files.createDirectory(pathToCreate);
    }

    private String getCorrectMimeType(final MultipartFile file) {
        String mimeType = file.getContentType();
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        return mimeType;
    }

    private Path getPathFromUuid(final UUID uuid) {
        return Paths.get(storageDirectory, uuid.toString());
    }

    private FileInfo getFileByUuid(final UUID uuid) {
        return fileRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(format("File %s is not registered!", uuid)));
    }
}
