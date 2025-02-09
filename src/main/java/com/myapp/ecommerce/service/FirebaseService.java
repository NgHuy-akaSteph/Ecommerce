package com.myapp.ecommerce.service;

import com.google.firebase.cloud.StorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FirebaseService {

    private final StorageClient storageClient;

    public String uploadFileToFireBase(MultipartFile file, String directory) {
        String fileName = "casestudym5/"+ directory + UUID.randomUUID() + "-" + file.getOriginalFilename();
        try {
            // Check if the directory exists by trying to list its contents
            if (storageClient.bucket().get("ecommerce/" + directory) == null) {
                // Create a dummy file to simulate the folder
                storageClient.bucket().create("ecommerce/" + directory + ".keep", new byte[0]);
            }
            var blob = storageClient.bucket().create(fileName, file.getInputStream(), file.getContentType());
            blob.createAcl(com.google.cloud.storage.Acl.of(com.google.cloud.storage.Acl.User.ofAllUsers(), com.google.cloud.storage.Acl.Role.READER));
            return blob.getMediaLink();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void deleteFileFromFireBase(String fileUrl) {
        try {
            // Extract the file name from the URL
            String fileName = fileUrl.split("/o/")[1].split("\\?")[0].replace("%2F", "/");
            storageClient.bucket().get(fileName).delete();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
