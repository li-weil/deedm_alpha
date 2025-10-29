<template>
  <div class="truth-table-constructor">
    <!-- 按钮操作区域 -->
    <div class="button-section">
      <!-- 第一行按钮：开始计算、生成公式、移除公式、公式合法性检查 -->
      <el-row :gutter="20" class="button-row">
        <el-col :span="4">
          <el-button type="primary" @click="startCalculation">
            <el-icon><Check /></el-icon>
            开始生成(Y)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="success" @click="generateFormula">
            <el-icon><MagicStick /></el-icon>
            生成公式(G)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="warning" @click="removeFormula">
            <el-icon><Delete /></el-icon>
            移除公式(V)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="info" @click="checkFormula">
            <el-icon><WarningFilled /></el-icon>
            合法性检查(K)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="danger" @click="clearResults">
            <el-icon><RefreshRight /></el-icon>
            清除结果(C)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button @click="closeInterface">
            <el-icon><Close /></el-icon>
            取消(R)
          </el-button>
        </el-col>
      </el-row>

      <!-- 第二行按钮：示例公式 -->
      <el-divider content-position="left">《离散数学基础》教材示例展示</el-divider>
      <el-row :gutter="15" class="example-buttons">
        <el-col :span="4">
          <el-button size="small" @click="loadExample('example2_4')">例题2.4</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem2_6_1')">例题2.6(1)</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem2_6_2')">例题2.6(2)</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem2_6_3')">例题2.6(3)</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem2_6_4')">例题2.6(4)</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem2_17')">例题2.17</el-button>
        </el-col>
      </el-row>

      <!-- 选项设置 -->
      <el-divider content-position="left">选择显示内容</el-divider>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-checkbox v-model="showDetailedTable" size="large">
            显示详细公式真值表
          </el-checkbox>
        </el-col>
        <el-col :span="12">
          <el-checkbox v-model="checkFormulaType" size="large">
            判断公式是否为重言式、可满足式或矛盾式
          </el-checkbox>
        </el-col>
      </el-row>
    </div>

    <!-- 公式输入区域 -->
    <div class="formula-input-section">
      <el-divider content-position="left">公式输入</el-divider>
      <el-input
        v-model="formulaInput"
        type="textarea"
        :rows="4"
        placeholder="请输入LaTeX格式的逻辑公式"
        class="formula-textarea"
      />
      <div class="input-hint">
        <el-text type="info" size="small">
          支持的LaTeX符号：\neg(¬), \wedge(∧), \vee(∨), \rightarrow(→), \leftrightarrow(↔)
        </el-text>
      </div>
    </div>

    <!-- 反馈信息区域，如果有反馈就渲染组件 -->
    <div v-if="feedback.length > 0" class="feedback-section">
      <el-divider content-position="left">反馈信息</el-divider>
      <div class="feedback-content">
        <div v-for="(item, index) in feedback" :key="index" class="feedback-item">
          <math-renderer
            :formula="item.formula"
            :type="'katex'"
            :display-mode="false"
            class="feedback-formula"
          />
          <el-text v-if="item.message" :type="item.type" class="feedback-message">
            {{ item.message }}
          </el-text>
        </div>
      </div>
    </div>

    <!-- 结果显示区域 -->
    <div v-if="results.length > 0" class="results-section">
      <el-divider content-position="left">真值表结果</el-divider>
      <div class="results-content">
        <div v-for="(result, index) in results" :key="index" class="result-item">
          <div class="result-formula">
            <math-renderer
              :formula="result.formula"
              :type="'katex'"
              :display-mode="false"
            />
          </div>


            <!-- 直接显示来自legacy的LaTeX表格 -->
          <div v-if="result.tableData && result.tableData.latexTable" class="truth-table-wrapper">
            <math-renderer
              :key="'table-' + result.index + '-' + Date.now()"
              :formula="result.tableData.latexTable"
              :type="'katex'"
              :display-mode="true"
              class="truth-table-content"
            />
          </div>
            


          <div v-if="checkFormulaType && result.tableData && result.tableData.formulaType" class="formula-type">
            <math-renderer
              :formula="result.formula"
              :type="'katex'"
              :display-mode="false"
              class="type-formula"
            />
            <el-tag :type="getFormulaTypeTag(result.tableData.formulaType)" class="type-tag">
              {{ translateFormulaType(result.tableData.formulaType) }}
            </el-tag>
          </div>

        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Check,
  MagicStick,
  Delete,
  WarningFilled,
  RefreshRight,
  Close
} from '@element-plus/icons-vue'
import MathRenderer from '@/components/common/MathRenderer.vue'

// 响应式数据
const formulaInput = ref('')
const showDetailedTable = ref(true)
const checkFormulaType = ref(true)
const feedback = ref([])
const results = ref([])
const counter = ref(0)

// 示例公式映射
const exampleFormulas = {
  example2_4: '(\\neg(\\neg p)\\rightarrow(t\\vee s))\\wedge(s\\leftrightarrow r)',
  problem2_6_1: 'p\\wedge(\\neg s)\\wedge(\\neg s)',
  problem2_6_2: '\\neg(\\neg r)\\rightarrow(\\neg(q\\vee p))',
  problem2_6_3: '((p\\vee p)\\leftrightarrow(p\\wedge s))\\rightarrow(p\\wedge s)',
  problem2_6_4: '(r\\rightarrow(q\\leftrightarrow s))\\vee(q\\leftrightarrow s)',
  problem2_17: '\\neg(p\\rightarrow q)\\wedge((\\neg q)\\leftrightarrow r)'
}

// 事件处理函数
const emit = defineEmits(['close', 'formula-calculated'])

const startCalculation = async () => {
  if (!formulaInput.value.trim()) {
    ElMessage.warning('请先输入公式')
    return
  }

  // 清空之前的反馈和结果
  feedback.value = []
  results.value = []  // 清除原来的真值表结果

  // 分割多个公式并统一格式
  const formulas = formulaInput.value.split(';')
    .map(f => f.trim())
    .filter(f => f)
    .map(f => normalizeFormulaFormat(f))  // 统一转换为单反斜杠格式

  try {
    console.log('TruthTableConstructor: 开始计算真值表，公式:', formulas)
    // 调用后端API生成真值表
    const tableData = await generateTruthTable(formulas, showDetailedTable.value, checkFormulaType.value)
    console.log('TruthTableConstructor: 收到真值表计算API响应:', tableData)

    // 添加到结果，直接存储表格数据
    const result = {
      index: counter.value + 1,
      formula: cleanFormulaForDisplay(formulas[0]), // 暂时只处理第一个公式
      tableData: tableData, // 直接存储表格数据
      truthTable: '' // 保留空字符串用于后备
    }

    // 如果需要检查公式类型
    if (checkFormulaType.value && tableData.formulaType) {
      result.formulaType = translateFormulaType(tableData.formulaType)
    }

    
    // 添加一个延迟，确保DOM准备就绪和MathRenderer初始化完成
    await new Promise(resolve => setTimeout(resolve, 200))

    results.value.push(result)
    console.log('TruthTableConstructor: 结果已添加，新results长度:', results.value.length)

    // 添加成功反馈
    feedback.value.push({
      formula: cleanFormulaForDisplay(formulas[0]),
      type: 'success',
      message: '公式解析成功'
    })

    // 收集所有数据，一次性发送到主界面
    const completeResult = {
      formula: cleanFormulaForDisplay(formulas[0]),
      tableData: tableData,
      formulaType: checkFormulaType.value && tableData.formulaType ? translateFormulaType(tableData.formulaType) : null,
      index: counter.value
    }

    // 一次性发送完整结果到主界面
    emit('formula-calculated', completeResult)

    ElMessage.success(`已完成 ${formulas.length} 个公式的真值表计算`)
  } catch (error) {
    console.error('TruthTableConstructor: 处理公式时发生错误:', error)
    feedback.value.push({
      formula: cleanFormulaForDisplay(formulas[0]),
      type: 'error',
      message: `处理公式时发生错误: ${error.message}`
    })
  }
}

const generateFormula = async () => {
  try {
    // 清除之前的结果（先清除状态，再设置新公式）
    results.value = [] // 清除之前的真值表结果
    feedback.value = [] // 清除之前的反馈
    counter.value = 0   // 重置计数器

    console.log('generateFormula: 开始调用后端生成随机公式API')

    // 调用后端API生成随机公式
    const result = await callBackendApi('/formula-syntax/generate', {
      method: 'GET'
    })

    console.log('generateFormula: 后端返回结果:', result)

    if (result.formula) {
      // 设置新公式，确保格式正确
      formulaInput.value = cleanFormulaForDisplay(result.formula)
      console.log('generateFormula: 生成公式:', formulaInput.value)
      ElMessage.success('已生成随机公式')
    } else {
      console.error('generateFormula: 后端返回结果中没有formula字段:', result)
      ElMessage.error('生成公式失败：后端返回格式错误')
    }
  } catch (error) {
    console.error('generateFormula: 生成随机公式失败:', error)

    // 如果后端调用失败，使用本地备用公式
    console.log('generateFormula: 使用备用本地公式生成')
    const fallbackFormulas = [
      'p\\wedge q',
      'p\\vee q',
      '\\neg p\\rightarrow q',
      'p\\leftrightarrow q',
      '(p\\vee q)\\wedge r',
      'p\\rightarrow(q\\wedge r)',
      'p\\wedge\\neg q',
      '(p\\rightarrow q)\\vee r'
    ]

    const randomFormula = fallbackFormulas[Math.floor(Math.random() * fallbackFormulas.length)]
    formulaInput.value = cleanFormulaForDisplay(randomFormula)
    console.log('generateFormula: 使用备用公式:', formulaInput.value)
    ElMessage.warning('后端服务不可用，已使用本地公式')
  }
}

const removeFormula = () => {
  formulaInput.value = ''
  ElMessage.info('已清空公式输入')
}

const checkFormula = async () => {
  if (!formulaInput.value.trim()) {
    ElMessage.warning('请先输入公式')
    return
  }

  feedback.value = []
  const formulas = formulaInput.value.split(';')
    .map(f => f.trim())
    .filter(f => f)
    .map(f => normalizeFormulaFormat(f))  // 统一转换为单反斜杠格式

  try {
    // 并行检查所有公式的合法性
    const checkPromises = formulas.map(async (formula) => {
      try {
        const result = await callBackendApi('/formula-syntax/check', {
          method: 'POST',
          body: JSON.stringify({
            formula: formula
          })
        })

        if (result.valid) {
          feedback.value.push({
            formula: cleanFormulaForDisplay(formula),
            type: 'success',
            message: '公式格式正确'
          })
        } else {
          feedback.value.push({
            formula: cleanFormulaForDisplay(formula),
            type: 'error',
            message: result.error || '公式格式不正确'
          })
        }
      } catch (error) {
        console.error('检查公式失败:', formula, error)
        feedback.value.push({
          formula: cleanFormulaForDisplay(formula),
          type: 'error',
          message: `检查失败: ${error.message}`
        })
      }
    })

    await Promise.all(checkPromises)
    ElMessage.success('公式合法性检查完成')
  } catch (error) {
    console.error('批量检查公式失败:', error)
    ElMessage.error(`检查失败: ${error.message}`)
  }
}

const clearResults = () => {
  results.value = []
  feedback.value = []
  counter.value = 0
  ElMessage.info('已清除所有结果')
}

const closeInterface = () => {
  emit('close')
}

const loadExample = (exampleKey) => {
  if (exampleFormulas[exampleKey]) {
    // 清除之前的结果（先清除状态，再设置新公式）
    results.value = [] // 清除之前的真值表结果
    feedback.value = [] // 清除之前的反馈
    counter.value = 0   // 重置计数器

    // 设置新公式，确保格式正确
    formulaInput.value = cleanFormulaForDisplay(exampleFormulas[exampleKey])

    console.log('loadExample: 加载示例:', formulaInput.value)
    console.log('loadExample: 原始公式:', exampleFormulas[exampleKey])

    ElMessage.info(`已加载示例：${exampleKey}`)
  }
}

// 通用API调用函数
const callBackendApi = async (endpoint, options = {}) => {
  try {
    // 使用绝对路径，确保移动端也能正确访问
    const baseUrl = window.location.origin
    const response = await fetch(`${baseUrl}/api${endpoint}`, {
      method: 'POST', // 默认使用POST方法
      headers: {
        'Content-Type': 'application/json',
        ...options.headers
      },
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

// Real backend API call
// ==================================== BACKEND API CALL ====================================

const generateTruthTable = async (formulas, detailed = false, checkType = true) => {
  try {
    // 使用绝对路径，确保移动端也能正确访问
    const baseUrl = window.location.origin
    const response = await fetch(`${baseUrl}/api/truth-table/generate`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        formulas: formulas,
        detailed: detailed,
        checkType: checkType
      })
    })

    if (!response.ok) {
      throw new Error('Failed to generate truth table')
    }

    const data = await response.json()

    if (data.errorMessage) {
      throw new Error(data.errorMessage)
    }

    return {
      latexTable: data.latexTable,
      formulaType: data.formulaType,
      headers: [], // 保持兼容性，但不再使用
      rows: [] // 保持兼容性，但不再使用
    }
  } catch (error) {
    console.error('Error generating truth table:', error)
    throw error
  }
}
// =========================================================================================

// 辅助函数
const translateFormulaType = (type) => {
  switch (type) {
    case 'tautology': return '重言式'
    case 'contradiction': return '矛盾式'
    case 'contingency': return '可满足式'
    default: return type
  }
}

const getFormulaTypeTag = (type) => {
  switch (type) {
    case '重言式': return 'success'
    case '可满足式': return 'warning'
    case '矛盾式': return 'danger'
    default: return 'info'
  }
}

// 统一处理公式格式，将双反斜杠转换为单反斜杠
const normalizeFormulaFormat = (formula) => {
  return formula.replace(/\\\\([a-zA-Z]+)/g, '\\$1')  // 将双反斜杠转换为单反斜杠
}

// 清理公式中的反斜杠，用于显示
const cleanFormulaForDisplay = (formula) => {
  return normalizeFormulaFormat(formula)
}

</script>

<style scoped>
.truth-table-constructor {
  max-height: 80vh;
  overflow-y: auto;
}

.button-section {
  margin-bottom: 2rem;
}

.button-row {
  margin-bottom: 1rem;
}

.example-buttons .el-button {
  width: 100%;
}

.formula-input-section {
  margin: 2rem 0;
}

.formula-textarea {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
}

.input-hint {
  margin-top: 0.5rem;
}

.feedback-section,
.results-section {
  margin: 2rem 0;
}

.feedback-content {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.feedback-item {
  margin-bottom: 1rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #dee2e6;
}

.feedback-item:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.feedback-formula {
  margin-bottom: 0.5rem;
}

.feedback-message {
  display: block;
  margin-top: 0.25rem;
}

.results-content {
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.result-item {
  margin-bottom: 2rem;
  padding-bottom: 2rem;
  border-bottom: 1px solid #dee2e6;
}

.result-item:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.result-formula {
  margin-bottom: 1rem;
  font-size: 1.1rem;
}

.truth-table {
  margin: 1rem 0;
  background: white;
  padding: 1rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.formula-type {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-top: 1rem;
}

.type-formula {
  font-weight: bold;
}

.type-tag {
  margin-left: 0.5rem;
}


/* HTML表格样式 */
.truth-table-vue {
  margin: 1rem 0;
  overflow: auto; /* 添加滚动支持 */
  background: white;
  border-radius: 8px;
  border: 2px solid #e9ecef;
  padding: 1rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.truth-table-html {
  width: auto; /* 改为auto以支持滚动 */
  min-width: 100%; /* 确保最小宽度为容器宽度 */
  border-collapse: collapse;
  border: 2px solid #dee2e6;
  border-radius: 8px;
  overflow: hidden;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  white-space: nowrap; /* 防止内容换行 */
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

/* 滚动条样式 */
.truth-table-constructor::-webkit-scrollbar {
  width: 8px;
}

.truth-table-constructor::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.truth-table-constructor::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.truth-table-constructor::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}

/* MathRenderer 容器样式 */
.truth-table-wrapper {
  margin: 1rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 2px solid #e9ecef;
  min-height: 60px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  width: 100%;
  box-sizing: border-box;
  overflow: auto; /* 改为auto，支持滚动 */
  position: relative;
}

.truth-table-wrapper .math-renderer {
  width: 100%;
  min-width: fit-content; /* 确保内容不会被压缩 */
  display: flex;
  justify-content: flex-start; /* 改为左对齐，保持表格完整显示 */
  align-items: center;
}

/* 表格响应式样式 */
.truth-table-wrapper .math-content {
  font-size: 0.9em;
  line-height: 1.3;
  min-width: fit-content; /* 确保内容最小宽度 */
  display: block;
  width: auto;
}

/* 让表格自适应容器大小 */
.truth-table-wrapper :deep(.mtable) {
  width: auto;
  min-width: fit-content;
  height: auto;
}

.truth-table-wrapper :deep(.katex-display) {
  margin: 0 !important;
  white-space: nowrap; /* 防止表格换行 */
}

/* 移动端响应式调整 */
@media (max-width: 768px) {
  .truth-table-wrapper {
    padding: 0.5rem;
    margin: 0.5rem 0;
  }

  .truth-table-wrapper .math-content {
    font-size: 0.8em; /* 移动端减小字体 */
  }

  /* 确保滚动条在移动端可见 */
  .truth-table-wrapper::-webkit-scrollbar {
    height: 6px;
    width: 6px;
  }

  .truth-table-wrapper::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 3px;
  }

  .truth-table-wrapper::-webkit-scrollbar-thumb {
    background: #c1c1c1;
    border-radius: 3px;
  }

  .truth-table-wrapper::-webkit-scrollbar-thumb:hover {
    background: #a1a1a1;
  }
}

/* 小屏幕设备进一步优化 */
@media (max-width: 480px) {
  .truth-table-wrapper {
    padding: 0.25rem;
  }

  .truth-table-wrapper .math-content {
    font-size: 0.7em;
  }
}
</style>