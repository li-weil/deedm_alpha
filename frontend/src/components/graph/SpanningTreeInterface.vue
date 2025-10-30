<template>
  <div class="spanning-tree-interface">
    <!-- 按钮操作区域 -->
    <div class="button-section">
      <!-- 第一行按钮：开始计算、生成随机图、清除输入、合法性检查 -->
      <el-row :gutter="20" class="button-row">
        <el-col :span="4">
          <el-button type="primary" @click="startCalculation">
            <el-icon><Check /></el-icon>
            开始执行(Y)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="success" @click="generateRandomGraph">
            <el-icon><MagicStick /></el-icon>
            随机生成(G)
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
            检查合法性(K)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="danger" @click="clearResults">
            <el-icon><RefreshRight /></el-icon>
            清理屏幕(C)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button @click="closeInterface">
            <el-icon><Close /></el-icon>
            返回(R)
          </el-button>
        </el-col>
      </el-row>

      <!-- 第二行按钮：示例图 -->
      <el-divider content-position="left">《离散数学基础》教材示例</el-divider>
      <el-row :gutter="15" class="example-buttons">
        <el-col :span="4">
          <el-button size="small" @click="loadExample('example9_29')">例子9.29</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('example9_30')">例子9.30</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('example1_12')">例子1.12</el-button>
        </el-col>
      </el-row>

      <!-- 图类型选择 -->
      <el-divider content-position="left">图的类型</el-divider>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-radio-group v-model="graphType" size="large">
            <el-radio label="undirected">无向图</el-radio>
            <el-radio label="directed" disabled>有向图</el-radio>
          </el-radio-group>
        </el-col>
      </el-row>

      <!-- 选项设置 -->
      <el-divider content-position="left">选择进行的遍历和计算</el-divider>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-checkbox v-model="options.performKruskal" size="large">
            执行Kruskal算法
          </el-checkbox>
        </el-col>
        <el-col :span="6">
          <el-checkbox v-model="options.performPrim" size="large">
            执行Prim算法
          </el-checkbox>
        </el-col>
        <el-col :span="6">
          <el-checkbox v-model="options.showDetails" size="large">
            给出算法计算过程
          </el-checkbox>
        </el-col>
        <el-col :span="6">
          <el-checkbox v-model="options.showMatrix" size="large">
            给出带权图的距离矩阵
          </el-checkbox>
        </el-col>
      </el-row>
      <el-row :gutter="20" style="margin-top: 1rem;">
        <el-col :span="6">
          <el-checkbox v-model="options.showGraph" size="large">
            给出原图的图形化表示
          </el-checkbox>
        </el-col>
        <el-col :span="6">
          <el-checkbox v-model="options.showSpanningTreeGraph" size="large">
            给出最小生成树的图形化表示
          </el-checkbox>
        </el-col>
      </el-row>
    </div>

    <!-- 图输入区域 -->
    <div class="graph-input-section">
      <el-divider content-position="left">请输入带权图的顶点集和边集格式化串</el-divider>
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="input-group">
            <label>顶点集(V)：</label>
            <el-input
              v-model="nodesInput"
              type="textarea"
              :rows="3"
              placeholder="格式：{v1, v2, v3} 或 {v1(label1), v2(label2)}"
              class="graph-textarea"
            />
          </div>
        </el-col>
        <el-col :span="12">
          <div class="input-group">
            <label>边集(E)：</label>
            <el-input
              v-model="edgesInput"
              type="textarea"
              :rows="3"
              placeholder="格式：{[权]=(v1,v2), [权]=(v2,v3)}"
              class="graph-textarea"
            />
          </div>
        </el-col>
      </el-row>
      <div class="input-hint">
        <el-text type="info" size="small">
          顶点集格式：{v1, v2, v3} 或 {v1(label1), v2(label2)}
          <br>
          边集的格式化串形式：{边label[权] = (顶点id, 顶点id), ..., 边label[权] = (顶点id, 顶点id)}
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
        <el-divider content-position="left">带权图最小生成树计算结果</el-divider>
        <div class="results-content">
          <div v-for="(result, index) in results" :key="index" class="result-item">

            <!-- 原图可视化 -->
            <div v-if="result.graphImageUrl" class="graph-visualization">
              <h4>原图可视化：</h4>
              <div class="graph-image-container">
                <img
                  :src="result.graphImageUrl"
                  alt="原图的可视化"
                  class="graph-image"
                  @error="handleImageError"
                />
              </div>
            </div>
            
            <!-- 距离矩阵显示 -->
            <div v-if="result.weightMatrix" class="weight-matrix">
              <h4>带权图的距离矩阵：</h4>
              <math-renderer
                :formula="result.weightMatrix"
                :type="'mathjax'"
                :display-mode="true"
                class="matrix-formula"
              />
            </div>

            <!-- Kruskal算法结果 -->
            <div v-if="result.kruskalResult" class="kruskal-result">
              <h4>Kruskal算法结果：</h4>
              <div class="algorithm-summary">
                <math-renderer
                  :formula="`使用Kruskal算法发现最小生成树（森林），总权重 = ${result.kruskalResult.totalWeight}`"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="summary-formula"
                />
              </div>

              <div v-if="result.kruskalResult.edges" class="algorithm-edges">
                <h5>生成树的边集：</h5>
                <math-renderer
                  :formula="result.kruskalResult.edges"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="edges-formula"
                />
              </div>

              <div v-if="result.kruskalResult.steps" class="algorithm-steps">
                <h5>算法计算过程：</h5>
                <math-renderer
                  :formula="result.kruskalResult.steps"
                  :type="'katex'"
                  :display-mode="true"
                  class="steps-formula"
                />
              </div>

              <div v-if="result.kruskalTreeImageUrl" class="tree-visualization">
                <h5>最小生成树图形：</h5>
                <div class="tree-image-container">
                  <img
                    :src="result.kruskalTreeImageUrl"
                    alt="Kruskal最小生成树"
                    class="tree-image"
                    @error="handleImageError"
                  />
                </div>
              </div>
            </div>

            <!-- Prim算法结果 -->
            <div v-if="result.primResult" class="prim-result">
              <h4>Prim算法结果：</h4>
              <div class="algorithm-summary">
                <math-renderer
                  :formula="`使用Prim算法发现最小生成树（森林），总权重 = ${result.primResult.totalWeight}`"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="summary-formula"
                />
              </div>

              <div v-if="result.primResult.edges" class="algorithm-edges">
                <h5>生成树的边集：</h5>
                <math-renderer
                  :formula="result.primResult.edges"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="edges-formula"
                />
              </div>

              <div v-if="result.primResult.steps" class="algorithm-steps">
                <h5>算法计算过程：</h5>
                <math-renderer
                  :formula="result.primResult.steps"
                  :type="'katex'"
                  :display-mode="true"
                  class="steps-formula"
                />
              </div>

              <div v-if="result.primTreeImageUrl" class="tree-visualization">
                <h5>最小生成树图形：</h5>
                <div class="tree-image-container">
                  <img
                    :src="result.primTreeImageUrl"
                    alt="Prim最小生成树"
                    class="tree-image"
                    @error="handleImageError"
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
import { ref, computed } from 'vue'
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
const nodesInput = ref('')
const edgesInput = ref('')
const graphType = ref('undirected')
const feedback = ref([])
const results = ref([])
const counter = ref(0)

// 分析选项
const options = ref({
  performKruskal: true,
  performPrim: true,
  showDetails: false,
  showMatrix: false,
  showGraph: true,
  showSpanningTreeGraph: true
})

// 示例图数据
const exampleGraphs = {
  example9_29: {
    nodes: '{v1, v2, v3, v4, v5}',
    edges: '{[15]=(v1,v2), [20]=(v1,v3), [40]=(v1,v4), [25]=(v1,v5), [30]=(v2,v3), [20]=(v2,v4), [30]=(v2,v5), [10]=(v3,v4), [15]=(v3,v5), [10]=(v4,v5)}',
    directed: false,
    options: { performKruskal: true, performPrim: false, showDetails: false, showMatrix: true, showGraph: true, showSpanningTreeGraph: true }
  },
  example9_30: {
    nodes: '{v1, v2, v3, v4, v5}',
    edges: '{[15]=(v1,v2), [20]=(v1,v3), [40]=(v1,v4), [25]=(v1,v5), [30]=(v2,v3), [20]=(v2,v4), [30]=(v2,v5), [10]=(v3,v4), [15]=(v3,v5), [10]=(v4,v5)}',
    directed: false,
    options: { performKruskal: false, performPrim: true, showDetails: false, showMatrix: true, showGraph: true, showSpanningTreeGraph: true }
  },
  example1_12: {
    nodes: '{GZ, WH, HZ, HF, BJ, TJ, SY}',
    edges: '{[1022]=(GZ,WH), [1607]=(GZ,HZ), [807]=(WH,HZ), [1200]=(WH,BJ), [362]=(WH,HF), [445]=(HZ,HF), [1512]=(HZ,TJ), [1109]=(HF,BJ), [971]=(HF,TJ), [137]=(BJ,TJ), [889]=(BJ,SY), [689]=(TJ,SY)}',
    directed: false,
    options: { performKruskal: true, performPrim: true, showDetails: false, showMatrix: true, showGraph: true, showSpanningTreeGraph: true }
  }
}

// 事件处理函数
const emit = defineEmits(['close', 'spanning-tree-calculated'])

const startCalculation = async () => {
  if (!nodesInput.value.trim() || !edgesInput.value.trim()) {
    ElMessage.warning('请输入顶点集和边集')
    return
  }

  if (!options.value.performKruskal && !options.value.performPrim) {
    ElMessage.warning('请至少选择一种算法（Kruskal或Prim）')
    return
  }

  // 清空之前的反馈和结果
  feedback.value = []
  results.value = []

  try {
    const request = {
      nodesString: nodesInput.value.trim(),
      edgesString: edgesInput.value.trim(),
      directed: graphType.value === 'directed',
      ...options.value
    }

    console.log('SpanningTreeInterface: 开始计算最小生成树', request)

    const response = await callBackendApi('/spanning-tree/calculate', {
      method: 'POST',
      body: JSON.stringify(request)
    })

    console.log('SpanningTreeInterface: 收到计算结果', response)

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
        message: '最小生成树计算完成'
      })

      // 发送结果到主界面
      emit('spanning-tree-calculated', result)

      ElMessage.success('最小生成树计算完成')
    } else {
      feedback.value.push({
        formula: `G = (V, E)`,
        type: 'error',
        message: response.errorMessage || '最小生成树计算失败'
      })
    }
  } catch (error) {
    console.error('最小生成树计算失败:', error)
    feedback.value.push({
      formula: `G = (V, E)`,
      type: 'error',
      message: `计算失败: ${error.message}`
    })
  }
}

const generateRandomGraph = async () => {
  try {
    console.log('SpanningTreeInterface: 开始生成随机带权图')

    const response = await callBackendApi('/spanning-tree/generate', {
      method: 'GET'
    })

    console.log('SpanningTreeInterface: 随机图生成结果', response)

    if (response.success) {
      nodesInput.value = response.nodesString
      edgesInput.value = response.edgesString
      graphType.value = response.directed ? 'directed' : 'undirected'

      // 设置选项
      Object.assign(options.value, response.options)

      // 清除之前的结果
      feedback.value = []
      results.value = []

      ElMessage.success('已生成随机带权图')
    } else {
      ElMessage.error('生成随机带权图失败: ' + response.message)
    }
  } catch (error) {
    console.error('生成随机带权图失败:', error)
    ElMessage.error(`生成随机带权图失败: ${error.message}`)
  }
}

const clearInput = () => {
  nodesInput.value = ''
  edgesInput.value = ''
  ElMessage.info('已清空输入')
}

const checkInput = async () => {
  if (!nodesInput.value.trim() || !edgesInput.value.trim()) {
    ElMessage.warning('请输入顶点集和边集')
    return
  }

  feedback.value = []

  try {
    const response = await callBackendApi('/spanning-tree/validate', {
      method: 'POST',
      body: JSON.stringify({
        nodesString: nodesInput.value.trim(),
        edgesString: edgesInput.value.trim(),
        directed: graphType.value === 'directed'
      })
    })

    if (response.valid) {
      feedback.value.push({
        formula: `G = (V, E)`,
        type: 'success',
        message: '带权图的输入格式正确，可以计算最小生成树！'
      })
      ElMessage.success('输入格式正确')
    } else {
      feedback.value.push({
        formula: `G = (V, E)`,
        type: 'error',
        message: response.message
      })
      ElMessage.error('输入格式不正确')
    }
  } catch (error) {
    console.error('检查输入失败:', error)
    feedback.value.push({
      formula: `G = (V, E)`,
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
  if (exampleGraphs[exampleKey]) {
    const example = exampleGraphs[exampleKey]

    // 清除之前的结果
    results.value = []
    feedback.value = []
    counter.value = 0

    // 设置示例数据
    nodesInput.value = example.nodes
    edgesInput.value = example.edges
    graphType.value = example.directed ? 'directed' : 'undirected'

    // 设置选项
    Object.assign(options.value, example.options)

    console.log('SpanningTreeInterface: 加载示例', exampleKey, example)
    ElMessage.info(`已加载示例：${exampleKey}`)
  }
}

const handleImageError = (event) => {
  console.error('图形可视化加载失败:', event.target.src)
  ElMessage.error('图形可视化加载失败')
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
.spanning-tree-interface {
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

.graph-input-section {
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

.graph-textarea {
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

.weight-matrix {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.matrix-formula {
  overflow-x: auto;
  font-size: 0.9em;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.kruskal-result,
.prim-result {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.algorithm-summary {
  margin: 1rem 0;
}

.summary-formula {
  padding: 0.5rem;
  background: #f0f9ff;
  border-radius: 4px;
  border: 1px solid #0ea5e9;
  font-weight: 600;
}

.algorithm-edges {
  margin: 1rem 0;
}

.edges-formula {
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.algorithm-steps {
  margin: 1rem 0;
}

.steps-formula {
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  font-size: 0.9em;
}

.tree-visualization,
.graph-visualization {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.tree-image-container,
.graph-image-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 6px;
  border: 1px solid #e9ecef;
  min-height: 100px;
}

.tree-image,
.graph-image {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  object-fit: contain;
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
  .spanning-tree-interface {
    max-height: 90vh;
  }

  .example-buttons .el-button {
    font-size: 0.8rem;
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