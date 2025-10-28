<template>
  <div class="left-panel" @wheel="handleWheel">
    <div class="panel-header">
      <h3>公式内容</h3>
      <el-button size="small" type="warning" @click="handleClear">
        <el-icon><Delete /></el-icon>
        清空
      </el-button>
    </div>
    <div class="panel-content" ref="content">
      <div class="formula-display">
        <math-renderer
          :formula="cleanFormulaForDisplay(currentFormula)"
          :type="'katex'"
          :display-mode="true"
          @rendered="onFormulaRendered"
          @error="onFormulaError"
        />
      </div>

      <!-- 显示所有公式和真值表 -->
      <div v-if="formulaResults.length > 0" class="formula-results">
        <div v-for="(result, index) in formulaResults" :key="index" class="result-item">
          <div class="result-formula">
            <strong>公式 {{ index + 1 }}: </strong>
            <math-renderer
              :formula="cleanFormulaForDisplay(result.formula)"
              :type="'katex'"
              :display-mode="false"
            />
          </div>

          <!-- 范式扩展结果 -->
          <div v-if="result.type === 'normal-form-expansion'" class="normal-form-expansion-results">
            <!-- 变量集信息 -->
            <div class="variable-info">
              <h4 class="result-title">扩展变量集：</h4>
              <span>{{ result.variableSet }}</span>
            </div>

            <!-- 扩展步骤 -->
            <div v-if="result.expansionSteps && result.expansionSteps.length > 0" class="expansion-steps">
              <h4 class="expansion-title">{{ result.targetType }}扩展步骤：</h4>
              <div v-for="(step, stepIndex) in result.expansionSteps" :key="'expansion-step-' + stepIndex" class="expansion-step-inline">
                <el-text type="primary" class="step-description">{{ step.description }}</el-text>
                <math-renderer
                  :formula="step.formula"
                  :type="'katex'"
                  :display-mode="false"
                  class="step-formula-inline"
                />
                <span v-if="step.binaryCode" class="formula-code-inline">[{{ step.binaryCode }}]</span>
                <el-text type="success" class="step-description">得到</el-text>
                <math-renderer
                  :formula="step.resultCodes"
                  :type="'mathjax'"
                  :display-mode="false"
                  class="result-codes-inline"
                />
              </div>
            </div>

            <!-- 最终结果 -->
            <div class="pnf-result">
              <div v-if="result.pdnfResult">
                <h5 class="pnf-title">最终的主析取范式(PDNF)是：</h5>
                <math-renderer
                  :formula="result.pdnfResult"
                  :type="'mathjax'"
                  :display-mode="false"
                />
              </div>
              <div v-if="result.pcnfResult">
                <h5 class="pnf-title">相应的主合取范式(PCNF)是：</h5>
                <math-renderer
                  :formula="result.pcnfResult"
                  :type="'mathjax'"
                  :display-mode="false"
                />
              </div>
            </div>
          </div>

          <!-- 等值演算检查结果 -->
          <div v-if="result.type === 'equiv-calculus-check'" class="equiv-calculus-results">
            <div class="result-title">
              <strong>检查演算步骤</strong>
            </div>

            <!-- 演算步骤显示 -->
            <div class="calculus-steps">
              <div v-for="(step, index) in result.steps" :key="index" class="calculus-step">
                <div v-if="index === 0" class="step-first">
                  <math-renderer
                    :formula="step.formula"
                    :type="'katex'"
                    :display-mode="false"
                    class="step-formula"
                  />
                  <span v-if="step.comment" class="step-comment"> // {{ step.comment }}</span>
                </div>
                <div v-else class="step-subsequent">
                  <span class="equiv-prefix">≡</span>
                  <math-renderer
                    :formula="step.formula"
                    :type="'katex'"
                    :display-mode="false"
                    class="step-formula"
                  />
                  <span v-if="step.comment" class="step-comment"> // {{ step.comment }}</span>
                </div>
              </div>
            </div>

            <!-- 检查结果 -->
            <div class="check-result">
              <div v-if="result.valid" class="valid-result">
                <el-alert
                  title="✓ 检查通过"
                  type="success"
                  description="通过真值表检验，上述等值演算过程没有错误。"
                  :closable="false"
                  show-icon
                />
              </div>
              <div v-else class="invalid-result">
                <el-alert
                  title="✗ 检查失败"
                  type="error"
                  :description="result.errorMessage"
                  :closable="false"
                  show-icon
                />

                <!-- 反例信息 -->
                <div v-if="result.counterExample" class="counter-example">
                  <h4 class="counter-title">反例：</h4>
                  <p>在真值赋值 {{ result.counterExample }} 下，以下公式不是重言式：</p>
                  <div class="checking-formula">
                    <math-renderer
                      :formula="result.checkingFormula"
                      :type="'katex'"
                      :display-mode="true"
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 显示推理有效性论证检查结果 -->
          <div v-if="result.type === 'reason-argument-check'" class="reason-argument-check-results">
            <div class="result-title">
              <strong>检查推理步骤</strong>
            </div>

            <!-- 推理关系显示 -->
            <div v-if="result.latexString" class="reasoning-overview">
              <h4>推理关系：</h4>
              <div class="reasoning-formula">
                <math-renderer
                  :formula="result.latexString"
                  :type="'katex'"
                  :display-mode="true"
                  class="formula-renderer"
                />
              </div>
            </div>

            <!-- 推理步骤显示 -->
            <div class="reasoning-steps">
              <div v-for="(step, index) in result.steps" :key="index" class="reasoning-step">
                <div class="step-content">
                  <span class="step-number">({{ step.serialNo }})</span>
                  <math-renderer
                    :formula="step.formula"
                    :type="'katex'"
                    :display-mode="false"
                    class="step-formula"
                  />
                  <span v-if="step.ruleName" class="step-rule"> // {{ step.prevSN }}{{ step.ruleName }}</span>
                </div>
              </div>
            </div>

            <!-- 检查过程显示 -->
            <div v-if="result.checkSteps && result.checkSteps.length > 0" class="check-process">
              <h4>检查过程：</h4>
              <div v-for="(step, index) in result.checkSteps" :key="index" class="check-step">
                <span class="check-text">检验步骤({{ step.serialNo }}){{ step.checkType }}</span>
                <math-renderer
                  :formula="step.formula"
                  :type="'katex'"
                  :display-mode="false"
                  class="check-formula"
                />
              </div>
            </div>

            <!-- 检查结果 -->
            <div class="check-result">
              <div v-if="result.valid" class="valid-result">
                <el-alert
                  title="✓ 检查通过"
                  type="success"
                  description="通过真值表检验，上述推理证明过程没有错误。"
                  :closable="false"
                  show-icon
                />
              </div>
              <div v-else class="invalid-result">
                <el-alert
                  title="✗ 检查失败"
                  type="error"
                  :description="result.errorMessage"
                  :closable="false"
                  show-icon
                />

                <!-- 反例信息 -->
                <div v-if="result.counterExample" class="counter-example">
                  <h4 class="counter-title">反例：</h4>
                  <p>在真值赋值 {{ result.counterExample }} 下，以下公式不是重言式：</p>
                  <div class="checking-formula">
                    <math-renderer
                      :formula="result.checkingFormula"
                      :type="'katex'"
                      :display-mode="true"
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 显示真值表 - 优先使用新的LaTeX表格格式 -->
          <div class="truth-table">
            <!-- 如果有latexTable，使用MathRenderer渲染 -->
            <div v-if="result.tableData && result.tableData.latexTable" class="truth-table-container">
              <math-renderer
                :key="'main-table-' + index + '-' + Date.now()"
                :formula="result.tableData.latexTable"
                :type="'katex'"
                :display-mode="true"
                class="truth-table-content"
              />
            </div>
            <!-- 保持原有的HTML表格作为后备 -->
            <div v-else-if="result.tableData && result.tableData.headers" class="truth-table-legacy">
              <table class="truth-table-html">
                <thead>
                  <tr>
                    <th v-for="(header, headerIndex) in result.tableData.headers" :key="headerIndex" class="header-cell">
                      <math-renderer
                        :formula="cleanFormulaForDisplay(header)"
                        :type="'katex'"
                        :display-mode="false"
                      />
                    </th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(row, rowIndex) in result.tableData.rows" :key="rowIndex">
                    <td v-for="(cell, cellIndex) in [...row.variableValues, row.resultValue]" :key="cellIndex" class="data-cell">
                      <math-renderer
                        :formula="cleanFormulaForDisplay(cell)"
                        :type="'katex'"
                        :display-mode="false"
                      />
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            <!-- 最终后备：显示原始truthTable字符串 -->
            <div v-else-if="result.truthTable" class="truth-table-fallback">
              <math-renderer
                :formula="result.truthTable"
                :type="'katex'"
                :display-mode="true"
                class="truth-table-content"
              />
            </div>

            <!-- 显示真值计算结果 -->
            <div v-else-if="result.truthValue !== undefined" class="truth-value-result">
              <div class="truth-value-display">
                <span class="truth-value-label">公式真值 = </span>
                <el-tag
                  :type="result.truthValue ? 'success' : 'danger'"
                  size="large"
                  class="truth-value-tag"
                >
                  {{ result.truthValue ? '1' : '0' }}
                </el-tag>
              </div>
            </div>
          </div>

          <!-- 显示详细计算过程 -->
          <div v-if="result.detailedSteps && result.detailedSteps.length > 0" class="detailed-steps">
            <div class="steps-content">
              <div v-for="(step, stepIndex) in result.detailedSteps" :key="stepIndex" class="step-item">
                <div v-if="step.explanation" class="step-explanation">
                  {{ step.explanation }}
                </div>
                <div class="step-formula">
                  <math-renderer
                    :formula="step.formula"
                    :type="'katex'"
                    :display-mode="true"
                  />
                </div>
              </div>
            </div>
          </div>

          <!-- 显示公式类型 -->
          <div v-if="result.formulaType" class="formula-type">
            <el-tag :type="getFormulaTypeTag(result.formulaType)" class="type-tag">
              {{ result.formulaType }}
            </el-tag>
          </div>

          <!-- 显示严格形式公式 -->
          <div v-if="result.syntaxData" class="syntax-content">
            <h5 class="syntax-title">严格形式公式：</h5>
            <div class="syntax-row">
              <span class="syntax-label">严格形式：</span>
              <math-renderer
                :formula="cleanFormulaForDisplay(result.syntaxData.strictForm)"
                :type="'katex'"
                :display-mode="false"
                class="syntax-formula"
              />
            </div>
            <div class="syntax-row">
              <span class="syntax-label">简化写为：</span>
              <math-renderer
                :formula="cleanFormulaForDisplay(result.syntaxData.simpleForm)"
                :type="'katex'"
                :display-mode="false"
                class="syntax-formula"
              />
            </div>
            <div class="syntax-row">
              <span class="syntax-label">公式类型：</span>
              <span class="syntax-type">{{ result.syntaxData.formulaType }}</span>
            </div>
          </div>

          <!-- 显示抽象语法树 -->
          <div v-if="result.astData" class="ast-content">
            <h5 class="ast-title">抽象语法树：</h5>
            <div class="ast-image-container" v-if="result.astData.imageUrl">
              <img
                :src="result.astData.imageUrl"
                :alt="'公式' + result.index + '的抽象语法树'"
                class="ast-image"
                @error="handleASTImageError"
                @load="handleASTImageLoad"
              />
            </div>
            <div v-else-if="result.astData.error" class="ast-error">
              <el-alert
                title="生成抽象语法树失败"
                :description="result.astData.error"
                type="error"
                show-icon
                :closable="false"
              />
            </div>
          </div>

          <!-- 显示范式计算结果 -->
          <div v-if="result.cnfResult || result.dnfResult" class="normal-form-results">
            <!-- CNF 结果 -->
            <div v-if="result.cnfResult" class="normal-form-result">
              <h5 class="result-title">合取范式结果：</h5>
              <div class="calculation-steps" v-if="result.cnfSteps && result.cnfSteps.length > 0">
                <div v-for="(step, stepIndex) in result.cnfSteps" :key="'cnf-step-' + stepIndex" class="calculation-step">
                  <math-renderer
                    :formula="step.formula"
                    :type="'katex'"
                    :display-mode="false"
                    class="step-formula"
                  />
                  <span class="step-comment">{{ step.comment }}</span>
                </div>
              </div>
              <div class="final-result">
                <strong>最终简化为CNF：</strong>
                <math-renderer
                  :formula="result.cnfResult.finalFormula"
                  :type="'katex'"
                  :display-mode="false"
                />
              </div>
            </div>

            <!-- DNF 结果 -->
            <div v-if="result.dnfResult" class="normal-form-result">
              <h5 class="result-title">析取范式结果：</h5>
              <div class="calculation-steps" v-if="result.dnfSteps && result.dnfSteps.length > 0">
                <div v-for="(step, stepIndex) in result.dnfSteps" :key="'dnf-step-' + stepIndex" class="calculation-step">
                  <math-renderer
                    :formula="step.formula"
                    :type="'katex'"
                    :display-mode="false"
                    class="step-formula"
                  />
                  <span class="step-comment">{{ step.comment }}</span>
                </div>
              </div>
              <div class="final-result">
                <strong>最终简化为DNF：</strong>
                <math-renderer
                  :formula="result.dnfResult.finalFormula"
                  :type="'katex'"
                  :display-mode="false"
                />
              </div>
            </div>

            <!-- 主范式结果 -->
            <div v-if="(result.cnfResult && (result.cnfResult.pcnf || result.cnfResult.pdnf)) || (result.dnfResult && (result.dnfResult.pdnf || result.dnfResult.pcnf))" class="pnf-result">
              <div v-if="result.cnfResult && result.cnfResult.pcnf">
                <h5 class="pnf-title">最终的主合取范式是：</h5>
                <math-renderer
                  :formula="result.cnfResult.pcnf"
                  :type="'mathjax'"
                  :display-mode="false"
                />
              </div>
              <div v-if="result.cnfResult && result.cnfResult.pdnf">
                <h5 class="pnf-title">相应的主析取范式是：</h5>
                <math-renderer
                  :formula="result.cnfResult.pdnf"
                  :type="'mathjax'"
                  :display-mode="false"
                />
              </div>
              <div v-if="result.dnfResult && result.dnfResult.pdnf">
                <h5 class="pnf-title">最终的主析取范式是：</h5>
                <math-renderer
                  :formula="result.dnfResult.pdnf"
                  :type="'mathjax'"
                  :display-mode="false"
                />
              </div>
              <div v-if="result.dnfResult && result.dnfResult.pcnf">
                <h5 class="pnf-title">相应的主合取范式是：</h5>
                <math-renderer
                  :formula="result.dnfResult.pcnf"
                  :type="'mathjax'"
                  :display-mode="false"
                />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'
import MathRenderer from '@/components/common/MathRenderer.vue'

// 定义 props
const props = defineProps({
  formulaResults: {
    type: Array,
    default: () => []
  },
  currentFormula: {
    type: String,
    default: '\\forall x \\in S, P(x) \\rightarrow Q(x)'
  }
})

// 定义 emits
const emit = defineEmits(['clear'])

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

// 公式渲染完成回调
const onFormulaRendered = () => {
  console.log('Formula rendered successfully on left panel')
}

// 公式渲染错误回调
const onFormulaError = (error) => {
  console.error('Formula rendering error on left panel:', error)
  ElMessage.error('公式渲染失败')
}

// AST图片加载成功处理
const handleASTImageLoad = (event) => {
  console.log('LeftPanel: AST图片加载成功:', event.target.src)
}

// AST图片加载失败处理
const handleASTImageError = (event) => {
  console.error('LeftPanel: AST图片加载失败:', event.target.src)
  ElMessage.error('抽象语法树图片加载失败')
}

// 获取公式类型标签样式
const getFormulaTypeTag = (type) => {
  switch (type) {
    case '重言式': return 'success'
    case '可满足式': return 'warning'
    case '矛盾式': return 'danger'
    default: return 'info'
  }
}

// 统一处理公式格式，将双反斜杠转换为单反斜杠
const normalizeFormulaFormat = (formula) => {
  if (!formula) return ''

  return formula
    // 处理双反斜杠的命令
    .replace(/\\\\([a-zA-Z]+)/g, '\\$1')
    // 处理多余的反斜杠
    .replace(/\\+/g, '\\')
    // 处理LaTeX环境
    .replace(/\\begin\{([^}]+)\}\\s*\\end\{\1\}/g, '')
    // 清理多余的空格和换行
    .replace(/\s+/g, ' ')
    .trim()
}

// 清理公式中的反斜杠，用于显示
const cleanFormulaForDisplay = (formula) => {
  if (!formula) return ''

  return normalizeFormulaFormat(formula)
    // 处理特殊符号显示
    .replace(/\\text\{([^}]+)\}/g, '$1')
    // 处理数学模式
    .replace(/\$([^$]+)\$/g, '$1')
    // 处理LaTeX命令
    .replace(/\\([a-zA-Z]+)/g, (match, command) => {
      const commandMap = {
        'forall': '∀',
        'exists': '∃',
        'in': '∈',
        'notin': '∉',
        'subset': '⊂',
        'subseteq': '⊆',
        'cup': '∪',
        'cap': '∩',
        'emptyset': '∅',
        'land': '∧',
        'lor': '∨',
        'neg': '¬',
        'rightarrow': '→',
        'leftrightarrow': '↔',
        'implies': '⇒',
        'equiv': '≡',
        'times': '×',
        'div': '÷',
        'pm': '±',
        'neq': '≠',
        'geq': '≥',
        'leq': '≤',
        'alpha': 'α',
        'beta': 'β',
        'gamma': 'γ',
        'delta': 'δ',
        'epsilon': 'ε',
        'theta': 'θ',
        'lambda': 'λ',
        'mu': 'μ',
        'pi': 'π',
        'sigma': 'σ',
        'phi': 'φ',
        'omega': 'ω'
      }
      return commandMap[command] || match
    })
}
</script>

<style scoped>
.left-panel {
  width: 50%;
  height: 100%;
  border: 1px solid #e4e7ed;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  min-width: 300px;
  flex: 1;
  border-right: 2px solid #dcdfe6;
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
  overflow-y: auto;
  padding: 1rem;
  scroll-behavior: smooth;
}

.formula-display {
  min-height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  background: #fafafa;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

/* 滚动条样式 */
.panel-content::-webkit-scrollbar {
  width: 8px;
}

.panel-content::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.panel-content::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.panel-content::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}

/* 公式结果样式 */
.formula-results {
  margin-top: 2rem;
}

.result-item {
  margin-bottom: 2rem;
  padding: 1.5rem;
  background: white;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.result-formula {
  margin-bottom: 1rem;
  font-size: 1.1rem;
  font-weight: 500;
}

.result-formula strong {
  color: #2c3e50;
}

.truth-table {
  margin: 1rem 0;
  background: white;
  padding: 1rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

/* 新的LaTeX表格容器样式 */
.truth-table-container {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.truth-table-container .truth-table-content {
  width: 100%;
  max-width: 100%;
  overflow-x: auto;
}

/* 后备样式 */
.truth-table-legacy,
.truth-table-fallback {
  width: 100%;
}

/* HTML表格样式 */
.truth-table-html {
  width: 100%;
  border-collapse: collapse;
  border: 2px solid #dee2e6;
  border-radius: 8px;
  overflow: hidden;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.truth-table-html th {
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  padding: 0.75rem;
  text-align: center;
  font-weight: 600;
  color: #495057;
}

.truth-table-html td {
  border: 1px solid #dee2e6;
  padding: 0.75rem;
  text-align: center;
  vertical-align: middle;
}

.truth-table-html thead th {
  border-bottom: 2px solid #dee2e6;
}

.truth-table-html tbody tr:nth-child(even) {
  background: #f8f9fa;
}

.truth-table-html tbody tr:hover {
  background: #e9ecef;
}

/* 真值计算结果样式 */
.truth-value-result {
  margin: 1rem 0;
  padding: 1rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.truth-value-display {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.truth-value-label {
  font-weight: 600;
  color: #2c3e50;
}

.truth-value-tag {
  font-size: 1.1rem;
  font-weight: bold;
}

/* 详细计算步骤样式 */
.detailed-steps {
  margin: 1rem 0;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.steps-content {
  background: white;
  padding: 1rem;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.step-item {
  margin-bottom: 1rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #dee2e6;
}

.step-item:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.step-explanation {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 1rem;
}

.step-formula {
  margin: 1rem 0;
  padding: 1rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}

.formula-type {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-top: 1rem;
}

.type-tag {
  margin-left: 0.5rem;
}

/* 严格形式公式结果样式 */
.syntax-content {
  background: white;
  padding: 1rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  margin: 1rem 0;
}

.syntax-title {
  margin: 0 0 1rem 0;
  color: #2c3e50;
  font-size: 1rem;
  font-weight: 600;
}

/* AST图片样式 */
.ast-content {
  background: white;
  padding: 1rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  margin: 1rem 0;
}

.ast-title {
  margin: 0 0 1rem 0;
  color: #2c3e50;
  font-size: 1rem;
  font-weight: 600;
}

.ast-image-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
  min-height: 100px;
}

.ast-image {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  object-fit: contain;
}

.ast-error {
  margin: 1rem 0;
}

.syntax-row {
  display: flex;
  align-items: center;
  margin-bottom: 0.75rem;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.syntax-row:last-child {
  margin-bottom: 0;
}

.syntax-label {
  font-weight: 600;
  color: #374151;
  min-width: 80px;
  flex-shrink: 0;
}

.syntax-formula {
  flex: 1;
  min-width: 0;
}

.syntax-type {
  color: #059669;
  font-weight: 500;
  background: #f0fdf4;
  padding: 0.25rem 0.75rem;
  border-radius: 4px;
  border: 1px solid #bbf7d0;
}

/* 范式计算结果样式 */
.normal-form-results {
  margin: 1rem 0;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.normal-form-result {
  background: white;
  padding: 1rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  margin: 1rem 0;
}

.result-title {
  margin: 0 0 1rem 0;
  color: #2c3e50;
  font-size: 1rem;
  font-weight: 600;
}

.calculation-steps {
  margin: 1rem 0;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.calculation-step {
  display: flex;
  align-items: center;
  margin-bottom: 0.75rem;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.calculation-step:last-child {
  margin-bottom: 0;
}

.calculation-step .step-formula {
  flex: 1;
  min-width: 0;
}

.step-comment {
  font-size: 0.9em;
  color: #666;
  margin-left: 0.5rem;
}

.final-result {
  margin-top: 1rem;
  padding: 1rem;
  background: #e8f5e8;
  border-radius: 4px;
  border: 1px solid #c3e6c3;
}

.expansion-steps {
  margin: 1.5rem 0;
  padding: 1.5rem;
  background: #f0f8ff;
  border-radius: 8px;
  border: 2px solid #4a90e2;
}

.expansion-title {
  margin: 0 0 1rem 0;
  color: #2c3e50;
  font-size: 1.1rem;
  font-weight: 600;
  text-align: center;
}

.expansion-step-inline {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  margin-bottom: 0.75rem;
  padding: 0.5rem 1rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #d0e3ff;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.expansion-step-inline:last-child {
  margin-bottom: 0;
}

.step-description {
  font-weight: 600;
  font-size: 0.95em;
  white-space: nowrap;
}

.step-formula-inline {
  background: #f8f9ff;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  border: 1px solid #d0e3ff;
}

.formula-code-inline {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  background: #f1f3f4;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.9em;
  color: #5f6368;
  border: 1px solid #dadce0;
  white-space: nowrap;
}

.result-codes-inline {
  background: #f8fff8;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  border: 1px solid #c3e6c3;
}

.pnf-result {
  margin-top: 1rem;
  padding: 1rem;
  background: #fff3cd;
  border-radius: 4px;
  border: 1px solid #ffeaa7;
}

.pnf-title {
  margin: 0.5rem 0;
  color: #856404;
  font-size: 0.95rem;
  font-weight: 600;
}

/* 范式扩展结果样式 */
.normal-form-expansion-results {
  margin: 1rem 0;
  padding: 1.5rem;
  background: linear-gradient(135deg, #f6f9ff 0%, #e8f4fd 100%);
  border-radius: 8px;
  border: 2px solid #4a90e2;
  box-shadow: 0 4px 12px rgba(74, 144, 226, 0.15);
}

.variable-info {
  margin: 1rem 0;
  padding: 1rem;
  background: white;
  border-radius: 6px;
  border: 1px solid #d0e3ff;
}

/* 等值演算检查结果样式 */
.equiv-calculus-results {
  margin: 1rem 0;
  padding: 1.5rem;
  background: linear-gradient(135deg, #fff8f0 0%, #fef3e2 100%);
  border-radius: 8px;
  border: 2px solid #ff9800;
  box-shadow: 0 4px 12px rgba(255, 152, 0, 0.15);
}

.calculus-steps {
  margin: 1.5rem 0;
  padding: 1.5rem;
  background: #f0f8ff;
  border-radius: 8px;
  border: 2px solid #4a90e2;
}

.calculus-step {
  margin-bottom: 0.75rem;
  font-family: 'Times New Roman', serif;
}

.calculus-step:last-child {
  margin-bottom: 0;
}

.step-first {
  padding-left: 2rem;
}

.step-subsequent {
  display: flex;
  align-items: center;
  padding-left: 2rem;
}

.equiv-prefix {
  margin-right: 1rem;
  font-size: 18px;
  font-weight: bold;
  color: #409eff;
}

.calculus-step .step-formula {
  background: #f8f9ff;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  border: 1px solid #d0e3ff;
  margin-right: 10px;
}

.calculus-step .step-comment {
  font-style: italic;
  color: #666;
  font-size: 14px;
}

.check-result {
  margin-top: 1.5rem;
}

.valid-result, .invalid-result {
  padding: 1rem;
  border-radius: 4px;
}

.counter-example {
  margin-top: 1rem;
  padding: 1rem;
  background: #fff3cd;
  border: 1px solid #ffeaa7;
  border-radius: 4px;
}

.counter-title {
  margin: 0 0 0.5rem 0;
  color: #856404;
  font-size: 1rem;
  font-weight: 600;
}

.checking-formula {
  margin-top: 0.5rem;
  padding: 1rem;
  background: white;
  border: 1px solid #ddd;
  border-radius: 4px;
  text-align: center;
}

/* 推理有效性论证检查结果样式 */
.reason-argument-check-results {
  margin: 1rem 0;
  padding: 1.5rem;
  background: linear-gradient(135deg, #f0f8ff 0%, #e6f3ff 100%);
  border-radius: 8px;
  border: 2px solid #4a90e2;
  box-shadow: 0 4px 12px rgba(74, 144, 226, 0.15);
}

.reasoning-overview {
  margin-bottom: 20px;
  padding: 15px;
  background: #f8f9ff;
  border-radius: 8px;
  border: 1px solid #4a90e2;
}

.reasoning-steps {
  margin: 1.5rem 0;
  padding: 1.5rem;
  background: #f0f8ff;
  border-radius: 8px;
  border: 2px solid #4a90e2;
}

.reasoning-step {
  margin-bottom: 0.75rem;
  font-family: 'Times New Roman', serif;
}

.reasoning-step:last-child {
  margin-bottom: 0;
}

.step-content {
  display: flex;
  align-items: center;
  gap: 10px;
}

.step-number {
  font-weight: bold;
  color: #409eff;
  min-width: 40px;
}

.reasoning-step .step-formula {
  background: #f8f9ff;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  border: 1px solid #d0e3ff;
}

.step-rule {
  font-style: italic;
  color: #666;
  font-size: 14px;
}

.check-process {
  margin: 1.5rem 0;
  padding: 1rem;
  background: #fff9e6;
  border-radius: 8px;
  border: 1px solid #ffc107;
}

.check-step {
  display: flex;
  align-items: center;
  margin-bottom: 0.5rem;
  gap: 10px;
}

.check-text {
  font-weight: bold;
  color: #856404;
}

.check-step .check-formula {
  background: #fff;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  border: 1px solid #ddd;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .left-panel {
    width: 100%;
    height: 50%;
    min-width: unset;
    border-right: 1px solid #e4e7ed;
    border-bottom: 2px solid #dcdfe6;
  }

  .normal-form-result {
    padding: 0.5rem;
  }

  .calculation-step {
    flex-direction: column;
    align-items: flex-start;
  }

  .step-comment {
    margin-left: 0;
    margin-top: 0.25rem;
  }

  .expansion-step-inline {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.25rem;
  }

  .step-description {
    white-space: normal;
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
}
</style>