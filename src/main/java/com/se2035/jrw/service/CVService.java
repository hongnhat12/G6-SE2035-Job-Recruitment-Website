package com.se2035.jrw.service;

import com.se2035.jrw.entity.CV;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface CVService {

    Optional<CV> getCurrentCv(Integer candidateId);

    CV uploadOrReplaceCurrentCv(Integer candidateId, MultipartFile file);

    Resource loadCvResource(CV cv);
}
