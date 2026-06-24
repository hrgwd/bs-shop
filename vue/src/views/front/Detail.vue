<template>
  <div class="main-content">
    <div style="width: 60%; background-color: white; min-height: 1000px; margin: 20px auto; border-radius: 20px">
      <div style="padding: 15px 20px">
        <el-row :gutter="20">
          <el-col :span="12">
            <img :src="goodsData.img" alt="" style="width: 100%; height: 400px; border-radius: 20px">
          </el-col>
          <el-col :span="12">
            <div style="font-size: 20px; font-weight: 900; overflow: hidden; text-overflow: ellipsis; display: -webkit-box; -webkit-line-clamp: 2; line-clamp: 2; -webkit-box-orient: vertical;">{{goodsData.name}}</div>
            <div style="color: #666666FF; margin-top: 5px">销量：{{goodsData.count}}</div>
            <div style="color: red; margin-top: 15px">疯抢价：<span style="font-size: 20px">{{goodsData.price}} / {{goodsData.unit}}</span></div>
            <div style="margin-top: 20px">
              <img src="@/assets/imgs/right.png" alt="" style="width: 70%; height: 130px; border-radius: 15px">
            </div>
            <div style="color: #666666FF; margin-top: 20px">商家：<a href="#" @click="navTo('/front/business?id=' + goodsData.businessId)" >{{goodsData.businessName}}</a></div>
            <div style="color: #666666FF; margin-top: 20px">分类：<a href="#" @click="navTo('/front/type?id=' + goodsData.typeId)">{{goodsData.typeName}}</a></div>
            <div style="color: #666666FF; margin-top: 20px">
              <el-button type="warning" @click="addcart">加入购物车</el-button>
              <el-button type="danger" @click="openBuyDialog">立即购买</el-button>
              <el-button type="warning" @click="collect">收藏</el-button>
            </div>
          </el-col>
        </el-row>
      </div>
      <div style="padding: 15px 20px">
        <el-tabs v-model="activeName" @tab-click="handleClick">
          <el-tab-pane label="宝贝详情" name="first">
            <div style="padding: 10px 175px" v-html="goodsData.description"></div>
          </el-tab-pane>
          <el-tab-pane label="宝贝评价" name="second">
            <div style="margin-top: 10px">
              <div style="margin-top: 20px" v-for="item in commentData" :key="item.id">
                <div style="display: flex">
                  <div style="width: 40px">
                    <img :src="item.userAvatar" alt="" style="height: 40px; width: 40px; border-radius: 50%">
                  </div>
                  <div style="width: 200px; margin-left: 10px">
                    <div style="font-weight: 700; font-size: 17px; color: #000000FF">{{item.userName}}</div>
                    <div style="color: #7A7A7AFF">{{item.time}}</div>
                  </div>
                </div>
                <div style="margin-top: 15px; font-size: 16px">{{item.content}}</div>
              </div>
            </div>
         </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <!-- 立即购买弹窗 -->
    <el-dialog title="立即购买" :visible.sync="buyDialogVisible" width="450px" :close-on-click-modal="false" destroy-on-close>
      <el-form label-width="90px">
        <el-form-item label="商品名称">
          <span>{{ goodsData.name }}</span>
        </el-form-item>
        <el-form-item label="单价">
          <span style="color: red; font-weight: bold;">￥{{ goodsData.price }} / {{ goodsData.unit }}</span>
        </el-form-item>
        <el-form-item label="购买数量">
          <el-input-number v-model="buyNum" :min="1" :max="999" style="width: 150px"></el-input-number>
        </el-form-item>
        <el-form-item label="合计金额">
          <span style="color: red; font-weight: bold; font-size: 18px;">￥{{ (goodsData.price * buyNum).toFixed(2) }}</span>
        </el-form-item>
        <el-form-item label="收货地址">
          <el-select v-model="buyAddressId" placeholder="请选择收货地址" style="width: 100%">
            <el-option
              v-for="item in addressData"
              :key="item.id"
              :label="item.username + ' -- ' + item.useraddress + ' -- ' + item.phone"
              :value="item.id">
            </el-option>
          </el-select>
          <div v-if="addressData.length === 0" style="color: #999; font-size: 12px; margin-top: 4px">
            暂无收货地址，请先前往
            <a href="/front/address" style="color: #409EFF">个人中心</a>
            添加地址
          </div>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="buyDialogVisible = false">取 消</el-button>
        <el-button type="danger" @click="submitBuy" :loading="buyLoading">去支付</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>

export default {

  data() {
    let goodsId = this.$route.query.id
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      goodsId: goodsId,
      goodsData: {},
      activeName: 'first',
      commentData: [],
      // 立即购买相关
      buyDialogVisible: false,
      buyNum: 1,
      buyAddressId: null,
      addressData: [],
      buyLoading: false,
    }
  },
  mounted() {
    this.loadGoods()
    this.loadComments()
    this.loadAddress()
  },
  methods: {
    loadGoods() {
      this.$request.get('/goods/selectById?id=' + this.goodsId).then(res => {
        if (res.code === '200') {
          this.goodsData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    loadAddress() {
      this.$request.get('/address/selectAll').then(res => {
        if (res.code === '200') {
          this.addressData = res.data || []
        }
      })
    },
    navTo(url){
      location.href = url
    },
    addcart(){
      let data = {
        userId: this.user.id,
        businessId: this.goodsData.businessId,
        goodsId: this.goodsId,
        num: 1
      }
      this.$request.post('/cart/add', data).then(res => {
        if (res.code === '200') {
          this.$message.success('加入购物车成功')
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    collect(){
      let data = {
        userId: this.user.id,
        businessId: this.goodsData.businessId,
        goodsId: this.goodsId,
      }
      this.$request.post('/collect/add', data).then(res => {
        if (res.code === '200') {
          this.$message.success('收藏成功')
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleClick(tab) {
      this.activeName = tab.name
    },
    loadComments() {
      this.$request.get('/comment/selectByGoodsId?id=' + this.goodsId).then(res => {
        if (res.code === '200') {
          this.commentData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    openBuyDialog() {
      if (!this.user || !this.user.id) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
        return
      }
      this.buyNum = 1
      this.buyAddressId = this.addressData.length > 0 ? this.addressData[0].id : null
      this.buyDialogVisible = true
    },
    submitBuy() {
      if (!this.buyAddressId) {
        this.$message.warning('请选择收货地址')
        return
      }
      this.buyLoading = true
      // 构造与购物车下单完全相同的数据结构
      const cartData = [{
        goodsId: Number(this.goodsId),
        businessId: this.goodsData.businessId,
        goodsPrice: this.goodsData.price,
        num: this.buyNum,
        id: null   // 直接购买无购物车记录id，后端 cartMapper.deleteById(null) 不会报错
      }]
      const data = {
        userId: this.user.id,
        addressId: this.buyAddressId,
        cartData: cartData,
        status: '待付款'
      }
      this.$request.post('/orders/add', data).then(res => {
        this.buyLoading = false
        if (res.code === '200') {
          const orderIds = res.data
          this.buyDialogVisible = false
          window.location.href = `http://localhost:9090/alipay/pay?orderIds=${encodeURIComponent(orderIds)}`
        } else {
          this.$message.error(res.msg)
        }
      }).catch(() => {
        this.buyLoading = false
      })
    }
  }
}
</script>
