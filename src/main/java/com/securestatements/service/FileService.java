package com.securestatements.service;

import java.io.FileOutputStream;

public class FileService {

    private static final String STORAGE = "statements/";

    public static String saveFile(String filename, byte[] data)
            throws Exception {

        String path = STORAGE + filename;

        FileOutputStream fos =
                new FileOutputStream(path);

        fos.write(data);
        fos.close();

        return path;
    }
}
