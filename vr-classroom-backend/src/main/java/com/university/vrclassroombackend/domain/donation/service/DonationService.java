package com.university.vrclassroombackend.domain.donation.service;

import com.university.vrclassroombackend.domain.donation.model.DonationOrder;

import java.util.List;

@Deprecated
public interface DonationService {
    @Deprecated
    DonationOrder createDonation(Integer donorId, Integer seatId, Integer tierId, String message);
    @Deprecated
    DonationOrder getDonationByOrderNo(String orderNo);
    @Deprecated
    List<DonationOrder> getDonationsByDonorId(Integer donorId);
    @Deprecated
    boolean completeDonation(Integer donationId);
    @Deprecated
    boolean cancelDonation(Integer donationId);
}
