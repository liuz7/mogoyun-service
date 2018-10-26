package com.mogo.utils;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Log4j2
public class FileUtil {

    public static void copyFile(Path sourceFile, Path targetFile) {
        try {
            if (!targetFile.getParent().toFile().exists()) {
                Files.createDirectory(targetFile.getParent());
            }
            Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
            log.info("Exception:{}", ioe);
        }
    }

    public static void deleteFile(Path file) {
        try {
            Files.deleteIfExists(file);
        } catch (IOException ioe) {
            log.info("Exception:{}", ioe);
        }
    }
}
