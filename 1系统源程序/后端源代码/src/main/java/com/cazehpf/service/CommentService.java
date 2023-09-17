package com.cazehpf.service;

import com.cazehpf.entity.Comment;

/**
 * author:hpf
 **/
public interface CommentService {
    /**
     * 发布评论
     * @param comment
     */
    void addComment(Comment comment);

    /**
     * 删除用户对某条评论的点赞
     * @param likeUserID
     * @param likeCommentID
     */
    void deleteUserLiked(Integer likeUserID,Integer likeCommentID);

    /**
     * 用户添加某条评论的点赞
     * @param likeUserID
     * @param likeCommentID
     */
    void addUserLiked(Integer likeUserID,Integer likeCommentID);

    /**
     * 举报评论
     * @param reportUserID
     * @param reportCommentID
     */
    void addUserReport(Integer reportUserID,Integer reportCommentID);

    /**
     * 删除指定ID评论及其点赞表中包含的记录
     * @param commentID
     */
    void deleteComment(Integer commentID);
}
