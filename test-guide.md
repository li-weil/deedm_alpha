# 真值表功能测试指南

## 1. 后端API测试

打开终端，运行以下命令测试后端API：

### 测试AND运算 (p ∧ q)
```bash
curl -X POST http://localhost:8080/api/truth-table/generate \
  -H "Content-Type: application/json" \
  -d '{"formulas": ["p\\\\wedge q"], "detailed": true, "checkType": true}'
```

### 测试OR运算 (p ∨ q)
```bash
curl -X POST http://localhost:8080/api/truth-table/generate \
  -H "Content-Type: application/json" \
  -d '{"formulas": ["p\\\\vee q"], "detailed": true, "checkType": true}'
```

### 测试NOT运算 (¬p)
```bash
curl -X POST http://localhost:8080/api/truth-table/generate \
  -H "Content-Type: application/json" \
  -d '{"formulas": ["\\\\neg p"], "detailed": true, "checkType": true}'
```

### 测试蕴含运算 (p → q)
```bash
curl -X POST http://localhost:8080/api/truth-table/generate \
  -H "Content-Type: application/json" \
  -d '{"formulas": ["p\\\\rightarrow q"], "detailed": true, "checkType": true}'
```

## 2. 前端界面测试

### 启动服务
确保前后端服务都在运行：
- 后端：Spring Boot (端口 8080)
- 前端：Vue.js (端口 3000)

### 访问前端
在浏览器中打开：http://localhost:3000

### 测试步骤
1. 点击导航菜单中的"真值表"或相关功能入口
2. 在公式输入框中输入LaTeX格式的逻辑公式，例如：
   - `p\wedge q` (p与q)
   - `p\vee q` (p或q)
   - `\neg p` (非p)
   - `p\rightarrow q` (p蕴含q)

3. 点击"开始计算(Y)"按钮
4. 查看生成的HTML真值表

## 3. 预期结果

### 后端API应该返回：
```json
{
  "columnHeaders": ["$p$", "$q$", "$p\\wedge q$"],
  "rows": [
    {"variableValues": ["$\\mathbf{0}$", "$\\mathbf{0}$"], "resultValue": "$\\mathbf{0}$"},
    {"variableValues": ["$\\mathbf{0}$", "$\\mathbf{1}$"], "resultValue": "$\\mathbf{1}$"},
    {"variableValues": ["$\\mathbf{1}$", "$\\mathbf{0}$"], "resultValue": "$\\mathbf{1}$"},
    {"variableValues": ["$\\mathbf{1}$", "$\\mathbf{1}$"], "resultValue": "$\\mathbf{1}$"}
  ],
  "formulaType": "contingency",
  "errorMessage": null
}
```

### 前端界面应该显示：
- 美观的HTML表格，包含数学符号渲染
- 公式类型判断（重言式、矛盾式、可满足式）
- 正确的真值表结构

## 4. 故障排除

### 如果后端无法访问：
```bash
# 检查端口占用
lsof -i :8080

# 重启后端服务
cd /home/admin-unix/Deedm/backend
mvn spring-boot:run
```

### 如果前端无法访问：
```bash
# 检查端口占用
lsof -i :3000

# 重启前端服务
cd /home/admin-unix/Deedm/frontend
npm run dev
```

## 5. 测试公式示例

你可以尝试以下公式：
- `p\wedge q\wedge r` (三变量与运算)
- `(p\vee q)\rightarrow r` (复杂蕴含)
- `\neg(p\wedge q)` (德摩根律)
- `p\leftrightarrow q` (等价运算)

每个公式都应该生成正确的真值表！