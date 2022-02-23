package com.imooc.mall.service.impl;

import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.dao.UserMapper;
import com.imooc.mall.model.pojo.User;
import com.imooc.mall.service.UserService;
import com.imooc.mall.utils.MD5Utils;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;

    @Override
    public User getUser() {
        return userMapper.selectByPrimaryKey(1);
    }

    @Override
    public void register(String userName, String password) throws ImoocMallException, NoSuchAlgorithmException {
        //查询用户名是否存在
        User result = userMapper.selectByName(userName);
        if (result != null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
        }
        //信息写到数据库
        User user = new User();
        user.setUsername(userName);
//        user.setPassword(password);
        user.setPassword(MD5Utils.getMD5Str(password));
        int count = userMapper.insertSelective(user);
        if (count == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.INSERT_FAILED);
        }
    }
    @Override
    public User login(String userName, String password){
        String md5Password = null;
        try {
            md5Password = MD5Utils.getMD5Str(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        User user = userMapper.selectLogin(userName, md5Password);
        if (user == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.WRONG_PASSWORD);
        }
        return user;
    }
    @Override
    public void updateInfo(User user) {
        int count = userMapper.updateByPrimaryKeySelective(user);
        if (count > 1) {
            throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
        }
    }
    @Override
    public boolean checkAdminRole(User user){
        return user.getRole().equals(2);
    }
}
