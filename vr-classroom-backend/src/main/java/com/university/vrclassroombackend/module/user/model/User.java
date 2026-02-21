package com.university.vrclassroombackend.module.user.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(unique = true)
    private String phone;
    
    @Column(unique = true, nullable = false)
    private String openId;
    
    @Column(nullable = false)
    private String name = "未认证";
    
    @Column(nullable = false)
    private String avatar = "assets/default_avatar.png";
    
    private String collegeId;
    
    @Column(nullable = false)
    private Integer verifyStatus = 0;
}




