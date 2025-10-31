<template>
  <div class="relation-operation-interface">
    <!-- 按钮操作区域 -->
    <div class="button-section">
      <!-- 第一行按钮：开始运算、生成随机关系、清除输入、合法性检查 -->
      <el-row :gutter="20" class="button-row">
        <el-col :span="4">
          <el-button type="primary" @click="startCalculation">
            <el-icon><Check /></el-icon>
            关系运算(Y)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="success" @click="generateRandomRelations">
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
          <el-button size="small" @click="loadExample('example6_8')">例题6.8</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem6_10')">习题6.10</el-button>
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
      <el-divider content-position="left">选择要进行的关系运算以及是否显示关系的矩阵和图形表示</el-divider>
      <el-row :gutter="20">
        <el-col :span="5">
          <el-checkbox v-model="options.intersection" size="large">
            关系交
          </el-checkbox>
        </el-col>
        <el-col :span="5">
          <el-checkbox v-model="options.union" size="large">
            关系并
          </el-checkbox>
        </el-col>
        <el-col :span="5">
          <el-checkbox v-model="options.subtract" size="large">
            关系差
          </el-checkbox>
        </el-col>
        <el-col :span="5">
          <el-checkbox v-model="options.inverse" size="large">
            关系逆
          </el-checkbox>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="5">
          <el-checkbox v-model="options.composite" size="large">
            关系复合
          </el-checkbox>
        </el-col>
        <el-col :span="5">
          <el-checkbox v-model="options.invcomp" size="large">
            逆的复合
          </el-checkbox>
        </el-col>
        <el-col :span="5">
          <el-checkbox v-model="options.useMatrix" size="large">
            显示关系矩阵
          </el-checkbox>
        </el-col>
        <el-col :span="5">
          <el-checkbox v-model="options.useGraph" size="large">
            显示关系图
          </el-checkbox>
        </el-col>
      </el-row>
    </div>

    <!-- 集合和关系输入区域 -->
    <div class="input-section">
      <el-divider content-position="left">集合和关系元素输入（也可只输入其中一部分，按住Ctrl+关系元素可拖住鼠标选择）</el-divider>

      <!-- 集合输入 -->
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="input-group">
            <label>集合(A)：</label>
            <el-input
              v-model="setAInput"
              placeholder="例如：{1, 2, 3, 4}"
              class="set-input"
            />
          </div>
        </el-col>
        <el-col :span="8">
          <div class="input-group">
            <label>集合(B)：</label>
            <el-input
              v-model="setBInput"
              placeholder="例如：{a, b, c, d}"
              class="set-input"
            />
          </div>
        </el-col>
        <el-col :span="8">
          <div class="input-group">
            <label>集合(C)：</label>
            <el-input
              v-model="setCInput"
              placeholder="例如：{x, y, z}"
              class="set-input"
            />
          </div>
        </el-col>
      </el-row>

      <!-- 关系R输入 -->
      <el-row :gutter="20" style="margin-top: 1rem;">
        <el-col :span="8">
          <div class="input-group">
            <label>关系(R)：</label>
            <el-input
              v-model="relationRInput"
              type="textarea"
              :rows="2"
              placeholder="例如：{<1, a>, <1, b>, <2, a>}"
              class="relation-input"
            />
          </div>
        </el-col>
        <el-col :span="4">
          <div class="relation-config">
            <label>关系R源集合：</label>
            <el-select v-model="relationRFromSet" placeholder="选择源集合">
              <el-option label="集合A" :value="0" />
              <el-option label="集合B" :value="1" />
              <el-option label="集合C" :value="2" />
            </el-select>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="relation-config">
            <label>关系R目标集合：</label>
            <el-select v-model="relationRToSet" placeholder="选择目标集合">
              <el-option label="集合A" :value="0" />
              <el-option label="集合B" :value="1" />
              <el-option label="集合C" :value="2" />
            </el-select>
          </div>
        </el-col>
      </el-row>

      <!-- 关系S输入 -->
      <el-row :gutter="20" style="margin-top: 1rem;">
        <el-col :span="8">
          <div class="input-group">
            <label>关系(S)：</label>
            <el-input
              v-model="relationSInput"
              type="textarea"
              :rows="2"
              placeholder="例如：{<a, x>, <b, y>, <c, x>}"
              class="relation-input"
            />
          </div>
        </el-col>
        <el-col :span="4">
          <div class="relation-config">
            <label>关系S源集合：</label>
            <el-select v-model="relationSFromSet" placeholder="选择源集合">
              <el-option label="集合A" :value="0" />
              <el-option label="集合B" :value="1" />
              <el-option label="集合C" :value="2" />
            </el-select>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="relation-config">
            <label>关系S目标集合：</label>
            <el-select v-model="relationSToSet" placeholder="选择目标集合">
              <el-option label="集合A" :value="0" />
              <el-option label="集合B" :value="1" />
              <el-option label="集合C" :value="2" />
            </el-select>
          </div>
        </el-col>
      </el-row>

      <div class="input-hint">
        <el-text type="info" size="small">
          集合格式：{element1, element2, element3}
          <br>
          关系格式：{&lt;element1, element2&gt;, &lt;element3, element4&gt;}
          <br>
          整数示例：{1, 2, 3} 字符示例：{a, b, c}
          <br>
          关系示例：{&lt;1, a&gt;, &lt;2, b&gt;, &lt;3, c&gt;}
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
        <el-divider content-position="left">关系运算分析结果</el-divider>
        <div class="results-content">
          <div v-for="(result, index) in results" :key="index" class="result-item">

            <!-- 集合和关系基本信息 -->
            <div class="result-basic">
              <h4>输入集合和关系：</h4>
              <math-renderer
                :formula="result.formula"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
            </div>

            <!-- 关系矩阵显示 -->
            <div v-if="result.relationRMatrix" class="matrix-result">
              <h4>关系R的矩阵：</h4>
              <math-renderer
                :formula="`M_R = ` + result.relationRMatrix"
                :type="'mathjax'"
                :display-mode="true"
                class="matrix-formula"
              />
            </div>

            <div v-if="result.relationSMatrix" class="matrix-result">
              <h4>关系S的矩阵：</h4>
              <math-renderer
                :formula="`M_S = ` + result.relationSMatrix"
                :type="'mathjax'"
                :display-mode="true"
                class="matrix-formula"
              />
            </div>

            <!-- 关系交结果 -->
            <div v-if="result.intersectionResult" class="operation-result">
              <h4>关系交运算：</h4>
              <math-renderer
                :formula="`R \\cap S = ` + result.intersectionResult"
                :type="'mathjax'"
                :display-mode="true"
                class="operation-formula"
              />
            </div>

            <!-- 关系并结果 -->
            <div v-if="result.unionResult" class="operation-result">
              <h4>关系并运算：</h4>
              <math-renderer
                :formula="`R \\cup S = ` + result.unionResult"
                :type="'mathjax'"
                :display-mode="true"
                class="operation-formula"
              />
            </div>

            <!-- 关系差结果 -->
            <div v-if="result.subtractResult" class="operation-result">
              <h4>关系差运算：</h4>
              <math-renderer
                :formula="`R - S = ` + result.subtractResult"
                :type="'mathjax'"
                :display-mode="true"
                class="operation-formula"
              />
            </div>

            <!-- 关系逆结果 -->
            <div v-if="result.inverseRResult || result.inverseSResult" class="operation-result">
              <h4>关系逆运算：</h4>
              <div v-if="result.inverseRResult">
                <math-renderer
                  :formula="`R^{-1} = ` + result.inverseRResult"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="operation-formula"
                />
              </div>
              <div v-if="result.inverseSResult">
                <math-renderer
                  :formula="`S^{-1} = ` + result.inverseSResult"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="operation-formula"
                />
              </div>
            </div>

            <!-- 关系复合结果 -->
            <div v-if="result.compositeResult" class="operation-result">
              <h4>关系复合运算：</h4>
              <math-renderer
                :formula="result.compositeResult"
                :type="'mathjax'"
                :display-mode="true"
                class="operation-formula"
              />
            </div>

            <!-- 逆的复合结果 -->
            <div v-if="result.invcompResult" class="operation-result">
              <h4>逆的复合运算：</h4>
              <math-renderer
                :formula="result.invcompResult"
                :type="'mathjax'"
                :display-mode="true"
                class="operation-formula"
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
  Close
} from '@element-plus/icons-vue'
import MathRenderer from '@/components/common/MathRenderer.vue'

// 定义 emits
const emit = defineEmits(['close', 'relation-operation-result'])

// 响应式数据
const elementType = ref('char')
const setAInput = ref('')
const setBInput = ref('')
const setCInput = ref('')
const relationRInput = ref('')
const relationSInput = ref('')
const relationRFromSet = ref(0)
const relationRToSet = ref(1)
const relationSFromSet = ref(1)
const relationSToSet = ref(2)

const options = ref({
  intersection: false,
  union: false,
  subtract: false,
  inverse: false,
  composite: false,
  invcomp: false,
  useMatrix: false,
  useGraph: false
})

const feedback = ref([])
const results = ref([])

// API调用函数
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

// 开始计算
const startCalculation = async () => {
  try {
    feedback.value = []
    results.value = []

    const requestData = {
      setA: setAInput.value,
      setB: setBInput.value,
      setC: setCInput.value,
      relationR: relationRInput.value,
      relationS: relationSInput.value,
      relationRFromSet: relationRFromSet.value,
      relationRToSet: relationRToSet.value,
      relationSFromSet: relationSFromSet.value,
      relationSToSet: relationSToSet.value,
      intTypeElement: elementType.value === 'int',
      ...options.value
    }

    const result = await callBackendApi('/setrelfun/relation-operation/calculate', {
      body: JSON.stringify(requestData)
    })

    if (result.success) {
      results.value = [result]
      ElMessage.success('关系运算完成')

      // 发送结果给父组件
      emit('relation-operation-result', result)
    } else {
      ElMessage.error(result.errorMessage || '关系运算失败')
    }
  } catch (error) {
    ElMessage.error('运算过程中发生错误: ' + error.message)
  }
}

// 生成随机关系
const generateRandomRelations = async () => {
  try {
    feedback.value = []

    const result = await callBackendApi('/setrelfun/relation-operation/generate')

    if (result.success) {
      setAInput.value = result.setA
      setBInput.value = result.setB
      setCInput.value = result.setC
      relationRInput.value = result.relationR
      relationSInput.value = result.relationS
      relationRFromSet.value = result.relationRFromSet
      relationRToSet.value = result.relationRToSet
      relationSFromSet.value = result.relationSFromSet
      relationSToSet.value = result.relationSToSet
      elementType.value = result.intTypeElement ? 'int' : 'char'

      // 默认选择一些运算
      options.value = {
        intersection: true,
        union: true,
        subtract: true,
        inverse: true,
        composite: true,
        invcomp: true,
        useMatrix: false,
        useGraph: false
      }

      feedback.value = [{
        formula: result.message,
        type: 'success'
      }]

      ElMessage.success('随机关系生成完成')
    } else {
      ElMessage.error(result.message || '生成随机关系失败')
    }
  } catch (error) {
    ElMessage.error('生成过程中发生错误: ' + error.message)
  }
}

// 清除输入
const clearInput = () => {
  setAInput.value = ''
  setBInput.value = ''
  setCInput.value = ''
  relationRInput.value = ''
  relationSInput.value = ''
  relationRFromSet.value = 0
  relationRToSet.value = 1
  relationSFromSet.value = 1
  relationSToSet.value = 2

  // 清除所有运算选项
  options.value = {
    intersection: false,
    union: false,
    subtract: false,
    inverse: false,
    composite: false,
    invcomp: false,
    useMatrix: false,
    useGraph: false
  }

  feedback.value = []
  ElMessage.info('输入已清除')
}

// 清除结果
const clearResults = () => {
  results.value = []
  feedback.value = []
  ElMessage.info('结果已清除')
}

// 合法性检查
const checkInput = async () => {
  try {
    feedback.value = []

    const requestData = {
      setA: setAInput.value,
      setB: setBInput.value,
      setC: setCInput.value,
      relationR: relationRInput.value,
      relationS: relationSInput.value,
      relationRFromSet: relationRFromSet.value,
      relationRToSet: relationRToSet.value,
      relationSFromSet: relationSFromSet.value,
      relationSToSet: relationSToSet.value,
      intTypeElement: elementType.value === 'int',
      ...options.value
    }

    const result = await callBackendApi('/setrelfun/relation-operation/validate', {
      body: JSON.stringify(requestData)
    })

    feedback.value = [{
      formula: result.message,
      type: result.valid ? 'success' : 'warning'
    }]

    if (result.valid) {
      ElMessage.success('输入格式正确，可以进行运算')
    } else {
      ElMessage.warning('输入格式不正确，请检查')
    }
  } catch (error) {
    ElMessage.error('验证过程中发生错误: ' + error.message)
  }
}

// 加载示例
const loadExample = (exampleType) => {
  clearInput()
  feedback.value = []

  if (exampleType === 'example6_8') {
    setAInput.value = '{1, 2, 3, 4}'
    setBInput.value = '{a, b, c, d}'
    setCInput.value = '{x, y, z}'
    relationRInput.value = '{<1, a>, <1, b>, <2, a>, <3, b>, <3, d>, <4, b>}'
    relationSInput.value = '{<a, x>, <b, y>, <b, z>, <c, x>, <d, x>}'
    relationRFromSet.value = 0  // A -> B
    relationRToSet.value = 1
    relationSFromSet.value = 1  // B -> C
    relationSToSet.value = 2
    elementType.value = 'char'

    options.value = {
      intersection: false,
      union: false,
      subtract: false,
      inverse: true,
      composite: true,
      invcomp: true,
      useMatrix: false,
      useGraph: false
    }

    feedback.value = [{
      formula: '已加载例题6.8的数据',
      type: 'info'
    }]
  } else if (exampleType === 'problem6_10') {
    setAInput.value = '{1, 2, 3, 4}'
    setBInput.value = ''
    setCInput.value = ''
    relationRInput.value = '{<1, 1>, <1, 3>, <2, 2>, <2, 4>, <3, 1>, <3, 3>, <4, 2>, <4, 4>}'
    relationSInput.value = '{<1, 1>, <1, 4>, <2, 2>, <3, 3>, <4, 1>, <4, 4>}'
    relationRFromSet.value = 0  // A -> A
    relationRToSet.value = 0
    relationSFromSet.value = 0  // A -> A
    relationSToSet.value = 0
    elementType.value = 'char'

    options.value = {
      intersection: true,
      union: true,
      subtract: true,
      inverse: true,
      composite: true,
      invcomp: true,
      useMatrix: false,
      useGraph: false
    }

    feedback.value = [{
      formula: '已加载习题6.10的数据',
      type: 'info'
    }]
  }
}

// 关闭界面
const closeInterface = () => {
  emit('close')
}
</script>

<style scoped>
.relation-operation-interface {
  display: flex;
  flex-direction: column;
  width: 100%;
}

/* 在大屏幕上完全自适应，无最大宽度限制 */
@media (min-width: 1200px) {
  .relation-operation-interface {
    width: 100%;
    max-width: none;
  }
}

/* 在中等屏幕上设置合理的最大宽度 */
@media (max-width: 1199px) and (min-width: 900px) {
  .relation-operation-interface {
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
  }
}

/* 在小屏幕上固定宽度，支持水平滚动 */
@media (max-width: 899px) {
  .relation-operation-interface {
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

.input-section {
  margin-bottom: 2rem;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.input-group label {
  font-weight: bold;
  color: #606266;
  white-space: nowrap;
}

.set-input,
.relation-input {
  width: 100%;
}

.relation-config {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.relation-config label {
  font-weight: bold;
  color: #606266;
  white-space: nowrap;
  font-size: 0.9rem;
}

.input-hint {
  margin-top: 1rem;
  padding: 0.5rem;
  background-color: #f5f7fa;
  border-radius: 4px;
  border-left: 4px solid #409eff;
}

.content-display-area {
  margin-top: 2rem;
}

.feedback-section,
.results-section {
  margin-bottom: 2rem;
}

.feedback-content {
  max-height: 200px;
  overflow-y: auto;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 1rem;
  background-color: #fafafa;
}

.feedback-item {
  margin-bottom: 0.5rem;
  padding: 0.5rem;
  border-radius: 4px;
  background-color: white;
}

.feedback-formula {
  margin-bottom: 0.25rem;
}

.feedback-message {
  font-size: 0.9rem;
}

.results-content {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 1rem;
  background-color: #fafafa;
}

.result-item {
  margin-bottom: 2rem;
  padding: 1rem;
  border-radius: 4px;
  background-color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.result-basic,
.matrix-result,
.operation-result {
  margin-bottom: 1rem;
  overflow-x: auto;
}

.result-basic h4,
.matrix-result h4,
.operation-result h4 {
  margin-bottom: 0.5rem;
  color: #409eff;
  font-size: 1.1rem;
}

.result-formula,
.matrix-formula,
.operation-formula {
  padding: 0.5rem;
  background-color: #f8f9fa;
  border-radius: 4px;
  border-left: 4px solid #409eff;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .relation-operation-interface {
    padding: 0.5rem;
  }

  .input-group {
    margin-bottom: 0.5rem;
  }

  .relation-config {
    margin-bottom: 0.5rem;
  }
}
</style>