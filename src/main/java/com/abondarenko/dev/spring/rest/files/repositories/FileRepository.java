package com.abondarenko.dev.spring.rest.files.repositories;

import com.abondarenko.dev.spring.rest.files.entities.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileRepository extends JpaRepository<FileInfo, UUID> {
}
