package com.biddingmate.biddinggo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "auction.prediction.supabase")
public class AuctionPredictionSupabaseProperties {
    private boolean enabled;
    private String url;
    private String serviceRoleKey;
    private String schema = "public";
    private Long timeoutMillis = 5000L;
}
