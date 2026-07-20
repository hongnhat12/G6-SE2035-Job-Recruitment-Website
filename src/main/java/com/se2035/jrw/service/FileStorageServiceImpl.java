package com.se2035.jrw.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${upload.dir:uploads}")
    private String uploadDir;

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        try {
            Path root = getUploadRoot();
            String safeFolder = sanitizeFolder(folder);
            Path directory = root.resolve(safeFolder).normalize();
            ensureInsideUploadRoot(directory, root);
            Files.createDirectories(directory);

            String extension = getExtension(file.getOriginalFilename());
            String uniqueFileName = UUID.randomUUID() + extension;
            Path target = directory.resolve(uniqueFileName).normalize();
            ensureInsideUploadRoot(target, root);

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + safeFolder + "/" + uniqueFileName;
        } catch (IOException ex) {
            throw new IllegalStateException("Could not store uploaded file", ex);
        }
    }

    @Override
    public Resource loadFileAsResource(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            throw new IllegalArgumentException("File path is empty");
        }

        try {
            Path root = getUploadRoot();
            String storageRelativePath = relativePath.replace('\\', '/');
            if (storageRelativePath.startsWith("/uploads/")) {
                storageRelativePath = storageRelativePath.substring("/uploads/".length());
            } else if (storageRelativePath.startsWith("uploads/")) {
                storageRelativePath = storageRelativePath.substring("uploads/".length());
            }

            Path file = root.resolve(storageRelativePath).normalize();
            ensureInsideUploadRoot(file, root);

            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new IllegalStateException("Stored file does not exist or cannot be read");
            }
            return resource;
        } catch (IOException ex) {
            throw new IllegalStateException("Could not load stored file", ex);
        }
    }

    @Override
    public void deleteFile(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            return;
        }

        try {
            Path root = getUploadRoot();
            String storageRelativePath = relativePath.replace('\\', '/');
            if (storageRelativePath.startsWith("/uploads/")) {
                storageRelativePath = storageRelativePath.substring("/uploads/".length());
            } else if (storageRelativePath.startsWith("uploads/")) {
                storageRelativePath = storageRelativePath.substring("uploads/".length());
            }

            Path file = root.resolve(storageRelativePath).normalize();
            ensureInsideUploadRoot(file, root);
            Files.deleteIfExists(file);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not delete stored file", ex);
        }
    }

    private Path getUploadRoot() throws IOException {
        Path root = Paths.get(uploadDir);
        if (!root.isAbsolute()) {
            root = Paths.get(System.getProperty("user.dir")).resolve(root);
        }
        root = root.toAbsolutePath().normalize();
        Files.createDirectories(root);
        return root;
    }

    private String sanitizeFolder(String folder) {
        if (folder == null || folder.isBlank()) {
            return "general";
        }
        String safeFolder = folder.trim().replace('\\', '/').replaceAll("[^A-Za-z0-9/_-]", "");
        if (safeFolder.isBlank() || safeFolder.contains("..")) {
            throw new IllegalArgumentException("Upload folder is invalid");
        }
        return safeFolder;
    }

    private String getExtension(String originalFilename) {
        if (originalFilename == null) {
            return "";
        }
        String fileName = Path.of(originalFilename).getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0) {
            return "";
        }
        return fileName.substring(dotIndex).toLowerCase(Locale.ROOT);
    }

    private void ensureInsideUploadRoot(Path path, Path root) {
        if (!path.toAbsolutePath().normalize().startsWith(root.toAbsolutePath().normalize())) {
            throw new IllegalArgumentException("File path is outside the upload directory");
        }
    }
}
