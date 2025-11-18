<template>
  <div class="gen-rep-comb-interface">
    <!-- 按钮操作区域 -->
    <div class="button-section">
      <!-- 第一行按钮：开始生成、生成示例、清除输入、合法性检查 -->
      <el-row :gutter="20" class="button-row">
        <el-col :span="4">
          <el-button type="primary" @click="startGeneration">
            <el-icon><Check /></el-icon>
            开始生成(Y)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="success" @click="generateExample">
            <el-icon><MagicStick /></el-icon>
            生成示例(G)
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

      <!-- 第二行按钮：教材示例 -->
      <el-divider content-position="left">《离散数学基础》教材示例展示</el-divider>
      <el-row :gutter="15" class="example-buttons">
        <el-col :span="6">
          <el-button size="small" @click="loadExample('example8_40')">例题8.40</el-button>
        </el-col>
        <el-col :span="6">
          <el-button size="small" @click="loadExample('example8_41')">例题8.41</el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 组合参数输入区域 -->
    <div class="input-section">
      <el-divider content-position="left">允许重复组合的生成参数</el-divider>
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="input-group">
            <label>基础字符集(B)：</label>
            <el-input
              v-model="baseSetInput"
              placeholder="例如：1, 2, 3, 4, 5, 6, 7, 8"
              class="set-input"
            />
          </div>
        </el-col>
        <el-col :span="12">
          <div class="input-group">
            <label>组合长度(L)：</label>
            <el-input
              v-model="lengthInput"
              placeholder="例如：5"
              class="set-input"
              type="number"
            />
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20" style="margin-top: 1rem;">
        <el-col :span="12">
          <div class="input-group">
            <label>起始字符串(S)：</label>
            <el-input
              v-model="startInput"
              placeholder="例如：13458 (可选)"
              class="set-input"
            />
          </div>
        </el-col>
        <el-col :span="12">
          <div class="input-group">
            <label>生成数量(N)：</label>
            <el-input
              v-model="numberInput"
              placeholder="例如：20"
              class="set-input"
              type="number"
            />
          </div>
        </el-col>
      </el-row>
      <div class="input-hint">
        <el-text type="info" size="small">
          基础字符集格式：element1, element2, element3
          <br>
          起始字符串长度必须等于组合长度，如果不提供将使用第一个组合
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

      <!-- 生成结果区域 -->
      <div v-if="results.length > 0" class="results-section">
        <el-divider content-position="left">允许重复组合的生成结果</el-divider>
        <div class="results-content">
          <div v-for="(result, index) in results" :key="index" class="result-item">
            <!-- 基本信息 -->
            <div class="result-basic">
              <h4>生成参数：</h4>
              <math-renderer
                :formula="`B = ${result.baseSetLaTeX}, \\quad n = ${result.length}, \\quad \\text{从} \\, ${result.startString} \\, \\text{开始生成} \\, ${result.number} \\, \\text{个组合}`"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
            </div>

            <!-- 生成结果 -->
            <div v-if="result.generatedCombinations && result.generatedCombinations.length > 0" class="combinations-result">
              <h4>生成的组合序列：</h4>
              <div class="combination-grid">
                <div
                  v-for="(combination, idx) in result.generatedCombinations"
                  :key="idx"
                  class="combination-item"
                >
                  <math-renderer
                    :formula="combination"
                    :type="'mathjax'"
                    :display-mode="false"
                    class="combination-formula"
                  />
                  <span v-if="idx < result.generatedCombinations.length - 1" class="arrow">→</span>
                </div>
                <div v-if="result.generatedCombinations.length >= result.number" class="continuation">
                  <span>··· ···</span>
                </div>
              </div>
            </div>

            <!-- 统计信息 -->
            <div class="statistics-result">
              <h4>统计信息：</h4>
              <math-renderer
                :formula="`\\text{总组合数} = ${result.baseSetSize}^{${result.length}} \\text{ 或 } ${result.combinationFormula} = ${result.totalCombinations}`"
                :type="'mathjax'"
                :display-mode="true"
                class="statistics-formula"
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
const baseSetInput = ref('')
const lengthInput = ref('')
const startInput = ref('')
const numberInput = ref('')
const feedback = ref([])
const results = ref([])
const counter = ref(0)

// 示例数据
const examples = {
  example8_40: {
    baseSet: '1, 2, 3, 4, 5, 6, 7, 8',
    length: 5,
    start: '13458',
    number: 20
  },
  example8_41: {
    baseSet: '1, 2, 3, 4, 5, 6, 7, 8',
    length: 5,
    start: '13478',
    number: 20
  }
}

// 事件处理函数
const emit = defineEmits(['close', 'gen-rep-comb-result'])

const startGeneration = async () => {
  if (!baseSetInput.value.trim() || !lengthInput.value.trim() || !numberInput.value.trim()) {
    ElMessage.warning('请输入基础字符集、组合长度和生成数量')
    return
  }

  // 清空之前的反馈和结果
  feedback.value = []
  results.value = []

  try {
    const request = {
      baseSetString: baseSetInput.value.trim(),
      length: parseInt(lengthInput.value.trim()),
      startString: startInput.value.trim() || '',
      number: parseInt(numberInput.value.trim()) || 20
    }

    console.log('GenRepCombInterface: 开始生成允许重复组合', request)

    const response = await callBackendApi('/gen-rep-comb/generate', {
      method: 'POST',
      body: JSON.stringify(request)
    })

    console.log('GenRepCombInterface: 收到生成结果', response)

    if (response.success) {
      const result = {
        ...response,
        index: counter.value + 1
      }

      results.value.push(result)
      counter.value++

      // 添加成功反馈
      feedback.value.push({
        formula: `B = ${response.baseSetLaTeX}, \\quad n = ${response.length}, \\quad \\text{从} \\, ${response.startString} \\, \\text{开始生成} \\, ${response.number} \\, \\text{个组合}`,
        type: 'success',
        message: '允许重复组合生成完成'
      })

      // 发送结果到主界面
      emit('gen-rep-comb-result', result)

      ElMessage.success('允许重复组合生成完成')
    } else {
      feedback.value.push({
        formula: 'B = ?, n = ?',
        type: 'error',
        message: response.errorMessage || '生成失败'
      })
    }
  } catch (error) {
    console.error('生成允许重复组合失败:', error)
    feedback.value.push({
      formula: 'B = ?, n = ?',
      type: 'error',
      message: `生成失败: ${error.message}`
    })
  }
}

const generateExample = async () => {
  try {
    console.log('GenRepCombInterface: 开始生成示例')

    const response = await callBackendApi('/gen-rep-comb/example', {
      method: 'GET'
    })

    console.log('GenRepCombInterface: 示例生成结果', response)

    if (response.baseSetString) {
      baseSetInput.value = response.baseSetString
      lengthInput.value = response.length.toString()
      startInput.value = response.startString || ''
      numberInput.value = response.number.toString()

      // 清除之前的结果
      feedback.value = []
      results.value = []

      ElMessage.success('已生成示例')
    } else {
      ElMessage.error('生成示例失败')
    }
  } catch (error) {
    console.error('生成示例失败:', error)
    ElMessage.error(`生成示例失败: ${error.message}`)
  }
}

const clearInput = () => {
  baseSetInput.value = ''
  lengthInput.value = ''
  startInput.value = ''
  numberInput.value = ''
  ElMessage.info('已清空输入')
}

const checkInput = async () => {
  if (!baseSetInput.value.trim() || !lengthInput.value.trim() || !numberInput.value.trim()) {
    ElMessage.warning('请输入基础字符集、组合长度和生成数量')
    return
  }

  feedback.value = []

  try {
    const response = await callBackendApi('/gen-rep-comb/validate', {
      method: 'POST',
      body: JSON.stringify({
        baseSetString: baseSetInput.value.trim(),
        lengthString: lengthInput.value.trim(),
        startString: startInput.value.trim(),
        numberString: numberInput.value.trim()
      })
    })

    if (response.valid) {
      feedback.value.push({
        formula: 'B = ?, n = ?',
        type: 'success',
        message: response.message
      })
      ElMessage.success('输入格式正确')
    } else {
      feedback.value.push({
        formula: 'B = ?, n = ?',
        type: 'error',
        message: response.message
      })
      ElMessage.error('输入格式不正确')
    }
  } catch (error) {
    console.error('检查输入失败:', error)
    feedback.value.push({
      formula: 'B = ?, n = ?',
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
    baseSetInput.value = example.baseSet
    lengthInput.value = example.length.toString()
    startInput.value = example.start
    numberInput.value = example.number.toString()

    console.log('GenRepCombInterface: 加载示例', exampleKey, example)
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
.gen-rep-comb-interface {
  display: flex;
  flex-direction: column;
  width: 100%;
}

/* 在大屏幕上完全自适应，无最大宽度限制 */
@media (min-width: 1200px) {
  .gen-rep-comb-interface {
    width: 100%;
    max-width: none;
  }
}

/* 在中等屏幕上设置合理的最大宽度 */
@media (max-width: 1199px) and (min-width: 900px) {
  .gen-rep-comb-interface {
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
  }
}

/* 在小屏幕上固定宽度，支持水平滚动 */
@media (max-width: 899px) {
  .gen-rep-comb-interface {
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

.combinations-result,
.statistics-result {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.combination-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  align-items: center;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 4px;
  overflow-x: auto;
}

.combination-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.combination-formula {
  font-size: 1rem;
  padding: 0.25rem 0.5rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
}

.arrow {
  margin: 0 0.25rem;
  color: #6b7280;
  font-weight: bold;
}

.continuation {
  grid-column: 1 / -1;
  text-align: center;
  color: #6b7280;
  font-style: italic;
}

.statistics-formula {
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
  .example-buttons .el-button {
    font-size: 0.8rem;
  }

  .combination-grid {
    font-size: 0.9rem;
  }

  .statistics-formula {
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

  .combination-grid {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>