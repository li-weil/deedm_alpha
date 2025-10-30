<template>
  <div class="shortest-path-interface">
    <!-- 按钮操作区域 -->
    <div class="button-section">
      <!-- 第一行按钮：开始执行、生成随机图、清除输入、合法性检查 -->
      <el-row :gutter="20" class="button-row">
        <el-col :span="4">
          <el-button type="primary" @click="startExecution">
            <el-icon><Check /></el-icon>
            开始执行(Y)
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
          <el-button size="small" @click="loadExample('example9_28')">例题9.28</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('example1_12')">例题1.12</el-button>
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
      <el-divider content-position="left">选择展示的内容</el-divider>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-checkbox v-model="options.executeDijkstra" size="large">
            执行Dijsktra算法
          </el-checkbox>
        </el-col>
        <el-col :span="6">
          <el-checkbox v-model="options.showDetails" size="large">
            显示算法详细过程
          </el-checkbox>
        </el-col>
        <el-col :span="6">
          <el-checkbox v-model="options.showPaths" size="large">
            显示最短路径结果
          </el-checkbox>
        </el-col>
        <el-col :span="6">
          <el-checkbox v-model="options.showPathGraph" size="large">
            显示最短路径图
          </el-checkbox>
        </el-col>
      </el-row>
      <el-row :gutter="20" style="margin-top: 1rem;">
        <el-col :span="6">
          <el-checkbox v-model="options.showMatrix" size="large">
            显示带权图的距离矩阵
          </el-checkbox>
        </el-col>
        <el-col :span="6">
          <el-checkbox v-model="options.showGraphVisualization" size="large">
            给出图形化表示
          </el-checkbox>
        </el-col>
      </el-row>
    </div>

    <!-- 图输入区域 -->
    <div class="graph-input-section">
      <el-divider content-position="left">带权图的节点集和边集</el-divider>
      <el-row :gutter="20">
        <el-col :span="8">
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
        <el-col :span="8">
          <div class="input-group">
            <label>起始节点(S)：</label>
            <el-input
              v-model="startNodeInput"
              placeholder="起始节点ID"
              class="graph-textarea"
            />
          </div>
        </el-col>
        <el-col :span="8">
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
          边集格式：无向图 {[权]=(v1,v2), [权]=(v2,v3)} 有向图 {[权]=&lt;v1,v2&gt;, [权]=&lt;v2,v3&gt;}
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
        <el-divider content-position="left">带权图最短路径计算结果</el-divider>
        <div class="results-content">
          <div v-for="(result, index) in results" :key="index" class="result-item">
            <!-- 图形可视化 -->
            <div v-if="result.graphImageUrl" class="graph-visualization">
              <h4>图形化表示：</h4>
              <div class="graph-image-container">
                <img
                  :src="result.graphImageUrl"
                  alt="带权图的可视化"
                  class="graph-image"
                  @error="handleImageError"
                />
              </div>
            </div>

            <!-- 邻接矩阵 -->
            <div v-if="result.adjacencyMatrix" class="adjacency-matrix">
              <h4>带权图的距离矩阵表示：</h4>
              <math-renderer
                :formula="result.adjacencyMatrix"
                :type="'mathjax'"
                :display-mode="true"
                class="matrix-formula"
              />
            </div>

            <!-- Dijkstra算法详细过程 -->
            <div v-if="result.dijkstraDetails" class="dijkstra-details">
              <h4>Dijkstra算法详细过程：</h4>
              <div class="algorithm-content">
                <math-renderer
                  :formula="result.dijkstraDetails"
                  :type="'katex'"
                  :display-mode="true"
                  class="algorithm-formula"
                />
              </div>
            </div>

            <!-- 最短路径结果 -->
            <div v-if="result.shortestPaths && result.shortestPaths.length > 0" class="shortest-paths">
              <h4>得到的最短路径与最短距离如下：</h4>
              <div class="paths-grid">
                <div v-for="path in result.shortestPaths" :key="path.target" class="path-item">
                  <math-renderer
                    :formula="path.formula"
                    :type="'mathjax'"
                    :display-mode="true"
                    class="path-formula"
                  />
                </div>
              </div>
            </div>



            <!-- 最短路径图 -->
            <div v-if="result.pathGraphImageUrl" class="path-graph-visualization">
              <h4>最短路径图：</h4>
              <div class="graph-image-container">
                <img
                  :src="result.pathGraphImageUrl"
                  alt="最短路径图的可视化"
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
const startNodeInput = ref('')
const edgesInput = ref('')
const graphType = ref('undirected')
const feedback = ref([])
const results = ref([])
const counter = ref(0)

// 分析选项
const options = ref({
  executeDijkstra: true,
  showDetails: true,
  showPaths: true,
  showPathGraph: false,
  showMatrix: true,
  showGraphVisualization: true
})

// 边的占位符文本
const edgesPlaceholder = computed(() => {
  return graphType.value === 'directed'
    ? '格式：{[权]=<v1,v2>, [权]=<v2,v3>}'
    : '格式：{[权]=(v1,v2), [权]=(v2,v3)}'
})

// 示例图数据
const exampleGraphs = {
  example9_28: {
    nodes: '{v1, v2, v3, v4, v5, v6}',
    startNode: 'v1',
    edges: '{[1]=<v1,v3>, [7]=<v1,v2>, [6]=<v3,v2>, [4]=<v2,v4>, [1]=<v2,v6>, [3]=<v5,v2>, [2]=<v3,v5>, [7]=<v3,v6>, [5]=<v5,v4>, [3]=<v6,v5> }',
    directed: true,
    options: { executeDijkstra: true, showDetails: true, showPaths: true, showPathGraph: true, showMatrix: true, showGraphVisualization: true }
  },
  example1_12: {
    nodes: '{GZ, WH, HZ, HF, BJ, TJ, SY}',
    startNode: 'GZ',
    edges: '{[1022]=(GZ,WH), [1607]=(GZ,HZ), [807]=(WH,HZ), [1200]=(WH,BJ), [362]=(WH,HF), [445]=(HZ,HF), [1512]=(HZ,TJ), [1109]=(HF,BJ), [971]=(HF,TJ), [137]=(BJ,TJ), [889]=(BJ,SY), [689]=(TJ,SY) }',
    directed: false,
    options: { executeDijkstra: true, showDetails: true, showPaths: true, showPathGraph: true, showMatrix: true, showGraphVisualization: true }
  }
}

// 事件处理函数
const emit = defineEmits(['close', 'shortest-path-calculated'])

const startExecution = async () => {
  if (!nodesInput.value.trim() || !edgesInput.value.trim() || !startNodeInput.value.trim()) {
    ElMessage.warning('请输入节点集、边集和起始节点')
    return
  }

  // 清空之前的反馈和结果
  feedback.value = []
  results.value = []

  try {
    const request = {
      nodesString: nodesInput.value.trim(),
      edgesString: edgesInput.value.trim(),
      startNode: startNodeInput.value.trim(),
      directed: graphType.value === 'directed',
      ...options.value
    }

  const response = await callBackendApi('/shortest-path/calculate', {
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
        message: '最短路径计算完成'
      })

      // 发送结果到主界面
      emit('shortest-path-calculated', result)

      ElMessage.success('带权图最短路径计算完成')
    } else {
      feedback.value.push({
        formula: `G = (V, E)`,
        type: 'error',
        message: response.errorMessage || '最短路径计算失败'
      })
    }
  } catch (error) {
    console.error('最短路径计算失败:', error)
    feedback.value.push({
      formula: `G = (V, E)`,
      type: 'error',
      message: `计算失败: ${error.message}`
    })
  }
}

const generateRandomGraph = async () => {
  try {
    const response = await callBackendApi('/shortest-path/generate', {
      method: 'GET'
    })

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
  startNodeInput.value = ''
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
    const response = await callBackendApi('/shortest-path/validate', {
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
        message: '带权图的输入格式正确'
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
    startNodeInput.value = example.startNode
    edgesInput.value = example.edges
    graphType.value = example.directed ? 'directed' : 'undirected'

    // 设置选项
    Object.assign(options.value, example.options)

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
.shortest-path-interface {
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

.adjacency-matrix {
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

.dijkstra-details {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.algorithm-content {
  overflow-x: auto;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.algorithm-formula {
  font-size: 0.9em;
}

.shortest-paths {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.paths-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1rem;
  margin-top: 1rem;
}

.path-item {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 6px;
  border: 1px solid #e9ecef;
  text-align: center;
}

.path-formula {
  font-size: 1rem;
}

.graph-visualization,
.path-graph-visualization {
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

/* 响应式设计 */
@media (max-width: 768px) {
  .shortest-path-interface {
    max-height: 90vh;
  }

  .paths-grid {
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

  .result-item {
    padding: 1rem;
  }
}
</style>