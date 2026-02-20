package com.university.vrclassroombackend.domain.space.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "classroom")
@Data
public class ClassRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private Integer buildingId;
    
    @Column(nullable = false)
    private String name;
    
    private Integer floor;
    
    private String description;
    
    private String vrModelUrl;
    
    @Column(nullable = false)
    private Integer seatCount;
    
    @Column(nullable = false)
    private Integer claimedCount = 0;
    
    @Column(nullable = false)
    private Boolean active = true;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Integer buildingId) {
        this.buildingId = buildingId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVrModelUrl() {
        return vrModelUrl;
    }

    public void setVrModelUrl(String vrModelUrl) {
        this.vrModelUrl = vrModelUrl;
    }

    public Integer getSeatCount() {
        return seatCount;
    }

    public void setSeatCount(Integer seatCount) {
        this.seatCount = seatCount;
    }

    public Integer getClaimedCount() {
        return claimedCount;
    }

    public void setClaimedCount(Integer claimedCount) {
        this.claimedCount = claimedCount;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
