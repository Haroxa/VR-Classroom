package com.university.vrclassroombackend.domain.space.repository;

import com.university.vrclassroombackend.domain.space.model.Campus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CampusRepository extends JpaRepository<Campus, Integer> {
    List<Campus> findByActiveTrueOrderBySortOrderAsc();
}
