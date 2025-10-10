<template>
  <div class="truth-table-interface">
    <!-- 按钮操作区域 -->
    <div class="button-section">
      <!-- 第一行按钮：开始计算、生成公式、移除公式、公式合法性检查 -->
      <el-row :gutter="20" class="button-row">
        <el-col :span="4">
          <el-button type="primary" @click="startCalculation">
            <el-icon><Check /></el-icon>
            开始生成(Y)
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
      <el-divider content-position="left">离散数学教材例题展示</el-divider>
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
        <el-col :span="6">
          <el-checkbox v-model="showDetailedTable" size="large">
            显示详细公式真值表
          </el-checkbox>
        </el-col>
        <el-col :span="6">
          <el-checkbox v-model="checkFormulaType" size="large">
            判断公式是否为重言式、可满足式或矛盾式
          </el-checkbox>
        </el-col>
        <el-col :span="6">
          <el-checkbox v-model="showStrictForm" size="large">
            给出符合公式归纳定义的严格形式公式
          </el-checkbox>
        </el-col>
        <el-col :span="6">
          <el-checkbox v-model="showAST" size="large">
            给出公式的抽象语法树
          </el-checkbox>
        </el-col>
      </el-row>
      <el-row :gutter="20" style="margin-top: 1rem;">
        <el-col :span="12">
          <el-button type="info" @click="testSyntaxAPI" size="small">
            测试语法API连接
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
        placeholder="请输入LaTeX格式的逻辑公式，多个公式用分号分隔..."
        class="formula-textarea"
      />
      <div class="input-hint">
        <el-text type="info" size="small">
          支持的LaTeX符号：\\neg(¬), \\wedge(∧), \\vee(∨), \\rightarrow(→), \\leftrightarrow(↔)
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

    <!-- 结果显示区域 -->
    <div v-if="results.length > 0" class="results-section">
      <el-divider content-position="left">真值表结果</el-divider>
      <div class="results-content">
        <div v-for="(result, index) in results" :key="index" class="result-item">
          <div class="result-formula">
            <strong>{{ result.index }}: </strong>
            <math-renderer
              :formula="result.formula"
              :type="'katex'"
              :display-mode="false"
            />
          </div>

          <div class="truth-table">
            <!-- 直接显示来自legacy的LaTeX表格 -->
            <div v-if="result.tableData && result.tableData.latexTable" class="truth-table-wrapper">
              <math-renderer
                :key="'table-' + result.index + '-' + Date.now()"
                :formula="result.tableData.latexTable"
                :type="'katex'"
                :display-mode="true"
                class="truth-table-content"
              />
            </div>
            <!-- 保持原有的HTML表格作为后备 -->
            <div v-else-if="result.tableData && result.tableData.headers" class="truth-table-vue">
              <table class="truth-table-html">
                <thead>
                  <tr>
                    <th v-for="(header, index) in result.tableData.headers" :key="index" class="header-cell">
                      <math-renderer
                        :formula="header"
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
                        :formula="cell"
                        :type="'katex'"
                        :display-mode="false"
                      />
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            <!-- 最终后备：原有LaTeX字符串 -->
            <math-renderer
              v-else
              :formula="result.truthTable"
              :type="'katex'"
              :display-mode="true"
              class="truth-table-content"
            />
          </div>

          <div v-if="checkFormulaType && result.tableData && result.tableData.formulaType" class="formula-type">
            <math-renderer
              :formula="result.formula"
              :type="'katex'"
              :display-mode="false"
              class="type-formula"
            />
            <el-tag :type="getFormulaTypeTag(result.tableData.formulaType)" class="type-tag">
              {{ translateFormulaType(result.tableData.formulaType) }}
            </el-tag>
          </div>
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
const showDetailedTable = ref(true)
const checkFormulaType = ref(true)
const showStrictForm = ref(false)
const showAST = ref(false)
const feedback = ref([])
const results = ref([])
const syntaxResults = ref([])
const astResults = ref([])
const counter = ref(0)
const astGenerating = ref(false)

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
const emit = defineEmits(['close', 'formula-calculated', 'syntax-calculated'])

const startCalculation = async () => {
  if (!formulaInput.value.trim()) {
    ElMessage.warning('请先输入公式')
    return
  }

  // 清空之前的反馈和结果
  feedback.value = []
  results.value = []  // 清除原来的真值表结果
  syntaxResults.value = [] // 清除之前的严格形式公式结果
  astResults.value = [] // 清除之前的AST结果

  // 分割多个公式并统一格式
  const formulas = formulaInput.value.split(';')
    .map(f => f.trim())
    .filter(f => f)
    .map(f => normalizeFormulaFormat(f))  // 统一转换为单反斜杠格式

  try {
    console.log('TruthTableInterface: 开始计算真值表，公式:', formulas)
    // 调用后端API生成真值表
    const tableData = await generateTruthTable(formulas, showDetailedTable.value, checkFormulaType.value)
    console.log('TruthTableInterface: 收到API响应:', tableData)

    // 添加到结果，直接存储表格数据
    const result = {
      index: counter.value + 1,
      formula: cleanFormulaForDisplay(formulas[0]), // 暂时只处理第一个公式
      tableData: tableData, // 直接存储表格数据
      truthTable: '' // 保留空字符串用于后备
    }

    // 如果需要检查公式类型
    if (checkFormulaType.value && tableData.formulaType) {
      result.formulaType = translateFormulaType(tableData.formulaType)
    }

    // 如果需要显示严格形式公式
    if (showStrictForm.value) {
      try {
        console.log('TruthTableInterface: 开始获取严格形式公式，公式:', formulas[0])
        console.log('TruthTableInterface: showStrictForm:', showStrictForm.value)
        console.log('TruthTableInterface: counter.value:', counter.value)

        const syntaxData = await getFormulaSyntaxData(formulas[0])
        console.log('TruthTableInterface: 获取到语法数据:', syntaxData)

        if (syntaxData.success) {
          // 创建独立的严格形式公式结果
          const syntaxResult = {
            index: counter.value + 1,
            formula: cleanFormulaForDisplay(formulas[0]),
            syntaxData: syntaxData
          }
          syntaxResults.value.push(syntaxResult)

          console.log('TruthTableInterface: 严格形式公式数据已添加到syntaxResults，将在主界面统一显示')
          console.log('TruthTableInterface: syntaxResults.value.length:', syntaxResults.value.length)
        } else {
          console.warn('TruthTableInterface: 语法分析失败:', syntaxData.error)
          ElMessage.error('语法分析失败: ' + (syntaxData.error || '未知错误'))
        }
      } catch (error) {
        console.error('获取严格形式公式失败:', error)
        ElMessage.error(`获取严格形式公式失败: ${error.message}`)
      }
    } else {
      console.log('TruthTableInterface: 未勾选严格形式公式选项，跳过语法分析')
    }

    // 如果需要生成AST图片
    if (showAST.value) {
      try {
        console.log('TruthTableInterface: 开始生成AST图片，公式:', formulas[0])
        const astData = await generateASTImageForStartCalculation(formulas[0])
        console.log('TruthTableInterface: 获取到AST数据:', astData)

        if (astData.success) {
          // 创建AST结果
          const astResult = {
            index: counter.value + 1,
            formula: cleanFormulaForDisplay(formulas[0]),
            astData: astData
          }
          astResults.value.push(astResult)
          console.log('TruthTableInterface: AST数据已添加到astResults')
        } else {
          console.warn('TruthTableInterface: AST生成失败:', astData.error)
        }
      } catch (error) {
        console.error('生成AST图片失败:', error)
        ElMessage.error(`生成抽象语法树失败: ${error.message}`)
      }
    } else {
      console.log('TruthTableInterface: 未勾选AST选项，跳过AST生成')
    }

    // 添加一个延迟，确保DOM准备就绪和MathRenderer初始化完成
    await new Promise(resolve => setTimeout(resolve, 200))

    console.log('TruthTableInterface: 准备添加结果到数组，当前results长度:', results.value.length)
    results.value.push(result)
    console.log('TruthTableInterface: 结果已添加，新results长度:', results.value.length)

    // 添加成功反馈
    feedback.value.push({
      formula: cleanFormulaForDisplay(formulas[0]),
      type: 'success',
      message: '公式解析成功'
    })

    // 收集所有数据，一次性发送到主界面
    const completeResult = {
      formula: cleanFormulaForDisplay(formulas[0]),
      tableData: tableData,
      formulaType: checkFormulaType.value && tableData.formulaType ? translateFormulaType(tableData.formulaType) : null,
      index: counter.value,
      syntaxData: null  // 默认为空
    }

    // 如果生成了严格形式公式，添加到结果中
    if (showStrictForm.value && syntaxResults.value.length > 0) {
      completeResult.syntaxData = syntaxResults.value[syntaxResults.value.length - 1].syntaxData
    }

    // 一次性发送完整结果到主界面
    emit('formula-calculated', completeResult)

    ElMessage.success(`已完成 ${formulas.length} 个公式的真值表计算`)
  } catch (error) {
    console.error('TruthTableInterface: 计算真值表时发生错误:', error)
    feedback.value.push({
      formula: cleanFormulaForDisplay(formulas[0]),
      type: 'error',
      message: `处理公式时发生错误: ${error.message}`
    })
  }
}

const generateFormula = () => {
  // 模拟生成随机公式 - 使用与例题相同的双反斜杠格式
  const randomFormulas = [
    'p\\wedge q',
    'p\\vee q',
    '\\neg p\\rightarrow q',
    'p\\leftrightarrow q',
    '(p\\vee q)\\wedge r',
    'p\\rightarrow(q\\wedge r)',
    'p\\wedge\\neg q',
    '(p\\rightarrow q)\\vee r'
  ]

  const randomFormula = randomFormulas[Math.floor(Math.random() * randomFormulas.length)]

  // 清除之前的结果（先清除状态，再设置新公式）
  astResults.value = [] // 清除之前的AST结果
  syntaxResults.value = [] // 清除之前的严格形式公式结果
  results.value = [] // 清除之前的真值表结果
  feedback.value = [] // 清除之前的反馈
  counter.value = 0   // 重置计数器

  // 设置新公式，确保格式正确
  formulaInput.value = cleanFormulaForDisplay(randomFormula)

  console.log('generateFormula: 生成公式:', formulaInput.value)
  console.log('generateFormula: 原始公式:', randomFormula)

  ElMessage.info('已生成随机公式')
}

const removeFormula = () => {
  formulaInput.value = ''
  ElMessage.info('已清空公式输入')
}

const checkFormula = () => {
  if (!formulaInput.value.trim()) {
    ElMessage.warning('请先输入公式')
    return
  }

  feedback.value = []
  const formulas = formulaInput.value.split(';')
    .map(f => f.trim())
    .filter(f => f)
    .map(f => normalizeFormulaFormat(f))  // 统一转换为单反斜杠格式

  formulas.forEach(formula => {
    if (isValidFormula(formula)) {
      feedback.value.push({
        formula: cleanFormulaForDisplay(formula),
        type: 'success',
        message: '公式格式正确'
      })
    } else {
      feedback.value.push({
        formula: cleanFormulaForDisplay(formula),
        type: 'error',
        message: '公式格式不正确'
      })
    }
  })
}

const clearResults = () => {
  results.value = []
  syntaxResults.value = []
  astResults.value = []
  feedback.value = []
  counter.value = 0
  ElMessage.info('已清除所有结果')
}

const testSyntaxAPI = async () => {
  console.log('=== 开始测试语法API ===')
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
    // 清除之前的结果（先清除状态，再设置新公式）
    astResults.value = [] // 清除之前的AST结果
    syntaxResults.value = [] // 清除之前的严格形式公式结果
    results.value = [] // 清除之前的真值表结果
    feedback.value = [] // 清除之前的反馈
    counter.value = 0   // 重置计数器

    // 设置新公式，确保格式正确
    formulaInput.value = cleanFormulaForDisplay(exampleFormulas[exampleKey])

    console.log('loadExample: 加载示例:', formulaInput.value)
    console.log('loadExample: 原始公式:', exampleFormulas[exampleKey])

    ElMessage.info(`已加载示例：${exampleKey}`)
  }
}

// Real backend API call
// ==================================== BACKEND API CALL ====================================
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

const generateTruthTable = async (formulas, detailed = false, checkType = true) => {
  try {
    // 使用绝对路径，确保移动端也能正确访问
    const baseUrl = window.location.origin
    const response = await fetch(`${baseUrl}/api/truth-table/generate`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        formulas: formulas,
        detailed: detailed,
        checkType: checkType
      })
    })

    if (!response.ok) {
      throw new Error('Failed to generate truth table')
    }

    const data = await response.json()

    if (data.errorMessage) {
      throw new Error(data.errorMessage)
    }

    return {
      latexTable: data.latexTable,
      formulaType: data.formulaType,
      headers: [], // 保持兼容性，但不再使用
      rows: [] // 保持兼容性，但不再使用
    }
  } catch (error) {
    console.error('Error generating truth table:', error)
    throw error
  }
}

// 生成抽象语法树图片（用于startCalculation流程）
const generateASTImageForStartCalculation = async (formula) => {
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

// 生成抽象语法树图片（独立按钮调用）
const generateASTImage = async () => {
  if (!formulaInput.value.trim()) {
    ElMessage.warning('请先输入公式')
    return
  }

  astGenerating.value = true

  // 获取第一个公式进行AST生成
  const formula = normalizeFormulaFormat(formulaInput.value.split(';')[0].trim())

  try {
    console.log('开始生成AST图片，公式:', formula)

    const astData = await generateASTImageForStartCalculation(formula)
    console.log('获取到AST数据:', astData)

    if (astData.success) {
      // 创建AST结果
      const astResult = {
        index: counter.value + 1,
        formula: cleanFormulaForDisplay(formula),
        astData: astData
      }

      // 添加到AST结果数组
      astResults.value.push(astResult)

      ElMessage.success('抽象语法树生成成功')
    } else {
      // 生成失败，添加错误结果
      const astResult = {
        index: counter.value + 1,
        formula: cleanFormulaForDisplay(formula),
        astData: astData
      }

      astResults.value.push(astResult)
      ElMessage.error('生成抽象语法树失败: ' + (astData.error || '未知错误'))
    }
  } catch (error) {
    console.error('生成AST图片失败:', error)

    // 添加错误结果
    const astResult = {
      index: counter.value + 1,
      formula: cleanFormulaForDisplay(formula),
      astData: {
        imageUrl: null,
        dotPath: null,
        error: error.message || '网络错误'
      }
    }

    astResults.value.push(astResult)
    ElMessage.error('生成抽象语法树失败: ' + error.message)
  } finally {
    astGenerating.value = false
  }
}

// 图片加载成功处理
const handleImageLoad = (event) => {
  console.log('AST图片加载成功:', event.target.src)
}

// 图片加载失败处理
const handleImageError = (event) => {
  console.error('AST图片加载失败:', event.target.src)
  ElMessage.error('抽象语法树图片加载失败')
}
// =========================================================================================


// 翻译公式类型
const translateFormulaType = (type) => {
  switch (type) {
    case 'tautology': return '重言式'
    case 'contradiction': return '矛盾式'
    case 'contingency': return '可满足式'
    default: return type
  }
}

// 统一处理公式格式，将双反斜杠转换为单反斜杠
const normalizeFormulaFormat = (formula) => {
  return formula.replace(/\\\\([a-zA-Z]+)/g, '\\$1')  // 将双反斜杠转换为单反斜杠
}

// 清理公式中的反斜杠，用于显示
const cleanFormulaForDisplay = (formula) => {
  return normalizeFormulaFormat(formula)
}

// 辅助函数
const isValidFormula = (formula) => {
  // 简单的公式验证逻辑（实际应该调用后端API）
  const basicPattern = /^[pqrst\s\\\\neg\\\\wedge\\\\vee\\\\rightarrow\\\\leftrightarrow\\(\\)]+$/
  return basicPattern.test(formula) && formula.length > 0
}

const getFormulaTypeTag = (type) => {
  switch (type) {
    case '重言式': return 'success'
    case '可满足式': return 'warning'
    case '矛盾式': return 'danger'
    default: return 'info'
  }
}
</script>

<style scoped>
.truth-table-interface {
  max-height: 80vh;
  overflow-y: auto;
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

.truth-table {
  margin: 1rem 0;
  background: white;
  padding: 1rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.formula-type {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-top: 1rem;
}

.type-formula {
  font-weight: bold;
}

.type-tag {
  margin-left: 0.5rem;
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

/* HTML表格样式 */
.truth-table-vue {
  margin: 1rem 0;
  overflow: auto; /* 添加滚动支持 */
  background: white;
  border-radius: 8px;
  border: 2px solid #e9ecef;
  padding: 1rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.truth-table-html {
  width: auto; /* 改为auto以支持滚动 */
  min-width: 100%; /* 确保最小宽度为容器宽度 */
  border-collapse: collapse;
  border: 2px solid #dee2e6;
  border-radius: 8px;
  overflow: hidden;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  white-space: nowrap; /* 防止内容换行 */
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

/* 滚动条样式 */
.truth-table-interface::-webkit-scrollbar {
  width: 8px;
}

.truth-table-interface::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.truth-table-interface::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.truth-table-interface::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}

/* MathRenderer 容器样式 */
.truth-table-wrapper {
  margin: 1rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 2px solid #e9ecef;
  min-height: 60px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  width: 100%;
  box-sizing: border-box;
  overflow: auto; /* 改为auto，支持滚动 */
  position: relative;
}

.truth-table-wrapper .math-renderer {
  width: 100%;
  min-width: fit-content; /* 确保内容不会被压缩 */
  display: flex;
  justify-content: flex-start; /* 改为左对齐，保持表格完整显示 */
  align-items: center;
}

/* 表格响应式样式 */
.truth-table-wrapper .math-content {
  font-size: 0.9em;
  line-height: 1.3;
  min-width: fit-content; /* 确保内容最小宽度 */
  display: block;
  width: auto;
}

/* 让表格自适应容器大小 */
.truth-table-wrapper :deep(.mtable) {
  width: auto;
  min-width: fit-content;
  height: auto;
}

.truth-table-wrapper :deep(.katex-display) {
  margin: 0 !important;
  white-space: nowrap; /* 防止表格换行 */
}

/* 移动端响应式调整 */
@media (max-width: 768px) {
  .truth-table-wrapper {
    padding: 0.5rem;
    margin: 0.5rem 0;
  }

  .truth-table-wrapper .math-content {
    font-size: 0.8em; /* 移动端减小字体 */
  }

  /* 确保滚动条在移动端可见 */
  .truth-table-wrapper::-webkit-scrollbar {
    height: 6px;
    width: 6px;
  }

  .truth-table-wrapper::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 3px;
  }

  .truth-table-wrapper::-webkit-scrollbar-thumb {
    background: #c1c1c1;
    border-radius: 3px;
  }

  .truth-table-wrapper::-webkit-scrollbar-thumb:hover {
    background: #a1a1a1;
  }
}

/* 小屏幕设备进一步优化 */
@media (max-width: 480px) {
  .truth-table-wrapper {
    padding: 0.25rem;
  }

  .truth-table-wrapper .math-content {
    font-size: 0.7em;
  }
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

/* AST图片响应式样式 */
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