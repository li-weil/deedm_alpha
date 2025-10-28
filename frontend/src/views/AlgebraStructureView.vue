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
      <div class="coming-soon">
        <el-empty description="功能开发中，敬请期待">
          <el-button type="primary" @click="showBinaryOperator = false">返回</el-button>
        </el-empty>
      </div>
    </el-dialog>

    <!-- 群U(m)分析界面模态框 -->
    <el-dialog
      v-model="showGroupUm"
      title="群U(m)及其子群与陪集"
      width="90%"
      :before-close="handleGroupUmClose"
      class="group-um-dialog"
    >
      <div class="coming-soon">
        <el-empty description="功能开发中，敬请期待">
          <el-button type="primary" @click="showGroupUm = false">返回</el-button>
        </el-empty>
      </div>
    </el-dialog>

    <!-- 置换群分析界面模态框 -->
    <el-dialog
      v-model="showGroupPerm"
      title="置换群及其子群与陪集"
      width="90%"
      :before-close="handleGroupPermClose"
      class="group-perm-dialog"
    >
      <div class="coming-soon">
        <el-empty description="功能开发中，敬请期待">
          <el-button type="primary" @click="showGroupPerm = false">返回</el-button>
        </el-empty>
      </div>
    </el-dialog>

    <!-- 格判断界面模态框 -->
    <el-dialog
      v-model="showLattice"
      title="偏序关系是否格的判断"
      width="90%"
      :before-close="handleLatticeClose"
      class="lattice-dialog"
    >
      <div class="coming-soon">
        <el-empty description="功能开发中，敬请期待">
          <el-button type="primary" @click="showLattice = false">返回</el-button>
        </el-empty>
      </div>
    </el-dialog>

    <!-- 布尔代数判断界面模态框 -->
    <el-dialog
      v-model="showBoolean"
      title="整除与布尔代数的判断"
      width="90%"
      :before-close="handleBooleanClose"
      class="boolean-dialog"
    >
      <div class="coming-soon">
        <el-empty description="功能开发中，敬请期待">
          <el-button type="primary" @click="showBoolean = false">返回</el-button>
        </el-empty>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'

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
  'boolean-result',
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

const handleGroupUmClose = () => {
  showGroupUm.value = false
}

const handleGroupPermClose = () => {
  showGroupPerm.value = false
}

const handleLatticeClose = () => {
  showLattice.value = false
}

const handleBooleanClose = () => {
  showBoolean.value = false
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