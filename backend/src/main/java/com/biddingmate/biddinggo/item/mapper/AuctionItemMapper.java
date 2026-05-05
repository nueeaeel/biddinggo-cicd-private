package com.biddingmate.biddinggo.item.mapper;

import com.biddingmate.biddinggo.common.inif.IMybatisCRUD;
import com.biddingmate.biddinggo.item.model.AuctionItem;
import com.biddingmate.biddinggo.item.model.AuctionItemStatus;
import com.biddingmate.biddinggo.item.model.ItemInspectionStatus;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AuctionItemMapper extends IMybatisCRUD<AuctionItem> {
    int updateStatus(
            @Param("itemId") Long itemId,
            @Param("newStatus") AuctionItemStatus newStatus,
            @Param("currentStatus") AuctionItemStatus currentStatus,
            @Param("currentInspectionStatus") ItemInspectionStatus currentInspectionStatus
    );

    void updateInspectionStatus(@Param("itemId") Long itemId,
                                @Param("newStatus") ItemInspectionStatus newStatus,
                                @Param("currentStatus") ItemInspectionStatus currentStatus,
                                @Param("quality") String quality);

    void updateItemsStatusByAuctionIds(@Param("auctionIds") List<Long> auctionIds,
                                       @Param("status") AuctionItemStatus status);
}
