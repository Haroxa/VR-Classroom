package com.university.vrclassroombackend.module.space.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.university.vrclassroombackend.module.space.model.Campus;

import java.util.List;

public interface CampusMapper extends BaseMapper<Campus> {
    // 可以在这里添加自定义的查询方法
    List<Campus> selectByActiveTrueOrderBySortOrderAsc();
}
