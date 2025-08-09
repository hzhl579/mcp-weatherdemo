# WeatherMCP 项目 README



## 一、项目简介



本项目是一个基于 Spring 生态的智能天气查询 Demo，结合 AI 能力，实现 “自然语言交互 + 天气数据获取” 功能。通过简单的自然语言指令（如 “获取北京后三天的天气”），自动调用工具并返回对应天气信息，快速搭建 “查天气 + AI 聊天” 小工具。

## 二、核心流程



1. **需求理解**：Controller 接收用户自然语言请求（如 `localhost:8081/chat?input=获取北京后三天的天气` ）。
2. **工具匹配**：AI 解析用户需求语义，通过 `@Tool` 注解匹配对应工具（如 `getWeatherForecast` 处理三天预报、`getRealTimeWeather` 处理实时天气 ）。
3. **数据获取**：调用高德天气 API（需自行申请密钥 ），返回结构化天气数据。
4. **结果返回**：AI 整合工具返回内容，生成自然语言回复，返回给用户。

## 三、环境准备



### 1. 依赖与环境



- **开发语言**：Java（推荐 11+ ）
- **框架**：Spring Boot、Spring AI
- **AI 模型**：支持 `@Tool` 调用（如本地 Ollama 或云 GPU 实例，需配置 `base-url` ）
- **天气数据**：依赖高德地图 API（需到 [高德开放平台](https://lbs.amap.com/) 申请 `ApiKey` ）

### 2. 环境配置



#### （1）AI 模型部署（二选一）



- **云 GPU 实例**：购买 2 元/h 以上的 GPU 实例（适配 Mistral 等模型 ），配置 `ollama.base-url` 为云实例地址（如 `http://7eb879a73f5c4dcb.api.gcs-xy1a.jdcloud.com/` ）。
- **本地 Ollama**：电脑需满足硬件要求（如 Mistral 模型需显存 ≥8GB ），安装 Ollama 后拉取模型（命令：`ollama pull Mistral` ）。

#### （2）密钥与配置



1. 申请高德 API 密钥，替换 `WeatherService` 中 `amapApiKey` 。

2. 修改

    

   ```
   application.properties
   ```

    

   或

    

   ```
   application.yml
   ```

    

   ：

   ```
   # 服务端口
   server.port=8081  
   # AI 模型地址（云/本地）
   ollama.base-url=http://你的模型地址  
   # 高德天气 API 密钥（替换为自己申请的）
   weather.key=你的高德 API 密钥  
   ```

   

## 四、代码结构



```
starter-stdio-server/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── config/  # 配置类（如 GPUOllamaConfig ）
│   │   │   ├── controller/  # 入口（WeatherController 接收请求）
│   │   │   ├── service/  # 业务逻辑（WeatherService 封装工具、调用 API ）
│   │   │   └── McpServerApplication.java  # Spring Boot 启动类（注册 Tool ）
│   │   └── resources/  
│   │       ├── application.properties  # 核心配置（端口、密钥、模型地址 ）
│   │       └── config/mcp-servers-config.json  # 客户端调用配置  
│   └── test/  # 测试类（如 testWeather ）
├── pom.xml  # Maven 依赖（Spring Boot、Spring AI 等 ）
└── README.md  # 项目说明（当前文件 ）
```



## 五、快速启动



### 1. 编译与运行



```
# 进入项目根目录
cd D:\newMcp-weather\starter-stdio-server  
# 编译（如需跳过测试，加 -DskipTests ）
mvn clean install  
# 启动服务
java -jar target/mcp-weather-stdio-server-0.0.1-SNAPSHOT.jar  
```



### 2. 接口测试



浏览器或 Postman 访问：

- **实时天气**：`http://localhost:8081/chat?input=查询北京的天气`
- **三天预报**：`http://localhost:8081/chat?input=获取北京后三天的天气`

## 六、关键实现说明



### 1. AI 工具注册



在 `McpServerApplication` 中，通过 `ToolCallbackProvider` 注册工具，让 AI “看见” 并调用：

```
@Bean
public ToolCallbackProvider weatherTools(WeatherService weatherService) {
    return MethodToolCallbackProvider.builder()
        // 注册 WeatherService 中所有 @Tool 方法
        .toolObjects(weatherService).build();  
}
```



### 2. 自然语言匹配逻辑



AI 调用工具公式：

```
AI 调用工具 = @Tool 注解描述 + 用户需求语义匹配  
```



示例：

```
@Tool(description = "Get 3-day weather forecast for a specific city using Amap API...")
public String getWeatherForecast(String cityCode) { ... }
```



当用户输入 `获取北京后三天的天气`，AI 匹配到 `3-day forecast` 描述，自动调用 `getWeatherForecast` 并传入城市编码。

## 七、常见问题



### 1. 模型拉取失败



- 现象：执行 `ollama pull Mistral` 报错网络失败。
- 解决：检查网络代理，或更换模型源（如用国内镜像 ），重新拉取。

### 2. 天气 API 无返回



- 现象：调用高德 API 返回空或报错。

- 解决：检查

   

  ```
  amapApiKey
  ```

   

  是否正确、是否开通对应权限，或直接浏览器访问 API 地址测试：

  ```
  https://restapi.amap.com/v3/weather/weatherInfo?city=110000&key=你的密钥&extensions=all
  ```

  

### 3. AI 不调用工具



- 现象：用户输入匹配工具描述，但未触发调用。
- 解决：检查 `ToolCallbackProvider` 是否正确注册，或 `@Tool` 描述是否清晰（避免歧义 ）。

## 八、扩展建议



1. **多模型支持**：添加 `OpenAI`、`DeepSeek` 等接口，通过代理或配置动态切换。
2. **前端交互**：开发 Vue/React 前端，实现可视化聊天界面，脱离浏览器 URL 传参。
3. **日志与监控**：添加 AOP 日志，记录工具调用流程、耗时，方便排查问题。

**源码地址**：本项目已上传 GitHub，仓库名 `WeatherMCP`，欢迎 Star、Fork 交流～

（注：替换文中 `你的密钥`、`模型地址` 等为实际配置，补充项目截图、演示动图可让 README 更生动 ）

