package com.hackathon.HackSync.utils.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.imagekit.client.ImageKitClient;
import io.imagekit.models.files.FileUploadParams;
import io.imagekit.models.files.FileUploadResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageKitService {

    private final ImageKitClient imageKitClient;

    public FileUploadResponse uploadImage(MultipartFile file) throws Exception {
        byte[] imageData = file.getBytes();

        FileUploadParams params = FileUploadParams.builder()
                .file(imageData)
                .fileName(file.getOriginalFilename())
                .folder("test")
                .build();

        return imageKitClient.files().upload(params);
    }
}
