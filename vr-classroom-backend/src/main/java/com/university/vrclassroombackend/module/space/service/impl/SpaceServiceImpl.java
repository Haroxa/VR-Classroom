package com.university.vrclassroombackend.module.space.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.university.vrclassroombackend.module.space.mapper.BuildingMapper;
import com.university.vrclassroombackend.module.space.mapper.CategoryMapper;
import com.university.vrclassroombackend.module.space.mapper.ClassRoomMapper;
import com.university.vrclassroombackend.module.space.mapper.CollegeMapper;
import com.university.vrclassroombackend.module.space.mapper.CampusMapper;
import com.university.vrclassroombackend.module.space.model.Building;
import com.university.vrclassroombackend.module.space.model.Campus;
import com.university.vrclassroombackend.module.space.model.Category;
import com.university.vrclassroombackend.module.space.model.ClassRoom;
import com.university.vrclassroombackend.module.space.model.College;
import com.university.vrclassroombackend.module.space.service.SpaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpaceServiceImpl implements SpaceService {

    private static final Logger logger = LoggerFactory.getLogger(SpaceServiceImpl.class);

    @Autowired
    private CampusMapper campusMapper;

    @Autowired
    private BuildingMapper buildingMapper;

    @Autowired
    private ClassRoomMapper classRoomMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CollegeMapper collegeMapper;

    @Override
    public List<Campus> getAllCampuses() {
        logger.info("查询所有校区");
        return campusMapper.selectList(null);
    }

    @Override
    public List<Building> getBuildingsByCampus(Integer campusId) {
        logger.info("查询校区下的楼栋: campusId={}", campusId);
        LambdaQueryWrapper<Building> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Building::getCampusId, campusId);
        return buildingMapper.selectList(queryWrapper);
    }

    @Override
    public List<Building> getAllBuildings() {
        logger.info("查询所有楼栋");
        return buildingMapper.selectList(null);
    }

    @Override
    public List<ClassRoom> getRoomsByBuilding(Integer buildingId) {
        logger.info("查询楼栋下的教室: buildingId={}", buildingId);
        LambdaQueryWrapper<ClassRoom> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClassRoom::getBuildingId, buildingId);
        return classRoomMapper.selectList(queryWrapper);
    }

    @Override
    public List<ClassRoom> getAllRooms() {
        logger.info("查询所有教室");
        return classRoomMapper.selectList(null);
    }

    @Override
    public List<Category> getAllCategories() {
        logger.info("查询所有分类");
        return categoryMapper.selectList(null);
    }

    @Override
    public List<College> getAllColleges() {
        logger.info("查询所有学院");
        return collegeMapper.selectList(null);
    }

    @Override
    public ClassRoom getRoomById(Integer roomId) {
        logger.info("查询教室详情: roomId={}", roomId);
        return classRoomMapper.selectById(roomId);
    }
}
