package com.loganalyzer.backend.controller;


import com.loganalyzer.backend.service.FileUploadService;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/upload")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @Autowired
    public FileUploadController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    /**
     * Receives a csv file uploaded to the server
     * TODO want to set this up to multi-thread read by my implemented producer consumer reader previously
     * @param file - the CSV file to be parsed
     * @return
     */
    @PostMapping("/file")
    public ResponseEntity<?> uploadCsvFile(
            @RequestParam("file") MultipartFile file, @RequestParam("input") String input) {
        if(file.isEmpty() || input.isEmpty()){
            return ResponseEntity.badRequest().build();
        }

        // get the authentication object from local storage ( the security context holder)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication != null && authentication.isAuthenticated()) {

            File parsedFile = fileUploadService.parseFile(file, input.toUpperCase());

            Resource resource = new FileSystemResource(parsedFile);

            return  ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + parsedFile.getName()  + System.currentTimeMillis() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }

}
