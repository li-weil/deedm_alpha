<template>
  <div class="count-function-interface">
    <!-- 按钮操作区域 -->
    <div class="button-section">
      <!-- 第一行按钮：开始运算、生成随机集合、清除输入、合法性检查 -->
      <el-row :gutter="20" class="button-row">
        <el-col :span="4">
          <el-button type="primary" @click="startCalculation" :loading="calculating">
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
          <el-button size="small" @click="loadExample('example8_36')">例题8.36</el-button>
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

      <!-- 函数性质选择 -->
      <el-divider content-position="left">选择要计数的函数性质</el-divider>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-checkbox v-model="options.countInjection" size="large">
            单射函数
          </el-checkbox>
        </el-col>
        <el-col :span="6">
          <el-checkbox v-model="options.countSurjection" size="large">
            满射函数
          </el-checkbox>
        </el-col>
        <el-col :span="6">
          <el-checkbox v-model="options.countBijection" size="large">
            双射函数
          </el-checkbox>
        </el-col>
        <el-col :span="6">
          <el-checkbox v-model="options.detailed" size="large">
            显示具体函数映射关系
          </el-checkbox>
        </el-col>
      </el-row>
    </div>

    <!-- 集合输入区域 -->
    <div class="set-input-section">
      <el-divider content-position="left">函数定义域(A)和值域(B)集合的输入</el-divider>
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="input-group">
            <label>定义域(A)：</label>
            <el-input
              v-model="setAInput"
              placeholder="例如：{1, 2, 3, 4, 5, 6}"
              class="set-input"
            />
          </div>
        </el-col>
        <el-col :span="12">
          <div class="input-group">
            <label>值域(B)：</label>
            <el-input
              v-model="setBInput"
              placeholder="例如：{a, b, c}"
              class="set-input"
            />
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20" style="margin-top: 1rem;">
        <el-col :span="12">
          <div class="input-group">
            <label>显示函数的最大数量(D)：</label>
            <el-input
              v-model="maxDisplayInput"
              placeholder="例如：100"
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
        </el-text>
      </div>
    </div>

    <!-- 内容显示区域 -->
    <div class="content-display-area">
      <!-- 反馈信息区域 -->
      <div v-if="feedback.length > 0" class="feedback-section">
        <el-divider content-position="left">计算进度和反馈信息</el-divider>
        <div class="feedback-content">
          <div v-for="(item, index) in feedback" :key="index" class="feedback-item">
            <math-renderer
              v-if="item.formula"
              :formula="item.formula"
              :type="'mathjax'"
              :display-mode="true"
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
        <el-divider content-position="left">函数计数分析结果</el-divider>
        <div class="results-content">
          <div v-for="(result, index) in results" :key="index" class="result-item">
            <!-- 集合基本信息 -->
            <div class="result-basic">
              <h4>输入集合：</h4>
              <math-renderer
                :formula="'A = ' + result.setALaTeX"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
              <math-renderer
                :formula="'B = ' + result.setBLaTeX"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
            </div>

            <!-- 统计结果 -->
            <div class="statistics-result">
              <h4>函数统计：</h4>
              <math-renderer
                :formula="'|B|^{|A|} = ' + result.bSize + '^{' + result.aSize + '} = ' + result.totalCount"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
              <el-text tag="p" class="summary-text">
                总共有 {{ result.totalCount }} 个函数，其中双射 {{ result.bijectionCount }} 个，
                满射 {{ result.surjectionCount }} 个，而单射 {{ result.injectionCount }} 个
              </el-text>
            </div>

            <!-- 具体函数列表 -->
            <div v-if="result.functionList && result.functionList.length > 0" class="function-list">
              <h4>具体函数列表：</h4>
              <div class="function-scroll-container">
                <div v-for="(func, funcIndex) in result.functionList" :key="funcIndex" class="function-item">
                  <p>{{ getFunctionLabel(func) }}</p>

                  <math-renderer
                    :formula="func.laTeX"
                    :type="'mathjax'"
                    :display-mode="true"
                    class="function-formula"
                  />
                </div>
                <el-text v-if="result.hasMoreFunctions" type="info" class="more-functions">
                  ... 还有更多函数未显示 ...
                </el-text>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Check, MagicStick, Delete, WarningFilled, RefreshRight, Close } from '@element-plus/icons-vue'
import MathRenderer from '@/components/common/MathRenderer.vue'

// 定义 emits
const emit = defineEmits(['close', 'count-function'])

// 响应式数据
const calculating = ref(false)
const elementType = ref('char')
const setAInput = ref('{1, 2, 3, 4, 5, 6}')
const setBInput = ref('{a, b, c}')
const maxDisplayInput = ref('100')
const feedback = ref([])
const results = ref([])

const options = reactive({
  countInjection: true,
  countSurjection: true,
  countBijection: true,
  detailed: false
})

// API 调用函数
const callBackendApi = async (endpoint, options = {}) => {
  try {
    // 暂时直接使用后端地址，绕过代理
    const baseUrl = 'http://localhost:8080'
    const fullUrl = `${baseUrl}/api${endpoint}`

    const jsonString = JSON.stringify(options.body)
    console.log('=== HTTP请求调试 ===')
    console.log('API调用URL:', fullUrl)
    console.log('请求方法: POST')
    console.log('Content-Type: application/json')
    console.log('请求体原始JSON:', jsonString)
    console.log('JSON字符串长度:', jsonString.length)
    console.log('JSON第一个字符:', jsonString.charAt(0))
    console.log('JSON最后一个字符:', jsonString.charAt(jsonString.length - 1))
    console.log('==================')

    const response = await fetch(fullUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      body: jsonString
    })

    console.log('响应状态:', response.status, response.statusText)

    const responseText = await response.text()
    console.log('响应原始文本:', responseText)

    let result
    try {
      result = JSON.parse(responseText)
      console.log('解析后的响应数据:', result)
    } catch (parseError) {
      console.error('JSON解析失败:', parseError)
      throw new Error('响应不是有效的JSON格式: ' + responseText)
    }

    if (!response.ok) {
      throw new Error(result.message || `HTTP error! status: ${response.status}`)
    }

    return result
  } catch (error) {
    console.error(`API调用失败 (${endpoint}):`, error)
    throw error
  }
}

// 生成函数标签
const getFunctionLabel = (func) => {
  if (func.type === 'bijection') {
    return `第 ${func.totalNumber} 个函数，双射第 ${func.countNumber} 个：`
  } else if (func.type === 'injection') {
    return `第 ${func.totalNumber} 个函数，单射第 ${func.countNumber} 个：`
  } else if (func.type === 'surjection') {
    return `第 ${func.totalNumber} 个函数，满射第 ${func.countNumber} 个：`
  } else {
    return `第 ${func.totalNumber} 个函数：`
  }
}

// 开始运算
const startCalculation = async () => {
  try {
    calculating.value = true
    clearResults()

    // 构建请求数据
    const data = {
      setAString: setAInput.value || "{1, 2, 3}",
      setBString: setBInput.value || "{a, b}",
      maxDisplayString: maxDisplayInput.value || "10",
      intType: elementType.value === 'int',
      countInjection: options.countInjection,
      countSurjection: options.countSurjection,
      countBijection: options.countBijection,
      detailed: options.detailed
    }

    // 确保数据格式正确
    const requestData = {}
    Object.keys(data).forEach(key => {
      if (typeof data[key] !== 'undefined' && data[key] !== null) {
        requestData[key] = data[key]
      }
    })

    console.log('=== 调试信息 ===')
    console.log('原始data:', data)
    console.log('处理后requestData:', requestData)
    console.log('JSON字符串:', JSON.stringify(requestData))
    console.log('requestData类型:', typeof requestData)
    console.log('是否为数组:', Array.isArray(requestData))
    console.log('===============')

    const response = await callBackendApi('/count-function/count', {
      body: requestData
    })

    if (response.success) {
      // 处理成功结果
      const result = {
        ...response,
        aSize: setAInput.value.replace(/[{}]/g, '').split(',').length,
        bSize: setBInput.value.replace(/[{}]/g, '').split(',').length,
        hasMoreFunctions: response.functionList.length >= parseInt(maxDisplayInput.value)
      }

      results.value.push(result)

      // 发送结果到父组件
      emit('count-function', result)

      ElMessage.success('函数计数计算完成')
    } else {
      ElMessage.error('计算失败: ' + response.errorMessage)
    }
  } catch (error) {
    console.error('计算过程中发生错误:', error)
    ElMessage.error('计算过程中发生错误: ' + error.message)
  } finally {
    calculating.value = false
  }
}

// 生成随机集合
const generateRandomSets = async () => {
  try {
    const response = await callBackendApi('/count-function/generate-example', {
      method: 'POST'
    })

    if (response.error) {
      ElMessage.error('生成示例失败: ' + response.error)
      return
    }

    setAInput.value = response.setAString
    setBInput.value = response.setBString
    maxDisplayInput.value = response.maxDisplayString
    elementType.value = response.intType ? 'int' : 'char'
    options.countInjection = response.countInjection
    options.countSurjection = response.countSurjection
    options.countBijection = response.countBijection
    options.detailed = response.detailed

    ElMessage.success('已生成随机集合')
  } catch (error) {
    console.error('生成随机集合时发生错误:', error)
    ElMessage.error('生成随机集合失败: ' + error.message)
  }
}

// 清除输入
const clearInput = () => {
  setAInput.value = ''
  setBInput.value = ''
  maxDisplayInput.value = '100'
  feedback.value = []
  ElMessage.success('输入已清除')
}

// 合法性检查
const checkInput = async () => {
  try {
    const requestData = {
      setAString: setAInput.value,
      setBString: setBInput.value,
      maxDisplayString: maxDisplayInput.value,
      intType: elementType.value === 'int'
    }

    const response = await callBackendApi('/count-function/validate', {
      body: requestData
    })

    if (response.valid) {
      ElMessage.success('输入的数据完全符合要求，可以进行后续的函数计算')
    } else {
      ElMessage.warning('输入的数据不完全符合要求')
      feedback.value = [{
        message: response.message,
        type: 'error'
      }]
    }
  } catch (error) {
    console.error('合法性检查时发生错误:', error)
    ElMessage.error('合法性检查失败: ' + error.message)
  }
}

// 清除结果
const clearResults = () => {
  results.value = []
  feedback.value = []
}

// 加载示例
const loadExample = async (exampleType) => {
  if (exampleType === 'example8_36') {
    setAInput.value = '{1, 2, 3, 4, 5, 6}'
    setBInput.value = '{a, b, c}'
    maxDisplayInput.value = '100'
    elementType.value = 'char'
    options.countInjection = false
    options.countSurjection = true
    options.countBijection = false
    options.detailed = true

    ElMessage.success('已加载例题8.36')
  }
}

// 关闭界面
const closeInterface = () => {
  emit('close')
}
</script>

<style scoped>

.count-function-interface {
  display: flex;
  flex-direction: column;
  width: 100%;
}

/* 在大屏幕上完全自适应，无最大宽度限制 */
@media (min-width: 1200px) {
  .count-function-interface {
    width: 100%;
    max-width: none;
  }
}

/* 在中等屏幕上设置合理的最大宽度 */
@media (max-width: 1199px) and (min-width: 900px) {
  .count-function-interface {
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
  }
}

/* 在小屏幕上固定宽度，支持水平滚动 */
@media (max-width: 899px) {
  .count-function-interface {
    width: 900px;
    min-width: 900px;
    max-width: 900px;
    margin: 0 auto;
  }
}

.button-section {
  margin-bottom: 2rem;
}

.button-row {
  margin-bottom: 1rem;
}

.example-buttons {
  margin-bottom: 1rem;
}

.set-input-section {
  margin-bottom: 2rem;
}

.input-group {
  margin-bottom: 1rem;
}

.input-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: bold;
}

.set-input {
  width: 100%;
}

.input-hint {
  margin-top: 1rem;
  padding: 0.5rem;
  background-color: #f5f5f5;
  border-radius: 4px;
}

.content-display-area {
  min-height: 200px;
}

.feedback-section,
.results-section {
  margin-bottom: 2rem;
}

/* 反馈信息样式优化 */
.feedback-content {
  padding: 1.5rem;
  background-color: #f8f9fa;
  border-radius: 6px;
  border-left: 3px solid #6c757d;
}

.feedback-item {
  margin-bottom: 1rem;
  padding: 1rem;
  background-color: white;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.feedback-formula {
  margin-bottom: 0.8rem;
  padding: 1rem;
  background-color: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.feedback-message {
  font-size: 14px;
  line-height: 1.6;
  color: #495057;
}

/* 结果展示区域样式优化 */
.results-content {
  padding: 1.5rem;
  background-color: #f8f9fa;
  border-radius: 6px;
}

.result-item {
  margin-bottom: 2rem;
  padding: 1.5rem;
  background-color: white;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.result-basic h4,
.statistics-result h4,
.function-list h4 {
  margin-bottom: 1rem;
  color: #212529;
  font-weight: 600;
  font-size: 16px;
}

.result-formula {
  margin-bottom: 1rem;
  padding: 1rem;
  background-color: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

/* 统计结果样式优化 */
.summary-text {
  font-size: 15px;
  color: #212529;
  margin: 1rem 0;
  line-height: 1.6;
  padding: 1rem;
  background-color: #f8f9fa;
  border-radius: 4px;
}

/* 函数列表样式优化 */
.function-list {
  margin-top: 1.5rem;
  padding-top: 1.5rem;
  border-top: 1px solid #e9ecef;
}

.function-scroll-container {
  max-height: 400px;
  overflow-y: auto;
  overflow-x: hidden;
  border: 1px solid #e9ecef;
  border-radius: 4px;
  background-color: white;
  padding: 1rem;
}

.function-item {
  margin-bottom: 1rem;
  padding: 1rem;
  background-color: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.function-item:last-child {
  margin-bottom: 0;
}

/* 滚动条样式美化 */
.function-scroll-container::-webkit-scrollbar {
  width: 8px;
}

.function-scroll-container::-webkit-scrollbar-track {
  background: #f1f3f4;
  border-radius: 4px;
}

.function-scroll-container::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
  border: 2px solid #f1f3f4;
}

.function-scroll-container::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.function-scroll-container::-webkit-scrollbar-thumb:active {
  background: #959595;
}

.function-label {
  font-weight: 600;
  color: #212529;
  margin-bottom: 0.8rem;
  display: block;
  font-size: 14px;
  padding: 0.5rem;
  background-color: #ffffff;
  border-radius: 4px;
}

.function-formula {
  padding: 1rem;
  background-color: #ffffff;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  font-family: 'Courier New', monospace;
  line-height: 1.5;
  color: #495057;
}

.more-functions {
  display: block;
  text-align: center;
  margin-top: 1.5rem;
  padding: 1rem;
  color: #6c757d;
  font-style: italic;
  font-size: 14px;
  background-color: #f8f9fa;
  border-radius: 4px;
  border: 1px dashed #ced4da;
}
</style>