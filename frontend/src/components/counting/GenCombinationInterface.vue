<template>
  <div class="gen-combination-interface">
    <!-- 按钮操作区域 -->
    <div class="button-section">
      <!-- 第一行按钮：开始生成、清除输入、合法性检查、清除结果、取消 -->
      <el-row :gutter="20" class="button-row">
        <el-col :span="4">
          <el-button type="primary" @click="startGeneration">
            <el-icon><Check /></el-icon>
            开始生成(Y)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="info" @click="checkInput">
            <el-icon><WarningFilled /></el-icon>
            合法性检查(K)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="warning" @click="clearInput">
            <el-icon><Delete /></el-icon>
            清除输入(V)
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
        <el-col :span="6">
          <el-button size="small" @click="loadExample('example8_40')">例题8.40</el-button>
        </el-col>
        <el-col :span="6">
          <el-button size="small" @click="loadExample('example8_41')">例题8.41</el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 参数输入区域 -->
    <div class="input-section">
      <el-divider content-position="left">参数输入</el-divider>

      <!-- 第一行：基础字符集和组合长度 -->
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="input-group">
            <label>基础字符集(B)：</label>
            <el-input
              v-model="baseSetInput"
              placeholder="例如：1, 2, 3, 4, 5, 6, 7, 8"
              class="param-input"
            />
          </div>
        </el-col>
        <el-col :span="12">
          <div class="input-group">
            <label>组合长度(L)：</label>
            <el-input-number
              v-model="lengthInput"
              :min="1"
              :max="20"
              placeholder="例如：5"
              class="param-input"
            />
          </div>
        </el-col>
      </el-row>

      <!-- 第二行：起始字符串和生成数量 -->
      <el-row :gutter="20" style="margin-top: 1rem;">
        <el-col :span="12">
          <div class="input-group">
            <label>起始字符串(S)：</label>
            <el-input
              v-model="startStringInput"
              placeholder="例如：13458 (可选)"
              class="param-input"
            />
          </div>
        </el-col>
        <el-col :span="12">
          <div class="input-group">
            <label>生成数量(N)：</label>
            <el-input-number
              v-model="numberInput"
              :min="1"
              :max="100"
              placeholder="例如：20"
              class="param-input"
            />
          </div>
        </el-col>
      </el-row>

      <div class="input-hint">
        <el-text type="info" size="small">
          基础字符集格式：element1, element2, element3 (用逗号分隔)
          <br>
          整数示例：1, 2, 3  字符示例：a, b, c
          <br>
          起始字符串长度必须等于组合长度，如不填写将从第一个组合开始生成
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
        <el-divider content-position="left">组合生成结果</el-divider>
        <div class="results-content">
          <div v-for="(result, index) in results" :key="index" class="result-item">
            <!-- 基本信息显示 -->
            <div class="result-basic">
              <h4>生成参数：</h4>
              <math-renderer
                :formula="result.latexDescription"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
            </div>

            <!-- 组合序列显示 -->
            <div v-if="result.generatedCombinations && result.generatedCombinations.length > 0" class="combination-result">
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

            <!-- 统计信息显示 -->
            <div class="statistics-result">
              <h4>统计信息：</h4>
              <math-renderer
                :formula="`总组合数 = ${result.latexFormula}`"
                :type="'mathjax'"
                :display-mode="true"
                class="statistics-formula"
              />
              <p>从基础集 {{ result.baseSetLaTeX }} 中允许重复地取 {{ result.length }} 个元素进行组合，总共有 {{ result.totalCombinations }} 种不同的组合。</p>
            </div>

            <!-- 警告信息 -->
            <div v-if="result.errorMessage && result.errorMessage.includes('警告')" class="warning-section">
              <el-alert
                :title="result.errorMessage"
                type="warning"
                :closable="false"
                show-icon
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
  WarningFilled,
  Delete,
  RefreshRight,
  Close
} from '@element-plus/icons-vue'
import MathRenderer from '@/components/common/MathRenderer.vue'

// 响应式数据
const baseSetInput = ref('')
const lengthInput = ref(5)
const startStringInput = ref('')
const numberInput = ref(20)
const feedback = ref([])
const results = ref([])
const counter = ref(0)

// 事件处理函数
const emit = defineEmits(['close', 'generate-combination-result'])

const startGeneration = async () => {
  if (!baseSetInput.value.trim()) {
    ElMessage.warning('请输入基础字符集')
    return
  }

  if (!lengthInput.value || lengthInput.value <= 0) {
    ElMessage.warning('请输入有效的组合长度')
    return
  }

  // 清空之前的反馈和结果
  feedback.value = []
  results.value = []

  try {
    const request = {
      baseSet: baseSetInput.value.trim(),
      length: lengthInput.value,
      startString: startStringInput.value.trim(),
      number: numberInput.value || 20
    }

    const response = await callBackendApi('/gen-combination/generate', {
      method: 'POST',
      body: JSON.stringify(request)
    })

    // 增加计数器
    counter.value++

    // 添加反馈信息
    feedback.value.push({
      formula: response.latexDescription,
      message: '开始生成组合...',
      type: 'success'
    })

    // 添加结果
    results.value.push(response)

    // 发送结果到主界面
    emit('generate-combination-result', response)

    ElMessage.success('组合生成完成')

  } catch (error) {
    console.error('生成组合失败:', error)
    ElMessage.error('生成组合失败: ' + error.message)

    // 添加错误反馈
    feedback.value.push({
      formula: '生成失败',
      message: error.message,
      type: 'error'
    })
  }
}

const checkInput = async () => {
  if (!baseSetInput.value.trim()) {
    ElMessage.warning('请输入基础字符集')
    return
  }

  try {
    const request = {
      baseSet: baseSetInput.value.trim(),
      length: lengthInput.value,
      startString: startStringInput.value.trim(),
      number: numberInput.value || 20
    }

    // 调用生成API获取完整的LaTeX公式描述
    const response = await callBackendApi('/gen-combination/generate', {
      method: 'POST',
      body: JSON.stringify(request)
    })

    if (response.errorMessage) {
      ElMessage.error('输入验证失败')
      feedback.value = [{
        formula: '输入格式错误',
        message: response.errorMessage,
        type: 'error'
      }]
    } else {
      ElMessage.success('输入验证通过')
      // 只显示LaTeX公式，不显示"输入格式正确"文本
      feedback.value = [{
        formula: response.latexDescription,
        message: null,
        type: 'success'
      }]
    }
  } catch (error) {
    console.error('输入验证失败:', error)
    ElMessage.error('输入验证失败: ' + error.message)
  }
}

const clearInput = () => {
  baseSetInput.value = ''
  lengthInput.value = 5
  startStringInput.value = ''
  numberInput.value = 20
  feedback.value = []
  ElMessage.success('输入已清除')
}

const clearResults = () => {
  results.value = []
  feedback.value = []
  counter.value = 0
  ElMessage.success('结果已清除')
}

const loadExample = async (exampleType) => {
  try {
    const response = await callBackendApi('/gen-combination/example', {
      method: 'POST',
      body: JSON.stringify({ exampleType })
    })

    baseSetInput.value = response.baseSet
    lengthInput.value = response.length
    startStringInput.value = response.startString
    numberInput.value = response.number

    // 自动进行输入检查
    await checkInput()

  } catch (error) {
    console.error('加载示例失败:', error)
    ElMessage.error('加载示例失败: ' + error.message)
  }
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
      body: options.body,
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

.gen-combination-interface {
  display: flex;
  flex-direction: column;
  width: 100%;
}

/* 在大屏幕上完全自适应，无最大宽度限制 */
@media (min-width: 1200px) {
  .gen-combination-interface {
    width: 100%;
    max-width: none;
  }
}

/* 在中等屏幕上设置合理的最大宽度 */
@media (max-width: 1199px) and (min-width: 900px) {
  .gen-combination-interface {
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
  }
}

/* 在小屏幕上固定宽度，支持水平滚动 */
@media (max-width: 899px) {
  .gen-combination-interface {
    width: 900px;
    min-width: 900px;
    max-width: 900px;
    margin: 0 auto;
  }
}



/* 按钮区域样式 */
.button-section {
  padding: 1rem;
  background: #ffffff;
  flex-shrink: 0;
}

.button-row {
  margin-bottom: 1rem;
}

.button-row .el-button {
  width: 100%;
}

.example-buttons .el-button {
  width: 100%;
  margin-bottom: 0.5rem;
}

/* 输入区域样式 */
.input-section {
  padding: 1rem;
  background: #ffffff;
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
  width: 100%;
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
  flex: 1;
  padding: 1rem;
  background: #ffffff;
  overflow-y: auto;
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

/* 组合序列样式 */
.combination-result {
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

/* 统计信息样式 */
.statistics-result {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
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

.statistics-text {
  font-size: 0.9rem;
  margin-top: 0.5rem;
  line-height: 1.5;
}

/* 警告区域样式 */
.warning-section {
  margin-top: 1rem;
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

/* 响应式优化 */
@media (max-width: 768px) {
  .button-row .el-col {
    margin-bottom: 0.5rem;
  }

  .example-buttons .el-button {
    font-size: 0.8rem;
  }

  .combination-grid {
    font-size: 0.9rem;
  }
}

@media (max-width: 480px) {
  .result-item {
    padding: 1rem;
  }

  .combination-grid {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>