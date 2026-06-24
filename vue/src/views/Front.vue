<template>
  <div>
    
    <!--头部-->
    <div class="front-header">
      <div class="front-header-left"  @click="navTo('/front/home')">
        <img src="@/assets/imgs/logo1.png"  style="border-radius: 50%" />
        <div class="title">小胡甄选优品</div>
      </div>
      <div class="front-header-center" style="text-align: right">
        <el-input style="width: 200px; flex-shrink: 0" placeholder="请输入商品名称" v-model="name"></el-input>
        <el-button type="primary" style="margin-left: 5px; flex-shrink: 0" @click="search">搜索</el-button>
     </div>
      <div class="front-header-right">
        <div v-if="!user.username">
          <el-button @click="$router.push('/login')">登录</el-button>
          <el-button @click="$router.push('/register')">注册</el-button>
        </div>
        <div v-else>
          <el-dropdown>
            <div class="front-header-dropdown">
              <img @click="navTo('/front/person')" :src="user.avatar" alt="" />
              <div style="margin-left: 10px; white-space: nowrap">
                <span>{{ user.name }}</span
                ><i class="el-icon-arrow-down" style="margin-left: 5px"></i>
              </div>
            </div>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item>
                <div style="text-decoration: none" @click="navTo('/front/cart')">我的购物车</div>
              </el-dropdown-item>
              <el-dropdown-item>
                <div style="text-decoration: none" @click="navTo('/front/collect')">我的收藏</div>
              </el-dropdown-item>
              <el-dropdown-item>
                <div style="text-decoration: none" @click="navTo('/front/address')">我的地址</div>
              </el-dropdown-item>
              <el-dropdown-item>
                <div style="text-decoration: none" @click="navTo('/front/orders')">我的订单</div>
              </el-dropdown-item>
              <el-dropdown-item>
                <div style="text-decoration: none" @click="logout">退出登录</div>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </div>
    <!--主体-->
    <div class="main-body">
      <router-view ref="child" @update:user="updateUser" />
    </div>

    <!-- 右下角悬浮客服按钮 -->
    <div class="float-service">
      <img @click="navTo('/front/customer')" src="@/assets/imgs/客服.png"  alt="客服"  />
      <span>客服</span>
    </div>
  </div>
</template>

<script>
export default {
  name: "FrontLayout",

  data() {
    return {
      top: "",
      notice: [],
      name: null,
      user: JSON.parse(localStorage.getItem("xm-user") || "{}"),
    };
  },

  mounted() {
    this.loadNotice();
  },
  methods: {
    loadNotice() {
      this.$request.get("/notice/selectAll").then((res) => {
        this.notice = res.data;
        let i = 0;
        if (this.notice && this.notice.length) {
          this.top = this.notice[0].content;
          setInterval(() => {
            this.top = this.notice[i].content;
            i++;
            if (i === this.notice.length) {
              i = 0;
            }
          }, 2500);
        }
      });
    },
    updateUser() {
      this.user = JSON.parse(localStorage.getItem("xm-user") || "{}"); // 重新获取下用户的最新信息
    },
    navTo(url) {
      location.href = url;
    },
    // 退出登录
    logout() {
      localStorage.removeItem("xm-user");
      this.$router.push("/login");
    },
    search() {
      let name = this.name ? this.name : ''
      location.href = '/front/search?name=' + name
}
  },
};
</script>

<style scoped>
@import "@/assets/css/front.css";

.float-service {
  position: fixed;
  right: 30px;
  bottom: 80px;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 60px;
  height: 60px;
  background-color: #ffffff;
  border-radius: 50%;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  user-select: none;
}
.float-service:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 22px rgba(0, 0, 0, 0.2);
}
.float-service img {
  width: 32px;
  height: 32px;
  object-fit: contain;
}
.float-service span {
  font-size: 11px;
  color: #ff5000;
  margin-top: 2px;
  font-weight: 500;
}
</style>
