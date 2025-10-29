<template>
  <div class="graph-theory-modals">
    <!-- 图遍历界面模态框 -->
    <el-dialog
      v-model="showGraphTravel"
      title="图的遍历"
      width="90%"
      :before-close="handleGraphTravelClose"
      class="graph-travel-dialog"
    >
      <graph-travel-interface
        @close="handleGraphTravelClose"
        @graph-analyzed="onGraphTravelResult"
      />
    </el-dialog>

    <!-- 树遍历界面模态框 -->
    <el-dialog
      v-model="showTreeTravel"
      title="树的遍历"
      width="90%"
      :before-close="handleTreeTravelClose"
      class="tree-travel-dialog"
    >
      <div class="coming-soon">
        <el-empty description="功能开发中，敬请期待">
          <el-button type="primary" @click="showTreeTravel = false">返回</el-button>
        </el-empty>
      </div>
    </el-dialog>

    <!-- 最短路径计算界面模态框 -->
    <el-dialog
      v-model="showShortestPath"
      title="带权图最短路径计算"
      width="90%"
      :before-close="handleShortestPathClose"
      class="shortest-path-dialog"
    >
      <div class="coming-soon">
        <el-empty description="功能开发中，敬请期待">
          <el-button type="primary" @click="showShortestPath = false">返回</el-button>
        </el-empty>
      </div>
    </el-dialog>

    <!-- 最小生成树计算界面模态框 -->
    <el-dialog
      v-model="showSpanningTree"
      title="带权图最小生成树计算"
      width="90%"
      :before-close="handleSpanningTreeClose"
      class="spanning-tree-dialog"
    >
      <div class="coming-soon">
        <el-empty description="功能开发中，敬请期待">
          <el-button type="primary" @click="showSpanningTree = false">返回</el-button>
        </el-empty>
      </div>
    </el-dialog>

    <!-- 哈夫曼树构造界面模态框 -->
    <el-dialog
      v-model="showHuffmanTree"
      title="哈夫曼树构造"
      width="90%"
      :before-close="handleHuffmanTreeClose"
      class="huffman-tree-dialog"
    >
      <div class="coming-soon">
        <el-empty description="功能开发中，敬请期待">
          <el-button type="primary" @click="showHuffmanTree = false">返回</el-button>
        </el-empty>
      </div>
    </el-dialog>

    <!-- 特殊图展示界面模态框 -->
    <el-dialog
      v-model="showSpecialGraph"
      title="展示特殊的图"
      width="90%"
      :before-close="handleSpecialGraphClose"
      class="special-graph-dialog"
    >
      <div class="coming-soon">
        <el-empty description="功能开发中，敬请期待">
          <el-button type="primary" @click="showSpecialGraph = false">返回</el-button>
        </el-empty>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import GraphTravelInterface from '@/components/graph/GraphTravelInterface.vue'
import { generateLaTeXCode } from '@/utils/latexGenerator.js'

// 定义 props 和 emits
const props = defineProps({
  formulaResults: {
    type: Array,
    default: () => []
  },
  currentFormula: {
    type: String,
    default: ''
  },
  latexCode: {
    type: String,
    default: ''
  }
})

const emit = defineEmits([
  'graph-travel-result',
  'tree-travel-result',
  'shortest-path-result',
  'spanning-tree-result',
  'huffman-tree-result',
  'special-graph-result',
  'update-current-formula',
  'update-latex-code'
])

// 控制各个界面的显示
const showGraphTravel = ref(false)
const showTreeTravel = ref(false)
const showShortestPath = ref(false)
const showSpanningTree = ref(false)
const showHuffmanTree = ref(false)
const showSpecialGraph = ref(false)

// 打开各个功能界面的方法 - 暴露给父组件调用
const openGraphTravel = () => {
  showGraphTravel.value = true
}

const openTreeTravel = () => {
  showTreeTravel.value = true
}

const openShortestPath = () => {
  showShortestPath.value = true
}

const openSpanningTree = () => {
  showSpanningTree.value = true
}

const openHuffmanTree = () => {
  showHuffmanTree.value = true
}

const openSpecialGraph = () => {
  showSpecialGraph.value = true
}

// 处理界面关闭
const handleGraphTravelClose = () => {
  showGraphTravel.value = false
}

// 处理图遍历结果
const onGraphTravelResult = (result) => {
  console.log('GraphTheoryView: 接收到图遍历结果', result)

  if (result && result.success) {
    const formattedResult = {
      index: props.formulaResults.length + 1,
      formula: result.formula || '图遍历分析',
      nodesString: result.nodesString,
      edgesString: result.edgesString,
      directed: result.directed,
      nodeDegrees: result.nodeDegrees,
      adjacencyMatrix: result.adjacencyMatrix,
      incidenceMatrix: result.incidenceMatrix,
      pathMatrices: result.pathMatrices,
      dfsResult: result.dfsResult,
      bfsResult: result.bfsResult,
      graphImageUrl: result.graphImageUrl,
      success: result.success,
      errorMessage: result.errorMessage
    }

    // 使用工具函数生成LaTeX代码
    const latexString = generateLaTeXCode(formattedResult)
    console.log('GraphTheoryView: 生成的LaTeX代码长度:', latexString?.length || 0)

    // 发送结果给父组件，包含LaTeX代码
    emit('graph-travel-result', { result: formattedResult, latexString })

    ElMessage.success('图遍历分析结果已添加到主界面')
  } else {
    ElMessage.error(result?.errorMessage || '图遍历分析失败')
  }
}

const handleTreeTravelClose = () => {
  showTreeTravel.value = false
}

const handleShortestPathClose = () => {
  showShortestPath.value = false
}

const handleSpanningTreeClose = () => {
  showSpanningTree.value = false
}

const handleHuffmanTreeClose = () => {
  showHuffmanTree.value = false
}

const handleSpecialGraphClose = () => {
  showSpecialGraph.value = false
}

// 暴露方法给父组件
defineExpose({
  openGraphTravel,
  openTreeTravel,
  openShortestPath,
  openSpanningTree,
  openHuffmanTree,
  openSpecialGraph
})
</script>

<style scoped>
.graph-theory-modals {
  /* 不需要额外样式，只用于包装模态框 */
}

.coming-soon {
  padding: 3rem;
  text-align: center;
}

/* 对话框样式保持一致 */
:deep(.graph-travel-dialog),
:deep(.tree-travel-dialog),
:deep(.shortest-path-dialog),
:deep(.spanning-tree-dialog),
:deep(.huffman-tree-dialog),
:deep(.special-graph-dialog) {
  display: flex;
  flex-direction: column;
}

:deep(.el-dialog__body) {
  padding: 1rem;
  max-height: 80vh;
  overflow-y: auto;
}
</style>