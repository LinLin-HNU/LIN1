# 苍穹外卖 - Sky Take-Out 🚀

> **一站式餐饮管理系统** — 基于 Spring Boot 的完整外卖平台解决方案，集管理端与用户端于一体，支持智能客服、实时订单推送、数据报表分析等核心功能。

---

## 📋 项目简介

苍穹外卖是一套面向餐饮企业的**全流程数字化管理系统**，涵盖**菜品管理、订单流转、用户运营、数据统计**等核心业务模块。系统采用 **管理端 + 用户端双端分离架构**，支持从下单、支付、接单、配送至完成的完整订单生命周期管理。

项目代码结构清晰，遵循主流开发规范，深度集成 **AI 智能客服、WebSocket 实时推送、Redis 缓存加速、微信支付** 等企业级能力，具备良好的扩展性与维护性。

---

## ✨ 项目亮点

| 亮点 | 说明 |
|------|------|
| 🤖 **AI 智能客服** | 基于 LangChain4j + 通义千问 Qwen 大模型，支持带记忆的多轮对话，可根据用户意图查询菜品、修改信息，实现真正的智能交互 |
| ⚡ **WebSocket 实时推送** | 支付成功即时通知商家端来单提醒，客户催单消息毫秒级送达，提升接单效率与用户体验 |
| 🧭 **百度地图智能配送校验** | 下单时自动调用百度地图 API 进行经纬度解析与路线规划，智能判断收货地址是否超出配送范围 |
| 🎯 **AOP + 自定义注解实现公共字段自动填充** | 通过 `@AutoFill` 注解 + 面向切面编程，自动完成创建时间、创建人、更新时间、更新人的赋值，消除大量重复代码 |
| 🕐 **定时任务自动化处理** | 每分钟扫描超时未支付订单并自动取消，每日凌晨自动完成长时间派送中的订单，减少人工干预 |
| 📊 **多维度数据报表** | 涵盖营业额统计、用户增长、订单趋势、销量 Top10 等分析维度，支持 Excel 报表导出，辅助经营决策 |
| 🔐 **双端 JWT 认证体系** | 管理端与用户端独立 JWT 令牌鉴权，配合拦截器实现精细化的接口权限控制 |
| 🗂️ **Knife4j 接口文档** | 前后端分离开发，自动生成 Swagger 风格 API 文档，支持在线调试 |
| ☁️ **阿里云 OSS 文件存储** | 菜品图片等静态资源统一上云，减轻服务器压力，保障高可用 |

---

## 🛠️ 技术栈

| 技术 | 用途 |
|------|------|
| **Spring Boot 2.7** | 应用基础框架 |
| **Spring MVC** | Web 层 |
| **MyBatis** | ORM 持久层框架 |
| **MySQL** | 关系型数据库 |
| **Redis** | 缓存（店铺状态等） |
| **Druid** | 数据库连接池 |
| **JWT** | 身份认证令牌 |
| **Knife4j (Swagger)** | 接口文档生成 |
| **WebSocket** | 实时消息推送 |
| **LangChain4j + Qwen** | AI 智能客服 |
| **阿里云 OSS** | 文件/图片存储 |
| **微信支付 API v3** | 在线支付 |
| **百度地图 API** | 地理编码/路径规划 |
| **Apache POI** | Excel 报表导出 |
| **PageHelper** | 分页插件 |
| **Lombok** | 代码简化 |

---

## 🏗️ 项目架构

```
sky-take-out
├── sky-common          # 公共模块：工具类、常量、异常、配置
├── sky-pojo            # 数据层：Entity、DTO、VO
└── sky-server          # 服务端：Controller、Service、Mapper、配置
```

### 功能模块

#### 管理端 (Admin)
- **员工管理** — 登录认证、信息维护、分页查询
- **分类管理** — 菜品/套餐分类 CRUD
- **菜品管理** — 菜品增删改查、起售停售
- **套餐管理** — 套餐组合管理
- **订单管理** — 订单搜索、接单/拒单/取消/派送/完成、催单处理
- **数据报表** — 营业额、用户、订单统计，销量 Top10，Excel 导出
- **工作台** — 今日运营数据概览、订单/菜品/套餐状态一览
- **店铺营业状态** — Redis 缓存控制

#### 用户端 (User)
- **微信登录** — 一键授权登录
- **菜品浏览** — 按分类浏览菜品与套餐
- **购物车** — 添加/查看/修改/清空
- **下单支付** — 提交订单 + 微信支付
- **历史订单** — 订单查询、取消、再来一单
- **地址簿** — 收货地址管理
- **AI 智能客服** — 智能问答、菜品查询

---

## 🚀 快速开始

### 前置条件

- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+
- Redis
- 阿里云 OSS 账号（用于文件存储）
- 微信支付商户号（用于支付）
- 百度地图 API Key（用于配送范围校验）

### 配置

1. **导入数据库**

```sql
-- 创建数据库
CREATE DATABASE sky_take_out;
-- 执行项目下的 sql 脚本（若提供）
```

2. **修改配置**

编辑 `sky-server/src/main/resources/application-dev.yml`，配置以下环境变量：

```bash
sky.datasource.host=localhost
sky.datasource.port=3306
sky.datasource.database=sky_take_out
sky.datasource.username=root
sky.datasource.password=your_password

sky.redis.host=localhost
sky.redis.port=6379
sky.redis.password=

sky.alioss.endpoint=your_oss_endpoint
sky.alioss.access-key-id=your_oss_key
sky.alioss.access-key-secret=your_oss_secret
sky.alioss.bucket-name=your_bucket_name

sky.wechat.appid=your_appid
sky.wechat.secret=your_secret

sky.shop.address=店铺地址
sky.baidu.ak=你的百度地图AK
```

3. **启动项目**

```bash
# 编译打包
mvn clean install

# 启动服务
mvn spring-boot:run -pl sky-server
```

### 访问地址

| 服务 | 地址 |
|------|------|
| 管理端 API | `http://localhost:8080/admin/` |
| 用户端 API | `http://localhost:8080/user/` |
| 接口文档 | `http://localhost:8080/doc.html` |

---

## 📁 项目结构

```
sky-server/src/main/java/com/sky
├── annotation/          # 自定义注解（@AutoFill）
├── aspect/              # 切面（公共字段自动填充）
├── config/              # 配置类（Redis/OSS/WebSocket/WebMvc）
├── controller/
│   ├── admin/           # 管理端控制器
│   ├── user/            # 用户端控制器
│   └── notify/          # 微信支付回调
├── handler/             # 全局异常处理
├── interceptor/         # JWT 拦截器（管理端+用户端）
├── mapper/              # MyBatis 数据访问层
├── properties/          # 配置属性类
├── service/
│   ├── impl/            # 业务逻辑实现
│   └── *.java           # 业务接口 + AI Tools
├── task/                # 定时任务（超时订单/派送完成）
└── websocket/           # WebSocket 服务端（实时推送）
```

---

## 📊 接口概览

### 管理端 API

| 模块 | 端点 | 说明 |
|------|------|------|
| 员工管理 | `/admin/employee/**` | 登录、分页查询、状态管理 |
| 分类管理 | `/admin/category/**` | CRUD 操作 |
| 菜品管理 | `/admin/dish/**` | 增删改查、起售停售 |
| 套餐管理 | `/admin/setmeal/**` | 套餐操作 |
| 订单管理 | `/admin/order/**` | 订单流转管理 |
| 数据报表 | `/admin/report/**` | 数据统计与导出 |
| 工作台 | `/admin/workspace/**` | 运营数据概览 |
| 店铺管理 | `/admin/shop/**` | 营业状态控制 |
| 通用接口 | `/admin/common/**` | 文件上传 |

### 用户端 API

| 模块 | 端点 | 说明 |
|------|------|------|
| 用户 | `/user/user/**` | 微信登录 |
| 分类 | `/user/category/**` | 分类列表 |
| 菜品 | `/user/dish/**` | 菜品查询 |
| 套餐 | `/user/setmeal/**` | 套餐查询 |
| 购物车 | `/user/shoppingCart/**` | 购物车管理 |
| 订单 | `/user/order/**` | 下单、支付、查询 |
| 地址簿 | `/user/addressBook/**` | 收货地址管理 |
| 店铺 | `/user/shop/**` | 店铺状态查询 |
| AI 客服 | `/admin/ai/**` | 智能对话 |

---

## 🤝 贡献

欢迎提 Issue 或 Pull Request 来完善项目。

---

## 📄 许可证

本项目仅用于学习交流，仅供参考。
