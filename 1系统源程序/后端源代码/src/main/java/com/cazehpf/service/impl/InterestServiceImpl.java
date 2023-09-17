package com.cazehpf.service.impl;/**
 * author:hpf
 **/

import com.cazehpf.entity.*;
import com.cazehpf.mapper.InterestMapper;
import com.cazehpf.service.InterestService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 功能描述
 *
 * @author: hpf
 * @date: 2023年09月06日 17:35
 */
@Service
public class InterestServiceImpl implements InterestService {
    @Autowired
    private InterestMapper interestMapper;

    @Override
    public InterestsPageBean page(Integer page, Integer pageSize,String name, List<String> level, String  city, String county) {
        //1. 设置分页参数
        PageHelper.startPage(page,pageSize);

        //2. 执行查询
        List<Interest> interestList = interestMapper.list(name, level, city, county);
        Page<Interest> p = (Page<Interest>) interestList;

        //3. 封装PageBean对象
        InterestsPageBean interestsPageBean = new InterestsPageBean(p.getTotal(), p.getResult());
        return interestsPageBean;
    }

    @Override
   public InterestsPageBean pageUser(Integer page, Integer pageSize ,String name,
                               List<String> level,
                               String  city,
                               String county,
                               Integer userID
    ){
        //1. 设置分页参数
        PageHelper.startPage(page,pageSize);

        //2. 执行查询
        List<Interest> interestList = interestMapper.listByUser(name, level, city, county,userID);
        Page<Interest> p = (Page<Interest>) interestList;

        //3. 封装PageBean对象
        InterestsPageBean interestsPageBean = new InterestsPageBean(p.getTotal(), p.getResult());
        return interestsPageBean;
    }

    @Override
    public List<InterestFilter> getInterests() {
        return interestMapper.selectInterests();
    }

    @Override
    public    InterestsPageBean pageInterestComment(Integer page, Integer pageSize ,
                                                    Integer userID,Integer interestID
    ){
        //1. 设置分页参数
        PageHelper.startPage(page,pageSize);

        //2. 执行查询
        List<InterestComment> interestCommentList = interestMapper.getCommentsByIDs(userID,interestID);
        Page<InterestComment> p = (Page<InterestComment>) interestCommentList;

        //3. 封装PageBean对象
        InterestsPageBean interestsPageBean = new InterestsPageBean(p.getTotal(), p.getResult());
        return interestsPageBean;
    }
    @Override
    public  Interest getByID(Integer interestID){
        return  interestMapper.getByID(interestID);


    }
    @Override
    /**
     * 获取景点ID对应的平均评分
     * @param interestID
     * @return
     */
   public Float getAverageScore(Integer interestID){
      return interestMapper.getAverageScore(interestID);
    }

    @Override
    public void addUserCollect(Integer collectUserID, Integer collectInterestID) {

        interestMapper.addUserCollect(collectUserID,collectInterestID);
    }

    @Override
    public void deleteUserCollect(Integer collectUserID, Integer collectInterestID) {
        interestMapper.deleteUserCollect(collectUserID,collectInterestID);

    }

    @Override
    public UserCollect getUserCollect(Integer collectUserID, Integer collectInterestID) {
        return  interestMapper.getUserCollect(collectUserID,collectInterestID);
    }

    @Override
    public List<InterestTop> getInterestTop() {
        return interestMapper.getInterestTop();
    }
}
