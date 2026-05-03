package com.biddingmate.biddinggo.file.service;

import com.biddingmate.biddinggo.common.exception.CustomException;
import com.biddingmate.biddinggo.common.exception.ErrorType;
import com.biddingmate.biddinggo.config.R2Properties;
import com.biddingmate.biddinggo.file.dto.CreatePresignedUploadUrlRequest;
import com.biddingmate.biddinggo.file.dto.CreatePresignedUploadUrlResponse;
import com.biddingmate.biddinggo.file.dto.DeleteFileRequest;
import com.biddingmate.biddinggo.file.dto.DeleteFileResponse;
import com.biddingmate.biddinggo.file.model.FileMetadata;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Cloudflare R2 파일 업로드/조회/삭제를 담당하는 서비스 구현체.
 * presigned URL 발급과 임시 업로드 파일 검증/정리를 함께 처리한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private static final String TEMP_FILE_KEY_PREFIX = "temp/items/";
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/gif"
    );

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final R2Properties r2Properties;

    @Override
    /**
     * 프론트가 R2에 직접 업로드할 수 있도록 presigned PUT URL을 발급한다.
     */
    public CreatePresignedUploadUrlResponse createPresignedUploadUrl(CreatePresignedUploadUrlRequest request) {
        validateRequest(request);

        // 임시 업로드 전용 key를 생성한다.
        String fileKey = generateFileKey(request.getOriginalFilename());

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(r2Properties.getBucket())
                    .key(fileKey)
                    .contentType(request.getContentType())
                    .build();

            // 설정된 만료 시간 동안만 유효한 presigned URL을 생성한다.
            Duration signatureDuration = Duration.ofMinutes(r2Properties.getPresignedUrlDurationMinutes());

            PutObjectPresignRequest putObjectPresignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(signatureDuration)
                    .putObjectRequest(putObjectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(putObjectPresignRequest);

            return CreatePresignedUploadUrlResponse.builder()
                    .uploadUrl(presignedRequest.url().toString())
                    .fileKey(fileKey)
                    .publicUrl(buildPublicUrl(fileKey))
                    .method("PUT")
                    .expiresInSeconds(signatureDuration.toSeconds())
                    .build();
        } catch (Exception exception) {
            throw new CustomException(ErrorType.R2_PRESIGNED_URL_GENERATION_FAILED);
        }
    }

    @Override
    /**
     * 등록 화면에서 제거한 임시 파일을 즉시 삭제한다.
     */
    public DeleteFileResponse deleteFile(DeleteFileRequest request) {
        if (request == null || request.getFileKey() == null || request.getFileKey().isBlank()) {
            throw new CustomException(ErrorType.INVALID_FILE_UPLOAD_REQUEST);
        }

        deleteFile(request.getFileKey(), false);

        return DeleteFileResponse.builder()
                .fileKey(request.getFileKey())
                .build();
    }

    @Override
    /**
     * fileKey로 최종 접근 가능한 공개 URL을 만든다.
     */
    public String buildPublicUrl(String fileKey) {
        validateFileKey(fileKey);

        String publicBaseUrl = r2Properties.getPublicBaseUrl();

        if (publicBaseUrl == null || publicBaseUrl.isBlank()) {
            throw new CustomException(ErrorType.R2_PRESIGNED_URL_GENERATION_FAILED);
        }

        return publicBaseUrl.endsWith("/")
                ? publicBaseUrl + fileKey
                : publicBaseUrl + "/" + fileKey;
    }

    @Override
    /**
     * 우리 서비스가 관리하는 임시 업로드 파일 key 형식인지 확인한다.
     */
    public boolean isManagedFileKey(String fileKey) {
        return fileKey != null
                && !fileKey.isBlank()
                && fileKey.startsWith(TEMP_FILE_KEY_PREFIX)
                && fileKey.length() > TEMP_FILE_KEY_PREFIX.length();
    }

    @Override
    /**
     * R2에서 실제 객체 메타데이터를 조회한다.
     * 존재 여부 확인과 contentType/contentLength 검증을 한 번에 처리한다.
     */
    public FileMetadata getFileMetadata(String fileKey) {
        validateFileKey(fileKey);

        try {
            HeadObjectResponse response = s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(r2Properties.getBucket())
                    .key(fileKey)
                    .build());

            // 조회한 실제 메타데이터를 DB 저장용 값으로 변환한다.
            return extractFileMetadata(response);
        } catch (S3Exception exception) {
            if (exception.statusCode() == 404) {
                throw new CustomException(ErrorType.UPLOADED_FILE_NOT_FOUND);
            }

            log.error("R2 파일 존재 여부 조회 실패 - fileKey: {}", fileKey, exception);
            throw new CustomException(ErrorType.FILE_LOOKUP_FAILED);
        } catch (Exception exception) {
            log.error("R2 파일 존재 여부 조회 실패 - fileKey: {}", fileKey, exception);
            throw new CustomException(ErrorType.FILE_LOOKUP_FAILED);
        }
    }

    @Override
    /**
     * 등록 실패 cleanup처럼 여러 임시 파일을 한 번에 정리할 때 사용한다.
     * 개별 삭제 실패가 전체 비즈니스 예외를 덮지 않도록 내부적으로만 로그를 남긴다.
     */
    public void deleteFiles(List<String> fileKeys) {
        if (fileKeys == null || fileKeys.isEmpty()) {
            return;
        }

        for (String fileKey : fileKeys) {
            deleteFile(fileKey, true);
        }
    }

    /**
     * presigned URL 발급 요청의 기본 형식을 검증한다.
     */
    private void validateRequest(CreatePresignedUploadUrlRequest request) {
        if (request == null
                || request.getOriginalFilename() == null || request.getOriginalFilename().isBlank()
                || request.getContentType() == null || request.getContentType().isBlank()
                || !ALLOWED_CONTENT_TYPES.contains(request.getContentType())) {
            throw new CustomException(ErrorType.INVALID_FILE_UPLOAD_REQUEST);
        }
    }

    /**
     * 임시 업로드 파일 key를 생성한다.
     * 날짜 경로와 UUID를 사용해 충돌 가능성을 낮춘다.
     */
    private String generateFileKey(String originalFilename) {
        String extension = extractExtension(originalFilename);
        LocalDate today = LocalDate.now();

        return String.format(
                TEMP_FILE_KEY_PREFIX + "%d/%02d/%02d/%s.%s",
                today.getYear(),
                today.getMonthValue(),
                today.getDayOfMonth(),
                UUID.randomUUID(),
                extension
        );
    }

    /**
     * 원본 파일명에서 확장자를 추출한다.
     */
    private String extractExtension(String originalFilename) {
        int extensionIndex = originalFilename.lastIndexOf('.');

        if (extensionIndex < 0 || extensionIndex == originalFilename.length() - 1) {
            throw new CustomException(ErrorType.INVALID_FILE_UPLOAD_REQUEST);
        }

        return originalFilename.substring(extensionIndex + 1).toLowerCase();
    }

    /**
     * fileKey가 우리 서비스의 임시 업로드 정책에 맞는지 검증한다.
     */
    private void validateFileKey(String fileKey) {
        if (!isManagedFileKey(fileKey)) {
            throw new CustomException(ErrorType.INVALID_FILE_UPLOAD_REQUEST);
        }
    }

    /**
     * R2에서 조회한 메타데이터를 애플리케이션에서 사용할 값으로 변환한다.
     * 허용하지 않은 MIME 타입이나 비정상 크기는 여기서 차단한다.
     */
    private FileMetadata extractFileMetadata(HeadObjectResponse response) {
        String contentType = response.contentType();
        Long contentLength = response.contentLength();

        if (contentType == null
                || contentType.isBlank()
                || !ALLOWED_CONTENT_TYPES.contains(contentType)
                || contentLength == null
                || contentLength <= 0
                || contentLength > Integer.MAX_VALUE) {
            throw new CustomException(ErrorType.INVALID_UPLOADED_FILE_METADATA);
        }

        return FileMetadata.builder()
                .contentType(contentType)
                .size(contentLength.intValue())
                .build();
    }

    /**
     * 단건 파일 삭제 공통 로직.
     * cleanup 상황에서는 실패를 무시하고 로그만 남기고,
     * 직접 호출한 삭제 API에서는 예외를 반환한다.
     */
    private void deleteFile(String fileKey, boolean ignoreFailure) {
        if (!isManagedFileKey(fileKey)) {
            if (ignoreFailure) {
                return;
            }
            throw new CustomException(ErrorType.INVALID_FILE_UPLOAD_REQUEST);
        }

        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(r2Properties.getBucket())
                    .key(fileKey)
                    .build());
        } catch (Exception exception) {
            if (!ignoreFailure) {
                throw new CustomException(ErrorType.FILE_DELETE_FAILED);
            }

            log.warn("R2 파일 삭제 정리 실패 - fileKey: {}", fileKey, exception);
        }
    }
}
