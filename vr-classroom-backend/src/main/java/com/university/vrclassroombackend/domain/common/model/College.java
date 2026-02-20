package com.university.vrclassroombackend.domain.common.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "college")
@Data
public class College {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private String name;
}
