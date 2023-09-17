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
 * @date: 2023年08月26日 19:11
 */
@Data
@NoArgsConstructor //无参构造
@AllArgsConstructor //全参构造
public class User {
    private Integer userID;//用户id
    private String userName;//用户名
    private String userPSW;//密码
    private String userMail;//邮箱
    private String userImg;//头像
    private LocalDateTime createTime; //创建时间
    private String signature;//签名
    private Integer sex;//性别
    private String address;//地址
    private  String hobby;//爱好
    private  String tes;//电话
    private Integer age;//年龄
    private  String work;//工作
    private  String tel;//电话

//    public User() {
//    }
//
//    public User(Integer id, String name) {
//        this.UserID = id;
//        this.UserName = name;
//
//    }
//
//    public Integer getUserID() {
//        return UserID;
//    }
//
//    public void setUserID(Integer userID) {
//        this.UserID = userID;
//    }
//
//    public String getUserName() {
//        return UserName;
//    }
//
//    public void setUserName(String userName) {
//        this.UserName = userName;
//    }
//
//
//
//
//    @Override
//    public String toString() {
//        return "User{" +
//                "id=" + UserID +
//                ", name='" + UserName + '\'' +
//                '}';
//    }

}
