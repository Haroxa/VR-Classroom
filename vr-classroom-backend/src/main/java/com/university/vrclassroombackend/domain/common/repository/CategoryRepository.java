package com.university.vrclassroombackend.domain.common.repository;

import com.university.vrclassroombackend.domain.common.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
