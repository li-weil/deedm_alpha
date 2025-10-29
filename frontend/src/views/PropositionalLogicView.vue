<template>
  <div class="propositional-logic-modals">
    <!-- 公式语法分析界面模态框 -->
    <el-dialog
      v-model="showFormulaSyntax"
      title="分析公式的语法"
      width="90%"
      :before-close="handleFormulaSyntaxClose"
      class="formula-syntax-dialog"
    >
      <formula-syntax-interface
        @close="showFormulaSyntax = false"
        @formula-calculated="onFormulaCalculated"
      />
    </el-dialog>

    <!-- 真值计算界面模态框 -->
    <el-dialog
      v-model="showTruthValue"
      title="计算公式的真值"
      width="90%"
      :before-close="handleTruthValueClose"
      class="truth-value-dialog"
    >
      <truth-value-calculator
        @close="showTruthValue = false"
        @formula-calculated="onFormulaCalculated"
      />
    </el-dialog>

    <!-- 真值表构造器界面模态框 -->
    <el-dialog
      v-model="showTruthTableConstructor"
      title="构造公式的真值表"
      width="90%"
      :before-close="handleTruthTableConstructorClose"
      class="truth-table-constructor-dialog"
    >
      <truth-table-constructor
        @close="showTruthTableConstructor = false"
        @formula-calculated="onFormulaCalculated"
      />
    </el-dialog>

    <!-- 主范式界面模态框 -->
    <el-dialog
      v-model="showPrincipalNormalForm"
      title="计算与公式逻辑等值的范式"
      width="90%"
      :before-close="handlePrincipalNormalFormClose"
      class="principal-normal-form-dialog"
    >
      <principal-normal-form-interface
        @close="showPrincipalNormalForm = false"
        @formula-calculated="onFormulaCalculated"
      />
    </el-dialog>

    <!-- 范式扩展界面模态框 -->
    <el-dialog
      v-model="showNormalFormulaExpansion"
      title="将范式扩展为主范式"
      width="90%"
      :before-close="handleNormalFormulaExpansionClose"
      class="normal-form-expansion-dialog"
    >
      <normal-formula-expansion-interface
        @close="showNormalFormulaExpansion = false"
        @result="onNormalFormulaExpansionResult"
      />
    </el-dialog>

    <!-- 等值演算过程检查界面模态框 -->
    <el-dialog
      v-model="showEquivCalculusCheck"
      title="等值演算过程检查"
      width="90%"
      :before-close="handleEquivCalculusCheckClose"
      class="equiv-calculus-check-dialog"
    >
      <equiv-calculus-check-interface
        @close="showEquivCalculusCheck = false"
        @result="onEquivCalculusResult"
      />
    </el-dialog>

    <!-- 推理有效性论证检查对话框 -->
    <el-dialog
      v-model="showReasonArgumentCheck"
      title="验证推理有效性论证检查"
      width="90%"
      :before-close="handleReasonArgumentCheckClose"
      class="reason-argument-check-dialog"
    >
      <reason-argument-check-interface
        @close="showReasonArgumentCheck = false"
        @result="onReasonArgumentCheckResult"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { generateLaTeXCode } from '@/utils/latexGenerator.js'

// 导入所有子组件
import FormulaSyntaxInterface from '@/components/logic/FormulaSyntaxInterface.vue'
import TruthValueCalculator from '@/components/logic/TruthValueCalculator.vue'
import TruthTableConstructor from '@/components/logic/TruthTableConstructor.vue'
import PrincipalNormalFormInterface from '@/components/logic/PrincipalNormalFormInterface.vue'
import NormalFormulaExpansionInterface from '@/components/logic/NormalFormulaExpansionInterface.vue'
import EquivCalculusCheckInterface from '@/components/logic/EquivCalculusCheckInterface.vue'
import ReasonArgumentCheckInterface from '@/components/logic/ReasonArgumentCheckInterface.vue'

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
  'formula-calculated',
  'equiv-calculus-result',
  'reason-argument-check-result',
  'normal-form-expansion-result',
  'update-current-formula',
  'update-latex-code'
])

// 控制各个界面的显示
const showFormulaSyntax = ref(false)
const showTruthValue = ref(false)
const showTruthTableConstructor = ref(false)
const showPrincipalNormalForm = ref(false)
const showNormalFormulaExpansion = ref(false)
const showEquivCalculusCheck = ref(false)
const showReasonArgumentCheck = ref(false)

// 打开各个功能界面的方法 - 暴露给父组件调用
const openFormulaSyntax = () => {
  showFormulaSyntax.value = true
}

const openTruthValue = () => {
  showTruthValue.value = true
}

const openTruthTable = () => {
  showTruthTableConstructor.value = true
}

const openPrincipalNormalForm = () => {
  showPrincipalNormalForm.value = true
}

const openNormalFormulaExpansion = () => {
  showNormalFormulaExpansion.value = true
}

const openEquivCalculusCheck = () => {
  showEquivCalculusCheck.value = true
}

const openReasonArgumentCheck = () => {
  showReasonArgumentCheck.value = true
}

// 处理界面关闭
const handleFormulaSyntaxClose = () => {
  showFormulaSyntax.value = false
}

const handleTruthValueClose = () => {
  showTruthValue.value = false
}

const handleTruthTableConstructorClose = () => {
  showTruthTableConstructor.value = false
}

const handlePrincipalNormalFormClose = () => {
  showPrincipalNormalForm.value = false
}

const handleNormalFormulaExpansionClose = () => {
  showNormalFormulaExpansion.value = false
}

const handleEquivCalculusCheckClose = () => {
  showEquivCalculusCheck.value = false
}

const handleReasonArgumentCheckClose = () => {
  showReasonArgumentCheck.value = false
}

// 处理公式计算完成事件
const onFormulaCalculated = (result) => {
  if (!result.index) {
    result.index = props.formulaResults.length + 1
  }

  // 确保语法数据和AST数据被正确保留
  if (result.syntaxData || result.astData) {
    result.type = 'formula-syntax'
  }

  // 使用工具函数生成LaTeX代码
  console.log('PropositionalLogicView: 调用generateLaTeXCode函数，结果对象:', result)
  const latexString = generateLaTeXCode(result)
  console.log('PropositionalLogicView: generateLaTeXCode返回的latexString:', latexString)
  console.log('PropositionalLogicView: latexString长度:', latexString?.length || 0)

  console.log('PropositionalLogicView: 处理公式计算结果:', result)
  console.log('PropositionalLogicView: AST数据:', result.astData)

  // 发送结果给父组件，包含LaTeX代码
  emit('formula-calculated', { result, latexString })
}

// 处理等值演算检查结果
const onEquivCalculusResult = (result) => {
  if (result && result.data) {
    const formattedResult = {
      index: props.formulaResults.length + 1,
      formula: `等值演算检查步骤`,
      type: 'equiv-calculus-check',
      stepNumber: result.data.stepNumber,
      steps: result.data.steps,
      valid: result.data.valid,
      errorMessage: result.data.errorMessage,
      counterExample: result.data.counterExample,
      checkingFormula: result.data.checkingFormula,
      success: result.data.success,
      message: result.data.message
    }

    // 使用工具函数生成LaTeX代码
    const latexString = generateLaTeXCode(formattedResult)

    // 发送结果给父组件，包含LaTeX代码
    emit('equiv-calculus-result', { result: formattedResult, latexString })
  }
}

// 处理推理有效性论证检查结果
const onReasonArgumentCheckResult = (result) => {
  if (result && result.data) {
    const formattedResult = {
      index: props.formulaResults.length + 1,
      formula: `推理有效性论证检查步骤`,
      type: 'reason-argument-check',
      stepNumber: result.data.stepNumber || props.formulaResults.length + 1,
      steps: result.data.steps,
      checkSteps: result.data.checkSteps,
      valid: result.data.valid,
      errorMessage: result.data.message,
      counterExample: result.data.counterExample,
      checkingFormula: result.data.checkingFormula,
      success: result.data.success,
      latexString: result.data.latexString,
      premises: result.data.premises,
      consequent: result.data.consequent
    }

    // 使用工具函数生成LaTeX代码
    const latexString = generateLaTeXCode(formattedResult)

    // 发送结果给父组件，包含LaTeX代码
    emit('reason-argument-check-result', { result: formattedResult, latexString })
  }
}

// 处理范式扩展结果
const onNormalFormulaExpansionResult = (result) => {
  if (result && result.data) {
    const formattedResult = {
      index: props.formulaResults.length + 1,
      formula: result.data.originalFormula,
      type: 'normal-form-expansion',
      targetType: result.data.targetType,
      originalFormula: result.data.originalFormula,
      variableSet: result.data.variableSet,
      expansionSteps: result.data.expansionSteps,
      pdnfResult: result.data.pdnfResult,
      pcnfResult: result.data.pcnfResult,
      success: result.data.success,
      message: result.data.message
    }

    // 使用工具函数生成LaTeX代码
    const latexString = generateLaTeXCode(formattedResult)

    // 发送结果给父组件，包含LaTeX代码
    emit('normal-form-expansion-result', { result: formattedResult, latexString })
  }
}

// 暴露方法给父组件
defineExpose({
  openFormulaSyntax,
  openTruthValue,
  openTruthTable,
  openPrincipalNormalForm,
  openNormalFormulaExpansion,
  openEquivCalculusCheck,
  openReasonArgumentCheck
})
</script>

<style scoped>
.propositional-logic-modals {
  /* 包装所有模态框的容器 */
  position: relative;
}

/* 对话框样式保持一致 */
:deep(.formula-syntax-dialog),
:deep(.truth-value-dialog),
:deep(.truth-table-constructor-dialog),
:deep(.principal-normal-form-dialog),
:deep(.normal-form-expansion-dialog),
:deep(.equiv-calculus-check-dialog),
:deep(.reason-argument-check-dialog) {
  display: flex;
  flex-direction: column;
}

:deep(.el-dialog__body) {
  padding: 1rem;
  max-height: 80vh;
  overflow-y: auto;
}
</style>