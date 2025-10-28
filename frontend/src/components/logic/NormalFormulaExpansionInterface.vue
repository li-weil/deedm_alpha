<template>
  <div class="normal-form-expansion-interface">
    <!-- 按钮操作区域 -->
    <div class="button-section">
      <!-- 第一行按钮：扩展公式、生成公式、移除公式、合法性检查 -->
      <el-row :gutter="20" class="button-row">
        <el-col :span="4">
          <el-button type="primary" @click="expandFormula">
            <el-icon><Check /></el-icon>
            扩展公式(Y)
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
          <el-button size="small" @click="loadExample('problem2_14_1')">例题2.14(1)</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem2_14_2')">例题2.14(2)</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem2_17')">例题2.17</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem2_18')">例题2.18</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem2_30')">例题2.30</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem2_32')">例题2.32</el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 扩展范式设置信息 -->
    <div class="settings-section">
      <el-divider content-position="left">扩展范式设置信息</el-divider>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="变量集(S):">
            <el-input
              v-model="variableSet"
              placeholder="请输入变量，如：p, q, r"
              class="variable-input"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="范式扩展为：">
            <el-radio-group v-model="expansionType">
              <el-radio label="dnf">主析取范式</el-radio>
              <el-radio label="cnf">主合取范式</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
    </div>

    <!-- 公式输入区域 -->
    <div class="formula-input-section">
      <el-divider content-position="left">公式输入区域</el-divider>
      <div class="formula-input-container">
        <el-input
          v-model="currentFormula"
          type="textarea"
          :rows="6"
          placeholder="请输入公式，每行一个公式，如：
p
¬q
(q ∨ r)
(¬r ∨ ¬q)"
          class="formula-textarea"
          @keyup.enter.ctrl="addFormula"
        />
        <div class="formula-actions">
          <el-button size="small" @click="addFormula">
            <el-icon><Plus /></el-icon>
            添加公式
          </el-button>
          <el-button size="small" @click="clearFormula">
            <el-icon><DocumentDelete /></el-icon>
            清空输入
          </el-button>
        </div>
      </div>
    </div>

    <!-- 已添加的公式列表 -->
    <div v-if="formulas.length > 0" class="formula-list-section">
      <el-divider content-position="left">已添加的公式</el-divider>
      <div class="formula-list">
        <el-tag
          v-for="(formula, index) in formulas"
          :key="index"
          closable
          @close="removeFormulaByIndex(index)"
          class="formula-tag"
        >
          <MathRenderer :formula="formula" />
        </el-tag>
      </div>
    </div>

    <!-- 结果显示区域 -->
    <div v-if="expansionResult" class="results-section">
      <el-divider content-position="left">范式扩展结果</el-divider>
      <div class="results-content">
        <div class="result-item">
          <div class="result-formula">
            <strong>1: </strong>
            <math-renderer
              :formula="expansionResult.originalFormula"
              :type="'katex'"
              :display-mode="false"
            />
          </div>

          <!-- 变量集信息 -->
          <div class="variable-info">
            <h4 class="result-title">扩展变量集：</h4>
            <span>{{ expansionResult.variableSet }}</span>
          </div>

          <!-- 扩展步骤 -->
          <div v-if="expansionResult.expansionSteps && expansionResult.expansionSteps.length > 0" class="expansion-steps">
            <h4 class="expansion-title">{{ expansionResult.targetType }}扩展步骤：</h4>
            <div v-for="(step, stepIndex) in expansionResult.expansionSteps" :key="'expansion-step-' + stepIndex" class="expansion-step-inline">
              <el-text type="primary" class="step-description">{{ step.description }}</el-text>
              <math-renderer
                :formula="step.formula"
                :type="'katex'"
                :display-mode="false"
                class="step-formula-inline"
              />
              <span v-if="step.binaryCode" class="formula-code-inline">[{{ step.binaryCode }}]</span>
              <el-text type="success" class="step-description">得到</el-text>
              <math-renderer
                :formula="step.resultCodes"
                :type="'mathjax'"
                :display-mode="false"
                class="result-codes-inline"
              />
            </div>
          </div>

          <!-- 最终结果 -->
          <div class="pnf-result">
            <div v-if="expansionResult.pdnfResult">
              <h5 class="pnf-title">最终的主析取范式(PDNF)是：</h5>
              <math-renderer
                :formula="expansionResult.pdnfResult"
                :type="'mathjax'"
                :display-mode="false"
              />
            </div>
            <div v-if="expansionResult.pcnfResult">
              <h5 class="pnf-title">最终的主合取范式(PCNF)是：</h5>
              <math-renderer
                :formula="expansionResult.pcnfResult"
                :type="'mathjax'"
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
  Close,
  Plus,
  DocumentDelete
} from '@element-plus/icons-vue'
import MathRenderer from '../common/MathRenderer.vue'

// 响应式数据
const emit = defineEmits(['close', 'result'])
const variableSet = ref('p, q, r')
const expansionType = ref('dnf')
const currentFormula = ref('')
const formulas = ref([])
const feedbackMessages = ref([])
const expansionResult = ref(null)

// 示例题库
const examples = {
  problem2_14_1: {
    formulas: ['p', '\\neg q', '(q\\vee r)', '(\\neg r\\vee\\neg q)'],
    variableSet: 'p, q, r',
    type: 'cnf'
  },
  problem2_14_2: {
    formulas: ['p\\wedge\\neg q\\wedge r'],
    variableSet: 'p, q, r',
    type: 'dnf'
  },
  problem2_17: {
    formulas: ['\\neg(p\\rightarrow q)\\wedge((\\neg q)\\leftrightarrow r)'],
    variableSet: 'p, q, r',
    type: 'cnf'
  },
  problem2_18: {
    formulas: ['(\\neg p\\vee q)', '(\\neg r\\vee s)'],
    variableSet: 'p, q, r, s',
    type: 'cnf'
  },
  problem2_30: {
    formulas: ['(\\neg p\\vee q\\vee r)', '(\\neg r\\vee \\neg s)', '(q\\vee t)', '(\\neg q\\vee\\neg t)', '(s\\vee\\neg p)'],
    variableSet: 'p, q, r, s, t',
    type: 'cnf'
  },
  problem2_32: {
    formulas: ['(\\neg p\\vee q)', '(\\neg q\\vee \\neg r)', '(r\\vee s)', '(\\neg s\\vee\\neg q)'],
    variableSet: 'p, q, r, s',
    type: 'cnf'
  }
}

// API 调用函数
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

// 添加公式
const addFormula = () => {
  if (!currentFormula.value.trim()) return

  const lines = currentFormula.value.split('\n').filter(line => line.trim())
  lines.forEach(line => {
    if (line.trim() && !formulas.value.includes(line.trim())) {
      formulas.value.push(line.trim())
    }
  })

  currentFormula.value = ''
  ElMessage.success('公式已添加')
}

// 移除公式
const removeFormula = () => {
  formulas.value = []
  feedbackMessages.value = []
  expansionResult.value = null
  ElMessage.info('已移除所有公式')
}

// 按索引移除公式
const removeFormulaByIndex = (index) => {
  formulas.value.splice(index, 1)
}

// 清空当前输入
const clearFormula = () => {
  currentFormula.value = ''
}

// 生成公式
const generateFormula = async () => {
  try {
    const result = await callBackendApi('/normal-form-expansion/generate', {
      method: 'POST',
      body: JSON.stringify({
        variableSet: variableSet.value,
        isDNF: expansionType.value === 'dnf'
      })
    })

    if (result.length > 0) {
      formulas.value = result
      ElMessage.success('已生成随机公式')
    } else {
      ElMessage.warning('生成公式失败，请检查变量集设置')
    }
  } catch (error) {
    ElMessage.error('生成公式时发生错误: ' + error.message)
  }
}

// 合法性检查
const checkFormula = async () => {
  if (formulas.value.length === 0) {
    ElMessage.warning('请先添加公式')
    return
  }

  try {
    feedbackMessages.value = []

    formulas.value.forEach(formula => {
      feedbackMessages.value.push(formula)
    })

    const result = await callBackendApi('/normal-form-expansion/check', {
      method: 'POST',
      body: JSON.stringify({
        formulas: formulas.value,
        variableSet: variableSet.value,
        isDNF: expansionType.value === 'dnf'
      })
    })

    if (result.success) {
      ElMessage.success(result.message)
    } else {
      ElMessage.error(result.message)
    }
  } catch (error) {
    ElMessage.error('公式检查时发生错误: ' + error.message)
  }
}

// 扩展公式
const expandFormula = async () => {
  if (formulas.value.length === 0) {
    ElMessage.warning('请先添加公式')
    return
  }

  try {
    feedbackMessages.value = []

    const result = await callBackendApi('/normal-form-expansion/expand', {
      method: 'POST',
      body: JSON.stringify({
        formulas: formulas.value,
        variableSet: variableSet.value,
        isDNF: expansionType.value === 'dnf'
      })
    })

    if (result.success) {
      ElMessage.success(result.message)

      // 保存扩展结果用于显示
      expansionResult.value = result

      // 返回结果给主界面
      emit('result', {
        type: 'normal-form-expansion',
        data: result
      })

      // 不自动关闭界面，让用户查看结果
    } else {
      ElMessage.error(result.message)
    }
  } catch (error) {
    ElMessage.error('范式扩展时发生错误: ' + error.message)
  }
}

// 加载示例
const loadExample = (exampleKey) => {
  const example = examples[exampleKey]
  if (example) {
    formulas.value = example.formulas
    variableSet.value = example.variableSet
    expansionType.value = example.type
    ElMessage.success('已加载示例: ' + exampleKey)
  }
}

// 清除结果
const clearResults = () => {
  formulas.value = []
  feedbackMessages.value = []
  expansionResult.value = null
  currentFormula.value = ''
  ElMessage.info('已清除所有结果')
}

// 关闭界面
const closeInterface = () => {
  emit('close')
}
</script>

<style scoped>
.normal-form-expansion-interface {
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

.settings-section {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.variable-input {
  width: 100%;
}

.formula-input-section {
  margin-bottom: 20px;
}

.formula-input-container {
  position: relative;
}

.formula-textarea {
  font-family: 'Courier New', monospace;
  font-size: 14px;
}

.formula-actions {
  margin-top: 10px;
  text-align: right;
}

.formula-list-section {
  margin-bottom: 20px;
}

.formula-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.formula-tag {
  margin: 5px;
  padding: 8px 12px;
  font-size: 14px;
  background-color: #e1f3d8;
  border: 1px solid #b3d8a8;
  color: #2c5e1e;
  max-width: 300px;
  word-break: break-all;
}

.feedback-section {
  margin-top: 20px;
}

.feedback-container {
  max-height: 300px;
  overflow-y: auto;
  background-color: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.feedback-message {
  margin: 8px 0;
  padding: 8px;
  background-color: white;
  border-radius: 4px;
  border-left: 4px solid #409eff;
}

.text-feedback {
  font-family: 'Courier New', monospace;
  font-size: 14px;
  color: #303133;
  line-height: 1.4;
}

.math-feedback {
  font-size: 16px;
  padding: 8px;
  background-color: #f9f9f9;
  border-radius: 4px;
  border: 1px solid #e6e6e6;
}


:deep(.el-radio-group) {
  display: flex;
  gap: 20px;
}

:deep(.el-form-item__label) {
  font-weight: bold;
  color: #303133;
}

/* 结果显示样式 - 完全复制自PrincipalNormalFormInterface */
.results-section {
  margin: 2rem 0;
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

.result-title {
  margin: 0 0 1rem 0;
  color: #2c3e50;
  font-size: 1.1rem;
  font-weight: 600;
}

.variable-info {
  margin: 1rem 0;
  padding: 1rem;
  background: #fff3cd;
  border: 1px solid #ffeaa7;
  border-radius: 4px;
  color: #856404;
}

.expansion-steps {
  margin: 1.5rem 0;
  padding: 1.5rem;
  background: #f0f8ff;
  border-radius: 8px;
  border: 2px solid #4a90e2;
}

.expansion-title {
  margin: 0 0 1rem 0;
  color: #2c3e50;
  font-size: 1.1rem;
  font-weight: 600;
  text-align: center;
}

.expansion-step-inline {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  margin-bottom: 0.75rem;
  padding: 0.5rem 1rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #d0e3ff;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.expansion-step-inline:last-child {
  margin-bottom: 0;
}

.step-description {
  font-weight: 600;
  font-size: 0.95em;
  white-space: nowrap;
}

.step-formula-inline {
  background: #f8f9ff;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  border: 1px solid #d0e3ff;
}

.formula-code-inline {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  background: #f1f3f4;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.9em;
  color: #5f6368;
  border: 1px solid #dadce0;
  white-space: nowrap;
}

.result-codes-inline {
  background: #f8fff8;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  border: 1px solid #c3e6c3;
}

.pnf-result {
  margin-top: 1rem;
  padding: 1rem;
  background: #fff3cd;
  border-radius: 4px;
  border: 1px solid #ffeaa7;
}

.pnf-title {
  margin: 0.5rem 0;
  color: #856404;
  font-size: 0.95rem;
  font-weight: 600;
}
</style>