package com.biddingmate.biddinggo.address.mapper;

import com.biddingmate.biddinggo.address.dto.AddressListResponse;
import com.biddingmate.biddinggo.address.model.Address;
import com.biddingmate.biddinggo.common.inif.IMybatisCRUD;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AddressMapper extends IMybatisCRUD<Address> {
    int countByMemberId(@Param("memberId") Long memberId);
    List<AddressListResponse> findAll(@Param("memberId") Long memberId);
    int updateDefault(@Param("addressId") Long addressId,
                      @Param("memberId") Long memberId);
    int delete(@Param("addressId") Long addressId,
               @Param("memberId") Long memberId);
}
