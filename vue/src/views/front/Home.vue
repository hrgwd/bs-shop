<template>
  <div class="main-content">
    <div style="height: 60px;"></div>
    <div style="display: flex; flex-wrap: nowrap; min-width: 1280px">
      <div class="left"></div>
      <div style="width: 66%; flex-shrink: 0; background-color: white; margin-bottom: 50px">
        <div style="color: #FE0137FF; margin: 15px 0 15px 18px; font-weight: bold; font-size: 16px">
          主题市场
        </div>

        <div style="display: flex; margin: 0 25px; height: 550px">
          <!-- 左侧分类 -->
          <div style="flex: 2">
            <div
              style="display: flex; color: #666666FF; margin: 14px 0"
              v-for="item in typeData"
              :key="item.id"
            >
              <img :src="item.img" alt="" style="height: 20px; width: 20px">
              <div style="margin-left: 10px; font-size: 14px">
                <a href="#" @click="navTo('/front/type?id=' + item.id)">{{ item.name }}</a>
              </div>
            </div>
          </div>

          <!-- 中间轮播 -->
          <div style="flex: 5; margin-top: 15px">
            <div>
              <el-carousel height="300px" style="border-radius: 10px">
                <el-carousel-item v-for="(item, index) in carousel_top" :key="index">
                  <img :src="item" alt="" style="width: 100%; height: 300px; border-radius: 10px">
                </el-carousel-item>
              </el-carousel>
            </div>

            <div style="margin-top: 30px; display: flex">
              <div style="flex: 1">
                <el-carousel height="300px" style="border-radius: 10px">
                  <el-carousel-item v-for="(item, index) in carousel_left" :key="index">
                    <img :src="item" alt="" style="width: 100%; height: 200px; border-radius: 10px">
                  </el-carousel-item>
                </el-carousel>
              </div>

              <div style="flex: 1; margin-left: 5px">
                <el-carousel height="300px" style="border-radius: 10px">
                  <el-carousel-item v-for="(item, index) in carousel_right" :key="index">
                    <img :src="item" alt="" style="width: 100%; height: 200px; border-radius: 10px">
                  </el-carousel-item>
                </el-carousel>
              </div>
            </div>
          </div>

          <!-- 右侧用户信息 -->
          <div style="flex: 3; background-color: #F3F3F3FF; margin-top: 15px; margin-left: 15px; border-radius: 10px">
            <div style="text-align: center; margin-top: 30px">
              <img
                @click="navTo('/front/person')"
                :src="user.avatar"
                alt=""
                style="width: 80px; height: 80px; border-radius: 50%"
              >
              <div style="margin-top: 10px">Hi，{{ user.name }}</div>
            </div>

            <div style="margin-top: 20px; padding: 0 15px">
              <img src="@/assets/imgs/right.png" alt="" style="height: 150px; width: 100%; border-radius: 20px">
            </div>

            <div
              style="margin: 20px 10px 10px 10px; width: 250px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis"
            >
              <i class="el-icon-bell"></i>
              <span style="font-weight: bold">公告</span>
              <span style="color: #666666;">：{{ top }}</span>
            </div>

            <div style="display: flex; margin-top: 50px">
              <div style="flex: 1; text-align: center">
                <a href="#" @click="navTo('/front/collect')">
                  <img src="@/assets/imgs/收藏.png" alt="" style="height: 25px; width: 25px">
                  <div>我的收藏</div>
                </a>
              </div>

              <div style="flex: 1; text-align: center">
                <a href="#" @click="navTo('/front/cart')">
                  <img src="@/assets/imgs/购物车.png" alt="" style="height: 25px; width: 25px">
                  <div>购物车</div>
                </a>
              </div>

              <div style="flex: 1; text-align: center">
                <a href="#" @click="navTo('/front/address')">
                  <img src="@/assets/imgs/店铺.png" alt="" style="height: 25px; width: 25px">
                  <div>我的地址</div>
                </a>
              </div>

              <div style="flex: 1; text-align: center">
                <a href="#" @click="navTo('/front/orders')">
                  <img src="@/assets/imgs/订单.png" alt="" style="height: 25px; width: 25px">
                  <div>我的订单</div>
                </a>
              </div>
            </div>
          </div>
        </div>

        <!-- 热卖商品 -->
        <div
          style="margin: 40px 0 0 15px; height: 40px; background-color: #00AEEC; font-size: 20px; color: white; width: 130px; font-weight: bold; line-height: 40px; text-align: center; border-radius: 20px"
        >
          热卖商品
        </div>
        <div style="margin: 10px 5px 0 5px">
          <el-row>
            <el-col :span="5" v-for="item in goodsData" :key="item.id">
              <img
                @click="navTo('/front/detail?id=' + item.id)"
                :src="item.img"
                alt=""
                style="width: 100%; height: 175px; border-radius: 10px; border: #cccccc 1px solid"
              >
              <div
                style="margin-top: 10px; font-weight: 500; font-size: 16px; width: 180px; color: #000000FF; text-overflow: ellipsis; overflow: hidden; white-space: nowrap;"
              >
                {{ item.name }}
              </div>
              <div style="margin-top: 5px; font-size: 20px; color: #FF5000FF">
                ￥ {{ item.price }} / {{ item.unit }}
              </div>
            </el-col>
          </el-row>
        </div>

        <!-- 猜你喜欢 -->
        <div
          style="margin: 40px 0 0 15px; height: 40px; background-color: #00AEEC; font-size: 20px; color: white; width: 130px; font-weight: bold; line-height: 40px; text-align: center; border-radius: 20px"
        >
          猜你喜欢
        </div>
        <div style="margin: 10px 5px 0 5px">
          <el-row>
            <el-col :span="5" v-for="item in recommendData" :key="item.id">
              <img
                @click="navTo('/front/detail?id=' + item.id)"
                :src="item.img"
                alt=""
                style="width: 100%; height: 175px; border-radius: 10px; border: #cccccc 1px solid"
              >
              <div
                style="margin-top: 10px; font-weight: 500; font-size: 16px; width: 180px; color: #000000FF; text-overflow: ellipsis; overflow: hidden; white-space: nowrap;"
              >
                {{ item.name }}
              </div>
              <div style="margin-top: 5px; font-size: 20px; color: #FF5000FF">
                ￥ {{ item.price }} / {{ item.unit }}
              </div>
            </el-col>
          </el-row>
        </div>
      </div>
      <div class="right"></div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      user: JSON.parse(localStorage.getItem("xm-user") || "{}"),
      typeData: [],
      goodsData: [],
      recommendData: [],
      top: null,
      notice: [],
      carousel_top: [
        require("@/assets/imgs/carousel-1.png"),
        require("@/assets/imgs/carousel-2.png"),
        require("@/assets/imgs/carousel-9.png"),
      ],
      carousel_left: [
        require("@/assets/imgs/carousel-3.png"),
        require("@/assets/imgs/carousel-4.png"),
        require("@/assets/imgs/carousel-5.png"),
      ],
      carousel_right: [
        require("@/assets/imgs/carousel-6.png"),
        require("@/assets/imgs/carousel-7.png"),
        require("@/assets/imgs/carousel-8.png"),
      ],
    };
  },
  mounted() {
    this.loadType();
    this.loadNotice();
    this.loadgoodsData();
    this.loadRecommend();
  },
  methods: {
    loadType() {
      this.$request.get("/type/selectAll").then((res) => {
        if (res.code === "200") {
          this.typeData = res.data;
        } else {
          this.$message.error(res.msg);
        }
      });
    },
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
    loadgoodsData() {
      this.$request.get("/goods/selectTop15").then((res) => {
        if (res.code === "200") {
          this.goodsData = res.data;
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    loadRecommend() {
      this.$request.get("/goods/recommend").then((res) => {
        if (res.code === "200") {
          this.recommendData = res.data;
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    navTo(url) {
      location.href = url;
    },
  },
};
</script>

<style scoped>
  .main-content {
    min-height: 100vh;
    min-width: 1280px;
    position: relative;
    overflow-x: auto;
    overflow-y: visible;
    background: linear-gradient(
      to bottom,
      #FB7299 0px,
      #FB7299 80px,
      #f6c1d1 260px,
      #f3e4ea 520px,
      #f7f7f9 900px,
      #f5f5f7 1400px
    );
  }

  .main-content::before {
    content: "";
    position: absolute;
    inset: 0;
    pointer-events: none;
    background: linear-gradient(
      to bottom,
      rgba(255,255,255,0.12) 0px,
      rgba(255,255,255,0.05) 140px,
      transparent 320px
    );
  }

  .main-content::after {
    content: "";
    position: absolute;
    inset: 0;
    pointer-events: none;
    background:
      linear-gradient(to right, rgba(0,0,0,0.05), transparent 20%),
      linear-gradient(to left, rgba(0,0,0,0.05), transparent 20%);
  }

  .main-content > div {
    position: relative;
    z-index: 2;
  }

  /* ========== 左右两侧：完全无缝衔接主背景渐变 ========== */
  .left {
    flex-shrink: 0;
    width: 17%;
    position: relative;
    overflow: hidden;
    background: linear-gradient(
      to bottom,
      #FB7299 0px,
      #FB7299 80px,
      #f6c1d1 260px,
      #f3e4ea 520px,
      #f7f7f9 900px,
      #f5f5f7 1400px
    );
    mask-image: linear-gradient(to right, rgba(0,0,0,1), transparent);
    -webkit-mask-image: linear-gradient(to right, rgba(0,0,0,1), transparent);
  }

  .right {
    flex-shrink: 0;
    width: 17%;
    position: relative;
    overflow: hidden;
    background: linear-gradient(
      to bottom,
      #FB7299 0px,
      #FB7299 80px,
      #f6c1d1 260px,
      #f3e4ea 520px,
      #f7f7f9 900px,
      #f5f5f7 1400px
    );
    mask-image: linear-gradient(to left, rgba(0,0,0,1), transparent);
    -webkit-mask-image: linear-gradient(to left, rgba(0,0,0,1), transparent);
  }

  .left::before,
  .right::before {
    content: "";
    position: absolute;
    inset: 0;
    background: radial-gradient(
      circle at center,
      rgba(255,255,255,0.25),
      transparent 70%
    );
    opacity: 0.6;
    pointer-events: none;
  }

  .left::after,
  .right::after {
    content: "";
    position: absolute;
    inset: 0;
    background: repeating-linear-gradient(
      45deg,
      rgba(255,255,255,0.04) 0px,
      rgba(255,255,255,0.04) 2px,
      transparent 2px,
      transparent 6px
    );
    opacity: 0.4;
    pointer-events: none;
  }

  @keyframes sideFloat {
    0% { transform: translateY(0px); }
    50% { transform: translateY(-6px); }
    100% { transform: translateY(0px); }
  }

  .left::before,
  .right::before {
    animation: sideFloat 12s ease-in-out infinite;
  }

  .el-col-5 {
    width: 20%;
    max-width: 20%;
    padding: 12px;
    transition: transform 0.25s ease;
    position: relative;
    overflow: hidden;
  }

  .el-col-5 img {
    width: 100%;
    border-radius: 12px;
    display: block;
    transition: all 0.3s ease;
    box-shadow: 0 4px 12px rgba(0,0,0,0.04);
  }

  .el-col-5:hover {
    transform: translateY(-6px);
  }

  .el-col-5:hover img {
    box-shadow: 0 12px 28px rgba(0,0,0,0.10);
  }

  /* 扫光 */
  .el-col-5::after {
    content: "";
    position: absolute;
    inset: 0;
    border-radius: 12px;
    pointer-events: none;
    background: linear-gradient(
      120deg,
      transparent 30%,
      rgba(255,255,255,0.25) 50%,
      transparent 70%
    );
    opacity: 0;
    transform: translateX(-60%);
    transition: all 0.4s ease;
  }

  .el-col-5:hover::after {
    opacity: 1;
    transform: translateX(60%);
  }
</style>