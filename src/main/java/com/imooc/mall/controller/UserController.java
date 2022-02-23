package com.imooc.mall.controller;

import com.imooc.mall.common.ApiRestResponse;
import com.imooc.mall.common.Constant;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.pojo.User;
import com.imooc.mall.service.UserService;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @ResponseBody
    @GetMapping("/test")
    public User personalPage(){
        return userService.getUser();
    }

    @ResponseBody
    @PostMapping("/register")
    public ApiRestResponse register(@RequestParam("userName") String userName,@RequestParam("password") String password) throws ImoocMallException, NoSuchAlgorithmException {
        if (StringUtils.isNullOrEmpty(userName)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isNullOrEmpty(password)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_PASSWORD);
        }
        if (password.length() < 8) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.PASSWORD_TOO_SHORT);
        }
        userService.register(userName, password);
        return ApiRestResponse.success();
    }
    @PostMapping("/login")
    @ResponseBody
    public ApiRestResponse login(@RequestParam("userName") String userName, @RequestParam("password") String password, HttpSession session) {
        if (StringUtils.isNullOrEmpty(userName)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isNullOrEmpty(password)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_PASSWORD);
        }
        User user = userService.login(userName, password);
        user.setPassword(null);
        session.setAttribute(Constant.IMOOC_MALL_USER, user);
        return ApiRestResponse.success(user);
    }
    @PostMapping("/update")
    @ResponseBody
    public ApiRestResponse updateSign(@RequestParam String signature, HttpSession session) {
        User currentUser = (User) session.getAttribute(Constant.IMOOC_MALL_USER);
        if (currentUser == null) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_LOGIN);
        }
        User user = new User();
        user.setPersonalizedSignature(signature);
        user.setId(currentUser.getId());
        userService.updateInfo(user);
        return ApiRestResponse.success();
    }

    @PostMapping("/logout")
    @ResponseBody
    public ApiRestResponse logout(HttpSession session){
        session.removeAttribute(Constant.IMOOC_MALL_USER);
        return ApiRestResponse.success();
    }

    @PostMapping("/adminLogin")
    @ResponseBody
    public ApiRestResponse adminLogin(@RequestParam("userName") String userName, @RequestParam("password") String password, HttpSession session) {
        if (StringUtils.isNullOrEmpty(userName)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isNullOrEmpty(password)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_PASSWORD);
        }
        User user = userService.login(userName, password);
        boolean adminRole = userService.checkAdminRole(user);
        if (adminRole) {
            user.setPassword(null);
            session.setAttribute(Constant.IMOOC_MALL_USER, user);
            return ApiRestResponse.success(user);
        } else {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_ADMIN);
        }
    }


}
