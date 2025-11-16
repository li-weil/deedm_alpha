<template>
  <div class="count-equation-solver-interface">
    <!-- 按钮操作区域 -->
    <div class="button-section">
      <!-- 第一行按钮：开始运算、清除输入、合法性检查、清除结果、取消 -->
      <el-row :gutter="20" class="button-row">
        <el-col :span="4">
          <el-button type="primary" @click="startSolve">
            <el-icon><Check /></el-icon>
            开始求解(Y)
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
          <el-button size="small" @click="loadExample('example8_30')">例题8.30</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('example8_31')">例题8.31</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('example8_32')">例题8.32</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('example8_34')">例题8.34</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('example8_35')">例题8.35</el-button>
        </el-col>
      </el-row>

      <!-- 输出模式选择 -->
      <el-divider content-position="left">选择输出模式</el-divider>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-radio-group v-model="outputMode" size="large">
            <el-radio label="onlyResult">只显示结果数</el-radio>
            <el-radio label="onlyAccept">只显示符合条件的解</el-radio>
            <el-radio label="allSolver">显示所有解</el-radio>
          </el-radio-group>
        </el-col>
      </el-row>
    </div>

    <!-- 方程输入区域 -->
    <div class="equation-input-section">
      <el-divider content-position="left">不定方程的参数输入</el-divider>
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="input-group">
            <label>未知数个数(N)：</label>
            <el-input
              v-model.number="varNumber"
              placeholder="请输入未知数个数"
              type="number"
              min="1"
              class="number-input"
            />
          </div>
        </el-col>
        <el-col :span="12">
          <div class="input-group">
            <label>未知数总和(S)：</label>
            <el-input
              v-model.number="varSum"
              placeholder="请输入未知数总和"
              type="number"
              min="0"
              class="number-input"
            />
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 约束条件区域 -->
    <div class="constraints-section">
      <el-divider content-position="left">约束条件与变量范围限制</el-divider>

      <!-- 逻辑关系选择 -->
      <el-row :gutter="20" style="margin-bottom: 1rem;">
        <el-col :span="12">
          <label>各约束条件之间的逻辑关系：</label>
          <el-radio-group v-model="logicAnd" size="large">
            <el-radio :label="true">逻辑与关系(AND)</el-radio>
            <el-radio :label="false">逻辑或关系(OR)</el-radio>
          </el-radio-group>
        </el-col>
      </el-row>

      <!-- 约束条件组1 -->
      <div class="constraint-group">
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="input-row">
              <label>第一组约束条件 ①：</label>
              <div class="constraint-inputs">
                <el-input
                  v-model.number="index11"
                  placeholder="变量下标"
                  type="number"
                  min="1"
                  style="width: 100px; margin-right: 5px;"
                />
                <span style="margin: 0 5px;">变量 ≥</span>
                <el-input
                  v-model.number="min11"
                  placeholder="最小值(-1表示无限制)"
                  type="number"
                  style="width: 120px; margin-right: 5px;"
                />
                <span style="margin: 0 5px;">且 ≤</span>
                <el-input
                  v-model.number="max11"
                  placeholder="最大值(-1表示无限制)"
                  type="number"
                  style="width: 120px;"
                />
              </div>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="input-row">
              <label>  ②：</label>
              <div class="constraint-inputs">
                <el-input
                  v-model.number="index12"
                  placeholder="变量下标"
                  type="number"
                  min="1"
                  style="width: 100px; margin-right: 5px;"
                />
                <span style="margin: 0 5px;">变量 ≥</span>
                <el-input
                  v-model.number="min12"
                  placeholder="最小值(-1表示无限制)"
                  type="number"
                  style="width: 120px; margin-right: 5px;"
                />
                <span style="margin: 0 5px;">且 ≤</span>
                <el-input
                  v-model.number="max12"
                  placeholder="最大值(-1表示无限制)"
                  type="number"
                  style="width: 120px;"
                />
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 约束条件组2 -->
      <div class="constraint-group">
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="input-row">
              <label>第二组约束条件 ①：</label>
              <div class="constraint-inputs">
                <el-input
                  v-model.number="index21"
                  placeholder="变量下标"
                  type="number"
                  min="1"
                  style="width: 100px; margin-right: 5px;"
                />
                <span style="margin: 0 5px;">变量 ≥</span>
                <el-input
                  v-model.number="min21"
                  placeholder="最小值(-1表示无限制)"
                  type="number"
                  style="width: 120px; margin-right: 5px;"
                />
                <span style="margin: 0 5px;">且 ≤</span>
                <el-input
                  v-model.number="max21"
                  placeholder="最大值(-1表示无限制)"
                  type="number"
                  style="width: 120px;"
                />
              </div>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="input-row">
              <label>  ②：</label>
              <div class="constraint-inputs">
                <el-input
                  v-model.number="index22"
                  placeholder="变量下标"
                  type="number"
                  min="1"
                  style="width: 100px; margin-right: 5px;"
                />
                <span style="margin: 0 5px;">变量 ≥</span>
                <el-input
                  v-model.number="min22"
                  placeholder="最小值(-1表示无限制)"
                  type="number"
                  style="width: 120px; margin-right: 5px;"
                />
                <span style="margin: 0 5px;">且 ≤</span>
                <el-input
                  v-model.number="max22"
                  placeholder="最大值(-1表示无限制)"
                  type="number"
                  style="width: 120px;"
                />
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 约束条件组3 -->
      <div class="constraint-group">
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="input-row">
              <label>第三组约束条件 ①：</label>
              <div class="constraint-inputs">
                <el-input
                  v-model.number="index31"
                  placeholder="变量下标"
                  type="number"
                  min="1"
                  style="width: 100px; margin-right: 5px;"
                />
                <span style="margin: 0 5px;">变量 ≥</span>
                <el-input
                  v-model.number="min31"
                  placeholder="最小值(-1表示无限制)"
                  type="number"
                  style="width: 120px; margin-right: 5px;"
                />
                <span style="margin: 0 5px;">且 ≤</span>
                <el-input
                  v-model.number="max31"
                  placeholder="最大值(-1表示无限制)"
                  type="number"
                  style="width: 120px;"
                />
              </div>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="input-row">
              <label>  ②：</label>
              <div class="constraint-inputs">
                <el-input
                  v-model.number="index32"
                  placeholder="变量下标"
                  type="number"
                  min="1"
                  style="width: 100px; margin-right: 5px;"
                />
                <span style="margin: 0 5px;">变量 ≥</span>
                <el-input
                  v-model.number="min32"
                  placeholder="最小值(-1表示无限制)"
                  type="number"
                  style="width: 120px; margin-right: 5px;"
                />
                <span style="margin: 0 5px;">且 ≤</span>
                <el-input
                  v-model.number="max32"
                  placeholder="最大值(-1表示无限制)"
                  type="number"
                  style="width: 120px;"
                />
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <div class="input-hint">
        <el-text type="info" size="small">
          提示：变量下标从1开始。最小值或最大值为空或-1表示无限制。约束条件用于限制某个变量的取值范围。
        </el-text>
      </div>
    </div>

    <!-- 结果显示区域 -->
    <div class="results-section" v-if="results.length > 0">
      <el-divider content-position="left">不定方程求解结果</el-divider>
      <div class="results-content">
        <div v-for="(result, index) in results" :key="index" class="result-item">
          <!-- 方程信息 -->
          <div class="equation-info">
            <h4>方程：</h4>
            <math-renderer
              :formula="result.formula"
              :type="'mathjax'"
              :display-mode="true"
              class="equation-formula"
            />
            <div v-if="result.filterLaTeX" class="filter-info">
              <h4>约束条件：</h4>
              <math-renderer
                :formula="result.filterLaTeX"
                :type="'mathjax'"
                :display-mode="true"
                class="filter-formula"
              />
            </div>
          </div>

          <!-- 统计结果 -->
          <div class="statistics-info">
            <h4>统计结果：</h4>
            <div class="stat-item">
              <span>总解数：{{ result.totalCount }}</span>
              <span>符合条件的解数：{{ result.acceptedCount }}</span>
            </div>
            <math-renderer
              :formula="result.combinationFormula"
              :type="'mathjax'"
              :display-mode="true"
              class="combination-formula"
            />
          </div>

          <!-- 详细解列表 -->
          <div v-if="result.solutions && result.solutions.length > 0" class="solutions-list">
            <h4>解列表：</h4>
            <div class="solutions-container">
              <div
                v-for="solution in result.solutions"
                :key="solution.number"
                class="solution-item"
                :class="{ 'accepted': solution.accepted, 'not-accepted': !solution.accepted }"
              >
                <div class="solution-header">
                  <span class="solution-number">No.{{ solution.number }}</span>
                  <span class="solution-status" v-if="solution.accepted">
                    , accepted {{ getAcceptedNumber(solution.number, result.solutions) }} solver:
                  </span>
                  <span class="solution-status" v-else>
                    , NOT accepted solver:
                  </span>
                </div>
                <div class="solution-content">
                  <math-renderer
                    :formula="solution.solutionLaTeX"
                    :type="'mathjax'"
                    :display-mode="false"
                    class="solution-formula"
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
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Check,
  Delete,
  WarningFilled,
  RefreshRight,
  Close
} from '@element-plus/icons-vue'
import MathRenderer from '@/components/common/MathRenderer.vue'

// 响应式数据
const varNumber = ref('')
const varSum = ref('')
const outputMode = ref('onlyResult')
const logicAnd = ref(true)

// 约束条件
const index11 = ref('')
const min11 = ref('')
const max11 = ref('')
const index12 = ref('')
const min12 = ref('')
const max12 = ref('')

const index21 = ref('')
const min21 = ref('')
const max21 = ref('')
const index22 = ref('')
const min22 = ref('')
const max22 = ref('')

const index31 = ref('')
const min31 = ref('')
const max31 = ref('')
const index32 = ref('')
const min32 = ref('')
const max32 = ref('')

const results = ref([])
const counter = ref(0)
const examples = ref({})

// 事件处理函数
const emit = defineEmits(['close', 'count-equation-solver-result'])

const startSolve = async () => {
  if (!varNumber.value || !varSum.value) {
    ElMessage.warning('请输入未知数个数和总和')
    return
  }

  // 清空之前的结果
  results.value = []

  try {
    const request = buildRequest()

    console.log('CountEquationSolverInterface: 开始求解', request)

    const response = await callBackendApi('/count-equation-solver/solve', {
      method: 'POST',
      body: JSON.stringify(request)
    })

    console.log('CountEquationSolverInterface: 收到求解结果', response)

    if (response.success) {
      const result = {
        ...response,
        index: counter.value + 1
      }

      results.value.push(result)
      counter.value++

      // 发送结果到主界面
      emit('count-equation-solver-result', result)

      ElMessage.success('方程求解完成')
    } else {
      ElMessage.error(response.errorMessage || '方程求解失败')
    }
  } catch (error) {
    console.error('方程求解失败:', error)
    ElMessage.error(`求解失败: ${error.message}`)
  }
}

const checkInput = async () => {
  if (!varNumber.value || !varSum.value) {
    ElMessage.warning('请输入未知数个数和总和')
    return
  }

  try {
    const request = buildRequest()

    const response = await callBackendApi('/count-equation-solver/validate', {
      method: 'POST',
      body: JSON.stringify(request)
    })

    if (response.valid) {
      ElMessage.success('输入格式正确')
    } else {
      ElMessage.error('输入格式不正确: ' + response.message)
    }
  } catch (error) {
    console.error('检查输入失败:', error)
    ElMessage.error(`检查失败: ${error.message}`)
  }
}

const clearInput = () => {
  varNumber.value = ''
  varSum.value = ''
  outputMode.value = 'onlyResult'
  logicAnd.value = true

  // 清空约束条件
  index11.value = ''
  min11.value = ''
  max11.value = ''
  index12.value = ''
  min12.value = ''
  max12.value = ''

  index21.value = ''
  min21.value = ''
  max21.value = ''
  index22.value = ''
  min22.value = ''
  max22.value = ''

  index31.value = ''
  min31.value = ''
  max31.value = ''
  index32.value = ''
  min32.value = ''
  max32.value = ''

  ElMessage.info('已清空输入')
}

const clearResults = () => {
  results.value = []
  counter.value = 0
  ElMessage.info('已清除所有结果')
}

const closeInterface = () => {
  emit('close')
}

const buildRequest = () => {
  return {
    varNumber: parseInt(varNumber.value),
    varSum: parseInt(varSum.value),
    outputMode: outputMode.value,
    logicAnd: logicAnd.value,

    // 约束条件组1
    index11: index11.value ? parseInt(index11.value) : null,
    min11: min11.value ? parseInt(min11.value) : null,
    max11: max11.value ? parseInt(max11.value) : null,
    index12: index12.value ? parseInt(index12.value) : null,
    min12: min12.value ? parseInt(min12.value) : null,
    max12: max12.value ? parseInt(max12.value) : null,

    // 约束条件组2
    index21: index21.value ? parseInt(index21.value) : null,
    min21: min21.value ? parseInt(min21.value) : null,
    max21: max21.value ? parseInt(max21.value) : null,
    index22: index22.value ? parseInt(index22.value) : null,
    min22: min22.value ? parseInt(min22.value) : null,
    max22: max22.value ? parseInt(max22.value) : null,

    // 约束条件组3
    index31: index31.value ? parseInt(index31.value) : null,
    min31: min31.value ? parseInt(min31.value) : null,
    max31: max31.value ? parseInt(max31.value) : null,
    index32: index32.value ? parseInt(index32.value) : null,
    min32: min32.value ? parseInt(min32.value) : null,
    max32: max32.value ? parseInt(max32.value) : null
  }
}

const loadExample = async (exampleKey) => {
  if (!examples.value[exampleKey]) {
    // 如果还没有加载示例数据，先获取
    await loadExamples()
  }

  const example = examples.value[exampleKey]
  if (example) {
    // 清除之前的结果
    results.value = []
    counter.value = 0

    // 设置示例数据
    varNumber.value = example.varNumber
    varSum.value = example.varSum

    // 重置其他字段为默认值
    clearInput()

    // 重新设置基本信息
    varNumber.value = example.varNumber
    varSum.value = example.varSum

    // 设置约束条件
    if (example.index11) index11.value = example.index11
    if (example.min11) min11.value = example.min11
    if (example.max11) max11.value = example.max11
    if (example.index12) index12.value = example.index12
    if (example.min12) min12.value = example.min12
    if (example.max12) max12.value = example.max12

    if (example.index21) index21.value = example.index21
    if (example.min21) min21.value = example.min21
    if (example.max21) max21.value = example.max21
    if (example.index22) index22.value = example.index22
    if (example.min22) min22.value = example.min22
    if (example.max22) max22.value = example.max22

    if (example.index31) index31.value = example.index31
    if (example.min31) min31.value = example.min31
    if (example.max31) max31.value = example.max31
    if (example.index32) index32.value = example.index32
    if (example.min32) min32.value = example.min32
    if (example.max32) max32.value = example.max32

    if (example.logicAnd !== undefined) logicAnd.value = example.logicAnd

    console.log('CountEquationSolverInterface: 加载示例', exampleKey, example)
    ElMessage.info(`已加载示例：${exampleKey}`)
  }
}

const loadExamples = async () => {
  try {
    const response = await callBackendApi('/count-equation-solver/examples', {
      method: 'GET'
    })

    if (response.success) {
      examples.value = response.examples
    }
  } catch (error) {
    console.error('加载示例失败:', error)
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

// 计算到指定位置为止被接受的解的数量
const getAcceptedNumber = (currentNumber, solutions) => {
  let acceptedCount = 0
  for (let i = 0; i < solutions.length; i++) {
    if (solutions[i].number === currentNumber) {
      if (solutions[i].accepted) {
        acceptedCount++
      }
      break
    }
    if (solutions[i].accepted) {
      acceptedCount++
    }
  }
  return acceptedCount
}

// 组件挂载时加载示例数据
onMounted(() => {
  loadExamples()
})
</script>

<style scoped>
.count-equation-solver-interface {
  display: flex;
  flex-direction: column;
  width: 100%;
}

/* 在大屏幕上完全自适应，无最大宽度限制 */
@media (min-width: 1200px) {
  .count-equation-solver-interface {
    width: 100%;
    max-width: none;
  }
}

/* 在中等屏幕上设置合理的最大宽度 */
@media (max-width: 1199px) and (min-width: 900px) {
  .count-equation-solver-interface {
    width: 100%;
    max-width: 1400px;
    margin: 0 auto;
  }
}

/* 在小屏幕上固定宽度，支持水平滚动 */
@media (max-width: 899px) {
  .count-equation-solver-interface {
    width: 1400px;
    min-width: 1400px;
    max-width: 1400px;
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

.equation-input-section {
  margin-top: 1rem;
  flex-shrink: 0;
}

.constraints-section {
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

.input-row {
  display: flex;
  align-items: center;
  margin-bottom: 1rem;
}

.input-row label {
  min-width: 120px;
  font-weight: 600;
  color: #374151;
}

.constraint-inputs {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}

.input-hint {
  margin-top: 1rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

/* 结果显示区域 */
.results-section {
  padding: 1rem;
  background: #ffffff;
  margin-top: 1rem;
}

.results-content {
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.result-item {
  margin-bottom: 2rem;
  padding-bottom: 2rem;
  border-bottom: 1px solid #dee2e6;
}

.result-item:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.equation-info,
.statistics-info,
.solutions-list {
  margin-bottom: 1.5rem;
}

.equation-formula,
.filter-formula,
.combination-formula {
  margin: 1rem 0;
  padding: 0.5rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  overflow-x: auto;
}

.stat-item {
  margin: 1rem 0;
  display: flex;
  gap: 2rem;
}

.solutions-container {
  margin-top: 1rem;
  max-height: 400px;
  overflow-y: auto;
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  padding: 0.75rem;
}

.solution-item {
  margin-bottom: 0.75rem;
  padding: 1rem;
  border-radius: 8px;
  background: white;
  border: 1px solid #e5e7eb;
  transition: all 0.2s ease;
}

.solution-item:last-child {
  margin-bottom: 0;
}

.solution-item.accepted {
  border-color: #10b981;
  background: #ecfdf5;
  box-shadow: 0 1px 3px rgba(16, 185, 129, 0.1);
}

.solution-item.not-accepted {
  border-color: #f59e0b;
  background: #fffbeb;
  opacity: 0.8;
}

.solution-item:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.solution-header {
  display: flex;
  align-items: center;
  margin-bottom: 0.5rem;
  flex-wrap: wrap;
  gap: 0.25rem;
}

.solution-number {
  font-weight: 600;
  color: #374151;
  font-size: 0.9rem;
}

.solution-status {
  color: #6b7280;
  font-size: 0.9rem;
  font-weight: 500;
}

.solution-content {
  display: flex;
  align-items: center;
}

.solution-formula {
  font-size: 1rem;
  overflow-x: auto;
  padding: 0.5rem;
  background: #f9fafb;
  border-radius: 4px;
  border: 1px solid #e5e7eb;
  flex: 1;
}

.solutions-summary {
  margin-top: 1rem;
}

/* 滚动条样式 */
.solutions-container::-webkit-scrollbar {
  width: 8px;
}

.solutions-container::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 4px;
}

.solutions-container::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 4px;
}

.solutions-container::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

h4 {
  color: #374151;
  margin: 1rem 0 0.5rem 0;
  font-size: 1.1rem;
  font-weight: 600;
}

/* 滚动条样式 */
.results-section::-webkit-scrollbar {
  width: 8px;
}

.results-section::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.results-section::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.results-section::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}

/* 公式容器确保不会溢出 */
.math-renderer {
  overflow-x: auto;
  max-width: 100%;
}

/* 响应式优化 */
@media (max-width: 768px) {
  .example-buttons .el-button {
    font-size: 0.8rem;
  }

  .solution-formula {
    font-size: 0.8rem;
  }
}

@media (max-width: 480px) {
  .button-row .el-col {
    margin-bottom: 0.5rem;
  }

  .solutions-container {
    max-height: 300px;
  }

  .solution-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.25rem;
  }
}
</style>