package com.biddingmate.biddinggo.file.service;

import com.biddingmate.biddinggo.file.dto.CreatePresignedUploadUrlRequest;
import com.biddingmate.biddinggo.file.dto.CreatePresignedUploadUrlResponse;
import com.biddingmate.biddinggo.file.dto.DeleteFileRequest;
import com.biddingmate.biddinggo.file.dto.DeleteFileResponse;
import com.biddingmate.biddinggo.file.model.FileMetadata;

import java.util.List;

public interface FileService {
    CreatePresignedUploadUrlResponse createPresignedUploadUrl(CreatePresignedUploadUrlRequest request);

    DeleteFileResponse deleteFile(DeleteFileRequest request);

    String buildPublicUrl(String fileKey);

    boolean isManagedFileKey(String fileKey);

    FileMetadata getFileMetadata(String fileKey);

    void deleteFiles(List<String> fileKeys);
}
