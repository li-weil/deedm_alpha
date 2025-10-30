<template>
  <div class="huffman-tree-interface">
    <!-- 按钮操作区域 -->
    <div class="button-section">
      <!-- 第一行按钮：开始执行、生成随机、清除输入、合法性检查、清除结果、取消 -->
      <el-row :gutter="20" class="button-row">
        <el-col :span="4">
          <el-button type="primary" @click="startExecution">
            <el-icon><Check /></el-icon>
            开始执行(Y)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="success" @click="generateRandomInputs">
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

      <!-- 第二行按钮：示例 -->
      <el-divider content-position="left">《离散数学基础》教材示例展示</el-divider>
      <el-row :gutter="15" class="example-buttons">
        <el-col :span="3">
          <el-button size="small" @click="loadExample('example9_31')">示例9.31</el-button>
        </el-col>
      </el-row>

      <!-- 选项设置 -->
      <el-divider content-position="left">选择进行的计算</el-divider>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-checkbox v-model="options.executeHuffmanAlgorithm" size="large">
            执行Huffman算法
          </el-checkbox>
        </el-col>
        <el-col :span="8">
          <el-checkbox v-model="options.showAlgorithmDetails" size="large">
            显示算法过程
          </el-checkbox>
        </el-col>
        <el-col :span="8">
          <el-checkbox v-model="options.showLeafCodes" size="large">
            显示叶节点编码
          </el-checkbox>
        </el-col>
      </el-row>
    </div>

    <!-- 输入区域 -->
    <div class="input-section">
      <el-divider content-position="left">带权叶节点集合输入</el-divider>
      <el-row :gutter="20">
        <el-col :span="24">
          <div class="input-group">
            <label>带权叶节点集合(L)：</label>
            <el-input
              v-model="leafInput"
              type="textarea"
              :rows="3"
              placeholder="格式：{a[15], b[12], c[25], d[8], e[20], f[6], g[8], h[6]}"
              class="huffman-textarea"
            />
          </div>
        </el-col>
      </el-row>
      <div class="input-hint">
        <el-text type="info" size="small">
          叶节点集合格式：{叶节点id(叶节点label)[权], ..., 叶节点id(叶节点label)[权]}
          <br>
          可以省略叶节点label，但规定label都是小写字母
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
        <el-divider content-position="left">哈夫曼树构造结果</el-divider>
        <div class="results-content">
          <div v-for="(result, index) in results" :key="index" class="result-item">

            <!-- 叶节点集合显示 -->
            <div v-if="result.leafSetLaTeX" class="leaf-set-display">
              <h4>带权叶节点集合：</h4>
              <math-renderer
                :formula="result.leafSetLaTeX"
                :type="'mathjax'"
                :display-mode="true"
                class="leaf-set-formula"
              />
            </div>

            <!-- 算法步骤显示 -->
            <div v-if="result.steps && result.steps.length > 0" class="algorithm-steps">
              <h4>算法构造步骤：</h4>
              <div v-for="step in result.steps" :key="step.step" class="step-item">
                <h5>{{ step.prompt }}</h5>
                <math-renderer
                  :formula="`步骤${step.step}: ${step.forestLaTeX}`"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="step-formula"
                />
                <div v-if="step.forestImageUrl" class="step-image">
                  <img
                    :src="step.forestImageUrl"
                    :alt="`步骤${step.step}的森林图`"
                    class="forest-image"
                    @error="handleImageError"
                  />
                </div>
              </div>
            </div>

            <!-- 最终Huffman树显示 -->
            <div v-if="result.treeImageUrl" class="final-tree">
              <h4>最终构造结果：</h4>
              <div class="tree-image-container">
                <img
                  :src="result.treeImageUrl"
                  alt="最终Huffman树"
                  class="tree-image"
                  @error="handleImageError"
                />
              </div>
            </div>

            <!-- 总权值计算 -->
            <div v-if="result.totalWeightLaTeX" class="weight-calculation">
              <h4>总权值计算：</h4>
              <math-renderer
                :formula="result.totalWeightLaTeX + ' = ' + result.totalWeight"
                :type="'mathjax'"
                :display-mode="true"
                class="weight-formula"
              />
              <p class="weight-result">构造得到的Huffman树总权值是：{{ result.totalWeight }}</p>
            </div>

            <!-- 叶节点编码显示 -->
            <div v-if="result.leafCodes && Object.keys(result.leafCodes).length > 0" class="leaf-codes">
              <h4>带权叶节点的编码如下：</h4>
              <div class="codes-grid">
                <div v-for="(code, label) in result.leafCodes" :key="label" class="code-item">
                  <math-renderer
                    :formula="`${label} : ${code}`"
                    :type="'mathjax'"
                    :display-mode="false"
                    class="code-formula"
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
const leafInput = ref('')
const feedback = ref([])
const results = ref([])
const counter = ref(0)

// 分析选项
const options = ref({
  executeHuffmanAlgorithm: true,
  showAlgorithmDetails: false,
  showLeafCodes: false
})

// 示例数据
const exampleData = {
  example9_31: {
    leafInput: '{a[15], b[12], c[25], d[8], e[20], f[6], g[8], h[6]}',
    options: {
      executeHuffmanAlgorithm: true,
      showAlgorithmDetails: true,
      showLeafCodes: true
    }
  }
}

// 事件处理函数
const emit = defineEmits(['close', 'huffman-tree-constructed'])

const startExecution = async () => {
  if (!leafInput.value.trim()) {
    ElMessage.warning('请输入带权叶节点集合')
    return
  }

  // 清空之前的反馈和结果
  feedback.value = []
  results.value = []

  try {
    const request = {
      leafString: leafInput.value.trim(),
      ...options.value
    }

    console.log('HuffmanTreeInterface: 开始构造Huffman树', request)

    const response = await callBackendApi('/huffman-tree/construct', {
      method: 'POST',
      body: JSON.stringify(request)
    })

    
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
        message: 'Huffman树构造完成'
      })

      // 发送结果到主界面
      emit('huffman-tree-constructed', result)

      ElMessage.success('Huffman树构造完成')
    } else {
      feedback.value.push({
        formula: `L = ${leafInput.value}`,
        type: 'error',
        message: response.errorMessage || 'Huffman树构造失败'
      })
    }
  } catch (error) {
    console.error('Huffman树构造失败:', error)
    feedback.value.push({
      formula: `L = ${leafInput.value}`,
      type: 'error',
      message: `构造失败: ${error.message}`
    })
  }
}

const generateRandomInputs = async () => {
  try {
    console.log('HuffmanTreeInterface: 开始生成随机输入')

    const response = await callBackendApi('/huffman-tree/generate', {
      method: 'GET'
    })

    console.log('HuffmanTreeInterface: 随机输入生成结果', response)

    if (response.success) {
      leafInput.value = response.leafString

      // 设置选项
      Object.assign(options.value, {
        executeHuffmanAlgorithm: response.executeHuffmanAlgorithm,
        showAlgorithmDetails: response.showAlgorithmDetails,
        showLeafCodes: response.showLeafCodes
      })

      // 清除之前的结果
      feedback.value = []
      results.value = []

      ElMessage.success('已生成随机输入')
    } else {
      ElMessage.error('生成随机输入失败: ' + response.message)
    }
  } catch (error) {
    console.error('生成随机输入失败:', error)
    ElMessage.error(`生成随机输入失败: ${error.message}`)
  }
}

const clearInput = () => {
  leafInput.value = ''
  options.value.executeHuffmanAlgorithm = false
  options.value.showAlgorithmDetails = false
  options.value.showLeafCodes = false
  ElMessage.info('已清空输入')
}

const checkInput = async () => {
  if (!leafInput.value.trim()) {
    ElMessage.warning('请输入带权叶节点集合')
    return
  }

  feedback.value = []

  try {
    const response = await callBackendApi('/huffman-tree/validate', {
      method: 'POST',
      body: JSON.stringify({
        leafString: leafInput.value.trim()
      })
    })

    if (response.valid) {
      feedback.value.push({
        formula: `L = ${leafInput.value}`,
        type: 'success',
        message: '带权叶节点集合格式正确'
      })
      ElMessage.success('输入格式正确')
    } else {
      feedback.value.push({
        formula: `L = ${leafInput.value}`,
        type: 'error',
        message: response.message
      })
      ElMessage.error('输入格式不正确')
    }
  } catch (error) {
    console.error('检查输入失败:', error)
    feedback.value.push({
      formula: `L = ${leafInput.value}`,
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
  if (exampleData[exampleKey]) {
    const example = exampleData[exampleKey]

    // 清除之前的结果
    results.value = []
    feedback.value = []
    counter.value = 0

    // 设置示例数据
    leafInput.value = example.leafInput

    // 设置选项
    Object.assign(options.value, example.options)

    console.log('HuffmanTreeInterface: 加载示例', exampleKey, example)
    ElMessage.info(`已加载示例：${exampleKey}`)
  }
}

const handleImageError = (event) => {
  console.error('Huffman树图片加载失败:', event.target.src)
  ElMessage.error('Huffman树图片加载失败')
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
.huffman-tree-interface {
  display: flex;
  flex-direction: column;
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

.huffman-textarea {
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

.leaf-set-display {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.leaf-set-formula {
  overflow-x: auto;
  font-size: 1.1rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.algorithm-steps {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.step-item {
  margin: 1.5rem 0;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 6px;
  border-left: 4px solid #409eff;
}

.step-formula {
  margin: 1rem 0;
  padding: 0.5rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  overflow-x: auto;
}

.step-image {
  margin-top: 1rem;
  text-align: center;
}

.forest-image {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.final-tree {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.tree-image-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 6px;
  border: 1px solid #e9ecef;
  min-height: 100px;
}

.tree-image {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.weight-calculation {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.weight-formula {
  margin: 1rem 0;
  font-size: 1.1rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  overflow-x: auto;
}

.weight-result {
  margin-top: 0.5rem;
  font-size: 1rem;
  color: #374151;
  font-weight: 500;
}

.leaf-codes {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.codes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 1rem;
  margin-top: 1rem;
}

.code-item {
  background: #f8f9fa;
  padding: 0.8rem;
  border-radius: 6px;
  border: 1px solid #e9ecef;
  text-align: center;
}

.code-formula {
  font-size: 0.9rem;
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

/* 响应式设计 */
@media (max-width: 768px) {
  .huffman-tree-interface {
    max-height: 90vh;
  }

  .codes-grid {
    grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  }

  .example-buttons .el-button {
    font-size: 0.8rem;
  }
}

@media (max-width: 480px) {
  .button-row .el-col {
    margin-bottom: 0.5rem;
  }

  .codes-grid {
    grid-template-columns: 1fr;
  }

  .result-item {
    padding: 1rem;
  }
}
</style>