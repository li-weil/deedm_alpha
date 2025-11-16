<template>
  <div class="equivalence-interface">
    <!-- 按钮操作区域 -->
    <div class="button-section">
      <!-- 第一行按钮：开始运算、生成随机集合、清除输入、合法性检查 -->
      <el-row :gutter="20" class="button-row">
        <el-col :span="4">
          <el-button type="primary" @click="startCalculation">
            <el-icon><Check /></el-icon>
            等价关系计算(Y)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="success" @click="generateRandomRelation">
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
          <el-button size="small" @click="loadExample('problem6_29_1')">例题6.29(1)</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem6_29_2')">例题6.29(2)</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem6_29_3')">例题6.29(3)</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem6_29_4')">例题6.29(4)</el-button>
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

      <!-- 运算选项设置 -->
      <el-divider content-position="left">选择要进行的等价关系计算</el-divider>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-checkbox v-model="options.calculateEquivClosure" size="large">
            等价关系闭包
          </el-checkbox>
        </el-col>
        <el-col :span="6">
          <el-checkbox v-model="options.calculateEquivalenceClasses" size="large">
            求等价类
          </el-checkbox>
        </el-col>
        <el-col :span="6">
          <el-checkbox v-model="options.calculateQuotientSet" size="large">
            求商集
          </el-checkbox>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 1rem;">
        <el-col :span="6">
          <el-checkbox v-model="options.showRelationMatrix" size="large">
            显示关系矩阵
          </el-checkbox>
        </el-col>
        <el-col :span="6">
          <el-checkbox v-model="options.showRelationGraph" size="large">
            显示关系图
          </el-checkbox>
        </el-col>
      </el-row>
    </div>

    <!-- 集合和关系输入区域 -->
    <div class="input-section">
      <el-divider content-position="left">输入集合和关系元素（序偶用&lt;a, b&gt;格式表示）</el-divider>
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
              class="relation-input"
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

      <!-- 运算结果区域 -->
      <div v-if="results.length > 0" class="results-section">
        <el-divider content-position="left">等价关系分析结果</el-divider>
        <div class="results-content">
          <div v-for="(result, index) in results" :key="index" class="result-item">

            <!-- 基本信息结果 -->
            <div class="result-basic">
              <h4>输入信息：</h4>
              <math-renderer
                :formula="result.formula"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
            </div>

            <!-- 关系性质分析结果 -->
            <div class="properties-result">
              <h4>关系性质分析：</h4>
              <div class="property-item">
                <el-tag :type="result.isReflexive ? 'success' : 'danger'" size="large">
                  自反性: {{ result.isReflexive ? '是' : '否' }}
                </el-tag>
                <math-renderer
                  v-if="result.reflexiveResult"
                  :formula="result.reflexiveResult"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="property-formula"
                />
              </div>

              <div class="property-item">
                <el-tag :type="result.isSymmetric ? 'success' : 'danger'" size="large">
                  对称性: {{ result.isSymmetric ? '是' : '否' }}
                </el-tag>
                <math-renderer
                  v-if="result.symmetricResult"
                  :formula="result.symmetricResult"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="property-formula"
                />
              </div>

              <div class="property-item">
                <el-tag :type="result.isTransitive ? 'success' : 'danger'" size="large">
                  传递性: {{ result.isTransitive ? '是' : '否' }}
                </el-tag>
                <math-renderer
                  v-if="result.transitiveResult"
                  :formula="result.transitiveResult"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="property-formula"
                />
              </div>

              <div class="property-item">
                <el-tag :type="result.isEquivalenceRelation ? 'success' : 'warning'" size="large">
                  等价关系: {{ result.isEquivalenceRelation ? '是' : '否' }}
                </el-tag>
              </div>
            </div>

            <!-- 关系矩阵结果 -->
            <div v-if="result.relationMatrix" class="matrix-result">
              <h4>关系矩阵：</h4>
              <math-renderer
                :formula="result.relationMatrix"
                :type="'mathjax'"
                :display-mode="true"
                class="matrix-formula"
              />
            </div>

            <!-- 关系图结果 -->
            <div v-if="result.relationGraphUrl" class="graph-result">
              <h4>关系图：</h4>
              <div class="graph-image">
                <el-image
                  :src="result.relationGraphUrl"
                  :preview-src-list="[result.relationGraphUrl]"
                  fit="contain"
                  style="max-width: 100%; max-height: 300px;"
                >
                  <template #error>
                    <div class="image-error">
                      <el-icon><Picture /></el-icon>
                      <span>关系图加载失败</span>
                    </div>
                  </template>
                </el-image>
              </div>
            </div>

            <!-- 等价关系闭包结果 -->
            <div v-if="result.equivalenceClosure" class="closure-result">
              <h4>等价关系闭包（最小等价关系）：</h4>
              <math-renderer
                :formula="result.equivalenceClosure"
                :type="'mathjax'"
                :display-mode="true"
                class="closure-formula"
              />

              <div v-if="result.equivalenceClosureMatrix" class="closure-matrix">
                <h5>闭包关系矩阵：</h5>
                <math-renderer
                  :formula="result.equivalenceClosureMatrix"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="matrix-formula"
                />
              </div>

              <div v-if="result.equivalenceClosureGraphUrl" class="closure-graph">
                <h5>闭包关系图：</h5>
                <div class="graph-image">
                  <el-image
                    :src="result.equivalenceClosureGraphUrl"
                    :preview-src-list="[result.equivalenceClosureGraphUrl]"
                    fit="contain"
                    style="max-width: 100%; max-height: 300px;"
                  >
                    <template #error>
                      <div class="image-error">
                        <el-icon><Picture /></el-icon>
                        <span>闭包关系图加载失败</span>
                      </div>
                    </template>
                  </el-image>
                </div>
              </div>
            </div>

            <!-- 等价类结果 -->
            <div v-if="result.equivalenceClasses && result.equivalenceClasses.length > 0" class="classes-result">
              <h4>等价类：</h4>
              <div v-for="(eqClass, classIndex) in result.equivalenceClasses" :key="classIndex" class="class-item">
                <math-renderer
                  :formula="'[' + eqClass.element + ']_R'"
                  :type="'mathjax'"
                  :display-mode="false"
                  class="class-title"
                />
                <math-renderer
                  :formula="eqClass.equivalenceClassLaTeX"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="class-formula"
                />
              </div>
            </div>

            <!-- 商集结果 -->
            <div v-if="result.quotientSet" class="quotient-result">
              <h4>商集：</h4>
              <math-renderer
                :formula="`A/R = ${result.quotientSet}`"
                :type="'mathjax'"
                :display-mode="true"
                class="quotient-formula"
              />
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
  Close,
  Picture
} from '@element-plus/icons-vue'
import MathRenderer from '@/components/common/MathRenderer.vue'

// 响应式数据
const setAInput = ref('')
const relationRInput = ref('')
const elementType = ref('char')
const feedback = ref([])
const results = ref([])
const counter = ref(0)

// 运算选项
const options = ref({
  calculateEquivClosure: true,
  calculateEquivalenceClasses: true,
  calculateQuotientSet: true,
  showRelationMatrix: true,
  showRelationGraph: true
})

// 示例数据
const examples = {
  problem6_29_1: {
    setA: '{1, 2, 3, 4}',
    relationR: '{<1, 1>, <2, 2>, <3, 3>, <4, 4>}',
    elementType: 'int',
    options: { calculateEquivClosure: false, calculateEquivalenceClasses: true, calculateQuotientSet: true, showRelationMatrix: true, showRelationGraph: true }
  },
  problem6_29_2: {
    setA: '{1, 2, 3, 4}',
    relationR: '{<1, 2>, <2, 1>, <3, 4>, <4, 3>}',
    elementType: 'int',
    options: { calculateEquivClosure: true, calculateEquivalenceClasses: true, calculateQuotientSet: true, showRelationMatrix: true, showRelationGraph: true }
  },
  problem6_29_3: {
    setA: '{1, 2, 3, 4}',
    relationR: '{<1, 2>, <2, 1>, <3, 4>, <4, 3>, <1, 1>, <2, 2>, <3, 3>, <4, 4>}',
    elementType: 'int',
    options: { calculateEquivClosure: false, calculateEquivalenceClasses: true, calculateQuotientSet: true, showRelationMatrix: true, showRelationGraph: true }
  },
  problem6_29_4: {
    setA: '{1, 2, 3, 4}',
    relationR: '{<1, 2>, <2, 1>, <3, 4>, <4, 3>, <1, 1>, <2, 2>, <3, 3>, <4, 4>}',
    elementType: 'int',
    options: { calculateEquivClosure: false, calculateEquivalenceClasses: true, calculateQuotientSet: true, showRelationMatrix: true, showRelationGraph: true }
  }
}

// 事件处理函数
const emit = defineEmits(['close', 'equivalence-result'])

const startCalculation = async () => {
  if (!setAInput.value.trim() || !relationRInput.value.trim()) {
    ElMessage.warning('请输入集合A和关系R')
    return
  }

  // 检查是否至少选择了一个运算
  const hasSelectedOperation = Object.values(options.value).some(option => option)
  if (!hasSelectedOperation) {
    ElMessage.warning('请至少选择一种等价关系计算')
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
      ...options.value
    }

    console.log('EquivalenceInterface: 开始等价关系计算', request)

    const response = await callBackendApi('/equivalence/calculate', {
      method: 'POST',
      body: JSON.stringify(request)
    })

    console.log('EquivalenceInterface: 收到计算结果', response)

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
        message: '等价关系计算完成'
      })

      // 发送结果到主界面
      emit('equivalence-result', result)

      ElMessage.success('等价关系计算完成')
    } else {
      feedback.value.push({
        formula: 'A = ?, R = ?',
        type: 'error',
        message: response.errorMessage || '等价关系计算失败'
      })
    }
  } catch (error) {
    console.error('等价关系计算失败:', error)
    feedback.value.push({
      formula: 'A = ?, R = ?',
      type: 'error',
      message: `计算失败: ${error.message}`
    })
  }
}

const generateRandomRelation = async () => {
  try {
    console.log('EquivalenceInterface: 开始生成随机等价关系')

    const response = await callBackendApi('/equivalence/generate', {
      method: 'GET'
    })

    console.log('EquivalenceInterface: 随机等价关系生成结果', response)

    if (response.success) {
      setAInput.value = response.setAString
      relationRInput.value = response.relationRString
      elementType.value = response.intTypeElement ? 'int' : 'char'

      // 设置选项
      Object.assign(options.value, response.options)

      // 清除之前的结果
      feedback.value = []
      results.value = []

      ElMessage.success('已生成随机等价关系')
    } else {
      ElMessage.error('生成随机等价关系失败: ' + response.message)
    }
  } catch (error) {
    console.error('生成随机等价关系失败:', error)
    ElMessage.error(`生成随机等价关系失败: ${error.message}`)
  }
}

const clearInput = () => {
  setAInput.value = ''
  relationRInput.value = ''
  ElMessage.info('已清空输入')
}

const checkInput = async () => {
  if (!setAInput.value.trim() || !relationRInput.value.trim()) {
    ElMessage.warning('请输入集合A和关系R')
    return
  }

  feedback.value = []

  try {
    const response = await callBackendApi('/equivalence/validate', {
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
    setAInput.value = example.setA
    relationRInput.value = example.relationR
    elementType.value = example.elementType

    // 设置选项
    Object.assign(options.value, example.options)

    console.log('EquivalenceInterface: 加载示例', exampleKey, example)
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
.equivalence-interface {
  display: flex;
  flex-direction: column;
  width: 100%;
}

/* 在大屏幕上完全自适应，无最大宽度限制 */
@media (min-width: 1200px) {
  .equivalence-interface {
    width: 100%;
    max-width: none;
  }
}

/* 在中等屏幕上设置合理的最大宽度 */
@media (max-width: 1199px) and (min-width: 900px) {
  .equivalence-interface {
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
  }
}

/* 在小屏幕上固定宽度，支持水平滚动 */
@media (max-width: 899px) {
  .equivalence-interface {
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

.set-input,
.relation-input {
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

.properties-result {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.property-item {
  margin: 1rem 0;
  display: flex;
  align-items: center;
  gap: 1rem;
}

.property-formula {
  margin-left: 1rem;
  font-size: 1rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  overflow-x: auto;
}

.matrix-result,
.closure-result,
.classes-result,
.quotient-result {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.matrix-formula,
.closure-formula,
.class-formula,
.quotient-formula {
  margin: 1rem 0;
  font-size: 1.1rem;
  padding: 0.5rem;
  background: #ffffff;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  overflow-x: auto;
}

.class-title {
  font-size: 1.1rem;
  font-weight: 600;
  margin: 0.5rem 0;
  color: #2c3e50;
}

.closure-matrix,
.closure-graph {
  margin-top: 1rem;
}

.graph-image {
  margin: 1rem 0;
  text-align: center;
}

.image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  background: #f5f5f5;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  color: #999;
}

.class-item {
  margin: 1rem 0;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
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

  .property-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }

  .property-formula,
  .matrix-formula,
  .closure-formula,
  .class-formula,
  .quotient-formula {
    font-size: 0.9rem;
  }

  .class-title {
    font-size: 1rem;
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