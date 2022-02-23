package com.imooc.mall.controller;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.common.ApiRestResponse;
import com.imooc.mall.common.Constant;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.pojo.Category;
import com.imooc.mall.model.pojo.User;
import com.imooc.mall.model.request.AddCategoryReq;
import com.imooc.mall.model.request.UpdateCategoryReq;
import com.imooc.mall.model.vo.CategoryVO;
import com.imooc.mall.service.CategoryService;
import com.imooc.mall.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@ApiOperation("后台添加目录")
@RestController
public class CategoryController {
    @Autowired
    UserService userService;
    @Autowired
    CategoryService categoryService;

    @PostMapping("/admin/category/add")
    public ApiRestResponse addCategory(HttpSession session, @Valid @RequestBody AddCategoryReq addCategoryReq) {
        User currentUser = (User) session.getAttribute(Constant.IMOOC_MALL_USER);
        if (currentUser == null) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_LOGIN);
        }
        boolean adminRole = userService.checkAdminRole(currentUser);
        if (adminRole) {
            categoryService.add(addCategoryReq);
            return ApiRestResponse.success();
        } else {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_ADMIN);
        }
    }
    @ApiOperation("后台更新目录")
    @PostMapping("/admin/category/update")
    public ApiRestResponse updateCategory(HttpSession session,@Valid @RequestBody UpdateCategoryReq updateCategoryReq){
        User currentUser = (User) session.getAttribute(Constant.IMOOC_MALL_USER);
        if (currentUser == null) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_LOGIN);
        }
        boolean adminRole = userService.checkAdminRole(currentUser);
        if (adminRole) {
            Category category = new Category();
            BeanUtils.copyProperties(updateCategoryReq, category);
            categoryService.update(category);
            return ApiRestResponse.success();
        } else {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_ADMIN);
        }
    }
    @ApiOperation("后台删除目录")
    @PostMapping("/admin/category/delete")
    public ApiRestResponse deleteCategory(@RequestParam Integer id){
        categoryService.delete(id);
        return ApiRestResponse.success();
    }
    @ApiOperation("后端管理员分页查询")
    @PostMapping("/admin/category/list")
    public ApiRestResponse listCategoryForAdmin(@RequestParam Integer pageNum,@RequestParam Integer pageSize) {
        PageInfo pageInfo = categoryService.listForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }
    @ApiOperation("前台目录列表")
    @PostMapping("/category/listForCustomer")
    public ApiRestResponse listCategoryForCustomer(){
        List<CategoryVO> categoryVOS = categoryService.listForCustomer(0);
        return ApiRestResponse.success(categoryVOS);
    }



}
