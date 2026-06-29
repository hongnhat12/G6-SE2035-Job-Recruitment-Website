package com.se2035.jrw.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String uploadFile(MultipartFile file, String folder);

    void deleteFile(String relativePath);
}

