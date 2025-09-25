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

// Component props configuration
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

// Load KaTeX library dynamically from CDN
const loadKaTeX = async () => {
  // Check if KaTeX is already loaded globally
  if (window.katex) {
    katexLoaded.value = true
    return
  }

  try {
    // Load KaTeX CSS styles for mathematical formatting
    const cssLink = document.createElement('link')
    cssLink.rel = 'stylesheet'
    cssLink.href = 'https://cdn.jsdelivr.net/npm/katex@0.16.9/dist/katex.min.css'
    document.head.appendChild(cssLink)

    // Load main KaTeX JavaScript library
    await new Promise((resolve, reject) => {
      const script = document.createElement('script')
      script.src = 'https://cdn.jsdelivr.net/npm/katex@0.16.9/dist/katex.min.js'
      script.async = true
      script.onload = resolve
      script.onerror = reject
      document.head.appendChild(script)
    })

    // Load KaTeX auto-render extension for enhanced LaTeX support
    await new Promise((resolve, reject) => {
      const script = document.createElement('script')
      script.src = 'https://cdn.jsdelivr.net/npm/katex@0.16.9/dist/contrib/auto-render.min.js'
      script.async = true
      script.onload = resolve
      script.onerror = reject
      document.head.appendChild(script)
    })

    katexLoaded.value = true
    console.log('KaTeX loaded successfully')
  } catch (err) {
    console.error('Failed to load KaTeX:', err)
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
    throw new Error('Formula is empty after cleaning')
  }

  // Determine display mode based on props
  const displayMode = props.displayMode || !props.inline

  try {
    // Check if formula contains LaTeX array/table environment (both array and tabular)
    if (cleanedFormula.includes('\\begin{array}') || cleanedFormula.includes('\\begin{tabular}')) {
      console.log("Rendering as HTML table:", cleanedFormula)

      // Remove display math delimiters if present
      let tableFormula = cleanedFormula
      if (tableFormula.startsWith('\\[') && tableFormula.endsWith('\\]')) {
        tableFormula = tableFormula.slice(2, -2)
      }

      // Clear container and create beautiful HTML table
      mathContainer.value.innerHTML = ''
      const table = createBeautifulHTMLTable(tableFormula)
      if (table) {
        mathContainer.value.appendChild(table)
        console.log('Beautiful HTML table created successfully')
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
const createBeautifulHTMLTable = (latexTable) => {
  try {
    // Debug logging for troubleshooting LaTeX parsing
    console.log('createBeautifulHTMLTable input:', JSON.stringify(latexTable))
    console.log('Input length:', latexTable.length)
    console.log('Contains \\begin{array}:', latexTable.includes('\\begin{array}'))
    console.log('Contains \\end{array}:', latexTable.includes('\\end{array}'))

    // Parse the LaTeX array/tabular structure using regex to extract column spec and content
    let arrayMatch = latexTable.match(/\\begin\{array\}\{([^}]*)\}([\s\S]*?)\\end\{array\}/)
    console.log('Array regex match result:', arrayMatch)

    // If not array, try tabular format
    if (!arrayMatch) {
      arrayMatch = latexTable.match(/\\begin\{tabular\}\{([^}]*)\}([\s\S]*?)\\end\{tabular\}/)
      console.log('Tabular regex match result:', arrayMatch)
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

// Fallback HTML table creation function (simpler version)
const createHTMLTable = (latexTable) => {
  try {
    // Parse the LaTeX array/tabular structure using corrected regex pattern
    let arrayMatch = latexTable.match(/\\begin\{array\}\{([^}]*)\}([\s\S]*?)\\end\{array\}/)

    // If not array, try tabular format
    if (!arrayMatch) {
      arrayMatch = latexTable.match(/\\begin\{tabular\}\{([^}]*)\}([\s\S]*?)\\end\{tabular\}/)
    }

    if (!arrayMatch) return null

    const columns = arrayMatch[1].length // number of columns from {ccccc}
    const content = arrayMatch[2]

    // Split content by \\ (rows) and then by & (cells)
    const rows = content.split('\\\\').map(row => row.trim()).filter(row => row)

    // Create basic table element with simple styling
    const table = document.createElement('table')
    table.style.borderCollapse = 'collapse'
    table.style.margin = '0 auto'
    table.style.fontFamily = 'Times New Roman, serif'

    // Process each row
    let actualRowIndex = 0
    rows.forEach((rowContent) => {
      // Skip rows that only contain \hline
      if (rowContent.trim() === '\\hline') return

      const row = document.createElement('tr')
      const cells = rowContent.split('&').map(cell => cell.trim())

      // Process each cell in the row
      cells.forEach(cellContent => {
        // Remove \hline from cell content if present
        let cleanCellContent = cellContent.replace(/\\hline/g, '').trim()

        // Skip empty cells
        if (cleanCellContent === '') return

        const cell = actualRowIndex === 0 ? document.createElement('th') : document.createElement('td')
        cell.style.border = '1px solid #ddd'
        cell.style.padding = '8px'
        cell.style.textAlign = 'center'

        // Process cell content: Convert LaTeX commands to HTML and logic symbols
        let processedContent = cleanCellContent
          .replace(/\$([^\$]+)\$/g, '$1') // Remove $ delimiters
          .replace(/\\mathbf\{([^}]+)\}/g, '<strong>$1</strong>')
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

        cell.innerHTML = processedContent
        row.appendChild(cell)
      })

      table.appendChild(row)
      actualRowIndex++
    })

    return table
  } catch (err) {
    console.error('Failed to create HTML table:', err)
    return null
  }
}

// Fallback rendering for simple formulas using Unicode symbol replacement
const renderSimpleLaTeX = () => {
  const cleanedFormula = cleanFormula(props.formula)
  if (!cleanedFormula) {
    throw new Error('Formula is empty after cleaning')
  }

  // Map LaTeX commands to Unicode symbols for basic mathematical rendering
  const symbolMap = {
    '\\wedge': '∧',        // Logical AND
    '\\vee': '∨',         // Logical OR
    '\\neg': '¬',         // Logical NOT
    '\\rightarrow': '→',  // Implication
    '\\leftrightarrow': '↔', // Biconditional
    '\\forall': '∀',      // Universal quantifier
    '\\exists': '∃',      // Existential quantifier
    '\\in': '∈',          // Element of
    '\\subset': '⊂',      // Subset
    '\\cup': '∪',         // Union
    '\\cap': '∩',         // Intersection
    '\\emptyset': '∅',    // Empty set
    '\\land': '∧',        // Alternative AND
    '\\lor': '∨',         // Alternative OR
    '\\lnot': '¬',        // Alternative NOT
    '\\to': '→',          // Alternative implication
    '\\iff': '↔'          // Alternative biconditional
  }

  // Replace all LaTeX commands with Unicode symbols
  let rendered = cleanedFormula
  for (const [latex, symbol] of Object.entries(symbolMap)) {
    rendered = rendered.replace(new RegExp(latex, 'g'), symbol)
  }

  // Remove formatting commands that don't have direct Unicode equivalents
  rendered = rendered.replace(/\\mathbf\{([^}]+)\}/g, '$1')
  rendered = rendered.replace(/\\texttt\{([^}]+)\}/g, '$1')

  // Render the processed content to DOM
  if (mathContainer.value) {
    mathContainer.value.innerHTML = rendered
    mathContainer.value.className += ' simple-latex'
    console.log('Simple LaTeX rendering completed:', rendered)
  } else {
    throw new Error('Math container not available for simple rendering')
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
        renderSimpleLaTeX()
      }
    } else {
      // Use simple LaTeX rendering for other types
      renderSimpleLaTeX()
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