package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Cart;
import com.example.entity.Goods;
import com.example.entity.Orders;
import com.example.mapper.CartMapper;
import com.example.mapper.GoodsMapper;
import com.example.mapper.OrdersMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 订单业务处理
 **/
@Service
public class OrdersService {

    @Resource
    private OrdersMapper ordersMapper;
    @Resource
    private CartMapper cartMapper;
    @Resource
    private GoodsMapper goodsMapper;

    /**
     * 新增：每件商品生成独立 orderId（毫秒+两位序号），返回逗号拼接的所有 orderId
     */
    public String add(Orders orders) {
        String base = DateUtil.format(new Date(), "yyyyMMddHHmmssSSS");
        List<String> orderIds = new ArrayList<>();
        List<Cart> cartList = orders.getCartData();
        for (int i = 0; i < cartList.size(); i++) {
            Cart cart = cartList.get(i);
            String orderId = base + String.format("%02d", i + 1);
            orderIds.add(orderId);

            Orders dbOrders = new Orders();
            BeanUtils.copyProperties(orders, dbOrders);
            dbOrders.setOrderId(orderId);
            dbOrders.setGoodsId(cart.getGoodsId());
            dbOrders.setBusinessId(cart.getBusinessId());
            dbOrders.setNum(cart.getNum());
            dbOrders.setPrice(cart.getNum() * cart.getGoodsPrice());
            ordersMapper.insert(dbOrders);

            // 下单时立即删除购物车记录，取消支付后订单以"待付款"展示在我的订单中
            if (cart.getId() != null) {
                cartMapper.deleteById(cart.getId());
            }

            // 增加商品销量
            Goods goods = goodsMapper.selectById(cart.getGoodsId());
            if (goods != null) {
                goods.setCount((goods.getCount() == null ? 0 : goods.getCount()) + cart.getNum());
                goodsMapper.updateById(goods);
            }
        }
        // 返回逗号拼接的所有独立订单号
        return String.join(",", orderIds);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        ordersMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            ordersMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Orders orders) {
        ordersMapper.updateById(orders);
    }

    /**
     * 根据ID查询
     */
    public Orders selectById(Integer id) {
        return ordersMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Orders> selectAll(Orders orders) {
        return ordersMapper.selectAll(orders);
    }

    /**
     * 分页查询
     */
    public PageInfo<Orders> selectPage(Orders orders, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            orders.setUserId(currentUser.getId());
        }
        if (RoleEnum.BUSINESS.name().equals(currentUser.getRole())) {
            orders.setBusinessId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Orders> list = ordersMapper.selectAll(orders);
        return PageInfo.of(list);
    }

    /**
     * 支付宝支付成功：将逗号分隔的所有 orderId 状态更新为"待发货"
     */
    public void paySuccess(String orderIds) {
        for (String orderId : orderIds.split(",")) {
            ordersMapper.updateStatusByOrderId(orderId.trim(), "待发货");
        }
    }
}
