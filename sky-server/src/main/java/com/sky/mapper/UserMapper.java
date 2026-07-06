package com.sky.mapper;


import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

@Mapper
public interface UserMapper {


    /**
     * 根据openid查询用户
     * @param openid
     * @return
     */
    @Select("select * from user where openid=#{openid}")
    User select(String openid);

    void insert(User user);

    /**
     * 根据用户id查询user对象数据
     * @param userId
     * @return
     */
    @Select("select * from user where id=#{userId}")
    User getById(Long userId);

    /**
     * 根据日期查询当日新增注册用户量
     * @param date
     * @return
     */
    Integer countUserByDate(LocalDate date);


}
