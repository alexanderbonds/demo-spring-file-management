package com.abondarenko.dev.spring.rest.files.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "files")
public class FileInfo {
    @Id
    @Column(name = "uuid")
    private UUID uuid;

    @Column(name = "filename", nullable = false)
    private String filename;

    @Column(name = "mimetype", nullable = false)
    private String mimetype;

    @Column(name = "size")
    private Long size;
}
