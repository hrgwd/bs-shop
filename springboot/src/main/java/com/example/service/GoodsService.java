package com.example.service;

import com.example.entity.Goods;
import com.example.entity.Orders;
import com.example.entity.RelateDTO;
import com.example.entity.User;
import com.example.mapper.CartMapper;
import com.example.mapper.CollectMapper;
import com.example.mapper.CommentMapper;
import com.example.mapper.GoodsMapper;
import com.example.mapper.OrdersMapper;
import com.example.mapper.UserMapper;
import com.example.utils.TokenUtils;
import com.example.utils.UserCF;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import org.springframework.stereotype.Service;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Cart;
import com.example.entity.Collect;
import com.example.entity.Comment;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import java.util.Set;
import java.util.stream.Collectors;



@Service
public class GoodsService {

    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private CartMapper cartMapper;
    @Resource
    private CollectMapper collectMapper;
    @Resource
    private OrdersMapper ordersMapper;
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private UserMapper userMapper;
   
    /**
     * 新增
     */
    public void add(Goods goods) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.BUSINESS.name().equals(currentUser.getRole())) {
            goods.setBusinessId(currentUser.getId());
        }
        goodsMapper.insert(goods);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        goodsMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            goodsMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Goods goods) {
        goodsMapper.updateById(goods);
    }

    /**
     * 根据ID查询
     */
    public Goods selectById(Integer id) {
        return goodsMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Goods> selectAll(Goods goods) {
        return goodsMapper.selectAll(goods);
    }

    /**
     * 分页查询
     */
    public PageInfo<Goods> selectPage(Goods goods, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.BUSINESS.name().equals(currentUser.getRole())) {
             goods.setBusinessId(currentUser.getId());
   }
        PageHelper.startPage(pageNum, pageSize);
        List<Goods> list = goodsMapper.selectAll(goods);
        return PageInfo.of(list);
    }

    /**
     * 查询热门商品Top15
     */
    public List<Goods> selectTop15() {
        return goodsMapper.selectTop15();
    }

    /**
     * 根据分类ID查询商品
     */
    public List<Goods> selectByTypeId(Integer id) {
        return goodsMapper.selectByTypeId(id);
    }

    /**
     * 根据商家ID查询商品
     */
    public List<Goods> selectByBusinessId(Integer id) {
        return goodsMapper.selectByBusinessId(id);
    }

    /**
     * 根据商品名称模糊查询
     */
    public List<Goods> selectByName(String name) {
        return goodsMapper.selectByName(name);
    }

    /**
     * 根据商品推荐查询
     */
    public List<Goods> recommend(){
        
        Account currentUser = TokenUtils.getCurrentUser();
        if (ObjectUtil.isEmpty(currentUser)) {
            // 没有用户登录，直接返回空推荐结果
            return new ArrayList<>();
        }
        
       //用户的下单，收藏，加入购物车，评论等行为都会认为它跟商品产生了关系
       
       //1.获取所有的收藏信息
        List<Collect> allCollects = collectMapper.selectAll(null) ;
       //2.获取所有的购物车信息
        List<Cart> allCarts = cartMapper.selectAll(null) ;
       //3.获取所有的订单信息
        List<Orders> allOrders = ordersMapper.selectAllOkOrders(null) ;
       //4.获取所有的评论信息
        List<Comment> allComments = commentMapper.selectAll(null) ;
       //5.获取所有的用户信息
        List<User> allUsers = userMapper.selectAll(null) ;
       //6.获取所有的商品信息
        List<Goods> allGoods = goodsMapper.selectAll(null) ;
        

        // 定义一个存储每个商品和每个用户关系的List
        List<RelateDTO> data = new ArrayList<>();

        // 开始计算每个商品和每个用户之间的关系数据
        for (Goods goods : allGoods) {
            Integer goodsId = goods.getId();
            for (User user : allUsers) {
                Integer userId = user.getId();
                int index = 1;
                // 1. 判断该用户有没有收藏该商品，收藏的权重我们给 2
                Optional<Collect> collectOptional = allCollects.stream().filter(x -> x.getGoodsId().equals(goodsId) && x.getUserId().equals(userId)).findFirst();
                if (collectOptional.isPresent()) {
                    index += 2;
                }
                // 2. 判断该用户有没有将该商品加入购物车，加入购物车的权重我们给 1
                Optional<Cart> cartOptional = allCarts.stream().filter(x -> x.getGoodsId().equals(goodsId) && x.getUserId().equals(userId)).findFirst();
                if (cartOptional.isPresent()) {
                index += 1;
                }
                // 3. 判断该用户有没有对该商品下过单（已完成的订单），订单的权重我们给 3
                Optional<Orders> ordersOptional = allOrders.stream().filter(x -> x.getGoodsId().equals(goodsId) && x.getUserId().equals(userId)).findFirst();
                if (ordersOptional.isPresent()) {
                    index += 3;
                }
                // 4. 判断该用户有没有对该商品评论过，评论的权重我们给 2
                Optional<Comment> commentOptional = allComments.stream().filter(x -> x.getGoodsId().equals(goodsId) && x.getUserId().equals(userId)).findFirst();
                if (commentOptional.isPresent()) {
                    index += 2;
                }
                if (index > 1) {
                    RelateDTO relateDTO = new RelateDTO(userId, goodsId, index);
                    data.add(relateDTO);
                }
            }
        }

        //数据准备结束后，将这些数据喂给推荐算法
        List<Integer> goodsIds = UserCF.recommend(currentUser.getId(), data);
        // 过滤掉null，避免推荐结果中包含无效商品
        List<Goods> recommendResult = goodsIds.stream()
            .map(goodsId -> allGoods.stream()
                .filter(x -> x.getId().equals(goodsId)).findFirst().orElse(null))
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());

        if (CollectionUtil.isEmpty(recommendResult)) {
            // 算法无结果，直接随机补5个
            return getRandomGoods(5, new ArrayList<>());
        }
        if (recommendResult.size() < 5) {
            // 不足5个，用随机商品补足，排除已推荐的商品避免重复
            int num = 5 - recommendResult.size();
            List<Goods> fillList = getRandomGoods(num, recommendResult);
            recommendResult.addAll(fillList);
        }
        return recommendResult;
    }
    
    private List<Goods> getRandomGoods(int num, List<Goods> excludeList) {
        List<Goods> allGoods = goodsMapper.selectAll(null);
        if (CollectionUtil.isEmpty(allGoods)) {
            return new ArrayList<>();
        }
        // 排除已有商品，避免重复
        Set<Integer> excludeIds = excludeList.stream()
            .map(Goods::getId).collect(Collectors.toSet());
        List<Goods> candidates = allGoods.stream()
            .filter(g -> !excludeIds.contains(g.getId()))
            .collect(Collectors.toList());
        // 打乱后取前 num 个，彻底避免重复
        Collections.shuffle(candidates);
        return candidates.stream().limit(num).collect(Collectors.toList());
    }

}