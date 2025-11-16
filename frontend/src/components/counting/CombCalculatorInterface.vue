<template>
  <div class="comb-calculator-interface">
    <!-- 按钮操作区域 -->
    <div class="button-section">
      <!-- 第一行按钮：开始计算、生成随机值、清除输入、验证输入 -->
      <el-row :gutter="20" class="button-row">
        <el-col :span="4">
          <el-button type="primary" @click="startCalculation">
            <el-icon><Check /></el-icon>
            开始计算(Y)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="success" @click="generateRandomValues">
            <el-icon><MagicStick /></el-icon>
            生成随机值(G)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="warning" @click="clearInput">
            <el-icon><Delete /></el-icon>
            清除输入(V)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="info" @click="validateInput">
            <el-icon><WarningFilled /></el-icon>
            验证输入(K)
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

      <!-- 运算选项设置 -->
      <el-divider content-position="left">选择要进行的数学运算（支持大数计算范围的结果）</el-divider>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-checkbox v-model="options.calculatePower" size="large">
            幂(n^m)
          </el-checkbox>
        </el-col>
        <el-col :span="6">
          <el-checkbox v-model="options.calculateCombination" size="large">
            组合数(C(n, m))
          </el-checkbox>
        </el-col>
        <el-col :span="6">
          <el-checkbox v-model="options.calculatePermutation" size="large">
            排列数(P(n, m))
          </el-checkbox>
        </el-col>
        <el-col :span="6">
          <el-checkbox v-model="options.calculateFactorial" size="large">
            阶乘(n!和m!)
          </el-checkbox>
        </el-col>
      </el-row>
    </div>

    <!-- 参数输入区域 -->
    <div class="input-section">
      <el-divider content-position="left">输入参数n和m进行数学计算</el-divider>
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="input-group">
            <label>底数(n)：</label>
            <el-input
              v-model="inputN"
              placeholder="例如：8"
              type="number"
              class="param-input"
            />
          </div>
        </el-col>
        <el-col :span="12">
          <div class="input-group">
            <label>指数(m)：</label>
            <el-input
              v-model="inputM"
              placeholder="例如：5"
              type="number"
              class="param-input"
            />
          </div>
        </el-col>
      </el-row>
      <div class="input-hint">
        <el-text type="info" size="small">
          请输入非负整数，建议控制在1000以内以避免计算时间过长
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
        <el-divider content-position="left">排列组合数计算分析结果</el-divider>
        <div class="results-content">
          <div v-for="(result, index) in results" :key="index" class="result-item">
            <!-- 基本信息 -->
            <div class="result-basic">
              <h4>输入参数：</h4>
              <math-renderer
                :formula="result.formula"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
            </div>

            <!-- 幂运算结果 -->
            <div v-if="result.powerResult" class="operation-result">
              <h4>幂运算：</h4>
              <math-renderer
                :formula="`${inputN}^{${inputM}} = ${result.powerResult}`"
                :type="'mathjax'"
                :display-mode="true"
                class="operation-formula"
              />
            </div>

            <!-- 组合数结果 -->
            <div v-if="result.combinationResult" class="operation-result">
              <h4>组合数：</h4>
              <math-renderer
                :formula="`C(${inputN}, ${inputM}) = ${result.combinationResult}`"
                :type="'mathjax'"
                :display-mode="true"
                class="operation-formula"
              />
            </div>

            <!-- 排列数结果 -->
            <div v-if="result.permutationResult" class="operation-result">
              <h4>排列数：</h4>
              <math-renderer
                :formula="`P(${inputN}, ${inputM}) = ${result.permutationResult}`"
                :type="'mathjax'"
                :display-mode="true"
                class="operation-formula"
              />
            </div>

            <!-- 阶乘结果 -->
            <div v-if="result.factorialNResult || result.factorialMResult" class="operation-result">
              <h4>阶乘：</h4>
              <div v-if="result.factorialNResult">
                <math-renderer
                  :formula="`${inputN}! = ${result.factorialNResult}`"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="operation-formula"
                />
              </div>
              <div v-if="result.factorialMResult">
                <math-renderer
                  :formula="`${inputM}! = ${result.factorialMResult}`"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="operation-formula"
                />
              </div>
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
const inputN = ref('')
const inputM = ref('')
const feedback = ref([])
const results = ref([])
const counter = ref(0)

// 运算选项
const options = ref({
  calculatePower: true,
  calculateCombination: true,
  calculatePermutation: true,
  calculateFactorial: true
})

// 事件处理函数
const emit = defineEmits(['close', 'comb-calculator'])

const startCalculation = async () => {
  if (!inputN.value.trim() || !inputM.value.trim()) {
    ElMessage.warning('请输入底数n和指数m')
    return
  }

  // 检查是否至少选择了一个运算
  const hasSelectedOperation = Object.values(options.value).some(option => option)
  if (!hasSelectedOperation) {
    ElMessage.warning('请至少选择一种数学运算')
    return
  }

  // 清空之前的反馈和结果
  feedback.value = []
  results.value = []

  try {
    const n = parseInt(inputN.value.trim())
    const m = parseInt(inputM.value.trim())

    if (isNaN(n) || isNaN(m)) {
      ElMessage.error('请输入有效的整数')
      return
    }

    const request = {
      n: n,
      m: m,
      ...options.value
    }

    console.log('CombCalculatorInterface: 开始排列组合计算', request)

    const response = await callBackendApi('/comb-calculator/calculate', {
      method: 'POST',
      body: JSON.stringify(request)
    })

    console.log('CombCalculatorInterface: 收到计算结果', response)

    if (response.success) {
      const result = {
        ...response,
        index: counter.value + 1,
        type: 'comb-calculator'
      }

      results.value.push(result)
      counter.value++

      // 添加成功反馈
      feedback.value.push({
        formula: response.formula,
        type: 'success',
        message: '排列组合计算完成'
      })

      // 发送结果到主界面
      emit('comb-calculator', result)

      ElMessage.success('排列组合计算完成')
    } else {
      feedback.value.push({
        formula: 'n = ?, m = ?',
        type: 'error',
        message: response.errorMessage || '排列组合计算失败'
      })
    }
  } catch (error) {
    console.error('排列组合计算失败:', error)
    feedback.value.push({
      formula: 'n = ?, m = ?',
      type: 'error',
      message: `计算失败: ${error.message}`
    })
  }
}

const generateRandomValues = async () => {
  try {
    console.log('CombCalculatorInterface: 开始生成随机值')

    const response = await callBackendApi('/comb-calculator/generate', {
      method: 'GET'
    })

    console.log('CombCalculatorInterface: 随机值生成结果', response)

    if (response.success) {
      inputN.value = response.n.toString()
      inputM.value = response.m.toString()

      // 设置选项
      Object.assign(options.value, {
        calculatePower: response.calculatePower,
        calculateCombination: response.calculateCombination,
        calculatePermutation: response.calculatePermutation,
        calculateFactorial: response.calculateFactorial
      })

      // 清除之前的结果
      feedback.value = []
      results.value = []

      ElMessage.success('已生成随机参数')
    } else {
      ElMessage.error('生成随机参数失败: ' + response.message)
    }
  } catch (error) {
    console.error('生成随机参数失败:', error)
    ElMessage.error(`生成随机参数失败: ${error.message}`)
  }
}

const clearInput = () => {
  inputN.value = ''
  inputM.value = ''
  ElMessage.info('已清空输入')
}

const validateInput = async () => {
  if (!inputN.value.trim() || !inputM.value.trim()) {
    ElMessage.warning('请输入底数n和指数m')
    return
  }

  feedback.value = []

  try {
    const response = await callBackendApi('/comb-calculator/validate', {
      method: 'POST',
      body: JSON.stringify({
        n: inputN.value.trim(),
        m: inputM.value.trim()
      })
    })

    if (response.valid) {
      feedback.value.push({
        formula: 'n = ?, m = ?',
        type: 'success',
        message: response.message
      })
      ElMessage.success('输入格式正确')
    } else {
      feedback.value.push({
        formula: 'n = ?, m = ?',
        type: 'error',
        message: response.message
      })
      ElMessage.error('输入格式不正确')
    }
  } catch (error) {
    console.error('验证输入失败:', error)
    feedback.value.push({
      formula: 'n = ?, m = ?',
      type: 'error',
      message: `验证失败: ${error.message}`
    })
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
.comb-calculator-interface {
  display: flex;
  flex-direction: column;
  width: 100%;
}

/* 在大屏幕上完全自适应，无最大宽度限制 */
@media (min-width: 1200px) {
  .comb-calculator-interface {
    width: 100%;
    max-width: none;
  }
}

/* 在中等屏幕上设置合理的最大宽度 */
@media (max-width: 1199px) and (min-width: 900px) {
  .comb-calculator-interface {
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
  }
}

/* 在小屏幕上固定宽度，支持水平滚动 */
@media (max-width: 899px) {
  .comb-calculator-interface {
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

.input-section {
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

.param-input {
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

.operation-result {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.operation-formula {
  margin: 1rem 0;
  font-size: 1.1rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  overflow-x: auto;
}

h4 {
  color: #374151;
  margin: 1rem 0 0.5rem 0;
  font-size: 1.1rem;
  font-weight: 600;
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
  .operation-formula {
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