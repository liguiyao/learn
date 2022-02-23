package com.imooc.mall.controller;

import com.imooc.mall.common.ApiRestResponse;
import com.imooc.mall.filter.UserFilter;
import com.imooc.mall.model.vo.CartVO;
import com.imooc.mall.service.CartService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    CartService cartService;

    @ApiOperation("购物车添加")
    @PostMapping("/add")
    public ApiRestResponse add(@RequestParam Integer productId, @RequestParam Integer count) {
        Integer userId = UserFilter.currentUser.getId();
        List<CartVO> add = cartService.add(userId, productId, count);
        return ApiRestResponse.success(add);
    }

    @ApiOperation("购物车列表")
    @GetMapping("/list")
    public ApiRestResponse list() {
        List<CartVO> list = cartService.list(UserFilter.currentUser.getId());
        return ApiRestResponse.success(list);
    }

    @ApiOperation("购物车更新")
    @PostMapping("/update")
    public ApiRestResponse update(@RequestParam Integer productId, @RequestParam Integer count) {
        List<CartVO> list = cartService.update(UserFilter.currentUser.getId(), productId, count);
        return ApiRestResponse.success(list);
    }

    @PostMapping("/delete")
    public ApiRestResponse delete(@RequestParam Integer productId) {
        List<CartVO> delete = cartService.delete(UserFilter.currentUser.getId(), productId);
        return ApiRestResponse.success(delete);
    }

    @ApiOperation("选中/不选中购物车")
    @PostMapping("/select")
    public ApiRestResponse select(@RequestParam Integer productId, @RequestParam Integer selected) {
        List<CartVO> cartVOS = cartService.selectOrNot(UserFilter.currentUser.getId(), productId, selected);
        return ApiRestResponse.success(cartVOS);
    }

    @ApiOperation("全选、全不选购物车")
    @PostMapping("/selectAll")
    public ApiRestResponse selectAll(@RequestParam Integer selected) {
        List<CartVO> cartVOS = cartService.selectAll(UserFilter.currentUser.getId(), selected);
        return ApiRestResponse.success(cartVOS);
    }


}
