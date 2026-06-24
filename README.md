# 基于 Spring Boot + Vue 的购物商城系统

## 项目简介

本项目是一个前后端分离的购物商城系统，包含用户端、商家端和管理员端三类角色。系统支持商品浏览、分类检索、购物车、收藏、订单、地址管理、在线支付、评论管理、商家商品管理、后台数据管理以及 DeepSeek 智能客服等功能。

后端基于 Spring Boot + MyBatis 构建 REST API，前端基于 Vue2 + Element UI 构建页面，数据库使用 MySQL，登录鉴权使用 JWT，支付模块接入支付宝沙箱。

## 技术栈

### 后端

- Spring Boot 2.7.18
- MyBatis
- PageHelper
- MySQL
- JWT
- Hutool
- Alipay SDK
- DeepSeek API

### 前端

- Vue 2.6
- Vue Router 3
- Element UI
- Axios
- wangEditor

## 功能模块

### 用户端

- 用户注册、登录
- 首页商品展示
- 商品分类浏览
- 商品详情查看
- 商品搜索
- 店铺浏览
- 商品收藏
- 收货地址管理
- 购物车管理
- 下单与订单管理
- 支付宝沙箱支付
- 商品评论
- DeepSeek 智能客服

### 商家端

- 商家登录
- 商家个人信息维护
- 商品管理
- 订单管理
- 评论查看

### 管理员端

- 管理员登录
- 管理员信息管理
- 用户管理
- 商家管理
- 商品分类管理
- 商品管理
- 订单管理
- 公告管理
- 评论管理
- 修改密码

## 项目结构

```text
bs-shop/
├─ springboot/                 # 后端 Spring Boot 项目
│  ├─ src/main/java/com/example
│  │  ├─ common/               # 公共返回结果、枚举、拦截器、跨域配置
│  │  ├─ controller/           # 接口控制器
│  │  ├─ entity/               # 实体类
│  │  ├─ exception/            # 全局异常处理
│  │  ├─ mapper/               # MyBatis Mapper 接口
│  │  ├─ service/              # 业务逻辑
│  │  └─ utils/                # JWT、推荐算法等工具类
│  ├─ src/main/resources
│  │  ├─ mapper/               # MyBatis XML 映射文件
│  │  └─ application.yml       # 后端配置文件
│  └─ pom.xml
│
├─ vue/                        # 前端 Vue 项目
│  ├─ src
│  │  ├─ assets/               # 静态资源、样式、图片
│  │  ├─ router/               # 路由配置
│  │  ├─ utils/                # Axios 请求封装
│  │  └─ views/                # 页面组件
│  │     ├─ front/             # 用户端页面
│  │     └─ manager/           # 后台管理页面
│  ├─ package.json
│  └─ vue.config.js
│
└─ files/                      # 文件上传目录
```

## 运行环境

- JDK 8+（当前 `pom.xml` 配置为 Java 1.8）
- Maven 3.6+
- MySQL 8
- Node.js 14+ / 16+ 推荐
- npm

## 后端配置

后端配置文件位于：

```text
springboot/src/main/resources/application.yml
```

主要配置项：

```yaml
server:
  port: 9090

spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: 123456
    url: jdbc:mysql://localhost:3306/manager?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false&serverTimezone=GMT%2b8&allowPublicKeyRetrieval=true

app:
  ip: localhost
```

请根据本机环境修改数据库用户名、密码和数据库名。当前项目默认连接的数据库名为 `manager`。

如需使用智能客服，请配置 DeepSeek API Key：

```yaml
deepseek:
  api-key: ${DEEPSEEK_API_KEY:你的 DeepSeek API Key}
  api-url: https://api.deepseek.com/v1/chat/completions
```

如需使用支付宝沙箱支付，请替换支付宝沙箱相关配置：

```yaml
alipay:
  app-id: 你的支付宝沙箱 appId
  private-key: 你的应用私钥
  public-key: 支付宝公钥
  gateway: https://openapi-sandbox.dl.alipaydev.com/gateway.do
  notify-url: http://localhost:9090/alipay/notify
  return-url: http://localhost:9090/alipay/return
```

> 注意：API Key、支付宝私钥、数据库密码等敏感信息不要提交到公开仓库，建议通过环境变量或本地私有配置文件管理。

## 前端配置

前端开发环境接口地址位于：

```text
vue/.env.development
```

默认配置：

```env
VUE_APP_BASEURL='http://localhost:9090'
```

前端开发服务端口配置位于：

```text
vue/vue.config.js
```

默认端口：

```js
devServer: {
  port: 8080
}
```

## 项目启动

### 1. 准备数据库

创建数据库：

```sql
CREATE DATABASE manager DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

然后导入项目对应的数据库脚本。

> 当前仓库未检测到 SQL 初始化脚本。如果你本地有 `.sql` 文件，请先导入；如果没有，需要根据实体类和 Mapper 文件补充数据库表结构。

### 2. 启动后端

进入后端目录：

```bash
cd springboot
```

安装依赖并启动：

```bash
mvn spring-boot:run
```

后端默认访问地址：

```text
http://localhost:9090
```

### 3. 启动前端

进入前端目录：

```bash
cd vue
```

安装依赖：

```bash
npm install
```

启动开发服务：

```bash
npm run serve
```

前端默认访问地址：

```text
http://localhost:8080
```

### 4. 前端打包

```bash
cd vue
npm run build
```

打包产物默认生成在：

```text
vue/dist/
```

## 主要页面

### 用户端

- `/front/home`：商城首页
- `/front/detail`：商品详情
- `/front/type`：分类商品
- `/front/business`：商家店铺
- `/front/search`：商品搜索
- `/front/cart`：购物车
- `/front/collect`：我的收藏
- `/front/address`：收货地址
- `/front/orders`：我的订单
- `/front/customer`：智能客服

### 后台管理端

- `/home`：后台首页
- `/admin`：管理员管理
- `/business`：商家管理
- `/user`：用户管理
- `/notice`：公告管理
- `/type`：分类管理
- `/goods`：商品管理
- `/orders`：订单管理
- `/comment`：评论管理

## 项目截图

建议将项目截图放在 `docs/` 目录下，例如：

```text
docs/
├─ login.png
├─ home.png
├─ goods.png
├─ cart.png
├─ order.png
├─ customer.png
└─ admin.png
```

然后可以在 README 中引用：

```md
![登录页](docs/login.png)
![商城首页](docs/home.png)
![订单管理](docs/order.png)
![后台管理](docs/admin.png)
```

## 注意事项

- 后端默认端口为 `9090`，前端默认端口为 `8080`。
- 前端请求后端接口地址由 `vue/.env.development` 中的 `VUE_APP_BASEURL` 控制。
- 支付宝沙箱回调地址在本地开发时可能无法被支付宝服务器直接访问，正式联调时可使用内网穿透工具。
- DeepSeek 智能客服需要可用的 API Key，并确保后端服务可以访问外网。
- 如果项目上传到 GitHub，请检查 `application.yml` 中是否包含真实密钥或私钥。


