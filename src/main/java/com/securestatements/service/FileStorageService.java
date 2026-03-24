
package com.securestatements.service;

import javax.servlet.http.Part;
import java.nio.file.*;
import java.util.UUID;
import java.io.InputStream;

public class FileStorageService {

    private static final String STORAGE = "secure-storage";

    public static String saveFile(Part part) throws Exception {

        Files.createDirectories(Paths.get(STORAGE));

        String name = UUID.randomUUID() + "_" + part.getSubmittedFileName();

        Path path = Paths.get(STORAGE, name);

        try(InputStream in = part.getInputStream()){

            Files.copy(in, path);
        }

        return path.toString();
    }
}
