package com.gradle.castjsontoproperties.controller;

import com.gradle.castjsontoproperties.service.IConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@RestController
@RequestMapping(value = "/convert")
public class ConverterController {
    private final IConverter converter;

    @Autowired
    public ConverterController(IConverter converter) {
        this.converter = converter;
    }

    @PostMapping(value = "/prop", headers = "content-type=multipart/*")
    public ResponseEntity<String> prop(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(converter.toProperties(file));
    }
}
