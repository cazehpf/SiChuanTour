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
 * @date: 2023年09月13日 12:21
 */
@Data
@NoArgsConstructor //无参构造
@AllArgsConstructor //全参构造
public class UserCollect {
    Integer collectID;
    Integer collectUserID;
    Integer collectInterestID;
}
