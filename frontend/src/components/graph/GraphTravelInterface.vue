<template>
  <div class="graph-travel-interface">
    <!-- 按钮操作区域 -->
    <div class="button-section">
      <!-- 第一行按钮：开始分析、生成随机图、清除输入、合法性检查 -->
      <el-row :gutter="20" class="button-row">
        <el-col :span="4">
          <el-button type="primary" @click="startAnalysis">
            <el-icon><Check /></el-icon>
            开始分析(Y)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="success" @click="generateRandomGraph">
            <el-icon><MagicStick /></el-icon>
            生成随机图(G)
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

      <!-- 第二行按钮：示例图 -->
      <el-divider content-position="left">《离散数学基础》教材示例展示</el-divider>
      <el-row :gutter="15" class="example-buttons">
        <el-col :span="3">
          <el-button size="small" @click="loadExample('example9_1')">例题9.1</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('example9_2')">例题9.2</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('example9_3_1')">例题9.3(1)</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('example9_16_1')">例题9.16(1)</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('example9_16_2')">例题9.16(2)</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('example9_20')">例题9.20</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('example9_21')">例题9.21</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('example9_20_bfs')">例题9.20(BFS)</el-button>
        </el-col>
      </el-row>

      <!-- 图类型选择 -->
      <el-divider content-position="left">图类型选择</el-divider>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-radio-group v-model="graphType" size="large">
            <el-radio label="undirected">无向图</el-radio>
            <el-radio label="directed">有向图</el-radio>
          </el-radio-group>
        </el-col>
      </el-row>

      <!-- 选项设置 -->
      <el-divider content-position="left">选择分析内容</el-divider>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-checkbox v-model="options.calculateDegree" size="large">
            计算顶点度数
          </el-checkbox>
        </el-col>
        <el-col :span="6">
          <el-checkbox v-model="options.calculatePath" size="large">
            计算连通路径
          </el-checkbox>
        </el-col>
        <el-col :span="6">
          <el-checkbox v-model="options.performDFS" size="large">
            深度优先遍历
          </el-checkbox>
        </el-col>
        <el-col :span="6">
          <el-checkbox v-model="options.performBFS" size="large">
            广度优先遍历
          </el-checkbox>
        </el-col>
      </el-row>
      <el-row :gutter="20" style="margin-top: 1rem;">
        <el-col :span="6">
          <el-checkbox v-model="options.showDetails" size="large">
            显示遍历计算
          </el-checkbox>
        </el-col>
        <el-col :span="6">
          <el-checkbox v-model="options.showAdjacencyMatrix" size="large">
            显示邻接矩阵
          </el-checkbox>
        </el-col>
        <el-col :span="6">
          <el-checkbox v-model="options.showIncidenceMatrix" size="large">
            显示关联矩阵
          </el-checkbox>
        </el-col>
        <el-col :span="6">
          <el-checkbox v-model="options.showGraphVisualization" size="large">
            显示图形可视化
          </el-checkbox>
        </el-col>
      </el-row>
    </div>

    <!-- 图输入区域 -->
    <div class="graph-input-section">
      <el-divider content-position="left">图的输入</el-divider>
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="input-group">
            <label>节点集(V)：</label>
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
              :placeholder="edgesPlaceholder"
              class="graph-textarea"
            />
          </div>
        </el-col>
      </el-row>
      <div class="input-hint">
        <el-text type="info" size="small">
          节点集格式：{v1, v2, v3} 或 {v1(label1), v2(label2)}
          <br>
          边集格式：无向图 {e1=(v1,v2), e2=(v2,v3)} 有向图 {e1=&lt;v1,v2&gt;, e2=&lt;v2,v3&gt;}

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
        <el-divider content-position="left">图遍历分析结果</el-divider>
        <div class="results-content">
          <div v-for="(result, index) in results" :key="index" class="result-item">

            <!-- 度数计算结果 -->
            <div v-if="result.nodeDegrees && result.nodeDegrees.length > 0" class="node-degrees">
              <h4>顶点度数：</h4>
              <div class="degrees-grid">
                <div v-for="node in result.nodeDegrees" :key="node.nodeId" class="degree-item">
                  <math-renderer
                    :formula="`d(${node.nodeLabel}) = ${node.degree}`"
                    :type="'mathjax'"
                    :display-mode="false"
                  />
                  <div v-if="result.directed" class="directed-degrees">
                    <math-renderer
                      :formula="`d^+(${node.nodeLabel}) = ${node.outDegree}`"
                      :type="'mathjax'"
                      :display-mode="false"
                      class="indegree"
                    />
                    <math-renderer
                      :formula="`d^-(${node.nodeLabel}) = ${node.inDegree}`"
                      :type="'mathjax'"
                      :display-mode="false"
                      class="outdegree"
                    />
                  </div>
                </div>
              </div>
            </div>

            <!-- 矩阵显示 -->
            <div v-if="result.adjacencyMatrix || result.incidenceMatrix" class="matrices">
              <div v-if="result.adjacencyMatrix" class="matrix-item">
                <h4>邻接矩阵 A：</h4>
                <math-renderer
                  :formula="result.adjacencyMatrix"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="matrix-formula"
                />
              </div>
              <div v-if="result.incidenceMatrix" class="matrix-item">
                <h4>关联矩阵 I：</h4>
                <math-renderer
                  :formula="result.incidenceMatrix"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="matrix-formula"
                />
              </div>
            </div>

            <!-- 路径矩阵 -->
            <div v-if="result.pathMatrices && result.pathMatrices.length > 0" class="path-matrices">
              <h4>邻接矩阵的幂：</h4>
              <div v-for="(matrix, idx) in result.pathMatrices" :key="idx" class="path-matrix">
                <math-renderer
                  :formula="matrix"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="path-matrix-formula"
                />
              </div>
            </div>

            <!-- DFS遍历结果 -->
            <div v-if="result.dfsResult" class="dfs-result">
              <h4>深度优先遍历(DFS)：</h4>
              <math-renderer
                :formula="result.dfsResult.traversalOrder"
                :type="'mathjax'"
                :display-mode="true"
                class="traversal-result"
              />
              <div v-if="result.dfsResult.steps && result.dfsResult.steps.length > 0" class="travel-steps">
                <h5>遍历步骤：</h5>
                <div v-for="step in result.dfsResult.steps" :key="step.step" class="step-item">
                  <math-renderer
                    :formula="`步骤${step.step}: T = ${step.visitedNodes} \\quad S = ${step.auxNodes}`"
                    :type="'mathjax'"
                    :display-mode="true"
                    class="step-formula"
                  />
                </div>
              </div>
            </div>

            <!-- BFS遍历结果 -->
            <div v-if="result.bfsResult" class="bfs-result">
              <h4>广度优先遍历(BFS)：</h4>
              <math-renderer
                :formula="result.bfsResult.traversalOrder"
                :type="'mathjax'"
                :display-mode="true"
                class="traversal-result"
              />
              <div v-if="result.bfsResult.steps && result.bfsResult.steps.length > 0" class="travel-steps">
                <h5>遍历步骤：</h5>
                <div v-for="step in result.bfsResult.steps" :key="step.step" class="step-item">
                  <math-renderer
                    :formula="`步骤${step.step}: T = ${step.visitedNodes} \\quad Q = ${step.auxNodes}`"
                    :type="'mathjax'"
                    :display-mode="true"
                    class="step-formula"
                  />
                </div>
              </div>
            </div>

            <!-- 图形可视化 -->
            <div v-if="result.graphImageUrl" class="graph-visualization">
              <h4>图形可视化：</h4>
              <div class="graph-image-container">
                <img
                  :src="result.graphImageUrl"
                  alt="图的可视化"
                  class="graph-image"
                  @error="handleImageError"
                />
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
  calculateDegree: true,
  calculatePath: false,
  performDFS: true,
  performBFS: true,
  showDetails: false,
  showAdjacencyMatrix: false,
  showIncidenceMatrix: false,
  showGraphVisualization: true
})

// 边的占位符文本
const edgesPlaceholder = computed(() => {
  return graphType.value === 'directed'
    ? '格式：{e1=<v1,v2>, e2=<v2,v3>}'
    : '格式：{e1=(v1,v2), e2=(v2,v3)}'
})

// 示例图数据
const exampleGraphs = {
  example9_1: {
    nodes: '{v1, v2, v3, v4}',
    edges: '{e1=(v1,v3), e2=(v1,v3), e3=(v1,v3), e4=(v1,v2), e5=(v1,v4), e6=(v2,v4), e7=(v2,v3), e8=(v3,v4), e9=(v4,v4)}',
    directed: false,
    options: { calculateDegree: true, performDFS: false, performBFS: false, showAdjacencyMatrix: true, showIncidenceMatrix: true, showGraphVisualization: true }
  },
  example9_2: {
    nodes: '{v1, v2, v3, v4}',
    edges: '{e1=<v1,v3>, e2=<v3,v1>, e3=<v1,v3>, e4=<v2,v1>, e5=<v4,v1>, e6=<v2,v4>, e7=<v2,v3>, e8=<v3,v4>, e9=<v4,v4>}',
    directed: true,
    options: { calculateDegree: true, performDFS: false, performBFS: false, showAdjacencyMatrix: true, showIncidenceMatrix: true, showGraphVisualization: true }
  },
  example9_3_1: {
    nodes: '{v1, v2, v3, v4, v5, v6}',
    edges: '{e1=(v1,v3), e2=(v1,v3), e3=(v1,v3), e4=(v1,v2), e5=(v1,v4), e6=(v2,v4), e7=(v2,v3), e8=(v3,v4), e9=(v4,v4), e10=(v2,v5)}',
    directed: false,
    options: { calculateDegree: true, performDFS: false, performBFS: false, showAdjacencyMatrix: true, showIncidenceMatrix: true, showGraphVisualization: true }
  },
  example9_16_1: {
    nodes: '{v1, v2, v3, v4, v5}',
    edges: '{e1=<v1,v2>, e2=<v3,v1>, e3=<v2,v3>, e4=<v4,v2>, e5=<v4,v1>, e6=<v4,v5>, e7=<v3,v5>, e8=<v5,v3>, e9=<v3,v5>}',
    directed: true,
    options: { calculateDegree: false, performDFS: false, performBFS: false, showAdjacencyMatrix: true, showIncidenceMatrix: true, showGraphVisualization: true }
  },
  example9_16_2: {
    nodes: '{v1, v2, v3, v4, v5}',
    edges: '{e1=(v1,v2), e2=(v3,v1), e3=(v2,v3), e4=(v4,v2), e5=(v4,v1), e6=(v4,v5), e7=<v2,v5>, e8=<v5,v3>, e9=<v3,v5>}',
    directed: false,
    options: { calculateDegree: false, performDFS: false, performBFS: false, showAdjacencyMatrix: true, showIncidenceMatrix: true, showGraphVisualization: true }
  },
  example9_20: {
    nodes: '{v1, v2, v3, v4, v5, v6, v7}',
    edges: '{e1=(v1,v2), e2=(v1,v3), e3=(v2,v3), e4=(v3,v7), e5=(v2,v4), e6=(v2,v6), e7=<v4,v5>, e8=<v5,v6>}',
    directed: false,
    options: { calculateDegree: false, performDFS: true, performBFS: false, showAdjacencyMatrix: false, showIncidenceMatrix: false, showGraphVisualization: true }
  },
  example9_21: {
    nodes: '{v1, v2, v3, v4, v5, v6, v7}',
    edges: '{e1=(v1,v2), e2=(v1,v3), e3=(v2,v3), e4=(v3,v7), e5=(v2,v4), e6=(v2,v6), e7=<v4,v5>, e8=<v5,v6>}',
    directed: false,
    options: { calculateDegree: false, performDFS: false, performBFS: true, showAdjacencyMatrix: false, showIncidenceMatrix: false, showGraphVisualization: true }
  },
  example9_20_bfs: {
    nodes: '{v1, v2, v3, v4, v5, v6, v7}',
    edges: '{e1=(v1,v2), e2=(v1,v3), e3=(v2,v3), e4=(v3,v7), e5=(v2,v4), e6=(v2,v6), e7=<v4,v5>, e8=<v5,v6>}',
    directed: false,
    options: { calculateDegree: false, performDFS: false, performBFS: true, showAdjacencyMatrix: false, showIncidenceMatrix: false, showGraphVisualization: true }
  }
}


// 事件处理函数
const emit = defineEmits(['close', 'graph-analyzed'])

const startAnalysis = async () => {
  if (!nodesInput.value.trim() || !edgesInput.value.trim()) {
    ElMessage.warning('请输入节点集和边集')
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

    console.log('GraphTravelInterface: 开始分析图', request)

    const response = await callBackendApi('/graph-travel/analyze', {
      method: 'POST',
      body: JSON.stringify(request)
    })

    console.log('GraphTravelInterface: 收到分析结果', response)

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
        message: '图分析完成'
      })

      // 发送结果到主界面
      emit('graph-analyzed', result)

      ElMessage.success('图遍历分析完成')
    } else {
      feedback.value.push({
        formula: `G = (V, E)`,
        type: 'error',
        message: response.errorMessage || '图分析失败'
      })
    }
  } catch (error) {
    console.error('图遍历分析失败:', error)
    feedback.value.push({
      formula: `G = (V, E)`,
      type: 'error',
      message: `分析失败: ${error.message}`
    })
  }
}

const generateRandomGraph = async () => {
  try {
    console.log('GraphTravelInterface: 开始生成随机图')

    const response = await callBackendApi('/graph-travel/generate', {
      method: 'GET'
    })

    console.log('GraphTravelInterface: 随机图生成结果', response)

    if (response.success) {
      nodesInput.value = response.nodesString
      edgesInput.value = response.edgesString
      graphType.value = response.directed ? 'directed' : 'undirected'

      // 设置选项
      Object.assign(options.value, response.options)

      // 清除之前的结果
      feedback.value = []
      results.value = []

      ElMessage.success('已生成随机图')
    } else {
      ElMessage.error('生成随机图失败: ' + response.message)
    }
  } catch (error) {
    console.error('生成随机图失败:', error)
    ElMessage.error(`生成随机图失败: ${error.message}`)
  }
}

const clearInput = () => {
  nodesInput.value = ''
  edgesInput.value = ''
  ElMessage.info('已清空输入')
}

const checkInput = async () => {
  if (!nodesInput.value.trim() || !edgesInput.value.trim()) {
    ElMessage.warning('请输入节点集和边集')
    return
  }

  feedback.value = []

  try {
    const response = await callBackendApi('/graph-travel/validate', {
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
        message: '图的输入格式正确'
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

    console.log('GraphTravelInterface: 加载示例', exampleKey, example)
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
.graph-travel-interface {
  display: flex;
  flex-direction: column;
  width: 100%;
}

/* 在大屏幕上完全自适应，无最大宽度限制 */
@media (min-width: 1200px) {
  .graph-travel-interface {
    width: 100%;
    max-width: none;
  }
}

/* 在中等屏幕上设置合理的最大宽度 */
@media (max-width: 1199px) and (min-width: 900px) {
  .graph-travel-interface {
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
  }
}

/* 在小屏幕上固定宽度，支持水平滚动 */
@media (max-width: 899px) {
  .graph-travel-interface {
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

.node-degrees {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.degrees-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 1rem;
  margin-top: 1rem;
}

.degree-item {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 6px;
  border: 1px solid #e9ecef;
  text-align: center;
}

.directed-degrees {
  margin-top: 0.5rem;
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  font-size: 0.9em;
}

.matrices {
  margin: 1.5rem 0;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 1rem;
}

.matrix-item {
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

.path-matrices {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.path-matrix {
  margin: 1rem 0;
  overflow-x: auto;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.path-matrix-formula {
  font-size: 0.9em;
}

.dfs-result,
.bfs-result {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.traversal-result {
  margin: 1rem 0;
  font-size: 1.1rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.travel-steps {
  margin-top: 1rem;
}

.step-item {
  margin: 1rem 0;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 6px;
  border-left: 4px solid #409eff;
  font-size: 0.9em;
}

.step-formula {
  padding: 0.5rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.graph-visualization {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

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

/* 内容区域响应式优化 */
@media (max-width: 768px) {
  .degrees-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  }

  .matrices {
    grid-template-columns: 1fr;
  }

  .example-buttons .el-button {
    font-size: 0.8rem;
  }
}

@media (max-width: 480px) {
  .button-row .el-col {
    margin-bottom: 0.5rem;
  }

  .degrees-grid {
    grid-template-columns: 1fr;
  }

  .result-item {
    padding: 1rem;
  }
}
</style>