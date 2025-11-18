<template>
  <div class="operation-property-interface">
    <!-- 按钮操作区域 -->
    <div class="button-section">
      <!-- 第一行按钮：开始分析、生成随机运算、清除输入、合法性检查 -->
      <el-row :gutter="20" class="button-row">
        <el-col :span="4">
          <el-button type="primary" @click="startAnalysis">
            <el-icon><Check /></el-icon>
            开始分析(Y)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="success" @click="generateRandomOperation">
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
          <el-button size="small" @click="loadExample('example10_5')">例题10.5</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem10_9')">例题10.9</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem10_11')">例题10.11</el-button>
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

      <!-- 运算性质选择 -->
      <el-divider content-position="left">选择要判断的运算性质</el-divider>
      <el-row :gutter="20">
        <el-col :span="4">
          <el-checkbox v-model="options.checkCommutative" size="large">
            交换性
          </el-checkbox>
        </el-col>
        <el-col :span="4">
          <el-checkbox v-model="options.checkAssociative" size="large">
            结合性
          </el-checkbox>
        </el-col>
        <el-col :span="4">
          <el-checkbox v-model="options.checkIdempotent" size="large">
            幂等性
          </el-checkbox>
        </el-col>
        <el-col :span="4">
          <el-checkbox v-model="options.checkCancellation" size="large">
            消去律
          </el-checkbox>
        </el-col>
        <el-col :span="4">
          <el-checkbox v-model="options.checkDistributive" size="large">
            分配律
          </el-checkbox>
        </el-col>
        <el-col :span="4">
          <el-checkbox v-model="options.checkAbsorption" size="large">
            吸收律
          </el-checkbox>
        </el-col>
      </el-row>
      <el-row :gutter="20" style="margin-top: 0.5rem;">
        <el-col :span="4">
          <el-checkbox v-model="options.checkInverse" size="large">
            逆元
          </el-checkbox>
        </el-col>
        <el-col :span="4">
          <el-checkbox v-model="options.checkIdentity" size="large">
            单位元
          </el-checkbox>
        </el-col>
        <el-col :span="4">
          <el-checkbox v-model="options.checkZero" size="large">
            零元
          </el-checkbox>
        </el-col>
      </el-row>
    </div>

    <!-- 运算输入区域 -->
    <div class="operation-input-section">
      <el-divider content-position="left">集合和运算符的输入</el-divider>
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="input-group">
            <label>集合(A)：</label>
            <el-input
              v-model="setAInput"
              placeholder="例如：{e, a, b, c}"
              class="set-input"
            />
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20" style="margin-top: 1rem;">
        <el-col :span="12">
          <div class="input-group">
            <label>运算符一(∘,\circ)：</label>
            <el-input
              v-model="operator1Input"
              type="textarea"
              :rows="4"
              placeholder="例如：{&lt;e, e, e&gt;, &lt;e, a, a&gt;, ...}"
              class="operator-input"
            />
          </div>
        </el-col>
        <el-col :span="12">
          <div class="input-group">
            <label>运算符二(*)：</label>
            <el-input
              v-model="operator2Input"
              type="textarea"
              :rows="4"
              placeholder="例如：{&lt;e, e, e&gt;, &lt;e, a, a&gt;, ...} (可选)"
              class="operator-input"
            />
          </div>
        </el-col>
      </el-row>
      <div class="input-hint">
        <el-text type="info" size="small">
          集合格式：{element1, element2, element3}
          <br>
          运算符格式：{&lt;first, second, result&gt;, ...} 其中first和second是运算元，result是运算结果
          <br>
          整数示例：{1, 2, 3} 字符示例：{a, b, c}
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

      <!-- 分析结果区域 -->
      <div v-if="results.length > 0" class="results-section">
        <el-divider content-position="left">运算性质分析结果</el-divider>
        <div class="results-content">
          <div v-for="(result, index) in results" :key="index" class="result-item">
            <!-- 基本信息 -->
            <div class="result-basic">
              <h4>输入信息：</h4>
              <math-renderer
                :formula="result.formula"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
            </div>

            <!-- 运算符表格 -->
            <div class="operation-tables">
              <h4>运算表格：</h4>
              <div v-if="result.operator1Table" class="operation-table">
                <math-renderer
                  :formula="result.operator1Table"
                  :type="'katex'"
                  :display-mode="true"
                  class="operation-formula"
                />
              </div>
              <div v-if="result.operator2Table" class="operation-table">
                <math-renderer
                  :formula="result.operator2Table"
                  :type="'katex'"
                  :display-mode="true"
                  class="operation-formula"
                />
              </div>
            </div>

            <!-- 运算符一的性质 -->
            <div v-if="result.operator1Properties && result.operator1Properties.length > 0" class="operator-properties">
              <h4>运算符一(∘)的性质：</h4>
              <div v-for="property in result.operator1Properties" :key="property.propertyType" class="property-item">
                <div class="property-header">
                  <strong>{{ getPropertyDisplayName(property.propertyType) }}：</strong>
                  <el-tag :type="property.hasProperty ? 'success' : 'danger'">
                    {{ property.hasProperty ? '满足' : '不满足' }}
                  </el-tag>
                </div>
                <div class="property-reason">
                <math-renderer
                  :formula="formatMathExpression(property.reason)"
                  :type="'katex'"
                  :display-mode="false"
                  :inline="true"
                />
              </div>
                <div v-if="property.details" class="property-details">{{ property.details }}</div>
              </div>
            </div>

            <!-- 运算符二的性质 -->
            <div v-if="result.operator2Properties && result.operator2Properties.length > 0" class="operator-properties">
              <h4>运算符二(*)的性质：</h4>
              <div v-for="property in result.operator2Properties" :key="property.propertyType" class="property-item">
                <div class="property-header">
                  <strong>{{ getPropertyDisplayName(property.propertyType) }}：</strong>
                  <el-tag :type="property.hasProperty ? 'success' : 'danger'">
                    {{ property.hasProperty ? '满足' : '不满足' }}
                  </el-tag>
                </div>
                <div class="property-reason">
                <math-renderer
                  :formula="formatMathExpression(property.reason)"
                  :type="'katex'"
                  :display-mode="false"
                  :inline="true"
                />
              </div>
                <div v-if="property.details" class="property-details">{{ property.details }}</div>
              </div>
            </div>

            <!-- 运算符之间的关系性质 -->
            <div v-if="result.relationProperties && result.relationProperties.length > 0" class="relation-properties">
              <h4>运算符之间的关系性质：</h4>
              <div v-for="relation in result.relationProperties" :key="`${relation.relationType}_${relation.direction}`" class="relation-item">
                <div class="relation-header">
                  <strong>{{ getRelationDisplayName(relation.relationType) }} - {{ relation.direction }}：</strong>
                  <el-tag :type="relation.hasRelation ? 'success' : 'danger'">
                    {{ relation.hasRelation ? '满足' : '不满足' }}
                  </el-tag>
                </div>
                <div class="relation-reason">
                <math-renderer
                  :formula="formatMathExpression(relation.reason)"
                  :type="'katex'"
                  :display-mode="false"
                  :inline="true"
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
const setAInput = ref('')
const operator1Input = ref('')
const operator2Input = ref('')
const elementType = ref('char')
const feedback = ref([])
const results = ref([])
const counter = ref(0)

// 运算性质选项
const options = ref({
  checkCommutative: true,
  checkAssociative: true,
  checkIdempotent: true,
  checkCancellation: true,
  checkDistributive: true,
  checkAbsorption: true,
  checkIdentity: true,
  checkZero: true,
  checkInverse: true
})

// 示例数据
const examples = {
  example10_5: {
    setA: '{e, a, b, c}',
    operator1: '{<e, e, e>, <e, a, a>, <e, b, b>, <e, c, c>, <a, e, a>, <a, a, e>, <a, b, c>, <a, c, b>, <b, e, b>, <b, a, c>, <b, b, e>, <b, c, a>, <c, e, c>, <c, a, b>, <c, b, a>, <c, c, e>}',
    operator2: '',
    elementType: 'char',
    options: {
      checkCommutative: false, checkAssociative: false, checkIdempotent: false,
      checkCancellation: false, checkDistributive: false, checkAbsorption: false,
      checkIdentity: false, checkZero: false, checkInverse: false
    }
  },
  problem10_9: {
    setA: '{e, x, y, z}',
    operator1: '{<e, e, e>, <e, x, x>, <e, y, y>, <e, z, z>, <x, e, x>, <x, x, x>, <x, y, e>, <x, z, e>, <y, e, y>, <y, x, e>, <y, y, x>, <y, z, x>, <z, e, z>, <z, x, e>, <z, y, x>, <z, z, x>}',
    operator2: '',
    elementType: 'char',
    options: {
      checkCommutative: false, checkAssociative: true, checkIdempotent: false,
      checkCancellation: false, checkDistributive: false, checkAbsorption: false,
      checkIdentity: false, checkZero: false, checkInverse: true
    }
  },
  problem10_11: {
    setA: '{e, a, b, c}',
    operator1: '{<e, e, e>, <e, a, a>, <e, b, b>, <e, c, c>, <a, e, a>, <a, a, e>, <a, b, c>, <a, c, b>, <b, e, b>, <b, a, c>, <b, b, e>, <b, c, a>, <c, e, c>, <c, a, b>, <c, b, a>, <c, c, e>}',
    operator2: '',
    elementType: 'char',
    options: {
      checkCommutative: true, checkAssociative: true, checkIdempotent: true,
      checkCancellation: true, checkDistributive: false, checkAbsorption: false,
      checkIdentity: true, checkZero: true, checkInverse: true
    }
  }
}

// 事件处理函数
const emit = defineEmits(['close', 'operation-property-result'])

const startAnalysis = async () => {
  if (!setAInput.value.trim() || !operator1Input.value.trim()) {
    ElMessage.warning('请输入集合A和运算符一')
    return
  }

  // 检查是否至少选择了一种性质
  const hasSelectedProperty = Object.values(options.value).some(option => option)
  if (!hasSelectedProperty) {
    ElMessage.warning('请至少选择一种运算性质进行判断')
    return
  }

  // 清空之前的反馈和结果
  feedback.value = []
  results.value = []

  try {
    const request = {
      setAString: setAInput.value.trim(),
      operator1String: operator1Input.value.trim(),
      operator2String: operator2Input.value.trim(),
      intTypeElement: elementType.value === 'int',
      ...options.value
    }

    console.log('OperationPropertyInterface: 开始运算性质分析', request)

    const response = await callBackendApi('/operation-property/analyze', {
      method: 'POST',
      body: JSON.stringify(request)
    })

    console.log('OperationPropertyInterface: 收到分析结果', response)

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
        message: '运算性质分析完成'
      })

      // 发送结果到主界面
      emit('operation-property-result', result)

      ElMessage.success('运算性质分析完成')
    } else {
      feedback.value.push({
        formula: 'A = ?, ∘ = ?, * = ?',
        type: 'error',
        message: response.errorMessage || '运算性质分析失败'
      })
    }
  } catch (error) {
    console.error('运算性质分析失败:', error)
    feedback.value.push({
      formula: 'A = ?, ∘ = ?, * = ?',
      type: 'error',
      message: `分析失败: ${error.message}`
    })
  }
}

const generateRandomOperation = async () => {
  try {
    console.log('OperationPropertyInterface: 开始生成随机运算')

    const response = await callBackendApi('/operation-property/generate', {
      method: 'GET'
    })

    console.log('OperationPropertyInterface: 随机运算生成结果', response)

    if (response.success) {
      setAInput.value = response.setAString
      operator1Input.value = response.operator1String
      operator2Input.value = response.operator2String
      elementType.value = response.intTypeElement ? 'int' : 'char'

      // 设置选项
      Object.assign(options.value, {
        checkCommutative: true,
        checkAssociative: true,
        checkIdempotent: true,
        checkCancellation: true,
        checkDistributive: true,
        checkAbsorption: true,
        checkIdentity: true,
        checkZero: true,
        checkInverse: true
      })

      // 清除之前的结果
      feedback.value = []
      results.value = []

      ElMessage.success('已生成随机运算')
    } else {
      ElMessage.error('生成随机运算失败: ' + response.message)
    }
  } catch (error) {
    console.error('生成随机运算失败:', error)
    ElMessage.error(`生成随机运算失败: ${error.message}`)
  }
}

const clearInput = () => {
  setAInput.value = ''
  operator1Input.value = ''
  operator2Input.value = ''
  ElMessage.info('已清空输入')
}

const checkInput = async () => {
  if (!setAInput.value.trim() || !operator1Input.value.trim()) {
    ElMessage.warning('请输入集合A和运算符一')
    return
  }

  feedback.value = []

  try {
    const response = await callBackendApi('/operation-property/validate', {
      method: 'POST',
      body: JSON.stringify({
        setAString: setAInput.value.trim(),
        operator1String: operator1Input.value.trim(),
        operator2String: operator2Input.value.trim(),
        intTypeElement: elementType.value === 'int'
      })
    })

    if (response.valid) {
      feedback.value.push({
        formula: 'A = ?, ∘ = ?, * = ?',
        type: 'success',
        message: response.message
      })
      ElMessage.success('输入格式正确')
    } else {
      feedback.value.push({
        formula: 'A = ?, ∘ = ?, * = ?',
        type: 'error',
        message: response.message
      })
      ElMessage.error('输入格式不正确')
    }
  } catch (error) {
    console.error('检查输入失败:', error)
    feedback.value.push({
      formula: 'A = ?, ∘ = ?, * = ?',
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
    operator1Input.value = example.operator1
    operator2Input.value = example.operator2
    elementType.value = example.elementType

    // 设置选项
    Object.assign(options.value, example.options)

    console.log('OperationPropertyInterface: 加载示例', exampleKey, example)
    ElMessage.info(`已加载示例：${exampleKey}`)
  }
}

// 辅助函数
const getPropertyDisplayName = (propertyType) => {
  const names = {
    commutative: '交换律',
    associative: '结合律',
    idempotent: '幂等律',
    cancellation: '消去律',
    identity: '单位元',
    zero: '零元',
    inverse: '逆元'
  }
  return names[propertyType] || propertyType
}

const getRelationDisplayName = (relationType) => {
  const names = {
    distributive: '分配律',
    absorption: '吸收律'
  }
  return names[relationType] || relationType
}

// 辅助函数：格式化数学表达式，添加适当的空格
const formatMathExpression = (formula) => {
  if (!formula) return formula

  return formula
    // 在运算符周围添加空格
    .replace(/([a-zA-Z0-9\)}])(\\circ|\\times|\\neq|=|≠|\\leq|\\geq|<|>)([a-zA-Z0-9\({])/g, '$1 $2 $3')
    // 在\circ后特别添加空格
    .replace(/\\circ(?!\s)/g, '\\circ ')
    // 在\neq前后添加空格
    .replace(/\\neq(?!\s)/g, ' \\neq ')
    // 在=前后添加空格
    .replace(/=(?!\s)/g, ' = ')
    // 清理多余的空格
    .replace(/\s+/g, ' ')
    .trim()
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
.operation-property-interface {
  display: flex;
  flex-direction: column;
  width: 100%;
}

/* 在大屏幕上完全自适应，无最大宽度限制 */
@media (min-width: 1200px) {
  .operation-property-interface {
    width: 100%;
    max-width: none;
  }
}

/* 在中等屏幕上设置合理的最大宽度 */
@media (max-width: 1199px) and (min-width: 900px) {
  .operation-property-interface {
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
  }
}

/* 在小屏幕上固定宽度，支持水平滚动 */
@media (max-width: 899px) {
  .operation-property-interface {
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

.operation-input-section {
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

.set-input, .operator-input {
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

.operation-tables {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.operation-table {
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

.operator-properties,
.relation-properties {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.property-item,
.relation-item {
  margin: 1rem 0;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.property-header,
.relation-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}

.property-reason,
.relation-reason {
  font-size: 0.9rem;
  color: #6b7280;
  margin-bottom: 0.25rem;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 0.25rem;
}

.property-reason .math-renderer,
.relation-reason .math-renderer {
  display: inline-flex;
  align-items: center;
}

.property-details {
  font-size: 0.9rem;
  color: #374151;
  font-style: italic;
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