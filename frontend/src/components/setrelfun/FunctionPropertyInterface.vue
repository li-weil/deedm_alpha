<template>
  <div class="function-property-interface">
    <!-- 按钮操作区域 -->
    <div class="button-section">
      <!-- 第一行按钮：函数性质判断、生成随机函数、清除输入、合法性检查 -->
      <el-row :gutter="20" class="button-row">
        <el-col :span="4">
          <el-button type="primary" @click="startAnalysis">
            <el-icon><Check /></el-icon>
            函数性质判断(Y)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="success" @click="generateRandomFunction">
            <el-icon><MagicStick /></el-icon>
            生成随机函数(G)
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
      <el-divider content-position="left">《离散数学基础》教材示例展示（包含非函数示例）</el-divider>
      <el-row :gutter="15" class="example-buttons">
        <el-col :span="3">
          <el-button size="small" @click="loadExample('example7_1_1')">例题7.1(1)</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('example7_1_2')">例题7.1(2)</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('example7_1_3')">例题7.1(3)</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('example7_1_4')">例题7.1(4)</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('problem7_7_1')">习题7.7(1)</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('problem7_7_2')">习题7.7(2)</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('problem7_7_3')">习题7.7(3)</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('problem7_7_4')">习题7.7(4)</el-button>
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

      <!-- 函数性质判断选项设置 -->
      <el-divider content-position="left">选择要判断的函数性质</el-divider>
      <el-row :gutter="20">
        <el-col :span="4">
          <el-checkbox v-model="options.checkInjection" size="large">
            是否单函数
          </el-checkbox>
        </el-col>
        <el-col :span="4">
          <el-checkbox v-model="options.checkSurjection" size="large">
            是否满函数
          </el-checkbox>
        </el-col>
        <el-col :span="4">
          <el-checkbox v-model="options.checkBijection" size="large">
            是否双函数
          </el-checkbox>
        </el-col>
        <el-col :span="4">
          <el-checkbox v-model="options.showRelationMatrix" size="large">
            显示函数（关系）矩阵
          </el-checkbox>
        </el-col>
        <el-col :span="4">
          <el-checkbox v-model="options.showRelationGraph" size="large">
            显示函数（关系）图
          </el-checkbox>
        </el-col>
      </el-row>
    </div>

    <!-- 函数输入区域 -->
    <div class="function-input-section">
      <el-divider content-position="left">集合和函数的输入</el-divider>
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="input-group">
            <label>源集合(A)：</label>
            <el-input
              v-model="setAInput"
              placeholder="例如：{1, 2, 3, 4, 5}"
              class="set-input"
            />
          </div>
        </el-col>
        <el-col :span="8">
          <div class="input-group">
            <label>目标集合(B)：</label>
            <el-input
              v-model="setBInput"
              placeholder="例如：{a, b, c, d}"
              class="set-input"
            />
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20" style="margin-top: 1rem;">
        <el-col :span="16">
          <div class="input-group">
            <label>函数(F)：</label>
            <el-input
              v-model="functionInput"
              type="textarea"
              :rows="2"
              placeholder="例如：{&lt;1, a&gt;, &lt;2, b&gt;, &lt;3, c&gt;}"
              class="function-input"
            />
          </div>
        </el-col>
      </el-row>
      <div class="input-hint">
        <el-text type="info" size="small">
          集合格式：{element1, element2, element3}
          <br>
          整数示例：{1, 2, 3} 字符示例：{a, b, c}
          <br>
          函数格式：{&lt;元素1, 映射1&gt;, &lt;元素2, 映射2&gt;}
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

      <!-- 运算结果区域 -->
      <div v-if="results.length > 0" class="results-section">
        <el-divider content-position="left">函数性质分析结果</el-divider>
        <div class="results-content">
          <div v-for="(result, index) in results" :key="index" class="result-item">
            <!-- 集合和函数基本信息 -->
            <div class="result-basic">
              <h4>输入集合和函数：</h4>
              <math-renderer
                :formula="result.formula"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
            </div>

            <!-- 函数判断结果 -->
            <div class="function-result">
              <h4>函数性质判断：</h4>
              <el-alert
                :type="result.function ? 'success' : 'error'"
                :title="result.function ? '该关系是函数' : '该关系不是函数'"
                :closable="false"
                show-icon
                class="function-alert"
              />
            </div>

            <!-- 关系矩阵 -->
            <div v-if="result.relationMatrix" class="matrix-result">
              <h4>关系矩阵：</h4>
              <math-renderer
                :formula="result.relationMatrix"
                :type="'mathjax'"
                :display-mode="true"
                class="matrix-formula"
              />
            </div>

            <!-- 关系图 -->
            <div v-if="result.relationGraphUrl" class="graph-result">
              <h4>关系图：</h4>
              <div class="graph-container">
                <el-image
                  :src="result.relationGraphUrl"
                  :preview-src-list="[result.relationGraphUrl]"
                  fit="contain"
                  style="max-width: 100%; max-height: 300px;"
                  class="relation-graph"
                />
              </div>
            </div>

            <!-- 单射性判断 -->
            <div v-if="result.injectionResult" class="property-result">
              <h4>单射性判断：</h4>
              <el-alert
                :type="result.injectionResult.isProperty ? 'success' : 'warning'"
                :title="result.injectionResult.description"
                :closable="false"
                show-icon
              >
                <template v-if="result.injectionResult.counterExample" #default>
                  <div class="counter-example">
                    反例：元素 {{ result.injectionResult.counterExample }}
                  </div>
                </template>
              </el-alert>
            </div>

            <!-- 满射性判断 -->
            <div v-if="result.surjectionResult" class="property-result">
              <h4>满射性判断：</h4>
              <el-alert
                :type="result.surjectionResult.isProperty ? 'success' : 'warning'"
                :title="result.surjectionResult.description"
                :closable="false"
                show-icon
              >
                <template v-if="result.surjectionResult.counterExample" #default>
                  <div class="counter-example">
                    反例：元素 {{ result.surjectionResult.counterExample }}
                  </div>
                </template>
              </el-alert>
            </div>

            <!-- 双射性判断 -->
            <div v-if="result.bijectionResult" class="property-result">
              <h4>双射性判断：</h4>
              <el-alert
                :type="result.bijectionResult.isProperty ? 'success' : 'warning'"
                :title="result.bijectionResult.description"
                :closable="false"
                show-icon
              >
                <template v-if="result.bijectionResult.counterExample" #default>
                  <div class="counter-example">
                    反例：元素 {{ result.bijectionResult.counterExample }}
                  </div>
                </template>
              </el-alert>
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
const setAInput = ref('')
const setBInput = ref('')
const functionInput = ref('')
const elementType = ref('char')
const feedback = ref([])
const results = ref([])
const counter = ref(0)

// 性质判断选项
const options = ref({
  checkInjection: true,
  checkSurjection: true,
  checkBijection: true,
  showRelationMatrix: false,
  showRelationGraph: false
})

// 示例数据
const examples = {
  example7_1_1: {
    setA: '{1, 2, 3, 4, 5}',
    setB: '{a, b, c, d}',
    function: '{<1, a>, <2, a>, <3, c>, <3, d>, <4, b>, <5, b>}',
    elementType: 'char',
    options: { checkInjection: true, checkSurjection: true, checkBijection: true, showRelationMatrix: false, showRelationGraph: false }
  },
  example7_1_2: {
    setA: '{1, 2, 3, 4, 5}',
    setB: '{a, b, c, d}',
    function: '{<1, a>, <2, b>, <3, c>, <4, d>}',
    elementType: 'char',
    options: { checkInjection: true, checkSurjection: true, checkBijection: true, showRelationMatrix: false, showRelationGraph: false }
  },
  example7_1_3: {
    setA: '{1, 2, 3, 4, 5}',
    setB: '{a, b, c, d}',
    function: '{<1, a>, <2, b>, <3, c>, <3, d>}',
    elementType: 'char',
    options: { checkInjection: true, checkSurjection: true, checkBijection: true, showRelationMatrix: false, showRelationGraph: false }
  },
  example7_1_4: {
    setA: '{1, 2, 3, 4, 5}',
    setB: '{a, b, c, d}',
    function: '{<1, a>, <2, b>, <3, b>, <4, d>, <5, c>}',
    elementType: 'char',
    options: { checkInjection: true, checkSurjection: true, checkBijection: true, showRelationMatrix: false, showRelationGraph: false }
  },
  problem7_7_1: {
    setA: '{1, 2, 3, 4, 5}',
    setB: '{a, b, c, d, e}',
    function: '{<1, b>, <2, d>, <3, b>, <4, d>, <5, a>}',
    elementType: 'char',
    options: { checkInjection: true, checkSurjection: true, checkBijection: true, showRelationMatrix: false, showRelationGraph: false }
  },
  problem7_7_2: {
    setA: '{1, 2, 3, 4, 5}',
    setB: '{a, b, c, d, e}',
    function: '{<1, a>, <2, d>, <3, c>, <4, e>, <5, b>}',
    elementType: 'char',
    options: { checkInjection: true, checkSurjection: true, checkBijection: true, showRelationMatrix: false, showRelationGraph: false }
  },
  problem7_7_3: {
    setA: '{1, 2, 3, 4, 5}',
    setB: '{a, b, c, d, e}',
    function: '{<1, d>, <2, c>, <3, e>, <4, e>, <5, d>}',
    elementType: 'char',
    options: { checkInjection: true, checkSurjection: true, checkBijection: true, showRelationMatrix: false, showRelationGraph: false }
  },
  problem7_7_4: {
    setA: '{1, 2, 3, 4, 5}',
    setB: '{a, b, c, d, e}',
    function: '{<1, d>, <2, e>, <3, b>, <4, c>, <5, a>}',
    elementType: 'char',
    options: { checkInjection: true, checkSurjection: true, checkBijection: true, showRelationMatrix: false, showRelationGraph: false }
  }
}

// 事件处理函数
const emit = defineEmits(['close', 'function-property-result'])

const startAnalysis = async () => {
  if (!setAInput.value.trim() || !setBInput.value.trim() || !functionInput.value.trim()) {
    ElMessage.warning('请输入集合A、集合B和函数F')
    return
  }

  // 检查是否至少选择了一个性质判断
  const hasSelectedProperty = options.value.checkInjection || options.value.checkSurjection || options.value.checkBijection || options.value.showRelationMatrix || options.value.showRelationGraph
  if (!hasSelectedProperty) {
    ElMessage.warning('请至少选择一种函数性质或显示选项')
    return
  }

  // 清空之前的反馈和结果
  feedback.value = []
  results.value = []

  try {
    const request = {
      setAString: setAInput.value.trim(),
      setBString: setBInput.value.trim(),
      functionString: functionInput.value.trim(),
      intTypeElement: elementType.value === 'int',
      ...options.value
    }

    console.log('FunctionPropertyInterface: 开始函数性质分析', request)

    const response = await callBackendApi('/function-property/analyze', {
      method: 'POST',
      body: JSON.stringify(request)
    })

    console.log('FunctionPropertyInterface: 收到分析结果', response)

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
        message: '函数性质分析完成'
      })

      // 发送结果到主界面
      emit('function-property-result', result)

      ElMessage.success('函数性质分析完成')
    } else {
      feedback.value.push({
        formula: 'A = ?, B = ?, F = ?',
        type: 'error',
        message: response.errorMessage || '函数性质分析失败'
      })
    }
  } catch (error) {
    console.error('函数性质分析失败:', error)
    feedback.value.push({
      formula: 'A = ?, B = ?, F = ?',
      type: 'error',
      message: `分析失败: ${error.message}`
    })
  }
}

const generateRandomFunction = async () => {
  try {
    console.log('FunctionPropertyInterface: 开始生成随机函数')

    const response = await callBackendApi('/function-property/generate', {
      method: 'GET'
    })

    console.log('FunctionPropertyInterface: 随机函数生成结果', response)

    if (response.success) {
      setAInput.value = response.setAString
      setBInput.value = response.setBString
      functionInput.value = response.functionString
      elementType.value = response.intTypeElement ? 'int' : 'char'

      // 设置选项
      Object.assign(options.value, response.options)

      // 清除之前的结果
      feedback.value = []
      results.value = []

      ElMessage.success('已生成随机函数')
    } else {
      ElMessage.error('生成随机函数失败: ' + response.message)
    }
  } catch (error) {
    console.error('生成随机函数失败:', error)
    ElMessage.error(`生成随机函数失败: ${error.message}`)
  }
}

const clearInput = () => {
  setAInput.value = ''
  setBInput.value = ''
  functionInput.value = ''
  ElMessage.info('已清空输入')
}

const checkInput = async () => {
  if (!setAInput.value.trim() || !setBInput.value.trim() || !functionInput.value.trim()) {
    ElMessage.warning('请输入集合A、集合B和函数F')
    return
  }

  feedback.value = []

  try {
    const response = await callBackendApi('/function-property/validate', {
      method: 'POST',
      body: JSON.stringify({
        setAString: setAInput.value.trim(),
        setBString: setBInput.value.trim(),
        functionString: functionInput.value.trim(),
        intTypeElement: elementType.value === 'int'
      })
    })

    if (response.valid) {
      feedback.value.push({
        formula: 'A = ?, B = ?, F = ?',
        type: 'success',
        message: response.message
      })
      ElMessage.success('输入格式正确')
    } else {
      feedback.value.push({
        formula: 'A = ?, B = ?, F = ?',
        type: 'error',
        message: response.message
      })
      ElMessage.error('输入格式不正确')
    }
  } catch (error) {
    console.error('检查输入失败:', error)
    feedback.value.push({
      formula: 'A = ?, B = ?, F = ?',
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
    setAInput.value = example.setA
    setBInput.value = example.setB
    functionInput.value = example.function
    elementType.value = example.elementType

    // 设置选项
    Object.assign(options.value, example.options)

    console.log('FunctionPropertyInterface: 加载示例', exampleKey, example)
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
.function-property-interface {
  display: flex;
  flex-direction: column;
  width: 100%;
}

/* 在大屏幕上完全自适应，无最大宽度限制 */
@media (min-width: 1200px) {
  .function-property-interface {
    width: 100%;
    max-width: none;
  }
}

/* 在中等屏幕上设置合理的最大宽度 */
@media (max-width: 1199px) and (min-width: 900px) {
  .function-property-interface {
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
  }
}

/* 在小屏幕上固定宽度，支持水平滚动 */
@media (max-width: 899px) {
  .function-property-interface {
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

.function-input-section {
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

.set-input,
.function-input {
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

.function-result,
.matrix-result,
.graph-result,
.property-result {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.function-alert {
  margin: 1rem 0;
}

.matrix-formula {
  margin: 1rem 0;
  font-size: 1.1rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  overflow-x: auto;
}

.graph-container {
  margin: 1rem 0;
  text-align: center;
}

.relation-graph {
  border: 1px solid #dee2e6;
  border-radius: 4px;
}

.counter-example {
  margin-top: 0.5rem;
  font-weight: 500;
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

  .matrix-formula {
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