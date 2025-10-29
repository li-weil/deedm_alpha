<template>
  <div class="right-panel" @wheel="handleWheel">
    <div class="panel-header">
      <h3>LaTeX 代码</h3>
      <el-button size="small" type="warning" @click="handleClear">
        <el-icon><Delete /></el-icon>
        清空
      </el-button>
    </div>
    <div class="panel-content" ref="content">
      <el-input
        :model-value="latexCode"
        type="textarea"
        :placeholder="latexCode ? '' : 'LaTeX代码将在计算后显示在这里...\n\n请使用上方菜单进行公式计算，结果将在此处显示LaTeX代码。'"
        readonly
        class="latex-textarea"
        :autosize="{ minRows: 10, maxRows: 50 }"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { Delete } from '@element-plus/icons-vue'

// 定义 props
const props = defineProps({
  latexCode: {
    type: String,
    default: ''
  }
})

// 定义 emits
const emit = defineEmits(['clear'])

// 监听latexCode变化
watch(() => props.latexCode, (newCode) => {
  console.log('RightPanel: latexCode属性更新:', newCode)
  console.log('RightPanel: latexCode长度:', newCode?.length || 0)
})

onMounted(() => {
  console.log('RightPanel: 组件已挂载，初始latexCode:', props.latexCode)
})

// 引用
const content = ref(null)

// 处理鼠标滚轮事件
const handleWheel = (event) => {
  // 确保事件存在有效的内容引用
  if (!content.value) return

  // 阻止默认滚动行为和事件冒泡
  if (event.preventDefault) {
    event.preventDefault()
  } else {
    event.returnValue = false // IE兼容
  }

  if (event.stopPropagation) {
    event.stopPropagation()
  } else {
    event.cancelBubble = true // IE兼容
  }

  // 获取滚动增量
  const delta = event.deltaY || event.detail || event.wheelDelta

  // 根据不同浏览器处理滚动方向
  const scrollDelta = delta > 0 ? 1 : -1

  // 执行滚动
  content.value.scrollTop += scrollDelta * 40 // 40px每次滚动
}

// 处理清空按钮点击
const handleClear = () => {
  emit('clear')
}



</script>

<style scoped>
.right-panel {
  width: 50%;
  height: 100%;
  border: 1px solid #e4e7ed;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  min-width: 300px;
  flex: 1;
  border-left: 2px solid #dcdfe6;
}

.panel-header {
  background: #f5f7fa;
  padding: 1rem;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.panel-header h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 1.1rem;
  flex: 1;
}

.panel-content {
  flex: 1;
  overflow: hidden;
  padding: 1rem;
  scroll-behavior: smooth;
  display: flex;
  flex-direction: column;
}

.latex-textarea {
  width: 100%;
  flex: 1;
  min-height: 200px;
  display: flex;
  flex-direction: column;
}

.latex-textarea :deep(.el-textarea) {
  width: 100%;
  height: 100% !important;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.latex-textarea :deep(.el-textarea__inner) {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.5;
  resize: none;
  border: 1px solid #dcdfe6;
  background: #fafafa;
  height: 100% !important;
  min-height: 200px;
  max-height: none;
  flex: 1;
  padding: 12px;
  border-radius: 4px;
  overflow-y: auto;
}

/* 滚动条样式 */
.panel-content::-webkit-scrollbar,
.latex-textarea :deep(.el-textarea__inner)::-webkit-scrollbar {
  width: 8px;
}

.panel-content::-webkit-scrollbar-track,
.latex-textarea :deep(.el-textarea__inner)::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.panel-content::-webkit-scrollbar-thumb,
.latex-textarea :deep(.el-textarea__inner)::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.panel-content::-webkit-scrollbar-thumb:hover,
.latex-textarea :deep(.el-textarea__inner)::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .right-panel {
    width: 100%;
    height: 50%;
    min-width: unset;
  }

  .latex-textarea :deep(.el-textarea__inner) {
    max-height: 40vh;
  }
}

@media (max-width: 480px) {
  .panel-header {
    padding: 0.5rem;
  }

  .panel-header h3 {
    font-size: 1rem;
  }

  .panel-content {
    padding: 0.5rem;
  }

  .latex-textarea :deep(.el-textarea__inner) {
    font-size: 12px;
  }
}
</style>