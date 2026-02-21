package com.university.vrclassroombackend.module.donation.repository;

import com.university.vrclassroombackend.module.donation.model.DonationOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DonationRepository extends JpaRepository<DonationOrder, Integer> {
    DonationOrder findByOrderNo(String orderNo);
    List<DonationOrder> findByDonorId(Integer donorId);
}




