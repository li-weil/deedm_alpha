<template>
  <div class="recursion-expression-interface">
    <!-- 按钮操作区域 -->
    <div class="button-section">
      <!-- 第一行按钮：开始运算、生成随机表达式、清除输入、合法性检查 -->
      <el-row :gutter="20" class="button-row">
        <el-col :span="4">
          <el-button type="primary" @click="startCalculation">
            <el-icon><Check /></el-icon>
            开始运算(Y)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="success" @click="generateRandomExpression">
            <el-icon><MagicStick /></el-icon>
            生成随机表达式(G)
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
      <el-divider content-position="left">《离散数学基础》递归关系示例展示</el-divider>
      <el-row :gutter="15" class="example-buttons">
        <el-col :span="4">
          <el-button size="small" @click="loadExample('fibonacci')">斐波那契数列</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('factorial')">阶乘递归</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('power')">2的幂次</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('linear')">线性递归</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('quadratic')">平方递归</el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 递归表达式输入区域 -->
    <div class="expression-input-section">
      <el-divider content-position="left">递归表达式的输入</el-divider>

      <!-- 说明信息 -->
      <el-alert
        title="递归表达式说明"
        description="支持含有初始值为一或二个未知数的递归计算，初始值未填写时使用默认值1。支持加减乘除、乘方、阶乘、排列组合等运算。"
        type="info"
        :closable="false"
        show-icon
        style="margin-bottom: 1rem;"
      />

      <el-row :gutter="20">
        <el-col :span="24">
          <div class="input-group">
            <label>递归表达式(E)：</label>
            <el-input
              v-model="expressionInput"
              placeholder="例如：a + b, 2*a + 1, a^2 + 1"
              class="expression-input"
            />
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 1rem;">
        <el-col :span="8">
          <div class="input-group">
            <label>递归次数n(N)：</label>
            <el-input
              v-model="inputN"
              placeholder="例如：5"
              class="param-input"
            />
          </div>
        </el-col>
        <el-col :span="8">
          <div class="input-group">
            <label>初始值a(A)：</label>
            <el-input
              v-model="inputA"
              placeholder="默认值：1"
              class="param-input"
            />
          </div>
        </el-col>
        <el-col :span="8">
          <div class="input-group">
            <label>初始值b(B)：</label>
            <el-input
              v-model="inputB"
              placeholder="默认值：1"
              class="param-input"
            />
          </div>
        </el-col>
      </el-row>

      <div class="input-hint">
        <el-text type="info" size="small">
          <strong>支持运算符：</strong>+ - * / ^ ! P(排列) C(组合) , (参数分隔符) () (括号)
          <br>
          <strong>变量说明：</strong>a, b表示初始值，n表示当前递归次数
          <br>
          <strong>示例：</strong>a + b (斐波那契), 2*a + 1 (等差数列), a * b (乘法递归)
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

      <!-- 运算结果区域 -->
      <div v-if="results.length > 0" class="results-section">
        <el-divider content-position="left">递归表达式计算结果</el-divider>
        <div class="results-content">
          <div v-for="(result, index) in results" :key="index" class="result-item">
            <!-- 递归表达式基本信息 -->
            <div class="result-basic">
              <h4>递归表达式参数：</h4>
              <div class="formula-info">
                <p><strong>表达式：</strong>{{ result.originalExpression }}</p>
                <p><strong>递归次数 n = </strong>{{ result.n }}</p>
                <p><strong>初始值 a = </strong>{{ result.a }}</p>
                <p><strong>初始值 b = </strong>{{ result.b }}</p>
              </div>
            </div>

            <!-- 计算结果 -->
            <div class="calculation-result">
              <h4>计算结果：</h4>
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
  MagicStick,
  Delete,
  WarningFilled,
  RefreshRight,
  Close
} from '@element-plus/icons-vue'
import MathRenderer from '@/components/common/MathRenderer.vue'

// 响应式数据
const expressionInput = ref('')
const inputN = ref('')
const inputA = ref('')
const inputB = ref('')
const feedback = ref([])
const results = ref([])
const counter = ref(0)

// 示例数据
const examples = {
  fibonacci: {
    expression: 'a + b',
    n: '5',
    a: '1',
    b: '1'
  },
  factorial: {
    expression: 'n * a',
    n: '5',
    a: '1',
    b: '1'
  },
  power: {
    expression: '2 * a',
    n: '5',
    a: '1',
    b: '1'
  },
  linear: {
    expression: '2 * a + 1',
    n: '5',
    a: '1',
    b: '1'
  },
  quadratic: {
    expression: 'a * a + 1',
    n: '4',
    a: '1',
    b: '1'
  }
}

// 事件处理函数
const emit = defineEmits(['close', 'recu-expr-calculator'])

const startCalculation = async () => {
  if (!expressionInput.value.trim()) {
    ElMessage.warning('请输入递归表达式')
    return
  }

  if (!inputN.value.trim()) {
    ElMessage.warning('请输入递归次数n')
    return
  }

  // 清空之前的反馈和结果
  feedback.value = []
  results.value = []

  try {
    const request = {
      expression: expressionInput.value.trim(),
      n: inputN.value.trim(),
      a: inputA.value.trim() || '1',
      b: inputB.value.trim() || '1'
    }

    console.log('RecursionExpressionInterface: 开始递归表达式计算', request)

    const response = await callBackendApi('/counting/recursion-expression/calculate', {
      method: 'POST',
      body: JSON.stringify(request)
    })

    console.log('RecursionExpressionInterface: 收到计算结果', response)

    if (response.success) {
      const result = {
        ...response,
        index: counter.value + 1,
        type: 'recu-expr-calculator'
      }

      results.value.push(result)
      counter.value++

      // 添加成功反馈
      feedback.value.push({
        formula: `\\text{递归表达式: ${request.expression}, 初始值: a_1=${request.a}`,
        type: 'success',
        message: '递归表达式计算完成'
      })

      // 发送结果到主界面
      emit('recu-expr-calculator', result)

      ElMessage.success('递归表达式计算完成')
    } else {
      feedback.value.push({
        formula: `\\text{递归表达式: ${request.expression}}`,
        type: 'error',
        message: response.message || '递归表达式计算失败'
      })
    }
  } catch (error) {
    console.error('递归表达式计算失败:', error)
    feedback.value.push({
      formula: `\\text{递归表达式: ${expressionInput.value}}`,
      type: 'error',
      message: `计算失败: ${error.message}`
    })
  }
}

const generateRandomExpression = async () => {
  try {
    console.log('RecursionExpressionInterface: 开始生成随机递归表达式')

    const response = await callBackendApi('/counting/recursion-expression/generate', {
      method: 'GET'
    })

    console.log('RecursionExpressionInterface: 随机表达式生成结果', response)

    if (response.success) {
      expressionInput.value = response.expression
      inputN.value = response.n
      inputA.value = response.a
      inputB.value = response.b

      // 清除之前的结果
      feedback.value = []
      results.value = []

      ElMessage.success('已生成随机递归表达式')
    } else {
      ElMessage.error('生成随机递归表达式失败: ' + response.message)
    }
  } catch (error) {
    console.error('生成随机递归表达式失败:', error)
    ElMessage.error(`生成随机递归表达式失败: ${error.message}`)
  }
}

const clearInput = () => {
  expressionInput.value = ''
  inputN.value = ''
  inputA.value = ''
  inputB.value = ''
  ElMessage.info('已清空输入')
}

const checkInput = async () => {
  if (!expressionInput.value.trim()) {
    ElMessage.warning('请输入递归表达式')
    return
  }

  if (!inputN.value.trim()) {
    ElMessage.warning('请输入递归次数n')
    return
  }

  feedback.value = []

  try {
    const response = await callBackendApi('/counting/recursion-expression/validate', {
      method: 'POST',
      body: JSON.stringify({
        expression: expressionInput.value.trim(),
        n: inputN.value.trim(),
        a: inputA.value.trim() || '1',
        b: inputB.value.trim() || '1'
      })
    })

    if (response.valid) {
      feedback.value.push({
        formula: `\\text{递归表达式: ${expressionInput.value}}`,
        type: 'success',
        message: response.message
      })
      ElMessage.success('递归表达式格式正确')
    } else {
      feedback.value.push({
        formula: `\\text{递归表达式: ${expressionInput.value}}`,
        type: 'error',
        message: response.message
      })
      ElMessage.error('递归表达式格式不正确')
    }
  } catch (error) {
    console.error('检查递归表达式失败:', error)
    feedback.value.push({
      formula: `\\text{递归表达式: ${expressionInput.value}}`,
      type: 'error',
      message: `检查失败: ${error.message}`
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

const loadExample = (exampleKey) => {
  if (examples[exampleKey]) {
    const example = examples[exampleKey]

    // 清除之前的结果
    results.value = []
    feedback.value = []
    counter.value = 0

    // 设置示例数据
    expressionInput.value = example.expression
    inputN.value = example.n
    inputA.value = example.a
    inputB.value = example.b

    console.log('RecursionExpressionInterface: 加载示例', exampleKey, example)
    ElMessage.info(`已加载示例：${exampleKey}`)
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
.recursion-expression-interface {
  display: flex;
  flex-direction: column;
  width: 100%;
}

/* 在大屏幕上完全自适应，无最大宽度限制 */
@media (min-width: 1200px) {
  .recursion-expression-interface {
    width: 100%;
    max-width: none;
  }
}

/* 在中等屏幕上设置合理的最大宽度 */
@media (max-width: 1199px) and (min-width: 900px) {
  .recursion-expression-interface {
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
  }
}

/* 在小屏幕上固定宽度，支持水平滚动 */
@media (max-width: 899px) {
  .recursion-expression-interface {
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

.expression-input,
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

.formula-info p {
  margin: 0.5rem 0;
  font-size: 0.95rem;
  color: #6b7280;
}

.calculation-result {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
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