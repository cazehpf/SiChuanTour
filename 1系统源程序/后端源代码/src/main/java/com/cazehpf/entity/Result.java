package com.cazehpf.entity;/**
 * author:hpf
 **/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 功能描述
 *
 * @author: hpf
 * @date: 2023年08月26日 20:48
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private Integer code;//响应码，200 代表成功; 400 代表参数错误;401代表权限不足;
    private String msg;  //响应信息 描述字符串
    private Object data; //返回的数据

    //增删改 成功响应
    public static Result success(){
        return new Result(200,"success",null);
    }
    //查询 成功响应
    public static Result success(Object data){
        return new Result(200,"success",data);
    }
    //失败响应
    public static Result errorParam(String msg){
        return new Result(400,msg,null);
    }
    //没有权限响应
    public static Result errorAuthority(String msg){
        return new Result(401,msg,null);
    }


}
