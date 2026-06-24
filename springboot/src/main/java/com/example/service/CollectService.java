package com.example.service;

import com.example.entity.Account;
import com.example.entity.Collect;
import com.example.exception.CustomException;
import com.example.mapper.CollectMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;

import cn.hutool.core.util.ObjectUtil;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * 收藏信息表业务处理
 **/
@Service
public class CollectService {

    @Resource
    private CollectMapper collectMapper;

    /**
     * 新增
     */
    public void add(Collect collect) {
        // 检查用户是否重复收藏同一商品
        Collect dbcollect = collectMapper.selectByUserIdAndGoodsId(collect.getUserId(), collect.getGoodsId());
        if (ObjectUtil.isNotEmpty(dbcollect )) {
            throw new CustomException(ResultCodeEnum.COLLECT_EXIST_ERROR);
        }
        collectMapper.insert(collect);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        collectMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            collectMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Collect collect) {
        collectMapper.updateById(collect);
    }

    /**
     * 根据ID查询
     */
    public Collect selectById(Integer id) {
        return collectMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Collect> selectAll(Collect collect) {
        return collectMapper.selectAll(collect);
    }

    /**
     * 分页查询
     */
    public PageInfo<Collect> selectPage(Collect collect, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            collect.setUserId(currentUser.getId()); 
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Collect> list = collectMapper.selectAll(collect);
        return PageInfo.of(list);
    }

}