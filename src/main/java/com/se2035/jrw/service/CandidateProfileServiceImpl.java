package com.se2035.jrw.service;

import com.se2035.jrw.entity.Candidate;
import com.se2035.jrw.repository.CandidateRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CandidateProfileServiceImpl implements CandidateProfileService {

    private static final Set<String> ALLOWED_GENDERS = Set.of("Male", "Female", "Other");
    private static final long MAX_AVATAR_SIZE = 2L * 1024 * 1024;

    private final CandidateRepo candidateRepo;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional(readOnly = true)
    public Candidate getByUserId(Integer userId) {
        return candidateRepo.findByUser_UserId(userId)
                .orElseThrow(() -> new IllegalStateException("Candidate profile not found"));
    }


    @Override
    @Transactional
    public Candidate updateAvatar(Integer userId, MultipartFile avatarFile) {
        Candidate candidate = getByUserId(userId);
        validateAvatar(avatarFile);

        String oldAvatarPath = candidate.getProfileImage();
        String newAvatarPath = fileStorageService.uploadFile(
                avatarFile,
                "avatars/candidates/" + candidate.getCandidateId()
        );

        try {
            candidate.setProfileImage(newAvatarPath);
            Candidate savedCandidate = candidateRepo.saveAndFlush(candidate);

            if (isManagedUpload(oldAvatarPath) && !oldAvatarPath.equals(newAvatarPath)) {
                try {
                    fileStorageService.deleteFile(oldAvatarPath);
                } catch (RuntimeException ignored) {
                    // The new avatar is already saved. An undeleted old file must not break the update.
                }
            }
            return savedCandidate;
        } catch (RuntimeException ex) {
            try {
                fileStorageService.deleteFile(newAvatarPath);
            } catch (RuntimeException ignored) {
                // Preserve the original database/storage error.
            }
            throw ex;
        }
    }

    @Override
    @Transactional
    public Candidate updateProfile(
            Integer userId,
            String fullName,
            String phone,
            LocalDate birthday,
            String gender,
            String address,
            String headline,
            String summary,
            String skills,
            String experience,
            String education) {

        Candidate candidate = getByUserId(userId);

        String normalizedFullName = normalizeRequired(fullName, "Full name", 100);
        String normalizedPhone = normalizeRequired(phone, "Phone number", 20);
        validatePhone(normalizedPhone);

        if (birthday != null && birthday.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Birthday cannot be in the future");
        }

        String normalizedGender = normalizeOptional(gender, 10);
        if (normalizedGender != null && !ALLOWED_GENDERS.contains(normalizedGender)) {
            throw new IllegalArgumentException("Gender is invalid");
        }

        candidate.setFullName(normalizedFullName);
        candidate.setPhone(normalizedPhone);
        candidate.setBirthday(birthday);
        candidate.setGender(normalizedGender);
        candidate.setAddress(normalizeOptional(address, 255));
        candidate.setHeadline(normalizeOptional(headline, 255));
        candidate.setSummary(normalizeOptional(summary, 5000));
        candidate.setSkills(normalizeOptional(skills, 500));
        candidate.setExperience(normalizeOptional(experience, 8000));
        candidate.setEducation(normalizeOptional(education, 8000));

        return candidateRepo.save(candidate);
    }


    private void validateAvatar(MultipartFile avatarFile) {
        if (avatarFile == null || avatarFile.isEmpty()) {
            throw new IllegalArgumentException("Please select an avatar image");
        }
        if (avatarFile.getSize() > MAX_AVATAR_SIZE) {
            throw new IllegalArgumentException("Avatar image must not exceed 2 MB");
        }

        String originalName = avatarFile.getOriginalFilename() == null
                ? ""
                : avatarFile.getOriginalFilename().toLowerCase(Locale.ROOT);
        String contentType = avatarFile.getContentType() == null
                ? ""
                : avatarFile.getContentType().toLowerCase(Locale.ROOT);

        boolean allowedExtension = originalName.endsWith(".jpg")
                || originalName.endsWith(".jpeg")
                || originalName.endsWith(".png");
        boolean allowedContentType = contentType.equals("image/jpeg")
                || contentType.equals("image/png");

        if (!allowedExtension || !allowedContentType || !hasValidImageSignature(avatarFile)) {
            throw new IllegalArgumentException("Only valid JPG or PNG images are accepted");
        }
    }

    private boolean hasValidImageSignature(MultipartFile avatarFile) {
        try (InputStream inputStream = avatarFile.getInputStream()) {
            byte[] header = inputStream.readNBytes(8);
            boolean jpeg = header.length >= 3
                    && (header[0] & 0xFF) == 0xFF
                    && (header[1] & 0xFF) == 0xD8
                    && (header[2] & 0xFF) == 0xFF;
            boolean png = header.length == 8
                    && (header[0] & 0xFF) == 0x89
                    && header[1] == 0x50
                    && header[2] == 0x4E
                    && header[3] == 0x47
                    && header[4] == 0x0D
                    && header[5] == 0x0A
                    && header[6] == 0x1A
                    && header[7] == 0x0A;
            return jpeg || png;
        } catch (IOException ex) {
            throw new IllegalArgumentException("Could not read the avatar image", ex);
        }
    }

    private boolean isManagedUpload(String path) {
        if (path == null) {
            return false;
        }
        String normalized = path.replace('\\', '/');
        return normalized.startsWith("/uploads/") || normalized.startsWith("uploads/");
    }

    private String normalizeRequired(String value, String fieldName, int maxLength) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        if (normalized.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " must not exceed " + maxLength + " characters");
        }
        return normalized;
    }

    private String normalizeOptional(String value, int maxLength) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String normalized = value.trim();
        if (normalized.length() > maxLength) {
            throw new IllegalArgumentException("Input must not exceed " + maxLength + " characters");
        }
        return normalized;
    }

    private void validatePhone(String phone) {
        if (!phone.matches("[0-9+()\\s-]{8,20}")) {
            throw new IllegalArgumentException("Phone number contains invalid characters");
        }

        long digitCount = phone.chars().filter(Character::isDigit).count();
        if (digitCount < 9 || digitCount > 15) {
            throw new IllegalArgumentException("Phone number must contain from 9 to 15 digits");
        }
    }
}
