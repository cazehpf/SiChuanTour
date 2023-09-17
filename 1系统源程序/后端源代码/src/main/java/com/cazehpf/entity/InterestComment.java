package com.cazehpf.entity;/**
 * author:hpf
 **/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 功能描述
 *
 * @author: hpf
 * @date: 2023年09月11日 20:18
 */
@Data
@NoArgsConstructor //无参构造
@AllArgsConstructor //全参构造
public class InterestComment {
    Integer commentID;
    Integer commentInterestID;
    Integer count;
    Integer myLike;
   String  userName;
   String userImg;
   LocalDateTime commentTime;
   String commentContent;
   Integer commentScore;
}
