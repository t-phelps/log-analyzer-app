package com.loganalyzer.backend.service;

import com.loganalyzer.backend.repository.FileUploadRepository;
import com.loganalyzer.backend.file.writer.SynchronizedFileWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class FileUploadService {

    private static final int NUMBER_OF_PRODUCERS = 4;
    private static final int NUMBER_OF_CONSUMERS = 2;
    // blocking queue has internal lock that manages access, so each thread access acquires the lock
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
    public File parseFile(MultipartFile file, String input) {

        return multiThreadedParse(file, input);
    }

    private File multiThreadedParse(MultipartFile file, String input) {
        try{
            /**
             * TODO transfer this block to another function
             */
            String fileName = file.getOriginalFilename();
            if(fileName == null){
                throw new FileNotFoundException("File Name null");
            }

            File tempDir = new File(System.getProperty("java.io.tmpdir"));
            File tempFile = new File(tempDir,fileName);
            file.transferTo(tempFile);

            /** ************************************************* */

            List<String> lines = Files.readAllLines(tempFile.toPath());
            int fileSize = lines.size();
            int linesPerProducer = fileSize / NUMBER_OF_PRODUCERS;

            List<Thread> producers = new ArrayList<>();

            for(int i = 0; i < NUMBER_OF_PRODUCERS; i++) {
                int start = i * linesPerProducer;
                int end = (i == NUMBER_OF_PRODUCERS - 1) ? fileSize : start + linesPerProducer; // handles extra lines (remainders)
                List<String> subList = lines.subList(start, end);

                Thread producer = getProducer(i, subList, input);
                producers.add(producer);
            }

            List<Thread> consumers = new ArrayList<>();
            // create the file the consumers will be writing to
            File writeFile = File.createTempFile("writeFile", ".csv");
            SynchronizedFileWriter synchronizedFileWriter = new SynchronizedFileWriter(writeFile.getAbsolutePath());

            for(int i = 0; i < NUMBER_OF_CONSUMERS; i++){

                Thread consumer = getConsumer(i, synchronizedFileWriter);
                consumers.add(consumer);
            }

            for(Thread producer : producers) {
                producer.join();
            }

            // safely kill out consumer threads once read after producers are joined
            for (int i = 0; i < NUMBER_OF_CONSUMERS; i++) {
                queue.put("POISON");
            }

            for(Thread consumer : consumers) {
                consumer.join();
            }

            if(!tempFile.delete()){
                throw new IOException("Could not delete temp file");
            }

            synchronizedFileWriter.close();
            return writeFile;

        }catch (IOException | InterruptedException e){
            return null;
        }
    }

    private static Thread getProducer(int i, List<String> subList, String input) {
        Thread producer = new Thread(() -> {
            try {
                for (String line : subList) {
                    if(line.contains(input)){
                        queue.put(line);  // put each line individually , .addAll not working
                    }

                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }, "Producer-" + (i + 1));
        producer.start();
        return producer;
    }

    private static Thread getConsumer(int i, SynchronizedFileWriter  synchronizedFileWriter) {
        Thread consumer = new Thread(() -> {
            try {
                while(true) {
                    String line = queue.take();

                    if("POISON".equals(line)) {
                        break;
                    }

                    synchronizedFileWriter.writeLine(Thread.currentThread().getName() + line);
                }
            }catch (InterruptedException | IOException e){
                throw new RuntimeException(e);
            }
        }, "Consumer: " + (i + 1));

        consumer.start();
        return consumer;
    }
}
