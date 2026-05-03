package com.biddingmate.biddinggo.payment.mapper;

import com.biddingmate.biddinggo.common.inif.IMybatisCRUD;
import com.biddingmate.biddinggo.payment.model.VirtualAccount;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VirtualAccountMapper extends IMybatisCRUD<VirtualAccount> {
}
