package com.biddingmate.biddinggo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "cloudflare.r2")
public class R2Properties {
    private String accessKey;
    private String secretKey;
    private String accountId;
    private String bucket;
    private String publicBaseUrl;
    private String region = "auto";
    private Long presignedUrlDurationMinutes = 10L;
}
