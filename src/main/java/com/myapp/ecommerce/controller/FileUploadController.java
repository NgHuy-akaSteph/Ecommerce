package com.myapp.ecommerce.controller;

import com.myapp.ecommerce.dto.response.ApiUpload;
import com.myapp.ecommerce.service.FirebaseService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileUploadController {

    FirebaseService firebaseService;

    @PostMapping("/upload")
    public ResponseEntity<ApiUpload> uploadFile(@RequestParam("file") MultipartFile file,
                                                @RequestParam("folder") String folder)
    {
        return ResponseEntity.ok().body(firebaseService.uploadFile(file, folder));
    }
}
