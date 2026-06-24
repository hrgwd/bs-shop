<template>
  <div>
    <div class="search">
      <el-input placeholder="请输入订单编号查询" style="width: 200px" v-model="orderId"></el-input>
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
    </div>

    <div class="operation">
      <el-button type="danger" plain @click="delBatch">批量删除</el-button>
    </div>

    <div class="table">
      <el-table :data="tableData" stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="id" label="序号" width="70" align="center" sortable></el-table-column>
        <el-table-column label="商品图片" width="80">
          <template v-slot="scope">
            <el-image style="width: 50px; height: 40px;" v-if="scope.row.goodsImg"
                      :src="scope.row.goodsImg" :preview-src-list="[scope.row.goodsImg]"></el-image>
          </template>
        </el-table-column>
        <el-table-column prop="orderId" label="订单编号" show-overflow-tooltip></el-table-column>
        <el-table-column prop="goodsName" label="商品名称" show-overflow-tooltip></el-table-column>
        <el-table-column prop="businessName" label="所属店铺" show-overflow-tooltip></el-table-column>
        <el-table-column label="商品单价" show-overflow-tooltip>
          <template v-slot="scope">
            {{ scope.row.goodsPrice }} / {{ scope.row.goodsUnit }}
          </template>
        </el-table-column>
        <el-table-column prop="num" label="商品数量" width="80"></el-table-column>
        <el-table-column prop="price" label="订单总价" width="90"></el-table-column>
        <el-table-column prop="username" label="收货人" show-overflow-tooltip></el-table-column>
        <el-table-column prop="phone" label="联系电话" show-overflow-tooltip></el-table-column>
        <el-table-column prop="useraddress" label="收货地址" show-overflow-tooltip></el-table-column>
        <el-table-column prop="status" label="订单状态" width="90"></el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template v-slot="scope">
            <el-button plain type="primary" size="mini" v-if="scope.row.status === '待发货'" @click="updateStatus(scope.row, '待收货')">发货</el-button>
            <el-button plain type="danger" size="mini" @click="del(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
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
</template>

<script>
export default {
  name: "Orders",
  data() {
    return {
      tableData: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      orderId: null, 
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      ids: []
    }
  },
  created() {
    this.load(1)
  },
  methods: {
    save() {   // 保存按钮触发的逻辑  它会触发新增或者更新
      this.$request({
        url: this.form.id ? '/orders/update' : '/orders/add',
        method: this.form.id ? 'PUT' : 'POST',
        data: this.form
      }).then(res => {
        if (res.code === '200') {  // 表示成功保存
          this.$message.success('更新状态成功')
          this.load()
          this.fromVisible = false
        } else {
          this.$message.error(res.msg)  // 弹出错误的信息
        }
      })
    },
    del(id) {
      this.$confirm('您确定删除吗？', '确认删除', { type: "warning" }).then(() => {
        this.$request.delete('/orders/delete/' + id).then(res => {
          if (res.code === '200') {
            this.$message.success('操作成功')
            this.load(1)
          } else {
            this.$message.error(res.msg)
          }
        })
      }).catch(() => {})
    },
    handleSelectionChange(rows) {
      this.ids = rows.map(v => v.id)
    },
    delBatch() {
      if (!this.ids.length) {
        this.$message.warning('请选择数据')
        return
      }
      this.$confirm('您确定批量删除这些数据吗？', '确认删除', { type: "warning" }).then(() => {
        this.$request.delete('/orders/delete/batch', { data: this.ids }).then(res => {
          if (res.code === '200') {
            this.$message.success('操作成功')
            this.load(1)
          } else {
            this.$message.error(res.msg)
          }
        })
      }).catch(() => {})
    },
    load(pageNum) {
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/orders/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          orderId: this.orderId,
        }
      }).then(res => {
        this.tableData = res.data?.list
        this.total = res.data?.total
      })
    },
    reset() {
      this.orderId = null
      this.load(1)
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
    updateStatus(row,status){
      this.form = row
      this.form.status = status
      this.save()
    },
  }
}
</script>

<style scoped>

</style>
