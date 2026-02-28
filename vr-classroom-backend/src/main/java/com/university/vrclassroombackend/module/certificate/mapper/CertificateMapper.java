package com.university.vrclassroombackend.module.certificate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.university.vrclassroombackend.module.certificate.model.Certificate;

import java.util.List;

public interface CertificateMapper extends BaseMapper<Certificate> {
    // 可以在这里添加自定义的查询方法
    Certificate selectByClaimId(Integer claimId);
    List<Certificate> selectByDonorId(Integer donorId);
}
