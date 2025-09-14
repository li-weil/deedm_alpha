现在已经实现在主界面左侧的"公式内容"渲染公式和真值表,不会清空原来的公式和真值表.
下一步，点击"开始计算"的同时，把对应公式和真值表的latex公式贴到主界面右侧的"laTex代码"区域

注意你的任务只限于完成你的要求，只构建我需要的文件，不要额外去写其他界面的代码，一次只完成一个要求，防止出错，防止上下文溢出。

实现目标后，在这个文档后面简要说明你的改动，包括创建的新文档。

## 改动说明

已成功实现当用户点击"开始计算"时，同时在主界面右侧的"LaTeX代码"区域显示对应公式和真值表的LaTeX代码。

### 修改的文件：
1. `/home/admin-unix/Deedm/frontend/src/views/MainView.vue`

### 具体改动：

#### 1. 修改onFormulaCalculated方法（第229-242行）：
更新了事件处理逻辑，在添加公式结果的同时生成LaTeX代码并更新到右侧面板：

```javascript
// 处理公式计算完成事件
const onFormulaCalculated = (result) => {
  // 添加到结果列表
  formulaResults.value.push(result)
  // 更新当前显示的公式
  currentFormula.value = result.formula

  // 生成LaTeX代码（公式和真值表）
  const latexString = generateLaTeXCode(result)
  // 更新LaTeX代码区域
  latexCode.value = latexString

  ElMessage.success('公式和真值表已添加到主界面')
}
```

#### 2. 添加generateLaTeXCode函数（第254-287行）：
新增了生成LaTeX代码的函数，将公式和真值表转换为标准LaTeX格式：

```javascript
// 生成LaTeX代码（公式和真值表）
const generateLaTeXCode = (result) => {
  let latexCode = `\\begin{array}{c}\n\\text{公式: } ${result.formula}\n\\end{array}\n\n`

  if (result.formulaType) {
    latexCode += `\\begin{array}{c}\n\\text{公式类型: } ${result.formulaType}\n\\end{array}\n\n`
  }

  // 生成真值表LaTeX代码
  if (result.tableData && result.tableData.headers && result.tableData.rows) {
    const headers = result.tableData.headers
    const rows = result.tableData.rows

    // 计算列数
    const colCount = headers.length
    const colSpec = 'c'.repeat(colCount)

    latexCode += `\\begin{array}{${colSpec}}\n`

    // 添加表头
    latexCode += headers.join(' & ') + ' \\\\\n'
    latexCode += '\\hline\n'

    // 添加表格行
    rows.forEach(row => {
      const rowData = [...row.variableValues, row.resultValue]
      latexCode += rowData.join(' & ') + ' \\\\\n'
    })

    latexCode += '\\end{array}'
  }

  return latexCode
}
```

### 技术改进：
- **LaTeX代码生成**：自动将公式和真值表转换为标准LaTeX格式
- **实时同步**：计算完成时立即更新右侧LaTeX代码区域
- **格式规范**：生成的LaTeX代码包含完整的表格结构和分隔线
- **数据完整性**：包含公式、公式类型（如果有）和完整的真值表数据

### 功能实现：
- **公式显示**：LaTeX代码中包含原始公式的显示
- **类型标注**：如果公式类型可用，会在LaTeX代码中标注
- **表格结构**：使用LaTeX的array环境生成完整的真值表
- **标准格式**：生成的LaTeX代码可以直接用于文档编辑

### 生成的LaTeX代码示例：
```latex
\begin{array}{c}
\text{公式: } p\wedge q
\end{array}

\begin{array}{c}
\text{公式类型: } 可满足式
\end{array}

\begin{array}{ccc}
p & q & p\wedge q \\
\hline
T & T & T \\
T & F & F \\
F & T & F \\
F & F & F \\
\end{array}
```

### 测试状态：
- 前端服务运行在 http://localhost:3000
- 修改已生效，可通过前端界面测试
- 用户点击"开始计算"后，右侧LaTeX代码区域会立即显示生成的LaTeX代码

- LaTeX代码框现已支持自适应屏幕大小，不会与左侧公式内
容区重叠

### 最新改进：
 - **自适应大小**：LaTeX代码框使用autosize属性，可根据
内容自动调整高度
 - **响应式布局**：在不同屏幕尺寸下保持良好的布局和显 示效果
- **防止重叠**：通过flex布局和min-width设置确保左右面
板不会重叠
 - **移动端优化**：在移动设备上自动切换为上下布局，提
高用户体验