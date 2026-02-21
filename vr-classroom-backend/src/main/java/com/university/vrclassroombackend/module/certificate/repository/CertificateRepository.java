package com.university.vrclassroombackend.module.certificate.repository;

import com.university.vrclassroombackend.module.certificate.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CertificateRepository extends JpaRepository<Certificate, Integer> {
    Certificate findByClaimId(Integer claimId);
    List<Certificate> findByDonorId(Integer donorId);
}




