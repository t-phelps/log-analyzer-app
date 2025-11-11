package com.loganalyzer.backend.service;

import com.loganalyzer.backend.repository.FileUploadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class FileUploadService {

    private static final int NUMBER_OF_PRODUCERS = 4;
    private static final int NUMBER_OF_CONSUMERS = 4;
    private static BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    private FileUploadRepository fileUploadRepository;

    @Autowired
    public FileUploadService(FileUploadRepository fileUploadRepository) {
        this.fileUploadRepository = fileUploadRepository;
    }

    /**
     * Multi-threaded parsing of a file
     * @param file - the file to parse
     * @return
     */
    public void parseFile(MultipartFile file) {

        transferFileToMemory(file);
    }

    private void transferFileToMemory(MultipartFile file) {
        try{
            String fileName = file.getOriginalFilename();
            if(fileName == null){
                throw new FileNotFoundException("File Name null");
            }

            File tempDir = new File(System.getProperty("java.io.tmpdir"));
            File tempFile = new File(tempDir,fileName);
            file.transferTo(tempFile);

            List<String> lines = Files.readAllLines(tempFile.toPath());
            int fileSize = lines.size();
            int linesPerProducer = fileSize / NUMBER_OF_PRODUCERS;

            List<Thread> producers = new ArrayList();
            for(int i = 0; i < NUMBER_OF_PRODUCERS; i++) {
                int start = i * linesPerProducer;
                int end = (i == NUMBER_OF_PRODUCERS - 1) ? fileSize : start + linesPerProducer;
                List<String> subList = lines.subList(start, end);
                Thread producer = new Thread(() -> {
                    for(int j = 0; j < subList.size(); j++) {
                        queue.add(subList.get(j));
                    }

                });
                producer.start();
                producers.add(producer);
            }

            List<Thread> consumers = new ArrayList();
            for(int i = 0; i < NUMBER_OF_CONSUMERS; i++){

                Thread consumer = new Thread(() -> {
                    try {
                        String line = queue.take();
                        if(line.isEmpty()){
                            return;
                        }
                        System.out.println("Thread: " + Thread.currentThread().getName() + ": " + line);
                    }catch (InterruptedException e){
                        throw new RuntimeException(e);
                    }
                }, "Consumer: " + (i + 1));

                consumer.start();
                consumers.add(consumer);
            }

            for(Thread producer : producers) {
                producer.join();
            }

            for(Thread consumer : consumers) {
                consumer.join();
            }




        }catch (IOException e){
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
