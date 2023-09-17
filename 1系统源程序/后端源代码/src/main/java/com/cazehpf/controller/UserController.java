package com.cazehpf.controller;/**
 * author:hpf
 **/

import com.cazehpf.entity.InterestsPageBean;
import com.cazehpf.entity.Result;
import com.cazehpf.entity.User;
import com.cazehpf.entity.UserVo;
import com.cazehpf.service.UserService;
import com.cazehpf.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能描述
 *
 * @author: hpf
 * @date: 2023年08月26日 20:38
 */
@Slf4j
@RestController
//@CrossOrigin
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 查询所有用户
     * @return
     */
    //@RequestMapping(value = "/depts",method = RequestMethod.GET) //指定请求方式为GET
    @GetMapping("/user")
    public Result getUserInfo(@RequestParam Integer userID){
        log.info("根源ID查询用户所有信息");
        //调用service查询部门数据
        User user =  userService.selectByID(userID);
        return Result.success(user);
    }

    /**
     * 获取用户收藏数目
     * @param userID
     * @return
     */
    @GetMapping("/userLikedNum")
    public Result getUserLikedNum(@RequestParam Integer userID){
        log.info("根源ID查询用户获赞数");
        //调用service查询部门数据
        Integer likedNum =  userService.selectLikedNumByID(userID);
        return Result.success(likedNum);
    }

    /**
     * 用户验证
     * 解决这一问题——如果我在浏览器的控制台输入sessionStorage.setItem("token","123")，
     * 就可以跳过前置守卫，访问首页。这里我是这样解决的，在前置守卫，如果判断存在token的话，就请求后端的接口，
     * 这样就经过了jwt拦截器的验证，如果请求成功，前端就next()，不然就next("/login")到登录页
     * @return result
     */
    @PostMapping("/authentication")
    public Result authentication(){
        return Result.success();
    }

    /**
     * 解决注册时同一用户名的问题。鼠标焦点离开后进行验证
     * @param userName
     * @return
     */
    @GetMapping("/checkName")
    public Result countByUserName(@RequestParam String userName) {
        User userDB = userService.getByName(userName);
        if(userDB!=null){
            return Result.errorParam("用户名已存在");
        }
        return Result.success();
    }
    /**
     * 解决注册时同一用户名的问题。鼠标焦点离开后进行验证
     * @param userMail
     * @return
     */
    @GetMapping("/checkMail")
    public Result countByUserMail(@RequestParam String userMail) {
        User userDB = userService.getByMail(userMail);
        if(userDB!=null){
            return Result.errorParam("邮箱已存在");
        }
        return Result.success();
    }
    /**
     * 用户登录
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody User user){
        String name = user.getUserName();
        String password = user.getUserPSW();
        User userDB = userService.getByNameAndPassword(name,password);
        if(userDB==null){
            return Result.errorParam("昵称或密码错误！");
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("userID", userDB.getUserID());
        claims.put("userName", userDB.getUserName());
        claims.put("userPSW", userDB.getUserPSW());

        String jwt = JwtUtils.generateJwt(claims); //jwt包含了当前登录的员工信息
        UserVo userVo = new UserVo();
        userVo.setUserName(userDB.getUserName());
        userVo.setUserID(userDB.getUserID());
        userVo.setUserPSW(userDB.getUserPSW());
        userVo.setUserImg(userDB.getUserImg());

        userVo.setToken(jwt);
        return Result.success(userVo);
    }
    /**
     * 新增用户(注册)
     * @return
     */
    @PostMapping("/register")
    public Result add(@RequestBody User user){
        System.out.println(user);

        log.info("新增用户: {}" , user);
        //调用service新增用户
        userService.add(user);
        return Result.success();
    }

    /**
     * 根据传进来的User的ID修改其对应的记录项
     * @param user
     * @return
     */
    @PutMapping("/modifyUser")
public Result modifyUser(@RequestBody User user){
    System.out.println(user);

    log.info("修改用户: {}" , user);
    //调用service修改用户
    userService.setUserByID(user);
    return Result.success();
}

    @GetMapping("/getUserInfo")
    public Result getUserInfo(@RequestParam String userName) {
        User userDB = userService.getByName(userName);
        if(userDB==null){
            return Result.errorParam("sessionStorage内容被篡改");
        }
        return Result.success(userDB);
    }

    /**
     * 分页查询用户评论
     * @param page
     * @param pageSize
     * @param startTime
     * @param endTime
     * @param interestFilter
     * @param userID
     * @return
     */
    @GetMapping("/userComment")
    public Result getUserComment(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "6") Integer pageSize,
            @RequestParam(required = false)  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startTime,
            @RequestParam(required = false)  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endTime,

            @RequestParam(defaultValue = "")String interestFilter,
            @RequestParam Integer userID){
        if (startTime == null) {
            // 如果参数为空，则设置默认值为当前时间
            startTime = LocalDate.of(2000, 1, 1);;
        }
        if (endTime == null) {
            // 如果参数为空，则设置默认值为当前时间
            endTime = LocalDate.of(2100, 1, 1);;
        }
        //调用service分页查询
        InterestsPageBean interestsPageBean = userService.pageUserComment(page, pageSize, startTime,endTime,interestFilter,userID);
        return Result.success(interestsPageBean);
    }
    @GetMapping("/userLiked")
    public Result getUserLiked(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "6") Integer pageSize,

            @RequestParam Integer userID){

        //调用service分页查询
        InterestsPageBean interestsPageBean = userService.pageUserLiked(page, pageSize,userID);
        return Result.success(interestsPageBean);
    }
}
