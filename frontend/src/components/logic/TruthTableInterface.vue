<template>
  <div class="truth-table-interface">
    <!-- 按钮操作区域 -->
    <div class="button-section">
      <!-- 第一行按钮：开始计算、生成公式、移除公式、公式合法性检查 -->
      <el-row :gutter="20" class="button-row">
        <el-col :span="4">
          <el-button type="primary" @click="startCalculation">
            <el-icon><Check /></el-icon>
            开始计算(Y)
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
      <el-divider content-position="left">离散数学教材例题展示</el-divider>
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
        placeholder="请输入LaTeX格式的逻辑公式，多个公式用分号分隔..."
        class="formula-textarea"
      />
      <div class="input-hint">
        <el-text type="info" size="small">
          支持的LaTeX符号：\\neg(¬), \\wedge(∧), \\vee(∨), \\rightarrow(→), \\leftrightarrow(↔)
        </el-text>
      </div>
    </div>

    <!-- 反馈信息区域 -->
    <div v-if="feedback.length > 0" class="feedback-section">
      <el-divider content-position="left">反馈信息</el-divider>
      <div class="feedback-content">
        <div v-for="(item, index) in feedback" :key="index" class="feedback-item">
          <math-renderer
            :formula="item.formula"
            :type="'latex'"
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
            <strong>{{ result.index }}: </strong>
            <math-renderer
              :formula="result.formula"
              :type="'latex'"
              :display-mode="false"
            />
          </div>

          <div class="truth-table">
            <!-- 渲染真值表数据为Vue组件 -->
            <div v-if="result.tableData" class="truth-table-vue">
              <table class="truth-table-html">
                <thead>
                  <tr>
                    <th v-for="(header, index) in result.tableData.headers" :key="index" class="header-cell">
                      <math-renderer
                        :formula="header"
                        :type="'latex'"
                        :display-mode="false"
                      />
                    </th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(row, rowIndex) in result.tableData.rows" :key="rowIndex">
                    <td v-for="(cell, cellIndex) in [...row.variableValues, row.resultValue]" :key="cellIndex" class="data-cell">
                      <math-renderer
                        :formula="cell"
                        :type="'latex'"
                        :display-mode="false"
                      />
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            <!-- 保留原有的LaTeX渲染器作为后备 -->
            <math-renderer
              v-else
              :formula="result.truthTable"
              :type="'latex'"
              :display-mode="true"
              class="truth-table-content"
            />
          </div>

          <div v-if="checkFormulaType && result.formulaType" class="formula-type">
            <math-renderer
              :formula="result.formula"
              :type="'latex'"
              :display-mode="false"
              class="type-formula"
            />
            <el-tag :type="getFormulaTypeTag(result.formulaType)" class="type-tag">
              {{ result.formulaType }}
            </el-tag>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
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
    // 调用后端API生成真值表
    const tableData = await generateTruthTable(formulas, showDetailedTable.value, checkFormulaType.value)

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

    results.value.push(result)

    // 添加成功反馈
    feedback.value.push({
      formula: cleanFormulaForDisplay(formulas[0]),
      type: 'success',
      message: '公式解析成功'
    })

    // 发送公式计算结果到主界面
    emit('formula-calculated', {
      formula: cleanFormulaForDisplay(formulas[0]),
      tableData: tableData,
      formulaType: checkFormulaType.value && tableData.formulaType ? translateFormulaType(tableData.formulaType) : null
    })

    ElMessage.success(`已完成 ${formulas.length} 个公式的真值表计算`)
  } catch (error) {
    feedback.value.push({
      formula: cleanFormulaForDisplay(formulas[0]),
      type: 'error',
      message: `处理公式时发生错误: ${error.message}`
    })
  }
}

const generateFormula = () => {
  // 模拟生成随机公式 - 使用单反斜杠格式
  const randomFormulas = [
    'p\\wedge q',
    'p\\vee q',
    '\\neg p\\rightarrow q',
    'p\\leftrightarrow q',
    '(p\\vee q)\\wedge r',
    'p\\rightarrow(q\\wedge r)'
  ]

  const randomFormula = randomFormulas[Math.floor(Math.random() * randomFormulas.length)]

  // 清空原有公式，只保留新生成的公式
  formulaInput.value = randomFormula

  ElMessage.info('已生成随机公式')
}

const removeFormula = () => {
  formulaInput.value = ''
  ElMessage.info('已清空公式输入')
}

const checkFormula = () => {
  if (!formulaInput.value.trim()) {
    ElMessage.warning('请先输入公式')
    return
  }

  feedback.value = []
  const formulas = formulaInput.value.split(';')
    .map(f => f.trim())
    .filter(f => f)
    .map(f => normalizeFormulaFormat(f))  // 统一转换为单反斜杠格式

  formulas.forEach(formula => {
    if (isValidFormula(formula)) {
      feedback.value.push({
        formula: cleanFormulaForDisplay(formula),
        type: 'success',
        message: '公式格式正确'
      })
    } else {
      feedback.value.push({
        formula: cleanFormulaForDisplay(formula),
        type: 'error',
        message: '公式格式不正确'
      })
    }
  })
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
    formulaInput.value = cleanFormulaForDisplay(exampleFormulas[exampleKey])
    ElMessage.info(`已加载示例：${exampleKey}`)
  }
}

// Real backend API call
const generateTruthTable = async (formulas, detailed = false, checkType = true) => {
  try {
    const response = await fetch('/api/truth-table/generate', {
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
      headers: data.columnHeaders || [],
      rows: data.rows || [],
      formulaType: data.formulaType
    }
  } catch (error) {
    console.error('Error generating truth table:', error)
    throw error
  }
}


// 翻译公式类型
const translateFormulaType = (type) => {
  switch (type) {
    case 'tautology': return '重言式'
    case 'contradiction': return '矛盾式'
    case 'contingency': return '可满足式'
    default: return type
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

// 辅助函数
const isValidFormula = (formula) => {
  // 简单的公式验证逻辑（实际应该调用后端API）
  const basicPattern = /^[pqrst\s\\\\neg\\\\wedge\\\\vee\\\\rightarrow\\\\leftrightarrow\\(\\)]+$/
  return basicPattern.test(formula) && formula.length > 0
}

const getFormulaTypeTag = (type) => {
  switch (type) {
    case '重言式': return 'success'
    case '可满足式': return 'warning'
    case '矛盾式': return 'danger'
    default: return 'info'
  }
}
</script>

<style scoped>
.truth-table-interface {
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
}

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

/* 滚动条样式 */
.truth-table-interface::-webkit-scrollbar {
  width: 8px;
}

.truth-table-interface::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.truth-table-interface::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.truth-table-interface::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}
</style>