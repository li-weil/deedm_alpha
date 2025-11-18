<template>
  <div class="bool-algebra-interface">
    <!-- 按钮操作区域 -->
    <div class="button-section">
      <!-- 第一行按钮：开始展示、生成随机、清除输入、合法性检查 -->
      <el-row :gutter="20" class="button-row">
        <el-col :span="4">
          <el-button type="primary" @click="startAnalysis">
            <el-icon><Check /></el-icon>
            开始展示(Y)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="success" @click="generateRandom">
            <el-icon><MagicStick /></el-icon>
            生成随机(G)
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
          <el-button size="small" @click="loadExample('example10_50_1')">例题10.50(1)</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('example10_50_2')">例题10.50(2)</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('example10_50_3')">例题10.50(3)</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('example10_50_4')">例题10.50(4)</el-button>
        </el-col>
      </el-row>

      <!-- 选项设置 -->
      <el-divider content-position="left">选择要展示的信息（注意：如果元素个数大于等于5，展示哈斯图可能比较耗时）</el-divider>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-checkbox v-model="options.showHasseDiagram" size="large">
            画出偏序的哈斯图
          </el-checkbox>
        </el-col>
        <el-col :span="6">
          <el-checkbox v-model="options.showOperationTables" size="large">
            画出运算表
          </el-checkbox>
        </el-col>
        <el-col :span="6">
          <el-checkbox v-model="options.showComplements" size="large">
            画出补元
          </el-checkbox>
        </el-col>
      </el-row>
    </div>

    <!-- 输入区域 -->
    <div class="input-section">
      <el-divider content-position="left">输入一个大于等于6的正整数m，展示以m的正因子集合为基集，整除关系为偏序构成的格F(m)</el-divider>
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="input-group">
            <label>整数(m)：</label>
            <el-input
              v-model="mInput"
              placeholder="例如：6"
              class="m-input"
              type="number"
              :min="6"
              :max="1000"
            />
          </div>
        </el-col>
        <el-col :span="12">
          <div class="input-hint">
            <el-text type="info" size="small">
              输入要求：m ≥ 6，为了确保性能，建议 m ≤ 1000，m的正因子数不超过30个。
            </el-text>
          </div>
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

      <!-- 分析结果区域 -->
      <div v-if="results.length > 0" class="results-section">
        <el-divider content-position="left">整除与布尔代数分析结果</el-divider>
        <div class="results-content">
          <div v-for="(result, index) in results" :key="index" class="result-item">

            <!-- 格的基本信息 -->
            <div class="result-basic">
              <h4>{{ result.index }}: 格F({{ result.m }})的基本信息：</h4>
              <math-renderer
                :formula="result.latticeDescription"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
              <div class="boolean-algebra-judgment">
                <el-tag :type="result.booleanAlgebra ? 'success' : 'warning'" size="large">
                  {{ result.booleanAlgebra ? '该格是布尔代数' : '该格不是布尔代数' }}
                </el-tag>
              </div>
            </div>

            <!-- 最大元和最小元 -->
            <div v-if="result.greatestElement && result.leastElement" class="elements-info">
              <h4>极值元素：</h4>
              <div class="element-item">
                <span>最大元： </span>
                <math-renderer
                  :formula="result.greatestElement"
                  :type="'mathjax'"
                  :display-mode="false"
                  class="element-formula"
                />
                <span>，最小元： </span>
                <math-renderer
                  :formula="result.leastElement"
                  :type="'mathjax'"
                  :display-mode="false"
                  class="element-formula"
                />
              </div>
            </div>

            <!-- 哈斯图 -->
            <div v-if="result.hasseDiagramUrl" class="hasse-diagram">
              <h4>哈斯图：</h4>
              <div class="diagram-container">
                <img
                  :src="result.hasseDiagramUrl"
                  alt="哈斯图"
                  class="hasse-image"
                  @error="onImageError"
                />
              </div>
            </div>

            <!-- 运算表 -->
            <div v-if="result.supOperatorTable && result.subOperatorTable" class="operation-tables">
              <h4>运算表（上确界（最小上界）和下确界（最大下界）运算符）：</h4>
              <div class="operation-item">
                <h5>上确界运算：</h5>
                <math-renderer
                  :formula="result.supOperatorTable"
                  :type="'katex'"
                  :display-mode="true"
                  class="operation-formula"
                />
              </div>
              <div class="operation-item">
                <h5>下确界运算：</h5>
                <math-renderer
                  :formula="result.subOperatorTable"
                  :type="'katex'"
                  :display-mode="true"
                  class="operation-formula"
                />
              </div>
            </div>

            <!-- 补元信息 -->
            <div v-if="result.complementInfos && result.complementInfos.length > 0" class="complements">
              <h4>补元信息：</h4>
              <div v-for="(compInfo, compIndex) in result.complementInfos" :key="compIndex" class="complement-item">
                <div class="element-label">
                  元素
                  <math-renderer
                    :formula="compInfo.element"
                    :type="'mathjax'"
                    :display-mode="false"
                    class="element-formula"
                  />
                  <span v-if="compInfo.hasComplement"> 的补元是：</span>
                  <span v-else> 没有补元</span>
                </div>
                <div v-if="compInfo.hasComplement" class="complement-value">
                  <math-renderer
                    :formula="compInfo.complements"
                    :type="'mathjax'"
                    :display-mode="true"
                    class="complement-formula"
                  />
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
  MagicStick,
  Delete,
  WarningFilled,
  RefreshRight,
  Close
} from '@element-plus/icons-vue'
import MathRenderer from '@/components/common/MathRenderer.vue'

// 响应式数据
const mInput = ref('')
const feedback = ref([])
const results = ref([])
const counter = ref(0)

// 选项设置
const options = ref({
  showHasseDiagram: true,
  showOperationTables: true,
  showComplements: true
})

// 示例数据
const examples = {
  example10_50_1: {
    m: 110,
    options: { showHasseDiagram: true, showOperationTables: true, showComplements: true }
  },
  example10_50_2: {
    m: 6,
    options: { showHasseDiagram: true, showOperationTables: true, showComplements: true }
  },
  example10_50_3: {
    m: 12,
    options: { showHasseDiagram: true, showOperationTables: true, showComplements: true }
  },
  example10_50_4: {
    m: 24,
    options: { showHasseDiagram: true, showOperationTables: true, showComplements: true }
  }
}

// 事件处理函数
const emit = defineEmits(['close', 'bool-algebra-result'])

const startAnalysis = async () => {
  if (!mInput.value.trim()) {
    ElMessage.warning('请输入正整数m')
    return
  }

  // 清空之前的反馈和结果
  feedback.value = []
  results.value = []

  try {
    const request = {
      m: parseInt(mInput.value.trim()),
      ...options.value
    }

    console.log('BoolAlgebraInterface: 开始布尔代数分析', request)

    const response = await callBackendApi('/bool-algebra/analyze', {
      method: 'POST',
      body: JSON.stringify(request)
    })

    console.log('BoolAlgebraInterface: 收到分析结果', response)

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
        message: '布尔代数分析完成'
      })

      // 发送结果到主界面
      emit('bool-algebra-result', result)

      ElMessage.success('布尔代数分析完成')
    } else {
      feedback.value.push({
        formula: `F(${request.m}) = ?`,
        type: 'error',
        message: response.errorMessage || '布尔代数分析失败'
      })
    }
  } catch (error) {
    console.error('布尔代数分析失败:', error)
    feedback.value.push({
      formula: `F(?) = ?`,
      type: 'error',
      message: `分析失败: ${error.message}`
    })
  }
}

const generateRandom = async () => {
  try {
    console.log('BoolAlgebraInterface: 开始生成随机输入')

    const response = await callBackendApi('/bool-algebra/generate', {
      method: 'GET'
    })

    console.log('BoolAlgebraInterface: 随机输入生成结果', response)

    if (response.success) {
      mInput.value = response.m.toString()

      // 设置选项
      Object.assign(options.value, response.options)

      // 清除之前的结果
      feedback.value = []
      results.value = []

      ElMessage.success('已生成随机输入')
    } else {
      ElMessage.error('生成随机输入失败: ' + response.message)
    }
  } catch (error) {
    console.error('生成随机输入失败:', error)
    ElMessage.error(`生成随机输入失败: ${error.message}`)
  }
}

const clearInput = () => {
  mInput.value = ''
  ElMessage.info('已清空输入')
}

const checkInput = async () => {
  if (!mInput.value.trim()) {
    ElMessage.warning('请输入正整数m')
    return
  }

  feedback.value = []

  try {
    const response = await callBackendApi('/bool-algebra/validate', {
      method: 'POST',
      body: JSON.stringify({
        m: mInput.value.trim()
      })
    })

    if (response.valid) {
      feedback.value.push({
        formula: `m = ${mInput.value}`,
        type: 'success',
        message: response.message
      })
      ElMessage.success('输入格式正确')
    } else {
      feedback.value.push({
        formula: `m = ${mInput.value}`,
        type: 'error',
        message: response.message
      })
      ElMessage.error('输入格式不正确')
    }
  } catch (error) {
    console.error('检查输入失败:', error)
    feedback.value.push({
      formula: `m = ${mInput.value}`,
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
    mInput.value = example.m.toString()

    // 设置选项
    Object.assign(options.value, example.options)

    console.log('BoolAlgebraInterface: 加载示例', exampleKey, example)
    ElMessage.info(`已加载示例：${exampleKey}`)
  }
}

const onImageError = (event) => {
  console.error('哈斯图加载失败:', event)
  event.target.style.display = 'none'
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
.bool-algebra-interface {
  display: flex;
  flex-direction: column;
  width: 100%;
}

/* 在大屏幕上完全自适应，无最大宽度限制 */
@media (min-width: 1200px) {
  .bool-algebra-interface {
    width: 100%;
    max-width: none;
  }
}

/* 在中等屏幕上设置合理的最大宽度 */
@media (max-width: 1199px) and (min-width: 900px) {
  .bool-algebra-interface {
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
  }
}

/* 在小屏幕上固定宽度，支持水平滚动 */
@media (max-width: 899px) {
  .bool-algebra-interface {
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

.m-input {
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

.boolean-algebra-judgment {
  margin-top: 1rem;
}

.elements-info {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.element-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.element-formula {
  font-size: 1.1rem;
}

.hasse-diagram {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.diagram-container {
  text-align: center;
}

.hasse-image {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.operation-tables {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.operation-item {
  margin: 1rem 0;
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

.complements {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.complement-item {
  margin: 1rem 0;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.element-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-wrap: wrap;
  margin-bottom: 0.5rem;
}

.complement-value {
  margin-left: 1rem;
}

.complement-formula {
  font-size: 1rem;
  padding: 0.5rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #dee2e6;
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

  .operation-formula,
  .complement-formula {
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

  .element-item,
  .element-label {
    font-size: 0.9rem;
  }
}
</style>