package com.securestatements.service;

import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FileServiceTest {

    @Test
    public void testSaveFile() throws Exception {
        String filename = "testfile.pdf";
        byte[] data = "Hello World".getBytes();

        String path = FileService.saveFile(filename, data);
        assertTrue(Files.exists(Path.of(path)));

        // Cleanup
        Files.delete(Path.of(path));
    }
}

