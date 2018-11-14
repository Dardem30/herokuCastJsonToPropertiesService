package com.gradle.castjsontoproperties.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public interface IConverter {
    /**
     * @param filename is required to read all lines of file
     * @param sourceFile is optional to delete file
     * @param resultFile the same as param above
     * @return All lines of file
     **/
    static String readFile(String filename, File sourceFile, File resultFile) throws IOException {
        String result = "";
        try (final BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))) {
            String line = bufferedReader.readLine();
            while (line != null) {
                result += line + "\n";
                line = bufferedReader.readLine();
            }
        }
        if (sourceFile != null) {
            sourceFile.delete();
        }
        if (resultFile != null) {
            resultFile.delete();
        }
        return result;
    };

    String toProperties(MultipartFile jsonPath) throws IOException;
}
