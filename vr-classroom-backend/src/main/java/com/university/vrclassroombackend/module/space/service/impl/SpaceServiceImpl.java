package com.university.vrclassroombackend.module.space.service.impl;

import com.university.vrclassroombackend.module.space.model.Campus;
import com.university.vrclassroombackend.module.space.model.Building;
import com.university.vrclassroombackend.module.space.model.ClassRoom;
import com.university.vrclassroombackend.module.space.model.Seat;
import com.university.vrclassroombackend.module.space.repository.CampusRepository;
import com.university.vrclassroombackend.module.space.repository.BuildingRepository;
import com.university.vrclassroombackend.module.space.repository.ClassRoomRepository;
import com.university.vrclassroombackend.module.space.repository.SeatRepository;
import com.university.vrclassroombackend.module.space.service.SpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Deprecated
@Service
public class SpaceServiceImpl implements SpaceService {
    
    @Autowired
    private CampusRepository campusRepository;
    
    @Autowired
    private BuildingRepository buildingRepository;
    
    @Autowired
    private ClassRoomRepository classRoomRepository;
    
    @Autowired
    private SeatRepository seatRepository;

    @Override
    @Deprecated
    public List<Campus> getCampuses() {
        return campusRepository.findByActiveTrueOrderBySortOrderAsc();
    }

    @Override
    @Deprecated
    public List<Building> getBuildings(Integer campusId) {
        return buildingRepository.findByCampusIdAndActiveTrueOrderBySortOrderAsc(campusId);
    }

    @Override
    @Deprecated
    public List<ClassRoom> getClassrooms(Integer buildingId) {
        return classRoomRepository.findByBuildingIdAndActiveTrue(buildingId);
    }

    @Override
    @Deprecated
    public List<Seat> getSeats(Integer classroomId, Integer page) {
        return seatRepository.findByClassroomId(classroomId);
    }
}




