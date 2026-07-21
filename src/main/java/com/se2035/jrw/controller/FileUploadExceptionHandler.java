package com.se2035.jrw.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class FileUploadExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSize(
            MaxUploadSizeExceededException exception,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("error", "Uploaded file must not exceed 5 MB");
        String requestUri = request == null ? "" : request.getRequestURI();
        return requestUri.contains("/profile/seeker/cv")
                ? "redirect:/profile#cv-section"
                : "redirect:/profile";
    }
}
