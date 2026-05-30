package com.myapp.ecommerce.service.impl;

import com.myapp.ecommerce.dto.response.ApiUpload;
import com.myapp.ecommerce.service.FileStorageService;
import com.myapp.ecommerce.exception.AppException;
import com.myapp.ecommerce.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class R2StorageServiceImpl implements FileStorageService {

    final S3Client s3Client;
    final S3Presigner s3Presigner;

    @Value("${cloudflare.r2.bucket-public}")
    String bucketPublic;

    @Value("${cloudflare.r2.bucket-private}")
    String bucketPrivate;

    @Value("${cloudflare.r2.public-url}")
    String publicUrl;

    @Override
    public ApiUpload uploadFile(MultipartFile file, String directory) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new AppException(ErrorCode.INVALID_FILE_TYPE);
        }
        
        String fileUrl = uploadFileToCloud(file, directory);
        return ApiUpload.builder()
                .fileName(fileUrl)
                .build();
    }

    @Override
    public String uploadFileToCloud(MultipartFile file, String directory) {
        String fileName = directory + UUID.randomUUID() + "-" + file.getOriginalFilename();
        
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketPublic)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, 
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // Return the public URL for the uploaded file
            // Note: Cloudflare R2 requires configuring a public bucket or custom domain
            // for public access, which is defined in publicUrl
            return publicUrl + "/" + fileName;

        } catch (IOException e) {
            log.error("Error uploading file to R2: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void deleteFileFromCloud(String fileUrl) {
        try {
            // Extract the object key from the URL
            // Assumes fileUrl is formatted as: https://pub-xxx.r2.dev/directory/uuid-filename.ext
            if (fileUrl != null && fileUrl.startsWith(publicUrl)) {
                String key = fileUrl.substring(publicUrl.length() + 1); // +1 to remove trailing slash
                
                DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                        .bucket(bucketPublic)
                        .key(key)
                        .build();
                        
                s3Client.deleteObject(deleteObjectRequest);
                log.info("Successfully deleted file: {}", key);
            }
        } catch (Exception e) {
            log.error("Error deleting file from R2: {}", e.getMessage(), e);
        }
    }

    @Override
    public String uploadPrivateFileToCloud(byte[] fileData, String fileName, String contentType) {
        String uniqueKey = "reports/" + UUID.randomUUID() + "-" + fileName;
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketPrivate)
                    .key(uniqueKey)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileData));
            log.info("Successfully uploaded private file to R2: {}", uniqueKey);
            return uniqueKey;
        } catch (Exception e) {
            log.error("Error uploading private file to R2: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String generatePresignedUrl(String key, int expirationInMinutes) {
        try {
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(expirationInMinutes))
                    .getObjectRequest(builder -> builder.bucket(bucketPrivate).key(key))
                    .build();

            return s3Presigner.presignGetObject(presignRequest).url().toString();
        } catch (Exception e) {
            log.error("Error generating presigned URL for private file: {}", e.getMessage(), e);
            return null;
        }
    }
}
