<template>
  <div class="main-content">
    <div class="chat-wrap">

      <!-- 左侧：我的订单（可滚动） -->
      <div class="side-panel">
        <div class="panel-title">
          <i class="el-icon-shopping-bag-2"></i> 我的订单
        </div>
        <div class="order-scroll">
          <div v-if="recentOrders.length > 0">
            <div class="order-card" v-for="item in recentOrders" :key="item.id">
              <el-image
                :src="item.goodsImg"
                fit="cover"
                class="order-img"
                :preview-src-list="[item.goodsImg]"
              />
              <div class="order-info">
                <div class="order-name">{{ item.goodsName }}</div>
                <div class="order-price">
                  ¥{{ item.goodsPrice }}
                  <span class="order-num">× {{ item.num }}</span>
                </div>
                <div class="order-row">
                  <span :class="['order-status', statusClass(item.status)]">{{ item.status }}</span>
                  <el-button type="text" size="mini" @click="askAboutOrder(item)">咨询</el-button>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="side-empty">
            <i class="el-icon-shopping-cart-2"></i>
            <span>暂无购买记录</span>
          </div>
        </div>
      </div>

      <!-- 右侧：聊天主体 -->
      <div class="chat-main">
        <!-- 顶部 -->
        <div class="chat-header">
          <img src="@/assets/imgs/客服.png" class="chat-header-icon" />
          <div>
            <div class="chat-header-title">智能客服</div>
            <div class="chat-header-sub">DeepSeek AI · 随时为您服务</div>
          </div>
          <div class="chat-header-online">
            <span class="online-dot"></span> 在线
          </div>
        </div>

        <!-- 消息区 -->
        <div class="chat-body" ref="chatBody">
          <!-- 欢迎消息 -->
          <div class="msg-row msg-left">
            <img src="@/assets/imgs/客服.png" class="avatar" />
            <div class="bubble bubble-ai">
              👋 您好！我是智能客服，有什么可以帮您的吗？<br/>
              <span style="color: var(--text-secondary); font-size: 12px">可咨询订单状态、商品信息、退换货等问题</span>
            </div>
          </div>

          <!-- 聊天消息 -->
          <div v-for="(msg, index) in messages" :key="index"
               :class="['msg-row', msg.role === 'user' ? 'msg-right' : 'msg-left']">
            <img v-if="msg.role === 'ai'" src="@/assets/imgs/客服.png" class="avatar" />
            <div :class="['bubble', msg.role === 'user' ? 'bubble-user' : 'bubble-ai']">
              <span style="white-space: pre-wrap">{{ msg.content }}</span>
            </div>
            <img v-if="msg.role === 'user'" :src="user.avatar || defaultAvatar" class="avatar" />
          </div>

          <!-- 打字中 -->
          <div v-if="loading" class="msg-row msg-left">
            <img src="@/assets/imgs/客服.png" class="avatar" />
            <div class="bubble bubble-ai typing">
              <span class="dot"></span><span class="dot"></span><span class="dot"></span>
            </div>
          </div>
        </div>

        <!-- 输入区（含快捷咨询） -->
        <div class="chat-footer">
          <div class="quick-bar">
            <span
              v-for="q in quickQuestions" :key="q"
              class="quick-tag"
              @click="sendQuick(q)"
            >{{ q }}</span>
          </div>
          <div class="footer-input-row">
            <el-input
              v-model="inputText"
              placeholder="请输入您的问题，按 Enter 发送…"
              :disabled="loading"
              @keyup.enter.native="sendMessage"
              class="chat-input"
            />
            <el-button type="primary" :loading="loading" @click="sendMessage" class="send-btn">
              <i class="el-icon-s-promotion"></i> 发送
            </el-button>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script>
export default {
  name: 'Customer',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea0be895f019f75ae0b06e98a1f2png.png',
      messages: [],
      inputText: '',
      loading: false,
      recentOrders: [],
      quickQuestions: ['我的订单发货了吗？', '商品支持退货吗？', '配送需要多少天？', '推荐什么商品？']
    }
  },
  mounted() {
    this.loadRecentOrders()
  },
  methods: {
    loadRecentOrders() {
      if (!this.user.id) return
      this.$request.get('/chat/recent', { params: { userId: this.user.id } }).then(res => {
        if (res.code === '200' && res.data) {
          this.recentOrders = res.data
        }
      })
    },
    statusClass(status) {
      const map = {
        '待付款': 'st-wait', '待发货': 'st-wait',
        '已发货': 'st-sent', '待收货': 'st-sent',
        '已完成': 'st-done', '已评价': 'st-done'
      }
      return map[status] || 'st-wait'
    },
    askAboutOrder(item) {
      this.inputText = `我购买的【${item.goodsName}】订单（单号：${item.orderId}）当前状态是"${item.status}"，请帮我了解一下。`
      this.sendMessage()
    },
    sendQuick(q) {
      this.inputText = q
      this.sendMessage()
    },
    async sendMessage() {
      const text = this.inputText.trim()
      if (!text || this.loading) return
      this.messages.push({ role: 'user', content: text })
      this.inputText = ''
      this.loading = true
      this.$nextTick(() => this.scrollToBottom())
      try {
        const res = await this.$request({
          url: '/chat/send',
          method: 'POST',
          data: { message: text, userId: this.user.id || 0 }
        })
        if (res.code === '200') {
          this.messages.push({ role: 'ai', content: res.data })
        } else {
          this.messages.push({ role: 'ai', content: '抱歉，出了点问题：' + res.msg })
        }
      } catch (e) {
        this.messages.push({ role: 'ai', content: '网络异常，请稍后重试。' })
      } finally {
        this.loading = false
        this.$nextTick(() => this.scrollToBottom())
      }
    },
    scrollToBottom() {
      const el = this.$refs.chatBody
      if (el) el.scrollTop = el.scrollHeight
    }
  }
}
</script>

<style scoped>
/* ── 整体容器 */
.chat-wrap {
  display: flex;
  gap: 18px;
  width: 96%;
  max-width: 1080px;
  margin: 20px auto;
  height: calc(100vh - 130px);
  min-height: 520px;
  max-height: 760px;
}

/* ────────────────── 左侧面板 ────────────────── */
.side-panel {
  width: 220px;
  flex-shrink: 0;
  background: var(--surface);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
  padding: 14px 12px 12px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.panel-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-regular);
  display: flex;
  align-items: center;
  gap: 5px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--border-light);
  flex-shrink: 0;
  margin-bottom: 8px;
}
.panel-title i { color: var(--pink); }

/* 订单滚动区 */
.order-scroll {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.order-scroll::-webkit-scrollbar { width: 4px; }
.order-scroll::-webkit-scrollbar-thumb { background: #fbbdd0; border-radius: 4px; }
.order-scroll::-webkit-scrollbar-track { background: transparent; }

/* 订单卡片 */
.order-card {
  display: flex;
  gap: 8px;
  padding: 8px;
  border-radius: var(--radius-md);
  border: 1px solid var(--border-light);
  transition: box-shadow var(--transition), border-color var(--transition);
  cursor: default;
}
.order-card:hover { border-color: #fbbdd0; box-shadow: var(--shadow-sm); }
.order-img {
  width: 50px; height: 50px;
  border-radius: var(--radius-md);
  flex-shrink: 0;
  background: var(--bg);
}
.order-info { flex: 1; min-width: 0; }
.order-name {
  font-size: 12px;
  color: var(--text-primary);
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 3px;
}
.order-price { font-size: 13px; color: var(--pink); font-weight: bold; margin-bottom: 3px; }
.order-num { font-size: 11px; color: var(--text-secondary); font-weight: normal; }
.order-row { display: flex; align-items: center; justify-content: space-between; }
.order-status { font-size: 11px; padding: 1px 6px; border-radius: var(--radius-full); }
.st-wait { background: #fff7e6; color: #fa8c16; }
.st-sent { background: var(--blue-light); color: var(--blue-deep); }
.st-done { background: #f6ffed; color: #389e0d; }

.side-empty {
  display: flex; flex-direction: column; align-items: center;
  gap: 6px; padding: 20px 0;
  color: var(--text-secondary); font-size: 12px;
}
.side-empty i { font-size: 28px; color: var(--text-placeholder); }

/* ────────────────── 右侧列 ────────────────── */
.chat-right {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* ────────────────── 聊天主体 ────────────────── */
.chat-main {
  flex: 1;
  min-height: 0;
  background: var(--surface);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-md);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 顶部栏 */
.chat-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 18px;
  background: linear-gradient(135deg, var(--pink) 0%, #fa5984 100%);
  flex-shrink: 0;
}
.chat-header-icon { width: 34px; height: 34px; border-radius: 50%; background: #fff; padding: 3px; flex-shrink: 0; }
.chat-header-title { font-size: 15px; font-weight: bold; color: #fff; }
.chat-header-sub { font-size: 11px; color: rgba(255,255,255,0.8); margin-top: 1px; }
.chat-header-online { margin-left: auto; display: flex; align-items: center; gap: 5px; font-size: 12px; color: rgba(255,255,255,0.9); }
.online-dot {
  width: 7px; height: 7px; background: #52c41a; border-radius: 50%;
  box-shadow: 0 0 0 2px rgba(82,196,26,0.3); animation: pulse 1.8s infinite;
}
@keyframes pulse {
  0%, 100% { box-shadow: 0 0 0 2px rgba(82,196,26,0.3); }
  50%       { box-shadow: 0 0 0 5px rgba(82,196,26,0.1); }
}

/* 消息区 */
.chat-body {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  background: var(--bg);
}
.msg-row { display: flex; align-items: flex-end; gap: 8px; }
.msg-right { flex-direction: row-reverse; }
.avatar { width: 32px; height: 32px; border-radius: 50%; object-fit: cover; flex-shrink: 0; border: 2px solid var(--border-light); }
.bubble { max-width: 70%; padding: 9px 13px; border-radius: var(--radius-lg); font-size: 13px; line-height: 1.65; word-break: break-word; }
.bubble-ai { background: var(--surface); color: var(--text-primary); border-bottom-left-radius: var(--radius-sm); box-shadow: var(--shadow-sm); border: 1px solid var(--border-light); }
.bubble-user { background: var(--pink); color: #fff; border-bottom-right-radius: var(--radius-sm); box-shadow: 0 2px 8px rgba(251,114,153,0.3); }

/* 打字动画 */
.typing { display: flex; align-items: center; gap: 5px; padding: 10px 14px; }
.dot { width: 6px; height: 6px; background: var(--text-placeholder); border-radius: 50%; animation: bounce 1.2s infinite ease-in-out; }
.dot:nth-child(2) { animation-delay: 0.2s; }
.dot:nth-child(3) { animation-delay: 0.4s; }
@keyframes bounce {
  0%, 80%, 100% { transform: translateY(0); }
  40%           { transform: translateY(-6px); }
}

/* 输入区（含快捷咨询） */
.chat-footer {
  padding: 8px 14px 12px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  background: var(--surface);
  border-top: 1px solid var(--border-light);
  flex-shrink: 0;
}
.quick-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
.quick-tag {
  font-size: 12px;
  color: var(--pink-deep);
  background: var(--pink-light);
  border: 1px solid #fbbdd0;
  border-radius: var(--radius-full);
  padding: 3px 11px;
  cursor: pointer;
  transition: background var(--transition);
  white-space: nowrap;
}
.quick-tag:hover { background: #ffdde8; }
.footer-input-row {
  display: flex;
  gap: 8px;
  align-items: center;
}
.chat-input { flex: 1; }
.send-btn { flex-shrink: 0; }
</style>

