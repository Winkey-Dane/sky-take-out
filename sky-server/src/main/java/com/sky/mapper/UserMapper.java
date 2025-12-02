package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {

    /**
     * 根据openid查询用户信息
     * @param openid
     * @return
     */
    @Select("SELECT * FROM user WHERE openid = #{openid}")
    User getUserByOpenid(String openid);

    /**
     * 插入用户信息
     * @param user
     */
    void insert(User user);

    /**
     * 根据用户id查询用户信息
     * @param userId
     * @return
     */
    @Select("SELECT * FROM user WHERE id = #{userId}")
    User getById(Long userId);

    /**
     * 统计某个日期新增用户数
     * @param map
     * @return
     */
    Integer getNewUserTotalByDate(Map map);

    /**
     * 统计某个日期用户总数
     * @param map
     * @return
     */
    @Select("select count(*) from user where create_time <= #{endTime}")
    Integer getUserTotalByDate(Map map);
}
