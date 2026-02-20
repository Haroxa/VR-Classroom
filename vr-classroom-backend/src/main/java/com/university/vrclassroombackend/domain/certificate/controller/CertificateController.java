package com.university.vrclassroombackend.domain.certificate.controller;

import com.university.vrclassroombackend.dto.ApiResponse;
import com.university.vrclassroombackend.domain.certificate.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Deprecated
@RestController
@RequestMapping("/api/certificate")
public class CertificateController {
    
    @Autowired
    private CertificateService certificateService;

    @Deprecated
    @GetMapping("/claim/{claimId}")
    public ResponseEntity<?> getCertificateByClaimId(@PathVariable Integer claimId) {
        return ResponseEntity.ok().body(ApiResponse.success(null));
    }

    @Deprecated
    @GetMapping("/donor/{donorId}")
    public ResponseEntity<?> getCertificatesByDonorId(@PathVariable Integer donorId) {
        return ResponseEntity.ok().body(ApiResponse.success(null));
    }

    @Deprecated
    @PostMapping("/generate")
    public ResponseEntity<?> generateCertificate(@RequestBody GenerateCertificateRequest request) {
        return ResponseEntity.ok().body(ApiResponse.success(null));
    }

    public static class GenerateCertificateRequest {
        private Integer claimId;

        public Integer getClaimId() {
            return claimId;
        }

        public void setClaimId(Integer claimId) {
            this.claimId = claimId;
        }
    }
}
