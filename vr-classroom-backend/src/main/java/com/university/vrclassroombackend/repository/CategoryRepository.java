package com.university.vrclassroombackend.repository;

import com.university.vrclassroombackend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
