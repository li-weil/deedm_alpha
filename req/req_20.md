当主界面点击清空按钮，应清理/data目录下的文件，防止每次生成的内容堆积起来

把项目改动简要写在后面：

## 已完成的功能实现

### 后端实现
1. **创建了文件清理工具类** (`/backend/src/main/java/com/deedm/util/FileCleanupUtil.java`)
   - 提供清理data目录的功能
   - 支持获取文件统计信息
   - 包含错误处理和日志记录

2. **创建了清理控制器** (`/backend/src/main/java/com/deedm/controller/CleanupController.java`)
   - `POST /api/cleanup/data` - 清理data目录接口
   - `GET /api/cleanup/status` - 获取目录状态接口
   - 返回详细的清理结果和统计信息

### 前端实现
3. **更新主界面清空功能** (`/frontend/src/views/MainView.vue`)
   - 只有左侧面板的"清空"按钮会清理data目录
   - 右侧LaTeX面板的"清空"按钮只清空LaTeX代码
   - 添加了axios调用后端清理接口
   - 包含错误处理，避免后端失败影响前端操作

### 功能特点
- ✅ 左侧面板清空按钮：清空前端内容 + 后端data目录
- ✅ 右侧LaTeX面板清空按钮：仅清空LaTeX代码
- ✅ 后端API返回详细的清理统计信息
- ✅ 前端显示友好的成功/失败消息
- ✅ 错误处理确保前端操作不受后端失败影响

### API接口
- `POST /api/cleanup/data` - 执行清理操作
- `GET /api/cleanup/status` - 查看目录状态