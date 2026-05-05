package com.biddingmate.biddinggo.address.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Address {
    private Long id;
    private Long memberId;
    private String zipcode;
    private String address;
    private String detailAddress;
    private LocalDateTime createdAt;
    private boolean defaultYn;
}
