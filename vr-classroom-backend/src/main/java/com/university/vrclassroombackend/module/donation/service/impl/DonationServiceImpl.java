package com.university.vrclassroombackend.module.donation.service.impl;

import com.university.vrclassroombackend.module.donation.model.DonationOrder;
import com.university.vrclassroombackend.module.donation.repository.DonationRepository;
import com.university.vrclassroombackend.module.donation.service.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Deprecated
@Service
public class DonationServiceImpl implements DonationService {
    
    @Autowired
    private DonationRepository donationRepository;

    @Override
    @Deprecated
    public DonationOrder createDonation(Integer donorId, Integer seatId, Integer tierId, String message) {
        return null;
    }

    @Override
    @Deprecated
    public DonationOrder getDonationByOrderNo(String orderNo) {
        return null;
    }

    @Override
    @Deprecated
    public List<DonationOrder> getDonationsByDonorId(Integer donorId) {
        return null;
    }

    @Override
    @Deprecated
    public boolean completeDonation(Integer donationId) {
        return false;
    }

    @Override
    @Deprecated
    public boolean cancelDonation(Integer donationId) {
        return false;
    }
}




