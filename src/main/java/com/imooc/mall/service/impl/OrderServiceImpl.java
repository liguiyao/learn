package com.imooc.mall.service.impl;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.common.Constant;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.filter.UserFilter;
import com.imooc.mall.model.dao.CartMapper;
import com.imooc.mall.model.dao.OrderItemMapper;
import com.imooc.mall.model.dao.OrderMapper;
import com.imooc.mall.model.dao.ProductMapper;
import com.imooc.mall.model.pojo.Order;
import com.imooc.mall.model.pojo.OrderItem;
import com.imooc.mall.model.pojo.Product;
import com.imooc.mall.model.request.CreateOrderReq;
import com.imooc.mall.model.vo.CartVO;
import com.imooc.mall.model.vo.OrderItemVO;
import com.imooc.mall.model.vo.OrderVO;
import com.imooc.mall.service.CartService;
import com.imooc.mall.service.OrderService;
import com.imooc.mall.utils.OrderCodeFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    CartService cartService;
    @Autowired
    ProductMapper productMapper;
    @Autowired
    CartMapper cartMapper;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    OrderItemMapper orderItemMapper;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public String create(CreateOrderReq createOrderReq) {
        //拿到用户id
        Integer userId = UserFilter.currentUser.getId();
        //从购物车查找已勾选的商品
        List<CartVO> cartVOList = cartService.list(userId);
        ArrayList<CartVO> cartVOTemp = new ArrayList<>();
        //如果购物车已勾选的为空，报错
        for (CartVO cartVO : cartVOList) {
            if (cartVO.getSelected().equals(Constant.Cart.CHECKED)) {
                cartVOTemp.add(cartVO);
            }
        }
        cartVOList = cartVOTemp;
        if (CollectionUtils.isEmpty(cartVOList)) {
            throw new ImoocMallException(ImoocMallExceptionEnum.EMPTY_CART);
        }
        //判断商品是否存在、上下架状态，库存
        validSaleStatusAndStock(cartVOList);
        //把购物车对象转换为订单item对象
        List<OrderItem> orderItemList = cartVOListToOrderItemList(cartVOList);
        //扣库存
        for (OrderItem orderItem : orderItemList) {
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            int stock = product.getStock() - orderItem.getQuantity();
            if (stock < 0) {
                throw new ImoocMallException(ImoocMallExceptionEnum.NOT_STOCK);
            }
            product.setStock(stock);
            productMapper.updateByPrimaryKeySelective(product);
        }
        //把购物车中已勾选的商品删除
        clearCart(cartVOList);
        //生成订单
        Order order = new Order();
        //生成订单号，有独立的规则
        String orderNum = OrderCodeFactory.getOrderCode(Long.valueOf(userId));
        order.setOrderNo(orderNum);
        order.setUserId(userId);
        order.setTotalPrice(totalPrice(orderItemList));
        order.setReceiverName(createOrderReq.getReceiverName());
        order.setReceiverAddress(createOrderReq.getReceiverAddress());
        order.setReceiverMobile(createOrderReq.getReceiverMobile());
        order.setOrderStatus(Constant.OrderStatusEnum.NOT_PAID.getCode());
        order.setPostage(0);
        order.setPaymentType(1);
        //插入到order表
        orderMapper.insertSelective(order);
        //循环保存每个商品到order_item表
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderNo(order.getOrderNo());
            orderItemMapper.insertSelective(orderItem);
        }
        //返回结果
        return orderNum;
    }
    @Override
    public OrderVO detail(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NO_ORDER);
        }
        //判断订单是否属于当前user
        Integer id = UserFilter.currentUser.getId();
        if (!order.getUserId().equals(id)) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NOT_U_ORDER);
        }
        return getOrderVO(order);
    }
    @Override
    public PageInfo listForCustomer(Integer pageNum, Integer pageSize) {
        Integer userId = UserFilter.currentUser.getId();
        List<Order> orders = orderMapper.selectForCustomer(userId);
        List<OrderVO> orderVOList = orderListToOrderVOList(orders);
        PageInfo pageInfo = new PageInfo(orders);
        pageInfo.setList(orderVOList);
        return pageInfo;
    }

    public void cancelOrder(String orderNo) {

    }

    private List<OrderVO> orderListToOrderVOList(List<Order> orderList) {
        List<OrderVO> orderVOS = new ArrayList<>();
        for (int i = 0; i < orderList.size(); i++) {
            orderVOS.add(getOrderVO(orderList.get(i)));
        }
        return orderVOS;
    }

    private OrderVO getOrderVO(Order order) {
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        List<OrderItem> orderItems = orderItemMapper.selectByOrderNo(order.getOrderNo());
        List<OrderItemVO> orderItemVOS = new ArrayList<>();
        for (int i = 0; i < orderItems.size(); i++) {
            OrderItemVO orderItemVO = new OrderItemVO();
            BeanUtils.copyProperties(orderItems.get(i), orderItemVO);
            orderItemVOS.add(orderItemVO);
        }
        orderVO.setOrderItemVOList(orderItemVOS);
        orderVO.setOrderStatusName(Constant.OrderStatusEnum.codeOf(orderVO.getOrderStatus()).getValue());
        return orderVO;
    }

    private void validSaleStatusAndStock(List<CartVO> cartVOList) {
        for (CartVO cartVO : cartVOList) {
            Product product = productMapper.selectByPrimaryKey(cartVO.getProductId());
            if (product == null || product.getStatus().equals(Constant.SaleStatus.NOT_SALE)) {
                throw new ImoocMallException(ImoocMallExceptionEnum.NOT_SALE);
            }
            if (cartVO.getQuantity() > product.getStock()) {
                throw new ImoocMallException(ImoocMallExceptionEnum.NOT_STOCK);
            }
        }
    }

    private List<OrderItem> cartVOListToOrderItemList(List<CartVO> cartVOList) {
        ArrayList<OrderItem> orderItemList = new ArrayList<>();
        for (CartVO cartVO : cartVOList) {
            OrderItem item = new OrderItem();
            item.setProductId(cartVO.getProductId());
            item.setProductName(cartVO.getProductName());
            item.setProductImg(cartVO.getProductImage());
            item.setUnitPrice(cartVO.getPrice());
            item.setQuantity(cartVO.getQuantity());
            item.setTotalPrice(cartVO.getTotalPrice());
            orderItemList.add(item);
        }
        return orderItemList;
    }

    private void clearCart(List<CartVO> cartVOList) {
        for (CartVO cartVO : cartVOList) {
            cartMapper.deleteByPrimaryKey(cartVO.getId());
        }
    }

    private Integer totalPrice(List<OrderItem> orderItemList) {
        Integer totalPrice = 0;
        for (OrderItem orderItem : orderItemList) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }
}
