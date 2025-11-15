package com.loganalyzer.backend.file.writer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Synchronized file writer class
 */
public class SynchronizedFileWriter {
    private final BufferedWriter fileWriter;

    /**
     * Constructor for the SynchronizedFileWriter
     * @param filePath - path to file
     * @throws IOException
     */
    public SynchronizedFileWriter(String filePath) throws IOException {
        this.fileWriter = new BufferedWriter(new FileWriter(filePath));
    }

    /**
     * Synchronized writing of liens to a file
     * @param line - line to write
     * @throws IOException - on write or newLine error
     */
    public synchronized void writeLine(String line) throws IOException {
        fileWriter.write(line);
        fileWriter.newLine();
    }

    // TODO do I even need this if i call close it automatically flushes the stream
    public synchronized void flush() throws IOException {
        fileWriter.flush();
    }

    /**
     * Closes stream and flushes it
     * @throws IOException
     */
    public void close() throws IOException {
        fileWriter.close();
    }



}
