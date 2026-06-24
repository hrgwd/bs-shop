package com.example.mapper;

import com.example.entity.Orders;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 操作orders相关数据接口
*/
public interface OrdersMapper {

    /**
      * 新增
    */
    int insert(Orders orders);

    /**
      * 删除
    */
    int deleteById(Integer id);

    /**
      * 修改
    */
    int updateById(Orders orders);

    /**
      * 根据ID查询
    */
    Orders selectById(Integer id);

    /**
      * 查询所有
    */
    List<Orders> selectAll(Orders orders);

    @Select("select * from orders where status = '已完成' or '已评价' ")
    List<Orders> selectAllOkOrders(String status);

    /**
     * 查询用户最近购买的 N 条订单（含商品信息）
     */
    List<Orders> selectRecentByUserId(@Param("userId") Integer userId, @Param("limit") Integer limit);

    /**
     * 根据订单号更新订单状态（支付宝回调使用）
     */
    @org.apache.ibatis.annotations.Update("update orders set status = #{status} where order_id = #{orderId}")
    void updateStatusByOrderId(@Param("orderId") String orderId, @Param("status") String status);

    /**
     * 根据订单号前缀批量更新状态（同批次所有订单）
     */
    @org.apache.ibatis.annotations.Update("update orders set status = #{status} where order_id like concat(#{prefix}, '%')")
    void updateStatusByOrderIdPrefix(@Param("prefix") String prefix, @Param("status") String status);

    /**
     * 根据订单号前缀 + userId 批量更新状态（精确限定同批次同用户，避免跨用户误更新）
     */
    @org.apache.ibatis.annotations.Update("update orders set status = #{status} where order_id like concat(#{prefix}, '%') and user_id = #{userId}")
    void updateStatusByOrderIdPrefixAndUserId(@Param("prefix") String prefix, @Param("userId") Integer userId, @Param("status") String status);

    /**
     * 根据订单号查询订单列表
     */
    @Select("select * from orders where order_id = #{orderId}")
    List<Orders> selectByOrderId(@Param("orderId") String orderId);

}