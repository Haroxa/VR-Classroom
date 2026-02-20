package com.university.vrclassroombackend.domain.certificate.service.impl;

import com.university.vrclassroombackend.domain.certificate.model.Certificate;
import com.university.vrclassroombackend.domain.certificate.repository.CertificateRepository;
import com.university.vrclassroombackend.domain.certificate.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Deprecated
@Service
public class CertificateServiceImpl implements CertificateService {
    
    @Autowired
    private CertificateRepository certificateRepository;

    @Override
    @Deprecated
    public Certificate getCertificateByClaimId(Integer claimId) {
        return null;
    }

    @Override
    @Deprecated
    public List<Certificate> getCertificatesByDonorId(Integer donorId) {
        return null;
    }

    @Override
    @Deprecated
    public Certificate generateCertificate(Integer claimId) {
        return null;
    }
}
