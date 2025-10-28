<template>
  <div class="formula-syntax-interface">
    <!-- 标题 -->

    <!-- 按钮操作区域 -->
    <div class="button-section">
      <!-- 第一行按钮 -->
      <el-row :gutter="20" class="button-row">
        <el-col :span="4">
          <el-button type="primary" @click="analyzeFormula">
            <el-icon><Check /></el-icon>
            开始分析(Y)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="success" @click="generateFormula">
            <el-icon><MagicStick /></el-icon>
            生成公式(G)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="warning" @click="removeFormula">
            <el-icon><Delete /></el-icon>
            移除公式(V)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="info" @click="checkFormula">
            <el-icon><WarningFilled /></el-icon>
            合法性检查(K)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="danger" @click="clearResults">
            <el-icon><RefreshRight /></el-icon>
            清除结果(C)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button @click="closeInterface">
            <el-icon><Close /></el-icon>
            取消(R)
          </el-button>
        </el-col>
      </el-row>

      <!-- 第二行按钮：示例公式 -->
      <el-divider content-position="left">《离散数学基础》教材示例展示</el-divider>
      <el-row :gutter="15" class="example-buttons">
        <el-col :span="4">
          <el-button size="small" @click="loadExample('example2_4')">例题2.4</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem2_6_1')">例题2.6(1)</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem2_6_2')">例题2.6(2)</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem2_6_3')">例题2.6(3)</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem2_6_4')">例题2.6(4)</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem2_17')">例题2.17</el-button>
        </el-col>
      </el-row>

      <!-- 选项设置 -->
      <el-divider content-position="left">选择显示内容</el-divider>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-checkbox v-model="showStrictForm" size="large">
            给出符合公式归纳定义的严格形式公式
          </el-checkbox>
        </el-col>
        <el-col :span="8">
          <el-checkbox v-model="showAST" size="large">
            给出公式的抽象语法树
          </el-checkbox>
        </el-col>
        <el-col :span="8">
          <el-button type="info" @click="testBackendAPI" size="small">
            测试后端API连接
          </el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 公式输入区域 -->
    <div class="formula-input-section">
      <el-divider content-position="left">公式输入</el-divider>
      <el-input
        v-model="formulaInput"
        type="textarea"
        :rows="4"
        placeholder="请输入LaTeX格式的逻辑公式..."
        class="formula-textarea"
      />
      <div class="input-hint">
        <el-text type="info" size="small">
          支持的LaTeX符号：\neg(¬), \wedge(∧), \vee(∨), \rightarrow(→), \leftrightarrow(↔)
        </el-text>
      </div>
    </div>

    <!-- 反馈信息区域 -->
    <div v-if="feedback.length > 0" class="feedback-section">
      <el-divider content-position="left">反馈信息</el-divider>
      <div class="feedback-content">
        <div v-for="(item, index) in feedback" :key="index" class="feedback-item">
          <math-renderer
            :formula="item.formula"
            :type="'katex'"
            :display-mode="false"
            class="feedback-formula"
          />
          <el-text v-if="item.message" :type="item.type" class="feedback-message">
            {{ item.message }}
          </el-text>
        </div>
      </div>
    </div>

    <!-- 严格形式公式显示区域 -->
    <div v-if="syntaxResults.length > 0" class="results-section">
      <el-divider content-position="left">严格形式公式</el-divider>
      <div class="results-content">
        <div v-for="(result, index) in syntaxResults" :key="'syntax-' + index" class="result-item">
          <div class="result-formula">
            <strong>公式 {{ result.index }}: </strong>
            <math-renderer
              :formula="result.formula"
              :type="'katex'"
              :display-mode="false"
            />
          </div>

          <div class="syntax-content">
            <div class="syntax-row">
              <span class="syntax-label">严格形式：</span>
              <math-renderer
                :formula="result.syntaxData.strictForm"
                :type="'katex'"
                :display-mode="false"
                class="syntax-formula"
              />
            </div>
            <div class="syntax-row">
              <span class="syntax-label">简化写为：</span>
              <math-renderer
                :formula="result.syntaxData.simpleForm"
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
        </div>
      </div>
    </div>

    <!-- 抽象语法树显示区域 -->
    <div v-if="astResults.length > 0" class="results-section">
      <el-divider content-position="left">抽象语法树</el-divider>
      <div class="results-content">
        <div v-for="(result, index) in astResults" :key="'ast-' + index" class="result-item">
          <div class="result-formula">
            <strong>公式 {{ result.index }}: </strong>
            <math-renderer
              :formula="result.formula"
              :type="'katex'"
              :display-mode="false"
            />
          </div>

          <div class="ast-content">
            <div class="ast-image-container" v-if="result.astData.imageUrl">
              <img
                :src="result.astData.imageUrl"
                :alt="'公式' + result.index + '的抽象语法树'"
                class="ast-image"
                @error="handleImageError"
                @load="handleImageLoad"
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
            <div v-else class="ast-loading">
              <el-icon class="is-loading"><Loading /></el-icon>
              <span>正在生成抽象语法树...</span>
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
import {
  Check,
  MagicStick,
  Delete,
  WarningFilled,
  RefreshRight,
  Close,
  Loading
} from '@element-plus/icons-vue'
import MathRenderer from '@/components/common/MathRenderer.vue'

// 响应式数据
const formulaInput = ref('')
const showStrictForm = ref(true)
const showAST = ref(false)
const feedback = ref([])
const syntaxResults = ref([])
const astResults = ref([])
const counter = ref(0)

// 示例公式映射
const exampleFormulas = {
  example2_4: '(\\neg(\\neg p)\\rightarrow(t\\vee s))\\wedge(s\\leftrightarrow r)',
  problem2_6_1: 'p\\wedge(\\neg s)\\wedge(\\neg s)',
  problem2_6_2: '\\neg(\\neg r)\\rightarrow(\\neg(q\\vee p))',
  problem2_6_3: '((p\\vee p)\\leftrightarrow(p\\wedge s))\\rightarrow(p\\wedge s)',
  problem2_6_4: '(r\\rightarrow(q\\leftrightarrow s))\\vee(q\\leftrightarrow s)',
  problem2_17: '\\neg(p\\rightarrow q)\\wedge((\\neg q)\\leftrightarrow r)'
}

// 事件处理函数
const emit = defineEmits(['close', 'formula-calculated'])

const analyzeFormula = async () => {
  if (!formulaInput.value.trim()) {
    ElMessage.warning('请先输入公式')
    return
  }

  // 清空之前的反馈和结果
  feedback.value = []
  syntaxResults.value = []
  astResults.value = []

  const formula = normalizeFormulaFormat(formulaInput.value.trim())

  try {
    console.log('FormulaSyntaxInterface: 开始分析公式:', formula)

    // 如果需要显示严格形式公式
    if (showStrictForm.value) {
      try {
        console.log('FormulaSyntaxInterface: 开始获取严格形式公式')
        const syntaxData = await getFormulaSyntaxData(formula)

        if (syntaxData.success) {
          const syntaxResult = {
            index: counter.value + 1,
            formula: cleanFormulaForDisplay(formula),
            syntaxData: syntaxData
          }
          syntaxResults.value.push(syntaxResult)

          feedback.value.push({
            formula: cleanFormulaForDisplay(formula),
            type: 'success',
            message: '公式解析成功'
          })

          console.log('FormulaSyntaxInterface: 严格形式公式数据已获取')
        } else {
          console.warn('FormulaSyntaxInterface: 语法分析失败:', syntaxData.error)
          feedback.value.push({
            formula: cleanFormulaForDisplay(formula),
            type: 'error',
            message: '语法分析失败: ' + (syntaxData.error || '未知错误')
          })
        }
      } catch (error) {
        console.error('获取严格形式公式失败:', error)
        feedback.value.push({
          formula: cleanFormulaForDisplay(formula),
          type: 'error',
          message: `语法分析失败: ${error.message}`
        })
      }
    }

    // 如果需要生成AST图片
    if (showAST.value) {
      try {
        console.log('FormulaSyntaxInterface: 开始生成AST图片')
        const astData = await generateASTImage(formula)
        console.log('FormulaSyntaxInterface: 获取到AST数据:', astData)

        if (astData.success) {
          const astResult = {
            index: counter.value + 1,
            formula: cleanFormulaForDisplay(formula),
            astData: astData
          }
          astResults.value.push(astResult)
          console.log('FormulaSyntaxInterface: AST数据已添加到astResults')
        } else {
          console.warn('FormulaSyntaxInterface: AST生成失败:', astData.error)
          feedback.value.push({
            formula: cleanFormulaForDisplay(formula),
            type: 'error',
            message: '抽象语法树生成失败: ' + (astData.error || '未知错误')
          })
        }
      } catch (error) {
        console.error('生成AST图片失败:', error)
        feedback.value.push({
          formula: cleanFormulaForDisplay(formula),
          type: 'error',
          message: `抽象语法树生成失败: ${error.message}`
        })
      }
    }

    // 收集所有数据，一次性发送到主界面
    const completeResult = {
      formula: cleanFormulaForDisplay(formula),
      index: counter.value,
      syntaxData: null,  // 默认为空
      astData: null      // 默认为空
    }

    // 如果生成了严格形式公式，添加到结果中
    if (showStrictForm.value && syntaxResults.value.length > 0) {
      completeResult.syntaxData = syntaxResults.value[syntaxResults.value.length - 1].syntaxData
    }

    // 如果生成了AST图片，添加到结果中
    if (showAST.value && astResults.value.length > 0) {
      completeResult.astData = astResults.value[astResults.value.length - 1].astData
    }

    // 一次性发送完整结果到主界面
    emit('formula-calculated', completeResult)

    counter.value++
    ElMessage.success('公式分析完成')

  } catch (error) {
    console.error('FormulaSyntaxInterface: 处理公式时发生错误:', error)
    feedback.value.push({
      formula: cleanFormulaForDisplay(formula),
      type: 'error',
      message: `处理公式时发生错误: ${error.message}`
    })
  }
}

const generateFormula = async () => {
  try {
    // 清除之前的结果
    astResults.value = []
    syntaxResults.value = []
    feedback.value = []
    counter.value = 0

    console.log('generateFormula: 开始调用后端生成随机公式API')

    // 调用后端API生成随机公式
    const result = await callBackendApi('/formula-syntax/generate', {
      method: 'GET'
    })

    console.log('generateFormula: 后端返回结果:', result)

    if (result.success && result.formula) {
      // 设置新公式，确保格式正确
      formulaInput.value = cleanFormulaForDisplay(result.formula)
      console.log('generateFormula: 生成公式:', formulaInput.value)
      ElMessage.success('已生成随机公式')
    } else {
      console.error('generateFormula: 后端返回结果中没有formula字段:', result)
      ElMessage.error('生成公式失败：后端返回格式错误')
    }
  } catch (error) {
    console.error('generateFormula: 生成随机公式失败:', error)

    // 如果后端调用失败，使用本地备用公式
    console.log('generateFormula: 使用备用本地公式生成')
    const fallbackFormulas = [
      'p\\wedge q',
      'p\\vee q',
      '\\neg p\\rightarrow q',
      'p\\leftrightarrow q',
      '(p\\vee q)\\wedge r',
      'p\\rightarrow(q\\wedge r)',
      'p\\wedge\\neg q',
      '(p\\rightarrow q)\\vee r',
      '\\neg(\\neg p)\\rightarrow(t\\vee s)',
      '(p\\vee p)\\leftrightarrow(p\\wedge s)'
    ]

    const randomFormula = fallbackFormulas[Math.floor(Math.random() * fallbackFormulas.length)]
    formulaInput.value = cleanFormulaForDisplay(randomFormula)
    console.log('generateFormula: 使用备用公式:', formulaInput.value)
    ElMessage.warning('后端服务不可用，已使用本地公式')
  }
}

const removeFormula = () => {
  formulaInput.value = ''
  ElMessage.info('已清空公式输入')
}

const checkFormula = async () => {
  if (!formulaInput.value.trim()) {
    ElMessage.warning('请先输入公式')
    return
  }

  feedback.value = []
  const formula = normalizeFormulaFormat(formulaInput.value.trim())

  try {
    console.log('checkFormula: 开始调用后端检查公式API')
    const result = await callBackendApi('/formula-syntax/check', {
      method: 'POST',
      body: JSON.stringify({
        formula: formula
      })
    })

    console.log('checkFormula: 后端返回结果:', result)

    if (result.valid) {
      feedback.value.push({
        formula: cleanFormulaForDisplay(formula),
        type: 'success',
        message: '公式格式正确'
      })
    } else {
      feedback.value.push({
        formula: cleanFormulaForDisplay(formula),
        type: 'error',
        message: result.error || '公式格式不正确'
      })
    }

    ElMessage.success('公式合法性检查完成')
  } catch (error) {
    console.error('检查公式失败:', error)
    feedback.value.push({
      formula: cleanFormulaForDisplay(formula),
      type: 'error',
      message: `检查失败: ${error.message}`
    })
  }
}

const clearResults = () => {
  syntaxResults.value = []
  astResults.value = []
  feedback.value = []
  counter.value = 0
  ElMessage.info('已清除所有结果')
}

const testBackendAPI = async () => {
  console.log('=== 开始测试后端API ===')
  const testFormula = 'p\\wedge q'

  try {
    console.log('测试公式:', testFormula)
    const result = await getFormulaSyntaxData(testFormula)
    console.log('测试结果:', result)

    if (result.success) {
      ElMessage.success('API连接成功！严格形式: ' + result.strictForm)
    } else {
      ElMessage.error('API返回错误: ' + result.error)
    }
  } catch (error) {
    console.error('API测试失败:', error)
    ElMessage.error('API连接失败: ' + error.message)
  }

  console.log('=== API测试结束 ===')
}

const closeInterface = () => {
  emit('close')
}

const loadExample = (exampleKey) => {
  if (exampleFormulas[exampleKey]) {
    // 清除之前的结果
    astResults.value = []
    syntaxResults.value = []
    feedback.value = []
    counter.value = 0

    // 设置新公式
    formulaInput.value = cleanFormulaForDisplay(exampleFormulas[exampleKey])

    console.log('loadExample: 加载示例:', formulaInput.value)
    ElMessage.info(`已加载示例：${exampleKey}`)
  }
}

// 工具函数
const normalizeFormulaFormat = (formula) => {
  return formula.replace(/\\\\([a-zA-Z]+)/g, '\\$1')
}

const cleanFormulaForDisplay = (formula) => {
  return normalizeFormulaFormat(formula)
}


// 通用API调用函数
const callBackendApi = async (endpoint, options = {}) => {
  try {
    // 使用绝对路径，确保移动端也能正确访问
    const baseUrl = window.location.origin
    const response = await fetch(`${baseUrl}/api${endpoint}`, {
      method: 'POST', // 默认使用POST方法
      headers: {
        'Content-Type': 'application/json',
        ...options.headers
      },
      ...options
    })

    const result = await response.json()

    if (!response.ok) {
      throw new Error(result.message || `HTTP error! status: ${response.status}`)
    }

    return result
  } catch (error) {
    console.error(`API调用失败 (${endpoint}):`, error)
    throw error
  }
}

const getFormulaSyntaxData = async (formula) => {
  try {
    const baseUrl = window.location.origin
    const url = `${baseUrl}/api/formula-syntax/analyze`
    console.log('getFormulaSyntaxData: 发送请求到:', url)
    console.log('getFormulaSyntaxData: 请求数据:', { formula: formula })

    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        formula: formula
      })
    })

    console.log('getFormulaSyntaxData: 收到响应，状态:', response.status)

    if (!response.ok) {
      const errorText = await response.text()
      console.error('getFormulaSyntaxData: 响应错误，内容:', errorText)
      throw new Error(`HTTP ${response.status}: ${errorText}`)
    }

    const data = await response.json()
    console.log('getFormulaSyntaxData: 解析响应数据:', data)
    return data
  } catch (error) {
    console.error('getFormulaSyntaxData: 请求失败:', error)
    throw error
  }
}

const generateASTImage = async (formula) => {
  try {
    // 调用后端AST生成API
    const baseUrl = window.location.origin
    const response = await fetch(`${baseUrl}/api/formula-syntax/ast`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        formula: formula
      })
    })

    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: Failed to generate AST`)
    }

    const data = await response.json()
    console.log('AST API响应:', data)

    if (data.success && data.webPath) {
      return {
        success: true,
        imageUrl: data.webPath,
        dotPath: data.dotPath,
        error: null
      }
    } else {
      return {
        success: false,
        imageUrl: null,
        dotPath: null,
        error: data.error || '生成抽象语法树失败'
      }
    }
  } catch (error) {
    console.error('生成AST图片失败:', error)
    return {
      success: false,
      imageUrl: null,
      dotPath: null,
      error: error.message || '网络错误'
    }
  }
}

// 图片处理函数
const handleImageLoad = (event) => {
  console.log('AST图片加载成功:', event.target.src)
}

const handleImageError = (event) => {
  console.error('AST图片加载失败:', event.target.src)
  ElMessage.error('抽象语法树图片加载失败')
}
</script>

<style scoped>
.formula-syntax-interface {
  max-height: 80vh;
  overflow-y: auto;
}

.interface-header {
  text-align: center;
  margin-bottom: 2rem;
}

.interface-header h2 {
  color: #2c3e50;
  margin: 0;
  font-weight: 600;
}

.button-section {
  margin-bottom: 2rem;
}

.button-row {
  margin-bottom: 1rem;
}

.example-buttons .el-button {
  width: 100%;
}

.formula-input-section {
  margin: 2rem 0;
}

.formula-textarea {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
}

.input-hint {
  margin-top: 0.5rem;
}

.feedback-section,
.results-section {
  margin: 2rem 0;
}

.feedback-content {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.feedback-item {
  margin-bottom: 1rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #dee2e6;
}

.feedback-item:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.feedback-formula {
  margin-bottom: 0.5rem;
}

.feedback-message {
  display: block;
  margin-top: 0.25rem;
}

.results-content {
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.result-item {
  margin-bottom: 2rem;
  padding-bottom: 2rem;
  border-bottom: 1px solid #dee2e6;
}

.result-item:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.result-formula {
  margin-bottom: 1rem;
  font-size: 1.1rem;
}

/* 严格形式公式样式 */
.syntax-content {
  background: white;
  padding: 1rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
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

/* AST图片样式 */
.ast-content {
  background: white;
  padding: 1rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  margin: 1rem 0;
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

.ast-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  color: #666;
  font-size: 0.9em;
}

.ast-loading .el-icon {
  font-size: 2rem;
  margin-bottom: 0.5rem;
  color: #409eff;
}

/* 滚动条样式 */
.formula-syntax-interface::-webkit-scrollbar {
  width: 8px;
}

.formula-syntax-interface::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.formula-syntax-interface::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.formula-syntax-interface::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .ast-content {
    padding: 0.5rem;
    margin: 0.5rem 0;
  }

  .ast-image-container {
    padding: 0.5rem;
  }

  .ast-loading {
    padding: 1rem;
  }
}

@media (max-width: 480px) {
  .ast-content {
    padding: 0.25rem;
  }

  .ast-image-container {
    padding: 0.25rem;
    min-height: 80px;
  }

  .ast-loading {
    padding: 0.5rem;
  }

  .ast-loading .el-icon {
    font-size: 1.5rem;
  }
}
</style>