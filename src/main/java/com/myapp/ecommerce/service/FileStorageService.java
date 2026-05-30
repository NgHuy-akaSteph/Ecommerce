package com.myapp.ecommerce.service;

import com.myapp.ecommerce.dto.response.ApiUpload;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    ApiUpload uploadFile(MultipartFile file, String directory);
    String uploadFileToCloud(MultipartFile file, String directory);
    void deleteFileFromCloud(String fileUrl);
    
    // Private file operations
    String uploadPrivateFileToCloud(byte[] fileData, String fileName, String contentType);
    String generatePresignedUrl(String key, int expirationInMinutes);
}
