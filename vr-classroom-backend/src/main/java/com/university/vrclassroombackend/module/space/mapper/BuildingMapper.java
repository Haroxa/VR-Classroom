package com.university.vrclassroombackend.module.space.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.university.vrclassroombackend.module.space.model.Building;

import java.util.List;

public interface BuildingMapper extends BaseMapper<Building> {
    // 可以在这里添加自定义的查询方法
    List<Building> selectByCampusIdAndActiveTrueOrderBySortOrderAsc(Integer campusId);
}
