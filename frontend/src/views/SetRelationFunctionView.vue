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
      <div class="coming-soon">
        <el-empty description="功能开发中，敬请期待">
          <el-button type="primary" @click="showSetExprOperation = false">返回</el-button>
        </el-empty>
      </div>
    </el-dialog>

    <!-- 单个关系运算界面模态框 -->
    <el-dialog
      v-model="showRelationOperation"
      title="单个关系的运算"
      width="90%"
      :before-close="handleRelationOperationClose"
      class="relation-operation-dialog"
    >
      <div class="coming-soon">
        <el-empty description="功能开发中，敬请期待">
          <el-button type="primary" @click="showRelationOperation = false">返回</el-button>
        </el-empty>
      </div>
    </el-dialog>

    <!-- 关系性质判断界面模态框 -->
    <el-dialog
      v-model="showRelationProperty"
      title="关系性质判断"
      width="90%"
      :before-close="handleRelationPropertyClose"
      class="relation-property-dialog"
    >
      <div class="coming-soon">
        <el-empty description="功能开发中，敬请期待">
          <el-button type="primary" @click="showRelationProperty = false">返回</el-button>
        </el-empty>
      </div>
    </el-dialog>

    <!-- 关系闭包计算界面模态框 -->
    <el-dialog
      v-model="showRelationClosure"
      title="关系闭包的计算"
      width="90%"
      :before-close="handleRelationClosureClose"
      class="relation-closure-dialog"
    >
      <div class="coming-soon">
        <el-empty description="功能开发中，敬请期待">
          <el-button type="primary" @click="showRelationClosure = false">返回</el-button>
        </el-empty>
      </div>
    </el-dialog>

    <!-- 等价关系计算界面模态框 -->
    <el-dialog
      v-model="showEquivalenceRelation"
      title="等价关系的计算"
      width="90%"
      :before-close="handleEquivalenceRelationClose"
      class="equivalence-relation-dialog"
    >
      <div class="coming-soon">
        <el-empty description="功能开发中，敬请期待">
          <el-button type="primary" @click="showEquivalenceRelation = false">返回</el-button>
        </el-empty>
      </div>
    </el-dialog>

    <!-- 偏序关系计算界面模态框 -->
    <el-dialog
      v-model="showPartialOrder"
      title="偏序关系的计算"
      width="90%"
      :before-close="handlePartialOrderClose"
      class="partial-order-dialog"
    >
      <div class="coming-soon">
        <el-empty description="功能开发中，敬请期待">
          <el-button type="primary" @click="showPartialOrder = false">返回</el-button>
        </el-empty>
      </div>
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