package com.se2035.jrw.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String uploadFile(MultipartFile file, String folder);

    Resource loadFileAsResource(String relativePath);

    void deleteFile(String relativePath);
}

