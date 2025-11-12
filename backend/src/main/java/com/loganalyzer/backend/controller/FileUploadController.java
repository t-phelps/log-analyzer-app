package com.loganalyzer.backend.controller;


import com.loganalyzer.backend.service.FileUploadService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            @RequestParam("file") MultipartFile file) {
        if(file.isEmpty()){
            return ResponseEntity.badRequest().build();
        }

        // get the authentication object from local storage ( the security context holder)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication != null && authentication.isAuthenticated()) {

            fileUploadService.parseFile(file);

            return ResponseEntity.ok().body("Successful upload");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }

}
