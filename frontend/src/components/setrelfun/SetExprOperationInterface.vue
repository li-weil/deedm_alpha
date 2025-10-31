<template>
  <div class="set-expr-operation-interface">
    <!-- 按钮操作区域 -->
    <div class="button-section">
      <!-- 第一行按钮：开始运算、生成随机集合、清除输入、合法性检查 -->
      <el-row :gutter="20" class="button-row">
        <el-col :span="4">
          <el-button type="primary" @click="startCalculation">
            <el-icon><Check /></el-icon>
            开始运算(Y)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="success" @click="generateRandomSets">
            <el-icon><MagicStick /></el-icon>
            生成随机集合(G)
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
          <el-button size="small" @click="loadExample('example5_4')">例题5.4</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('example5_9')">例题5.9</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('example5_13')">例题5.13</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('example5_15')">例题5.15</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('example5_17')">例题5.17</el-button>
        </el-col>
      </el-row>

      <!-- 元素类型选择 -->
      <el-divider content-position="left">集合元素类型选择</el-divider>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-radio-group v-model="elementType" size="large">
            <el-radio label="char">单个字符</el-radio>
            <el-radio label="int">整数</el-radio>
          </el-radio-group>
        </el-col>
      </el-row>
    </div>

    <!-- 集合输入区域 -->
    <div class="set-input-section">
      <el-divider content-position="left">输入集合的元素和集合表达式</el-divider>
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="input-group">
            <label>全集(U)：</label>
            <el-input
              v-model="setUInput"
              placeholder="例如：{1, 2, 3, 4, 5}"
              class="set-input"
            />
          </div>
        </el-col>

      </el-row>
      <el-row :gutter="20" style="margin-top: 1rem;">
        <el-col :span="12">
          <div class="input-group">
            <label>集合(A)：</label>
            <el-input
              v-model="setAInput"
              placeholder="例如：{1, 3, 5}"
              class="set-input"
            />
          </div>
        </el-col>
        <el-col :span="12">
          <div class="input-group">
            <label>集合(B)：</label>
            <el-input
              v-model="setBInput"
              placeholder="例如：{2, 4, 6}"
              class="set-input"
            />
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20" style="margin-top: 1rem;">
        <el-col :span="24">
          <div class="input-group">
            <label>集合表达式(n)：</label>
            <el-input
              v-model="expressionInput"
              placeholder="例如：\overline{A\cap B \oplus A}"
              class="set-input"
            />
          </div>
        </el-col>
      </el-row>
      <div class="input-hint">
        <el-text type="info" size="small">
          集合格式：{element1, element2, element3}
          <br>
          整数示例：{1, 2, 3} 字符示例：{a, b, c}
          <br>
          表达式支持运算符：\cap(交集), \cup(并集), \oplus(对称差), \overline(补集), -(差集)
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
        <el-divider content-position="left">集合表达式运算结果</el-divider>
        <div class="results-content">
          <div v-for="(result, index) in results" :key="index" class="result-item">
            <!-- 集合基本信息 -->
            <div class="result-basic">
              <h4>输入集合：</h4>
              <math-renderer
                :formula="result.formula"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
            </div>

            <!-- 表达式运算结果 -->
            <div v-if="result.latexResult" class="operation-result">
              <h4>表达式运算结果：</h4>
              <math-renderer
                :formula="`result = ${result.latexResult}`"
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
const setUInput = ref('')
const setAInput = ref('')
const setBInput = ref('')
const expressionInput = ref('')
const elementType = ref('char')
const feedback = ref([])
const results = ref([])
const counter = ref(0)

// 示例数据
const examples = {
  example5_4: {
    setU: '{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16}',
    setA: '{1, 3, 5, 7, 9, 11}',
    setB: '{1, 4, 7, 10, 13, 16}',
    expression: '\\overline{A\\cap B \\oplus A}',
    elementType: 'int'
  },
  example5_9: {
    setU: '{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16}',
    setA: '{1, 3, 5, 7, 9, 11}',
    setB: '{1, 4, 7, 10, 13, 16}',
    expression: '\\overline{A\\cap B \\oplus A}',
    elementType: 'int'
  },
  example5_13: {
    setU: '{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16}',
    setA: '{1, 3, 5, 7, 9, 11}',
    setB: '{1, 4, 7, 10, 13, 16}',
    expression: '\\overline{A\\cap B \\oplus A}',
    elementType: 'int'
  },
  example5_15: {
    setU: '{1, 2, 3, 4}',
    setA: '{1, 2, 3}',
    setB: '{1, 4}',
    expression: '\\overline{A\\cap B \\oplus A}',
    elementType: 'int'
  },
  example5_17: {
    setU: '{1, 2, 3, 4, 5, 6, 7, 8}',
    setA: '{1, 3, 5, 7}',
    setB: '{1, 4, 7}',
    expression: '\\overline{A\\cap B \\oplus A}',
    elementType: 'int'
  }
}

// 事件处理函数
const emit = defineEmits(['close', 'set-expr-operation-result'])

const startCalculation = async () => {
  if (!setUInput.value.trim() || !setAInput.value.trim() || !setBInput.value.trim()) {
    ElMessage.warning('请输入全集U、集合A和集合B')
    return
  }

  if (!expressionInput.value.trim()) {
    ElMessage.warning('请输入集合表达式')
    return
  }

  // 清空之前的反馈和结果
  feedback.value = []
  results.value = []

  try {
    const request = {
      setU: setUInput.value.trim(),
      setA: setAInput.value.trim(),
      setB: setBInput.value.trim(),
      expression: expressionInput.value.trim(),
      intTypeElement: elementType.value === 'int'
    }

    console.log('SetExprOperationInterface: 开始集合表达式运算', request)

    const response = await callBackendApi('/set-expression-operation/calculate', {
      method: 'POST',
      body: JSON.stringify(request)
    })

    console.log('SetExprOperationInterface: 收到运算结果', response)

    if (response.success) {
      const result = {
        ...response,
        index: counter.value + 1
      }

      results.value.push(result)
      counter.value++

      // 添加成功反馈
      feedback.value.push({
        formula: response.formula,
        type: 'success',
        message: '集合表达式运算完成'
      })

      // 发送结果到主界面
      emit('set-expr-operation-result', result)

      ElMessage.success('集合表达式运算完成')
    } else {
      feedback.value.push({
        formula: 'U = ?, A = ?, B = ?, expression = ?',
        type: 'error',
        message: response.errorMessage || '集合表达式运算失败'
      })
    }
  } catch (error) {
    console.error('集合表达式运算失败:', error)
    feedback.value.push({
      formula: 'U = ?, A = ?, B = ?, expression = ?',
      type: 'error',
      message: `运算失败: ${error.message}`
    })
  }
}

const generateRandomSets = async () => {
  try {
    console.log('SetExprOperationInterface: 开始生成随机集合')

    const response = await callBackendApi('/set-expression-operation/generate', {
      method: 'GET'
    })

    console.log('SetExprOperationInterface: 随机集合生成结果', response)

    if (response.success) {
      setUInput.value = response.setU
      setAInput.value = response.setA
      setBInput.value = response.setB
      expressionInput.value = response.expression
      elementType.value = response.intTypeElement ? 'int' : 'char'

      // 清除之前的结果
      feedback.value = []
      results.value = []

      ElMessage.success('已生成随机集合')
    } else {
      ElMessage.error('生成随机集合失败: ' + response.message)
    }
  } catch (error) {
    console.error('生成随机集合失败:', error)
    ElMessage.error(`生成随机集合失败: ${error.message}`)
  }
}

const clearInput = () => {
  setUInput.value = ''
  setAInput.value = ''
  setBInput.value = ''
  expressionInput.value = ''
  ElMessage.info('已清空输入')
}

const checkInput = async () => {
  if (!setUInput.value.trim() || !setAInput.value.trim() || !setBInput.value.trim()) {
    ElMessage.warning('请输入全集U、集合A和集合B')
    return
  }

  if (!expressionInput.value.trim()) {
    ElMessage.warning('请输入集合表达式')
    return
  }

  feedback.value = []

  try {
    const response = await callBackendApi('/set-expression-operation/validate', {
      method: 'POST',
      body: JSON.stringify({
        setU: setUInput.value.trim(),
        setA: setAInput.value.trim(),
        setB: setBInput.value.trim(),
        expression: expressionInput.value.trim(),
        intTypeElement: elementType.value === 'int'
      })
    })

    if (response.valid) {
      feedback.value.push({
        formula: 'U = ?, A = ?, B = ?, expression = ?',
        type: 'success',
        message: response.message
      })
      ElMessage.success('输入格式正确')
    } else {
      feedback.value.push({
        formula: 'U = ?, A = ?, B = ?, expression = ?',
        type: 'error',
        message: response.message
      })
      ElMessage.error('输入格式不正确')
    }
  } catch (error) {
    console.error('检查输入失败:', error)
    feedback.value.push({
      formula: 'U = ?, A = ?, B = ?, expression = ?',
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
    setUInput.value = example.setU
    setAInput.value = example.setA
    setBInput.value = example.setB
    expressionInput.value = example.expression
    elementType.value = example.elementType

    console.log('SetExprOperationInterface: 加载示例', exampleKey, example)
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
.set-expr-operation-interface {
  display: flex;
  flex-direction: column;
  width: 100%;
}

/* 在大屏幕上完全自适应，无最大宽度限制 */
@media (min-width: 1200px) {
  .set-expr-operation-interface {
    width: 100%;
    max-width: none;
  }
}

/* 在中等屏幕上设置合理的最大宽度 */
@media (max-width: 1199px) and (min-width: 900px) {
  .set-expr-operation-interface {
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
  }
}

/* 在小屏幕上固定宽度，支持水平滚动 */
@media (max-width: 899px) {
  .set-expr-operation-interface {
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

.set-input-section {
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

.set-input {
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