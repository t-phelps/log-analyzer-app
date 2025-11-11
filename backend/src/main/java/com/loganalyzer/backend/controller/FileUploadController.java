package com.loganalyzer.backend.controller;


import com.loganalyzer.backend.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.loganalyzer.backend.jwt.JwtTokenGenerator;

@RestController
@RequestMapping("/upload")
public class FileUploadController {

    private FileUploadService fileUploadService;
    private JwtTokenGenerator jwtTokenGenerator;

    @Autowired
    public FileUploadController(FileUploadService fileUploadService, JwtTokenGenerator jwtTokenGenerator) {
        this.fileUploadService = fileUploadService;
        this.jwtTokenGenerator = jwtTokenGenerator;
    }

    /**
     * Receives a csv file uploaded to the server
     * TODO want to set this up to multi-thread read by my implemented producer consumer reader previously
     * @param file - the CSV file to be parsed
     * @param jwt - the JWT for validation
     * @return
     */
    @PostMapping("/file")
    public ResponseEntity<?> uploadCsvFile(
            @RequestParam("file") MultipartFile file,
            @CookieValue(name = "jwt", required = true) String jwt ) {
//        if(file.isEmpty()){
//            return ResponseEntity.badRequest().build();
//        }

        jwtTokenGenerator.validateJwt(jwt);

        return ResponseEntity.ok().body("Successful upload");
    }

}
