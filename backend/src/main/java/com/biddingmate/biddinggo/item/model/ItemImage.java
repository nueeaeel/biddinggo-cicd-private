package com.biddingmate.biddinggo.item.model;

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
public class ItemImage {
    private Long id;
    private Long itemId;
    private String url;
    private Integer displayOrder;
    private String type;
    private Integer size;
    private LocalDateTime createdAt;
}
