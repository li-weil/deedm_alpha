<template>
  <div class="gen-permutation-interface">
    <!-- 按钮操作区域 -->
    <div class="button-section">
      <!-- 第一行按钮：开始生成、合法性检查、清除输入、清除结果、取消 -->
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
        <el-col :span="4">
          <el-button size="small" @click="loadExample">例题8.38</el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 输入参数区域 -->
    <div class="input-section">
      <el-divider content-position="left">排列生成的输入字符串、长度，以及起始字符串和生成个数（通过起始字符串开始，避免结果太长而太多重，起始字符串可以任意指定，将从找到的第一个开始）</el-divider>

      <el-row :gutter="20">
        <el-col :span="12">
          <div class="input-group">
            <label>集合字符串(B)：</label>
            <el-input
              v-model="baseSet"
              placeholder="例如：1, 2, 3, 4, 5"
              class="input-field"
            />
          </div>
        </el-col>
        <el-col :span="12">
          <div class="input-group">
            <label>排列长度(L)：</label>
            <el-input-number
              v-model="length"
              :min="1"
              :max="20"
              placeholder="排列长度"
              class="input-number"
            />
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 1rem;">
        <el-col :span="12">
          <div class="input-group">
            <label>排列起始(S)：</label>
            <el-input
              v-model="startString"
              placeholder="起始排列（可选）"
              class="input-field"
            />
          </div>
        </el-col>
        <el-col :span="12">
          <div class="input-group">
            <label>生成个数(N)：</label>
            <el-input-number
              v-model="number"
              :min="1"
              :max="100"
              placeholder="生成个数"
              class="input-number"
            />
          </div>
        </el-col>
      </el-row>

      <div class="input-hint">
        <el-text type="info" size="small">
          集合格式：element1, element2, element3（用逗号分隔）
          <br>
          排列长度不能超过集合元素数量
          <br>
          起始字符串为空时从第一个排列开始生成
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
        <el-divider content-position="left">排列生成分析结果</el-divider>
        <div class="results-content">
          <div v-for="(result, index) in results" :key="index" class="result-item">
            <!-- 基本信息 -->
            <div class="result-basic">
              <h4>生成参数：</h4>
              <math-renderer
                :formula="result.formula"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
              <div class="parameter-info">
                <p><strong>集合 B：</strong> {{ result.baseSet }}</p>
                <p><strong>排列长度：</strong> {{ result.length }}</p>
                <p><strong>起始排列：</strong> {{ result.startString || '从第一个开始' }}</p>
                <p><strong>生成个数：</strong> {{ result.number }}</p>
                <p><strong>总排列数：</strong> {{ result.totalCount }}</p>
              </div>
            </div>

            <!-- 生成结果 -->
            <div v-if="result.permutations && result.permutations.length > 0" class="permutations-result">
              <h4>生成的排列序列：</h4>
              <div class="permutations-grid">
                <div
                  v-for="(perm, permIndex) in result.permutations"
                  :key="permIndex"
                  class="permutation-item"
                >
                  <math-renderer
                    :formula="perm"
                    :type="'mathjax'"
                    :display-mode="false"
                    class="permutation-formula"
                  />
                  <span v-if="permIndex < result.permutations.length - 1" class="arrow">→</span>
                </div>
                <div v-if="result.permutations.length >= result.number" class="continuation">
                  <span>··· ···</span>
                </div>
              </div>
            </div>

            <!-- 警告信息 -->
            <div v-if="!result.startStringFound" class="warning-section">
              <el-alert
                title="起始字符串警告"
                :description="'要生成的起始字符串 ' + result.startString + ' 不在生成的排列之中！'"
                type="warning"
                :closable="false"
                show-icon
              />
            </div>

            <!-- 统计信息 -->
            <div class="statistics-section">
              <h4>统计信息：</h4>
              <p>{{ result.message }}</p>
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
const baseSet = ref('')
const length = ref(3)
const startString = ref('')
const number = ref(20)
const feedback = ref([])
const results = ref([])
const counter = ref(0)

// 事件处理函数
const emit = defineEmits(['close', 'gen-permutation-result'])

const startGeneration = async () => {
  if (!baseSet.value.trim()) {
    ElMessage.warning('请输入集合字符串')
    return
  }

  if (!length.value || length.value <= 0) {
    ElMessage.warning('请输入有效的排列长度')
    return
  }

  // 清空之前的反馈和结果
  feedback.value = []
  results.value = []

  try {
    const request = {
      baseSet: baseSet.value.trim(),
      length: length.value,
      startString: startString.value.trim(),
      number: number.value
    }

    console.log('GenPermutationInterface: 开始生成排列', request)

    const response = await callBackendApi('/gen-permutation/generate', {
      method: 'POST',
      body: JSON.stringify(request)
    })

    console.log('GenPermutationInterface: 收到生成结果', response)

    if (response.success) {
      const result = {
        ...response,
        index: counter.value + 1
      }

      results.value.push(result)
      counter.value++

      // 添加成功反馈
      feedback.value.push({
        formula: `B = ${response.baseSet}, n = ${response.length}, P(${response.baseSet?.match(/\{([^}]+)\}/)?.[1]?.split(',').length || 0}, ${response.length}) = ${response.totalCount}`,
        type: 'success',
        message: '排列生成完成'
      })

      // 发送结果到主界面
      emit('gen-permutation-result', result)

      ElMessage.success('排列生成完成')
    } else {
      feedback.value.push({
        formula: 'B = ?, n = ?',
        type: 'error',
        message: response.errorMessage || '排列生成失败'
      })
    }
  } catch (error) {
    console.error('排列生成失败:', error)
    feedback.value.push({
      formula: 'B = ?, n = ?',
      type: 'error',
      message: `生成失败: ${error.message}`
    })
  }
}

const checkInput = async () => {
  if (!baseSet.value.trim()) {
    ElMessage.warning('请输入集合字符串')
    return
  }

  feedback.value = []

  try {
    const response = await callBackendApi('/gen-permutation/validate', {
      method: 'POST',
      body: JSON.stringify({
        baseSet: baseSet.value.trim(),
        length: length.value.toString(),
        startString: startString.value.trim(),
        number: number.value.toString()
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

const clearInput = () => {
  baseSet.value = ''
  length.value = 3
  startString.value = ''
  number.value = 20
  ElMessage.info('已清空输入')
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

const loadExample = async () => {
  try {
    console.log('GenPermutationInterface: 加载示例')

    const response = await callBackendApi('/gen-permutation/example', {
      method: 'GET'
    })

    console.log('GenPermutationInterface: 示例数据', response)

    if (response.baseSet) {
      baseSet.value = response.baseSet
      length.value = response.length
      startString.value = response.startString
      number.value = response.number

      // 清除之前的结果
      feedback.value = []
      results.value = []

      ElMessage.success('已加载示例：例题8.38')
    } else {
      ElMessage.error('加载示例失败')
    }
  } catch (error) {
    console.error('加载示例失败:', error)
    ElMessage.error(`加载示例失败: ${error.message}`)
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
.gen-permutation-interface {
  display: flex;
  flex-direction: column;
  width: 100%;
}

/* 在大屏幕上完全自适应，无最大宽度限制 */
@media (min-width: 1200px) {
  .gen-permutation-interface {
    width: 100%;
    max-width: none;
  }
}

/* 在中等屏幕上设置合理的最大宽度 */
@media (max-width: 1199px) and (min-width: 900px) {
  .gen-permutation-interface {
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
  }
}

/* 在小屏幕上固定宽度，支持水平滚动 */
@media (max-width: 899px) {
  .gen-permutation-interface {
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

.input-field {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
}

.input-number {
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

.parameter-info {
  margin-top: 1rem;
  padding: 1rem;
  background: white;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.parameter-info p {
  margin: 0.5rem 0;
  font-size: 0.9rem;
}

.permutations-result {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.permutations-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  align-items: center;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 4px;
  overflow-x: auto;
}

.permutation-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.permutation-formula {
  font-size: 1rem;
  padding: 0.25rem 0.5rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
}

.arrow {
  font-size: 1.2rem;
  color: #6b7280;
  font-weight: bold;
}

.continuation {
  font-size: 1.2rem;
  color: #6b7280;
  font-weight: bold;
  padding: 0.5rem;
}

.warning-section {
  margin: 1.5rem 0;
}

.statistics-section {
  margin: 1.5rem 0;
  padding: 1rem;
  background: white;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.statistics-section p {
  margin: 0.5rem 0;
  font-size: 0.9rem;
  line-height: 1.5;
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

  .permutations-grid {
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

  .permutations-grid {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>