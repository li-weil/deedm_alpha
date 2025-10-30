<template>
  <div class="special-graph-interface">
    <!-- 按钮操作区域 -->
    <div class="button-section">
      <!-- 第一行按钮：开始展示、清除输入、重置选项、清除结果、取消 -->
      <el-row :gutter="20" class="button-row">
        <el-col :span="4">
          <el-button type="primary" @click="startDisplay">
            <el-icon><Check /></el-icon>
            开始展示(Y)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="warning" @click="clearInput">
            <el-icon><Delete /></el-icon>
            清除输入(V)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="info" @click="resetOptions">
            <el-icon><RefreshRight /></el-icon>
            重置选项(R)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="danger" @click="clearResults">
            <el-icon><DeleteFilled /></el-icon>
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

      <!-- 图形参数输入 -->
      <el-divider content-position="left">展示特殊的图的顶点数n和m</el-divider>
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="input-group">
            <label>顶点数(n)：</label>
            <el-input
              v-model.number="inputN"
              type="number"
              :min="1"
              placeholder="请输入顶点数n"
              class="param-input"
            />
          </div>
        </el-col>
        <el-col :span="12">
          <div class="input-group">
            <label>顶点数(m)：</label>
            <el-input
              v-model.number="inputM"
              type="number"
              :min="1"
              placeholder="请输入顶点数m（用于完全二分图）"
              class="param-input"
            />
          </div>
        </el-col>
      </el-row>

      <!-- 选择要展示的图形 -->
      <el-divider content-position="left">选择要展示的图形：</el-divider>
      <el-row :gutter="20">
        <el-col :span="4">
          <el-checkbox v-model="options.showComplete" size="large">
            完全图
          </el-checkbox>
        </el-col>
        <el-col :span="4">
          <el-checkbox v-model="options.showCycle" size="large">
            圈图
          </el-checkbox>
        </el-col>
        <el-col :span="4">
          <el-checkbox v-model="options.showWheel" size="large">
            轮图
          </el-checkbox>
        </el-col>
        <el-col :span="4">
          <el-checkbox v-model="options.showHypercube" size="large">
            超立方体
          </el-checkbox>
        </el-col>
        <el-col :span="4">
          <el-checkbox v-model="options.showBigraph" size="large">
            完全二分图
          </el-checkbox>
        </el-col>
      </el-row>
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

      <!-- 特殊图展示结果区域 -->
      <div v-if="results.length > 0" class="results-section">
        <el-divider content-position="left">特殊图展示结果</el-divider>
        <div class="results-content">
          <div v-for="(result, index) in results" :key="index" class="result-item">

            <!-- 图形展示网格 -->
            <div v-if="result.graphResults && result.graphResults.length > 0" class="graphs-grid">
              <div v-for="graph in result.graphResults" :key="graph.graphType" class="graph-item">
                <div class="graph-header">
                  <h5>{{ graph.description }}：</h5>
                  <math-renderer
                    :formula="graph.name"
                    :type="'mathjax'"
                    :display-mode="false"
                    class="graph-name"
                  />
                </div>

                <div class="graph-content">
                  <!-- 图形可视化 -->
                  <div v-if="graph.generated && graph.imageUrl" class="graph-visualization">
                    <div class="graph-image-container">
                      <img
                        :src="graph.imageUrl"
                        :alt="graph.description"
                        class="graph-image"
                        @error="handleImageError"
                        @load="handleImageLoad"
                      />
                    </div>
                  </div>

                  <!-- 生成失败提示 -->
                  <div v-else class="graph-error">
                    <el-alert
                      :title="`${graph.description}：无法生成`"
                      type="error"
                      :closable="false"
                      show-icon
                    />
                  </div>
                </div>
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
  Delete,
  RefreshRight,
  DeleteFilled,
  Close
} from '@element-plus/icons-vue'
import MathRenderer from '@/components/common/MathRenderer.vue'

// 响应式数据
const inputN = ref(3)
const inputM = ref(5)
const feedback = ref([])
const results = ref([])
const counter = ref(0)

// 显示选项
const options = ref({
  showComplete: false,
  showCycle: false,
  showWheel: false,
  showHypercube: false,
  showBigraph: false
})

// 事件处理函数
const emit = defineEmits(['close', 'special-graph-displayed'])

const startDisplay = async () => {
  if (!validateInput()) {
    return
  }

  // 检查是否至少选择了一种图形
  if (!hasSelectedGraphs()) {
    ElMessage.warning('请至少选择一种要展示的图形')
    return
  }

  // 清空之前的反馈和结果
  feedback.value = []
  results.value = []

  try {
    const request = {
      n: inputN.value,
      m: inputM.value,
      ...options.value
    }

    console.log('SpecialGraphInterface: 开始生成特殊图', request)

    const response = await callBackendApi('/special-graph/generate', {
      method: 'POST',
      body: JSON.stringify(request)
    })

    console.log('SpecialGraphInterface: 收到生成结果', response)

    if (response.success) {
      const result = {
        ...response,
        index: counter.value + 1
      }

      results.value.push(result)
      counter.value++

      // 添加成功反馈
      feedback.value.push({
        formula: `n = ${response.n}, \\quad m = ${response.m}`,
        type: 'success',
        message: '特殊图生成完成'
      })

      // 发送结果到主界面
      emit('special-graph-displayed', result)

      ElMessage.success('特殊图展示完成')
    } else {
      feedback.value.push({
        formula: `n = ${inputN.value}, m = ${inputM.value}`,
        type: 'error',
        message: response.errorMessage || '特殊图生成失败'
      })
    }
  } catch (error) {
    console.error('特殊图生成失败:', error)
    feedback.value.push({
      formula: `n = ${inputN.value}, m = ${inputM.value}`,
      type: 'error',
      message: `生成失败: ${error.message}`
    })
  }
}

const clearInput = () => {
  inputN.value = 3
  inputM.value = 5
  ElMessage.info('已清空输入')
}

const resetOptions = () => {
  options.value = {
    showComplete: false,
    showCycle: false,
    showWheel: false,
    showHypercube: false,
    showBigraph: false
  }
  ElMessage.info('已重置选项')
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

const handleImageError = (event) => {
  console.error('特殊图图片加载失败:', event.target.src)
  ElMessage.error('图形加载失败')
}

const handleImageLoad = (event) => {
  console.log('特殊图图片加载成功:', event.target.src)
}

// 输入验证
const validateInput = () => {
  if (!inputN.value || inputN.value < 1) {
    ElMessage.warning('请输入有效的顶点数 n (≥1)')
    return false
  }

  if (!inputM.value || inputM.value < 1) {
    ElMessage.warning('请输入有效的顶点数 m (≥1)')
    return false
  }

  // 圈图至少需要3个顶点
  if (options.value.showCycle && inputN.value < 3) {
    ElMessage.warning('圈图需要至少3个顶点')
    return false
  }

  // 轮图至少需要4个顶点
  if (options.value.showWheel && inputN.value < 4) {
    ElMessage.warning('轮图需要至少4个顶点')
    return false
  }

  return true
}

// 检查是否至少选择了一种图形
const hasSelectedGraphs = () => {
  return Object.values(options.value).some(option => option)
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
.special-graph-interface {
  display: flex;
  flex-direction: column;
}

.button-section {
  margin-bottom: 1rem;
  flex-shrink: 0;
}

.button-row {
  margin-bottom: 1rem;
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

/* 内容显示区域 */
.content-display-area {
  padding: 1rem;
  background: #ffffff;
  max-height: 60vh;
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

.feedback-formula {
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

.params-info {
  margin-bottom: 1.5rem;
}

.params-formula {
  overflow-x: auto;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  font-size: 1.1rem;
}

.graphs-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 1.5rem;
  margin-top: 1rem;
}

.graph-item {
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.graph-header {
  margin-bottom: 1rem;
  text-align: center;
}

.graph-name {
  font-size: 1.1rem;
  color: #409eff;
}

.graph-content {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 150px;
}

.graph-visualization {
  width: 100%;
}

.graph-image-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 6px;
  border: 1px solid #e9ecef;
}

.graph-image {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  object-fit: contain;
}

.graph-error {
  width: 100%;
}

h4 {
  color: #374151;
  margin: 1rem 0 0.5rem 0;
  font-size: 1.1rem;
  font-weight: 600;
}

h5 {
  color: #6b7280;
  margin: 0.5rem 0;
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

/* 响应式设计 */
@media (max-width: 768px) {
  .special-graph-interface {
    max-height: 90vh;
  }

  .graphs-grid {
    grid-template-columns: 1fr;
  }

  .button-row .el-col {
    margin-bottom: 0.5rem;
  }
}

@media (max-width: 480px) {
  .input-group {
    margin-bottom: 0.5rem;
  }

  .graphs-grid {
    gap: 1rem;
  }

  .result-item {
    padding: 1rem;
  }
}
</style>