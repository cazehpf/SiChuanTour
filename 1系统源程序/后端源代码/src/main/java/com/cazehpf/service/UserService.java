package com.cazehpf.service;

import com.cazehpf.entity.InterestsPageBean;
import com.cazehpf.entity.User;
import com.cazehpf.entity.UserComment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * author:hpf
 **/
public interface UserService {

    /**
     * 根据ID查询用户的所有信息
     * @return
     */
    User selectByID(Integer userID);

    /**
     * 根据用户ID查询用户获赞数目
     * @param userID
     * @return
     */
    Integer selectLikedNumByID(Integer userID);
    /**
     * 新增用户
     * @param user
     */
    void add(User user);

    /**
     * 根据用户名和密码查询用户
     * @param name
     * @param password
     * @return
     */
    User getByNameAndPassword(String name,String password);
    /**
     * 根据用户名查询用户
     * @param name
     * @return
     */
    User getByName(String name);

    /**
     * 根据邮箱查询用户
     * @param mail
     * @return
     */
    User getByMail(String mail);

    void setUserByID(User user);

    InterestsPageBean pageUserComment(Integer page, Integer pageSize, LocalDate startTime, LocalDate endTime, String interestFilter, Integer userID);
    InterestsPageBean pageUserLiked(Integer page, Integer pageSize,Integer userID);

}
