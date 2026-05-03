package com.biddingmate.biddinggo.file.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class FileMetadata {
    private String contentType;
    private Integer size;
}
