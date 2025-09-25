<template>
  <!-- 相当于HTML的div容器，包含整个页面内容 -->
  <div class="main-view">
    <!-- 顶部导航栏 - 类似HTML的header，使用Element Plus组件库 -->
    <el-header class="header">
      <!-- 水平菜单，类似HTML的nav -->
      <el-menu
        mode="horizontal"  
        :default-active="activeMenu" 
        class="main-menu"
        @select="handleMenuSelect" 
      >
        <!-- 命题逻辑菜单 - 可展开的子菜单，类似HTML的dropdown -->
        <el-sub-menu index="propositional-logic">
          <template #title>命题逻辑</template>  <!-- 子菜单标题 -->
          <el-menu-item index="truth-table">构造公式真值表</el-menu-item>  <!-- 菜单项，点击触发index="truth-table" -->
          <el-menu-item index="normal-form">扩展范式为主范式</el-menu-item>
        </el-sub-menu>

        <!-- 集合关系函数菜单 - 另一个可展开的子菜单 -->
        <el-sub-menu index="set-relation-function">
          <template #title>集合关系函数</template>
          <el-menu-item index="set-operations">集合运算</el-menu-item>
          <el-menu-item index="relation-operations">关系运算</el-menu-item>
          <el-menu-item index="relation-properties">关系性质判断</el-menu-item>
          <el-menu-item index="relation-closure">关系闭包</el-menu-item>
          <el-menu-item index="equivalence-relations">等价关系</el-menu-item>
          <el-menu-item index="partial-order">偏序关系</el-menu-item>
          <el-menu-item index="function-properties">函数性质判断</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-header>

    <!-- 主要内容区域 - 左右分屏布局，类似HTML的main -->
    <el-container class="main-container">
      <!-- 左侧面板 - 显示公式内容，类似HTML的aside侧边栏 -->
      <el-aside class="left-panel" @wheel="handleLeftWheel">  <!-- 监听鼠标滚轮事件 -->
        <div class="panel-header">
          <h3>公式内容</h3>
          <!-- 按钮，点击时调用clearFormulaContent函数 -->
          <el-button size="small" type="warning" @click="clearFormulaContent">
            <el-icon><Delete /></el-icon>  <!-- 删除图标 -->
            清空
          </el-button>
        </div>
        <div class="panel-content" ref="leftContent">  <!-- DOM元素引用，用于JavaScript操作 -->
          <!-- 公式内容将在这里显示 -->
          <div class="formula-display">
            <!-- 数学公式渲染组件 -->
            <math-renderer
              :formula="cleanFormulaForDisplay(currentFormula)"
              :type="'katex'"
              :display-mode="true"
              @rendered="onFormulaRendered"
              @error="onFormulaError"
            />
          </div>

          <!-- 显示所有公式和真值表 - 条件渲染：只有当formulaResults数组有内容时才显示 -->
          <div v-if="formulaResults.length > 0" class="formula-results">
            <!-- 循环遍历formulaResults数组，v-for类似for循环，:key用于Vue性能优化 -->
            <div v-for="(result, index) in formulaResults" :key="index" class="result-item">
              <div class="result-formula">
                <strong>公式 {{ index + 1 }}: </strong>
                <!-- 数学公式渲染组件 -->
                <math-renderer
                  :formula="cleanFormulaForDisplay(result.formula)"
                  :type="'katex'"
                  :display-mode="false"
                />
              </div>

              <!-- 显示真值表 - 优先使用新的LaTeX表格格式 -->
              <div class="truth-table">
                <!-- 如果有latexTable，使用MathRenderer渲染 -->
                <div v-if="result.tableData && result.tableData.latexTable" class="truth-table-container">
                  <math-renderer
                    :key="'main-table-' + index + '-' + Date.now()"
                    :formula="result.tableData.latexTable"
                    :type="'katex'"
                    :display-mode="true"
                    class="truth-table-content"
                  />
                </div>
                <!-- 保持原有的HTML表格作为后备 -->
                <div v-else-if="result.tableData && result.tableData.headers" class="truth-table-legacy">
                  <table class="truth-table-html">
                    <thead>  <!-- 表头 -->
                      <tr>
                        <!-- 循环生成表头单元格 -->
                        <th v-for="(header, headerIndex) in result.tableData.headers" :key="headerIndex" class="header-cell">
                          <math-renderer
                            :formula="cleanFormulaForDisplay(header)"
                            :type="'katex'"
                            :display-mode="false"
                          />
                        </th>
                      </tr>
                    </thead>
                    <tbody>  <!-- 表格内容 -->
                      <!-- 循环生成表格行 -->
                      <tr v-for="(row, rowIndex) in result.tableData.rows" :key="rowIndex">
                        <!-- 循环生成表格单元格：合并变量值和结果值 -->
                        <td v-for="(cell, cellIndex) in [...row.variableValues, row.resultValue]" :key="cellIndex" class="data-cell">
                          <math-renderer
                            :formula="cleanFormulaForDisplay(cell)"
                            :type="'katex'"
                            :display-mode="false"
                          />
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
                <!-- 最终后备：显示原始truthTable字符串 -->
                <div v-else-if="result.truthTable" class="truth-table-fallback">
                  <math-renderer
                    :formula="result.truthTable"
                    :type="'katex'"
                    :display-mode="true"
                    class="truth-table-content"
                  />
                </div>
              </div>

              <!-- 显示公式类型 - 条件渲染：只有当result.formulaType存在时才显示 -->
              <div v-if="result.formulaType" class="formula-type">
                <!-- 标签组件，显示公式类型（重言式、矛盾式等） -->
                <el-tag :type="getFormulaTypeTag(result.formulaType)" class="type-tag">
                  {{ result.formulaType }}
                </el-tag>
              </div>
            </div>
          </div>
        </div>
      </el-aside>

      <!-- 右侧面板 - LaTeX代码 -->
      <el-aside class="right-panel" @wheel="handleRightWheel">  <!-- 监听鼠标滚轮事件 -->
        <div class="panel-header">
          <h3>LaTeX 代码</h3>
          <!-- 清空按钮 -->
          <el-button size="small" type="warning" @click="clearLatexCode">
            <el-icon><Delete /></el-icon>
            清空
          </el-button>
        </div>
        <div class="panel-content" ref="rightContent">  <!-- DOM元素引用 -->
          <!-- 文本输入框组件，只读显示LaTeX代码 -->
          <el-input
            v-model="latexCode"  
            type="textarea"      
            :rows="1"
            placeholder="LaTeX代码将在这里显示..."
            readonly             
            class="latex-textarea"
            :autosize="{ minRows: 10, maxRows: 50 }"  
          />
        </div>
      </el-aside>
    </el-container>

    <!-- 真值表界面模态框 - 弹出对话框 -->
    <el-dialog
      v-model="showTruthTable" 
      title="构造公式真值表"
      width="90%"
      :before-close="handleTruthTableClose" 
      class="truth-table-dialog"
    >
      <!-- 引入真值表界面组件 -->
      <truth-table-interface
        @close="showTruthTable = false"  
        @formula-calculated="onFormulaCalculated" 
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'
import MathRenderer from '@/components/common/MathRenderer.vue'
import TruthTableInterface from '@/components/logic/TruthTableInterface.vue'

// 响应式数据
const activeMenu = ref('')
const currentFormula = ref('\\forall x \\in S, P(x) \\rightarrow Q(x)')
const formulaType = ref('latex')
const latexCode = ref('\\forall x \\in S, P(x) \\rightarrow Q(x)')

// 存储公式和真值表结果
const formulaResults = ref([])

// 控制真值表界面的显示
const showTruthTable = ref(false)

// 左右面板内容区域的引用
const leftContent = ref(null)
const rightContent = ref(null)

// 处理左侧面板鼠标滚轮事件
const handleLeftWheel = (event) => {
  event.preventDefault()
  event.stopPropagation()

  if (leftContent.value) {
    leftContent.value.scrollTop += event.deltaY
  }
}

// 处理右侧面板鼠标滚轮事件
const handleRightWheel = (event) => {
  event.preventDefault()
  event.stopPropagation()

  if (rightContent.value) {
    rightContent.value.scrollTop += event.deltaY
  }
}

// 公式渲染完成回调
const onFormulaRendered = () => {
  console.log('Formula rendered successfully')
}

// 公式渲染错误回调
const onFormulaError = (error) => {
  console.error('Formula rendering error:', error)
  ElMessage.error('公式渲染失败')
}

// 处理菜单项点击
const handleMenuSelect = (index) => {
  console.log('Selected menu item:', index)
  // 这里可以根据不同的菜单项执行不同的操作
  switch (index) {
    case 'truth-table':
      showTruthTable.value = true
      ElMessage.info('构造公式真值表界面已打开')
      break
    case 'normal-form':
      ElMessage.info('扩展范式为主范式功能')
      break
    case 'set-operations':
      ElMessage.info('集合运算功能')
      break
    case 'relation-operations':
      ElMessage.info('关系运算功能')
      break
    case 'relation-properties':
      ElMessage.info('关系性质判断功能')
      break
    case 'relation-closure':
      ElMessage.info('关系闭包功能')
      break
    case 'equivalence-relations':
      ElMessage.info('等价关系功能')
      break
    case 'partial-order':
      ElMessage.info('偏序关系功能')
      break
    case 'function-properties':
      ElMessage.info('函数性质判断功能')
      break
    default:
      break
  }
}

// 处理真值表界面关闭
const handleTruthTableClose = () => {
  showTruthTable.value = false
}

// 处理公式计算完成事件
const onFormulaCalculated = (result) => {
  // 添加到结果列表
  formulaResults.value.push(result)
  // 更新当前显示的公式
  currentFormula.value = result.formula

  // 生成LaTeX代码（公式和真值表）
  const latexString = generateLaTeXCode(result)
  // 追加到LaTeX代码区域，不清空之前的内容
  if (latexCode.value) {
    latexCode.value += '\n\n' + latexString
  } else {
    latexCode.value = latexString
  }

  ElMessage.success('公式和真值表已添加到主界面')
}

// 获取公式类型标签样式
const getFormulaTypeTag = (type) => {
  switch (type) {
    case '重言式': return 'success'
    case '可满足式': return 'warning'
    case '矛盾式': return 'danger'
    default: return 'info'
  }
}

// 清空公式内容
const clearFormulaContent = () => {
  formulaResults.value = []
  currentFormula.value = '\\forall x \\in S, P(x) \\rightarrow Q(x)'
  ElMessage.success('公式内容已清空')
}

// 清空LaTeX代码
const clearLatexCode = () => {
  latexCode.value = ''
  ElMessage.success('LaTeX代码已清空')
}

// 统一处理公式格式，将双反斜杠转换为单反斜杠
const normalizeFormulaFormat = (formula) => {
  return formula.replace(/\\\\([a-zA-Z]+)/g, '\\$1')  // 将双反斜杠转换为单反斜杠
}

// 清理公式中的反斜杠，用于显示
const cleanFormulaForDisplay = (formula) => {
  return normalizeFormulaFormat(formula)
}

// 生成LaTeX代码（公式和真值表）
const generateLaTeXCode = (result) => {
  let latexCode = `\\begin{array}{c}\n\\text{公式: } ${result.formula}\n\\end{array}\n\n`

  if (result.formulaType) {
    latexCode += `\\begin{array}{c}\n\n\\text{公式类型: } ${result.formulaType}\n\n\\end{array}\n\n`
  }

  // 生成真值表LaTeX代码 - 优先使用新的latexTable格式
  if (result.tableData && result.tableData.latexTable) {
    // 直接使用后端返回的LaTeX表格
    latexCode += result.tableData.latexTable
  }
  // 保持原有的表格格式作为后备
  else if (result.tableData && result.tableData.headers && result.tableData.rows) {
    const headers = result.tableData.headers
    const rows = result.tableData.rows

    // 计算列数
    const colCount = headers.length
    const colSpec = 'c'.repeat(colCount)

    latexCode += `\\begin{array}{${colSpec}}\n`

    // 添加表头 - 使用数学符号格式
    const formattedHeaders = headers.map(header => {
      // 如果是单个变量，添加$符号
      if (/^[a-zA-Z]$/.test(header)) {
        return `$${header}$`
      }
      return header
    })
    latexCode += formattedHeaders.join(' & ') + ' \\\\\n'
    latexCode += '\\hline\n'

    // 添加表格行 - 使用粗体数学符号格式
    rows.forEach(row => {
      const rowData = [...row.variableValues, row.resultValue]
      const formattedRow = rowData.map(cell => {
        // 如果是T/F值，转换为0/1并添加粗体数学符号
        if (cell === 'T' || cell === 'F') {
          const value = cell === 'T' ? '1' : '0'
          return `$\\mathbf{${value}}$`
        }
        // 如果是单个变量，添加$符号
        if (/^[a-zA-Z]$/.test(cell)) {
          return `$${cell}$`
        }
        return cell
      })
      latexCode += formattedRow.join(' & ') + ' \\\\\n'
    })

    latexCode += '\\end{array}'
  }

  return latexCode
}

onMounted(() => {
  // 组件挂载后的初始化逻辑
  console.log('Main view mounted')
})
</script>

<style scoped>
.main-view {
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.main-menu {
  background: none;
  border: none;
}

.main-menu :deep(.el-sub-menu__title),
.main-menu :deep(.el-menu-item) {
  color: white;
  border-bottom: 2px solid transparent;
}

.main-menu :deep(.el-sub-menu__title:hover),
.main-menu :deep(.el-menu-item:hover),
.main-menu :deep(.el-sub-menu__title.is-active),
.main-menu :deep(.el-menu-item.is-active) {
  background: rgba(255, 255, 255, 0.1);
  border-bottom-color: white;
}

.main-container {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.left-panel,
.right-panel {
  width: 50%;
  height: 100%;
  border: 1px solid #e4e7ed;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  min-width: 300px;
  flex: 1;
}

.left-panel {
  border-right: 2px solid #dcdfe6;
}

.panel-header {
  background: #f5f7fa;
  padding: 1rem;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.panel-header h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 1.1rem;
  flex: 1;
}

.panel-content {
  flex: 1;
  overflow-y: auto;
  padding: 1rem;
  scroll-behavior: smooth;
}

.formula-display {
  min-height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  background: #fafafa;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.latex-textarea {
  width: 100%;
  height: 100%;
  min-height: 200px;
}

.latex-textarea :deep(.el-textarea__inner) {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.5;
  resize: none;
  border: none;
  background: #fafafa;
  height: 100% !important;
  min-height: 200px;
  max-height: 80vh;
}

/* 滚动条样式 */
.panel-content::-webkit-scrollbar {
  width: 8px;
}

.panel-content::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.panel-content::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.panel-content::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}

/* 公式结果样式 */
.formula-results {
  margin-top: 2rem;
}

.result-item {
  margin-bottom: 2rem;
  padding: 1.5rem;
  background: white;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.result-formula {
  margin-bottom: 1rem;
  font-size: 1.1rem;
  font-weight: 500;
}

.result-formula strong {
  color: #2c3e50;
}

.truth-table {
  margin: 1rem 0;
  background: white;
  padding: 1rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

/* 新的LaTeX表格容器样式 */
.truth-table-container {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.truth-table-container .truth-table-content {
  width: 100%;
  max-width: 100%;
  overflow-x: auto;
}

/* 后备样式 */
.truth-table-legacy,
.truth-table-fallback {
  width: 100%;
}

/* HTML表格样式 */
.truth-table-html {
  width: 100%;
  border-collapse: collapse;
  border: 2px solid #dee2e6;
  border-radius: 8px;
  overflow: hidden;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.truth-table-html th {
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  padding: 0.75rem;
  text-align: center;
  font-weight: 600;
  color: #495057;
}

.truth-table-html td {
  border: 1px solid #dee2e6;
  padding: 0.75rem;
  text-align: center;
  vertical-align: middle;
}

.truth-table-html thead th {
  border-bottom: 2px solid #dee2e6;
}

.truth-table-html tbody tr:nth-child(even) {
  background: #f8f9fa;
}

.truth-table-html tbody tr:hover {
  background: #e9ecef;
}

.formula-type {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-top: 1rem;
}

.type-tag {
  margin-left: 0.5rem;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .main-container {
    flex-direction: column;
  }

  .left-panel,
  .right-panel {
    width: 100%;
    height: 50%;
    min-width: unset;
  }

  .left-panel {
    border-right: 1px solid #e4e7ed;
    border-bottom: 2px solid #dcdfe6;
  }

  .latex-textarea :deep(.el-textarea__inner) {
    max-height: 40vh;
  }
}

@media (max-width: 480px) {
  .panel-header {
    padding: 0.5rem;
  }

  .panel-header h3 {
    font-size: 1rem;
  }

  .panel-content {
    padding: 0.5rem;
  }

  .latex-textarea :deep(.el-textarea__inner) {
    font-size: 12px;
  }
}
</style>