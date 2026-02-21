package com.university.vrclassroombackend.module.space.repository;

import com.university.vrclassroombackend.module.space.model.ClassRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassRoomRepository extends JpaRepository<ClassRoom, Integer> {
    List<ClassRoom> findByBuildingIdAndActiveTrue(Integer buildingId);
}




