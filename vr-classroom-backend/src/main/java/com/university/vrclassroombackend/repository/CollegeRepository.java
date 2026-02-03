package com.university.vrclassroombackend.repository;

import com.university.vrclassroombackend.model.College;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollegeRepository extends JpaRepository<College, Integer> {
}
