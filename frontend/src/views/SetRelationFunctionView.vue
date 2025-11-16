<template>
  <div class="set-relation-function-modals">
    <!-- 单个集合运算界面模态框 -->
    <el-dialog
      v-model="showSetOperation"
      title="单个集合的运算"
      width="90%"
      :before-close="handleSetOperationClose"
      class="set-operation-dialog"
    >
      <set-operation-interface
        @close="handleSetOperationClose"
        @set-operation-result="onSetOperationResult"
      />
    </el-dialog>

    <!-- 集合表达式运算界面模态框 -->
    <el-dialog
      v-model="showSetExprOperation"
      title="集合表达式运算"
      width="90%"
      :before-close="handleSetExprOperationClose"
      class="set-expr-operation-dialog"
    >
      <set-expr-operation-interface
        @close="handleSetExprOperationClose"
        @set-expr-operation-result="onSetExprOperationResult"
      />
    </el-dialog>

    <!-- 单个关系运算界面模态框 -->
    <el-dialog
      v-model="showRelationOperation"
      title="单个关系的运算"
      width="90%"
      :before-close="handleRelationOperationClose"
      class="relation-operation-dialog"
    >
      <relation-operation-interface
        @close="handleRelationOperationClose"
        @relation-operation-result="onRelationOperationResult"
      />
    </el-dialog>

    <!-- 关系性质判断界面模态框 -->
    <el-dialog
      v-model="showRelationProperty"
      title="关系性质判断"
      width="90%"
      :before-close="handleRelationPropertyClose"
      class="relation-property-dialog"
    >
      <relation-property-interface
        @close="handleRelationPropertyClose"
        @relation-property-result="onRelationPropertyResult"
      />
    </el-dialog>

    <!-- 关系闭包计算界面模态框 -->
    <el-dialog
      v-model="showRelationClosure"
      title="关系闭包的计算"
      width="90%"
      :before-close="handleRelationClosureClose"
      class="relation-closure-dialog"
    >
      <relation-closure-interface
        @close="handleRelationClosureClose"
        @relation-closure-result="onRelationClosureResult"
      />
    </el-dialog>

    <!-- 等价关系计算界面模态框 -->
    <el-dialog
      v-model="showEquivalenceRelation"
      title="等价关系的计算"
      width="90%"
      :before-close="handleEquivalenceRelationClose"
      class="equivalence-relation-dialog"
    >
      <equivalence-interface
        @close="handleEquivalenceRelationClose"
        @equivalence-result="onEquivalenceResult"
      />
    </el-dialog>

    <!-- 偏序关系计算界面模态框 -->
    <el-dialog
      v-model="showPartialOrder"
      title="偏序关系的计算"
      width="90%"
      :before-close="handlePartialOrderClose"
      class="partial-order-dialog"
    >
      <partial-order-interface
        @close="handlePartialOrderClose"
        @partial-order-result="onPartialOrderResult"
      />
    </el-dialog>

    <!-- 函数性质判断界面模态框 -->
    <el-dialog
      v-model="showFunction"
      title="函数性质的判断"
      width="90%"
      :before-close="handleFunctionClose"
      class="function-dialog"
    >
      <div class="coming-soon">
        <el-empty description="功能开发中，敬请期待">
          <el-button type="primary" @click="showFunction = false">返回</el-button>
        </el-empty>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import SetOperationInterface from '@/components/setrelfun/SetOperationInterface.vue'
import SetExprOperationInterface from '@/components/setrelfun/SetExprOperationInterface.vue'
import RelationOperationInterface from '@/components/setrelfun/RelationOperationInterface.vue'
import RelationPropertyInterface from '@/components/setrelfun/RelationPropertyInterface.vue'
import RelationClosureInterface from '@/components/setrelfun/RelationClosureInterface.vue'
import EquivalenceInterface from '@/components/setrelfun/EquivalenceInterface.vue'
import PartialOrderInterface from '@/components/setrelfun/PartialOrderInterface.vue'
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
  'set-operation-result',
  'set-expr-operation-result',
  'relation-operation-result',
  'relation-property-result',
  'relation-closure-result',
  'equivalence-relation-result',
  'partial-order-result',
  'function-result',
  'update-current-formula',
  'update-latex-code'
])

// 控制各个界面的显示
const showSetOperation = ref(false)
const showSetExprOperation = ref(false)
const showRelationOperation = ref(false)
const showRelationProperty = ref(false)
const showRelationClosure = ref(false)
const showEquivalenceRelation = ref(false)
const showPartialOrder = ref(false)
const showFunction = ref(false)

// 打开各个功能界面的方法 - 暴露给父组件调用
const openSetOperation = () => {
  showSetOperation.value = true
}

const openSetExprOperation = () => {
  showSetExprOperation.value = true
}

const openRelationOperation = () => {
  showRelationOperation.value = true
}

const openRelationProperty = () => {
  showRelationProperty.value = true
}

const openRelationClosure = () => {
  showRelationClosure.value = true
}

const openEquivalenceRelation = () => {
  showEquivalenceRelation.value = true
}

const openPartialOrder = () => {
  showPartialOrder.value = true
}

const openFunction = () => {
  showFunction.value = true
}

// 处理界面关闭
const handleSetOperationClose = () => {
  showSetOperation.value = false
}

const handleSetExprOperationClose = () => {
  showSetExprOperation.value = false
}

const handleRelationOperationClose = () => {
  showRelationOperation.value = false
}

const handleRelationPropertyClose = () => {
  showRelationProperty.value = false
}

const handleRelationClosureClose = () => {
  showRelationClosure.value = false
}

const handleEquivalenceRelationClose = () => {
  showEquivalenceRelation.value = false
}

const handlePartialOrderClose = () => {
  showPartialOrder.value = false
}

const handleFunctionClose = () => {
  showFunction.value = false
}

// 处理单个集合运算结果
const onSetOperationResult = (result) => {
  console.log('SetRelationFunctionView: 接收到集合运算结果', result)

  if (result && result.success) {
    const formattedResult = {
      index: props.formulaResults.length + 1,
      formula: result.formula || '集合运算分析',
      type: result.type || 'set-operation',
      setU: result.setU,
      setA: result.setA,
      setB: result.setB,
      intersectionResult: result.intersectionResult,
      unionResult: result.unionResult,
      subtractResult: result.subtractResult,
      complementAResult: result.complementAResult,
      complementBResult: result.complementBResult,
      differenceResult: result.differenceResult,
      powerSetAResult: result.powerSetAResult,
      powerSetBResult: result.powerSetBResult,
      success: result.success,
      errorMessage: result.errorMessage
    }

    // 使用工具函数生成LaTeX代码
    const latexString = generateLaTeXCode(formattedResult)
    console.log('SetRelationFunctionView: 生成的LaTeX代码长度:', latexString?.length || 0)

    // 发送结果给父组件，包含LaTeX代码
    emit('set-operation-result', { result: formattedResult, latexString })

    ElMessage.success('集合运算结果已添加到主界面')
  } else {
    ElMessage.error(result?.errorMessage || '集合运算失败')
  }
}

// 处理等价关系计算结果
const onEquivalenceResult = (result) => {
  console.log('SetRelationFunctionView: 接收到等价关系计算结果', result)

  if (result && result.success) {
    const formattedResult = {
      index: props.formulaResults.length + 1,
      formula: result.formula || '等价关系分析',
      type: result.type || 'equivalence-relation',
      setAString: result.setAString,
      relationRString: result.relationRString,
      intTypeElement: result.intTypeElement,
      isReflexive: result.isReflexive,
      isSymmetric: result.isSymmetric,
      isTransitive: result.isTransitive,
      isEquivalenceRelation: result.isEquivalenceRelation,
      reflexiveResult: result.reflexiveResult,
      symmetricResult: result.symmetricResult,
      transitiveResult: result.transitiveResult,
      relationMatrix: result.relationMatrix,
      relationGraphUrl: result.relationGraphUrl,
      equivalenceClosure: result.equivalenceClosure,
      equivalenceClosureMatrix: result.equivalenceClosureMatrix,
      equivalenceClosureGraphUrl: result.equivalenceClosureGraphUrl,
      equivalenceClasses: result.equivalenceClasses,
      quotientSet: result.quotientSet,
      success: result.success,
      errorMessage: result.errorMessage
    }

    // 使用工具函数生成LaTeX代码
    const latexString = generateLaTeXCode(formattedResult)
    console.log('SetRelationFunctionView: 生成的LaTeX代码长度:', latexString?.length || 0)

    // 发送结果给父组件，包含LaTeX代码
    emit('equivalence-relation-result', { result: formattedResult, latexString })

    ElMessage.success('等价关系计算结果已添加到主界面')
  } else {
    ElMessage.error(result?.errorMessage || '等价关系计算失败')
  }
}

const onSetExprOperationResult = (result) => {
  console.log('SetRelationFunctionView: 接收到集合表达式运算结果', result)

  if (result && result.success) {
    const formattedResult = {
      index: props.formulaResults.length + 1,
      formula: result.formula || '集合表达式运算分析',
      type: result.type || 'setExpressionOperation',
      result: result.result,
      latexResult: result.latexResult,
      index: result.index
    }

    // 使用 generateLaTeXCode 生成 LaTeX 字符串
    const latexString = generateLaTeXCode(formattedResult)

    // 发送结果到主界面
    emit('set-expr-operation-result', { result: formattedResult, latexString })

    ElMessage.success('集合表达式运算结果已添加到主界面')
  } else {
    ElMessage.error(result?.errorMessage || '集合表达式运算失败')
  }
}

const onRelationOperationResult = (result) => {
  console.log('SetRelationFunctionView: 接收到关系运算结果', result)

  if (result && result.success) {
    const formattedResult = {
      index: props.formulaResults.length + 1,
      formula: result.formula || '关系运算分析',
      type: result.type || 'relationOperation',
      setA: result.setA,
      setB: result.setB,
      setC: result.setC,
      relationR: result.relationR,
      relationS: result.relationS,
      relationRLatex: result.relationRLatex,
      relationSLatex: result.relationSLatex,
      intersectionResult: result.intersectionResult,
      unionResult: result.unionResult,
      subtractResult: result.subtractResult,
      inverseRResult: result.inverseRResult,
      inverseSResult: result.inverseSResult,
      compositeResult: result.compositeResult,
      invcompResult: result.invcompResult,
      relationRMatrix: result.relationRMatrix,
      relationSMatrix: result.relationSMatrix,
      success: result.success,
      errorMessage: result.errorMessage
    }

    // 使用 generateLaTeXCode 生成 LaTeX 字符串
    const latexString = generateLaTeXCode(formattedResult)

    // 发送结果到主界面
    emit('relation-operation-result', { result: formattedResult, latexString })

    ElMessage.success('关系运算结果已添加到主界面')
  } else {
    ElMessage.error(result?.errorMessage || '关系运算失败')
  }
}

const onRelationPropertyResult = (result) => {
  console.log('SetRelationFunctionView: 接收到关系性质判断结果', result)

  if (result && result.success) {
    const formattedResult = {
      index: props.formulaResults.length + 1,
      formula: result.formula || '关系性质判断分析',
      type: result.type || 'relation-property',
      setAString: result.setAString,
      relationRString: result.relationRString,
      intTypeElement: result.intTypeElement,
      matrixString: result.matrixString,
      graphImageUrl: result.graphImageUrl,
      reflexiveResult: result.reflexiveResult,
      irreflexiveResult: result.irreflexiveResult,
      symmetricResult: result.symmetricResult,
      antisymmetricResult: result.antisymmetricResult,
      transitiveResult: result.transitiveResult,
      ...result
    }

    const latexString = generateLaTeXCode(formattedResult)
    emit('relation-property-result', {
      result: formattedResult,
      latexString: latexString
    })

    ElMessage.success('关系性质判断结果已添加到主界面')
  } else {
    ElMessage.error(result?.errorMessage || '关系性质判断失败')
  }
}

const onRelationClosureResult = (result) => {
  console.log('SetRelationFunctionView: 接收到关系闭包计算结果', result)

  if (result && result.success) {
    const formattedResult = {
      index: props.formulaResults.length + 1,
      formula: result.formula || '关系闭包计算分析',
      type: result.type || 'relation-closure',
      setAString: result.setAString,
      relationRString: result.relationRString,
      intTypeElement: result.intTypeElement,
      relationMatrix: result.relationMatrix,
      relationGraphUrl: result.relationGraphUrl,
      reflexiveClosureResult: result.reflexiveClosureResult,
      symmetricClosureResult: result.symmetricClosureResult,
      transitiveClosureResult: result.transitiveClosureResult,
      equivalenceClosureResult: result.equivalenceClosureResult,
      ...result
    }

    // 使用 generateLaTeXCode 生成 LaTeX 字符串
    const latexString = generateLaTeXCode(formattedResult)

    // 发送结果到主界面
    emit('relation-closure-result', { result: formattedResult, latexString })

    ElMessage.success('关系闭包计算结果已添加到主界面')
  } else {
    ElMessage.error(result?.errorMessage || '关系闭包计算失败')
  }
}

// 处理偏序关系计算结果
const onPartialOrderResult = (result) => {
  console.log('SetRelationFunctionView: 接收到偏序关系计算结果', result)

  if (result && result.success) {
    const formattedResult = {
      index: props.formulaResults.length + 1,
      formula: result.formula || '偏序关系分析',
      type: result.type || 'partial-order',
      setAString: result.setAString,
      setSString: result.setSString,
      relationRString: result.relationRString,
      intTypeElement: result.intTypeElement,
      isPartialOrder: result.isPartialOrder,
      isReflexive: result.isReflexive,
      isAntisymmetric: result.isAntisymmetric,
      isTransitive: result.isTransitive,
      relationMatrix: result.relationMatrix,
      relationGraphUrl: result.relationGraphUrl,
      hasseDiagramUrl: result.hasseDiagramUrl,
      minimumElements: result.minimumElements,
      maximumElements: result.maximumElements,
      leastElement: result.leastElement,
      greatestElement: result.greatestElement,
      lowerBounds: result.lowerBounds,
      upperBounds: result.upperBounds,
      greatestLowerBound: result.greatestLowerBound,
      leastUpperBound: result.leastUpperBound,
      success: result.success,
      errorMessage: result.errorMessage
    }

    // 使用工具函数生成LaTeX代码
    const latexString = generateLaTeXCode(formattedResult)
    console.log('SetRelationFunctionView: 生成的LaTeX代码长度:', latexString?.length || 0)

    // 发送结果给父组件，包含LaTeX代码
    emit('partial-order-result', { result: formattedResult, latexString })

    ElMessage.success('偏序关系计算结果已添加到主界面')
  } else {
    ElMessage.error(result?.errorMessage || '偏序关系计算失败')
  }
}

// 暴露方法给父组件
defineExpose({
  openSetOperation,
  openSetExprOperation,
  openRelationOperation,
  openRelationProperty,
  openRelationClosure,
  openEquivalenceRelation,
  openPartialOrder,
  openFunction
})
</script>

<style scoped>

.coming-soon {
  padding: 3rem;
  text-align: center;
}

/* 对话框样式保持一致 */
:deep(.set-operation-dialog),
:deep(.set-expr-operation-dialog),
:deep(.relation-operation-dialog),
:deep(.relation-property-dialog),
:deep(.relation-closure-dialog),
:deep(.equivalence-relation-dialog),
:deep(.partial-order-dialog),
:deep(.function-dialog) {
  display: flex;
  flex-direction: column;
}

:deep(.el-dialog__body) {
  padding: 1rem;
  max-height: 80vh;
  overflow-y: auto;
}
</style>