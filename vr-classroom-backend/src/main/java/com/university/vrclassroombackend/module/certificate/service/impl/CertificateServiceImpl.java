package com.university.vrclassroombackend.module.certificate.service.impl;

import com.university.vrclassroombackend.module.certificate.model.Certificate;
import com.university.vrclassroombackend.module.certificate.mapper.CertificateMapper;
import com.university.vrclassroombackend.module.certificate.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Deprecated
@Service
public class CertificateServiceImpl implements CertificateService {
    
    @Autowired
    private CertificateMapper certificateMapper;

    @Override
    @Deprecated
    public Certificate getCertificateByClaimId(Integer claimId) {
        return certificateMapper.selectByClaimId(claimId);
    }

    @Override
    @Deprecated
    public List<Certificate> getCertificatesByDonorId(Integer donorId) {
        return certificateMapper.selectByDonorId(donorId);
    }

    @Override
    @Deprecated
    public Certificate generateCertificate(Integer claimId) {
        return null;
    }
}




