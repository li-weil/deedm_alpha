# Deedm 安全审计与清空隔离方案（已落地）

## 1. 方案目标
- 不引入账号系统前提下，满足“数据库安全审计覆盖到每个数据库用户”的检查项。
- 解决“一个访问者点击清空导致其他访问者图片丢失”的风险。
- 保持交付周期内最小改造。

## 2. 审计主体定义
- 主审计用户：`X-Visitor-Id`（前端自动生成并随请求发送）。
- 辅助身份：`X-Forwarded-For` / `RemoteAddr` + `User-Agent`。
- 数据库用户：当前应用连接用户（默认 `sa`），作为 `db_user` 写入审计记录。

说明：当前无登录体系，无法映射真实自然人账号，因此采用“访问者标识 + 网络ip”的应用层审计用户模型。

## 3. 后端实现
- AOP 切面覆盖 `com.deedm.controller..*` 下所有 `POST/PUT/DELETE` 请求。
- 每次写操作写入两处证据：
  - 应用日志（`./logs/deedm-backend.log`，含 `AUDIT ...` 行）
  - H2 审计表 `audit_log`
- `audit_log` 关键字段：
  - `created_at`
  - `action`
  - `request_path`
  - `http_method`
  - `visitor_id`
  - `client_ip`
  - `user_agent`
  - `db_user`
  - `success`
  - `error_message`

## 4. 清空接口安全改造
接口：`POST /api/cleanup/data`

旧行为：无请求体，清空整个 `./data` 目录（高风险，可能误删其他访问者文件，且可能误伤非图片文件）。

新行为：只按白名单删除。
请求体示例：
```json
{
  "filenames": ["AST_abcd.png", "Relation_1234.png"]
}
```

后端校验策略：
- 只允许文件名（禁止路径、`..`、斜杠）。
- 只允许后缀：`.png`、`.dot`。
- 只删除 `app.data.directory`（默认 `./data`）下匹配文件。

## 5. 前端配合
- 全局拦截 `fetch`，自动添加 `X-Visitor-Id`。
- 点击“清空”时，从当前页面结果对象中提取图片文件名，提交到 `/api/cleanup/data`。
- 不再触发“全目录清理”。

## 6. 配置项
- `backend/pom.xml`
  - 新增 `spring-boot-starter-aop`
- `backend/src/main/resources/application.yml`
  - `server.forward-headers-strategy: framework`
  - `logging.file.name: ./logs/deedm-backend.log`
  - `audit.enabled: true`

## 7. 验收与扫描证据
1. 触发任意写接口（如清空、公式计算 POST）。
2. 检查文件日志：
```bash
grep "AUDIT" ./logs/deedm-backend.log | tail -n 20
```
3. 检查数据库审计表（H2）：
```sql
SELECT id, created_at, action, http_method, visitor_id, client_ip, db_user, success
FROM audit_log
ORDER BY id DESC
LIMIT 20;
```
4. 双访问者隔离验证：
- 浏览器 A、B 分别生成图片。
- A 执行清空。
- B 的图片仍可访问。

## 8. 限制与后续建议
- 该方案是“应用层审计用户”，不是实名账号体系。
- 后续正式版建议引入登录用户与按用户目录存储（例如 `/data/{userId}/`），并将审计与业务用户主键强绑定。
