<template>
  <div class="main-content">
    <div style="width: 70%; background-color: white; margin: 30px auto; border-radius: 20px">
      <div style="padding-bottom: 10px">
        <div style="display: flex; font-size: 18px; color: #000000FF; line-height: 80px; border-bottom: #cccccc 1px solid;">
          <div style="flex: 1; margin-left: 20px">我的购物车（{{ goodsData.length }}件）</div>
          <div style="flex: 2; text-align: right;">
          <el-select v-model="addressId" placeholder="请选择收货地址" style="width: 60%">
            <el-option v-for="item in addressData" :key="item.id" :label="item.username + '--' + item.useraddress + '--' + item.phone" :value="item.id"></el-option>
          </el-select>
        </div>
          <div style="flex: 1; font-size: 16px; text-align: right; padding-right: 30px;">
            已选商品 <span style="color: red; font-weight: bold;">￥ {{totalPrice}}</span> <el-button type="danger" round @click="pay">下单</el-button>
          </div>
        </div>
        <div style="margin: 20px 0; padding: 0 50px ">
          <div class="table">
            <el-table :data="goodsData" strip @selection-change="handleSelectionChange" >
            <el-table-column type="selection" width="55" align="center"></el-table-column>
              <el-table-column label="商品图片" width="120px">
                <template v-slot="scope">
                  <el-image style="width: 80px; height: 60px; border-radius: 3px" v-if="scope.row.goodsImg"
                            :src="scope.row.goodsImg" :preview-src-list="[scope.row.goodsImg]"></el-image>
                </template>
              </el-table-column>
              <el-table-column prop="goodsName" label="商品名称" width="200px">
                <template v-slot="scope">
                  <a :href="'/front/detail?id=' + scope.row.goodsId">{{ scope.row.goodsName }}</a>
                </template>
              </el-table-column>
              <el-table-column prop="businessName" label="店铺名称">
                <template v-slot="scope">
                  <a :href="'/front/business?id=' + scope.row.businessId">{{ scope.row.businessName }}</a>
                </template>
              </el-table-column>
              <el-table-column label="商品价格">
                <template v-slot="scope">
                  {{ scope.row.goodsPrice }} / {{ scope.row.goodsUnit }}
                </template>
              </el-table-column>
             <el-table-column prop="num" label="选择数量">
                <template v-slot= "scope">
                  <el-input-number v-model="scope.row.num" style="width: 100px;" @change="handleChange(scope.row)" :min="1" ></el-input-number>
                </template>
             </el-table-column>
              <el-table-column label="操作" align="center" width="180">
                <template v-slot="scope">
                  <el-button size="mini" type="danger" plain @click="del(scope.row.id)">移除购物车</el-button>
                </template>
              </el-table-column>
            </el-table>

            <div class="pagination" style="margin-top: 20px">
              <el-pagination
                  background
                  @current-change="handleCurrentChange"
                  :current-page="pageNum"
                  :page-sizes="[5, 10, 20]"
                  :page-size="pageSize"
                  layout="total, prev, pager, next"
                  :total="total">
              </el-pagination>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

export default {

  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      addressData: [],
      addressId: null,
      goodsData: [],
      selectData: [],
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      totalPrice: 0,
    }
  },
  mounted() {
    this.loadGoods(1)
    this.loadaddress()
    document.addEventListener('visibilitychange', this.handleVisibilityChange)
    window.addEventListener('pageshow', this.handlePageShow)
  },
  beforeDestroy() {
    document.removeEventListener('visibilitychange', this.handleVisibilityChange)
    window.removeEventListener('pageshow', this.handlePageShow)
  },
  // methods：本页面所有的点击事件或者其他函数定义区
  methods: {
    handleVisibilityChange() {

      if (document.visibilityState === 'visible') {
        this.loadGoods(1)
        this.selectData = []
        this.totalPrice = 0
      }
    },
    handlePageShow(e) {
      if (e.persisted) {
        this.loadGoods(1)
        this.selectData = []
        this.totalPrice = 0
      }
    },
    loadaddress(){
      this.$request.get('/address/selectAll').then(res =>{
        if(res.code === '200'){
          this.addressData = res.data
        }else {
          this.$message.error(res.msg)
        }
      })
    },
    loadGoods(pageNum) {
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/cart/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
        }
      }).then(res => {
        if (res.code === '200') {
          this.goodsData = res.data?.list
          this.total = res.data?.total
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    navTo(url) {
      location.href = url
    },
    del(id) {
      this.$request.delete('/cart/delete/' + id).then(res => {
        if (res.code === '200') {
          this.$message.success('删除成功')
          this.loadGoods(1)
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleCurrentChange(pageNum) {
      this.loadGoods(pageNum)
    },
    handleSelectionChange(rows){
      this.totalPrice = 0
      this.selectData = rows
      this.selectData.forEach(item => {
        this.totalPrice += item.goodsPrice * item.num
      })
    },
    handleChange(row){
      this.totalPrice = 0
      this.selectData.forEach(item => {
      this.totalPrice += item.goodsPrice * item.num
      })
    },
    pay(){
      if(!this.addressId){
        this.$message.warning('请选择收货地址')
        return
      }
      if(!this.selectData  || this.selectData.length === 0){
        this.$message.warning('请选择要购买的商品')
        return
      }
      let data = {
        userId: this.user.id,
        addressId: this.addressId,
        cartData: this.selectData,
        status:"待付款"
      }
      this.$request.post('/orders/add', data).then(res => {
        if (res.code === '200') {
          const orderIds = res.data  // 逗号拼接的独立订单号
          // 下单后立即刷新购物车（后端已删购物车记录），再跳转支付
          this.loadGoods(1)
          this.selectData = []
          this.totalPrice = 0
          window.location.href = `http://localhost:9090/alipay/pay?orderIds=${encodeURIComponent(orderIds)}`
        } else {
          this.$message.error(res.msg)
        }
      })
    },
  }
}
</script>