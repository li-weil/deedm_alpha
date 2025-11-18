<template>
  <div class="algebra-structure-modals">
    <!-- 运算性质判断界面模态框 -->
    <el-dialog
      v-model="showBinaryOperator"
      title="运算性质的判断"
      width="90%"
      :before-close="handleBinaryOperatorClose"
      class="binary-operator-dialog"
    >
      <operation-property-interface
        @close="handleBinaryOperatorClose"
        @operation-property-result="onOperationPropertyResult"
      />
    </el-dialog>

    <!-- 群U(m)分析界面模态框 -->
    <el-dialog
      v-model="showGroupUm"
      title="群U(m)及其子群与陪集"
      width="90%"
      :before-close="handleGroupUmClose"
      class="group-um-dialog"
    >
      <group-um-interface
        @close="handleGroupUmClose"
        @group-um-result="onGroupUmResult"
      />
    </el-dialog>

    <!-- 置换群分析界面模态框 -->
    <el-dialog
      v-model="showGroupPerm"
      title="置换群及其子群与陪集"
      width="90%"
      :before-close="handleGroupPermClose"
      class="group-perm-dialog"
    >
      <group-perm-interface
        @close="handleGroupPermClose"
        @group-perm-result="onGroupPermResult"
      />
    </el-dialog>

    <!-- 格判断界面模态框 -->
    <el-dialog
      v-model="showLattice"
      title="偏序关系是否格的判断"
      width="90%"
      :before-close="handleLatticeClose"
      class="lattice-dialog"
    >
      <lattice-judge-interface
        @close="handleLatticeClose"
        @lattice-judge-result="onLatticeJudgeResult"
      />
    </el-dialog>

    <!-- 布尔代数判断界面模态框 -->
    <el-dialog
      v-model="showBoolean"
      title="整除与布尔代数的判断"
      width="90%"
      :before-close="handleBooleanClose"
      class="boolean-dialog"
    >
      <bool-algebra-interface
        @close="handleBooleanClose"
        @bool-algebra-result="onBoolAlgebraResult"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { generateLaTeXCode } from '@/utils/latexGenerator'
import OperationPropertyInterface from '@/components/algebra/OperationPropertyInterface.vue'
import GroupUmInterface from '@/components/algebra/GroupUmInterface.vue'
import GroupPermInterface from '@/components/algebra/GroupPermInterface.vue'
import LatticeJudgeInterface from '@/components/algebra/LatticeJudgeInterface.vue'
import BoolAlgebraInterface from '@/components/algebra/BoolAlgebraInterface.vue'

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
  'binary-operator-result',
  'group-um-result',
  'group-perm-result',
  'lattice-result',
  'bool-algebra-result',
  'update-current-formula',
  'update-latex-code'
])

// 控制各个界面的显示
const showBinaryOperator = ref(false)
const showGroupUm = ref(false)
const showGroupPerm = ref(false)
const showLattice = ref(false)
const showBoolean = ref(false)

// 打开各个功能界面的方法 - 暴露给父组件调用
const openBinaryOperator = () => {
  showBinaryOperator.value = true
}

const openGroupUm = () => {
  showGroupUm.value = true
}

const openGroupPerm = () => {
  showGroupPerm.value = true
}

const openLattice = () => {
  showLattice.value = true
}

const openBoolean = () => {
  showBoolean.value = true
}

// 处理界面关闭
const handleBinaryOperatorClose = () => {
  showBinaryOperator.value = false
}

// 处理运算性质判断结果
const onOperationPropertyResult = (result) => {
  console.log('AlgebraStructureView: 收到运算性质判断结果', result)

  // 生成LaTeX代码
  const latexString = generateLaTeXCode(result)

  // 发送结果到主界面
  emit('binary-operator-result', {
    ...result,
    latexString
  })

  emit('update-latex-code', latexString)
}

const handleGroupUmClose = () => {
  showGroupUm.value = false
}

// 处理群U(m)分析结果
const onGroupUmResult = (result) => {
  console.log('AlgebraStructureView: 收到群U(m)分析结果', result)

  // 生成LaTeX代码
  const latexString = generateLaTeXCode(result)

  // 发送结果到主界面
  emit('group-um-result', {
    ...result,
    latexString
  })

  emit('update-latex-code', latexString)
}

const handleGroupPermClose = () => {
  showGroupPerm.value = false
}

// 处理置换群分析结果
const onGroupPermResult = (result) => {
  console.log('AlgebraStructureView: 收到置换群分析结果', result)

  // 生成LaTeX代码
  const latexString = generateLaTeXCode(result)

  // 发送结果到主界面
  emit('group-perm-result', {
    ...result,
    latexString
  })

  emit('update-latex-code', latexString)
}

const handleLatticeClose = () => {
  showLattice.value = false
}

// 处理格判断结果
const onLatticeJudgeResult = (result) => {
  console.log('AlgebraStructureView: 收到格判断结果', result)

  // 生成LaTeX代码
  const latexString = generateLaTeXCode(result)

  // 发送结果到主界面
  emit('lattice-result', {
    ...result,
    latexString
  })

  emit('update-latex-code', latexString)
}

const handleBooleanClose = () => {
  showBoolean.value = false
}

const onBoolAlgebraResult = (result) => {
  console.log('AlgebraStructureView: 收到布尔代数分析结果', result)

    // 生成LaTeX代码
  const latexString = generateLaTeXCode(result)

  // 发送结果到主界面
  emit('bool-algebra-result', {
    ...result,
    latexString
  })

  emit('update-latex-code', latexString)
}

// 暴露方法给父组件
defineExpose({
  openBinaryOperator,
  openGroupUm,
  openGroupPerm,
  openLattice,
  openBoolean
})
</script>

<style scoped>

.coming-soon {
  padding: 3rem;
  text-align: center;
}

/* 对话框样式保持一致 */
:deep(.binary-operator-dialog),
:deep(.group-um-dialog),
:deep(.group-perm-dialog),
:deep(.lattice-dialog),
:deep(.boolean-dialog) {
  display: flex;
  flex-direction: column;
}

:deep(.el-dialog__body) {
  padding: 1rem;
  max-height: 80vh;
  overflow-y: auto;
}
</style>