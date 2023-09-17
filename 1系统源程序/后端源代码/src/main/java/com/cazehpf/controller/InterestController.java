package com.cazehpf.controller;/**
 * author:hpf
 **/

import com.cazehpf.entity.*;
import com.cazehpf.service.InterestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 功能描述
 *
 * @author: hpf
 * @date: 2023年09月06日 17:40
 */
@Slf4j
@RestController
public class InterestController {
    @Autowired
    private InterestService interestService;

    //获取前10评论的景点
    @GetMapping("/interestTop")
    public  Result getInterestTop(){
        List<InterestTop> interestTops=interestService.getInterestTop();
        return  Result.success(interestTops);


    }

    /**
     * 获取所有景点数据
     * @param page
     * @param pageSize
     * @param name
     * @param level
     * @param city
     * @param county
     * @return
     */
    @GetMapping("/interestPage")
    public Result page(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "6") Integer pageSize,
                       @RequestParam(defaultValue = "") String name,
                       @RequestParam( value = "level" ,required=false) List<String> level,
                       @RequestParam(defaultValue = "")String  city,
                       @RequestParam(defaultValue = "")String county
    ) {
        log.info("分页查询, 参数: {},{},{},{},{},{}", page, pageSize, name,  level,  city, county);
        //调用service分页查询
        InterestsPageBean interestsPageBean = interestService.page(page, pageSize, name,  level,  city, county);
        return Result.success(interestsPageBean);
    }
    @GetMapping("/interests")
    public Result getInterests(){

        List<InterestFilter> interestList =interestService.getInterests();
        return Result.success(interestList);
    }
    /**
     * 获取用户收藏景点并分页展示
     * @param userID
     * @return
     */
    @GetMapping("/userCollect")
    public Result getUserCollect(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "6") Integer pageSize,
            @RequestParam(defaultValue = "") String name,
            @RequestParam( value = "level" ,required=false) List<String> level,
            @RequestParam(defaultValue = "")String  city,
            @RequestParam(defaultValue = "")String county,
            @RequestParam Integer userID){
        log.info("分页查询, 参数: {},{},{},{},{},{},{}", page, pageSize, name,  level,  city, county,userID);
        //调用service分页查询
        InterestsPageBean interestsPageBean = interestService.pageUser(page, pageSize, name,  level,  city, county,userID);
        return Result.success(interestsPageBean);
    }

    /**
     * 根据景点ID和用户ID查询详细评论
     * @param page
     * @param pageSize
     * @param interestID
     * @param userID
     * @return
     */
    @GetMapping("/interestComment")
    public Result getInterestComment(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "6") Integer pageSize,
            @RequestParam Integer interestID,
            @RequestParam Integer userID){
        //调用service分页查询
        InterestsPageBean interestsPageBean = interestService.pageInterestComment(page, pageSize, interestID,userID);
        return Result.success(interestsPageBean);
    }

    /**
     * 根据ID获取景点信息
     * @param interestID
     * @return
     */
    @GetMapping("/getInterestInfo")
    public Result getInterestInfo(@RequestParam Integer interestID) {
        Interest interest = interestService.getByID(interestID);

        return Result.success(interest);
    }
    /**
     * 根据ID获取景点信息
     * @param interestID
     * @return
     */
    @GetMapping("/averageScore")
    public Result getAverageScore(@RequestParam Integer interestID) {
       Float averageScore = interestService.getAverageScore(interestID);

        return Result.success(averageScore);
    }

    /**
     * 获取用户是否收藏
     * @param collectUserID
     * @param collectInterestID
     * @return
     */
    @GetMapping("/isUserCollect")
    public Result getUserCollect(@RequestParam Integer collectUserID,@RequestParam Integer collectInterestID){
      UserCollect userCollect= interestService.getUserCollect(collectUserID,collectInterestID);
        if(userCollect==null){
            return Result.errorParam("没有收藏");
        }
      return Result.success("已收藏");
    }

    /**
     * 添加收藏
     * @param collectUserID
     * @param collectInterestID
     * @return
     */
    @PostMapping("/addUserCollect")
    public Result addUserCollect(@RequestParam Integer collectUserID,@RequestParam Integer collectInterestID){
        interestService.addUserCollect(collectUserID,collectInterestID);
        return Result.success();
    }


    /**
     * 取消收藏
     * @param collectUserID
     * @param collectInterestID
     * @return
     */
    @DeleteMapping("/deleteUserCollect")
    public Result deleteUserCollect(@RequestParam Integer collectUserID,@RequestParam Integer collectInterestID){
        interestService.deleteUserCollect(collectUserID,collectInterestID);
        return Result.success();
    }
}
