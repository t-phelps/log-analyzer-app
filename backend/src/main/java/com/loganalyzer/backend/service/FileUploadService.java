package com.loganalyzer.backend.service;

import com.loganalyzer.backend.repository.FileUploadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class FileUploadService {

    private FileUploadRepository fileUploadRepository;

    @Autowired
    public FileUploadService(FileUploadRepository fileUploadRepository) {
        this.fileUploadRepository = fileUploadRepository;
    }

    public MultipartFile parseFile(MultipartFile file) {

        try{
            byte[] bytes = file.getBytes();

            String fileName = file.getOriginalFilename();

            String contentType = file.getContentType();

            long fileSize =  file.getSize();

            File tempFile = new File("", fileName);
            file.transferTo(tempFile);


            return null;

        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
}
