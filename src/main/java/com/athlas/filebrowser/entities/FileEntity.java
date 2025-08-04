package com.athlas.filebrowser.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "files")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class FileEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private BigDecimal id;

    @Column(name = "filename", unique = true)
    private String filename;

    @Column(name = "size")
    private BigDecimal size;

    @Column(name = "last_modified")
    private Date lastModified;

    @Column(name = "checksum")
    private String checksum;
}
