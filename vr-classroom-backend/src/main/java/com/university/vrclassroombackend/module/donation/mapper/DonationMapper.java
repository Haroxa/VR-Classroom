package com.university.vrclassroombackend.module.donation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.university.vrclassroombackend.module.donation.model.DonationOrder;

import java.util.List;

public interface DonationMapper extends BaseMapper<DonationOrder> {
    // 可以在这里添加自定义的查询方法
    DonationOrder selectByOrderNo(String orderNo);
    List<DonationOrder> selectByDonorId(Integer donorId);
}
