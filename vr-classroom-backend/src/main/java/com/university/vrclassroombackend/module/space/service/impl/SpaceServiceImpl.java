package com.university.vrclassroombackend.module.space.service.impl;

import com.university.vrclassroombackend.module.space.model.Campus;
import com.university.vrclassroombackend.module.space.model.Building;
import com.university.vrclassroombackend.module.space.model.ClassRoom;
import com.university.vrclassroombackend.module.space.model.Seat;
import com.university.vrclassroombackend.module.space.mapper.CampusMapper;
import com.university.vrclassroombackend.module.space.mapper.BuildingMapper;
import com.university.vrclassroombackend.module.space.mapper.ClassRoomMapper;
import com.university.vrclassroombackend.module.space.mapper.SeatMapper;
import com.university.vrclassroombackend.module.space.service.SpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Deprecated
@Service
public class SpaceServiceImpl implements SpaceService {
    
    @Autowired
    private CampusMapper campusMapper;
    
    @Autowired
    private BuildingMapper buildingMapper;
    
    @Autowired
    private ClassRoomMapper classRoomMapper;
    
    @Autowired
    private SeatMapper seatMapper;

    @Override
    @Deprecated
    public List<Campus> getCampuses() {
        return campusMapper.selectByActiveTrueOrderBySortOrderAsc();
    }

    @Override
    @Deprecated
    public List<Building> getBuildings(Integer campusId) {
        return buildingMapper.selectByCampusIdAndActiveTrueOrderBySortOrderAsc(campusId);
    }

    @Override
    @Deprecated
    public List<ClassRoom> getClassrooms(Integer buildingId) {
        return classRoomMapper.selectByBuildingIdAndActiveTrue(buildingId);
    }

    @Override
    @Deprecated
    public List<Seat> getSeats(Integer classroomId, Integer page) {
        return seatMapper.selectByClassroomId(classroomId);
    }
}




