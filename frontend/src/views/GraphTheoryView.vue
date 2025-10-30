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
      <tree-traversal-interface
        @close="handleTreeTravelClose"
        @tree-traversed="onTreeTraversalResult"
      />
    </el-dialog>

    <!-- 最短路径计算界面模态框 -->
    <el-dialog
      v-model="showShortestPath"
      title="带权图最短路径计算"
      width="90%"
      :before-close="handleShortestPathClose"
      class="shortest-path-dialog"
    >
      <shortest-path-interface
        @close="handleShortestPathClose"
        @shortest-path-calculated="onShortestPathResult"
      />
    </el-dialog>

    <!-- 最小生成树计算界面模态框 -->
    <el-dialog
      v-model="showSpanningTree"
      title="带权图最小生成树计算"
      width="90%"
      :before-close="handleSpanningTreeClose"
      class="spanning-tree-dialog"
    >
      <spanning-tree-interface
        @close="handleSpanningTreeClose"
        @spanning-tree-calculated="onSpanningTreeResult"
      />
    </el-dialog>

    <!-- 哈夫曼树构造界面模态框 -->
    <el-dialog
      v-model="showHuffmanTree"
      title="哈夫曼树构造"
      width="90%"
      :before-close="handleHuffmanTreeClose"
      class="huffman-tree-dialog"
    >
      <huffman-tree-interface
        @close="handleHuffmanTreeClose"
        @huffman-tree-constructed="onHuffmanTreeResult"
      />
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
import TreeTraversalInterface from '@/components/graph/TreeTraversalInterface.vue'
import ShortestPathInterface from '@/components/graph/ShortestPathInterface.vue'
import SpanningTreeInterface from '@/components/graph/SpanningTreeInterface.vue'
import HuffmanTreeInterface from '@/components/graph/HuffmanTreeInterface.vue'
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
      type: result.type || 'graph-travel',
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

// 处理树遍历结果
const onTreeTraversalResult = (result) => {
  console.log('GraphTheoryView: 接收到树遍历结果', result)

  if (result && result.success) {
    const formattedResult = {
      index: props.formulaResults.length + 1,
      formula: result.formula || '树遍历分析',
      type: result.type || 'tree-travel',
      rootNode: result.rootNode,
      adjacencyMatrix: result.adjacencyMatrix,
      incidenceMatrix: result.incidenceMatrix,
      preorderResult: result.preorderResult,
      inorderResult: result.inorderResult,
      postorderResult: result.postorderResult,
      graphImageUrl: result.graphImageUrl,
      success: result.success,
      errorMessage: result.errorMessage
    }

    // 使用工具函数生成LaTeX代码
    const latexString = generateLaTeXCode(formattedResult)
    console.log('GraphTheoryView: 生成的LaTeX代码长度:', latexString?.length || 0)

    // 发送结果给父组件，包含LaTeX代码
    emit('tree-travel-result', { result: formattedResult, latexString })

    ElMessage.success('树遍历分析结果已添加到主界面')
  } else {
    ElMessage.error(result?.errorMessage || '树遍历分析失败')
  }
}

const handleShortestPathClose = () => {
  showShortestPath.value = false
}

// 处理最短路径计算结果
const onShortestPathResult = (result) => {
  if (result && result.success) {
    const formattedResult = {
      index: props.formulaResults.length + 1,
      formula: result.formula || '带权图最短路径计算',
      type: result.type || 'shortest-path',
      nodesString: result.nodesString,
      edgesString: result.edgesString,
      startNode: result.startNode,
      directed: result.directed,
      adjacencyMatrix: result.adjacencyMatrix,
      dijkstraDetails: result.dijkstraDetails,
      shortestPaths: result.shortestPaths,
      graphImageUrl: result.graphImageUrl,
      pathGraphImageUrl: result.pathGraphImageUrl,
      success: result.success,
      errorMessage: result.errorMessage
    }

    // 使用工具函数生成LaTeX代码
    const latexString = generateLaTeXCode(formattedResult)

    // 发送结果给父组件，包含LaTeX代码（与图遍历格式一致）
    emit('shortest-path-result', { result: formattedResult, latexString })

    ElMessage.success('带权图最短路径计算结果已添加到主界面')
  } else {
    ElMessage.error(result?.errorMessage || '带权图最短路径计算失败')
  }
}

// 处理最小生成树计算结果
const onSpanningTreeResult = (result) => {
  if (result && result.success) {
    const formattedResult = {
      index: props.formulaResults.length + 1,
      formula: result.formula || '带权图最小生成树计算',
      type: result.type || 'spanning-tree',
      nodesString: result.nodesString,
      edgesString: result.edgesString,
      directed: result.directed,
      weightMatrix: result.weightMatrix,
      kruskalResult: result.kruskalResult,
      primResult: result.primResult,
      graphImageUrl: result.graphImageUrl,
      kruskalTreeImageUrl: result.kruskalTreeImageUrl,
      primTreeImageUrl: result.primTreeImageUrl,
      success: result.success,
      errorMessage: result.errorMessage
    }

    // 使用工具函数生成LaTeX代码
    const latexString = generateLaTeXCode(formattedResult)
    console.log('GraphTheoryView: 生成的LaTeX代码长度:', latexString?.length || 0)

    // 发送结果给父组件，包含LaTeX代码
    emit('spanning-tree-result', { result: formattedResult, latexString })

    ElMessage.success('带权图最小生成树计算结果已添加到主界面')
  } else {
    ElMessage.error(result?.errorMessage || '带权图最小生成树计算失败')
  }
}

const handleSpanningTreeClose = () => {
  showSpanningTree.value = false
}

// 处理Huffman树构造结果
const onHuffmanTreeResult = (result) => {
  if (result && result.success) {
    const formattedResult = {
      index: props.formulaResults.length + 1,
      formula: result.formula || '哈夫曼树构造',
      type: result.type || 'huffmanTree',
      leafSetLaTeX: result.leafSetLaTeX,
      steps: result.steps,
      treeImageUrl: result.treeImageUrl,
      totalWeightLaTeX: result.totalWeightLaTeX,
      totalWeight: result.totalWeight,
      leafCodes: result.leafCodes,
      success: result.success,
      errorMessage: result.errorMessage
    }

    // 使用工具函数生成LaTeX代码
    const latexString = generateLaTeXCode(formattedResult)

    // 发送结果到主界面
    emit('huffman-tree-result', {
      result: formattedResult,
      latexString
    })

    // 不自动关闭模态框，让用户继续查看详细结果
    ElMessage.success('哈夫曼树构造结果已添加到主界面，您可以继续查看详细结果或手动关闭此界面')
  } else {
    ElMessage.error(result?.errorMessage || '哈夫曼树构造失败')
  }
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