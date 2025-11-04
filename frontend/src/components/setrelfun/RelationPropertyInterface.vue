<template>
  <div class="relation-property-interface">
    <!-- 按钮操作区域 -->
    <div class="button-section">
      <!-- 第一行按钮：关系性质判断、生成随机关系、清除输入、合法性检查 -->
      <el-row :gutter="20" class="button-row">
        <el-col :span="4">
          <el-button type="primary" @click="startAnalysis">
            <el-icon><Check /></el-icon>
            关系性质判断(Y)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="success" @click="generateRandomRelation">
            <el-icon><MagicStick /></el-icon>
            生成随机关系(G)
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
        <el-col :span="6">
          <el-button size="small" @click="loadExample('problem6_13_1')">例题6.13(1)</el-button>
        </el-col>
        <el-col :span="6">
          <el-button size="small" @click="loadExample('problem6_13_2')">例题6.13(2)</el-button>
        </el-col>
        <el-col :span="6">
          <el-button size="small" @click="loadExample('problem6_13_3')">例题6.13(3)</el-button>
        </el-col>
        <el-col :span="6">
          <el-button size="small" @click="loadExample('problem6_13_4')">例题6.13(4)</el-button>
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

      <!-- 关系性质选择 -->
      <el-divider content-position="left">选择要判断的关系性质</el-divider>
      <el-row :gutter="20">
        <el-col :span="4">
          <el-checkbox v-model="properties.checkReflexive" size="large">
            自反性
          </el-checkbox>
        </el-col>
        <el-col :span="4">
          <el-checkbox v-model="properties.checkIrreflexive" size="large">
            反自反性
          </el-checkbox>
        </el-col>
        <el-col :span="4">
          <el-checkbox v-model="properties.checkSymmetric" size="large">
            对称性
          </el-checkbox>
        </el-col>
        <el-col :span="4">
          <el-checkbox v-model="properties.checkAntisymmetric" size="large">
            反对称性
          </el-checkbox>
        </el-col>
        <el-col :span="4">
          <el-checkbox v-model="properties.checkTransitive" size="large">
            传递性
          </el-checkbox>
        </el-col>
      </el-row>

      <!-- 显示选项 -->
      <el-divider content-position="left">显示选项</el-divider>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-checkbox v-model="options.useMatrix" size="large">
            显示关系矩阵
          </el-checkbox>
        </el-col>
        <el-col :span="6">
          <el-checkbox v-model="options.useGraph" size="large">
            显示关系图
          </el-checkbox>
        </el-col>
      </el-row>
    </div>

    <!-- 关系输入区域 -->
    <div class="relation-input-section">
      <el-divider content-position="left">集合和关系输入</el-divider>
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="input-group">
            <label>集合(A)：</label>
            <el-input
              v-model="setAInput"
              placeholder="例如：{1, 2, 3, 4}"
              class="set-input"
            />
          </div>
        </el-col>
        <el-col :span="12">
          <div class="input-group">
            <label>关系(R)：</label>
            <el-input
              v-model="relationRInput"
              type="textarea"
              :rows="2"
              placeholder="例如：{<1, 1>, <2, 2>, <3, 3>, <4, 4>}"
              class="set-input"
            />
          </div>
        </el-col>
      </el-row>
      <div class="input-hint">
        <el-text type="info" size="small">
          集合格式：{element1, element2, element3}
          <br>
          关系格式：{&lt;a, b&gt;, &lt;c, d&gt;, ...}
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
        <el-divider content-position="left">关系性质分析结果</el-divider>
        <div class="results-content">
          <div v-for="(result, index) in results" :key="index" class="result-item">
            <!-- 基本信息 -->
            <div class="result-basic">
              <h4>输入集合和关系：</h4>
              <math-renderer
                :formula="result.formula"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
            </div>

            <!-- 关系矩阵 -->
            <div v-if="result.matrixString" class="matrix-result">
              <h4>关系矩阵：</h4>
              <math-renderer
                :formula="result.matrixString"
                :type="'mathjax'"
                :display-mode="true"
                class="matrix-formula"
              />
            </div>

            <!-- 关系图 -->
            <div v-if="result.graphImageUrl" class="graph-result">
              <h4>关系图：</h4>
              <div class="graph-image-container">
                <img :src="result.graphImageUrl" alt="关系图" class="graph-image" />
              </div>
            </div>

            <!-- 自反性结果 -->
            <div v-if="result.reflexiveResult" class="property-result">
              <h4>自反性分析：</h4>
              <div class="property-content">
                <math-renderer
                  :formula="result.reflexiveResult.explanation"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="property-formula"
                />
                <div v-if="result.reflexiveResult.counterExample" class="counter-example">
                  <el-text type="danger">反例：</el-text>
                  <math-renderer
                    :formula="result.reflexiveResult.counterExample"
                    :type="'mathjax'"
                    :display-mode="true"
                    class="counter-example-formula"
                  />
                </div>
              </div>
            </div>

            <!-- 反自反性结果 -->
            <div v-if="result.irreflexiveResult" class="property-result">
              <h4>反自反性分析：</h4>
              <div class="property-content">
                <math-renderer
                  :formula="result.irreflexiveResult.explanation"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="property-formula"
                />
                <div v-if="result.irreflexiveResult.counterExample" class="counter-example">
                  <el-text type="danger">反例：</el-text>
                  <math-renderer
                    :formula="result.irreflexiveResult.counterExample"
                    :type="'mathjax'"
                    :display-mode="true"
                    class="counter-example-formula"
                  />
                </div>
              </div>
            </div>

            <!-- 对称性结果 -->
            <div v-if="result.symmetricResult" class="property-result">
              <h4>对称性分析：</h4>
              <div class="property-content">
                <math-renderer
                  :formula="result.symmetricResult.explanation"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="property-formula"
                />
                <div v-if="result.symmetricResult.counterExample1" class="counter-example">
                  <el-text type="danger">反例：</el-text>
                  <math-renderer
                    :formula="result.symmetricResult.counterExample1 + ' \\wedge ' + result.symmetricResult.counterExample2"
                    :type="'mathjax'"
                    :display-mode="true"
                    class="counter-example-formula"
                  />
                </div>
              </div>
            </div>

            <!-- 反对称性结果 -->
            <div v-if="result.antisymmetricResult" class="property-result">
              <h4>反对称性分析：</h4>
              <div class="property-content">
                <math-renderer
                  :formula="result.antisymmetricResult.explanation"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="property-formula"
                />
                <div v-if="result.antisymmetricResult.counterExample1" class="counter-example">
                  <el-text type="danger">反例：</el-text>
                  <math-renderer
                    :formula="result.antisymmetricResult.counterExample1 + ' \\wedge ' + result.antisymmetricResult.counterExample2"
                    :type="'mathjax'"
                    :display-mode="true"
                    class="counter-example-formula"
                  />
                </div>
              </div>
            </div>

            <!-- 传递性结果 -->
            <div v-if="result.transitiveResult" class="property-result">
              <h4>传递性分析：</h4>
              <div class="property-content">
                <math-renderer
                  :formula="result.transitiveResult.explanation"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="property-formula"
                />
                <div v-if="result.transitiveResult.counterExample1" class="counter-example">
                  <el-text type="danger">反例：</el-text>
                  <math-renderer
                    :formula="result.transitiveResult.counterExample1 + ' \\wedge ' + result.transitiveResult.counterExample2 + ' \\wedge ' + result.transitiveResult.counterExample3"
                    :type="'mathjax'"
                    :display-mode="true"
                    class="counter-example-formula"
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
import { ref, nextTick } from 'vue'
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
const relationRInput = ref('')
const elementType = ref('char')
const feedback = ref([])
const results = ref([])
const counter = ref(0)

// 关系性质选项
const properties = ref({
  checkReflexive: true,
  checkIrreflexive: true,
  checkSymmetric: true,
  checkAntisymmetric: true,
  checkTransitive: true
})

// 显示选项
const options = ref({
  useMatrix: false,
  useGraph: false
})

// 示例数据
const examples = {
  problem6_13_1: {
    setA: '{1, 2, 3, 4}',
    relationR: '{<1, 1>, <2, 2>, <3, 3>, <4, 4>}',
    elementType: 'int',
    properties: { checkReflexive: true, checkIrreflexive: true, checkSymmetric: true, checkAntisymmetric: true, checkTransitive: true },
    options: { useMatrix: false, useGraph: false }
  },
  problem6_13_2: {
    setA: '{1, 2, 3, 4}',
    relationR: '{<1, 1>, <1, 2>, <2, 1>, <2, 2>, <3, 3>, <4, 2>, <4, 4>}',
    elementType: 'int',
    properties: { checkReflexive: true, checkIrreflexive: true, checkSymmetric: true, checkAntisymmetric: true, checkTransitive: true },
    options: { useMatrix: false, useGraph: false }
  },
  problem6_13_3: {
    setA: '{1, 2, 3, 4}',
    relationR: '{<1, 1>, <2, 4>, <3, 3>, <4, 2>}',
    elementType: 'int',
    properties: { checkReflexive: true, checkIrreflexive: true, checkSymmetric: true, checkAntisymmetric: true, checkTransitive: true },
    options: { useMatrix: false, useGraph: false }
  },
  problem6_13_4: {
    setA: '{1, 2, 3, 4}',
    relationR: '{<1, 2>, <3, 4>, <1, 4>, <3, 2>}',
    elementType: 'int',
    properties: { checkReflexive: true, checkIrreflexive: true, checkSymmetric: true, checkAntisymmetric: true, checkTransitive: true },
    options: { useMatrix: false, useGraph: false }
  }
}

// 事件处理函数
const emit = defineEmits(['close', 'relation-property-result'])

const startAnalysis = async () => {
  if (!setAInput.value || !setAInput.value.trim() || !relationRInput.value || !relationRInput.value.trim()) {
    ElMessage.warning('请输入集合A和关系R')
    return
  }

  // 检查是否至少选择了一个性质
  const hasSelectedProperty = properties.value && Object.values(properties.value).some(property => property)
  if (!hasSelectedProperty) {
    ElMessage.warning('请至少选择一种关系性质进行判断')
    return
  }

  // 清空之前的反馈和结果
  feedback.value = []
  results.value = []

  try {
    const request = {
      setAString: setAInput.value.trim(),
      relationRString: relationRInput.value.trim(),
      intTypeElement: elementType.value === 'int',
      ...(properties.value || {}),
      ...(options.value || {})
    }

    console.log('RelationPropertyInterface: 开始关系性质分析', request)

    const response = await callBackendApi('/relation-property/analyze', {
      method: 'POST',
      body: JSON.stringify(request)
    })

    console.log('RelationPropertyInterface: 收到分析结果', response)

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
        message: '关系性质分析完成'
      })

      // 发送结果到主界面
      emit('relation-property-result', result)

      ElMessage.success('关系性质分析完成')
    } else {
      feedback.value.push({
        formula: 'A = ?, R = ?',
        type: 'error',
        message: response.errorMessage || '关系性质分析失败'
      })
    }
  } catch (error) {
    console.error('关系性质分析失败:', error)
    feedback.value.push({
      formula: 'A = ?, R = ?',
      type: 'error',
      message: `分析失败: ${error.message}`
    })
  }
}

const generateRandomRelation = async () => {
  try {
    console.log('RelationPropertyInterface: 开始生成随机关系')

    const response = await callBackendApi('/relation-property/generate', {
      method: 'GET'
    })

    console.log('RelationPropertyInterface: 随机关系生成结果', response)

    if (response.success) {
      setAInput.value = response.setAString
      relationRInput.value = response.relationRString
      elementType.value = response.intTypeElement ? 'int' : 'char'

      // 清除之前的结果
      feedback.value = []
      results.value = []

      ElMessage.success('已生成随机关系')
    } else {
      ElMessage.error('生成随机关系失败: ' + response.errorMessage)
    }
  } catch (error) {
    console.error('生成随机关系失败:', error)
    ElMessage.error(`生成随机关系失败: ${error.message}`)
  }
}

const clearInput = () => {
  if (setAInput.value) setAInput.value = ''
  if (relationRInput.value) relationRInput.value = ''
  ElMessage.info('已清空输入')
}

const checkInput = async () => {
  if (!setAInput.value || !setAInput.value.trim() || !relationRInput.value || !relationRInput.value.trim()) {
    ElMessage.warning('请输入集合A和关系R')
    return
  }

  feedback.value = []

  try {
    const response = await callBackendApi('/relation-property/validate', {
      method: 'POST',
      body: JSON.stringify({
        setAString: setAInput.value.trim(),
        relationRString: relationRInput.value.trim(),
        intTypeElement: elementType.value === 'int'
      })
    })

    if (response.valid) {
      feedback.value.push({
        formula: 'A = ?, R = ?',
        type: 'success',
        message: response.message
      })
      ElMessage.success('输入格式正确')
    } else {
      feedback.value.push({
        formula: 'A = ?, R = ?',
        type: 'error',
        message: response.message
      })
      ElMessage.error('输入格式不正确')
    }
  } catch (error) {
    console.error('检查输入失败:', error)
    feedback.value.push({
      formula: 'A = ?, R = ?',
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
    console.log('设置前 - setAInput.value:', setAInput.value, 'relationRInput.value:', relationRInput.value)

    // 直接设置值，不使用nextTick
    setAInput.value = example.setA
    relationRInput.value = example.relationR
    elementType.value = example.elementType

    console.log('设置后 - setAInput.value:', setAInput.value, 'relationRInput.value:', relationRInput.value)
    console.log('示例数据 relationR:', example.relationR)

    // 设置选项
    Object.assign(properties.value, example.properties)
    Object.assign(options.value, example.options)

    // 强制触发响应式更新
    nextTick(() => {
      console.log('nextTick中 - setAInput.value:', setAInput.value, 'relationRInput.value:', relationRInput.value)
      console.log('RelationPropertyInterface: 加载示例', exampleKey, example)
      ElMessage.info(`已加载示例：${exampleKey}`)
    })
  } else {
    console.error('示例不存在:', exampleKey)
    ElMessage.error(`示例不存在：${exampleKey}`)
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
.relation-property-interface {
  display: flex;
  flex-direction: column;
  width: 100%;
}

/* 在大屏幕上完全自适应，无最大宽度限制 */
@media (min-width: 1200px) {
  .relation-property-interface {
    width: 100%;
    max-width: none;
  }
}

/* 在中等屏幕上设置合理的最大宽度 */
@media (max-width: 1199px) and (min-width: 900px) {
  .relation-property-interface {
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
  }
}

/* 在小屏幕上固定宽度，支持水平滚动 */
@media (max-width: 899px) {
  .relation-property-interface {
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

.relation-input-section {
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

.set-input {
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

.matrix-result,
.graph-result {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
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

.graph-image-container {
  margin-top: 1rem;
  text-align: center;
}

.graph-image {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.property-result {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.property-content {
  margin-top: 1rem;
}

.property-formula {
  margin: 1rem 0;
  font-size: 1.1rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  overflow-x: auto;
}

.counter-example {
  margin-top: 1rem;
  padding: 1rem;
  background: #fef2f2;
  border-radius: 4px;
  border: 1px solid #fecaca;
}

.counter-example-formula {
  margin: 0.5rem 0;
  font-size: 1rem;
  padding: 0.5rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #fecaca;
  overflow-x: auto;
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

  .property-formula,
  .counter-example-formula {
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