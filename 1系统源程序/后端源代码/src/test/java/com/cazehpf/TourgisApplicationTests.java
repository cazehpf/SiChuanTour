package com.cazehpf;

import com.cazehpf.entity.User;
import com.cazehpf.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class TourgisApplicationTests {

    @Autowired
    private UserMapper userMapper;




    //新增用户
    @Test
    public void testInsert() {
        //构造用户对象
        User user = new User();
        user.setUserName("Tom");
        user.setUserPSW("1234");
        //执行新增用户信息操作
        userMapper.insert(user);
        System.out.println(user.getUserID());
    }
}
