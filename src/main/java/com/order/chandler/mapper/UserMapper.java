package com.order.chandler.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.order.chandler.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
