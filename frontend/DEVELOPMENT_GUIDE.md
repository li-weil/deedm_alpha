# Deedm 前端开发指南

## 📋 概述

本文档描述了 Deedm 离散数学教学演示辅助工具的前端开发流程和架构规范。基于优化后的 Vue 3 + Element Plus 架构，为开发新功能提供完整的指导。

## 🏗️ 架构概览

### 系统架构图
```
┌─────────────────────────────────────────────────────┐
│ 主控制层 (MainView.vue)                              │
│ ├── 顶部导航菜单 (下拉菜单配置)                        │
│ ├── 左右分屏布局 (LeftPanel + RightPanel)             │
│ ├── 模态框管理 (学科View组件)                          │
│ ├── 状态管理 (formulaResults, currentFormula等)       │
│ └── 事件处理 (handleResultWithLatex等)                │
├───────────────────────────────────────────────────── ┤
│ 学科视图层 (学科View组件)                              │
│ ├── PropositionalLogicView.vue  已实现                │
│ ├── SetRelationFunctionView.vue (集合关系函数)        │
│ ├── CombinatoricsView.vue (组合计数)                  │
│ ├── GraphTheoryView.vue (图与树)                      │
│ └── AlgebraStructureView.vue (代数结构)               │
├─────────────────────────────────────────────────────┤
│ 功能组件层 (具体功能实现)                             │
│ └── /components/                                    │
│     ├── logic/ (命题逻辑)                            │
│     ├── setrelfun/ (集合论)                          │
│     ├── count/ (组合数学)                            │
│     ├── graph/ (图论)                               │
│     └── algebra/ (代数结构)                          │
├─────────────────────────────────────────────────────┤
│ 通用组件层                                           │
│ └── /components/common/                             │
│     ├── LeftPanel.vue (左侧结果面板)                 │
│     ├── RightPanel.vue (右侧LaTeX面板)               │
│     └── MathRenderer.vue (数学公式渲染)             │
└─────────────────────────────────────────────────────┘
```

### 数据流向
```
用户操作 → MainView.handleMenuSelect() → 学科View.openFeature()
    ↓
功能组件 → API调用 → 数据格式化 → LaTeX生成
    ↓
emit {result, latexString} → MainView.handleResultWithLatex()
    ↓
LeftPanel显示结果 + RightPanel显示LaTeX
```
## 编写小tip
利用我给出的代码关键字定位，在文件中进行关键字检索，可以避免阅读整个文件导致上下文太长
添加的内容不必放在代码块最后，可以直接放在关键字的下一行，如果放在最后又得读入整个代码块的内容导致上下文太长
## 注意
- 渲染方式选择mathjax,katex对于矩阵的渲染效果不佳
## 图片生成
- 利用graphviz，结合原java应用实现，参考`/home/admin-unix/Deedm/backend/src/main/java/com/deedm/controller/GraphTravelController.java`，`/home/admin-unix/Deedm/backend/src/main/java/com/deedm/service/GraphTravelService.java`


## 🚀 新功能开发标准流程(集合关系函数(S)板块)

### 步骤1：后端接口实现
- **Controller层**: `/backend/src/main/java/com/deedm/controller/setrelfun`对应的子目录板块中
- **Service层**: `/backend/src/main/java/com/deedm/service/setrelfun`对应的子目录板块中
- **Respose**要返回retule.type参数，用来在`/home/admin-unix/Deedm/frontend/src/utils/latexGenerator.js`中判断是哪一个子界面，进而完成latex代码生成逻辑
- 我已经手动完成了legacy代码迁移到`/home/admin-unix/Deedm/backend/src/main/java/com/deedm/legacy`

### 步骤2：前端子界面组件开发
**文件位置**: `/frontend/src/components/setrelfun` 对应板块文件夹
样式参考`/home/admin-unix/Deedm/frontend/src/components/graph/GraphTravelInterface.vue`

### 步骤3：更新LeftPanel.vue
**位置**: `/home/admin-unix/Deedm/frontend/src/components/common/LeftPanel.vue`
- 在 `<div v-for="(result, index) in formulaResults">` 代码块下添加结果显示
- 添加失败处理函数
- 在 `<style scoped>` 添加简明样式，样式和子界面保持一致

### 步骤4：更新latexGenerator.js
**位置**: `/home/admin-unix/Deedm/frontend/src/utils/latexGenerator.js`
添加对应result类型的latex代码生成


### 步骤5：更新学科View文件
**位置**: `/home/admin-unix/Deedm/frontend/src/views/SetRelationFunctionView.vue` 对应板块视图文件
- 在 `<div class="[domain]-modals">` 代码块下添加子界面模态框
- 导入 `@/utils/latexGenerator.js` 组件
- 导入子界面组件
- 使用 `generateLaTeXCode` 函数生成latex代码
- 添加结果处理函数，发送结果到主界面，函数命名和主界面相同，具体实现参考
`/home/admin-unix/Deedm/frontend/src/views/PropositionalLogicView.vue`其中的`onEquivCalculusResult`函数

### 步骤6：更新MainView.vue
**位置**: `/home/admin-unix/Deedm/frontend/src/views/MainView.vue`
- 添加接受子界面信息的函数（如 `onGraphTravelResult`、`onTreeTravelResult`）
具体实现参考`onEquivCalculusResult`
- 函数名与次级界面保持一致



所有板块参考已经实现的命题逻辑板块。
已经实现的命题逻辑板块子界面在`/home/admin-unix/Deedm/frontend/src/components/logic`
命题逻辑板块次级界面`/home/admin-unix/Deedm/frontend/src/views/PropositionalLogicView.vue`



### API调用规范
```javascript
// 使用统一的API调用模式
const callBackendApi = async (endpoint, options = {}) => {
  try {
    const baseUrl = window.location.origin
    const response = await fetch(`${baseUrl}/api${endpoint}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...options.headers
      },
      body: JSON.stringify(options.body),
      ...options
    })

    const result = await response.json()

    if (!response.ok) {
      throw new Error(result.message || `HTTP error! status: ${response.status}`)
    }

    return result
  } catch (error) {
    console.error(`API调用失败 (${endpoint}):`, error)
    throw error
  }
}
```

### 数学公式渲染
```vue
<!-- 统一使用MathRenderer组件 -->
<math-renderer
  :formula="cleanFormulaForDisplay(formula)"
  :type="'katex'"
  :display-mode="true"
  @rendered="onFormulaRendered"
  @error="onFormulaError"
/>
```

## 🧪 测试检查清单

### 功能测试
- [ ] 菜单项点击能正确打开模态框
- [ ] 表单验证正常工作
- [ ] API调用成功，数据格式正确
- [ ] 结果正确显示在左侧面板
- [ ] LaTeX代码正确生成在右侧面板
- [ ] 错误处理正常工作
- [ ] 模态框能正常关闭

### 界面测试
- [ ] 响应式设计正常（移动端适配）
- [ ] 交互元素状态正确（loading、disabled等）
- [ ] 滚动功能正常
- [ ] 数学公式渲染正确
- [ ] 表格显示正确

### 性能测试
- [ ] 大数据量时性能良好
- [ ] 内存使用合理
- [ ] 无内存泄漏
- [ ] 组件卸载时清理资源


