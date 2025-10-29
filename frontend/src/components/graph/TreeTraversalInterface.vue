<template>
  <div class="tree-traversal-interface">
    <!-- 按钮操作区域 -->
    <div class="button-section">
      <!-- 第一行按钮：开始分析、生成随机树、清除输入、合法性检查 -->
      <el-row :gutter="20" class="button-row">
        <el-col :span="4">
          <el-button type="primary" @click="startAnalysis">
            <el-icon><Check /></el-icon>
            开始遍历(Y)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="success" @click="generateRandomTree">
            <el-icon><MagicStick /></el-icon>
            生成随机树(G)
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

      <!-- 第二行按钮：示例树 -->
      <el-divider content-position="left">《离散数学基础》教材示例展示</el-divider>
      <el-row :gutter="15" class="example-buttons">
        <el-col :span="6">
          <el-button size="small" @click="loadExample('example9_26')">例题9.26</el-button>
        </el-col>
        <el-col :span="6">
          <el-button size="small" @click="loadExample('example9_27')">例题9.27</el-button>
        </el-col>
        <el-col :span="6">
          <el-button size="small" @click="loadExample('binary_tree')">二叉树示例</el-button>
        </el-col>
      </el-row>

      <!-- 遍历方式选择 -->
      <el-divider content-position="left">选择遍历方式</el-divider>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-checkbox v-model="options.preorder" size="large">
            前序遍历 (Preorder)
          </el-checkbox>
        </el-col>
        <el-col :span="8">
          <el-checkbox v-model="options.inorder" size="large">
            中序遍历 (Inorder)
          </el-checkbox>
        </el-col>
        <el-col :span="8">
          <el-checkbox v-model="options.postorder" size="large">
            后序遍历 (Postorder)
          </el-checkbox>
        </el-col>
      </el-row>

      <!-- 显示选项 -->
      <el-divider content-position="left">显示选项</el-divider>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-checkbox v-model="options.showAdjacencyMatrix" size="large">
            显示邻接矩阵
          </el-checkbox>
        </el-col>
        <el-col :span="8">
          <el-checkbox v-model="options.showIncidenceMatrix" size="large">
            显示关联矩阵
          </el-checkbox>
        </el-col>
        <el-col :span="8">
          <el-checkbox v-model="options.showGraphVisualization" size="large">
            显示树形图
          </el-checkbox>
        </el-col>
      </el-row>
    </div>

    <!-- 树输入区域 -->
    <div class="tree-input-section">
      <el-divider content-position="left">树的输入</el-divider>
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="input-group">
            <label>节点集(V)：</label>
            <el-input
              v-model="nodesInput"
              type="textarea"
              :rows="2"
              placeholder="格式：{a, b, c, d} 或 {a(label1), b(label2)}"
              class="tree-textarea"
            />
          </div>
        </el-col>
        <el-col :span="8">
          <div class="input-group">
            <label>根节点：</label>
            <el-input
              v-model="rootInput"
              placeholder="根节点ID，如：a"
              class="tree-textarea"
            />
          </div>
        </el-col>
        <el-col :span="8">
          <div class="input-group">
            <label>边集(E)：</label>
            <el-input
              v-model="edgesInput"
              type="textarea"
              :rows="2"
              placeholder="格式：{(a,b), (a,c), (b,d)}"
              class="tree-textarea"
            />
          </div>
        </el-col>
      </el-row>
      <div class="input-hint">
        <el-text type="info" size="small">
          节点集格式：{a, b, c} 或 {a(label1), b(label2)}
          <br>
          边集格式：{(a,b), (a,c), (b,d)} - 无向边表示树的连接关系
          <br>
          根节点：必须是节点集中的某个节点ID
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
        <el-divider content-position="left">树遍历分析结果</el-divider>
        <div class="results-content">
          <div v-for="(result, index) in results" :key="index" class="result-item">
            <!-- 基本信息 -->

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

            <!-- 前序遍历结果 -->
            <div v-if="result.preorderResult" class="traversal-result">
              <h4>前序遍历 (Preorder)：</h4>
              <math-renderer
                :formula="result.preorderResult.traversalOrder"
                :type="'mathjax'"
                :display-mode="true"
                class="traversal-formula"
              />
            </div>

            <!-- 中序遍历结果 -->
            <div v-if="result.inorderResult" class="traversal-result">
              <h4>中序遍历 (Inorder)：</h4>
              <math-renderer
                :formula="result.inorderResult.traversalOrder"
                :type="'mathjax'"
                :display-mode="true"
                class="traversal-formula"
              />
            </div>

            <!-- 后序遍历结果 -->
            <div v-if="result.postorderResult" class="traversal-result">
              <h4>后序遍历 (Postorder)：</h4>
              <math-renderer
                :formula="result.postorderResult.traversalOrder"
                :type="'mathjax'"
                :display-mode="true"
                class="traversal-formula"
              />
            </div>

            <!-- 树形可视化 -->
            <div v-if="result.graphImageUrl" class="graph-visualization">
              <h4>树形可视化：</h4>
              <div class="graph-image-container">
                <img
                  :src="result.graphImageUrl"
                  alt="树的可视化"
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
const rootInput = ref('')
const feedback = ref([])
const results = ref([])
const counter = ref(0)

// 遍历选项
const options = ref({
  preorder: true,
  inorder: true,
  postorder: true,
  showAdjacencyMatrix: false,
  showIncidenceMatrix: false,
  showGraphVisualization: true
})

// 示例树数据
const exampleTrees = {
  example9_26: {
    nodes: '{a, b, c, d, e, f, g, h, i, j, k, p}',
    edges: '{(a,b), (a,j), (a,h), (b,c), (b,k), (b,e), (e,d), (e,f), (h,g), (h,p), (h,i)}',
    root: 'a',
    options: {
      preorder: true, inorder: true, postorder: true,
      showAdjacencyMatrix: false, showIncidenceMatrix: false, showGraphVisualization: true
    }
  },
  example9_27: {
    nodes: '{times(*), plus(+), minus(-), divide(/), three(3), seven(7), anotherthree(3), five(5), two(2)}',
    edges: '{(times,plus), (times,minus), (plus,three), (plus,divide), (divide,seven), (divide,anotherthree), (minus,five), (minus,two)}',
    root: 'times',
    options: {
      preorder: true, inorder: true, postorder: true,
      showAdjacencyMatrix: false, showIncidenceMatrix: false, showGraphVisualization: true
    }
  },
  binary_tree: {
    nodes: '{A, B, C, D, E, F, G}',
    edges: '{(A,B), (A,C), (B,D), (B,E), (C,F), (C,G)}',
    root: 'A',
    options: {
      preorder: true, inorder: true, postorder: true,
      showAdjacencyMatrix: false, showIncidenceMatrix: false, showGraphVisualization: true
    }
  }
}

// 事件处理函数
const emit = defineEmits(['close', 'tree-traversed'])

const startAnalysis = async () => {
  if (!nodesInput.value.trim() || !edgesInput.value.trim() || !rootInput.value.trim()) {
    ElMessage.warning('请输入节点集、边集和根节点')
    return
  }

  // 清空之前的反馈和结果
  feedback.value = []
  results.value = []

  try {
    const request = {
      nodesString: nodesInput.value.trim(),
      edgesString: edgesInput.value.trim(),
      rootString: rootInput.value.trim(),
      directed: false, // 树都是无向的
      preorder: options.value.preorder,
      inorder: options.value.inorder,
      postorder: options.value.postorder,
      showAdjacencyMatrix: options.value.showAdjacencyMatrix,
      showIncidenceMatrix: options.value.showIncidenceMatrix,
      showGraphVisualization: options.value.showGraphVisualization
    }

    console.log('TreeTraversalInterface: 开始分析树', request)

    const response = await callBackendApi('/tree-traversal/analyze', {
      method: 'POST',
      body: JSON.stringify(request)
    })

    console.log('TreeTraversalInterface: 收到分析结果', response)

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
        message: '树遍历分析完成'
      })

      // 发送结果到主界面
      emit('tree-traversed', result)

      ElMessage.success('树遍历分析完成')
    } else {
      feedback.value.push({
        formula: `T = (V, E)`,
        type: 'error',
        message: response.errorMessage || '树遍历分析失败'
      })
    }
  } catch (error) {
    console.error('树遍历分析失败:', error)
    feedback.value.push({
      formula: `T = (V, E)`,
      type: 'error',
      message: `分析失败: ${error.message}`
    })
  }
}

const generateRandomTree = async () => {
  try {
    console.log('TreeTraversalInterface: 开始生成随机树')

    const response = await callBackendApi('/tree-traversal/generate', {
      method: 'GET'
    })

    console.log('TreeTraversalInterface: 随机树生成结果', response)

    if (response.success) {
      nodesInput.value = response.nodesString
      edgesInput.value = response.edgesString
      rootInput.value = response.rootString

      // 设置选项
      Object.assign(options.value, response.options)

      // 清除之前的结果
      feedback.value = []
      results.value = []

      ElMessage.success('已生成随机树')
    } else {
      ElMessage.error('生成随机树失败: ' + response.message)
    }
  } catch (error) {
    console.error('生成随机树失败:', error)
    ElMessage.error(`生成随机树失败: ${error.message}`)
  }
}

const clearInput = () => {
  nodesInput.value = ''
  edgesInput.value = ''
  rootInput.value = ''
  ElMessage.info('已清空输入')
}

const checkInput = async () => {
  if (!nodesInput.value.trim() || !edgesInput.value.trim() || !rootInput.value.trim()) {
    ElMessage.warning('请输入节点集、边集和根节点')
    return
  }

  feedback.value = []

  try {
    const response = await callBackendApi('/tree-traversal/validate', {
      method: 'POST',
      body: JSON.stringify({
        nodesString: nodesInput.value.trim(),
        edgesString: edgesInput.value.trim(),
        rootString: rootInput.value.trim()
      })
    })

    if (response.valid) {
      feedback.value.push({
        formula: `T = (V, E)`,
        type: 'success',
        message: '树的输入格式正确且为有效树结构'
      })
      ElMessage.success('输入格式正确')
    } else {
      feedback.value.push({
        formula: `T = (V, E)`,
        type: 'error',
        message: response.message
      })
      ElMessage.error('输入格式不正确')
    }
  } catch (error) {
    console.error('检查输入失败:', error)
    feedback.value.push({
      formula: `T = (V, E)`,
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
  if (exampleTrees[exampleKey]) {
    const example = exampleTrees[exampleKey]

    // 清除之前的结果
    results.value = []
    feedback.value = []
    counter.value = 0

    // 设置示例数据
    nodesInput.value = example.nodes
    edgesInput.value = example.edges
    rootInput.value = example.root

    // 设置选项
    Object.assign(options.value, example.options)

    console.log('TreeTraversalInterface: 加载示例', exampleKey, example)
    ElMessage.info(`已加载示例：${exampleKey}`)
  }
}

const handleImageError = (event) => {
  console.error('树形可视化加载失败:', event.target.src)
  ElMessage.error('树形可视化加载失败')
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
.tree-traversal-interface {
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

.tree-input-section {
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

.tree-textarea {
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
  background: #fafafa;
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

.root-info {
  margin-top: 1rem;
  padding: 0.5rem;
  background: #e8f5e8;
  border-radius: 4px;
  border: 1px solid #c3e6cb;
}

.root-formula {
  display: inline-block;
  margin-left: 0.5rem;
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

.traversal-result {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.traversal-formula {
  margin: 1rem 0;
  font-size: 1.1rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
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

/* 响应式设计 */
@media (max-width: 768px) {
  .tree-traversal-interface {
    max-height: 90vh;
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

  .result-item {
    padding: 1rem;
  }
}
</style>