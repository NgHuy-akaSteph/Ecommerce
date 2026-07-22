package com.myapp.ecommerce.controller;

import com.myapp.ecommerce.dto.response.ApiResponse;
import com.myapp.ecommerce.service.FileStorageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Admin", description = "Roles, permissions, and reports")
public class ReportController {

    FileStorageService fileStorageService;

    @GetMapping("/download-url")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> getDownloadUrl(@RequestParam String fileKey) {
        log.info("Generating secure download URL for key: {}", fileKey);
        String presignedUrl = fileStorageService.generatePresignedUrl(fileKey, 15);
        return ResponseEntity.ok(ApiResponse.ok("Generate secure download URL successfully", presignedUrl));
    }
}