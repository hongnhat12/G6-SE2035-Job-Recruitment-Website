package com.se2035.jrw.service;

import com.se2035.jrw.entity.Candidate;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public interface CandidateProfileService {

    Candidate getByUserId(Integer userId);

    Candidate updateAvatar(Integer userId, MultipartFile avatarFile);

    Candidate updateProfile(
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
            String education
    );
}
