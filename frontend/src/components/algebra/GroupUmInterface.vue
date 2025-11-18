<template>
  <div class="group-um-interface">
    <!-- 按钮操作区域 -->
    <div class="button-section">
      <!-- 第一行按钮：开始展示、生成随机参数、清除结果、退出 -->
      <el-row :gutter="20" class="button-row">
        <el-col :span="4">
          <el-button type="primary" @click="startAnalysis">
            <el-icon><Check /></el-icon>
            开始展示(Y)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="success" @click="generateRandomParams">
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
            退出(R)
          </el-button>
        </el-col>
      </el-row>

      <!-- 第二行按钮：示例题 -->
      <el-divider content-position="left">《离散数学基础》教材示例展示</el-divider>
      <el-row :gutter="15" class="example-buttons">
        <el-col :span="4">
          <el-button size="small" @click="loadExample('example10_28_1')">例题10.28(1)</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('example10_28_2')">例题10.28(2)</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('example10_31')">例题10.31</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('example10_32')">例题10.32</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('example10_35')">例题10.35</el-button>
        </el-col>
      </el-row>

      <!-- 参数输入区域 -->
      <el-divider content-position="left">参数输入</el-divider>
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="input-group">
            <label>输入模数(m)：</label>
            <el-input
              v-model.number="inputM"
              type="number"
              class="param-input"
              :min="1"
              :max="30"
            />
          </div>
        </el-col>
      </el-row>
      <div class="input-hint">
        <el-text type="info" size="small">
          请输入一个大于等于3的整数m，建议不超过30（过大可能需要很长计算时间）
        </el-text>
      </div>

      <!-- 显示选项设置 -->
      <el-divider content-position="left">选择要展示的信息（注意：若群U(m)的元素超过30个，展示子群及陪集可能需要很长时间）</el-divider>
      <el-row :gutter="20">
        <el-col :span="4">
          <el-checkbox v-model="options.showCycleGroup" size="large">
            是否循环群
          </el-checkbox>
        </el-col>
        <el-col :span="4">
          <el-checkbox v-model="options.showPower" size="large">
            群元素的幂
          </el-checkbox>
        </el-col>
        <el-col :span="4">
          <el-checkbox v-model="options.showOrder" size="large">
            群元素的阶
          </el-checkbox>
        </el-col>
        <el-col :span="4">
          <el-checkbox v-model="options.showSubgroups" size="large">
            所有非平凡子群
          </el-checkbox>
        </el-col>
        <el-col :span="4">
          <el-checkbox v-model="options.showCosets" size="large">
            非平凡子群的陪集
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

      <!-- 分析结果区域 -->
      <div v-if="results.length > 0" class="results-section">
        <el-divider content-position="left">群U(m)分析结果</el-divider>
        <div class="results-content">
          <div v-for="(result, index) in results" :key="index" class="result-item">
            <!-- 基本信息 -->
            <div class="result-basic">
              <h4>群的基本信息：</h4>
              <math-renderer
                :formula="result.formula"
                :type="'katex'"
                :display-mode="true"
                class="result-formula"
              />
            </div>

            <!-- U(m)运算表 -->
            <div v-if="result.operatorTable" class="operator-table-result">
              <h4>群U({{ result.m }})的运算表：</h4>
              <div class="operator-table-container">
                <math-renderer
                  :formula="result.operatorTable"
                  :type="'katex'"
                  :display-mode="true"
                  class="operator-formula"
                />
              </div>
            </div>

            <!-- 循环群信息 -->
            <div v-if="result.showCycleGroup" class="cycle-group-result">
              <h4>循环群分析：</h4>
              <div v-if="result.cycleGroup">
                <p>群 U({{ result.m }}) 是循环群，生成元：{{ result.generators }}</p>
              </div>
              <div v-else>
                <p>群 U({{ result.m }}) 不是循环群</p>
              </div>
            </div>

            <!-- 群元素的幂 -->
            <div v-if="result.showPower && result.elementPowers" class="power-result">
              <h4>群元素的幂：</h4>
              <div v-for="(powerInfo, idx) in result.elementPowers" :key="idx" class="power-item">
                <p><strong>元素 {{ powerInfo.element }} 的幂：</strong></p>
                <div class="power-list">
                  <math-renderer
                    v-for="(power, powerIdx) in powerInfo.powers"
                    :key="powerIdx"
                    :formula="power"
                    :type="'mathjax'"
                    :display-mode="false"
                    class="power-formula-inline"
                  />
                </div>
              </div>
            </div>

            <!-- 群元素的阶 -->
            <div v-if="result.showOrder && result.elementOrders" class="order-result">
              <h4>群元素的阶：</h4>
              <div class="order-grid">
                <math-renderer
                  v-for="(orderInfo, idx) in result.elementOrders"
                  :key="idx"
                  :formula="orderInfo.formula"
                  :type="'mathjax'"
                  :display-mode="false"
                  class="order-formula"
                />
              </div>
            </div>

            <!-- 子群信息 -->
            <div v-if="result.showSubgroups && result.subgroups" class="subgroup-result">
              <h4>群的非平凡子群：</h4>
              <div v-for="(subgroup, idx) in result.subgroups" :key="idx" class="subgroup-item">
                <div class="subgroup-info">
                  <p><strong>子群：</strong> { {{ subgroup.elements }} }</p>
                  <div v-if="subgroup.isCycleSubgroup">
                    <p>它是循环子群，生成元：{{ subgroup.generators }}</p>
                  </div>
                  <div v-else>
                    <p>它不是循环子群</p>
                  </div>
                  <div class="subgroup-operator">
                    <math-renderer
                      :formula="subgroup.operatorTable"
                      :type="'katex'"
                      :display-mode="true"
                      class="operator-formula"
                    />
                  </div>
                </div>

                <!-- 陪集信息 -->
                <div v-if="result.showCosets && subgroup.cosets && subgroup.cosets.length > 0" class="coset-info">
                  <p><strong>子群 { {{ subgroup.elements }} } 的陪集包括：</strong></p>
                  <div class="coset-grid">
                    <math-renderer
                      v-for="(coset, cosetIdx) in subgroup.cosets"
                      :key="cosetIdx"
                      :formula="coset"
                      :type="'mathjax'"
                      :display-mode="false"
                      class="coset-formula"
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
  MagicStick,
  Delete,
  WarningFilled,
  RefreshRight,
  Close
} from '@element-plus/icons-vue'
import MathRenderer from '@/components/common/MathRenderer.vue'

// 响应式数据
const inputM = ref('')
const feedback = ref([])
const results = ref([])
const counter = ref(0)

// 显示选项
const options = ref({
  showCycleGroup: true,
  showPower: true,
  showOrder: true,
  showSubgroups: true,
  showCosets: true
})

// 示例数据
const examples = {
  example10_28_1: {
    m: 5,
    options: { showCycleGroup: true, showPower: true, showOrder: false, showSubgroups: false, showCosets: false }
  },
  example10_28_2: {
    m: 7,
    options: { showCycleGroup: true, showPower: true, showOrder: false, showSubgroups: false, showCosets: false }
  },
  example10_31: {
    m: 5,
    options: { showCycleGroup: false, showPower: true, showOrder: true, showSubgroups: false, showCosets: false }
  },
  example10_32: {
    m: 7,
    options: { showCycleGroup: false, showPower: false, showOrder: false, showSubgroups: true, showCosets: false }
  },
  example10_35: {
    m: 7,
    options: { showCycleGroup: false, showPower: false, showOrder: false, showSubgroups: true, showCosets: true }
  }
}

// 事件处理函数
const emit = defineEmits(['close', 'group-um-result'])

const startAnalysis = async () => {
  if (!inputM.value || inputM.value < 1) {
    ElMessage.warning('请输入一个有效的正整数m')
    return
  }

  // 检查是否至少选择了一个显示选项
  const hasSelectedOption = Object.values(options.value).some(option => option)
  if (!hasSelectedOption) {
    ElMessage.warning('请至少选择一种要展示的信息')
    return
  }

  // 清空之前的反馈和结果
  feedback.value = []
  results.value = []

  try {
    const request = {
      m: inputM.value,
      ...options.value
    }

    console.log('GroupUmInterface: 开始分析群U(m)', request)

    const response = await callBackendApi('/group-um/analyze', {
      method: 'POST',
      body: JSON.stringify(request)
    })

    console.log('GroupUmInterface: 收到分析结果', response)

    if (response.success) {
      const result = {
        ...response,
        index: counter.value + 1,
        ...options.value
      }

      results.value.push(result)
      counter.value++

      // 添加成功反馈
      feedback.value.push({
        formula: `U(${inputM.value})`,
        type: 'success',
        message: '群U(m)分析完成'
      })

      // 发送结果到主界面
      emit('group-um-result', result)

      ElMessage.success('群U(m)分析完成')
    } else {
      feedback.value.push({
        formula: `U(${inputM.value})`,
        type: 'error',
        message: response.errorMessage || '群U(m)分析失败'
      })
    }
  } catch (error) {
    console.error('群U(m)分析失败:', error)
    feedback.value.push({
      formula: `U(${inputM.value})`,
      type: 'error',
      message: `分析失败: ${error.message}`
    })
  }
}

const generateRandomParams = async () => {
  try {
    console.log('GroupUmInterface: 开始生成随机参数')

    const response = await callBackendApi('/group-um/generate', {
      method: 'GET'
    })

    console.log('GroupUmInterface: 随机参数生成结果', response)

    if (response.success) {
      inputM.value = response.m

      // 设置选项
      Object.assign(options.value, {
        showCycleGroup: response.showCycleGroup,
        showPower: response.showPower,
        showOrder: response.showOrder,
        showSubgroups: response.showSubgroups,
        showCosets: response.showCosets
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
  inputM.value = ''
  ElMessage.info('已清空输入')
}

const checkInput = async () => {
  if (!inputM.value || inputM.value < 1) {
    ElMessage.warning('请输入一个有效的正整数m')
    return
  }

  feedback.value = []

  try {
    const response = await callBackendApi('/group-um/validate', {
      method: 'POST',
      body: JSON.stringify({
        m: inputM.value
      })
    })

    if (response.valid) {
      feedback.value.push({
        formula: `m = ${inputM.value}`,
        type: 'success',
        message: response.message
      })
      ElMessage.success('输入参数正确')
    } else {
      feedback.value.push({
        formula: `m = ${inputM.value}`,
        type: 'error',
        message: response.message
      })
      ElMessage.error('输入参数不正确')
    }
  } catch (error) {
    console.error('检查输入失败:', error)
    feedback.value.push({
      formula: `m = ${inputM.value}`,
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
    inputM.value = example.m

    // 设置选项
    Object.assign(options.value, example.options)

    console.log('GroupUmInterface: 加载示例', exampleKey, example)
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
.group-um-interface {
  display: flex;
  flex-direction: column;
  width: 100%;
}

/* 在大屏幕上完全自适应，无最大宽度限制 */
@media (min-width: 1200px) {
  .group-um-interface {
    width: 100%;
    max-width: none;
  }
}

/* 在中等屏幕上设置合理的最大宽度 */
@media (max-width: 1199px) and (min-width: 900px) {
  .group-um-interface {
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
  }
}

/* 在小屏幕上固定宽度，支持水平滚动 */
@media (max-width: 899px) {
  .group-um-interface {
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

.operator-table-result,
.cycle-group-result,
.power-result,
.order-result,
.subgroup-result {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.power-item {
  margin: 1rem 0;
}

.power-formula {
  margin: 1rem 0;
  font-size: 1.1rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  overflow-x: auto;
}

.power-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin: 0.5rem 0;
}

.power-formula-inline {
  padding: 0.25rem 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  font-size: 0.9rem;
}

.operator-table-container {
  margin: 1rem 0;
  overflow-x: auto;
}

.operator-formula {
  font-size: 1rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  overflow-x: auto;
  min-width: max-content;
}

.order-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  margin: 1rem 0;
}

.order-formula {
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.subgroup-item {
  margin: 1.5rem 0;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.subgroup-info {
  margin-bottom: 1rem;
}

.operator-formula {
  margin: 1rem 0;
  font-size: 1rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  overflow-x: auto;
}

.coset-info {
  margin-top: 1rem;
  padding: 1rem;
  background: white;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.coset-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-top: 0.5rem;
}

.coset-item {
  padding: 0.25rem 0.5rem;
  background: #e9ecef;
  border-radius: 4px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 0.9rem;
}

.coset-formula {
  padding: 0.25rem 0.5rem;
  background: #e9ecef;
  border-radius: 4px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 0.9rem;
}

h4 {
  color: #374151;
  margin: 1rem 0 0.5rem 0;
  font-size: 1.1rem;
  font-weight: 600;
}

p {
  color: #6b7280;
  margin: 0.5rem 0;
  line-height: 1.5;
}

strong {
  color: #374151;
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

  .power-formula,
  .operator-formula {
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