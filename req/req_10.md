现在已经实现清除原来的真值表结果,
下一步，当用户点击"开始计算"时，在主界面左侧的"公式内容"渲染公式和真值表，和窗口界面不同，主界面不会清空原来的公式和真值表

注意你的任务只限于完成你的要求，只构建我需要的文件，不要额外去写其他界面的代码，一次只完成一个要求，防止出错，防止上下文溢出。

实现目标后，在这个文档后面简要说明你的改动，包括创建的新文档。

## 改动说明

已成功实现当用户点击"开始计算"时，在主界面左侧的"公式内容"渲染公式和真值表的功能。主界面会保留所有之前计算的公式和真值表，不会清空。

### 修改的文件：
1. `/home/admin-unix/Deedm/frontend/src/components/logic/TruthTableInterface.vue`
2. `/home/admin-unix/Deedm/frontend/src/views/MainView.vue`

### 具体改动：

#### 1. TruthTableInterface.vue 修改：

**添加新的事件发射器（第220行）：**
```javascript
const emit = defineEmits(['close', 'formula-calculated'])
```

**修改startCalculation方法（第264-269行）：**
在计算成功后，将结果发射给父组件：
```javascript
// 发送公式计算结果到主界面
emit('formula-calculated', {
  formula: cleanFormulaForDisplay(formulas[0]),
  tableData: tableData,
  formulaType: checkFormulaType.value && tableData.formulaType ? translateFormulaType(tableData.formulaType) : null
})
```

#### 2. MainView.vue 修改：

**添加事件监听器（第79行）：**
```html
<truth-table-interface @close="showTruthTable = false" @formula-calculated="onFormulaCalculated" />
```

**添加数据存储（第97行）：**
```javascript
const formulaResults = ref([])
```

**添加事件处理方法（第229-248行）：**
```javascript
// 处理公式计算完成事件
const onFormulaCalculated = (result) => {
  // 添加到结果列表
  formulaResults.value.push(result)
  // 更新当前显示的公式
  currentFormula.value = result.formula
  // 更新LaTeX代码
  latexCode.value = result.formula
  ElMessage.success('公式和真值表已添加到主界面')
}
```

**更新左侧面板模板（第51-98行）：**
添加了公式结果和真值表的显示区域，支持显示多个公式及其对应的真值表。

**添加样式（第372-452行）：**
为新添加的公式结果和真值表添加了完整的样式支持。

### 技术改进：
- **组件通信**：通过Vue的事件发射机制实现子组件向父组件传递数据
- **数据持久化**：主界面维护公式结果列表，不会清空历史数据
- **响应式更新**：公式结果变化时自动更新界面显示
- **样式一致性**：主界面的真值表样式与弹窗界面保持一致

### 功能实现：
- **结果累积**：主界面会保存所有计算过的公式和真值表
- **实时更新**：点击"开始计算"后立即在主界面显示结果
- **完整信息**：显示公式、真值表、公式类型等完整信息
- **用户体验**：与弹窗界面不同，主界面提供持久化的结果展示

### 测试状态：
- 前端服务运行在 http://localhost:3000
- 修改已生效，可通过前端界面测试
- 用户现在可以多次计算公式，主界面会累积显示所有结果
