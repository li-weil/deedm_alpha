<template>
  <div class="count-integer-interface">
    <!-- 按钮操作区域 -->
    <div class="button-section">
      <!-- 第一行按钮：开始运算、清除输入、合法性检查等 -->
      <el-row :gutter="20" class="button-row">
        <el-col :span="4">
          <el-button type="primary" @click="startCounting" :loading="loading">
            <el-icon><Check /></el-icon>
            开始运算(Y)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="info" @click="checkValidity">
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
          <el-button type="success" @click="clearResults">
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
          <el-button size="small" @click="loadExample('example8_11')">例题8.11</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('example8_12_1')">例题8.12(1)</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('example8_12_2')">例题8.12(2)</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('example8_12_3')">例题8.12(3)</el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 输入区域 -->
    <div class="input-section">
      <el-card class="input-card" header="计数整数范围以及整除性质条件设置">
        <!-- 基本参数行 -->
        <div class="basic-params-row">
          <div class="param-group">
            <label class="param-label">起始整数(S)：</label>
            <el-input v-model="startInput" placeholder="例如: 1" class="range-input" />
          </div>

          <div class="param-group">
            <label class="param-label">终止整数(E)：</label>
            <el-input v-model="endInput" placeholder="例如: 1000" class="range-input" />
          </div>

          <div class="param-group">
            <label class="param-label">各组条件间的逻辑关系：</label>
            <el-radio-group v-model="logicRelation" class="logic-group">
              <el-radio label="AND">逻辑与关系</el-radio>
              <el-radio label="OR">逻辑或关系</el-radio>
            </el-radio-group>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 三组过滤条件 -->
    <div class="filter-section">
      <el-card class="filter-card" header="整除性质过滤条件设置（请输入非零整数，用逗号分隔多个整数）">
        <div class="filter-conditions">
          <!-- 第一组条件 -->
          <div class="filter-condition-group">
            <h4>第一组条件</h4>
            <div class="filter-row">
              <div class="division-filter">
                <label class="filter-label">该组的整数</label>
                <el-select v-model="filterGroups[0].conditions[0].divisionType" size="small" class="division-select">
                  <el-option label="必须能被" value="DIVIDED" />
                  <el-option label="必须不能被" value="NOT_DIVIDED" />
                </el-select>
                <el-input v-model="filterGroups[0].conditions[0].integers" placeholder="这些整数之一，如: 3,5" size="small" class="integers-input" />
                <label class="filter-label">整除</label>
              </div>

              <div class="logic-separator">
                <el-select v-model="filterGroups[0].innerLogic" size="small" class="logic-select">
                  <el-option label="而且" value="AND" />
                  <el-option label="或者" value="OR" />
                </el-select>
              </div>

              <div class="division-filter">
                <el-select v-model="filterGroups[0].conditions[1].divisionType" size="small" class="division-select">
                  <el-option label="必须能被" value="DIVIDED" />
                  <el-option label="必须不能被" value="NOT_DIVIDED" />
                </el-select>
                <el-input v-model="filterGroups[0].conditions[1].integers" placeholder="这些整数之一，如: 2,4" size="small" class="integers-input" />
                <label class="filter-label">整除</label>
              </div>
            </div>
          </div>

          <!-- 第二组条件 -->
          <div class="filter-condition-group">
            <h4>第二组条件</h4>
            <div class="filter-row">
              <div class="division-filter">
                <label class="filter-label">该组的整数</label>
                <el-select v-model="filterGroups[1].conditions[0].divisionType" size="small" class="division-select">
                  <el-option label="必须能被" value="DIVIDED" />
                  <el-option label="必须不能被" value="NOT_DIVIDED" />
                </el-select>
                <el-input v-model="filterGroups[1].conditions[0].integers" placeholder="这些整数之一，如: 3" size="small" class="integers-input" />
                <label class="filter-label">整除</label>
              </div>

              <div class="logic-separator">
                <el-select v-model="filterGroups[1].innerLogic" size="small" class="logic-select">
                  <el-option label="而且" value="AND" />
                  <el-option label="或者" value="OR" />
                </el-select>
              </div>

              <div class="division-filter">
                <el-select v-model="filterGroups[1].conditions[1].divisionType" size="small" class="division-select">
                  <el-option label="必须能被" value="DIVIDED" />
                  <el-option label="必须不能被" value="NOT_DIVIDED" />
                </el-select>
                <el-input v-model="filterGroups[1].conditions[1].integers" placeholder="这些整数之一，如: 5,7" size="small" class="integers-input" />
                <label class="filter-label">整除</label>
              </div>
            </div>
          </div>

          <!-- 第三组条件 -->
          <div class="filter-condition-group">
            <h4>第三组条件</h4>
            <div class="filter-row">
              <div class="division-filter">
                <label class="filter-label">该组的整数</label>
                <el-select v-model="filterGroups[2].conditions[0].divisionType" size="small" class="division-select">
                  <el-option label="必须能被" value="DIVIDED" />
                  <el-option label="必须不能被" value="NOT_DIVIDED" />
                </el-select>
                <el-input v-model="filterGroups[2].conditions[0].integers" placeholder="这些整数之一，如: 2" size="small" class="integers-input" />
                <label class="filter-label">整除</label>
              </div>

              <div class="logic-separator">
                <el-select v-model="filterGroups[2].innerLogic" size="small" class="logic-select">
                  <el-option label="而且" value="AND" />
                  <el-option label="或者" value="OR" />
                </el-select>
              </div>

              <div class="division-filter">
                <el-select v-model="filterGroups[2].conditions[1].divisionType" size="small" class="division-select">
                  <el-option label="必须能被" value="DIVIDED" />
                  <el-option label="必须不能被" value="NOT_DIVIDED" />
                </el-select>
                <el-input v-model="filterGroups[2].conditions[1].integers" placeholder="这些整数之一，如: 3,6" size="small" class="integers-input" />
                <label class="filter-label">整除</label>
              </div>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 输出选项 -->
    <div class="output-section">
      <el-card class="output-card" header="选择计数结果的展示方式">
        <el-radio-group v-model="outputOption" class="output-options">
          <el-radio label="ONLY_RESULT">只显示统计结果</el-radio>
          <el-radio label="ACCEPT_50">显示到前50个接受的整数</el-radio>
          <el-radio label="PARTIAL_100">显示前100个整数的详细信息</el-radio>
          <el-radio label="ONLY_ACCEPTED">只显示接受的整数</el-radio>
          <el-radio label="ALL_INTEGERS">显示范围内所有整数</el-radio>
        </el-radio-group>
      </el-card>
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
        <el-divider content-position="left">整数计数分析结果</el-divider>
        <div class="results-content">
          <div v-for="(result, index) in results" :key="index" class="result-item">
            <!-- 基本信息 -->
            <div class="result-basic">
              <h4>计数范围：</h4>
              <math-renderer
                :formula="result.formula"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
              <h4>过滤条件：</h4>
              <math-renderer
                :formula="result.filterDescription"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
            </div>

            <!-- 统计结果 -->
            <div class="statistics-result">
              <h4>统计结果：</h4>
              <math-renderer
                :formula="`总整数个数：${result.totalCount}，满足条件的整数个数：${result.acceptedCount}`"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
            </div>

            <!-- 详细结果 -->
            <div v-if="getFilteredDetailedResults(result).length > 0" class="detailed-results">
              <h4>详细结果：</h4>
              <div class="results-table">
                <el-table :data="getFilteredDetailedResults(result)" stripe size="small" max-height="400">
                  <el-table-column prop="index" label="序号" width="80" />
                  <el-table-column prop="value" label="整数值" width="100" />
                  <el-table-column prop="acceptMessage" label="接受状态" />
                </el-table>
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
  WarningFilled,
  Delete,
  RefreshRight,
  Close
} from '@element-plus/icons-vue'
import MathRenderer from '@/components/common/MathRenderer.vue'

// 响应式数据
const startInput = ref('')
const endInput = ref('')
const logicRelation = ref('AND')
const outputOption = ref('ONLY_RESULT')
const loading = ref(false)
const feedback = ref([])
const results = ref([])
const counter = ref(0)

// 过滤条件组数据结构
const filterGroups = ref([
  {
    innerLogic: 'AND',
    conditions: [
      { divisionType: 'DIVIDED', integers: '' },
      { divisionType: 'DIVIDED', integers: '' }
    ]
  },
  {
    innerLogic: 'AND',
    conditions: [
      { divisionType: 'DIVIDED', integers: '' },
      { divisionType: 'DIVIDED', integers: '' }
    ]
  },
  {
    innerLogic: 'AND',
    conditions: [
      { divisionType: 'DIVIDED', integers: '' },
      { divisionType: 'DIVIDED', integers: '' }
    ]
  }
])

// 示例数据
const examples = {
  example8_11: {
    start: '1',
    end: '1000',
    logicRelation: 'OR',
    outputOption: 'ONLY_RESULT',
    filterGroups: [
      {
        innerLogic: 'AND',
        conditions: [
          { divisionType: 'DIVIDED', integers: '3, 5' },
          { divisionType: 'DIVIDED', integers: '' }
        ]
      },
      { innerLogic: 'AND', conditions: [{ divisionType: 'DIVIDED', integers: '' }, { divisionType: 'DIVIDED', integers: '' }] },
      { innerLogic: 'AND', conditions: [{ divisionType: 'DIVIDED', integers: '' }, { divisionType: 'DIVIDED', integers: '' }] }
    ]
  },
  example8_12_1: {
    start: '100',
    end: '999',
    logicRelation: 'AND',
    outputOption: 'ONLY_RESULT',
    filterGroups: [
      {
        innerLogic: 'AND',
        conditions: [
          { divisionType: 'NOT_DIVIDED', integers: '3' },
          { divisionType: 'DIVIDED', integers: '' }
        ]
      },
      { innerLogic: 'AND', conditions: [{ divisionType: 'DIVIDED', integers: '' }, { divisionType: 'DIVIDED', integers: '' }] },
      { innerLogic: 'AND', conditions: [{ divisionType: 'DIVIDED', integers: '' }, { divisionType: 'DIVIDED', integers: '' }] }
    ]
  },
  example8_12_2: {
    start: '100',
    end: '999',
    logicRelation: 'AND',
    outputOption: 'ONLY_RESULT',
    filterGroups: [
      {
        innerLogic: 'AND',
        conditions: [
          { divisionType: 'NOT_DIVIDED', integers: '3, 5' },
          { divisionType: 'DIVIDED', integers: '' }
        ]
      },
      { innerLogic: 'AND', conditions: [{ divisionType: 'DIVIDED', integers: '' }, { divisionType: 'DIVIDED', integers: '' }] },
      { innerLogic: 'AND', conditions: [{ divisionType: 'DIVIDED', integers: '' }, { divisionType: 'DIVIDED', integers: '' }] }
    ]
  },
  example8_12_3: {
    start: '100',
    end: '999',
    logicRelation: 'AND',
    outputOption: 'ONLY_RESULT',
    filterGroups: [
      {
        innerLogic: 'AND',
        conditions: [
          { divisionType: 'DIVIDED', integers: '3' },
          { divisionType: 'NOT_DIVIDED', integers: '5' }
        ]
      },
      { innerLogic: 'AND', conditions: [{ divisionType: 'DIVIDED', integers: '' }, { divisionType: 'DIVIDED', integers: '' }] },
      { innerLogic: 'AND', conditions: [{ divisionType: 'DIVIDED', integers: '' }, { divisionType: 'DIVIDED', integers: '' }] }
    ]
  }
}

// 事件处理函数
const emit = defineEmits(['close', 'count-integer-result'])

const startCounting = async () => {
  if (!startInput.value.trim() || !endInput.value.trim()) {
    ElMessage.warning('请输入起始和终止整数')
    return
  }

  // 清空之前的反馈和结果
  feedback.value = []
  results.value = []

  loading.value = true
  try {
    const request = buildRequest()

    console.log('CountIntegerInterface: 开始整数计数', request)

    const response = await callBackendApi('/count-integer/count', {
      method: 'POST',
      body: JSON.stringify(request)
    })

    console.log('CountIntegerInterface: 收到计数结果', response)

    if (response.success) {
      const result = {
        ...response,
        index: counter.value + 1,
        outputOption: outputOption.value  // 确保包含输出选项信息
      }

      results.value.push(result)
      counter.value++

      // 添加成功反馈
      feedback.value.push({
        formula: response.formula,
        type: 'success',
        message: '整数计数完成'
      })

      // 发送结果到主界面
      emit('count-integer-result', result)

      ElMessage.success('整数计数完成')
    } else {
      feedback.value.push({
        formula: 'S = [?, ?]',
        type: 'error',
        message: response.errorMessage || '整数计数失败'
      })
    }
  } catch (error) {
    console.error('整数计数失败:', error)
    feedback.value.push({
      formula: 'S = [?, ?]',
      type: 'error',
      message: `计数失败: ${error.message}`
    })
  } finally {
    loading.value = false
  }
}

const checkValidity = async () => {
  if (!startInput.value.trim() || !endInput.value.trim()) {
    ElMessage.warning('请输入起始和终止整数')
    return
  }

  feedback.value = []

  try {
    const response = await callBackendApi('/count-integer/validate', {
      method: 'POST',
      body: JSON.stringify({
        start: startInput.value.trim(),
        end: endInput.value.trim()
      })
    })

    if (response.valid) {
      feedback.value.push({
        formula: `S = [${startInput.value}, ${endInput.value}]`,
        type: 'success',
        message: response.message
      })
      ElMessage.success('输入格式正确')
    } else {
      feedback.value.push({
        formula: `S = [${startInput.value}, ${endInput.value}]`,
        type: 'error',
        message: response.message
      })
      ElMessage.error('输入格式不正确')
    }
  } catch (error) {
    console.error('检查输入失败:', error)
    feedback.value.push({
      formula: `S = [${startInput.value}, ${endInput.value}]`,
      type: 'error',
      message: `检查失败: ${error.message}`
    })
  }
}

const clearInput = () => {
  startInput.value = ''
  endInput.value = ''
  logicRelation.value = 'AND'
  outputOption.value = 'ONLY_RESULT'

  // 重置过滤条件
  filterGroups.value.forEach(group => {
    group.innerLogic = 'AND'
    group.conditions.forEach(condition => {
      condition.divisionType = 'DIVIDED'
      condition.integers = ''
    })
  })

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
  if (examples[exampleKey]) {
    const example = examples[exampleKey]

    // 清除之前的结果
    results.value = []
    feedback.value = []
    counter.value = 0

    // 设置示例数据
    startInput.value = example.start
    endInput.value = example.end
    logicRelation.value = example.logicRelation
    outputOption.value = example.outputOption

    // 设置过滤条件
    filterGroups.value = JSON.parse(JSON.stringify(example.filterGroups))

    console.log('CountIntegerInterface: 加载示例', exampleKey, example)
    ElMessage.info(`已加载示例：${exampleKey}`)
  }
}

const buildRequest = () => {
  const request = {
    start: parseInt(startInput.value.trim()),
    end: parseInt(endInput.value.trim()),
    logicRelation: logicRelation.value,
    outputOption: outputOption.value,
    filterGroups: []
  }

  // 构建过滤条件组
  filterGroups.value.forEach(group => {
    const filterGroup = {
      divisionConditions: []
    }

    // 添加非空的条件
    group.conditions.forEach(condition => {
      if (condition.integers && condition.integers.trim()) {
        filterGroup.divisionConditions.push({
          divisionType: condition.divisionType,
          integers: condition.integers.trim()
        })
      }
    })

    // 如果该组有有效条件，则添加到请求中
    if (filterGroup.divisionConditions.length > 0) {
      request.filterGroups.push(filterGroup)
    }
  })

  return request
}

// 根据输出选项过滤详细结果
const getFilteredDetailedResults = (result) => {
  if (!result.detailedResults || result.detailedResults.length === 0) {
    return []
  }

  // 如果选择了"只显示接受的整数"，则只返回接受的整数
  if (result.outputOption === 'ONLY_ACCEPTED') {
    return result.detailedResults.filter(item => item.accepted)
  }

  // 其他选项返回所有详细结果
  return result.detailedResults
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
.count-integer-interface {
  display: flex;
  flex-direction: column;
  width: 100%;
}

/* 在大屏幕上完全自适应，无最大宽度限制 */
@media (min-width: 1200px) {
  .count-integer-interface {
    width: 100%;
    max-width: none;
  }
}

/* 在中等屏幕上设置合理的最大宽度 */
@media (max-width: 1199px) and (min-width: 900px) {
  .count-integer-interface {
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
  }
}

/* 在小屏幕上固定宽度，支持水平滚动 */
@media (max-width: 899px) {
  .count-integer-interface {
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

.input-section,
.filter-section,
.output-section {
  margin-top: 1rem;
  flex-shrink: 0;
}

.basic-params-row {
  display: flex;
  align-items: center;
  gap: 2rem;
  flex-wrap: wrap;
}

.param-group {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.param-label {
  font-weight: 600;
  color: #374151;
  white-space: nowrap;
}

.range-input {
  width: 120px;
}

.logic-group {
  display: flex;
  gap: 1rem;
}

.filter-condition-group {
  margin-bottom: 1.5rem;
  padding: 1rem;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #f9fafb;
}

.filter-condition-group h4 {
  margin: 0 0 1rem 0;
  color: #374151;
  font-size: 1rem;
  font-weight: 600;
}

.filter-row {
  display: flex;
  align-items: center;
  gap: 1rem;
  flex-wrap: wrap;
}

.division-filter {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.division-select {
  width: 120px;
}

.integers-input {
  width: 200px;
}

.logic-separator {
  margin: 0 1rem;
}

.logic-select {
  width: 80px;
}

.output-options {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
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

.result-basic,
.statistics-result {
  margin-bottom: 1.5rem;
}

.detailed-results {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.results-table {
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