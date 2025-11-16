<template>
  <div class="expression-calculator-interface">
    <!-- 按钮操作区域 -->
    <div class="button-section">
      <!-- 第一行按钮：开始计算、清除输入、合法性检查、清除结果、取消 -->
      <el-row :gutter="20" class="button-row">
        <el-col :span="4">
          <el-button type="primary" @click="startCalculation">
            <el-icon><Check /></el-icon>
            开始计算(Y)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="warning" @click="clearInput">
            <el-icon><Delete /></el-icon>
            清除输入(V)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="info" @click="checkInput">
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

      <!-- 第二行按钮：示例题 -->
      <el-divider content-position="left">《离散数学基础》教材示例展示</el-divider>
      <el-row :gutter="15" class="example-buttons">
        <el-col :span="4">
          <el-button size="small" @click="loadExample('factorial')">阶乘计算</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('permutation')">排列计算</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('combination')">组合计算</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('complex')">复杂表达式</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('power')">幂运算</el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 表达式输入区域 -->
    <div class="expression-input-section">
      <el-divider content-position="left">组合表达式输入</el-divider>
      <el-row :gutter="20">
        <el-col :span="24">
          <div class="input-group">
            <label>组合表达式(n)：</label>
            <el-input
              v-model="expressionInput"
              placeholder="例如：2+3*4, 5!, C(6,3), P(5,2), 2^3! 等"
              class="expression-input"
              type="textarea"
              :rows="3"
              resize="none"
            />
          </div>
        </el-col>
      </el-row>
      <div class="input-hint">
        <el-text type="info" size="small">
          支持的运算符：+ - * / ^ ! P() C() , ()
          <br>
          示例：2+3*4, 5!, C(6,3), P(5,2), 2^3!, (2+3)!, C(P(5,2),3)
        </el-text>
      </div>
    </div>

    <!-- 内容显示区域 -->
    <div class="content-display-area">
      <!-- 反馈信息区域 -->
      <div v-if="feedback.length > 0" class="feedback-section">
        <el-divider content-position="left">反馈信息</el-divider>
        <div class="feedback-content">
          <div v-for="(item, index) in feedback" :key="index" class="feedback-item">
            <math-renderer
              :formula="item.formula"
              :type="'mathjax'"
              :display-mode="false"
              class="feedback-formula"
            />
            <el-text v-if="item.message" :type="item.type" class="feedback-message">
              {{ item.message }}
            </el-text>
          </div>
        </div>
      </div>

      <!-- 计算结果区域 -->
      <div v-if="results.length > 0" class="results-section">
        <el-divider content-position="left">组合表达式计算结果</el-divider>
        <div class="results-content">
          <div v-for="(result, index) in results" :key="index" class="result-item">
            <!-- 表达式基本信息 -->
            <div class="result-basic">
              <h4>第 {{ result.index }} 次计算：</h4>
              <math-renderer
                :formula="result.formula"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
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
  Delete,
  WarningFilled,
  RefreshRight,
  Close
} from '@element-plus/icons-vue'
import MathRenderer from '@/components/common/MathRenderer.vue'

// 响应式数据
const expressionInput = ref('')
const feedback = ref([])
const results = ref([])
const counter = ref(0)

// 示例数据
const examples = {
  factorial: {
    expression: '5!',
    description: '计算5的阶乘'
  },
  permutation: {
    expression: 'P(6,2)',
    description: '从6个元素中取2个的排列数'
  },
  combination: {
    expression: 'C(10,3)',
    description: '从10个元素中取3个的组合数'
  },
  complex: {
    expression: 'C(6,2) + P(4,2) - 3!',
    description: '复杂组合表达式'
  },
  power: {
    expression: '2^3!',
    description: '2的3阶乘次幂'
  }
}

// 事件处理函数
const emit = defineEmits(['close', 'expression-calculator-result'])

const startCalculation = async () => {
  if (!expressionInput.value.trim()) {
    ElMessage.warning('请输入组合表达式')
    return
  }

  // 清空之前的反馈和结果
  feedback.value = []

  try {
    const request = {
      expression: expressionInput.value.trim()
    }

    console.log('ExpressionCalculatorInterface: 开始组合表达式计算', request)

    const response = await callBackendApi('/counting/expression-calculator/calculate', {
      method: 'POST',
      body: JSON.stringify(request)
    })

    console.log('ExpressionCalculatorInterface: 收到计算结果', response)

    if (response.success) {
      const result = {
        ...response,
        index: counter.value + 1
      }

      results.value.push(result)
      counter.value++

      // 添加成功反馈
      feedback.value.push({
        formula: `\\text{组合表达式} = ${response.originalExpression}`,
        type: 'success',
        message: '表达式计算完成'
      })

      // 发送结果到主界面
      emit('expression-calculator-result', result)

      ElMessage.success('表达式计算完成')
    } else {
      feedback.value.push({
        formula: `\\text{组合表达式} = ${expressionInput.value}`,
        type: 'error',
        message: response.message || '表达式计算失败'
      })

      ElMessage.error('表达式计算失败: ' + response.message)
    }
  } catch (error) {
    console.error('表达式计算失败:', error)
    feedback.value.push({
      formula: `\\text{组合表达式} = ${expressionInput.value}`,
      type: 'error',
      message: `计算失败: ${error.message}`
    })

    ElMessage.error(`计算失败: ${error.message}`)
  }
}

const clearInput = () => {
  expressionInput.value = ''
  ElMessage.info('已清空输入')
}

const checkInput = async () => {
  if (!expressionInput.value.trim()) {
    ElMessage.warning('请输入组合表达式')
    return
  }

  feedback.value = []

  try {
    const response = await callBackendApi('/counting/expression-calculator/validate', {
      method: 'POST',
      body: JSON.stringify({
        expression: expressionInput.value.trim()
      })
    })

    if (response.success) {
      feedback.value.push({
        formula: `\\text{组合表达式} = ${expressionInput.value}`,
        type: 'success',
        message: response.message
      })
      ElMessage.success('表达式格式正确')
    } else {
      feedback.value.push({
        formula: `\\text{组合表达式} = ${expressionInput.value}`,
        type: 'error',
        message: response.message
      })
      ElMessage.error('表达式格式不正确')
    }
  } catch (error) {
    console.error('检查表达式失败:', error)
    feedback.value.push({
      formula: `\\text{组合表达式} = ${expressionInput.value}`,
      type: 'error',
      message: `检查失败: ${error.message}`
    })

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
  if (examples[exampleKey]) {
    const example = examples[exampleKey]

    // 清除之前的结果
    results.value = []
    feedback.value = []
    counter.value = 0

    // 设置示例数据
    expressionInput.value = example.expression

    console.log('ExpressionCalculatorInterface: 加载示例', exampleKey, example)
    ElMessage.info(`已加载示例：${example.description}`)
  }
}

// 通用API调用函数
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
</script>

<style scoped>
.expression-calculator-interface {
  display: flex;
  flex-direction: column;
  width: 100%;
}

/* 在大屏幕上完全自适应，无最大宽度限制 */
@media (min-width: 1200px) {
  .expression-calculator-interface {
    width: 100%;
    max-width: none;
  }
}

/* 在中等屏幕上设置合理的最大宽度 */
@media (max-width: 1199px) and (min-width: 900px) {
  .expression-calculator-interface {
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
  }
}

/* 在小屏幕上固定宽度，支持水平滚动 */
@media (max-width: 899px) {
  .expression-calculator-interface {
    width: 900px;
    min-width: 900px;
    max-width: 900px;
    margin: 0 auto;
  }
}

.button-section {
  margin-bottom: 1rem;
  flex-shrink: 0;
}

.button-row {
  margin-bottom: 1rem;
}

.example-buttons .el-button {
  width: 100%;
  margin-bottom: 0.5rem;
}

.expression-input-section {
  margin-top: 1rem;
  flex-shrink: 0;
}

.input-group {
  margin-bottom: 1rem;
}

.input-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #374151;
}

.expression-input {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
}

.input-hint {
  margin-top: 0.5rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

/* 内容显示区域 */
.content-display-area {
  padding: 1rem;
  background: #ffffff;
}

.feedback-section,
.results-section {
  margin-bottom: 2rem;
}

.feedback-content,
.results-content {
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.feedback-item,
.result-item {
  margin-bottom: 2rem;
  padding-bottom: 2rem;
  border-bottom: 1px solid #dee2e6;
}

.feedback-item:last-child,
.result-item:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.feedback-formula,
.result-formula {
  margin-bottom: 1rem;
  font-size: 1.1rem;
  overflow-x: auto;
  padding: 0.5rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.feedback-message {
  font-size: 0.9rem;
  margin-top: 0.5rem;
}

.result-basic {
  margin-bottom: 1.5rem;
}

h4 {
  color: #374151;
  margin: 1rem 0 0.5rem 0;
  font-size: 1.1rem;
  font-weight: 600;
}

h5 {
  color: #6b7280;
  margin: 1rem 0 0.5rem 0;
  font-size: 1rem;
  font-weight: 500;
}

/* 滚动条样式 */
.content-display-area::-webkit-scrollbar {
  width: 8px;
}

.content-display-area::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.content-display-area::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.content-display-area::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}

/* 公式容器确保不会溢出 */
.math-renderer {
  overflow-x: auto;
  max-width: 100%;
}

/* 内容区域响应式优化 */
@media (max-width: 768px) {
  .example-buttons .el-button {
    font-size: 0.8rem;
  }

  .result-formula {
    font-size: 0.9rem;
  }
}

@media (max-width: 480px) {
  .button-row .el-col {
    margin-bottom: 0.5rem;
  }

  .result-item {
    padding: 1rem;
  }
}
</style>