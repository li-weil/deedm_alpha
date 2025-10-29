<template>
  <!-- Main container with conditional inline styling -->
  <div class="math-renderer" :class="{ 'inline': inline }">
    <!-- Loading state: Shows spinner and loading message -->
    <div v-if="loading" class="loading">
      <el-icon class="loading-icon"><Loading /></el-icon>
      <span>公式加载中...</span>
    </div>
    <!-- Error state: Shows warning icon and error message -->
    <div v-else-if="error" class="error">
      <el-icon><WarningFilled /></el-icon>
      <span>公式渲染失败: {{ error }}</span>
    </div>
    <!-- Main content container: Always exists for rendering math content -->
    <!-- ref="mathContainer" -Vue的ref属性，用于在JavaScript中直接引用这个DOM元素 -->
    <!-- :data-formula="formula" - Vue的动态属性绑定：- : 是 v-bind: 的简写 -->
    <!-- - 将JavaScript变量 formula 的值绑定到HTML5 data属性data-formula 上 -->
    <!-- :style="{ display: loading || error ? 'none' : 'block' }"- 动态样式绑定： -->
    <!-- - 使用JavaScript表达式决定CSS display 属性 -->
    <!-- - 如果 loading 或 error 为true，显示 none（隐藏元素） -->
    <!-- - 否则显示 block（正常显示） -->
    <div class="math-content" ref="mathContainer" :data-formula="formula" :style="{ display: loading || error ? 'none' : 'block' }">
      <!-- Math content will be rendered here by JavaScript -->
    </div>
  </div>
</template>

<script setup>
// Vue 3 Composition API imports for reactivity and lifecycle management
import { ref, onMounted, watch, nextTick } from 'vue'
// Element Plus icon imports for loading and error states
import { Loading, WarningFilled } from '@element-plus/icons-vue'

// Component props(property) configuration
const props = defineProps({
  formula: {
    type: String,
    required: true
  },
  type: {
    type: String,
    default: 'katex',
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

// Event emissions for parent component communication
const emit = defineEmits(['rendered', 'error'])

// Reactive state references
const mathContainer = ref(null)      // DOM container reference for math rendering
const loading = ref(false)            // Loading state flag
const error = ref(null)              // Error state storage
const katexLoaded = ref(false)        // KaTeX library loaded status

// Load MathJax library dynamically as alternative to KaTeX
const loadMathJax = async () => {
  // Check if MathJax is already loaded globally and ready
  if (window.MathJax && window.MathJax.typesetPromise) {
    katexLoaded.value = true
    console.log('MathJax already loaded and ready')
    return
  }

  try {
    console.log('Loading MathJax...')

    // Load MathJax configuration - 使用更简单的配置
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

    // Load MathJax script with better timing control - 使用多个CDN源和版本
    const mathjaxSources = [
      'https://cdn.jsdelivr.net/npm/mathjax@3.2.2/es5/tex-mml-chtml.js',  // 指定版本
      'https://unpkg.com/mathjax@3.2.2/es5/tex-mml-chtml.js',              // 指定版本
      'https://cdnjs.cloudflare.com/ajax/libs/mathjax/3.2.2/es5/tex-mml-chtml.js',
      'https://cdn.jsdelivr.net/npm/mathjax@3/es5/tex-mml-chtml.js',  // 回退到最新版
      'https://unpkg.com/mathjax@3/es5/tex-mml-chtml.js',              // 回退到最新版
    ]

    let loadSuccess = false
    let lastError = null

    for (let i = 0; i < mathjaxSources.length; i++) {
      try {
        console.log(`尝试从源 ${i + 1}/${mathjaxSources.length} 加载MathJax: ${mathjaxSources[i]}`)

        await new Promise((resolve, reject) => {
          const script = document.createElement('script')
          script.src = mathjaxSources[i]
          script.async = false // 改为同步加载以确保顺序
          script.timeout = 10000 // 10秒超时

          script.onload = () => {
            console.log(`MathJax script loaded from source ${i + 1}, checking readiness...`)

            // 等待MathJax完全初始化
            let attempts = 0
            const maxAttempts = 100 // 减少尝试次数，避免等待太长

            const checkReady = () => {
              attempts++
              if (window.MathJax && window.MathJax.typesetPromise) {
                console.log(`MathJax ready after ${attempts} attempts`)
                if (window.MathJax.version) {
                  console.log('MathJax version:', window.MathJax.version)
                }
                loadSuccess = true
                katexLoaded.value = true
                resolve()
              } else if (attempts < maxAttempts) {
                setTimeout(checkReady, 100) // 增加间隔时间
              } else {
                console.error('MathJax failed to initialize after maximum attempts')
                reject(new Error('MathJax initialization timeout'))
              }
            }

            // 开始检查MathJax是否就绪
            setTimeout(checkReady, 500) // 给MathJax更多初始化时间
          }

          script.onerror = () => {
            console.error(`Failed to load MathJax from source ${i + 1}`)
            lastError = new Error(`Failed to load MathJax from source ${i + 1}`)
            reject(lastError)
          }

          script.ontimeout = () => {
            console.error(`MathJax loading timeout from source ${i + 1}`)
            lastError = new Error(`MathJax loading timeout from source ${i + 1}`)
            reject(lastError)
          }

          document.head.appendChild(script)
        })

        if (loadSuccess) {
          break // 成功加载，退出循环
        }
      } catch (error) {
        console.warn(`Source ${i + 1} failed, trying next...`)
        lastError = error
        continue
      }
    }

    if (!loadSuccess) {
      throw new Error(`All MathJax sources failed. Last error: ${lastError?.message}`)
    }

    console.log('MathJax library loaded and configured successfully')

  } catch (error) {
    console.error('Failed to load MathJax:', error)
    throw error
  }
}

// Load KaTeX library dynamically from CDN
const loadKaTeX = async () => {
  // Check if KaTeX is already loaded globally
  if (window.katex) {
    katexLoaded.value = true
    return
  }

  try {
    // 中国境内CDN镜像配置 - 优先使用国内CDN提高加载速度
    const cdnOptions = [
      // 首选：七牛云CDN（国内）
      {
        css: 'https://cdn.staticfile.org/KaTeX/0.16.9/katex.min.css',
        js: 'https://cdn.staticfile.org/KaTeX/0.16.9/katex.min.js',
        autoRender: 'https://cdn.staticfile.org/KaTeX/0.16.9/contrib/auto-render.min.js'
      },
      // 备选1：BootCDN（国内）
      {
        css: 'https://cdn.bootcdn.net/ajax/libs/KaTeX/0.16.9/katex.min.css',
        js: 'https://cdn.bootcdn.net/ajax/libs/KaTeX/0.16.9/katex.min.js',
        autoRender: 'https://cdn.bootcdn.net/ajax/libs/KaTeX/0.16.9/contrib/auto-render.min.js'
      },
      // 备选2：jsdelivr（国外，作为最后备用）
      {
        css: 'https://cdn.jsdelivr.net/npm/katex@0.16.9/dist/katex.min.css',
        js: 'https://cdn.jsdelivr.net/npm/katex@0.16.9/dist/katex.min.js',
        autoRender: 'https://cdn.jsdelivr.net/npm/katex@0.16.9/dist/contrib/auto-render.min.js'
      }
    ]

    let loadSuccess = false
    let lastError = null

    // 尝试从不同的CDN加载KaTeX资源
    for (let i = 0; i < cdnOptions.length; i++) {
      const cdn = cdnOptions[i]
      try {
        console.log(`尝试从CDN ${i + 1}/${cdnOptions.length} 加载KaTeX...`)

        // Load KaTeX CSS styles for mathematical formatting
        const cssLink = document.createElement('link')
        cssLink.rel = 'stylesheet'
        cssLink.href = cdn.css
        document.head.appendChild(cssLink)

        // Load main KaTeX JavaScript library
        await new Promise((resolve, reject) => {
          const script = document.createElement('script')
          script.src = cdn.js
          script.async = true
          script.timeout = 10000 // 10秒超时
          script.onload = resolve
          script.onerror = reject
          script.ontimeout = reject
          document.head.appendChild(script)
        })

        // Load KaTeX auto-render extension for enhanced LaTeX support
        await new Promise((resolve, reject) => {
          const script = document.createElement('script')
          script.src = cdn.autoRender
          script.async = true
          script.timeout = 10000 // 10秒超时
          script.onload = resolve
          script.onerror = reject
          script.ontimeout = reject
          document.head.appendChild(script)
        })

        console.log(`CDN ${i + 1} 加载成功！`)
        loadSuccess = true
        break
      } catch (err) {
        console.warn(`CDN ${i + 1} 加载失败:`, err)
        lastError = err
        // 清理已添加的标签
        const links = document.querySelectorAll('link[href*="katex"]')
        const scripts = document.querySelectorAll('script[src*="katex"]')
        links.forEach(link => link.remove())
        scripts.forEach(script => script.remove())
        continue
      }
    }

    if (!loadSuccess) {
      throw new Error(`所有CDN都无法加载KaTeX。最后错误: ${lastError?.message || '未知错误'}`)
    }

    katexLoaded.value = true
    console.log('KaTeX lib loaded successfully')
  } catch (err) {
    console.error('Failed to load KaTeX lib:', err)
    throw err
  }
}

// Clean and prepare formula for rendering by normalizing LaTeX format
const cleanFormula = (formula) => {
  if (!formula) return ''

  let cleaned = formula

  // Convert all double backslashes to single backslashes for standard LaTeX format
  // This handles backend output that uses double backslashes for LaTeX commands
  cleaned = cleaned.replace(/\\\\([a-zA-Z]+)/g, '\\$1')

  // Special handling for LaTeX array/table environment
  if (cleaned.includes('\\begin{array}') || cleaned.includes('\\begin{tabular}')) {
    // Remove the \[ and \] delimiters if present (display math mode delimiters)
    if (cleaned.startsWith('\\[') && cleaned.endsWith('\\]')) {
      cleaned = cleaned.slice(2, -2)
    }
    return cleaned.trim()
  }

  // Remove LaTeX math mode delimiters for regular formulas ($formula$)
  if (cleaned.startsWith('$') && cleaned.endsWith('$')) {
    cleaned = cleaned.slice(1, -1)
  }

  // Remove extra whitespace for cleaner rendering
  cleaned = cleaned.trim()

  return cleaned
}

// Render formula using KaTeX with special handling for array/table structures
const renderKaTeX = () => {
  console.log('renderKaTeX called, mathContainer:', mathContainer.value)
  console.log('katexLoaded:', katexLoaded.value, 'window.katex:', !!window.katex)

  // Validate KaTeX library is loaded
  if (!katexLoaded.value || !window.katex) {
    throw new Error('KaTeX not loaded')
  }

  // Validate DOM container is available
  if (!mathContainer.value) {
    console.error('Math container not available, current state:', {
      loading: loading.value,
      error: error.value,
      formula: props.formula
    })
    throw new Error('Math container not available')
  }

  // Clean and validate formula input
  const cleanedFormula = cleanFormula(props.formula)
  if (!cleanedFormula) {
    throw new Error('Katex Rendering: Formula is empty after cleaning')
  }

  // Determine display mode based on props
  const displayMode = props.displayMode || !props.inline

  try {
    // Check if formula contains LaTeX array/table environment (both array and tabular)
    //Katex不支持渲染tabular样式的表格，且Katex表格渲染样式不美观
    if (cleanedFormula.includes('\\begin{array}') || cleanedFormula.includes('\\begin{tabular}')) {
      console.log("Rendering as HTML table:", cleanedFormula)

      // Remove display math delimiters if present
      let tableFormula = cleanedFormula
      if (tableFormula.startsWith('\\[') && tableFormula.endsWith('\\]')) {
        tableFormula = tableFormula.slice(2, -2)
      }

      // Clear container and create HTML table
      mathContainer.value.innerHTML = ''

      // Check if table contains LaTeX formulas that need KaTeX rendering
      const needsKaTeX = /\\(fbox|textrm|quad|qquad|mathbf|infty)/.test(tableFormula) || /\$[^$]+\$/.test(tableFormula)

      let table
      if (needsKaTeX) {
        console.log('Table contains LaTeX formulas, using KaTeX table rendering')
        table = createKaTeXTable(tableFormula)
      } else {
        console.log('Table contains simple text, using standard HTML table rendering')
        table = createBeautifulHTMLTable(tableFormula)
      }

      if (table) {
        mathContainer.value.appendChild(table)
        console.log('HTML table created successfully')
      } else {
        // Fallback: try KaTeX render if HTML table creation fails
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
        console.log('KaTeX fallback rendering for table')
      }
    } else {
      // For regular formulas, use standard KaTeX rendering
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
      console.log('KaTeX rendering successful:', cleanedFormula)
    }
  } catch (err) {
    console.warn('Rendering failed:', err)
    throw err
  }
}

// Create beautiful HTML table from LaTeX array format with modern styling
// Create HTML table with embedded KaTeX rendering for formulas
const createKaTeXTable = (latexTable) => {
  try {
    console.log('createKaTeXTable input:', JSON.stringify(latexTable))

    // Parse the LaTeX array/tabular structure
    let arrayMatch = latexTable.match(/\\begin\{array\}\{([^}]*)\}([\s\S]*?)\\end\{array\}/)
    if (!arrayMatch) {
      arrayMatch = latexTable.match(/\\begin\{tabular\}\{([^}]*)\}([\s\S]*?)\\end\{tabular\}/)
    }

    if (!arrayMatch) return null

    const columnSpec = arrayMatch[1]
    const content = arrayMatch[2]

    // Parse table structure
    const rows = content.split('\\\\').map(row => row.trim()).filter(row => row.length > 0)

    // Create main table element with same styling as createBeautifulHTMLTable
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

    // Process each row
    let actualRowIndex = 0
    rows.forEach((rowContent) => {
      // Skip rows that only contain \hline
      if (rowContent.trim() === '\\hline') return

      const row = document.createElement('tr')
      row.style.cssText = actualRowIndex === 0 ?
        `background: linear-gradient(135deg, #2d3748 0%, #4a5568 100%); color: white;` :
        actualRowIndex % 2 === 0 ?
        `background-color: #edf2f7;` :
        `background-color: #f8fafc;`

      // Split row content by & to get individual cells
      const cells = rowContent.split('&').map(cell => cell.trim())

      // Process each cell in the row
      cells.forEach((cellContent, cellIndex) => {
        let cleanCellContent = cellContent.replace(/\\hline/g, '').trim()
        if (cleanCellContent === '') return

        const cell = actualRowIndex === 0 ? document.createElement('th') : document.createElement('td')

        // Process cell content with KaTeX rendering
        const processedCell = processCellContentWithKaTeX(cleanCellContent, actualRowIndex === 0)

        cell.appendChild(processedCell)

        // Apply styling
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

        // Add hover effect for data cells
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
    console.error('Failed to create KaTeX table:', err)
    return null
  }
}

// Process cell content and render LaTeX formulas with KaTeX
const processCellContentWithKaTeX = (content, isHeader) => {
  const container = document.createElement('div')
  container.style.whiteSpace = 'nowrap'

  // Process content by finding and rendering LaTeX formulas
  let processedContent = content

  // Replace \textrm commands
  processedContent = processedContent.replace(/\\textrm\{([^}]+)\}/g, '$1')

  // Replace \quad and \qquad with spaces
  processedContent = processedContent.replace(/\\qquad/g, '  ')
  processedContent = processedContent.replace(/\\quad/g, ' ')

  // Replace \infty with infinity symbol
  processedContent = processedContent.replace(/\\infty/g, '∞')

  // Handle empty cell indicators
  processedContent = processedContent.replace(/\\quad/g, ' ')

  // Find all LaTeX formulas within $...$
  const formulaMatches = processedContent.match(/\$([^\$]+)\$/g)
  if (formulaMatches) {
    let lastIndex = 0
    formulaMatches.forEach(formula => {
      const formulaText = formula.slice(1, -1) // Remove $ symbols

      // Add text before the formula
      const beforeText = processedContent.substring(lastIndex, processedContent.indexOf(formula))
      if (beforeText) {
        const textNode = document.createTextNode(beforeText)
        container.appendChild(textNode)
      }

      // Render the formula with KaTeX
      try {
        const katexSpan = document.createElement('span')
        katex.render(formulaText, katexSpan, {
          displayMode: false,
          throwOnError: false
        })

        // Apply additional styling for headers
        if (isHeader) {
          katexSpan.style.fontStyle = 'italic'
          katexSpan.style.fontFamily = "'Times New Roman', serif"
        }

        container.appendChild(katexSpan)
      } catch (err) {
        console.warn('KaTeX rendering failed for formula:', formulaText, err)
        const fallbackText = document.createTextNode(formulaText)
        container.appendChild(fallbackText)
      }

      lastIndex = processedContent.indexOf(formula) + formula.length
    })

    // Add remaining text
    const remainingText = processedContent.substring(lastIndex)
    if (remainingText) {
      container.appendChild(document.createTextNode(remainingText))
    }
  } else {
    // No $...$ formulas found, check for \fbox commands
    const fboxMatches = processedContent.match(/\\fbox\{([^}]+)\}/g)
    if (fboxMatches) {
      let lastIndex = 0
      fboxMatches.forEach(fbox => {
        const fboxContent = fbox.match(/\\fbox\{([^}]+)\}/)[1]

        // Add text before the fbox
        const beforeText = processedContent.substring(lastIndex, processedContent.indexOf(fbox))
        if (beforeText) {
          const textNode = document.createTextNode(beforeText)
          container.appendChild(textNode)
        }

        // Create fbox styled span
        const fboxSpan = document.createElement('span')
        fboxSpan.style.cssText = `
          border: 1px solid #374151;
          padding: 2px 4px;
          border-radius: 2px;
          background-color: #f3f4f6;
          font-weight: 500;
          ${isHeader ? 'color: white; border-color: white; background-color: rgba(255,255,255,0.2);' : 'color: #374151;'}
        `

        // Check if the content itself contains math
        if (fboxContent.includes('/') && fboxContent.match(/^[0-9.]+\//)) {
          // This looks like a fraction, render with KaTeX
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

      // Add remaining text
      const remainingText = processedContent.substring(lastIndex)
      if (remainingText) {
        container.appendChild(document.createTextNode(remainingText))
      }
    } else {
      // No special formatting, just add the text
      container.appendChild(document.createTextNode(processedContent))
    }
  }

  return container
}

const createBeautifulHTMLTable = (latexTable) => {
  try {
    // Debug logging for troubleshooting LaTeX parsing
    console.log('createBeautifulHTMLTable input:', JSON.stringify(latexTable))
    // console.log('Input length:', latexTable.length)
    // console.log('Contains \\begin{array}:', latexTable.includes('\\begin{array}'))
    // console.log('Contains \\end{array}:', latexTable.includes('\\end{array}'))

    // Parse the LaTeX array/tabular structure using regex to extract column spec and content
    let arrayMatch = latexTable.match(/\\begin\{array\}\{([^}]*)\}([\s\S]*?)\\end\{array\}/)
    // console.log('Array regex match result:', arrayMatch)

    // If not array, try tabular format
    if (!arrayMatch) {
      arrayMatch = latexTable.match(/\\begin\{tabular\}\{([^}]*)\}([\s\S]*?)\\end\{tabular\}/)
      // console.log('Tabular regex match result:', arrayMatch)
    }

    // Fallback pattern matching if primary pattern fails
    if (!arrayMatch) {
      console.log('Trying alternative regex patterns...')
      const pattern1 = latexTable.match(/\\begin\{(?:array|tabular)\}\{([^}]*)\}([\\s\\S]*?)\\end\{(?:array|tabular)\}/)
      console.log('Alternative pattern result:', pattern1)
      return pattern1 || null
    }
    if (!arrayMatch) return null

    const columnSpec = arrayMatch[1] // column specification like {ccccc}
    const content = arrayMatch[2]

    // Parse table structure: split content by \\ (rows) and then by & (cells)
    const rows = content.split('\\\\').map(row => row.trim()).filter(row => row.length > 0)

    // Create main table element with modern styling
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

    // Process each row from LaTeX content
    let actualRowIndex = 0
    rows.forEach((rowContent) => {
      // Skip rows that only contain \hline
      if (rowContent.trim() === '\\hline') return

      const row = document.createElement('tr')
      // Apply different styling for header row vs data rows with enhanced contrast
      row.style.cssText = actualRowIndex === 0 ?
        `background: linear-gradient(135deg, #2d3748 0%, #4a5568 100%); color: white;` :
        actualRowIndex % 2 === 0 ?
        `background-color: #edf2f7;` :
        `background-color: #f8fafc;`

      // Split row content by & to get individual cells
      const cells = rowContent.split('&').map(cell => cell.trim())

      // Process each cell in the row
      cells.forEach((cellContent, cellIndex) => {
        // Remove \hline from cell content if present
        let cleanCellContent = cellContent.replace(/\\hline/g, '').trim()

        // Skip empty cells
        if (cleanCellContent === '') return

        // Create header cell for first row, data cell for others
        const cell = actualRowIndex === 0 ? document.createElement('th') : document.createElement('td')

        // Process cell content: Convert LaTeX commands to HTML and logic symbols
        let processedContent = cleanCellContent
          .replace(/\$([^\$]+)\$/g, '$1') // Remove $ delimiters
          .replace(/\\mathbf\{([^}]+)\}/g, '<strong style="color: #2d3748;">$1</strong>')
          .replace(/\\leftrightarrow/g, '↔')
          .replace(/\\rightarrow/g, '→')
          .replace(/\\wedge/g, '∧')
          .replace(/\\vee/g, '∨')
          .replace(/\\neg/g, '¬')
          // Remove curly braces around formulas that don't contain LaTeX commands
          .replace(/\{([^{}\\]+)\}/g, (match, content) => {
            // Only remove braces if content doesn't contain LaTeX commands
            return content.includes('\\') ? match : content;
          })

        // Convert header formulas to italic for mathematical conventions
        if (actualRowIndex === 0) {
          processedContent = `<em style="font-style: italic; font-family: 'Times New Roman', serif;">${processedContent}</em>`
        }

        // Apply different styling for header cells vs data cells
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

        // Add interactive hover effect for data cells (not header)
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

        // Set the processed content and add cell to row
        cell.innerHTML = processedContent
        row.appendChild(cell)
      })

      // Add completed row to table
      table.appendChild(row)
      actualRowIndex++
    })

    return table
  } catch (err) {
    console.error('Failed to create beautiful HTML table:', err)
    return null
  }
}

// Render formula using MathJax
const renderMathJax = async () => {
  console.log('renderMathJax called, mathContainer:', mathContainer.value)

  // Validate MathJax library is loaded
  if (!katexLoaded.value || !window.MathJax || !window.MathJax.typesetPromise) {
    throw new Error('MathJax not loaded or not ready')
  }

  // Validate DOM container is available
  if (!mathContainer.value) {
    throw new Error('Math container not available')
  }

  // Clean and validate formula input
  const cleanedFormula = cleanFormula(props.formula)
  if (!cleanedFormula) {
    throw new Error('MathJax Rendering: Formula is empty after cleaning')
  }

  try {
    // Clear previous content
    mathContainer.value.innerHTML = ''

    // Determine display mode and prepare formula
    const displayMode = props.displayMode || !props.inline
    let mathJaxFormula = cleanedFormula

    // For display mode, use display math delimiters
    if (displayMode) {
      mathJaxFormula = `\\[${mathJaxFormula}\\]`
    } else {
      mathJaxFormula = `\\(${mathJaxFormula}\\)`
    }

    // Create a temporary container for MathJax rendering
    const tempContainer = document.createElement('div')
    tempContainer.innerHTML = mathJaxFormula
    tempContainer.style.display = displayMode ? 'block' : 'inline'
    mathContainer.value.appendChild(tempContainer)

    // Wait for MathJax to finish typesetting
    await window.MathJax.typesetPromise([tempContainer])
    console.log('MathJax rendering successful:', cleanedFormula)

  } catch (err) {
    console.warn('MathJax rendering failed:', err)
    throw err
  }
}

// Main rendering function that orchestrates the entire rendering process
const renderFormula = async () => {
  console.log('renderFormula called with formula:', props.formula)
  if (!props.formula) return

  // Set loading state and clear any previous errors
  loading.value = true
  error.value = null

  try {
    // Wait for Vue to update DOM and ensure container is available
    await nextTick()
    console.log('After nextTick, mathContainer:', mathContainer.value)

    // Choose rendering method based on prop configuration
    if (props.type === 'katex') {
      try {
        // Load KaTeX library if not already loaded
        if (!katexLoaded.value) {
          console.log('Loading KaTeX...')
          await loadKaTeX()
        }
        await nextTick()
        console.log('Before renderKaTeX, mathContainer:', mathContainer.value)
        renderKaTeX()
      } catch (katexError) {
        // Fallback to simple LaTeX rendering if KaTeX fails
        console.warn('KaTeX failed, using fallback:', katexError)
      }
    } else if (props.type === 'mathjax') {
      try {
        // Load MathJax library if not already loaded
        if (!katexLoaded.value) {
          console.log('Loading MathJax...')
          await loadMathJax()
        } else {
          // 即使标记为已加载，也要再次确认MathJax完全就绪
          if (!window.MathJax || !window.MathJax.typesetPromise) {
            console.log('MathJax marked as loaded but not ready, reloading...')
            katexLoaded.value = false
            await loadMathJax()
          }
        }

        await nextTick()
        console.log('Before renderMathJax, mathContainer:', mathContainer.value)

        // 确保MathJax完全就绪后再渲染
        let retries = 0
        const maxRetries = 10
        while (retries < maxRetries) {
          try {
            await renderMathJax()
            break // 成功渲染，退出重试循环
          } catch (renderError) {
            retries++
            console.warn(`MathJax render attempt ${retries} failed:`, renderError)
            if (retries >= maxRetries) {
              throw renderError
            }
            // 等待一段时间后重试
            await new Promise(resolve => setTimeout(resolve, 200))
          }
        }

        console.log('MathJax rendering completed successfully')
      } catch (mathjaxError) {
        console.warn('MathJax failed after multiple attempts:', mathjaxError)
        error.value = `MathJax渲染失败: ${mathjaxError.message}`

        // 如果MathJax失败，尝试使用KaTeX作为后备
        try {
          console.log('Attempting KaTeX fallback...')
          await loadKaTeX()
          renderKaTeX()
        } catch (fallbackError) {
          console.error('Both MathJax and KaTeX failed:', fallbackError)
        }
      }
    } else {
      // Use simple LaTeX rendering for other types
    }

    // Emit success event to parent component
    emit('rendered')
  } catch (err) {
    // Handle rendering errors and emit error event
    console.error('Formula rendering failed:', err)
    error.value = `公式渲染失败: ${err.message}`
    emit('error', err)
  } finally {
    // Always reset loading state when done
    loading.value = false
  }
}

// Watch for formula changes and trigger re-rendering when formula is updated
watch(() => props.formula, (newFormula) => {
  if (newFormula) {
    renderFormula()
  }
})

// Component lifecycle hook: Render formula when component is mounted
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