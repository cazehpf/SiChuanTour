package com.cazehpf.controller;/**
 * author:hpf
 **/

import com.cazehpf.entity.Comment;
import com.cazehpf.entity.Result;

import com.cazehpf.service.impl.CommentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 功能描述
 *
 * @author: hpf
 * @date: 2023年09月12日 10:56
 */
@Slf4j
@RestController
public class CommentController {
    @Autowired
    private CommentServiceImpl commentService;
    /**
     * 添加评论(注册)
     * @return
     */
    @PostMapping("/addComment")
    public Result addComment(@RequestBody Comment comment){
        System.out.println(comment);
        if(comment.getCommentContent().length()>16000) return Result.errorParam("评论过大");
        log.info("新发布评论: {}" , comment);
        //调用service新增用户
        commentService.addComment(comment);
        return Result.success();
    }

    /**
     * 取消点赞
     * @param likeUserID
     * @param likeCommentID
     * @return
     */
    @DeleteMapping("/deleteUserLiked")
    public Result deleteUserLiked(@RequestParam Integer likeUserID,@RequestParam Integer likeCommentID){
        commentService.deleteUserLiked(likeUserID,likeCommentID);
        return Result.success();
    }

    /**
     * 删除评论，事务处理
     * @param commentID
     * @return
     */
    @DeleteMapping("/deleteComment")
    public Result deleteComment(@RequestParam Integer commentID){
        commentService.deleteComment(commentID);
        return Result.success();
    }

    /**
     * 点赞
     * @param likeUserID
     * @param likeCommentID
     * @return
     */
    @PostMapping("/addUserLiked")
    public Result addUserLiked(@RequestParam Integer likeUserID,@RequestParam Integer likeCommentID){
        commentService.addUserLiked(likeUserID,likeCommentID);
        return Result.success();
    }

    @PostMapping("/addUserReport")
    public Result addUserReport(@RequestParam Integer reportUserID,@RequestParam Integer reportCommentID){
        commentService.addUserReport(reportUserID,reportCommentID);
        return Result.success();
    }
}
