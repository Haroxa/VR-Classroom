package com.university.vrclassroombackend.module.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.university.vrclassroombackend.module.user.model.User;

public interface UserMapper extends BaseMapper<User> {
    // 可以在这里添加自定义的查询方法
    User selectByPhone(String phone);
    User selectByOpenId(String openId);
}
