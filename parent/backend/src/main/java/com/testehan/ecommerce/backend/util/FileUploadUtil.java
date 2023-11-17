package com.testehan.ecommerce.backend.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUploadUtil {

    public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new IOException("Could not save file: " + fileName, ex);
        }
    }

    public static void deletePreviousFiles(String uploadDir){
        File imageUploadDirectory = new File(uploadDir);
        String[] oldPhotos = imageUploadDirectory.list();
        if (oldPhotos != null) {
            for (String photo : oldPhotos) {
                File currentFile = new File(imageUploadDirectory.getPath(), photo);
                currentFile.delete();
            }
        }
    }

    public static void deletePreviousFilesAndDirectory(String uploadDir) {
        deletePreviousFiles(uploadDir);

        File imageUploadDirectory = new File(uploadDir);
        imageUploadDirectory.delete();
    }
}
