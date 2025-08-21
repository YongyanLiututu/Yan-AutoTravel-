## Yan-AutoTravel 旅行规划项目

一个基�?Spring Boot + Spring AI 的后端与 Vue 3 + Vite 的前端组合项目，提供行程规划、问答与推荐能力，并配套 CSV 向量化预处理脚本�?
�?README 面向你个人快速理解与使用，重点说明“如何启动”和“每个文件做什么”�?
---

### 一键快速开�?
准备：需要安�?Java 17、Node.js 18+、Python 3.10+�?
- 后端（端�?8080�?  1) 进入 `backend` 目录，设�?AI 与城市数据环境变量（可按需）：
  ```powershell
  setx CITYDATA_DIR "C:\\Users\\Administrator\\Desktop\\archive\\citydata"
  setx MCP_AMAP_SERVER "https://www.modelscope.cn/mcp/servers/@amap/amap-maps"
  setx MCP_12306_SERVER "https://www.modelscope.cn/mcp/servers/@Joooook/12306-mcp"
  ```
  2) 配置 `application.yml` 中的 openai.api-key（建议用环境变量覆盖，不要明文提交）�?  3) �?`backend` 执行�?  ```powershell
  mvn spring-boot:run
  ```
  4) Swagger 接口文档：`http://localhost:8080/swagger-ui/index.html`

- 前端（端�?5173，代理到 8080�?  1) 进入 `web` 目录，安装依赖并启动�?  ```powershell
  npm install
  npm run dev
  ```
  2) 浏览器打开：`http://localhost:5173`

- 数据脚本（可选）
  1) 进入 `vector-ingest`，安装依赖：
  ```powershell
  pip install pandas requests pillow
  ```
  2) 规范化城�?CSV�?  ```powershell
  python ingest_hyper_csv.py
  ```
  3) 批量生成缩略图（基于上一步输出）�?  ```powershell
  python image_jobs.py .\output\poi_normalized.csv
  ```

---

### 目录与文件说明（逐文件职责）

- 根目�?  - `README.md`：你正在看的说明文件�?  - `_bak/TripRequirement_copy_1755438927.java`：备�?参考文件，不参与编译�?  - `Untitled.ipynb`：Jupyter 草稿笔记本，可用于临时数据探索�?
- `backend/`（Java 17 + Spring Boot 3.3�?  - `pom.xml`：后端依赖与构建配置。内�?Spring Web/WebFlux、Validation、Actuator、Jackson、Spring AI、Swagger 等�?  - `src/main/resources/application.yml`：应用配置�?    - `server.port: 8080` 后端端口�?    - `spring.ai.openai.*` 模型服务（示例使�?DashScope 兼容 OpenAI 格式）�?    - `app.citydataDir` �?`app.mcp.*` 提供城市数据�?MCP 服务器地址�?  - `src/main/java/com/Yan-AutoTravel/Application.java`：Spring Boot 启动入口�?
  - `src/main/java/com/Yan-AutoTravel/api/`
    - `PlanController.java`：行程规划相�?REST 接口（如创建/查看计划，面�?`Editor.vue`、`PlanView.vue`）�?    - `DraftController.java`：行程草�?版本管理接口（保�?读取草稿等）�?    - `QaController.java`：问答接口，提供 SSE 流式输出，前端通过 `EventSource('/api/qa/ask')` 订阅�?    - `RecommendController.java`：发�?推荐接口，前�?`Discover.vue` 通过 `GET /api/recommendations` 获取推荐卡片列表�?
  - `src/main/java/com/Yan-AutoTravel/amap/RouteController.java`：高�?路线能力封装（如距离矩阵/路径规划），用于出行时间估计与行程编排�?  - `src/main/java/com/Yan-AutoTravel/mcp/VisionController.java`：多模态分析（BLIP/OCR 占位），提供 `/api/vision/analyze` 上传图片得到描述、OCR 与标签，用于问答补强�?
  - `src/main/java/com/Yan-AutoTravel/train12306/TrainController.java`�?2306 列车/车次查询接口，结�?MCP 12306 服务，用于跨城交通规划�?
  - `src/main/java/com/Yan-AutoTravel/agent/PlannerService.java`：核心“行程规划器”服务层，承�?ReAct/RAG/工具调用编排，供�?Controller 调用�?
  - `src/main/java/com/Yan-AutoTravel/config/WebConfig.java`：Web 相关基础配置（如 CORS、消息转换器、拦截器等）�?
  - `src/main/java/com/Yan-AutoTravel/tools/McpClients.java`：MCP（Model Context Protocol）客户端封装，集中管理外部工具（�?AMap�?2306）的调用配置与实例化�?    - 额外预留：`app.mcp.baidumap.server`、`app.mcp.flight.server`，用于接入百度地图与航班 MCP�?
  - `target/`：构建产物（.class、资源等），无需手动修改�?
- `web/`（Vue 3 + Vite + Element Plus�?  - `package.json`：前端依赖与脚本�?    - `npm run dev` 启动开发服务器�?    - `npm run build` 产出静态资源�?    - `npm run preview` 本地预览打包结果�?  - `vite.config.ts`：Vite 配置，设�?`@` 指向 `src`，并�?`/api` 代理�?`http://localhost:8080`�?  - `index.html`：Vite HTML 入口�?  - `src/main.ts`：应用入口，注册 Router、Pinia、Element Plus�?  - `src/styles/theme.css`：全局主题（粉色渐�?玻璃拟态），提供暗色模式变量�?  - `src/App.vue`：应用壳，导�?过渡/主题切换开关（暗色/亮色不影响既有功能）�?  - `src/router/index.ts`：路由定义与页面标题设置�?  - `src/stores/plan.ts`：Pinia 存储，包含行程草稿保�?读取（后端优先，LocalStorage 兜底）、会�?`sessionId`、缩略图索引加载�?  - `src/pages/Home.vue`：主页，新增 Hero 与特性卡片，仍保留原跳转逻辑�?  - `src/pages/Editor.vue`：行程编辑器，新增“行程规划表”（可增�?拖拽排序/AI 草案占位）、“出行助手”（时长估算）。新增保�?读取草稿按钮，不影响原功能�?  - `src/pages/Discover.vue`：发现推荐，新增关键词过滤与富媒体缩略图（对�?`vector-ingest` 生成�?`thumbs_index.csv`，可选）�?  - `src/pages/Ask.vue`：问答系统，增加会话 `sessionId`、简单问题重写（不清晰时自动补充上下文），仍通过 `EventSource('/api/qa/ask')` 流式接收�?  - `src/pages/PlanView.vue`：行程详情渲染（只读从后端按 ID 加载，或本地兜底）�?
- `vector-ingest/`（数据预处理脚本�?  - `ingest_hyper_csv.py`：将城市/景点 CSV 规范化到统一列集并去重�?    - 读取 `CITYDATA_DIR` 文件夹下�?CSV；输出到 `vector-ingest/output/poi_normalized.csv`�?    - 关键列：`city, province, poi_name, url, address, intro, open_hours, image_url, rating, duration, season, ticket, tips`�?  - `image_jobs.py`：批量下�?`image_url` 并生�?WebP 缩略图，输出 `thumbs/` �?`thumbs_index.csv`�?
---

### 配置与运行要�?
- AI 与模型：基于 Spring AI �?OpenAI 兼容接口，可通过 `spring.ai.openai.*` 配置 `base-url`、`api-key` �?`model`�?- 环境变量�?  - `CITYDATA_DIR`：城�?CSV 数据目录（`application.yml` 与脚本均会读取）�?  - `MCP_AMAP_SERVER` / `MCP_12306_SERVER`：MCP 工具服务端地址，可按需更换�?- 端口与代理：前端 5173 �?代理 `/api` 到后�?8080，避�?CORS 问题�?- 主题：`src/styles/theme.css` 提供粉色渐变主题及暗色变量；�?`App.vue` 右上角可切换暗色/亮色�?
---

### 常见问题（简要）

- 端口被占用：修改 `application.yml` �?`server.port` �?`vite.config.ts` �?`server.port`�?- API Key 管理：建议用系统环境变量或本地不跟踪配置覆盖，不要把密钥提交到仓库�?- Windows 路径：注意转义反斜杠，优先使用环境变量覆盖默认路径�?
---

### 模块关系与数据流（简述）

- 页面层（`src/pages/*`�?  - `Home` 负责导航与快捷入口�?  - `Discover` 通过 `/api/recommendations` 拉取推荐卡片，结�?`plan` Store 提供�?`thumbs_index.csv`（可选），渲染缩略图�?  - `Editor` 读写 `plan` Store（草稿保�?加载、拖拽排序），可在右侧“出行助手”调�?`/api/route/matrix` 估算时间�?  - `Ask` 通过 `EventSource('/api/qa/ask')` 流式接收回答，并携带 `sessionId` 以便服务端进行会话记忆；在问题不清晰时前端做轻量重写以提升命中率�?  - `PlanView` 根据路由 `:id` 调用 `/api/plan/{id}` 加载只读行程，或�?Store 本地兜底�?
- 状态层（`src/stores/plan.ts`�?  - 维护 `sessionId`、当�?`plan` 数据与缩略图索引�?  - 对外暴露 `saveDraft`/`loadDraft`/`loadPlanById`/`loadThumbsIndex` 等方法�?
- 后端交互
  - 行程：`GET /api/plan/{id}` 只读加载�?  - 草稿：`POST /api/drafts` 保存；`GET /api/drafts/latest` �?`GET /api/drafts/{id}` 读取�?  - 问答：`GET /api/qa/ask`（SSE），支持 `sessionId` 与查询参数�?  - 路线：`POST /api/route/matrix`，估算时长�?  - 多模态：`POST /api/vision/analyze`（multipart）→ 返回 captions/ocr/tags；前端会合并到问答问题中�?
以上接口名称基于目录职责推断，若后端命名不同，可�?`store` 与页面中统一调整，不影响整体结构�?
---

### 构建与部�?
- 后端构建可执�?Jar�?```powershell
cd backend
mvn clean package
java -jar target/Yan-AutoTravel-travel-0.1.0.jar
```

- 前端打包�?```powershell
cd web
npm run build
```
产物位于 `web/dist/`，可由任意静态服务器托管，并通过反向代理�?`/api` 指向后端�?

