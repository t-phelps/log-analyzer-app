package com.loganalyzer.backend.repository;


import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

@Repository
public class FileUploadRepository {

    private DSLContext dsl;

    public FileUploadRepository(DSLContext dsl) {
        this.dsl = dsl;
    }


}
