package com.cazehpf.service.impl;/**
 * author:hpf
 **/

import com.cazehpf.entity.InterestsPageBean;
import com.cazehpf.entity.User;
import com.cazehpf.entity.UserComment;
import com.cazehpf.entity.UserLiked;
import com.cazehpf.mapper.UserMapper;
import com.cazehpf.service.UserService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 功能描述
 *
 * @author: hpf
 * @date: 2023年08月26日 20:41
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User selectByID(Integer userID) {
        return userMapper.selectByID(userID);
    }

    @Override
    public Integer selectLikedNumByID(Integer userID) {
        return userMapper.selectLikedNumByID(userID);
    }

    @Override
    public void add(User user) {
        user.setCreateTime(LocalDateTime.now());
        userMapper.insert(user);
    }

    @Override
    public void setUserByID(User user) {
        userMapper.setUserByID(user);
    }

    @Override
    public InterestsPageBean pageUserComment(Integer page, Integer pageSize, LocalDate startTime, LocalDate endTime, String interestFilter, Integer userID) {
        //1. 设置分页参数
        PageHelper.startPage(page,pageSize);

        //2. 执行查询
        List<UserComment> userCommentList= userMapper.getUserComments(startTime,endTime,interestFilter,userID);
        Page<UserComment> p = (Page<UserComment>) userCommentList;

        //3. 封装PageBean对象
        InterestsPageBean interestsPageBean = new InterestsPageBean(p.getTotal(), p.getResult());
        return interestsPageBean;
    }

    @Override
    public InterestsPageBean pageUserLiked(Integer page, Integer pageSize, Integer userID) {
        //1. 设置分页参数
        PageHelper.startPage(page,pageSize);

        //2. 执行查询
        List<UserLiked> userLikedList= userMapper.getUserLiked(userID);
        Page<UserLiked> p = (Page<UserLiked>) userLikedList;

        //3. 封装PageBean对象
        InterestsPageBean interestsPageBean = new InterestsPageBean(p.getTotal(), p.getResult());
        return interestsPageBean;
    }


    @Override
    public User getByNameAndPassword(String name, String password) {
        return userMapper.selectByNamePSW(name, password);
    }

    @Override
    public User getByName(String name) {
        return userMapper.selectByName(name);
    }

    @Override
    public User getByMail(String mail) {
        return userMapper.selectByMail(mail);
    }
}
