package com.exception;/**
 * author:hpf
 **/

/**
 * 功能描述
 *
 * @author: hpf
 * @date: 2023年08月30日 12:16
 */

import com.cazehpf.entity.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)//捕获所有异常
    public Result ex(Exception ex){
        ex.printStackTrace();
        return Result.errorParam("对不起,操作失败,请联系管理员");
    }

}
