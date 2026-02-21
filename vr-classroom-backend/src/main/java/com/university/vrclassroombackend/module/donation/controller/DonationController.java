package com.university.vrclassroombackend.module.donation.controller;

import com.university.vrclassroombackend.common.dto.ApiResponse;
import com.university.vrclassroombackend.module.donation.dto.CreateDonationRequest;
import com.university.vrclassroombackend.module.donation.service.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Deprecated
@RestController
@RequestMapping("/api/donation")
public class DonationController {
    
    @Autowired
    private DonationService donationService;

    @Deprecated
    @PostMapping("/create")
    public ResponseEntity<?> createDonation(
            @RequestBody CreateDonationRequest request,
            jakarta.servlet.http.HttpServletRequest httpRequest) {
        return ResponseEntity.ok().body(ApiResponse.success(null));
    }

    @Deprecated
    @GetMapping("/orders")
    public ResponseEntity<?> getDonationOrders(
            @RequestParam(defaultValue = "0") Integer page,
            jakarta.servlet.http.HttpServletRequest httpRequest) {
        return ResponseEntity.ok().body(ApiResponse.success(null));
    }

    @Deprecated
    @GetMapping("/orders/{orderNo}")
    public ResponseEntity<?> getDonationOrder(@PathVariable String orderNo) {
        return ResponseEntity.ok().body(ApiResponse.success(null));
    }

    @Deprecated
    @PostMapping("/orders/{id}/cancel")
    public ResponseEntity<?> cancelDonation(@PathVariable Integer id) {
        return ResponseEntity.ok().body(ApiResponse.success());
    }
}




