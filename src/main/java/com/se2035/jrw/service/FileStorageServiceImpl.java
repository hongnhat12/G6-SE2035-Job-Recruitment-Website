package com.se2035.jrw.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${upload.dir:uploads}")
    private String uploadDir;

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        if (file == null || file.isEmpty())
            return null;

        try {
            Path dirPath = Paths.get(uploadDir, folder);
            Files.createDirectories(dirPath);

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFileName = UUID.randomUUID().toString() + extension;

            Path filePath = dirPath.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), filePath);

            return "/uploads/" + folder + "/" + uniqueFileName;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deleteFile(String relativePath) {
        if (relativePath == null || relativePath.isBlank())
            return;
        try {
            String filePath = relativePath.replaceFirst("^/uploads/", "");
            Path path = Paths.get(uploadDir, filePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
