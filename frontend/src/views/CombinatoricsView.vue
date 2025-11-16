<template>
  <div class="combinatorics-modals">
    <!-- 排列组合数计算界面模态框 -->
    <el-dialog
      v-model="showCombCalculator"
      title="排列组合数计算"
      width="90%"
      :before-close="handleCombCalculatorClose"
      class="comb-calculator-dialog"
    >
      <comb-calculator-interface
        @close="handleCombCalculatorClose"
        @comb-calculator-result="onCombCalculatorResult"
      />
    </el-dialog>

    <!-- 组合表达式计算界面模态框 -->
    <el-dialog
      v-model="showExprCalculator"
      title="组合表达式计算"
      width="90%"
      :before-close="handleExprCalculatorClose"
      class="expr-calculator-dialog"
    >
      <expression-calculator-interface
        @close="handleExprCalculatorClose"
        @expression-calculator-result="onExpressionCalculatorResult"
      />
    </el-dialog>

    <!-- 递归表达式计算界面模态框 -->
    <el-dialog
      v-model="showRecuExprCalculator"
      title="递归表达式计算"
      width="90%"
      :before-close="handleRecuExprCalculatorClose"
      class="recu-expr-calculator-dialog"
    >
      <div class="coming-soon">
        <el-empty description="功能开发中，敬请期待">
          <el-button type="primary" @click="showRecuExprCalculator = false">返回</el-button>
        </el-empty>
      </div>
    </el-dialog>

    <!-- 字符串计数界面模态框 -->
    <el-dialog
      v-model="showCountString"
      title="字符串计数"
      width="90%"
      :before-close="handleCountStringClose"
      class="count-string-dialog"
    >
      <div class="coming-soon">
        <el-empty description="功能开发中，敬请期待">
          <el-button type="primary" @click="showCountString = false">返回</el-button>
        </el-empty>
      </div>
    </el-dialog>

    <!-- 整数计数界面模态框 -->
    <el-dialog
      v-model="showCountInteger"
      title="基于整除性质的整数计数"
      width="90%"
      :before-close="handleCountIntegerClose"
      class="count-integer-dialog"
    >
      <div class="coming-soon">
        <el-empty description="功能开发中，敬请期待">
          <el-button type="primary" @click="showCountInteger = false">返回</el-button>
        </el-empty>
      </div>
    </el-dialog>

    <!-- 不定方程求解界面模态框 -->
    <el-dialog
      v-model="showCountSolver"
      title="不定方程非负整数解计数"
      width="90%"
      :before-close="handleCountSolverClose"
      class="count-solver-dialog"
    >
      <div class="coming-soon">
        <el-empty description="功能开发中，敬请期待">
          <el-button type="primary" @click="showCountSolver = false">返回</el-button>
        </el-empty>
      </div>
    </el-dialog>

    <!-- 函数计数界面模态框 -->
    <el-dialog
      v-model="showCountFunction"
      title="不同性质的函数计数"
      width="90%"
      :before-close="handleCountFunctionClose"
      class="count-function-dialog"
    >
      <div class="coming-soon">
        <el-empty description="功能开发中，敬请期待">
          <el-button type="primary" @click="showCountFunction = false">返回</el-button>
        </el-empty>
      </div>
    </el-dialog>

    <!-- 排列生成界面模态框 -->
    <el-dialog
      v-model="showGeneratePermutation"
      title="排列的生成(G)"
      width="90%"
      :before-close="handleGeneratePermutationClose"
      class="generate-permutation-dialog"
    >
      <div class="coming-soon">
        <el-empty description="功能开发中，敬请期待">
          <el-button type="primary" @click="showGeneratePermutation = false">返回</el-button>
        </el-empty>
      </div>
    </el-dialog>

    <!-- 组合生成界面模态框 -->
    <el-dialog
      v-model="showGenerateCombination"
      title="不重复组合的生成(N)"
      width="90%"
      :before-close="handleGenerateCombinationClose"
      class="generate-combination-dialog"
    >
      <div class="coming-soon">
        <el-empty description="功能开发中，敬请期待">
          <el-button type="primary" @click="showGenerateCombination = false">返回</el-button>
        </el-empty>
      </div>
    </el-dialog>

    <!-- 重复组合生成界面模态框 -->
    <el-dialog
      v-model="showGenerateRepcomb"
      title="允许重复组合的生成(F)"
      width="90%"
      :before-close="handleGenerateRepcombClose"
      class="generate-repcomb-dialog"
    >
      <div class="coming-soon">
        <el-empty description="功能开发中，敬请期待">
          <el-button type="primary" @click="showGenerateRepcomb = false">返回</el-button>
        </el-empty>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import CombCalculatorInterface from '@/components/counting/CombCalculatorInterface.vue'
import ExpressionCalculatorInterface from '@/components/counting/ExpressionCalculatorInterface.vue'
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
  'comb-calculator-result',
  'expr-calculator-result',
  'recu-expr-calculator-result',
  'count-string-result',
  'count-integer-result',
  'count-solver-result',
  'count-function-result',
  'generate-permutation-result',
  'generate-combination-result',
  'generate-repcomb-result',
  'update-current-formula',
  'update-latex-code'
])

// 控制各个界面的显示
const showCombCalculator = ref(false)
const showExprCalculator = ref(false)
const showRecuExprCalculator = ref(false)
const showCountString = ref(false)
const showCountInteger = ref(false)
const showCountSolver = ref(false)
const showCountFunction = ref(false)
const showGeneratePermutation = ref(false)
const showGenerateCombination = ref(false)
const showGenerateRepcomb = ref(false)

// 打开各个功能界面的方法 - 暴露给父组件调用
const openCombCalculator = () => {
  showCombCalculator.value = true
}

const openExprCalculator = () => {
  showExprCalculator.value = true
}

const openRecuExprCalculator = () => {
  showRecuExprCalculator.value = true
}

const openCountString = () => {
  showCountString.value = true
}

const openCountInteger = () => {
  showCountInteger.value = true
}

const openCountSolver = () => {
  showCountSolver.value = true
}

const openCountFunction = () => {
  showCountFunction.value = true
}

const openGeneratePermutation = () => {
  showGeneratePermutation.value = true
}

const openGenerateCombination = () => {
  showGenerateCombination.value = true
}

const openGenerateRepcomb = () => {
  showGenerateRepcomb.value = true
}

// 处理界面关闭
const handleCombCalculatorClose = () => {
  showCombCalculator.value = false
}

const handleExprCalculatorClose = () => {
  showExprCalculator.value = false
}

const handleRecuExprCalculatorClose = () => {
  showRecuExprCalculator.value = false
}

const handleCountStringClose = () => {
  showCountString.value = false
}

const handleCountIntegerClose = () => {
  showCountInteger.value = false
}

const handleCountSolverClose = () => {
  showCountSolver.value = false
}

const handleCountFunctionClose = () => {
  showCountFunction.value = false
}

const handleGeneratePermutationClose = () => {
  showGeneratePermutation.value = false
}

const handleGenerateCombinationClose = () => {
  showGenerateCombination.value = false
}

const handleGenerateRepcombClose = () => {
  showGenerateRepcomb.value = false
}

// 处理排列组合数计算结果
const onCombCalculatorResult = (result) => {
  try {
    console.log('CombinatoricsView: 处理排列组合数计算结果', result)

    // 生成LaTeX代码
    const latexString = generateLaTeXCode(result)

    // 发送结果到主界面
    emit('comb-calculator-result', result)
    emit('update-current-formula', result.formula)
    emit('update-latex-code', latexString)

    ElMessage.success('排列组合数计算结果已发送到主界面')
  } catch (error) {
    console.error('处理排列组合数计算结果失败:', error)
    ElMessage.error('处理结果失败: ' + error.message)
  }
}

const onExpressionCalculatorResult = (result) => {
  try {
    console.log('CombinatoricsView: 处理组合表达式计算结果', result)

    // 生成LaTeX代码
    const latexString = generateLaTeXCode(result)

    // 发送结果到主界面
    emit('expression-calculator-result', result)
    emit('update-current-formula', result.originalExpression || result.formula)
    emit('update-latex-code', latexString)

    ElMessage.success('组合表达式计算结果已发送到主界面')
  } catch (error) {
    console.error('处理组合表达式计算结果失败:', error)
    ElMessage.error('处理结果失败: ' + error.message)
  }
}

// 暴露方法给父组件
defineExpose({
  openCombCalculator,
  openExprCalculator,
  openRecuExprCalculator,
  openCountString,
  openCountInteger,
  openCountSolver,
  openCountFunction,
  openGeneratePermutation,
  openGenerateCombination,
  openGenerateRepcomb
})
</script>

<style scoped>

.coming-soon {
  padding: 3rem;
  text-align: center;
}

/* 对话框样式保持一致 */
:deep(.comb-calculator-dialog),
:deep(.expr-calculator-dialog),
:deep(.recu-expr-calculator-dialog),
:deep(.count-string-dialog),
:deep(.count-integer-dialog),
:deep(.count-solver-dialog),
:deep(.count-function-dialog),
:deep(.generate-permutation-dialog),
:deep(.generate-combination-dialog),
:deep(.generate-repcomb-dialog) {
  display: flex;
  flex-direction: column;
}

:deep(.el-dialog__body) {
  padding: 1rem;
  max-height: 80vh;
  overflow-y: auto;
}
</style>