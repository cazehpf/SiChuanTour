package com.cazehpf.mapper;


import com.cazehpf.entity.User;
import com.cazehpf.entity.UserComment;
import com.cazehpf.entity.UserLiked;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;

import java.util.List;

/**
 * author:hpf
 **/
@Mapper //在运行时,会自动生成该接口的实现类对象(代理对象), 并且将该对象交给IOC容器管理
public interface UserMapper {

    //根据用户ID查询用户获赞数目
    public Integer selectLikedNumByID(Integer userID);

    //根据用户ID查询用户信息
    @Select("select * from User where userID=#{userID}")
    public User selectByID(Integer userID);

    //新增用户
    @Options(useGeneratedKeys = true, keyProperty = "userID")
    @Insert("insert into User(userName, userPSW,userMail,createTime)" +
            " values (#{userName},#{userPSW},#{userMail},#{createTime})")
    public void insert(User user);

    //根据用户名和账号查询用户
    @Select("select * from User where userName=#{name} and userPSW=#{password} ")
    public User selectByNamePSW(String name,String password );

    //根据用户名查询用户
    @Select("select * from User where userName=#{name}")
    public User selectByName(String name);

    //根据邮箱查询用户
    @Select("select * from User where userMail=#{mail}")
    public User selectByMail(String mail);

    //根据User附带的ID修改User

    public void setUserByID(User user);

    //查询当前userID的所有评论，并分页
    public  List<UserComment> getUserComments(LocalDate startTime, LocalDate endTime, String interestFilter, Integer userID);

    public  List<UserLiked> getUserLiked(Integer userID);

}
