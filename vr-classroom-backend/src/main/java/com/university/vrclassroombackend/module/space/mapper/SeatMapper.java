package com.university.vrclassroombackend.module.space.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.university.vrclassroombackend.module.space.model.Seat;

import java.util.List;

public interface SeatMapper extends BaseMapper<Seat> {
    // 可以在这里添加自定义的查询方法
    List<Seat> selectByClassroomId(Integer classroomId);
}
