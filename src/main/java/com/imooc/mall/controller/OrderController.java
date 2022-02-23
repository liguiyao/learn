package com.imooc.mall.controller;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.common.ApiRestResponse;
import com.imooc.mall.model.request.CreateOrderReq;
import com.imooc.mall.model.vo.OrderVO;
import com.imooc.mall.service.OrderService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class OrderController {
    @Autowired
    OrderService orderService;

    @ApiOperation("创建订单")
    @PostMapping("/order/create")
    public ApiRestResponse create(@Valid @RequestBody CreateOrderReq createOrderReq) {
        String orderNum = orderService.create(createOrderReq);
        return ApiRestResponse.success(orderNum);
    }
    @ApiOperation("订单详情")
    @PostMapping("/order/detail")
    public ApiRestResponse detail(@RequestParam String orderNo) {
        OrderVO detail = orderService.detail(orderNo);
        return ApiRestResponse.success(detail);
    }
    @ApiOperation("订单列表")
    @PostMapping("/order/list")
    public ApiRestResponse list(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageInfo pageInfo = orderService.listForCustomer(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }


}
