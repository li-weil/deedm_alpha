<template>
  <!-- 公式渲染器 -->
  <div class="math-renderer" :class="{ 'inline': inline }">
    <!-- 加载中 -->
    <div v-if="loading" class="loading">
      <el-icon class="loading-icon"><Loading /></el-icon>
      <span>加载中...</span>
    </div>
    <!-- 错误提示 -->
    <div v-else-if="error" class="error">
      <el-icon><WarningFilled /></el-icon>
      <span>渲染失败: {{ error }}</span>
    </div>
    <!-- 公式内容 -->
    <div
      class="math-content"
      ref="mathContainer"
      :data-formula="formula"
      :style="{ display: loading || error ? 'none' : 'block' }"
    >
    </div>
  </div>
</template>

<script setup>
// 导入依赖
import { ref, onMounted, watch, nextTick } from 'vue'
import { Loading, WarningFilled } from '@element-plus/icons-vue'

// 组件属性
const props = defineProps({
  formula: { type: String, required: true },
  type: {
    type: String,
    default: 'katex',
    validator: (value) => ['latex', 'mathjax', 'katex'].includes(value)
  },
  inline: { type: Boolean, default: false },
  displayMode: { type: Boolean, default: false }
})

// 事件
const emit = defineEmits(['rendered', 'error'])

// 响应式状态，状态更新会重新加载DOM组件
const mathContainer = ref(null)      // DOM容器
const loading = ref(false)            // 加载状态
const error = ref(null)              // 错误状态
const katexLoaded = ref(false)        // 库加载状态

// 动态加载 MathJax 库
const loadMathJax = async () => {
  // 检查 MathJax 是否已加载并就绪
  if (window.MathJax && window.MathJax.typesetPromise) {
    katexLoaded.value = true
    console.log('MathJax 已加载并就绪')
    return
  }

  try {
    console.log('正在加载 MathJax...')

    // 配置 MathJax
    window.MathJax = {
      tex: {
        inlineMath: [['$', '$'], ['\\(', '\\)']],
        displayMath: [['$$', '$$'], ['\\[', '\\]']],
        processEscapes: true
      },
      startup: {
        typeset: false
      }
    }

    // 多个 CDN 源配置
    const mathjaxSources = [
      'https://cdn.jsdelivr.net/npm/mathjax@3.2.2/es5/tex-mml-chtml.js',
      'https://unpkg.com/mathjax@3.2.2/es5/tex-mml-chtml.js',
      'https://cdnjs.cloudflare.com/ajax/libs/mathjax/3.2.2/es5/tex-mml-chtml.js',
      'https://cdn.jsdelivr.net/npm/mathjax@3/es5/tex-mml-chtml.js',
      'https://unpkg.com/mathjax@3/es5/tex-mml-chtml.js',
    ]

    let loadSuccess = false
    let lastError = null

    // 尝试从各个 CDN 加载
    for (let i = 0; i < mathjaxSources.length; i++) {
      try {
        console.log(`尝试从 CDN ${i + 1}/${mathjaxSources.length} 加载`)

        await new Promise((resolve, reject) => {
          const script = document.createElement('script')
          script.src = mathjaxSources[i]
          script.async = false
          script.timeout = 10000

          script.onload = () => {
            // 等待 MathJax 初始化完成
            let attempts = 0
            const maxAttempts = 100

            const checkReady = () => {
              attempts++
              if (window.MathJax && window.MathJax.typesetPromise) {
                console.log(`MathJax 初始化成功，尝试次数: ${attempts}`)
                loadSuccess = true
                katexLoaded.value = true
                resolve()
              } else if (attempts < maxAttempts) {
                setTimeout(checkReady, 100)
              } else {
                reject(new Error('MathJax 初始化超时'))
              }
            }

            setTimeout(checkReady, 500)
          }

          script.onerror = () => {
            console.error(`CDN ${i + 1} 加载失败`)
            lastError = new Error(`CDN ${i + 1} 加载失败`)
            reject(lastError)
          }

          script.ontimeout = () => {
            console.error(`CDN ${i + 1} 加载超时`)
            lastError = new Error(`CDN ${i + 1} 加载超时`)
            reject(lastError)
          }

          document.head.appendChild(script)
        })

        if (loadSuccess) break
      } catch (error) {
        console.warn(`CDN ${i + 1} 失败，尝试下一个`)
        lastError = error
        continue
      }
    }

    if (!loadSuccess) {
      throw new Error(`所有 CDN 都加载失败: ${lastError?.message}`)
    }

    console.log('MathJax 加载并配置成功')

  } catch (error) {
    console.error('MathJax 加载失败:', error)
    throw error
  }
}

// 动态加载 KaTeX 库
const loadKaTeX = async () => {
  // 检查 KaTeX 是否已加载
  if (window.katex) {
    katexLoaded.value = true
    return
  }

  try {
    // CDN 源配置，优先使用国内 CDN
    const cdnOptions = [
      // 七牛云 CDN
      {
        css: 'https://cdn.staticfile.org/KaTeX/0.16.9/katex.min.css',
        js: 'https://cdn.staticfile.org/KaTeX/0.16.9/katex.min.js',
        autoRender: 'https://cdn.staticfile.org/KaTeX/0.16.9/contrib/auto-render.min.js'
      },
      // BootCDN
      {
        css: 'https://cdn.bootcdn.net/ajax/libs/KaTeX/0.16.9/katex.min.css',
        js: 'https://cdn.bootcdn.net/ajax/libs/KaTeX/0.16.9/katex.min.js',
        autoRender: 'https://cdn.bootcdn.net/ajax/libs/KaTeX/0.16.9/contrib/auto-render.min.js'
      },
      // jsdelivr CDN
      {
        css: 'https://cdn.jsdelivr.net/npm/katex@0.16.9/dist/katex.min.css',
        js: 'https://cdn.jsdelivr.net/npm/katex@0.16.9/dist/katex.min.js',
        autoRender: 'https://cdn.jsdelivr.net/npm/katex@0.16.9/dist/contrib/auto-render.min.js'
      }
    ]

    let loadSuccess = false
    let lastError = null

    // 尝试从各个 CDN 加载
    for (let i = 0; i < cdnOptions.length; i++) {
      const cdn = cdnOptions[i]
      try {
        console.log(`尝试从 CDN ${i + 1}/${cdnOptions.length} 加载 KaTeX`)

        // 加载 CSS 样式
        const cssLink = document.createElement('link')
        cssLink.rel = 'stylesheet'
        cssLink.href = cdn.css
        document.head.appendChild(cssLink)

        // 加载主要 KaTeX 库
        await new Promise((resolve, reject) => {
          const script = document.createElement('script')
          script.src = cdn.js
          script.async = true
          script.timeout = 10000
          script.onload = resolve
          script.onerror = reject
          script.ontimeout = reject
          document.head.appendChild(script)
        })

        // 加载自动渲染扩展
        await new Promise((resolve, reject) => {
          const script = document.createElement('script')
          script.src = cdn.autoRender
          script.async = true
          script.timeout = 10000
          script.onload = resolve
          script.onerror = reject
          script.ontimeout = reject
          document.head.appendChild(script)
        })

        console.log(`CDN ${i + 1} 加载成功`)
        loadSuccess = true
        break
      } catch (err) {
        console.warn(`CDN ${i + 1} 加载失败:`, err)
        lastError = err
        // 清理已加载的资源
        const links = document.querySelectorAll('link[href*="katex"]')
        const scripts = document.querySelectorAll('script[src*="katex"]')
        links.forEach(link => link.remove())
        scripts.forEach(script => script.remove())
        continue
      }
    }

    if (!loadSuccess) {
      throw new Error(`所有 CDN 都加载失败: ${lastError?.message || '未知错误'}`)
    }

    katexLoaded.value = true
    console.log('KaTeX 加载成功')
  } catch (err) {
    console.error('KaTeX 加载失败:', err)
    throw err
  }
}

// 清理和标准化公式格式
const cleanFormula = (formula) => {
  if (!formula) return ''

  let cleaned = formula

  // 双反斜杠转单反斜杠
  cleaned = cleaned.replace(/\\\\([a-zA-Z]+)/g, '\\$1')

  // 处理表格环境
  if (cleaned.includes('\\begin{array}') || cleaned.includes('\\begin{tabular}')) {
    if (cleaned.startsWith('\\[') && cleaned.endsWith('\\]')) {
      cleaned = cleaned.slice(2, -2)
    }
    return cleaned.trim()
  }

  // 移除数学模式分隔符
  if (cleaned.startsWith('$') && cleaned.endsWith('$')) {
    cleaned = cleaned.slice(1, -1)
  }

  return cleaned.trim()
}

// 使用 KaTeX 渲染公式和表格
const renderKaTeX = () => {
  console.log('开始 KaTeX 渲染，容器:', mathContainer.value)

  // 检查 KaTeX 库
  if (!katexLoaded.value || !window.katex) {
    throw new Error('KaTeX 未加载')
  }

  // 检查容器
  if (!mathContainer.value) {
    console.error('数学容器不可用:', {
      loading: loading.value,
      error: error.value,
      formula: props.formula
    })
    throw new Error('数学容器不可用')
  }

  // 清理公式
  const cleanedFormula = cleanFormula(props.formula)
  if (!cleanedFormula) {
    throw new Error('公式清理后为空')
  }

  // 显示模式
  const displayMode = props.displayMode || !props.inline

  try {
    // 检查是否为表格（KaTeX 不支持 tabular，且样式不美观）
    if (cleanedFormula.includes('\\begin{array}') || cleanedFormula.includes('\\begin{tabular}')) {
      console.log('渲染为 HTML 表格:', cleanedFormula)

      let tableFormula = cleanedFormula
      if (tableFormula.startsWith('\\[') && tableFormula.endsWith('\\]')) {
        tableFormula = tableFormula.slice(2, -2)
      }

      mathContainer.value.innerHTML = ''

      // 检查 表格内部是否需要 KaTeX 渲染
      // 如果包含集合符号，优先使用HTML渲染以正确处理花括号
      const needsKaTeX = !/\\\{[^}]*f_[^}]*\\\}/.test(tableFormula) &&
        (/\\(fbox|textrm|quad|qquad|mathbf|infty|times|checkmark)/.test(tableFormula) || /\$[^$]+\$/.test(tableFormula))

      let table
      if (needsKaTeX) {
        console.log('使用 KaTeX 表格内部渲染')
        table = createKaTeXTable(tableFormula)
      } else {
        console.log('使用标准 HTML 表格渲染')
        table = createBeautifulHTMLTable(tableFormula)
      }

      if (table) {
        mathContainer.value.appendChild(table)
        console.log('表格创建成功')
      } else {
        console.log('表格渲染失败')
      }
    } else {
      // 常规公式使用标准 KaTeX 渲染
      window.katex.render(cleanedFormula, mathContainer.value, {
        displayMode: displayMode,
        throwOnError: false,
        trust: true,
        strict: false,
        output: 'html',
        macros: {
          "\\mathbf": "\\textbf"
        }
      })
      console.log('KaTeX 公式渲染成功:', cleanedFormula)
    }
  } catch (err) {
    console.warn('渲染失败:', err)
    throw err
  }
}

// 创建带 KaTeX 渲染的 HTML 表格
const createKaTeXTable = (latexTable) => {
  try {

    // 解析 LaTeX 表格结构
    let arrayMatch = latexTable.match(/\\begin\{array\}\{([^}]*)\}([\s\S]*?)\\end\{array\}/)
    if (!arrayMatch) {
      arrayMatch = latexTable.match(/\\begin\{tabular\}\{([^}]*)\}([\s\S]*?)\\end\{tabular\}/)
    }

    if (!arrayMatch) return null

    const content = arrayMatch[2]

    // 解析表格行
    const rows = content.split('\\\\').map(row => row.trim()).filter(row => row.length > 0)

    // 创建表格元素
    const table = document.createElement('table')
    table.className = 'truth-table-html'
    table.style.cssText = `
      border-collapse: collapse;
      margin: 0 auto;
      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
      font-size: 14px;
      border: 2px solid #4a5568;
      border-radius: 8px;
      overflow: hidden;
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
      background: white;
    `

    // 处理表格行
    let actualRowIndex = 0
    rows.forEach((rowContent) => {
      // 跳过只包含\hline的行
      if (rowContent.trim() === '\\hline') return

      const row = document.createElement('tr')
      row.style.cssText = actualRowIndex === 0 ?
        `background: linear-gradient(135deg, #2d3748 0%, #4a5568 100%); color: white;` :
        actualRowIndex % 2 === 0 ?
        `background-color: #edf2f7;` :
        `background-color: #f8fafc;`

      // 分割单元格内容
      const cells = rowContent.split('&').map(cell => cell.trim())

      // 处理每个单元格
      cells.forEach((cellContent, cellIndex) => {
        let cleanCellContent = cellContent.replace(/\\hline/g, '').trim()
        if (cleanCellContent === '') return

        const cell = actualRowIndex === 0 ? document.createElement('th') : document.createElement('td')

        // 使用KaTeX处理单元格内容
        const processedCell = processCellContentWithKaTeX(cleanCellContent, actualRowIndex === 0)

        cell.appendChild(processedCell)

        // 应用样式
        cell.style.cssText = actualRowIndex === 0 ?
          `border: 1px solid #4a5568;
           padding: 12px 16px;
           text-align: center;
           font-weight: 600;
           font-size: 13px;
           letter-spacing: 0.5px;` :
          `border: 1px solid #e2e8f0;
           padding: 10px 14px;
           text-align: center;
           color: #2d3748;
           font-weight: 500;
           transition: all 0.2s ease;`

        // 添加悬停效果
        if (actualRowIndex > 0) {
          cell.addEventListener('mouseenter', () => {
            cell.style.backgroundColor = '#e2e8f0'
            cell.style.transform = 'scale(1.02)'
          })
          cell.addEventListener('mouseleave', () => {
            cell.style.backgroundColor = actualRowIndex % 2 === 0 ? '#edf2f7' : '#f8fafc'
            cell.style.transform = 'scale(1)'
          })
        }

        row.appendChild(cell)
      })

      table.appendChild(row)
      actualRowIndex++
    })

    return table
  } catch (err) {
    console.error('创建KaTeX表格失败:', err)
    return null
  }
}

// 使用KaTeX处理单元格内容
const processCellContentWithKaTeX = (content, isHeader) => {
  const container = document.createElement('div')
  container.style.whiteSpace = 'nowrap'

  // 处理LaTeX命令
  let processedContent = content

  // 替换文本样式命令
  processedContent = processedContent.replace(/\\textrm\{([^}]+)\}/g, '$1')

  // 替换间距命令
  processedContent = processedContent.replace(/\\qquad/g, '  ')
  processedContent = processedContent.replace(/\\quad/g, ' ')

  // 替换符号命令
  processedContent = processedContent.replace(/\\infty/g, '∞')
  processedContent = processedContent.replace(/\\times/g, '×')
  processedContent = processedContent.replace(/\\checkmark/g, '✓')

  // 处理f_1、f_2等形式的下标 - 转换为带下标的HTML，避免KaTeX解析问题
  processedContent = processedContent.replace(/([a-zA-Z]+)_(\{[^}]+\}|\w+|\d+)/g, (match, letter, subscript) => {
    // 移除可能存在的大括号
    const cleanSubscript = subscript.replace(/[{}]/g, '')
    return `${letter}<sub>${cleanSubscript}</sub>`
  })

  // 处理带空格的函数名，如"cos a"、"sin b"等
  processedContent = processedContent.replace(/(\w+)\s+(\d+)/g, '$1<sub>$2</sub>')

  // 处理\otimes_7或\otimes_{7}形式 - 转换为带下标的HTML，避免KaTeX解析问题
  processedContent = processedContent.replace(/\\otimes_\{?(\d+)\}?/g, '⊗<sub>$1</sub>')
  processedContent = processedContent.replace(/\\otimes([^_])/g, '⊗$1')
  processedContent = processedContent.replace(/\\otimes$/, '⊗')

  // 处理空单元格指示符
  processedContent = processedContent.replace(/\\quad/g, ' ')

  // 查找$...$公式
  const formulaMatches = processedContent.match(/\$([^\$]+)\$/g)
  if (formulaMatches) {
    let lastIndex = 0
    formulaMatches.forEach(formula => {
      const formulaText = formula.slice(1, -1) // 移除$符号

      // 添加公式前的文本
      const beforeText = processedContent.substring(lastIndex, processedContent.indexOf(formula))
      if (beforeText) {
        const textNode = document.createTextNode(beforeText)
        container.appendChild(textNode)
      }

      // 渲染公式
      try {
        const katexSpan = document.createElement('span')
        katex.render(formulaText, katexSpan, {
          displayMode: false,
          throwOnError: false
        })

        // 标题样式
        if (isHeader) {
          katexSpan.style.fontStyle = 'italic'
          katexSpan.style.fontFamily = "'Times New Roman', serif"
        }

        container.appendChild(katexSpan)
      } catch (err) {
        console.warn('KaTeX渲染失败:', formulaText, err)
        const fallbackText = document.createTextNode(formulaText)
        container.appendChild(fallbackText)
      }

      lastIndex = processedContent.indexOf(formula) + formula.length
    })

    // 添加剩余文本
    const remainingText = processedContent.substring(lastIndex)
    if (remainingText) {
      container.appendChild(document.createTextNode(remainingText))
    }
  } else {
    // 检查\fbox命令
    const fboxMatches = processedContent.match(/\\fbox\{([^}]+)\}/g)
    if (fboxMatches) {
      let lastIndex = 0
      fboxMatches.forEach(fbox => {
        const fboxContent = fbox.match(/\\fbox\{([^}]+)\}/)[1]

        // 添加fbox前的文本
        const beforeText = processedContent.substring(lastIndex, processedContent.indexOf(fbox))
        if (beforeText) {
          const textNode = document.createTextNode(beforeText)
          container.appendChild(textNode)
        }

        // 创建fbox样式
        const fboxSpan = document.createElement('span')
        fboxSpan.style.cssText = `
          border: 1px solid #374151;
          padding: 2px 4px;
          border-radius: 2px;
          background-color: #f3f4f6;
          font-weight: 500;
          ${isHeader ? 'color: white; border-color: white; background-color: rgba(255,255,255,0.2);' : 'color: #374151;'}
        `

        // 检查是否包含数学内容
        if (fboxContent.includes('/') && fboxContent.match(/^[0-9.]+\//)) {
          // 分数渲染
          try {
            katex.render(fboxContent, fboxSpan, {
              displayMode: false,
              throwOnError: false
            })
          } catch (err) {
            fboxSpan.textContent = fboxContent
          }
        } else {
          fboxSpan.textContent = fboxContent
        }

        container.appendChild(fboxSpan)
        lastIndex = processedContent.indexOf(fbox) + fbox.length
      })

      // 添加剩余文本
      const remainingText = processedContent.substring(lastIndex)
      if (remainingText) {
        container.appendChild(document.createTextNode(remainingText))
      }
    } else {
      // 检查特殊符号
      const checkmarkMatch = processedContent.match(/✓/)
      const timesMatch = processedContent.match(/×/)

      if (checkmarkMatch || timesMatch) {
        // 特殊符号样式
        const textNode = document.createTextNode(processedContent)
        container.appendChild(textNode)

        // 应用符号样式
        if (checkmarkMatch) {
          container.style.color = '#22c55e'
          container.style.fontWeight = 'bold'
          container.style.fontSize = '16px'
        }
        if (timesMatch) {
          container.style.color = '#ef4444'
          container.style.fontWeight = 'bold'
          container.style.fontSize = '16px'
        }
      } else {
        // 检查是否包含HTML标签（特别是<sub>标签）
        if (processedContent.includes('<sub>')) {
          // 如果包含HTML标签，使用innerHTML
          container.innerHTML = processedContent
        } else {
          // 普通文本
          container.appendChild(document.createTextNode(processedContent))
        }
      }
    }
  }

  return container
}

const createBeautifulHTMLTable = (latexTable) => {
  try {
    // 调试信息
    console.log('创建HTML表格输入:', JSON.stringify(latexTable))
    console.log('表格原始内容:', latexTable)

    // 解析LaTeX表格结构
    let arrayMatch = latexTable.match(/\\begin\{array\}\{([^}]*)\}([\s\S]*?)\\end\{array\}/)
    console.log('array匹配结果:', arrayMatch)

    // 尝试tabular格式
    if (!arrayMatch) {
      arrayMatch = latexTable.match(/\\begin\{tabular\}\{([^}]*)\}([\s\S]*?)\\end\{tabular\}/)
      console.log('tabular匹配结果:', arrayMatch)
    }

    // 备用匹配模式
    if (!arrayMatch) {
      console.log('尝试备用正则模式...')
      const pattern1 = latexTable.match(/\\begin\{(?:array|tabular)\}\{([^}]*)\}([\\s\\S]*?)\\end\{(?:array|tabular)\}/)
      console.log('备用模式结果:', pattern1)
      if (!pattern1) {
        console.log('所有匹配都失败，表格内容为:', latexTable)
        return null
      }
      arrayMatch = pattern1
    }
    if (!arrayMatch) return null

    const columnSpec = arrayMatch[1] // 列规格
    const content = arrayMatch[2]

    // 解析表格结构：按行分割，再按单元格分割
    const rows = content.split('\\\\').map(row => row.trim()).filter(row => row.length > 0)

    // 创建表格元素
    const table = document.createElement('table')
    table.className = 'truth-table-html'
    table.style.cssText = `
      border-collapse: collapse;
      margin: 0 auto;
      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
      font-size: 14px;
      border: 2px solid #4a5568;
      border-radius: 8px;
      overflow: hidden;
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
      background: white;
    `

    // 处理每一行
    let actualRowIndex = 0
    rows.forEach((rowContent) => {
      // 跳过只包含\hline的行
      if (rowContent.trim() === '\\hline') return

      const row = document.createElement('tr')
      // 应用不同的行样式
      row.style.cssText = actualRowIndex === 0 ?
        `background: linear-gradient(135deg, #2d3748 0%, #4a5568 100%); color: white;` :
        actualRowIndex % 2 === 0 ?
        `background-color: #edf2f7;` :
        `background-color: #f8fafc;`

      // 分割单元格
      const cells = rowContent.split('&').map(cell => cell.trim())

      // 处理每个单元格
      cells.forEach((cellContent, cellIndex) => {
        // 清理单元格内容
        let cleanCellContent = cellContent.replace(/\\hline/g, '').trim()

        // 跳过空单元格
        if (cleanCellContent === '') return

        // 创建单元格
        const cell = actualRowIndex === 0 ? document.createElement('th') : document.createElement('td')

        // 处理单元格内容：转换LaTeX命令到HTML和逻辑符号
        let processedContent = cleanCellContent
          .replace(/\$([^\$]+)\$/g, '$1') // 移除$分隔符
          .replace(/\\mathbf\{([^}]+)\}/g, '<strong style="color: #2d3748;">$1</strong>')
          .replace(/\\leftrightarrow/g, '↔')
          .replace(/\\rightarrow/g, '→')
          .replace(/\\wedge/g, '∧')
          .replace(/\\vee/g, '∨')
          .replace(/\\neg/g, '¬')
          .replace(/\\circ/g, '∘')

          // 特别处理商群集合符号 \{f_1, f_5, f_6\} - 必须在下标处理之前
          .replace(/\\\{([^}]+)\\\}/g, (match, content) => {
            // 先处理集合内的下标
            let processedInner = content
              .replace(/([a-zA-Z]+)_(\{[^}]+\}|\w+|\d+)/g, (match, letter, subscript) => {
                const cleanSubscript = subscript.replace(/[{}]/g, '')
                return `${letter}<sub>${cleanSubscript}</sub>`
              })
            return `{${processedInner}}`
          })

          // 处理f_1、f_2等形式的下标 - 转换为带下标的HTML
          .replace(/([a-zA-Z]+)_(\{[^}]+\}|\w+|\d+)/g, (match, letter, subscript) => {
            // 移除可能存在的大括号
            const cleanSubscript = subscript.replace(/[{}]/g, '')
            return `${letter}<sub>${cleanSubscript}</sub>`
          })

          // 处理带空格的函数名，如"cos a"、"sin b"等
          .replace(/(\w+)\s+(\d+)/g, '$1<sub>$2</sub>')
          // 处理\otimes_7或\otimes_{7}形式 - 转换为带下标的HTML
          .replace(/\\otimes_\{?(\d+)\}?/g, '⊗<sub>$1</sub>')
          .replace(/\\otimes([^_])/g, '⊗$1')
          .replace(/\\otimes$/, '⊗')

          // 处理其他花括号（不是集合符号的）
          .replace(/\{([^{}\\]+)\}/g, (match, content) => {
            // 只有内容不包含LaTeX命令时才移除花括号
            return content.includes('\\') ? match : content;
          })

        // 调试输出处理前后的内容（只对包含f_的单元格，减少日志）
        if (actualRowIndex > 0 && cleanCellContent.includes('f_')) {
          console.log(`处理单元格内容: "${cleanCellContent}" -> "${processedContent}"`)
        }

        // 标题行使用斜体
        if (actualRowIndex === 0) {
          processedContent = `<em style="font-style: italic; font-family: 'Times New Roman', serif;">${processedContent}</em>`
        }

        // 应用单元格样式
        cell.style.cssText = actualRowIndex === 0 ?
          `border: 1px solid #4a5568;
           padding: 12px 16px;
           text-align: center;
           font-weight: 600;
           font-size: 13px;
           letter-spacing: 0.5px;` :
          `border: 1px solid #e2e8f0;
           padding: 10px 14px;
           text-align: center;
           color: #2d3748;
           font-weight: 500;
           transition: all 0.2s ease;`

        // 添加悬停效果
        if (actualRowIndex > 0) {
          cell.addEventListener('mouseenter', () => {
            cell.style.backgroundColor = '#e2e8f0'
            cell.style.transform = 'scale(1.02)'
          })
          cell.addEventListener('mouseleave', () => {
            cell.style.backgroundColor = actualRowIndex % 2 === 0 ? '#edf2f7' : '#f8fafc'
            cell.style.transform = 'scale(1)'
          })
        }

        // 设置内容并添加到行
        cell.innerHTML = processedContent
        row.appendChild(cell)
      })

      // 添加行到表格
      table.appendChild(row)
      actualRowIndex++
    })

    return table
  } catch (err) {
    console.error('创建HTML表格失败:', err)
    return null
  }
}

// 使用MathJax渲染公式
const renderMathJax = async () => {
  console.log('调用MathJax渲染，容器:', mathContainer.value)

  // 验证MathJax库
  if (!katexLoaded.value || !window.MathJax || !window.MathJax.typesetPromise) {
    throw new Error('MathJax未加载或未就绪')
  }

  // 验证DOM容器
  if (!mathContainer.value) {
    throw new Error('数学容器不可用')
  }

  // 清理和验证公式
  const cleanedFormula = cleanFormula(props.formula)
  if (!cleanedFormula) {
    throw new Error('MathJax渲染：公式清理后为空')
  }

  try {
    // 清除之前的内容
    mathContainer.value.innerHTML = ''

    // 确定显示模式并准备公式
    const displayMode = props.displayMode || !props.inline
    let mathJaxFormula = cleanedFormula

    // 设置数学模式分隔符
    if (displayMode) {
      mathJaxFormula = `\\[${mathJaxFormula}\\]`
    } else {
      mathJaxFormula = `\\(${mathJaxFormula}\\)`
    }

    // 创建临时容器
    const tempContainer = document.createElement('div')
    tempContainer.innerHTML = mathJaxFormula
    tempContainer.style.display = displayMode ? 'block' : 'inline'
    mathContainer.value.appendChild(tempContainer)

    // 等待MathJax排版完成
    await window.MathJax.typesetPromise([tempContainer])
    console.log('MathJax渲染成功:', cleanedFormula)

  } catch (err) {
    console.warn('MathJax渲染失败:', err)
    throw err
  }
}

// 主渲染函数
const renderFormula = async () => {
  console.log('渲染公式:', props.formula)
  if (!props.formula) return

  // 设置加载状态
  loading.value = true
  error.value = null

  try {
    // 等待Vue更新DOM
    await nextTick()
    console.log('DOM更新后，容器:', mathContainer.value)

    // 根据配置选择渲染方法
    if (props.type === 'katex') {
      try {
        // 加载KaTeX
        if (!katexLoaded.value) {
          console.log('加载KaTeX...')
          await loadKaTeX()
        }
        await nextTick()
        console.log('KaTeX渲染前，容器:', mathContainer.value)
        renderKaTeX()
      } catch (katexError) {
        // KaTeX失败时的回退
        console.warn('KaTeX失败，使用回退方案:', katexError)
      }
    } else if (props.type === 'mathjax') {
      try {
        // 加载MathJax
        if (!katexLoaded.value) {
          console.log('加载MathJax...')
          await loadMathJax()
        } else {
          // 确认MathJax完全就绪
          if (!window.MathJax || !window.MathJax.typesetPromise) {
            console.log('MathJax标记已加载但未就绪，重新加载...')
            katexLoaded.value = false
            await loadMathJax()
          }
        }

        await nextTick()
        console.log('MathJax渲染前，容器:', mathContainer.value)

        // 确保MathJax完全就绪后渲染
        let retries = 0
        const maxRetries = 10
        while (retries < maxRetries) {
          try {
            await renderMathJax()
            break // 成功渲染，退出重试
          } catch (renderError) {
            retries++
            console.warn(`MathJax渲染尝试 ${retries} 失败:`, renderError)
            if (retries >= maxRetries) {
              throw renderError
            }
            // 等待后重试
            await new Promise(resolve => setTimeout(resolve, 200))
          }
        }

        console.log('MathJax渲染完成')
      } catch (mathjaxError) {
        console.warn('MathJax多次尝试后失败:', mathjaxError)
        error.value = `MathJax渲染失败: ${mathjaxError.message}`

        // MathJax失败时尝试KaTeX作为后备
        try {
          console.log('尝试KaTeX后备方案...')
          await loadKaTeX()
          renderKaTeX()
        } catch (fallbackError) {
          console.error('MathJax和KaTeX都失败:', fallbackError)
        }
      }
    } else {
      // 其他类型使用简单LaTeX渲染
    }

    // 发送成功事件
    emit('rendered')
  } catch (err) {
    // 处理渲染错误
    console.error('公式渲染失败:', err)
    error.value = `公式渲染失败: ${err.message}`
    emit('error', err)
  } finally {
    // 重置加载状态
    loading.value = false
  }
}

// 监听公式变化
watch(() => props.formula, (newFormula) => {
  if (newFormula) {
    renderFormula()
  }
})

// 组件挂载时渲染
onMounted(() => {
  if (props.formula) {
    renderFormula()
  }
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

.math-content :deep(.katex) {
  font-size: 1em;
}

.math-content :deep(.katex-display) {
  margin: 0.5em 0;
}

.math-content :deep(.katex-error) {
  color: #f56c6c;
}

.math-content :deep(.simple-latex) {
  font-family: 'Times New Roman', serif;
  font-style: italic;
}

/* KaTeX display styling for non-table content */
.math-content :deep(.katex-display > .katex) {
  display: inline-block;
  text-align: initial;
  max-width: 100%;
}

/* Beautiful HTML table styling */
.math-content .truth-table-html {
  border-collapse: collapse;
  margin: 0 auto;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  font-size: 14px;
  border: 2px solid #4a5568;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  background: white;
  max-width: 100%;
}

.math-content .truth-table-html th {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: 1px solid #4a5568;
  padding: 12px 16px;
  text-align: center;
  font-weight: 600;
  font-size: 13px;
  letter-spacing: 0.5px;
}

.math-content .truth-table-html td {
  border: 1px solid #e2e8f0;
  padding: 10px 14px;
  text-align: center;
  color: #2d3748;
  font-weight: 500;
  transition: all 0.2s ease;
}

.math-content .truth-table-html tbody tr:nth-child(even) {
  background-color: #edf2f7;
}

.math-content .truth-table-html tbody tr:nth-child(odd) {
  background-color: #f8fafc;
}

.math-content .truth-table-html tr:hover td {
  background-color: #e2e8f0 !important;
  transform: scale(1.02);
}

/* Ensure mathbf is properly styled */
.math-content :deep(.katex-mathbf) {
  font-weight: bold;
  font-family: KaTeX_Main, Times New Roman, serif;
}

/* Fallback: Try to target any table-like structure */
.math-content :deep(table) {
  border-collapse: collapse !important;
  border-spacing: 0 !important;
  border: 2px solid #666 !important;
  margin: 0 auto !important;
}

.math-content :deep(table td) {
  border: 1px solid #999 !important;
  padding: 10px 15px !important;
  text-align: center !important;
  min-width: 45px;
  box-shadow: inset 0 0 0 1px #999 !important;
}

.math-content :deep(table tr:first-child td) {
  font-weight: bold !important;
  background-color: #f8f9fa !important;
  border-top: 2px solid #666 !important;
  border-bottom: 2px solid #666 !important;
}

.math-content :deep(table td:not(:last-child)) {
  border-right: 2px solid #666 !important;
}

/* Math content container - no extra borders, let the parent wrapper handle styling */
.math-content {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  padding: 0.5rem;
  box-sizing: border-box;
}

/* Try to target array columns with CSS selectors for vertical lines */
.math-content :deep(.arraycolsep) {
  border-right: 2px solid #666 !important;
}

.math-content :deep(.vlist) {
  border-right: 1px solid #999 !important;
}
</style>