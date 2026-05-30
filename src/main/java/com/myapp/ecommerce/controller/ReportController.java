package com.myapp.ecommerce.controller;

import com.myapp.ecommerce.service.FileStorageService;
import com.myapp.ecommerce.util.annotation.ApiMessage;
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
public class ReportController {

    FileStorageService fileStorageService;

    @GetMapping("/download-url")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiMessage("Generate secure download URL successfully")
    public ResponseEntity<String> getDownloadUrl(@RequestParam String fileKey) {
        log.info("Generating secure download URL for key: {}", fileKey);
        String presignedUrl = fileStorageService.generatePresignedUrl(fileKey, 15);
        return ResponseEntity.ok(presignedUrl);
    }
}
