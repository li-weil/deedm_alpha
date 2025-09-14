<template>
  <div class="math-renderer" :class="{ 'inline': inline }">
    <div v-if="loading" class="loading">
      <el-icon class="loading-icon"><Loading /></el-icon>
      <span>公式加载中...</span>
    </div>
    <div v-else-if="error" class="error">
      <el-icon><WarningFilled /></el-icon>
      <span>公式渲染失败: {{ error }}</span>
    </div>
    <div v-else class="math-content" ref="mathContainer"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Loading, WarningFilled } from '@element-plus/icons-vue'

const props = defineProps({
  formula: {
    type: String,
    required: true
  },
  type: {
    type: String,
    default: 'latex', // 'latex', 'mathjax', 'katex'
    validator: (value) => ['latex', 'mathjax', 'katex'].includes(value)
  },
  inline: {
    type: Boolean,
    default: false
  },
  displayMode: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['rendered', 'error'])

const mathContainer = ref(null)
const loading = ref(false)
const error = ref(null)

// Load required libraries
const loadMathLibrary = async () => {
  try {
    if (props.type === 'mathjax') {
      await loadMathJax()
    } else if (props.type === 'katex') {
      await loadKaTeX()
    }
  } catch (err) {
    console.error('Failed to load math library:', err)
    error.value = '数学库加载失败'
    emit('error', err)
  }
}

const loadMathJax = async () => {
  if (window.MathJax) return

  return new Promise((resolve, reject) => {
    const script = document.createElement('script')
    script.src = 'https://cdn.jsdelivr.net/npm/mathjax@3/es5/tex-mml-chtml.js'
    script.async = true
    script.onload = resolve
    script.onerror = reject
    document.head.appendChild(script)
  })
}

const loadKaTeX = async () => {
  if (window.katex) return

  // Load KaTeX CSS
  const cssLink = document.createElement('link')
  cssLink.rel = 'stylesheet'
  cssLink.href = 'https://cdn.jsdelivr.net/npm/katex@0.16.9/dist/katex.min.css'
  document.head.appendChild(cssLink)

  // Load KaTeX JS
  return new Promise((resolve, reject) => {
    // Check if already loaded
    if (window.katex) {
      resolve()
      return
    }

    const script = document.createElement('script')
    script.src = 'https://cdn.jsdelivr.net/npm/katex@0.16.9/dist/katex.min.js'
    script.async = true
    script.onload = () => {
      console.log('KaTeX loaded successfully')
      resolve()
    }
    script.onerror = (err) => {
      console.error('Failed to load KaTeX:', err)
      reject(err)
    }
    document.head.appendChild(script)
  })
}

const renderFormula = async () => {
  if (!props.formula || !mathContainer.value) return

  loading.value = true
  error.value = null

  try {
    // Try to load KaTeX first, but fallback to simple rendering if it fails
    if (props.type === 'katex') {
      try {
        await loadKaTeX()
        await nextTick()
        renderKaTeX()
      } catch (katexError) {
        console.warn('KaTeX failed, falling back to simple rendering:', katexError)
        renderLaTeX()
      }
    } else if (props.type === 'mathjax') {
      try {
        await loadMathJax()
        await nextTick()
        renderMathJax()
      } catch (mathjaxError) {
        console.warn('MathJax failed, falling back to simple rendering:', mathjaxError)
        renderLaTeX()
      }
    } else {
      renderLaTeX()
    }

    emit('rendered')
  } catch (err) {
    console.error('Formula rendering failed:', err)
    error.value = '公式渲染失败'
    emit('error', err)
  } finally {
    loading.value = false
  }
}

const renderKaTeX = () => {
  if (!window.katex) {
    console.warn('KaTeX not available, using simple rendering')
    renderLaTeX()
    return
  }

  try {
    const displayMode = props.displayMode || !props.inline

    // Clean the formula for KaTeX
    let cleanFormula = props.formula

    // Handle LaTeX math mode delimiters first
    if (cleanFormula.startsWith('$') && cleanFormula.endsWith('$')) {
      cleanFormula = cleanFormula.slice(1, -1) // Remove $ delimiters
    }

    // Handle double backslashes for KaTeX
    cleanFormula = cleanFormula.replace(/\\\\([a-zA-Z]+)/g, '\\$1')

    console.log('Attempting KaTeX rendering:', cleanFormula)

    window.katex.render(cleanFormula, mathContainer.value, {
      displayMode,
      throwOnError: false,
      trust: true,
      strict: false,
      output: 'html'
    })

    console.log('KaTeX rendering successful')
  } catch (err) {
    console.warn('KaTeX rendering failed, falling back to simple rendering:', err)
    renderLaTeX()
  }
}

const renderMathJax = () => {
  if (!window.MathJax) {
    throw new Error('MathJax not loaded')
  }

  let formula = props.formula

  // Handle LaTeX math mode delimiters first
  if (formula.startsWith('$') && formula.endsWith('$')) {
    formula = formula.slice(1, -1) // Remove $ delimiters
  }

  const mathText = props.displayMode || !props.inline
    ? `\\[${formula}\\]`
    : `\\(${formula}\\)`

  mathContainer.value.innerHTML = mathText

  if (window.MathJax.typeset) {
    window.MathJax.typeset([mathContainer.value])
  }
}

const renderLaTeX = () => {
  // Enhanced LaTeX rendering for basic formulas
  // This is a fallback for when external libraries fail
  let renderedFormula = props.formula

  // Direct replacement approach - handle common LaTeX patterns directly
  console.log('Original formula:', renderedFormula)

  // Handle LaTeX math mode delimiters first
  if (renderedFormula.startsWith('$') && renderedFormula.endsWith('$')) {
    renderedFormula = renderedFormula.slice(1, -1) // Remove $ delimiters
  }

  // Handle specific LaTeX commands that return from backend
  renderedFormula = renderedFormula.replace(/\\mathbf\{([01])\}/g, '$1') // Convert \mathbf{0} to 0, \mathbf{1} to 1
  renderedFormula = renderedFormula.replace(/\\texttt\{([a-z])\}/g, '$1') // Convert \texttt{p} to p, etc.

  // First, handle double backslashes
  renderedFormula = renderedFormula.replace(/\\\\wedge/g, '∧')
  renderedFormula = renderedFormula.replace(/\\\\vee/g, '∨')
  renderedFormula = renderedFormula.replace(/\\\\neg/g, '¬')
  renderedFormula = renderedFormula.replace(/\\\\rightarrow/g, '→')
  renderedFormula = renderedFormula.replace(/\\\\leftrightarrow/g, '↔')

  // Then handle single backslashes (fallback)
  renderedFormula = renderedFormula.replace(/\\wedge/g, '∧')
  renderedFormula = renderedFormula.replace(/\\vee/g, '∨')
  renderedFormula = renderedFormula.replace(/\\neg/g, '¬')
  renderedFormula = renderedFormula.replace(/\\rightarrow/g, '→')
  renderedFormula = renderedFormula.replace(/\\leftrightarrow/g, '↔')

  console.log('After direct replacements:', renderedFormula)

  // Handle additional symbols if needed
  renderedFormula = renderedFormula.replace(/\\forall/g, '∀')
  renderedFormula = renderedFormula.replace(/\\exists/g, '∃')
  renderedFormula = renderedFormula.replace(/\\in/g, '∈')
  renderedFormula = renderedFormula.replace(/\\subset/g, '⊂')
  renderedFormula = renderedFormula.replace(/\\cup/g, '∪')
  renderedFormula = renderedFormula.replace(/\\cap/g, '∩')
  renderedFormula = renderedFormula.replace(/\\emptyset/g, '∅')

  mathContainer.value.innerHTML = renderedFormula
  mathContainer.value.className += ' simple-latex'
  console.log('Simple LaTeX rendering completed:', renderedFormula)
}

// Watch for formula changes
watch(() => props.formula, renderFormula, { immediate: true })

onMounted(() => {
  renderFormula()
})
</script>

<style scoped>
.math-renderer {
  display: inline-block;
}

.math-renderer.inline {
  display: inline;
}

.loading {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #909399;
  font-size: 0.9rem;
}

.loading-icon {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.error {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #f56c6c;
  font-size: 0.9rem;
}

.math-content {
  line-height: 1.6;
}

.math-content:deep(.formula-error) {
  color: #f56c6c;
  font-style: italic;
}

.math-content:deep(.simple-latex) {
  font-family: 'Times New Roman', serif;
  font-style: italic;
}

/* KaTeX specific styles */
:deep(.katex) {
  font-size: 1em;
}

:deep(.katex-display) {
  margin: 1em 0;
}

:deep(.katex-error) {
  color: #f56c6c;
}

/* MathJax specific styles */
:deep(.MathJax) {
  font-size: 1em !important;
}

:deep(.MathJax_CHTML) {
  font-size: 1em !important;
}
</style>