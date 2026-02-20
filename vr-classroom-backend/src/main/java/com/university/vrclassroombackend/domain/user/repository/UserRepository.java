package com.university.vrclassroombackend.domain.user.repository;

import com.university.vrclassroombackend.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByPhone(String phone);
    User findByOpenId(String openId);
}
