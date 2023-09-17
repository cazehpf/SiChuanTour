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
 * @date: 2023年09月12日 14:32
 */
@Data
@NoArgsConstructor //无参构造
@AllArgsConstructor //全参构造
public class UserComment {
    Integer commentID;
    Integer commentInterestID;
    Integer commentUserID;
    Integer count;
    LocalDateTime commentTime;
    String commentContent;
    Integer commentScore;
    String interestName;
}
