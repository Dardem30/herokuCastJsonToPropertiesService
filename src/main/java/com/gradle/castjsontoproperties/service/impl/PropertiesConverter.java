package com.gradle.castjsontoproperties.service.impl;

import com.gradle.castjsontoproperties.service.IConverter;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Component
public class PropertiesConverter implements IConverter {
    private boolean flag = false;
    private String keys = "";
    private final static Logger logger = Logger.getLogger(PropertiesConverter.class);

    @Override
    public String toProperties(final MultipartFile multipartFile) throws IOException {
        File propFile = File.createTempFile("app", "properties");
        File file = File.createTempFile("app", "json");
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(multipartFile.getBytes());
        }
        final Properties properties = new Properties();
        InputStream inStream = new FileInputStream(propFile);
        properties.load(inStream);
        inStream.close();
        final JSONObject jsonObject = new JSONObject(IConverter.readFile(file.getAbsolutePath(), null, null));
        Stream<Map.Entry<String, Object>> stream = jsonObject.toMap().entrySet().stream();
        properties.putAll(stream
                .peek(entry -> {
                    if (entry.getValue() instanceof Map)
                        jsonObjectEntry(entry.getKey(), properties, (Map<String, Object>) entry.getValue());
                })
                .filter(entry -> !(entry.getValue() instanceof Map))
                .peek(entry -> {
                    if (entry.getValue() instanceof ArrayList) {
                        ArrayList list =
                                (ArrayList) entry.getValue();
                        IntStream.range(0, list.size())
                                .forEach(index -> properties.put(entry.getKey() + "." + (index + 1), list.get(index)));
                    }
                })
                .filter(entry -> !(entry.getValue() instanceof ArrayList))
                .collect(Collectors.toMap(val -> val.getKey(), val -> String.valueOf(val.getValue()))));

        FileWriter writer = new FileWriter(propFile);
        properties.store(writer, null);
        writer.close();
        return IConverter.readFile(propFile.getAbsolutePath(), propFile, file);
    }

    private void jsonObjectEntry(final Object key, final Properties properties, final Map<String, Object> map) {
        map.forEach((enKey, enValue) -> {
            if (enValue instanceof HashMap) {

                keys = keys.equals("") ? key + "." + enKey : keys + "." + enKey;

                final Map<String, Object> recMap = (Map<String, Object>) enValue;
                if (recMap.entrySet().stream().anyMatch(recEntry -> recEntry.getValue() instanceof Map)) {
                    flag = true;
                }

                if (flag) {
                    jsonObjectEntry(keys, properties, recMap);
                    flag = false;
                } else {
                    String fakeKey = keys;
                    keys = keys.split("\\.")[0];
                    jsonObjectEntry(fakeKey, properties, recMap);
                }
            } else if (enValue instanceof ArrayList) {
                ArrayList list =
                        (ArrayList) enValue;
                IntStream.range(0, list.size())
                        .forEach(index -> properties.put(key + "." + enKey + "." + (index + 1), list.get(index)));
            } else {
                properties.put(key + "." + enKey, String.valueOf(enValue));
            }
        });
    }
}