<template>
  <div class="relation-closure-interface">
    <!-- 按钮操作区域 -->
    <div class="button-section">
      <!-- 第一行按钮：开始运算、生成随机关系、清除输入、合法性检查 -->
      <el-row :gutter="20" class="button-row">
        <el-col :span="4">
          <el-button type="primary" @click="startCalculation">
            <el-icon><Check /></el-icon>
            关系闭包运算(Y)
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
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem6_25')">例题6.25</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem6_26')">例题6.26</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem6_27')">例题6.27</el-button>
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

      <!-- 闭包选项设置 -->
      <el-divider content-position="left">选择要计算的关系闭包</el-divider>
      <el-row :gutter="20">
        <el-col :span="4">
          <el-checkbox v-model="options.calculateReflexiveClosure" size="large">
            自反闭包
          </el-checkbox>
        </el-col>
        <el-col :span="4">
          <el-checkbox v-model="options.calculateSymmetricClosure" size="large">
            对称闭包
          </el-checkbox>
        </el-col>
        <el-col :span="4">
          <el-checkbox v-model="options.calculateTransitiveClosure" size="large">
            传递闭包
          </el-checkbox>
        </el-col>
        <el-col :span="4">
          <el-checkbox v-model="options.calculateEquivalenceClosure" size="large">
            等价闭包
          </el-checkbox>
        </el-col>
      </el-row>

      <!-- 传递闭包算法选择 -->
      <el-divider content-position="left">传递闭包算法选择</el-divider>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-radio-group v-model="transitiveAlgorithm" size="large">
            <el-radio label="warshall">Warshall算法</el-radio>
            <el-radio label="matrix">矩阵逻辑积</el-radio>
          </el-radio-group>
        </el-col>
        <el-col :span="6">
          <el-checkbox v-model="options.showTransitiveDetails" size="large">
            显示详细步骤
          </el-checkbox>
        </el-col>
      </el-row>

      <!-- 显示选项 -->
      <el-divider content-position="left">显示选项</el-divider>
      <el-row :gutter="20">
        <el-col :span="4">
          <el-checkbox v-model="options.showRelationMatrix" size="large">
            显示关系矩阵
          </el-checkbox>
        </el-col>
        <el-col :span="4">
          <el-checkbox v-model="options.showRelationGraph" size="large">
            显示关系图
          </el-checkbox>
        </el-col>
      </el-row>
    </div>

    <!-- 关系输入区域 -->
    <div class="relation-input-section">
      <el-divider content-position="left">集合和关系的输入</el-divider>
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
              placeholder="例如：{&lt;1, 2&gt;, &lt;2, 3&gt;, &lt;3, 4&gt;}"
              class="relation-input"
            />
          </div>
        </el-col>
      </el-row>
      <div class="input-hint">
        <el-text type="info" size="small">
          集合格式：{element1, element2, element3}
          <br>
          关系格式：{&lt;from1, to1&gt;, &lt;from2, to2&gt;}
          <br>
          整数示例：{1, 2, 3} 关系示例：{&lt;1, 2&gt;, &lt;2, 3&gt;}
          <br>
          字符示例：{a, b, c} 关系示例：{&lt;a, b&gt;, &lt;b, c&gt;}
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
        <el-divider content-position="left">关系闭包计算结果</el-divider>
        <div class="results-content">
          <div v-for="(result, index) in results" :key="index" class="result-item">

            <!-- 基本信息显示 -->
            <div class="result-basic">
              <h4>输入信息：</h4>
              <math-renderer
                :formula="result.formula"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />

              <!-- 关系矩阵 -->
              <div v-if="result.relationMatrix" class="relation-matrix">
                <h5>关系矩阵：</h5>
                <math-renderer
                  :formula="'M_R = ' + result.relationMatrix"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="matrix-formula"
                />
              </div>

              <!-- 关系图 -->
              <div v-if="result.relationGraphUrl" class="relation-graph">
                <h5>关系图：</h5>
                <img :src="result.relationGraphUrl" alt="关系图" class="relation-graph-image" />
              </div>
            </div>

            <!-- 自反闭包结果 -->
            <div v-if="result.reflexiveClosureResult" class="closure-result">
              <h4>自反闭包 r(R)：</h4>
              <math-renderer
                :formula="'r(R) = ' + result.reflexiveClosureResult.closureLaTeX"
                :type="'mathjax'"
                :display-mode="true"
                class="closure-formula"
              />
              <div v-if="result.reflexiveClosureResult.closureMatrix">
                <h5>闭包矩阵：</h5>
                <math-renderer
                  :formula="'M_{r(R)} = ' + result.reflexiveClosureResult.closureMatrix"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="matrix-formula"
                />
              </div>
              <div v-if="result.reflexiveClosureResult.closureGraphUrl">
                <h5>闭包关系图：</h5>
                <img :src="result.reflexiveClosureResult.closureGraphUrl" alt="自反闭包关系图" class="closure-graph-image" />
              </div>
            </div>

            <!-- 对称闭包结果 -->
            <div v-if="result.symmetricClosureResult" class="closure-result">
              <h4>对称闭包 s(R)：</h4>
              <math-renderer
                :formula="'s(R) = ' + result.symmetricClosureResult.closureLaTeX"
                :type="'mathjax'"
                :display-mode="true"
                class="closure-formula"
              />
              <div v-if="result.symmetricClosureResult.closureMatrix">
                <h5>闭包矩阵：</h5>
                <math-renderer
                  :formula="'M_{s(R)} = ' + result.symmetricClosureResult.closureMatrix"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="matrix-formula"
                />
              </div>
              <div v-if="result.symmetricClosureResult.closureGraphUrl">
                <h5>闭包关系图：</h5>
                <img :src="result.symmetricClosureResult.closureGraphUrl" alt="对称闭包关系图" class="closure-graph-image" />
              </div>
            </div>

            <!-- 传递闭包结果 -->
            <div v-if="result.transitiveClosureResult" class="transitive-closure-result">
              <h4>传递闭包 t(R) ({{ result.transitiveClosureResult.algorithmUsed }})：</h4>
              <math-renderer
                :formula="'t(R) = ' + result.transitiveClosureResult.closureLaTeX"
                :type="'mathjax'"
                :display-mode="true"
                class="closure-formula"
              />
              <div v-if="result.transitiveClosureResult.closureMatrix">
                <h5>闭包矩阵：</h5>
                <math-renderer
                  :formula="'M_{t(R)} = ' + result.transitiveClosureResult.closureMatrix"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="matrix-formula"
                />
              </div>

              <!-- 传递闭包详细计算过程 -->
              <div v-if="result.transitiveClosureResult.detailMatrices && result.transitiveClosureResult.detailMatrices.length > 0" class="transitive-details">
                <h5>传递闭包计算过程：</h5>
                <div v-for="(matrix, idx) in result.transitiveClosureResult.detailMatrices" :key="idx" class="detail-step">
                  <p class="step-description">{{ result.transitiveClosureResult.detailDescriptions[idx] }}：</p>
                  <math-renderer
                    :formula="matrix"
                    :type="'mathjax'"
                    :display-mode="true"
                    class="detail-matrix"
                  />
                </div>
              </div>

              <div v-if="result.transitiveClosureResult.closureGraphUrl">
                <h5>闭包关系图：</h5>
                <img :src="result.transitiveClosureResult.closureGraphUrl" alt="传递闭包关系图" class="closure-graph-image" />
              </div>
            </div>

            <!-- 等价闭包结果 -->
            <div v-if="result.equivalenceClosureResult" class="closure-result">
              <h4>等价闭包 tsr(R)：</h4>
              <math-renderer
                :formula="'tsr(R) = ' + result.equivalenceClosureResult.closureLaTeX"
                :type="'mathjax'"
                :display-mode="true"
                class="closure-formula"
              />
              <div v-if="result.equivalenceClosureResult.closureMatrix">
                <h5>闭包矩阵：</h5>
                <math-renderer
                  :formula="'M_{tsr(R)} = ' + result.equivalenceClosureResult.closureMatrix"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="matrix-formula"
                />
              </div>
              <div v-if="result.equivalenceClosureResult.closureGraphUrl">
                <h5>闭包关系图：</h5>
                <img :src="result.equivalenceClosureResult.closureGraphUrl" alt="等价闭包关系图" class="closure-graph-image" />
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
const relationRInput = ref('')
const elementType = ref('char')
const feedback = ref([])
const results = ref([])
const counter = ref(0)
const transitiveAlgorithm = ref('warshall') // 默认选择Warshall算法

// 闭包选项
const options = ref({
  calculateReflexiveClosure: true,
  calculateSymmetricClosure: true,
  calculateTransitiveClosure: true,
  calculateEquivalenceClosure: true,
  showTransitiveDetails: false,
  showRelationMatrix: false,
  showRelationGraph: false
})

// 示例数据
const examples = {
  problem6_25: {
    setA: '{1, 2, 3, 4}',
    relationR: '{<4, 3>, <2, 1>, <3, 4>, <1, 1>, <2, 3>}',
    elementType: 'char',
    transitiveAlgorithm: 'warshall', // 默认算法
    options: {
      calculateReflexiveClosure: true,
      calculateSymmetricClosure: true,
      calculateTransitiveClosure: true,
      calculateEquivalenceClosure: true,
      showTransitiveDetails: false,
      showRelationMatrix: false,
      showRelationGraph: false
    }
  },
  problem6_26: {
    setA: '{1, 2, 3, 4}',
    relationR: '{<3, 4>, <3, 1>, <4, 3>, <2, 2>, <1, 2>}',
    elementType: 'char',
    transitiveAlgorithm: 'matrix', // 矩阵逻辑积算法
    options: {
      calculateReflexiveClosure: false,
      calculateSymmetricClosure: false,
      calculateTransitiveClosure: true,
      calculateEquivalenceClosure: false,
      showTransitiveDetails: true,
      showRelationMatrix: false,
      showRelationGraph: false
    }
  },
  problem6_27: {
    setA: '{1, 2, 3, 4}',
    relationR: '{<3, 4>, <3, 1>, <4, 3>, <2, 2>, <1, 2>}',
    elementType: 'char',
    transitiveAlgorithm: 'warshall', // Warshall算法
    options: {
      calculateReflexiveClosure: false,
      calculateSymmetricClosure: false,
      calculateTransitiveClosure: true,
      calculateEquivalenceClosure: false,
      showTransitiveDetails: true,
      showRelationMatrix: false,
      showRelationGraph: false
    }
  }
}

// 事件处理函数
const emit = defineEmits(['close', 'relation-closure-result'])

const startCalculation = async () => {
  if (!setAInput.value.trim() || !relationRInput.value.trim()) {
    ElMessage.warning('请输入集合A和关系R')
    return
  }

  // 检查是否至少选择了一个闭包
  const hasSelectedClosure = options.value.calculateReflexiveClosure ||
                           options.value.calculateSymmetricClosure ||
                           options.value.calculateTransitiveClosure ||
                           options.value.calculateEquivalenceClosure
  if (!hasSelectedClosure) {
    ElMessage.warning('请至少选择一种关系闭包')
    return
  }

  // 清空之前的反馈和结果
  feedback.value = []
  results.value = []

  try {
    // 根据选择的传递闭包算法设置相应选项
    const request = {
      setAString: setAInput.value.trim(),
      relationRString: relationRInput.value.trim(),
      intTypeElement: elementType.value === 'int',
      ...options.value,
      useWarshallAlgorithm: transitiveAlgorithm.value === 'warshall'
    }

    console.log('RelationClosureInterface: 开始关系闭包运算', request)

    const response = await callBackendApi('/relation-closure/calculate', {
      method: 'POST',
      body: JSON.stringify(request)
    })

    console.log('RelationClosureInterface: 收到运算结果', response)

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
        message: '关系闭包运算完成'
      })

      // 发送结果到主界面
      emit('relation-closure-result', result)

      ElMessage.success('关系闭包运算完成')
    } else {
      feedback.value.push({
        formula: 'A = ?, R = ?',
        type: 'error',
        message: response.errorMessage || '关系闭包运算失败'
      })
    }
  } catch (error) {
    console.error('关系闭包运算失败:', error)
    feedback.value.push({
      formula: 'A = ?, R = ?',
      type: 'error',
      message: `运算失败: ${error.message}`
    })
  }
}

const generateRandomRelation = async () => {
  try {
    console.log('RelationClosureInterface: 开始生成随机关系')

    const response = await callBackendApi('/relation-closure/generate', {
      method: 'GET'
    })

    console.log('RelationClosureInterface: 随机关系生成结果', response)

    if (response.success) {
      setAInput.value = response.setA
      relationRInput.value = response.relationR
      elementType.value = response.intTypeElement ? 'int' : 'char'

      // 设置选项
      Object.assign(options.value, response.options)

      // 清除之前的结果
      feedback.value = []
      results.value = []

      ElMessage.success('已生成随机关系')
    } else {
      ElMessage.error('生成随机关系失败: ' + response.message)
    }
  } catch (error) {
    console.error('生成随机关系失败:', error)
    ElMessage.error(`生成随机关系失败: ${error.message}`)
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
    const response = await callBackendApi('/relation-closure/validate', {
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

    // 设置传递闭包算法
    if (example.transitiveAlgorithm) {
      transitiveAlgorithm.value = example.transitiveAlgorithm
    }

    // 设置选项
    Object.assign(options.value, example.options)

    console.log('RelationClosureInterface: 加载示例', exampleKey, example)
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
.relation-closure-interface {
  display: flex;
  flex-direction: column;
  width: 100%;
}

/* 在大屏幕上完全自适应，无最大宽度限制 */
@media (min-width: 1200px) {
  .relation-closure-interface {
    width: 100%;
    max-width: none;
  }
}

/* 在中等屏幕上设置合理的最大宽度 */
@media (max-width: 1199px) and (min-width: 900px) {
  .relation-closure-interface {
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
  }
}

/* 在小屏幕上固定宽度，支持水平滚动 */
@media (max-width: 899px) {
  .relation-closure-interface {
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

.relation-matrix,
.relation-graph {
  margin: 1rem 0;
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

.relation-graph-image,
.closure-graph-image {
  max-width: 100%;
  height: auto;
  margin-top: 0.5rem;
  border: 1px solid #dee2e6;
  border-radius: 4px;
}

.closure-result,
.transitive-closure-result {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.closure-formula {
  margin: 1rem 0;
  font-size: 1.1rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  overflow-x: auto;
}

.transitive-details {
  margin-top: 1rem;
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.detail-step {
  margin-bottom: 1.5rem;
}

.step-description {
  color: #374151;
  font-weight: 500;
  margin-bottom: 0.5rem;
}

.detail-matrix {
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

  .closure-formula,
  .matrix-formula,
  .detail-matrix {
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