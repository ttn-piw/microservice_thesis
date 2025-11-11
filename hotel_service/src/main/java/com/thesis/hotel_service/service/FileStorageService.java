package com.thesis.hotel_service.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
    private final Path storageFolder = Paths.get("uploads");

    public FileStorageService(){
        try {
            Files.createDirectories(storageFolder);
        } catch (IOException e) {
            throw new RuntimeException("Can not create storage folder");
        }
    }

    public String saveFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("ERROR: Empty file");
        }

        try {
            // Take .png / .jpeg .....
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String uniqueFileName = UUID.randomUUID().toString() + extension;

            Path filePath = this.storageFolder.resolve(uniqueFileName)
                    .toAbsolutePath();

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            return uniqueFileName;

        } catch (IOException e) {
            throw new RuntimeException("Can not save file", e);
        }
    }
}
