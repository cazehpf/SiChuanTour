package com.cazehpf.mapper;

import com.cazehpf.entity.Comment;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

/**
 * author:hpf
 **/
@Mapper
public interface CommentMapper {

    /**
     * 添加评论
     * @param comment
     */
    @Insert("insert into Comment(commentTime,commentInterestID,commentContent,commentUserID,commentScore)"+
    " values(#{commentTime},#{commentInterestID},#{commentContent},#{commentUserID},#{commentScore})"
    )
    public void addComment(Comment comment);

    /**
     * 2删除评论表指定ID 的评论
     * @param commentID
     */
    @Delete("delete from Comment where commentID=#{commentID} ")
    public void deleteComment(Integer commentID);

    /**
     * 1删除点赞表指定ID的点赞
     * @param commentID
     */
    @Delete("delete from Liked where likeCommentID=#{commentID}")
    public  void  deleteCommentLike(Integer commentID);

    /**
     * 当前用户删除指定comment的点赞
     * @param likeUserID
     * @param likeCommentID
     */
    @Delete("delete from Liked where likeUserID=#{likeUserID} and likeCommentID=#{likeCommentID}")
    public  void deleteUserLiked(Integer likeUserID,Integer likeCommentID);

    /**
     * 当前用户对指定comment的点赞
     * @param likeUserID
     * @param likeCommentID
     */
    @Insert("insert into Liked(likeUserID,likeCommentID,likeTime) values(#{likeUserID},#{likeCommentID},#{localDateTime})")
    public  void addUserLiked(Integer likeUserID, Integer likeCommentID, LocalDateTime localDateTime);

    /**
     * 举报评论
     * @param reportUserID
     * @param reportCommentID
     * @param localDateTime
     */
    @Insert("insert into Report(reportUserID,reportCommentID,reportTime) values(#{reportUserID},#{reportCommentID},#{localDateTime})")
    public  void addUserReport(Integer reportUserID, Integer reportCommentID, LocalDateTime localDateTime);
}
