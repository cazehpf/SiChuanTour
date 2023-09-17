package com.cazehpf.service.impl;/**
 * author:hpf
 **/

import com.cazehpf.entity.Comment;
import com.cazehpf.mapper.CommentMapper;
import com.cazehpf.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 功能描述
 *
 * @author: hpf
 * @date: 2023年09月12日 11:05
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    CommentMapper commentMapper;

    @Override
    public void addComment(Comment comment) {
        comment.setCommentTime(LocalDateTime.now());
        commentMapper.addComment(comment);
    }

    @Override
    public void deleteUserLiked(Integer likeUserID, Integer likeCommentID) {
        commentMapper.deleteUserLiked(likeUserID, likeCommentID);
    }

    @Override
    public void addUserLiked(Integer likeUserID, Integer likeCommentID) {
        LocalDateTime localDateTime = LocalDateTime.now();
        commentMapper.addUserLiked(likeUserID, likeCommentID, localDateTime);
    }

    @Override
    public void addUserReport(Integer reportUserID, Integer reportCommentID) {
        LocalDateTime localDateTime = LocalDateTime.now();
        commentMapper.addUserReport(reportUserID, reportCommentID, localDateTime);
    }

    @Override
    @Transactional //删除事务
    public void deleteComment(Integer commentID) {
        //1首先删除点赞表里面的所有记录
        commentMapper.deleteCommentLike(commentID);
        //2再删除评论表中的该条评论
        commentMapper.deleteComment(commentID);

    }
}
