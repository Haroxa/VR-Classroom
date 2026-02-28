package com.university.vrclassroombackend.module.space.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.university.vrclassroombackend.module.space.model.ClassRoom;

import java.util.List;

public interface ClassRoomMapper extends BaseMapper<ClassRoom> {
    // 可以在这里添加自定义的查询方法
    List<ClassRoom> selectByBuildingIdAndActiveTrue(Integer buildingId);
}
