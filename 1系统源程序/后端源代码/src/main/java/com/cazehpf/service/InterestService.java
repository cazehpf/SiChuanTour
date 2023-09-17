package com.cazehpf.service;

import com.cazehpf.entity.*;

import java.util.List;

/**
 * author:hpf
 **/
public interface InterestService {
    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    InterestsPageBean page(Integer page, Integer pageSize ,String name,
                           List<String> level,
                           String  city,
                           String county);
    /**
     * 分页查询2
     * @param page
     * @param pageSize
     * @return
     */
    InterestsPageBean pageUser(Integer page, Integer pageSize ,String name,
                           List<String> level,
                           String  city,
                           String county,
                               Integer userID
    );
    //获取所有景点信息
    List<InterestFilter>  getInterests();
    /**
     * 分页查询3
     * @param page
     * @param pageSize
     * @return
     */
    InterestsPageBean pageInterestComment(Integer page, Integer pageSize ,
                               Integer userID,Integer interestID
    );

    /**
     * 根据景点ID查询景点信息
     * @param interestID
     * @return
     */
    Interest getByID(Integer interestID);

    /**
     * 获取景点ID对应的平均评分
     * @param interestID
     * @return
     */
    Float getAverageScore(Integer interestID);

    void addUserCollect(Integer collectUserID,Integer collectInterestID);
    void deleteUserCollect(Integer collectUserID,Integer collectInterestID);

    UserCollect getUserCollect(Integer collectUserID,Integer collectInterestID);
    List<InterestTop> getInterestTop();
}
