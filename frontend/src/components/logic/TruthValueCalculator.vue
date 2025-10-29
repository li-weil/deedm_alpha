<template>
  <div class="truth-value-calculator">
    <!-- 页面标题区 -->


    <!-- 按钮操作区域 -->
    <div class="button-section">
      <!-- 第一行按钮：开始计算、生成公式、移除公式、公式合法性检查 -->
      <el-row :gutter="20" class="button-row">
        <el-col :span="4">
          <el-button type="primary" @click="calculateTruthValue">
            <el-icon><Check /></el-icon>
            开始计算
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

      <!-- 模块 2：内容显示选项 -->
      <el-divider content-position="left">选择显示内容</el-divider>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-checkbox v-model="showDetailedProcess" size="large">
            给出计算公式真值的详细过程
          </el-checkbox>
        </el-col>
      </el-row>
    </div>

    <!-- 模块 3：真值计算功能 -->
    <div class="formula-input-section">
      <el-divider content-position="left">公式输入</el-divider>

      <div class="input-hint-text">
        请输入使用 LaTeX 命令表示的公式、命题变元集（用 p, q, r 等形式）和赋值（用 000, 001 等形式）
      </div>

      <!-- 命题逻辑公式输入框 -->
      <div class="input-group">
        <label class="input-label">命题逻辑公式 (F)</label>
        <el-input
          v-model="formulaInput"
          type="textarea"
          :rows="3"
          placeholder="请输入LaTeX格式的逻辑公式..."
          class="formula-textarea"
        />
        <div class="input-hint">
          <el-text type="info" size="small">
            支持的LaTeX符号：\neg(¬), \wedge(∧), \vee(∨), \rightarrow(→), \leftrightarrow(↔)
          </el-text>
        </div>
      </div>

      <!-- 对命题变量的赋值和命题变量输入框 -->
      <el-row :gutter="20" class="assignment-row">
        <el-col :span="12">
          <div class="input-group">
            <label class="input-label">对命题变量的赋值 (A)</label>
            <el-input
              v-model="variableAssignmentInput"
              placeholder="在此输入赋值"
              class="assignment-input"
            />
          </div>
        </el-col>
        <el-col :span="12">
          <div class="input-group">
            <label class="input-label">命题变量 (V)</label>
            <el-input
              v-model="propositionalVariablesInput"
              placeholder="输入命题变量"
              class="variables-input"
            />
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 模块 4：反馈信息区 -->
    <div v-if="feedback.length > 0" class="feedback-section">
      <el-divider content-position="left">反馈信息</el-divider>
      <div class="feedback-content">
        <div v-for="(item, index) in feedback" :key="index" class="feedback-item">
          <div v-if="item.formula" class="feedback-formula">
            <math-renderer
              :formula="item.formula"
              :type="'katex'"
              :display-mode="false"
            />
          </div>
          <div v-if="item.message" class="feedback-message" :class="item.type">
            {{ item.message }}
          </div>
        </div>
      </div>
    </div>

    <!-- 模块 5：输出显示区（真值计算结果） -->
    <div v-if="results.length > 0" class="results-section">
      <el-divider content-position="left">计算结果</el-divider>
      <div class="results-content">
        <div v-for="(result, index) in results" :key="index" class="result-item">
          <div class="result-formula">
            <math-renderer
              :formula="result.formula"
              :type="'katex'"
              :display-mode="false"
            />
          </div>

          <!-- 真值计算结果 -->
          <div v-if="result.truthValue !== undefined" class="truth-value-result">
            <div class="truth-value-display">
              <span class="truth-value-label">公式真值 = </span>
              <el-tag
                :type="result.truthValue ? 'success' : 'danger'"
                size="large"
                class="truth-value-tag"
              >
                {{ result.truthValue ? '1' : '0' }}
              </el-tag>
            </div>
          </div>

          <!-- 详细计算过程 -->

          <div v-for="(step, stepIndex) in result.detailedSteps" :key="stepIndex" class="step-item">
            <div class="step-formula">
              <math-renderer
                :formula="step.formula"
                :type="'katex'"
                :display-mode="false"
              />
            </div>
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
const formulaInput = ref('(p\\rightarrow q)\\wedge p')
const variableAssignmentInput = ref('')
const propositionalVariablesInput = ref('')
const showDetailedProcess = ref(false)
const checkFormulaType = ref(false)
const showStrictForm = ref(false)
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

const calculateTruthValue = async () => {
  if (!formulaInput.value.trim()) {
    ElMessage.warning('请先输入公式')
    return
  }

  feedback.value = []
  results.value = []

  const formula = normalizeFormulaFormat(formulaInput.value.trim())
  const assignment = variableAssignmentInput.value.trim()
  const variables = propositionalVariablesInput.value.trim()

  try {
    console.log('TruthValueCalculator: 开始计算公式真值', { formula, assignment, variables })

    // 调用后端API计算真值
    const truthValueData = await calculateTruthValueAPI(formula, assignment, variables, {
      showDetailedProcess: showDetailedProcess.value,
      checkType: checkFormulaType.value,
      showStrictForm: showStrictForm.value
    })

    const result = {
      index: counter.value + 1,
      formula: cleanFormulaForDisplay(formula),
      truthValue: truthValueData.truthValue,
      formulaType: truthValueData.formulaType,
      detailedSteps: truthValueData.detailedSteps || [],
      syntaxData: truthValueData.syntaxData || null
    }

    results.value.push(result)
    counter.value++

    // 添加成功反馈
    feedback.value.push({
      formula: cleanFormulaForDisplay(formula),
      type: 'success',
      message: '公式解析成功'
    })

    // 发送结果到主界面
    emit('formula-calculated', result)

    ElMessage.success('公式真值计算完成')
  } catch (error) {
    console.error('TruthValueCalculator: 计算公式真值时发生错误:', error)
    feedback.value.push({
      formula: cleanFormulaForDisplay(formula),
      type: 'error',
      message: `计算失败: ${error.message}`
    })
  }
}

const generateFormula = async () => {
  try {
    feedback.value = []
    results.value = []
    counter.value = 0

    console.log('TruthValueCalculator: 开始生成随机公式')

    const result = await callBackendApi('/formula-syntax/generate', {
      method: 'GET'
    })

    if (result.formula) {
      formulaInput.value = cleanFormulaForDisplay(result.formula)
      ElMessage.success('已生成随机公式')
    } else {
      ElMessage.error('生成公式失败：后端返回格式错误')
    }
  } catch (error) {
    console.error('TruthValueCalculator: 生成随机公式失败:', error)

    // 使用本地备用公式
    const fallbackFormulas = [
      '(p\\rightarrow q)\\wedge p',
      'p\\vee \\neg p',
      'p\\wedge q',
      '\\neg p\\rightarrow q',
      'p\\leftrightarrow q'
    ]

    const randomFormula = fallbackFormulas[Math.floor(Math.random() * fallbackFormulas.length)]
    formulaInput.value = cleanFormulaForDisplay(randomFormula)
    ElMessage.warning('后端服务不可用，已使用本地公式')
  }
}

const clearResults = () => {
  results.value = []
  feedback.value = []
  counter.value = 0
  ElMessage.info('已清除所有结果')
}

const removeFormula = () => {
  formulaInput.value = ''
  ElMessage.info('已移除公式')
}

const checkFormula = async () => {
  if (!formulaInput.value.trim()) {
    ElMessage.warning('请先输入公式')
    return
  }

  feedback.value = []
  const formula = normalizeFormulaFormat(formulaInput.value.trim())

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
    console.error('检查公式失败:', error)
    feedback.value.push({
      formula: cleanFormulaForDisplay(formula),
      type: 'error',
      message: `检查失败: ${error.message}`
    })
  }
}

const closeInterface = () => {
  emit('close')
}

const loadExample = (exampleKey) => {
  if (exampleFormulas[exampleKey]) {
    feedback.value = []
    results.value = []
    counter.value = 0

    formulaInput.value = cleanFormulaForDisplay(exampleFormulas[exampleKey])
    ElMessage.info(`已加载示例：${exampleKey}`)
  }
}

// API调用函数
const callBackendApi = async (endpoint, options = {}) => {
  try {
    const baseUrl = window.location.origin
    const response = await fetch(`${baseUrl}/api${endpoint}`, {
      method: 'POST',
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

const calculateTruthValueAPI = async (formula, assignment, variables, options) => {
  try {
    const baseUrl = window.location.origin
    const response = await fetch(`${baseUrl}/api/truth-value/calculate`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        formula: formula,
        assignment: assignment,
        variables: variables,
        ...options
      })
    })

    if (!response.ok) {
      throw new Error('Failed to calculate truth value')
    }

    const data = await response.json()
    return data
  } catch (error) {
    console.error('Error calculating truth value:', error)
    throw error
  }
}



const normalizeFormulaFormat = (formula) => {
  return formula.replace(/\\\\([a-zA-Z]+)/g, '\\$1')
}

const cleanFormulaForDisplay = (formula) => {
  return normalizeFormulaFormat(formula)
}
</script>

<style scoped>
.truth-value-calculator {
  max-height: 80vh;
  overflow-y: auto;
}

.page-title-section {
  text-align: center;
  margin-bottom: 2rem;
}

.page-title {
  color: #2c3e50;
  font-size: 1.5rem;
  font-weight: bold;
  margin: 0;
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

.input-hint-text {
  color: #666;
  font-size: 0.9rem;
  margin-bottom: 1rem;
  line-height: 1.5;
}

.input-group {
  margin-bottom: 1rem;
}

.input-label {
  display: block;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 0.5rem;
}

.formula-textarea {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
}

.assignment-row {
  margin-bottom: 1rem;
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

.feedback-message.success {
  color: #67c23a;
  font-weight: 500;
}

.feedback-message.error {
  color: #f56c6c;
  font-weight: 500;
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

.truth-value-result {
  margin: 1rem 0;
  padding: 1rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.truth-value-display {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.truth-value-label {
  font-weight: 600;
  color: #2c3e50;
}

.truth-value-tag {
  font-size: 1.1rem;
  font-weight: bold;
}

.formula-type {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-top: 1rem;
}

.type-label {
  font-weight: 600;
  color: #2c3e50;
}

.type-tag {
  margin-left: 0.5rem;
}

.detailed-steps {
  margin: 1rem 0;
  padding: 1rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.steps-title {
  margin: 0 0 1rem 0;
  color: #2c3e50;
  font-size: 1rem;
  font-weight: 600;
}

.steps-content {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
}

.step-item {
  margin-bottom: 1rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #dee2e6;
}

.step-item:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.step-explanation {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 1rem;
}

.step-formula {
  margin: 1rem 0;
  padding: 1rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}

.syntax-content {
  background: white;
  padding: 1rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  margin: 1rem 0;
}

.syntax-row {
  display: flex;
  align-items: center;
  margin-bottom: 0.75rem;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.syntax-row:last-child {
  margin-bottom: 0;
}

.syntax-label {
  font-weight: 600;
  color: #374151;
  min-width: 80px;
  flex-shrink: 0;
}

.syntax-formula {
  flex: 1;
  min-width: 0;
}

/* 滚动条样式 */
.truth-value-calculator::-webkit-scrollbar {
  width: 8px;
}

.truth-value-calculator::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.truth-value-calculator::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.truth-value-calculator::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .button-row .el-col {
    margin-bottom: 0.5rem;
  }

  .truth-value-display {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }

  .syntax-row {
    flex-direction: column;
    align-items: flex-start;
  }

  .syntax-label {
    min-width: auto;
  }
}
</style>