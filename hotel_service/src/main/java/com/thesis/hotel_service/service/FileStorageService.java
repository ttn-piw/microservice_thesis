package com.thesis.hotel_service.service;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class FileStorageService {
    private final Path storageFolder = Paths.get("uploads");
    private final Path storageFolderHotel = Paths.get("uploads/hotels");
    private final Path storageFolderRoomType = Paths.get("uploads/room_types");

    public FileStorageService(){
        try {
            Files.createDirectories(storageFolder);
        } catch (IOException e) {
            throw new RuntimeException("Can not create storage folder");
        }
    }

    public String saveFileToHotels(MultipartFile file) {
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

            Path filePath = this.storageFolderHotel.resolve(uniqueFileName)
                    .toAbsolutePath();
            log.info("File path: {}", filePath);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            return uniqueFileName;

        } catch (IOException e) {
            throw new RuntimeException("Can not save file", e);
        }
    }

    public String saveFileToRoomTypes(MultipartFile file) {
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

            Path filePath = this.storageFolderRoomType.resolve(uniqueFileName)
                    .toAbsolutePath();
            log.info("File path: {}", filePath);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            return uniqueFileName;

        } catch (IOException e) {
            throw new RuntimeException("Can not save file", e);
        }
    }
}
