package com.university.vrclassroombackend.module.certificate.service;

import com.university.vrclassroombackend.module.certificate.model.Certificate;

import java.util.List;

@Deprecated
public interface CertificateService {
    @Deprecated
    Certificate getCertificateByClaimId(Integer claimId);
    @Deprecated
    List<Certificate> getCertificatesByDonorId(Integer donorId);
    @Deprecated
    Certificate generateCertificate(Integer claimId);
}




