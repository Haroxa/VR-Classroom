package com.university.vrclassroombackend.module.space.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.university.vrclassroombackend.module.space.model.College;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CollegeMapper extends BaseMapper<College> {
}
