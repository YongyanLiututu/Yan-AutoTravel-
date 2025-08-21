# Yan-AutoTravel 智能旅行规划系统

一个基于**Spring Boot + Spring AI** 的后端与 **Vue 3 + Vite** 的前端组合项目，提供智能行程规划、多模态问答与个性化推荐能力，并配套 CSV 向量化预处理脚本。系统集成了 **MCP (Model Context Protocol)** 工具链，实现从自然语言理解到 RAG (Retrieval-Augmented Generation) 的完整 AI 应用流程。
## 🚀 一键快速开始
### 环境准备
- **Java 17+** (后端)
- **Node.js 18+** (前端) 
- **Python 3.10+** (数据预处理)
### 一键快速开始
准备：需要安装Java 17、Node.js 18+、Python 3.10+。
- 后端（端口8080）：  1) 进入 `backend` 目录，设置AI 与城市数据环境变量（可按需）：
  ```powershell
  setx CITYDATA_DIR "C:\\Users\\Administrator\\Desktop\\archive\\citydata"
  setx MCP_AMAP_SERVER "https://www.modelscope.cn/mcp/servers/@amap/amap-maps"
  setx MCP_12306_SERVER "https://www.modelscope.cn/mcp/servers/@Joooook/12306-mcp"
  ```
  2) 配置 `application.yml` 中的 openai.api-key（建议用环境变量覆盖，不要明文提交）。  3) 在`backend` 执行：  ```powershell
  mvn spring-boot:run
  ```
  4) Swagger 接口文档：`http://localhost:8080/swagger-ui/index.html`
### 后端启动 (端口 8080)
```powershell
# 1. 设置环境变量 (可选，有默认值)
setx CITYDATA_DIR "C:\Users\Administrator\Desktop\archive\citydata"
setx MCP_AMAP_SERVER "https://www.modelscope.cn/mcp/servers/@amap/amap-maps"
setx MCP_12306_SERVER "https://www.modelscope.cn/mcp/servers/@Joooook/12306-mcp"
- 前端（端口5173，代理到 8080）：  1) 进入 `web` 目录，安装依赖并启动：  ```powershell
  npm install
  npm run dev
  ```
  2) 浏览器打开：`http://localhost:5173`
# 2. 配置 AI 服务 (application.yml)
# 修改 spring.ai.openai.api-key 为你的DashScope API Key

# 3. 启动后端
cd backend
mvn spring-boot:run

# 4. 访问 Swagger 文档
# http://localhost:8080/swagger-ui/index.html
```

### 前端启动 (端口 5173)
```powershell
cd web
npm install
npm run dev

# 浏览器访问：
# http://localhost:5173
```

### 数据预处理(可选)
```powershell
cd vector-ingest
pip install pandas requests pillow

# 规范化城市CSV 数据
python ingest_hyper_csv.py

# 批量生成缩略图：
python image_jobs.py .\output\poi_normalized.csv
```

---

## 📁 完整项目结构

```
Yan-AutoTravel-travel/
├── README.md                    # 项目说明文档
├── Untitled.ipynb              # Jupyter 数据探索笔记本
├── _bak/                       # 备份文件目录
│  └── TripRequirement_copy_1755438927.java
├── .ipynb_checkpoints/         # Jupyter 检查点
├── .vscode/                    # VS Code 配置
├── data/                       # 数据文件目录
├── mcp-8.21-v4/               # MCP 版本备份
├── backend/                    # Spring Boot 后端
│  ├── pom.xml                # Maven 依赖配置
│  ├── target/                # 构建产物
│  └── src/main/
│      ├── java/com/Yan-AutoTravel/
│      │  ├── Application.java              # Spring Boot 启动入口
│      │  ├── api/                          # REST API 控制器层
│      │  │  ├── PlanController.java       # 行程规划接口
│      │  │  ├── DraftController.java      # 草稿管理接口
│      │  │  ├── DraftsApiController.java  # 草稿 API 实现
│      │  │  ├── QaController.java         # 问答系统接口 (SSE 流式)
│      │  │  ├── RecommendController.java  # 推荐系统接口
│      │  │  ├── CityController.java       # 城市数据接口
│      │  │  ├── UserPrefController.java   # 用户偏好接口
│      │  │  └── dto/                      # 数据传输对象
│      │  ├── agent/                        # AI 代理层
│      │  │  └── PlannerService.java       # 核心行程规划服务(ReAct/RAG)
│      │  ├── qa/                           # 问答系统核心
│      │  │  └── OrchestratorService.java  # 问答编排服务 (MCP→RAG)
│      │  ├── mcp/                          # MCP 工具集成
│      │  │  └── VisionController.java     # 多模态分析(BLIP/OCR)
│      │  ├── amap/                         # 高德地图集成
│      │  │  └── RouteController.java      # 路线规划能力
│      │  ├── train12306/                   # 12306 集成
│      │  │  └── TrainController.java      # 列车查询能力
│      │  ├── route/                        # 路线服务
│      │  ├── tools/                        # 工具集成
│      │  └── config/                       # 配置
│      └── WebConfig.java           # Web 配置 (CORS)
│  └── resources/
│      └── application.yml              # 应用配置文件
├── web/                        # Vue 3 前端
│  ├── package.json           # 前端依赖配置
│  ├── vite.config.ts         # Vite 构建配置
│  ├── index.html             # HTML 入口
│  ├── tsconfig.json          # TypeScript 配置
│  └── src/
│      ├── main.ts            # 应用入口
│      ├── App.vue            # 根组(导航/主题切换)
│      ├── router/
│      │  └── index.ts       # 路由配置
│      ├── stores/
│      │  └── plan.ts        # Pinia 状态管(行程/会话)
│      ├── pages/             # 页面组件
│      │  ├── Home.vue       # 主页 (Hero/特性卡片)
│      │  ├── Editor.vue     # 行程编辑(拖拽/AI 助手)
│      │  ├── Ask.vue        # 问答系统 (多模态流式)
│      │  ├── Discover.vue   # 发现推荐 (过滤/缩略图)
│      │  └── PlanView.vue   # 行程详情 (只读)
│      ├── components/        # 通用组件
│      ├── styles/
│      │  └── theme.css      # 全局主题 (粉色渐变/暗色)
│      └── types/             # TypeScript 类型定义
└── vector-ingest/             # 数据预处理脚本
    ├── ingest_hyper_csv.py    # CSV 规范化处理
    └── image_jobs.py          # 缩略图批量生成
```

### 前端核心文件

#### 1. **plan.ts** - 状态管(行程/会话)
```typescript
export const usePlanStore = defineStore('plan', {
  state: () => ({
    plan: { rows: [] } as PlanData,
    sessionId: localStorage.getItem('ym-session-id') || uid(),
    thumbs: {} as ThumbIndex,
  }),
  actions: {
    async saveDraft() { /* 草稿保存 */ },
    async loadDraft() { /* 草稿加载 */ },
    async loadThumbsIndex() { /* 缩略图索引 */ }
  }
})
```
**职责**:
- 管理行程数据状态
- 会话 ID 持久化
- 草稿自动保存/加载

**状态管理架构**:
- **Pinia 状态库**: 基于 Vue 3 Composition API 的现代化状态管理
- **响应式数据**: 使用 Vue 3 的 `ref` 和 `reactive` 实现响应式状态
- **持久化策略**: 实现多级缓存 (内存、LocalStorage、后端)
- **状态同步**: 支持多标签页状态同步和冲突解决

**数据流管理**:
- **单向数据流**: 实现 Actions -> State -> Views 的单向数据流
- **状态分片**: 按功能模块进行状态分片和隔离
- **计算属性**: 使用 `computed` 实现派生状态计算
- **状态订阅**: 支持状态变化的订阅和通知机制

**缓存策略**:
- **LRU 缓存**: 实现最近最少使用的缓存淘汰策略
- **预加载**: 智能预加载用户可能访问的数据
- **增量更新**: 支持数据的增量更新和合并
- **离线支持**: 实现离线状态下的数据操作和同步

#### 2. **Ask.vue** - 多模态问答界面
```vue
<template>
  <div class="ask">
    <el-input v-model="msg" placeholder="输入问题..." />
    <el-upload :before-upload="beforeUpload" accept="image/*">
      <el-button>添加图片</el-button>
    </el-upload>
    <div class="log">
      <div v-for="l in logs" class="line">{{ l }}</div>
    </div>
  </div>
</template>
```
**职责**:
- 支持文本 + 图片多模态输入
- **EventSource** 流式接收 AI 回答
- 问题自动重写 (模糊查询优化)
- 图片分析结果集成

**多模态交互架构**:
- **文件处理**: 支持拖拽上传、剪贴板粘贴、摄像头拍照
- **格式转换**: 自动转换图片格式 (WebP, JPEG, PNG)
- **压缩优化**: 客户端图片压缩和尺寸调整
- **预览功能**: 实时图片预览和编辑
**流式通信机制**:
- **EventSource API**: 使用原生 EventSource 实现服务器推送
- **重连机制**: 自动重连和错误恢复
- **消息解析**: 支持自定义消息格式和类型
- **性能优化**: 实现消息缓冲和批量渲染
**智能输入增强**:
- **自动补全**: 基于历史查询的智能补全
- **语法检查**: 实时语法和语义检查
- **意图识别**: 前端轻量级意图识别
- **上下文管理**: 维护多轮对话上下文
**用户体验优化**:
- **加载状态**: 优雅的加载动画和进度指示
- **错误处理**: 友好的错误提示和恢复建议
- **响应式设计**: 适配不同屏幕尺寸和设备
- **无障碍支持**: 支持键盘导航和屏幕阅读器

#### 3. **Editor.vue** - 智能行程编辑界面
```vue
<template>
  <div class="editor">
    <!-- 行程规划 -->
    <div class="plan-table">
      <!-- 可拖拽排序的行程槽位 -->
    </div>
    <!-- 出行助手 -->
    <div class="travel-assistant">
      <!-- AI 时间估算 -->
    </div>
  </div>
</template>
```
**职责**:
- 可视化行程编辑
- 拖拽排序功能
- AI 出行时间估算
- 草稿自动保存

**交互式编辑架构**:
- **拖拽排序**: 使用 Sortable.js 实现流畅的拖拽排序
- **实时预览**: 编辑时实时预览最终效果
- **撤销重做**: 支持多级撤销和重做操作
- **协作编辑**: 支持多人实时协作编辑

**智能推荐引擎**:
- **基于位置**: 根据当前位置推荐附近景点
- **基于时间**: 考虑开放时间和最佳游览时间
- **基于偏好**: 根据用户历史偏好个性化推荐
- **基于天气**: 结合天气情况调整推荐

**时间估算算法**:
- **路径规划**: 集成高德地图 API 进行路径规划
- **交通模型**: 支持步行、公交、地铁、打车等多种交通方式
- **实时路况**: 考虑实时交通状况进行时间估算
- **历史数据**: 基于历史数据优化估算准确性
**数据同步机制**:
- **实时保存**: 自动保存用户编辑内容
- **版本控制**: 支持行程版本管理和回滚
- **冲突解决**: 处理并发编辑冲突
- **离线编辑**: 支持离线状态下的编辑操作
---

## 🔄 MCP -> RAG 核心逻辑流程

### 1. **用户输入处理**
```
用户问题 -> QaController -> OrchestratorService.rewrite()
```
- 解析用户意图 (城市、时间、偏好)
- 基于会话上下文补全信息
- 应用用户偏好优化查询

**技术实现细节**:
- **NLP 预处理**: 使用 spaCy 和 HanLP 进行中文分词和词性标注
- **意图识别模型**: 基于 BERT 和 RoBERTa 的意图分类模型
- **实体抽取**: 使用 BiLSTM-CRF 和 BERT-CRF 进行命名实体识别
- **查询扩展**: 基于 WordNet 或同义词词典进行查询扩展
- **上下文管理**: 使用 Redis 或内存缓存维护会话状态
### 2. **MCP 工具链调用**
```
Intent -> OrchestratorService.toolchain() -> MCP 服务
```
- **地理编码**: 调用百度地图 MCP 获取坐标
- **交通查询**: 调用 12306 MCP 获取车次信息
- **路线规划**: 调用高德地图 MCP 计算路径
- **多模态分析**: 调用 Vision MCP 分析图片

**工具链编排策略**:
- **动态工具选择**: 基于意图和实体的启发式规则选择工具
- **并行调用优化**: 使用 CompletableFuture 实现工具并行调用
- **依赖关系管理**: 处理工具间的依赖关系和执行顺序
- **超时控制**: 为每个工具设置合理的超时时间
- **错误恢复**: 实现工具调用失败时的降级策略

**MCP 协议实现**:
- **HTTP/2 支持**: 使用 HTTP/2 提高并发性能
- **连接复用**: 实现连接池和连接复用
- **协议版本**: 支持 MCP v1.0 和 v2.0 协议
- **认证机制**: 支持 API Key、OAuth 2.0 等多种认证方式
### 3. **RAG 检索增强**
```
工具结果 + 知识库 -> RAG 检索 -> 内容合成
```
- 基于工具结果检索相关景点、餐厅信息
- 从 CSV 向量化数据中获取详细信息
- 结合用户偏好进行个性化推荐

**向量检索架构**:
- **嵌入模型**: 使用 Sentence-BERT 和 OpenAI Embeddings 生成文本向量
- **向量数据**: 集成 Pinecone、Weaviate 和 FAISS 进行向量存储和检索
- **混合检索**: 结合语义检索和关键词检索提高召回率
- **重排序**: 使用 BERT 和 Cross-Encoder 进行检索结果重排序

**知识库构建**:
- **数据预处理**: 使用 pandas 进行数据清洗和标准化
- **向量化流水线**: 批量生成文本嵌入向量
- **索引优化**: 使用 HNSW 和 IVF 算法优化向量索引
- **增量更新**: 支持知识库的增量更新和版本管理
**个性化推荐算法**:
- **协同过滤**: 基于用户行为的协同过滤推荐
- **内容过滤**: 基于用户偏好的内容过滤推荐
- **深度学习**: 使用 Neural Collaborative Filtering 进行推荐
- **多目标优化**: 平衡相关性、多样性、新颖性等多个目标

### 4. **结果生成与流式输出**
```
合成结果 -> QaController -> SSE 流式响应 -> 前端实时显示
```
- 生成结构化回复
- 提供可执行的操作建议
- 通过 SSE 实时推送到前端

**内容生成策略**:
- **模板引擎**: 使用 FreeMarker 和 Thymeleaf 进行模板化内容生成
- **LLM 调用**: 集成 OpenAI GPT、Claude 或本地大模型进行内容生成
- **结构化输出**: 使用 JSON Schema 约束输出格式
- **质量控制**: 实现内容质量评估和过滤机制
**流式传输优化**:
- **分块传输**: 将长内容分块传输，提高响应速度
- **压缩算法**: 使用 gzip 和 brotli 压缩传输数据
- **缓存策略**: 实现多级缓存减少重复计算
- **负载均衡**: 使用 Nginx 和 HAProxy 进行负载均衡

**前端渲染优化**:
- **虚拟滚动**: 使用虚拟滚动处理大量消息
- **增量更新**: 实现 DOM 的增量更新
- **动画效果**: 添加流畅的动画和过渡效果
- **性能监控**: 集成性能监控和错误追踪
### 5. **状态持久化**
```
操作建议 -> PlanStore -> 后端 API -> 数据 -> 本地存储
```
- 自动保存到行程草稿
- 会话状态持久化
- 支持离线缓存

**数据持久化架构**:
- **多级存储**: 实现内存、Redis 和数据库的多级存储
- **事务管理**: 使用 Spring 事务管理确保数据一致性
- **数据版本**: 支持数据的版本管理和回滚
- **备份恢复**: 实现自动备份和灾难恢复机制
**缓存策略**:
- **分布式缓存**: 使用 Redis Cluster 实现分布式缓存
- **缓存预热**: 系统启动时预热热点数据
- **缓存更新**: 实现缓存与数据库的一致性更新
- **缓存穿透保护**: 防止缓存穿透和雪崩

**离线支持**:
- **Service Worker**: 使用 Service Worker 实现离线缓存
- **IndexedDB**: 使用 IndexedDB 存储大量离线数据
- **同步机制**: 实现离线数据与服务器的同步
- **冲突解决**: 处理离线编辑与在线数据的冲突

---

## 🎨 前端特(特性)
### 1. **动态主题系统**
- 粉色渐变主题
- 玻璃拟态设计
- 暗色/亮色模式切换
- 平滑过渡动画

### 2. **响应式布局**
- 移动端适配
- 组件自适应
- 流畅交互体验

### 3. **实时功能**
- SSE 流式问答
- 实时草稿保存
- 拖拽排序
- 图片上传预览

---

## ⚙️ 配置说明

### 环境变量
```yaml
# 城市数据目录
CITYDATA_DIR: "C:\Users\Administrator\Desktop\archive\citydata"

# MCP 服务端点
MCP_AMAP_SERVER: "https://www.modelscope.cn/mcp/servers/@amap/amap-maps"
MCP_12306_SERVER: "https://www.modelscope.cn/mcp/servers/@Joooook/12306-mcp"
MCP_BAIDUMAP_SERVER: ""  # 预留
MCP_FLIGHT_SERVER: ""    # 预留

# 数据库配置
DB_HOST: "localhost"
DB_PORT: "5432"
DB_NAME: "Yan-AutoTravel_travel"
DB_USER: "postgres"
DB_PASSWORD: "your-password"

# Redis 配置
REDIS_HOST: "localhost"
REDIS_PORT: "6379"
REDIS_PASSWORD: "your-redis-password"

# 向量数据库配置
VECTOR_DB_TYPE: "pinecone"  # pinecone, weaviate, faiss
VECTOR_DB_API_KEY: "your-vector-db-api-key"
VECTOR_DB_ENVIRONMENT: "us-west1-gcp"
```

### AI 服务配置
```yaml
spring:
  ai:
    openai:
      api-key: "your-dashscope-api-key"
      base-url: "https://dashscope.aliyuncs.com/compatible-mode/v1"
      chat:
        options:
          model: "qwen2.5-7b-instruct"
          temperature: 0.7
          max-tokens: 2048
          top-p: 0.9
          frequency-penalty: 0.0
          presence-penalty: 0.0
      embedding:
        options:
          model: "text-embedding-ada-002"
          dimensions: 1536
```

### 性能调优配置
```yaml
server:
  port: 8080
  tomcat:
    threads:
      max: 200
      min-spare: 10
    connection-timeout: 20000
    max-connections: 8192
    accept-count: 100

spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
  
  redis:
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
        max-wait: -1ms
```

### 监控与日志配置
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
    tags:
      application: Yan-AutoTravel-travel
      environment: production

logging:
  level:
    com.Yan-AutoTravel: DEBUG
    org.springframework.ai: INFO
    org.springframework.web: INFO
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/Yan-AutoTravel-travel.log
    max-size: 100MB
    max-history: 30
```

---

## 🚀 部署指南

### 后端打包
```powershell
cd backend
mvn clean package
java -jar target/Yan-AutoTravel-travel-0.1.0.jar
```

### 前端构建
```powershell
cd web
npm run build
# 产物位于 web/dist/
```

### 反向代理配置
```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    # 前端静态文件
    location / {
        root /path/to/web/dist;
        try_files $uri $uri/ /index.html;
    }
    
    # API 代理到后端
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

---

## 🔧 常见问题

### 1. 端口冲突
- 修改 `application.yml` 中的 `server.port`
- 修改 `vite.config.ts` 中的 `server.port`

### 2. API Key 管理
- 使用环境变量覆盖，避免明文提交
- 建议使用 `.env` 文件或系统环境变量
### 3. MCP 服务连接
- 检查网络连接
- 验证 MCP 服务端点可用性
- 查看后端日志排查问题

### 4. 数据预处理
- 确保 `CITYDATA_DIR` 路径正确
- 检查 CSV 文件格式
- 验证图片下载权限

---

## 📈 项目特色与技术亮点
### 🧠 AI 技术栈
1. **完整 AI 应用流程**: 端到端实现 MCP 工具调用和 RAG 检索增强
2. **多模态交互**: 支持文本 + 图片的智能问答，集成 OCR 和图像识别
3. **智能行程规划**: ReAct 框架实现推理与行动的结合，支持多目标优化
4. **个性化推荐**: 基于用户偏好和上下文的智能推荐，使用协同过滤和深度学习
### 🚀 性能与架构
5. **实时流式响应**: SSE 技术提供流畅的用户体验，支持背压控制和错误恢复
6. **现代化前端**: Vue 3 + TypeScript + 响应式设计，支持 PWA 和离线使用
7. **可扩展架构**: 模块化设计，易于添加新的 MCP 服务，支持微服务部署
8. **高可用设计**: 实现负载均衡、故障转移、自动扩缩容

### 🔧 技术深度
9. **向量检索优化**: 集成 FAISS、Pinecone 等向量数据库，支持 HNSW 索引
10. **缓存策略**: 多级缓存架构，支持分布式缓存和缓存预热
11. **安全机制**: 实现 RBAC、API 签名、审计日志等安全特性
12. **监控告警**: 集成 Prometheus + Grafana 进行性能监控和告警
### 🎯 用户体验
13. **智能输入增强**: 自动补全、语法检查、意图识别
14. **交互式编辑**: 拖拽排序、实时预览、撤销重做
15. **响应式设计**: 适配移动端、平板、桌面等多种设备
16. **无障碍支持**: 支持键盘导航和屏幕阅读器

---

## 🤝 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

---

## 📄 许可证
本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

#  YAN-AUTO-TRAVEL-
