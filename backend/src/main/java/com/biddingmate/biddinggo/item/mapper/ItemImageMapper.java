package com.biddingmate.biddinggo.item.mapper;

import com.biddingmate.biddinggo.auction.dto.AuctionDetailResponse;
import com.biddingmate.biddinggo.common.inif.IMybatisCRUD;
import com.biddingmate.biddinggo.inspection.dto.InspectionDetailResponse;
import com.biddingmate.biddinggo.item.model.ItemImage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ItemImageMapper extends IMybatisCRUD<ItemImage> {
    List<AuctionDetailResponse.Image> findDetailImagesByItemId(Long itemId);
    List<InspectionDetailResponse.Image> findInspectionDetailImagesByItemId(Long itemId);
}
