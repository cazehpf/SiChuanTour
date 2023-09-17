package com.cazehpf.entity;/**
 * author:hpf
 **/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 功能描述
 *
 * @author: hpf
 * @date: 2023年09月12日 10:45
 */
@Data
@NoArgsConstructor //无参构造
@AllArgsConstructor //全参构造
public class Comment {
    Integer commentID;
    LocalDateTime commentTime;
    Integer commentInterestID;
    String commentContent;
    String commentUserID;
    String commentScore;
}
