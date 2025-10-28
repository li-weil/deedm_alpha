# Deedm 前端开发指南

## 📋 概述

本文档描述了 Deemm 离散数学教学演示辅助工具的前端开发流程和架构规范。基于优化后的 Vue 3 + Element Plus 架构，为开发新功能提供完整的指导。

## 🏗️ 架构概览

### 系统架构图
```
┌─────────────────────────────────────────────────────┐
│ 主控制层 (MainView.vue)                             │
│ ├── 顶部导航菜单 (下拉菜单配置)                         │
│ ├── 左右分屏布局 (LeftPanel + RightPanel)           │
│ ├── 模态框管理 (学科View组件)                        │
│ ├── 状态管理 (formulaResults, currentFormula等)       │
│ └── 事件处理 (handleResultWithLatex等)                │
├─────────────────────────────────────────────────────┤
│ 学科视图层 (学科View组件)                             │
│ ├── PropositionalLogicView.vue  已实现             │
│ ├── SetRelationFunctionView.vue (集合关系函数)        │
│ ├── CombinatoricsView.vue (组合计数)                 │
│ ├── GraphTheoryView.vue (图与树)                     │
│ └── AlgebraStructureView.vue (代数结构)              │
├─────────────────────────────────────────────────────┤
│ 功能组件层 (具体功能实现)                             │
│ └── /components/[domain]/                           │
│     ├── logic/ (命题逻辑)                            │
│     ├── set/ (集合论)                               │
│     ├── combinatorics/ (组合数学)                   │
│     ├── graph/ (图论)                              │
│     └── algebra/ (代数结构)                         │
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
emit {result, latexString} → MainView.updateState()
    ↓
LeftPanel显示结果 + RightPanel显示LaTeX
```

## 🚀 新功能开发标准流程

### 步骤1：确定功能位置和层级

#### 1.1 确定功能类型
- **现有学科新功能** → 对应的 `View.vue` 文件
- **全新学科领域** → 创建新的 `View.vue` 文件
- **通用功能组件** → `/components/common/`

#### 1.2 目录结构规范
```
frontend/src/
├── views/                    # 学科视图层
│   ├── MainView.vue          # 主控制界面
│   ├── PropositionalLogicView.vue
│   ├── SetRelationFunctionView.vue
│   ├── CombinatoricsView.vue
│   ├── GraphTheoryView.vue
│   └── AlgebraStructureView.vue
├── components/               # 组件层
│   ├── common/              # 通用组件
│   │   ├── LeftPanel.vue
│   │   ├── RightPanel.vue
│   │   └── MathRenderer.vue
│   └── [domain]/            # 领域特定组件
│       ├── logic/
│       ├── set/
│       ├── combinatorics/
│       ├── graph/
│       └── algebra/
```

### 步骤2：更新下拉菜单配置

#### 2.1 在 MainView.vue 中添加菜单项

**文件位置：** `/home/admin-unix/Deedm/frontend/src/views/MainView.vue` (第12-99行)

**示例代码：**
```vue
<!-- 在对应学科菜单中添加新功能 -->
<el-sub-menu index="set-relation-function">
  <template #title>
    <el-icon><Connection /></el-icon>
    集合关系函数(S)
  </template>

  <!-- 现有菜单项 -->
  <el-menu-item index="set-operation">单个集合的运算</el-menu-item>

  <!-- 新增菜单项 -->
  <el-menu-item index="your-new-feature">新功能名称</el-menu-item>
</el-sub-menu>
```

#### 2.2 添加图标（如需要）

**文件位置：** MainView.vue 顶部导入部分
```vue
import { Tools, Connection, Histogram, Share, Notebook, QuestionFilled, YourIcon } from '@element-plus/icons-vue'
```

### 步骤3：实现具体功能组件

#### 3.1 创建功能组件

**文件位置：** `/home/admin-unix/Deedm/frontend/src/components/[domain]/YourFeatureInterface.vue`

### 步骤4：集成到学科视图

#### 4.1 在对应View.vue中添加模态框

**文件位置：**  `/home/admin-unix/Deedm/frontend/src/views/`目录之下对应板块

**添加模态框代码：**
```vue
<!-- 在template中添加新功能的模态框 -->
<el-dialog
  v-model="showYourFeature"
  title="您的新功能标题"
  width="90%"
  :before-close="handleYourFeatureClose"
  class="your-feature-dialog"
  :close-on-click-modal="false"
>
  <your-feature-interface
    @close="showYourFeature = false"
    @result="onYourFeatureResult"
  />
</el-dialog>
```

#### 4.2 添加组件导入和状态管理

**在script setup部分添加：**
```vue
import YourFeatureInterface from '@/components/[domain]/YourFeatureInterface.vue'

// 控制界面显示
const showYourFeature = ref(false)

// 打开界面的方法
const openYourFeature = () => {
  showYourFeature.value = true
}

// 处理界面关闭
const handleYourFeatureClose = () => {
  showYourFeature.value = false
}

// 处理结果（遵循现有模式）
const onYourFeatureResult = (data) => {
  const { result, latexString } = data

  // 如果是命题逻辑功能，在这里生成LaTeX
  if (props.rightPanelRef && props.rightPanelRef.value) {
    const generatedLatexString = props.rightPanelRef.value.generateLaTeXCode(result)

    emit('your-feature-result', {
      result,
      latexString: latexString || generatedLatexString
    })
  } else {
    emit('your-feature-result', { result, latexString })
  }
}

// 暴露方法给父组件
defineExpose({
  // 现有方法...
  openSetOperation,
  openSetExprOperation,
  // 新增方法
  openYourFeature
})
```

#### 4.3 更新emits定义

```vue
const emit = defineEmits([
  'set-operation-result',
  'set-expr-operation-result',
  // 现有emits...
  // 新增emit
  'your-feature-result',
  'update-current-formula',
  'update-latex-code'
])
```

#### 4.4 添加CSS样式

```vue
<style scoped>
/* 在style部分添加新的对话框样式 */
:deep(.your-feature-dialog) {
  display: flex;
  flex-direction: column;
}

:deep(.el-dialog__body) {
  padding: 1rem;
  max-height: 80vh;
  overflow-y: auto;
}
</style>
```

### 步骤5：在MainView.vue中连接事件

#### 5.1 导入学科视图（如果是新学科）

**文件位置：** MainView.vue (第219-227行)
```vue
// 导入次级界面组件
import SetRelationFunctionView from '@/views/SetRelationFunctionView.vue'
import NewSubjectView from '@/views/NewSubjectView.vue'  // 新增学科
```

#### 5.2 添加组件引用

**在template部分添加：**
```vue
<!-- 新学科的模态框组件 -->
<NewSubjectView
  :formula-results="formulaResults"
  :current-formula="currentFormula"
  :latex-code="latexCode"
  :right-panel-ref="rightPanelRef"
  @new-feature-result="onNewFeatureResult"
  @update-current-formula="updateCurrentFormula"
  @update-latex-code="updateLatexCode"
  ref="newSubjectModalRef"
/>
```

#### 5.3 添加组件引用定义

```vue
// 组件引用
const rightPanelRef = ref(null)
const propositionalLogicModalRef = ref(null)
const setRelationFunctionModalRef = ref(null)
const newSubjectModalRef = ref(null)  // 新增
```

#### 5.4 添加菜单处理逻辑

**文件位置：** MainView.vue 的 handleMenuSelect 函数 (第348-384行)
```vue
case 'your-new-feature':
  setRelationFunctionModalRef.value?.openYourFeature()
  break

// 如果是新学科
case 'new-subject-feature':
  newSubjectModalRef.value?.openNewFeature()
  break
```

#### 5.5 添加结果处理函数

**文件位置：** MainView.vue 添加新的处理函数
```vue
// 新功能结果处理函数
const onNewFeatureResult = (data) => {
  const { result, latexString } = data
  handleResultWithLatex(result, latexString, '新功能操作完成')
}

// 新学科结果处理函数
const onNewSubjectFeatureResult = (data) => {
  const { result, latexString } = data
  handleResultWithLatex(result, latexString, '新学科功能完成')
}
```

### 步骤6：更新LeftPanel和RightPanel

#### 6.1 更新LeftPanel.vue

**文件位置：** `/home/admin-unix/Deedm/frontend/src/components/common/LeftPanel.vue`

**添加新的结果显示组件：**
```vue
<!-- 在template的result-item循环中添加新的结果显示 -->
<div v-if="result.type === 'your-feature-type'" class="your-feature-result">
  <h5 class="result-title">您的功能结果：</h5>

  <!-- 自定义显示内容 -->
  <div class="custom-display">
    <div class="result-formula">
      <math-renderer
        :formula="cleanFormulaForDisplay(result.formula)"
        :type="'katex'"
        :display-mode="true"
      />
    </div>

    <!-- 特殊数据展示 -->
    <div v-if="result.data && result.data.specialData" class="special-data">
      <h6>特殊数据：</h6>
      <div class="data-content">
        {{ result.data.specialData }}
      </div>
    </div>

    <!-- 详细信息展示 -->
    <div v-if="result.details" class="details-display">
      <h6>详细信息：</h6>
      <div class="details-content">
        {{ result.details }}
      </div>
    </div>

    <!-- 表格展示 -->
    <div v-if="result.tableData" class="table-display">
      <h6>结果表格：</h6>
      <el-table :data="result.tableData" border size="small">
        <el-table-column
          v-for="(column, index) in result.tableColumns || ['列1', '列2']"
          :key="index"
          :prop="'col' + index"
          :label="column"
        />
      </el-table>
    </div>
  </div>
</div>
```

**添加对应的CSS样式：**
```vue
<style scoped>
.your-feature-result {
  margin: 1rem 0;
  padding: 1.5rem;
  background: linear-gradient(135deg, #f6f9ff 0%, #e8f4fd 100%);
  border-radius: 8px;
  border: 2px solid #4a90e2;
  box-shadow: 0 4px 12px rgba(74, 144, 226, 0.15);
}

.custom-display {
  margin-top: 1rem;
}

.special-data,
.details-display,
.table-display {
  margin: 1rem 0;
  padding: 1rem;
  background: white;
  border-radius: 6px;
  border: 1px solid #d0e3ff;
}

.special-data h6,
.details-display h6,
.table-display h6 {
  margin: 0 0 0.5rem 0;
  color: #2c3e50;
  font-weight: 600;
}
</style>
```

#### 6.2 更新RightPanel.vue

**文件位置：** `/home/admin-unix/Deedm/frontend/src/components/common/RightPanel.vue`

**在generateLaTeXCode函数中添加新的处理分支：**
```javascript
// 在generateLaTeXCode函数中添加新的处理分支
const generateLaTeXCode = (result) => {
  let latexCode = `\\begin{array}{c}\n\\text{公式: } ${result.formula}\n\\end{array}\n\n`

  // ... 现有的处理逻辑 ...

  // 添加新功能类型的LaTeX生成
  if (result.type === 'your-feature-type') {
    latexCode += generateYourFeatureLaTeX(result)
  }

  // ... 其他处理逻辑 ...

  return latexCode
}

// 专门的LaTeX生成函数
const generateYourFeatureLaTeX = (result) => {
  let latex = `\\begin{array}{c}\n\\text{${result.featureName || '功能'}计算结果:}\n\\end{array}\n\n`

  // 添加计算公式
  if (result.formula) {
    latex += `\\begin{array}{c}\n\\text{计算公式: } ${result.formula}\n\\end{array}\n\n`
  }

  // 添加计算步骤
  if (result.data && result.data.steps && result.data.steps.length > 0) {
    latex += `\\begin{array}{c}\n\\text{计算步骤:}\n\\end{array}\n\n`
    result.data.steps.forEach((step, index) => {
      latex += `\\begin{array}{c}\n\\text{步骤${index + 1}: } ${step.description || ''}\n${step.formula || ''}\n\\end{array}\n\n`
    })
  }

  // 添加最终结果
  if (result.data && result.data.finalResult) {
    latex += `\\begin{array}{c}\n\\text{最终结果: } ${result.data.finalResult}\n\\end{array}\n\n`
  }

  // 添加详细信息
  if (result.details) {
    latex += `\\begin{array}{c}\n\\text{详细信息:}\n${result.details}\n\\end{array}\n\n`
  }

  // 添加表格（如果有）
  if (result.tableData && result.tableData.length > 0) {
    latex += generateTableLaTeX(result.tableData, result.tableColumns)
  }

  return latex
}

// 通用表格LaTeX生成函数
const generateTableLaTeX = (tableData, columns) => {
  if (!tableData || !tableData.length || !columns) return ''

  const colCount = columns.length
  const colSpec = 'c'.repeat(colCount)

  let latex = `\\begin{array}{${colSpec}}\n`

  // 表头
  latex += columns.join(' & ') + ' \\\\\n'
  latex += '\\hline\n'

  // 表格行
  tableData.forEach((row, index) => {
    const rowData = columns.map((col, colIndex) => {
      return row[`col${colIndex}`] || row[colIndex] || ''
    })
    latex += rowData.join(' & ') + ' \\\\\n'
  })

  latex += '\\end{array}\n\n'
  return latex
}
```

## 📝 开发最佳实践

### 代码规范
1. **Vue 3 Composition API**：使用 `<script setup>` 语法
2. **TypeScript支持**：使用类型提示提高代码质量
3. **组件命名**：使用 PascalCase，功能组件以 `Interface.vue` 结尾
4. **文件组织**：按功能域组织组件文件

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

### 错误处理规范
```javascript
// 使用统一的错误处理
const handleError = (error, operation) => {
  console.error(`${operation}失败:`, error)
  ElMessage.error(`${operation}失败: ${error.message || '未知错误'}`)
}

const handleAsyncError = async (asyncFunction, errorMessage) => {
  try {
    return await asyncFunction()
  } catch (error) {
    console.error(`${errorMessage}:`, error)
    ElMessage.error(`${errorMessage}: ${error.message || '未知错误'}`)
    return null
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

## 🔄 不同界面间的协作关系

### MainView ↔ 学科View 协作
```javascript
// MainView 传递给学科View的数据
:formula-results="formulaResults"    // 用于索引计算
:current-formula="currentFormula"    // 当前公式
:latex-code="latexCode"             // LaTeX代码
:right-panel-ref="rightPanelRef"    // LaTeX生成器引用

// 学科View 返回给MainView的数据
emit('feature-result', { result, latexString })
```

### 学科View ↔ 功能组件 协作
```javascript
// 学科View 管理模态框显示
const showFeature = ref(false)

// 功能组件 处理具体逻辑
emit('result', formattedResult)  // 包含LaTeX字符串
emit('close')  // 关闭模态框
```

### MainView ↔ RightPanel 协作
```javascript
// MainView 调用 LaTeX生成
if (rightPanelRef.value) {
  const latexString = rightPanelRef.value.generateLaTeXCode(result)
  updateLatexCode(latexString)
}
```

### 数据流转示例
```
用户输入 → 功能组件 → API → {result, latexString} → 学科View
    ↓
学科View → emit → MainView → 更新状态
    ↓
MainView → formulaResults.push(result) → LeftPanel显示
MainView → updateLatexCode(latexString) → RightPanel显示
```

## 📚 常见问题和解决方案

### Q1: 如何处理复杂的数学符号？
A: 使用 MathRenderer 组件，支持 KaTeX 和 MathJax 两种渲染引擎。

### Q2: 如何优化大数据量显示？
A:
- 使用虚拟滚动
- 分页显示
- 延迟加载
- 数据缓存

### Q3: 如何实现导出功能？
A:
```javascript
const handleExport = async (format) => {
  switch (format) {
    case 'latex':
      exportAsLatex()
      break
    case 'pdf':
      exportAsPdf()
      break
    case 'csv':
      exportAsCsv()
      break
  }
}
```

### Q4: 如何处理API错误？
A: 使用统一的错误处理机制，提供友好的用户提示。

### Q5: 如何优化性能？
A:
- 使用 computed 缓存计算结果
- 避免不必要的重新渲染
- 使用 debounce 处理频繁操作
- 合理使用 watch 和 watchEffect

## 🎯 总结

遵循本指南，你可以：
1. **高效开发**：标准化的流程和模板
2. **保证质量**：完善的测试清单和最佳实践
3. **易于维护**：清晰的架构和代码规范
4. **团队协作**：统一的开发标准和文档

如有疑问，请参考现有代码实现或咨询项目负责人。

---

*最后更新：2025年10月*