package com.university.vrclassroombackend.domain.space.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "building")
@Data
public class Building {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private Integer campusId;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    private String vrModelUrl;
    
    @Column(nullable = false)
    private Integer sortOrder;
    
    @Column(nullable = false)
    private Boolean active = true;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCampusId() {
        return campusId;
    }

    public void setCampusId(Integer campusId) {
        this.campusId = campusId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
