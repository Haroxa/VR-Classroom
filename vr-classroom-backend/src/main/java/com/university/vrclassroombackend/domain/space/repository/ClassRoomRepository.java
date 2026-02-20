package com.university.vrclassroombackend.domain.space.repository;

import com.university.vrclassroombackend.domain.space.model.ClassRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassRoomRepository extends JpaRepository<ClassRoom, Integer> {
    List<ClassRoom> findByBuildingIdAndActiveTrue(Integer buildingId);
}
