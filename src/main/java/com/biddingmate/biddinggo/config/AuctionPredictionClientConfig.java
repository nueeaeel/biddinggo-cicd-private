package com.biddingmate.biddinggo.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        AuctionPredictionEmbeddingProperties.class,
        AuctionPredictionSupabaseProperties.class
})
public class AuctionPredictionClientConfig {
}
