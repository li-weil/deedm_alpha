<template>
  <div class="group-perm-interface">
    <!-- 按钮操作区域 -->
    <div class="button-section">
      <!-- 第一行按钮：开始展示、清除输入、清除结果、取消 -->
      <el-row :gutter="20" class="button-row">
        <el-col :span="4">
          <el-button type="primary" @click="startAnalysis">
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
          <el-button size="small" @click="loadExample('example10_36')">例题10.36</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('example10_40')">例题10.40</el-button>
        </el-col>
      </el-row>

      <!-- 输入参数设置 -->
      <el-divider content-position="left">置换群参数输入</el-divider>
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="input-group">
            <label>度数(n)：</label>
            <el-input
              v-model.number="mInput"
              placeholder="请输入2到5之间的整数"
              type="number"
              :min="2"
              :max="5"
              class="param-input"
            />
          </div>
        </el-col>
      </el-row>
      <div class="input-hint">
        <el-text type="info" size="small">
          度数n的范围：2 ≤ n ≤ 5
          <br>
          注意：度数大于5时群的阶数会过大，展示所有子群和陪集可能需要很长时间，可能导致程序卡死异常退出。
        </el-text>
      </div>

      <!-- 分析选项设置 -->
      <el-divider content-position="left">选择要展示的信息</el-divider>
      <el-row :gutter="20">
        <el-col :span="4">
          <el-checkbox v-model="options.showCycleGroup" size="large">
            是否循环群
          </el-checkbox>
        </el-col>
        <el-col :span="4">
          <el-checkbox v-model="options.showElementPower" size="large">
            群元素的幂
          </el-checkbox>
        </el-col>
        <el-col :span="4">
          <el-checkbox v-model="options.showElementOrder" size="large">
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
        <el-col :span="4">
          <el-checkbox v-model="options.showNormalSubgroups" size="large">
            正规子群及商群
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
        <el-divider content-position="left">置换群分析结果</el-divider>
        <div class="results-content">
          <div v-for="(result, index) in results" :key="index" class="result-item">
            <!-- 基本信息 -->
            <div class="result-basic">
              <h4>置换群基本信息：</h4>
              <math-renderer
                :formula="result.formula"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
            </div>

            <!-- 元素表格和运算表格 -->
            <div v-if="result.elementTable || result.operatorTable" class="operation-result">
              <h4>群元素和运算表：</h4>
              <div v-if="result.elementTable">
                <math-renderer
                  :formula="result.elementTable"
                  :type="'katex'"
                  :display-mode="true"
                  class="operation-formula"
                />
              </div>
              <div v-if="result.operatorTable">
                <math-renderer
                  :formula="result.operatorTable"
                  :type="'katex'"
                  :display-mode="true"
                  class="operation-formula"
                />
              </div>
            </div>

            <!-- 循环群分析 -->
            <div v-if="result.showCycleGroup !== undefined" class="operation-result">
              <h4>循环群分析：</h4>
              <div v-if="result.isCycleGroup">
                <math-renderer
                  :formula="'置换群 S(' + result.m + ') 是循环群，生成元为：' + result.generators"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="operation-formula"
                />
              </div>
              <div v-else>
                <math-renderer
                  :formula="'置换群 S(' + result.m + ') 不是循环群'"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="operation-formula"
                />
              </div>
            </div>

            <!-- 群元素的幂 -->
            <div v-if="result.elementPowers && result.elementPowers.length > 0" class="operation-result">
              <h4>群元素的幂（包括群元素的逆）：</h4>
              <div v-for="(power, idx) in result.elementPowers" :key="idx" class="power-item">
                <math-renderer
                  :formula="power"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="operation-formula"
                />
              </div>
            </div>

            <!-- 群元素的阶 -->
            <div v-if="result.elementOrders && result.elementOrders.length > 0" class="operation-result">
              <h4>群元素的阶：</h4>
              <div class="orders-container">
                <math-renderer
                  :formula="result.elementOrders.join('')"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="operation-formula"
                />
              </div>
            </div>

            <!-- 子群分析 -->
            <div v-if="result.subgroups && result.subgroups.length > 0" class="operation-result">
              <h4>群的所有非平凡子群：</h4>
              <div v-for="(subgroup, idx) in result.subgroups" :key="idx" class="subgroup-item">
                <h5>子群 {{ idx + 1 }}：</h5>
                <math-renderer
                  :formula="'\\{' + subgroup.subgroupElements + '\\}'"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="operation-formula"
                />
                <div v-if="subgroup.cycleSubgroup">
                  <math-renderer
                    :formula="'是循环子群，生成元为：' + subgroup.generators"
                    :type="'mathjax'"
                    :display-mode="true"
                    class="operation-formula"
                  />
                </div>
                <div v-else>
                  <math-renderer
                    :formula="'不是循环子群'"
                    :type="'mathjax'"
                    :display-mode="true"
                    class="operation-formula"
                  />
                </div>
                <div v-if="subgroup.operatorTable">
                  <math-renderer
                    :formula="subgroup.operatorTable"
                    :type="'katex'"
                    :display-mode="true"
                    class="operation-formula"
                  />
                </div>
              </div>
            </div>

            <!-- 陪集分析 -->
            <div v-if="result.cosets && result.cosets.length > 0" class="operation-result">
              <h4>群的所有非平凡子群的陪集：</h4>
              <div v-for="(coset, idx) in result.cosets" :key="idx" class="coset-item">
                <h5>
                  <span>子群 {{ idx + 1 }}：</span>
                  <math-renderer
                    :formula="'\\{' + coset.subgroupElements + '\\}'"
                    :type="'mathjax'"
                    :display-mode="false"
                    class="inline-formula"
                  />
                  <span>的陪集：</span>
                </h5>
                <div class="coset-section">
                  <h6>左陪集包括：</h6>
                  <math-renderer
                    :formula="coset.leftCosets.join('')"
                    :type="'mathjax'"
                    :display-mode="true"
                    class="operation-formula"
                  />
                </div>
                <div class="coset-section">
                  <h6>右陪集包括：</h6>
                  <math-renderer
                    :formula="coset.rightCosets.join('')"
                    :type="'mathjax'"
                    :display-mode="true"
                    class="operation-formula"
                  />
                </div>
              </div>
            </div>

            <!-- 正规子群分析 -->
            <div v-if="result.normalSubgroups && result.normalSubgroups.length > 0" class="operation-result">
              <h4>群的正规子群分析：</h4>
              <div v-for="(normal, idx) in result.normalSubgroups" :key="idx" class="normal-item">
                <h5>
                  <span>子群 {{ idx + 1 }}：</span>
                  <math-renderer
                    :formula="'\\{' + normal.subgroupElements + '\\}'"
                    :type="'mathjax'"
                    :display-mode="false"
                    class="inline-formula"
                  />
                </h5>
                <div v-if="normal.normal">
                  <h6>是正规子群，其商群的运算表如下：</h6>
                  <math-renderer
                    :formula="normal.quotientGroupTable"
                    :type="'katex'"
                    :display-mode="true"
                    class="operation-formula"
                  />
                </div>
                <div v-else>
                  <h6>不是正规子群</h6>
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
  Close
} from '@element-plus/icons-vue'
import MathRenderer from '@/components/common/MathRenderer.vue'

// 响应式数据
const mInput = ref(3)
const feedback = ref([])
const results = ref([])
const counter = ref(0)

// 分析选项
const options = ref({
  showCycleGroup: true,
  showElementPower: true,
  showElementOrder: true,
  showSubgroups: true,
  showCosets: true,
  showNormalSubgroups: false
})

// 事件处理函数
const emit = defineEmits(['close', 'group-perm-result'])

const startAnalysis = async () => {
  if (!mInput.value || mInput.value < 2 || mInput.value > 5) {
    ElMessage.warning('请输入2到5之间的整数作为度数')
    return
  }

  // 检查是否至少选择了一个分析选项
  const hasSelectedOption = Object.values(options.value).some(option => option)
  if (!hasSelectedOption) {
    ElMessage.warning('请至少选择一种分析选项')
    return
  }

  // 清空之前的反馈和结果
  feedback.value = []
  results.value = []
  

  try {
    const request = {
      m: mInput.value,
      ...options.value
    }

    console.log('GroupPermInterface: 开始置换群分析', request)

    const response = await callBackendApi('/group-perm/analyze', {
      method: 'POST',
      body: request
    })

    console.log('GroupPermInterface: 收到分析结果', response)

    if (response.success) {
      // 添加调试输出
      console.log('=== 前端调试：子群数据 ===')
      console.log('子群数量:', response.subgroups?.length || 0)
      if (response.subgroups) {
        response.subgroups.forEach((subgroup, idx) => {
          console.log(`子群${idx+1}: {${subgroup.subgroupElements}}, 是否循环: ${subgroup.cycleSubgroup}, 生成元: ${subgroup.generators}`)
        })
      }

      console.log('=== 前端调试：正规子群数据 ===')
      console.log('正规子群数量:', response.normalSubgroups?.length || 0)
      if (response.normalSubgroups) {
        response.normalSubgroups.forEach((normal, idx) => {
          console.log(`正规子群${idx+1}: {${normal.subgroupElements}}, 是否正规: ${normal.normal}`)
        })
      }

      const result = {
        ...response,
        m: mInput.value,
        index: counter.value + 1,
        showCycleGroup: options.value.showCycleGroup,
        showElementPower: options.value.showElementPower,
        showElementOrder: options.value.showElementOrder,
        showSubgroups: options.value.showSubgroups,
        showCosets: options.value.showCosets,
        showNormalSubgroups: options.value.showNormalSubgroups
      }

      results.value.push(result)
      counter.value++

      // 添加成功反馈
      feedback.value.push({
        formula: response.formula,
        type: 'success',
        message: '置换群分析完成'
      })

      // 发送结果到主界面
      emit('group-perm-result', result)

      ElMessage.success('置换群分析完成')
    } else {
      feedback.value.push({
        formula: 'S(' + mInput.value + ')',
        type: 'error',
        message: response.errorMessage || '置换群分析失败'
      })
    }
  } catch (error) {
    console.error('置换群分析失败:', error)
    feedback.value.push({
      formula: 'S(' + mInput.value + ')',
      type: 'error',
      message: `分析失败: ${error.message}`
    })
  }
}

const clearInput = () => {
  mInput.value = 3
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

const loadExample = (exampleKey) => {
  const examples = {
    example10_36: {
      m: 3,
      showCycleGroup: true,
      showElementPower: true,
      showElementOrder: true,
      showSubgroups: true,
      showCosets: true,
      showNormalSubgroups: false
    },
    example10_40: {
      m: 3,
      showCycleGroup: false,
      showElementPower: false,
      showElementOrder: false,
      showSubgroups: false,
      showCosets: true,
      showNormalSubgroups: true
    }
  }

  if (examples[exampleKey]) {
    const example = examples[exampleKey]

    // 清除之前的结果
    results.value = []
    feedback.value = []
    counter.value = 0

    // 设置示例数据
    mInput.value = example.m
    Object.assign(options.value, example)

    console.log('GroupPermInterface: 加载示例', exampleKey, example)
    ElMessage.info(`已加载示例：${exampleKey}`)
  }
}

// 通用API调用函数
const callBackendApi = async (endpoint, options = {}) => {
  try {
    const baseUrl = window.location.origin

    const requestOptions = {
      method: options.method || 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...options.headers
      }
    }

    // 只有当body存在时才添加body
    if (options.body) {
      requestOptions.body = JSON.stringify(options.body)
    }

    console.log(`发送API请求: ${baseUrl}/api${endpoint}`, requestOptions)

    const response = await fetch(`${baseUrl}/api${endpoint}`, requestOptions)

    console.log(`收到API响应: status=${response.status}, ok=${response.ok}`)

    // 检查响应是否为空
    const responseText = await response.text()
    console.log(`响应文本内容:`, responseText)

    if (!responseText.trim()) {
      throw new Error('服务器返回空响应')
    }

    // 尝试解析JSON
    let result
    try {
      result = JSON.parse(responseText)
    } catch (parseError) {
      console.error('JSON解析失败:', parseError, '响应内容:', responseText)
      throw new Error(`服务器响应格式错误: ${parseError.message}`)
    }

    if (!response.ok) {
      throw new Error(result.message || result.errorMessage || `HTTP error! status: ${response.status}`)
    }

    return result
  } catch (error) {
    console.error(`API调用失败 (${endpoint}):`, error)
    throw error
  }
}
</script>

<style scoped>
.group-perm-interface {
  display: flex;
  flex-direction: column;
  width: 100%;
}

/* 在大屏幕上完全自适应，无最大宽度限制 */
@media (min-width: 1200px) {
  .group-perm-interface {
    width: 100%;
    max-width: none;
  }
}

/* 在中等屏幕上设置合理的最大宽度 */
@media (max-width: 1199px) and (min-width: 900px) {
  .group-perm-interface {
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
  }
}

/* 在小屏幕上固定宽度，支持水平滚动 */
@media (max-width: 899px) {
  .group-perm-interface {
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
  background: #ffffff;
  border-radius: 4px;
  border: 1px solid #f1f1f1;
  overflow-x: auto;
}

.power-item,
.subgroup-item,
.coset-item,
.normal-item {
  margin: 1rem 0;
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 6px;
  border: 1px solid #e9ecef;
}

.coset-section {
  margin: 1rem 0;
}

.orders-container {
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

h6 {
  color: #9ca3af;
  margin: 0.8rem 0 0.3rem 0;
  font-size: 0.9rem;
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

/* 内联公式样式 - 与文字在同一行显示 */
.inline-formula {
  display: inline;
  vertical-align: baseline;
  margin: 0 0.2rem;
  font-size: inherit;
  line-height: 1;
}

/* 针对 h5 和 h6 标签中的内联公式进行特殊对齐 */
h5 .inline-formula,
h6 .inline-formula {
  vertical-align: baseline;
  margin: 0 0.15rem;
}

/* h5标签中的所有元素统一对齐 */
h5 span,
h5 .inline-formula {
  display: inline;
  vertical-align: baseline;
}

h6 span,
h6 .inline-formula {
  display: inline;
  vertical-align: baseline;
}

/* 深度修复MathJax内联公式的对齐问题 */
.inline-formula :deep(.MathJax) {
  vertical-align: baseline !important;
  display: inline !important;
  margin: 0 !important;
}

.inline-formula :deep(.MathJax > mjx-container) {
  vertical-align: baseline !important;
  display: inline !important;
  margin: 0 !important;
  font-size: inherit !important;
}

.inline-formula :deep(.MathJax > mjx-container > svg) {
  vertical-align: baseline !important;
  display: inline !important;
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