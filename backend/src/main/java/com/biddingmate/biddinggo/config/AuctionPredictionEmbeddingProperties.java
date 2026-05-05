package com.biddingmate.biddinggo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "auction.prediction.embedding")
public class AuctionPredictionEmbeddingProperties {
    private boolean enabled;
    private String baseUrl = "https://api.openai.com/v1";
    private String apiKey;
    private String model = "text-embedding-3-small";
    private Integer dimensions;
    private Long timeoutMillis = 5000L;
}
