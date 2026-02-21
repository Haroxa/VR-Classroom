package com.university.vrclassroombackend.module.donation.dto;

public class CreateDonationRequest {
    private Integer seatId;
    private Integer tierId;
    private String message;

    public Integer getSeatId() {
        return seatId;
    }

    public void setSeatId(Integer seatId) {
        this.seatId = seatId;
    }

    public Integer getTierId() {
        return tierId;
    }

    public void setTierId(Integer tierId) {
        this.tierId = tierId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}




