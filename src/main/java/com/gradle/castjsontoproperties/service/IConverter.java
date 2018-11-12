package com.gradle.castjsontoproperties.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Properties;

public interface IConverter {
    static String readFile(String filename) throws IOException {
        String result = "";
        try (final BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))) {
            String line = bufferedReader.readLine();
            while (line != null) {
                result += line + "\n";
                line = bufferedReader.readLine();
            }
        }
        final Properties properties = new Properties();
        String path = IConverter.class.getResource("/app.properties").getPath();
        InputStream inStream = new FileInputStream(path);
        properties.load(inStream);
        inStream.close();
        properties.clear();
        FileWriter fileWriter = new FileWriter(path);
        properties.store(fileWriter, null);
        fileWriter.close();
        return result;
    };

    String toProperties(MultipartFile jsonPath) throws IOException;
}
