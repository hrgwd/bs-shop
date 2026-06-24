package com.example.service;

import com.example.entity.Account;
import com.example.entity.Cart;
import com.example.mapper.CartMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import com.example.common.enums.RoleEnum;

import cn.hutool.core.util.ObjectUtil;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 购物车信息表业务处理
 **/
@Service
public class CartService {

    @Resource
    private CartMapper cartMapper;

    /**
     * 新增
     */
    public void add(Cart cart) {
        // 检查用户是否已经将该商品添加到购物车，如果已经添加，则更新数量，否则插入新记录
        Cart dbCart = cartMapper.selectByUserIdAndGoodsId(cart.getUserId(), cart.getGoodsId());
        if (ObjectUtil.isNotEmpty(dbCart)) {
            dbCart.setNum(dbCart.getNum() + 1);
            cartMapper.updateById(dbCart);
        } else {
            cartMapper.insert(cart);
        }

    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        cartMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            cartMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Cart cart) {
        cartMapper.updateById(cart);
    }

    /**
     * 根据ID查询
     */
    public Cart selectById(Integer id) {
        return cartMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Cart> selectAll(Cart cart) {
        return cartMapper.selectAll(cart);
    }

    /**
     * 分页查询
     */
    public PageInfo<Cart> selectPage(Cart cart, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            cart.setUserId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Cart> list = cartMapper.selectAll(cart);
        return PageInfo.of(list);
    }

}