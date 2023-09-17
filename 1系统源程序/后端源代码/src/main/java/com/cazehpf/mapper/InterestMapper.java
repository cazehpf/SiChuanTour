package com.cazehpf.mapper;

import com.cazehpf.entity.*;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * author:hpf
 **/
@Mapper
public interface InterestMapper {
    /**
     * 查询总记录数
     * @return
     */
    @Select("select count(*) from Interest")
    public Long count();

    /**
     * 获取景点ID对应的平均评分
     * @param interestID
     * @return
     */
    @Select("select AVG( commentScore)  as averageScore from `Comment` where commentInterestID=#{interestID}")
    public Float getAverageScore(Integer interestID);
    /**
     * 根据ID查询景点信息
     * @param interestID
     * @return
     */
    @Select("select * from Interest where InterestID=#{interestID}")
    public  Interest getByID(Integer interestID);


    /**
     * 查询总记录数2,其实就是查询该景点ID下的评论数
     * @return
     */
    @Select("select count(*) from Comment where commentInterestID=#{interestID}")
    public Long countInterestComment(Integer interestID);

    //获取所有景点
    @Select("select interestName as value from Interest ")
    public  List<InterestFilter> selectInterests();

    /**
     * 根据景点ID、当前用户ID查询景点评论、评论用户、点赞等信息
     * @param interestID
     * @param userID
     * @return
     */
    public  List<InterestComment> getCommentsByIDs( Integer interestID,Integer userID);


    /**
     * 查询评论top10景点
     * @return
     */
    public List<InterestTop> getInterestTop();
//    @Select("select * from Interest limit #{start},#{pageSize}")
//    public List<Interest> page(Integer start, Integer pageSize);

    /**
     * 分页条件查询,获取列表数据
     * @param name 景点名字模糊查询
     * @param level 景点等级
     * @param city 景点所属城市
     * @param county 景点所属区县
     * @return
     */
    public List<Interest> list(String name, List<String> level, String  city, String county);

    /**
     * 分页条件查询，查询用户的收藏景点
     * @param name
     * @param level
     * @param city
     * @param county
     * @param userID
     * @return
     */
    public List<Interest> listByUser(String name, List<String> level, String  city, String county,Integer userID);



    /**
     * 取消收藏
     * @param collectUserID
     * @param collectInterestID
     */
    @Delete("delete from Collect where collectUserID=#{collectUserID} and collectInterestID=#{collectInterestID}")
    public  void deleteUserCollect(Integer collectUserID,Integer collectInterestID);

    /**
     * 收藏
     * @param collectUserID
     * @param collectInterestID
     */
    @Insert("insert into Collect(collectUserID,collectInterestID) values(#{collectUserID},#{collectInterestID})")
    public  void addUserCollect(Integer collectUserID, Integer collectInterestID);

    @Select("select * from Collect where collectUserID=#{collectUserID} and collectInterestID=#{collectInterestID}")
    public UserCollect getUserCollect(Integer collectUserID, Integer collectInterestID);
}
