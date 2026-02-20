package com.university.vrclassroombackend.domain.common.repository;

import com.university.vrclassroombackend.domain.common.model.College;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollegeRepository extends JpaRepository<College, Integer> {
}
