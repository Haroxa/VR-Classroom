package com.university.vrclassroombackend.module.space.service;

import com.university.vrclassroombackend.module.space.model.Campus;
import com.university.vrclassroombackend.module.space.model.Building;
import com.university.vrclassroombackend.module.space.model.ClassRoom;
import com.university.vrclassroombackend.module.space.model.Seat;

import java.util.List;

@Deprecated
public interface SpaceService {
    @Deprecated
    List<Campus> getCampuses();
    @Deprecated
    List<Building> getBuildings(Integer campusId);
    @Deprecated
    List<ClassRoom> getClassrooms(Integer buildingId);
    @Deprecated
    List<Seat> getSeats(Integer classroomId, Integer page);
}




