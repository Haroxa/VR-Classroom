package com.university.vrclassroombackend.domain.space.repository;

import com.university.vrclassroombackend.domain.space.model.Building;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BuildingRepository extends JpaRepository<Building, Integer> {
    List<Building> findByCampusIdAndActiveTrueOrderBySortOrderAsc(Integer campusId);
}
