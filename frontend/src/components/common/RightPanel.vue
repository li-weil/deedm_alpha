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

  // 检查事件目标是否为textarea内部
  const target = event.target
  const isTextareaInner = target.closest('.el-textarea__inner')

  // 如果在textarea内部且可以滚动，让textarea处理滚动
  if (isTextareaInner) {
    const textarea = target.closest('.el-textarea__inner')
    if (textarea && textarea.scrollHeight > textarea.clientHeight) {
      // 检查是否还有滚动空间
      const canScrollUp = textarea.scrollTop > 0
      const canScrollDown = textarea.scrollTop < textarea.scrollHeight - textarea.clientHeight

      const delta = event.deltaY || event.detail || event.wheelDelta
      const scrollDirection = delta > 0 ? 1 : -1

      // 如果还有滚动空间，让textarea处理
      if ((scrollDirection > 0 && canScrollDown) || (scrollDirection < 0 && canScrollUp)) {
        return // 让textarea处理滚动
      }
    }
  }

  // 阻止默认滚动行为和事件冒泡
  event.preventDefault()
  event.stopPropagation()

  // 获取滚动增量，支持不同浏览器和设备
  const delta = event.deltaY || event.detail || event.wheelDelta

  // 根据不同浏览器处理滚动方向
  const scrollDirection = delta > 0 ? 1 : -1

  // 动态计算滚动步长，考虑以下因素：
  // 1. 基础步长
  // 2. 滚动强度（delta值大小）
  // 3. 系统滚动偏好（如果可用）
  const baseStepSize = 30
  const intensityMultiplier = Math.min(Math.abs(delta) / 100, 3) // 限制最大倍数
  const stepSize = baseStepSize * (1 + intensityMultiplier * 0.5)

  // 计算最终滚动距离
  const scrollDistance = scrollDirection * stepSize

  // 使用平滑滚动
  const currentScrollTop = content.value.scrollTop
  const targetScrollTop = currentScrollTop + scrollDistance

  // 确保不超出边界
  const maxScrollTop = content.value.scrollHeight - content.value.clientHeight
  const finalScrollTop = Math.max(0, Math.min(targetScrollTop, maxScrollTop))

  // 使用requestAnimationFrame实现平滑滚动
  if (content.value.smoothScrolling) {
    // 如果正在滚动中，取消之前的滚动
    cancelAnimationFrame(content.value.smoothScrolling)
  }

  const animateScroll = () => {
    const distance = finalScrollTop - content.value.scrollTop
    if (Math.abs(distance) > 1) {
      content.value.scrollTop += distance * 0.2 // 平滑系数
      content.value.smoothScrolling = requestAnimationFrame(animateScroll)
    } else {
      content.value.scrollTop = finalScrollTop
      content.value.smoothScrolling = null
    }
  }

  content.value.smoothScrolling = requestAnimationFrame(animateScroll)
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