package com.se2035.jrw.service;

import com.se2035.jrw.entity.CV;
import com.se2035.jrw.entity.Candidate;
import com.se2035.jrw.repository.CVRepo;
import com.se2035.jrw.repository.CandidateRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CVServiceImpl implements CVService {

    private static final long MAX_CV_SIZE = 5L * 1024L * 1024L;
    private static final byte[] PDF_SIGNATURE = "%PDF-".getBytes(StandardCharsets.US_ASCII);

    private final CandidateRepo candidateRepo;
    private final CVRepo cvRepo;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional(readOnly = true)
    public Optional<CV> getCurrentCv(Integer candidateId) {
        return cvRepo.findFirstByCandidate_CandidateIdAndIsDefaultTrueOrderByUploadedAtDesc(candidateId)
                .or(() -> cvRepo.findFirstByCandidate_CandidateIdOrderByUploadedAtDesc(candidateId));
    }

    @Override
    @Transactional
    public CV uploadOrReplaceCurrentCv(Integer candidateId, MultipartFile file) {
        validatePdf(file);

        Candidate candidate = candidateRepo.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Candidate profile not found"));

        String originalName = sanitizeOriginalName(file.getOriginalFilename());
        String uploadedPath = fileStorageService.uploadFile(file, "cvs");

        try {
            List<CV> existingCvs = cvRepo.findByCandidate(candidate);
            for (CV existingCv : existingCvs) {
                if (Boolean.TRUE.equals(existingCv.getIsDefault())) {
                    existingCv.setIsDefault(false);
                    cvRepo.save(existingCv);
                }
            }

            CV newCv = CV.builder()
                    .candidate(candidate)
                    .cvName(originalName)
                    .filePath(uploadedPath)
                    .isDefault(true)
                    .build();

            return cvRepo.save(newCv);
        } catch (RuntimeException ex) {
            fileStorageService.deleteFile(uploadedPath);
            throw ex;
        }
    }

    @Override
    public Resource loadCvResource(CV cv) {
        if (cv == null || cv.getFilePath() == null || cv.getFilePath().isBlank()) {
            throw new IllegalStateException("CV file is unavailable");
        }
        return fileStorageService.loadFileAsResource(cv.getFilePath());
    }

    private void validatePdf(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select a PDF file");
        }

        if (file.getSize() > MAX_CV_SIZE) {
            throw new IllegalArgumentException("CV file must not exceed 5 MB");
        }

        String originalName = sanitizeOriginalName(file.getOriginalFilename());
        if (!originalName.toLowerCase(Locale.ROOT).endsWith(".pdf")) {
            throw new IllegalArgumentException("Only PDF files are accepted");
        }

        try (InputStream inputStream = file.getInputStream()) {
            byte[] signature = inputStream.readNBytes(PDF_SIGNATURE.length);
            if (signature.length != PDF_SIGNATURE.length) {
                throw new IllegalArgumentException("The uploaded file is not a valid PDF");
            }
            for (int i = 0; i < PDF_SIGNATURE.length; i++) {
                if (signature[i] != PDF_SIGNATURE[i]) {
                    throw new IllegalArgumentException("The uploaded file is not a valid PDF");
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Could not read the uploaded PDF", ex);
        }
    }

    private String sanitizeOriginalName(String originalName) {
        if (originalName == null || originalName.isBlank()) {
            return "candidate-cv.pdf";
        }
        String fileName = Path.of(originalName).getFileName().toString().trim();
        return fileName.isBlank() ? "candidate-cv.pdf" : fileName;
    }
}
