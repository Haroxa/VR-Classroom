package com.university.vrclassroombackend.module.space.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
        LambdaQueryWrapper<Campus> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Campus::getActive, true)
                   .orderByAsc(Campus::getSortOrder);
        return campusMapper.selectList(queryWrapper);
    }

    @Override
    @Deprecated
    public List<Building> getBuildings(Integer campusId) {
        LambdaQueryWrapper<Building> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Building::getCampusId, campusId)
                   .eq(Building::getActive, true)
                   .orderByAsc(Building::getSortOrder);
        return buildingMapper.selectList(queryWrapper);
    }

    @Override
    @Deprecated
    public List<ClassRoom> getClassrooms(Integer buildingId) {
        LambdaQueryWrapper<ClassRoom> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClassRoom::getBuildingId, buildingId)
                   .eq(ClassRoom::getActive, true);
        return classRoomMapper.selectList(queryWrapper);
    }

    @Override
    @Deprecated
    public List<Seat> getSeats(Integer classroomId, Integer page) {
        return seatMapper.selectByClassroomId(classroomId);
    }
}




