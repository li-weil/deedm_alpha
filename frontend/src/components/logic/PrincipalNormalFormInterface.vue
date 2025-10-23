<template>
  <div class="principal-normal-form-interface">
    <!-- 按钮操作区域 -->
    <div class="button-section">
      <!-- 第一行按钮：开始计算、生成公式、移除公式、公式合法性检查 -->
      <el-row :gutter="20" class="button-row">
        <el-col :span="4">
          <el-button type="primary" @click="startCalculation">
            <el-icon><Check /></el-icon>
            开始计算(Y)
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
          <el-button size="small" @click="loadExample('problem2_14_1')">例题2.14(1)</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem2_14_2')">例题2.14(2)</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem2_18')">例题2.18</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem2_30')">例题2.30</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem2_32')">例题2.32</el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 计算范式设置信息 -->
    <div class="settings-section">
      <el-divider content-position="left">计算范式设置信息</el-divider>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="变量集(S):">
            <el-input
              v-model="variableSet"
              placeholder="请输入变量，如：p, q, r"
              class="variable-input"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="这些式子之间的逻辑关系是：">
            <el-radio-group v-model="logicalRelation">
              <el-radio label="or">析取关系</el-radio>
              <el-radio label="and">合取关系</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-checkbox-group v-model="normalFormTypes">
            <el-checkbox label="dnf">析取范式</el-checkbox>
            <el-checkbox label="cnf">合取范式</el-checkbox>
            <el-checkbox label="pnf">扩展主范式</el-checkbox>
          </el-checkbox-group>
        </el-col>
        <el-col :span="12">
          <el-checkbox-group v-model="calculationMethods">
            <el-checkbox label="equiv">使用等值演算法</el-checkbox>
            <el-checkbox label="table">使用真值表法</el-checkbox>
            <el-checkbox label="detailed">显示详细信息</el-checkbox>
          </el-checkbox-group>
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
        placeholder="请输入LaTeX格式的逻辑公式，多个公式用换行分隔..."
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

    <!-- 结果显示区域 -->
    <div v-if="results.length > 0" class="results-section">
      <el-divider content-position="left">主范式结果</el-divider>
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

          <!-- CNF 结果 -->
          <div v-if="result.cnfResult" class="normal-form-result">
            <h4 class="result-title">合取范式结果：</h4>
            <div class="calculation-steps" v-if="calculationMethods.includes('detailed') && result.cnfSteps">
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

            <!-- PCNF 扩展步骤 -->
            <div v-if="result.cnfExpansionSteps && result.cnfExpansionSteps.length > 0" class="expansion-steps">
              <h4 class="expansion-title">主合取范式扩展步骤：</h4>
              <div v-for="(step, stepIndex) in result.cnfExpansionSteps" :key="'cnf-expansion-' + stepIndex" class="expansion-step">
                <div class="expansion-formula">
                  <math-renderer
                    :formula="step.formula"
                    :type="'katex'"
                    :display-mode="false"
                    class="step-formula"
                  />
                  <span v-if="step.formulaCode" class="formula-code">[{{ step.formulaCode }}]</span>
                </div>
                <div class="expansion-description">
                  <el-text type="primary" class="expansion-text">{{ step.expansionDescription }}</el-text>
                </div>
                <div v-if="step.resultCodes" class="expansion-result">
                  <el-text type="success" class="result-text">{{ step.resultDescription }}：</el-text>
                  <math-renderer
                    :formula="step.resultCodes"
                    :type="'katex'"
                    :display-mode="false"
                    class="result-codes"
                  />
                </div>
              </div>
            </div>

            <!-- PCNF 结果 -->
            <div v-if="result.cnfResult.pcnf && normalFormTypes.includes('pnf')" class="pnf-result">
              <h5 class="pnf-title">最终的主合取范式是：</h5>
              <math-renderer
                :formula="result.cnfResult.pcnf"
                :type="'katex'"
                :display-mode="false"
              />
              <h5 class="pnf-title">相应的主析取范式是：</h5>
              <math-renderer
                :formula="result.cnfResult.pdnf"
                :type="'katex'"
                :display-mode="false"
              />
            </div>
          </div>

          <!-- DNF 结果 -->
          <div v-if="result.dnfResult" class="normal-form-result">
            <h4 class="result-title">析取范式结果：</h4>
            <div class="calculation-steps" v-if="calculationMethods.includes('detailed') && result.dnfSteps">
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

            <!-- PDNF 扩展步骤 -->
            <div v-if="result.dnfExpansionSteps && result.dnfExpansionSteps.length > 0" class="expansion-steps">
              <h4 class="expansion-title">主析取范式扩展步骤：</h4>
              <div v-for="(step, stepIndex) in result.dnfExpansionSteps" :key="'dnf-expansion-' + stepIndex" class="expansion-step">
                <div class="expansion-formula">
                  <math-renderer
                    :formula="step.formula"
                    :type="'katex'"
                    :display-mode="false"
                    class="step-formula"
                  />
                  <span v-if="step.formulaCode" class="formula-code">[{{ step.formulaCode }}]</span>
                </div>
                <div class="expansion-description">
                  <el-text type="primary" class="expansion-text">{{ step.expansionDescription }}</el-text>
                </div>
                <div v-if="step.resultCodes" class="expansion-result">
                  <el-text type="success" class="result-text">{{ step.resultDescription }}：</el-text>
                  <math-renderer
                    :formula="step.resultCodes"
                    :type="'katex'"
                    :display-mode="false"
                    class="result-codes"
                  />
                </div>
              </div>
            </div>

            <!-- PDNF 结果 -->
            <div v-if="result.dnfResult.pdnf && normalFormTypes.includes('pnf')" class="pnf-result">
              <h5 class="pnf-title">最终的主析取范式是：</h5>
              <math-renderer
                :formula="result.dnfResult.pdnf"
                :type="'katex'"
                :display-mode="false"
              />
              <h5 class="pnf-title">相应的主合取范式是：</h5>
              <math-renderer
                :formula="result.dnfResult.pcnf"
                :type="'katex'"
                :display-mode="false"
              />
            </div>
          </div>

          <!-- 真值表结果 -->
          <div v-if="result.truthTable" class="truth-table-result">
            <h4 class="result-title">真值表计算结果：</h4>
            <div class="truth-table-container">
              <math-renderer
                :key="'table-' + result.index + '-' + Date.now()"
                :formula="result.truthTable"
                :type="'katex'"
                :display-mode="true"
                class="truth-table-content"
              />
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
  Close
} from '@element-plus/icons-vue'
import MathRenderer from '@/components/common/MathRenderer.vue'

// 响应式数据
const formulaInput = ref('')
const variableSet = ref('')
const logicalRelation = ref('or')
const normalFormTypes = ref(['cnf', 'pnf'])
const calculationMethods = ref(['equiv', 'detailed'])
const feedback = ref([])
const results = ref([])
const counter = ref(0)

// 示例公式映射
const exampleFormulas = {
  problem2_14_1: '\\neg(p\\rightarrow q)\\wedge(\\neg q\\leftrightarrow r)',
  problem2_14_2: 'p\\wedge\\neg q\\wedge r',
  problem2_18: 'p\\rightarrow q\\nr\\rightarrow s',
  problem2_30: 'p\\rightarrow q\\vee r\\n\\neg(r\\wedge s)\\n(q\\wedge\\neg t)\\vee(t\\wedge\\neg q)\\n\\neg s\\rightarrow\\neg p',
  problem2_32: 'p\\rightarrow q\\n\\neg(q\\wedge r)\\n\\neg(\\neg r\\wedge\\neg s)\\ns\\rightarrow\\neg q'
}

const exampleVariableSets = {
  problem2_14_1: 'p, q, r',
  problem2_14_2: 'p, q, r',
  problem2_18: 'p, q, r, s',
  problem2_30: 'p, q, r, s, t',
  problem2_32: 'p, q, r, s'
}

const exampleLogicalRelations = {
  problem2_14_1: 'and',
  problem2_14_2: 'or',
  problem2_18: 'and',
  problem2_30: 'and',
  problem2_32: 'and'
}

// 事件处理函数
const emit = defineEmits(['close', 'formula-calculated'])

const startCalculation = async () => {
  if (!formulaInput.value.trim()) {
    ElMessage.warning('请先输入公式')
    return
  }

  if (!variableSet.value.trim()) {
    ElMessage.warning('请先输入变量集')
    return
  }

  if (normalFormTypes.value.length === 0) {
    ElMessage.warning('请至少选择一种范式类型')
    return
  }

  // 清空之前的反馈和结果
  feedback.value = []
  results.value = []

  // 分割多个公式
  const formulas = formulaInput.value.split('\n')
    .map(f => f.trim())
    .filter(f => f)
    .map(f => normalizeFormulaFormat(f))

  try {
    console.log('PrincipalNormalFormInterface: 开始计算主范式，公式:', formulas)

    // 根据是否需要扩展步骤选择不同的端点
    const endpoint = normalFormTypes.value.includes('pnf') && calculationMethods.value.includes('detailed')
      ? '/principal-normal-form/calculate-with-expansion'
      : '/principal-normal-form/calculate'

    // 调用后端API计算主范式
    const result = await callBackendApi(endpoint, {
      method: 'POST',
      body: JSON.stringify({
        formulas: formulas,
        variableSet: variableSet.value,
        logicalRelation: logicalRelation.value,
        normalFormTypes: normalFormTypes.value,
        calculationMethods: calculationMethods.value
      })
    })

    if (result.success) {
      // 转换后端结果格式为前端格式
      const frontendResult = convertBackendResultToFrontend(result)
      results.value.push(frontendResult)

      // 添加成功反馈
      feedback.value.push({
        formula: cleanFormulaForDisplay(formulas[0]),
        type: 'success',
        message: '主范式计算成功'
      })

      // 发送结果到主界面
      emit('formula-calculated', frontendResult)

      ElMessage.success('主范式计算完成')
    } else {
      throw new Error(result.errorMessage || '计算失败')
    }

  } catch (error) {
    console.error('PrincipalNormalFormInterface: 计算主范式时发生错误:', error)
    feedback.value.push({
      formula: cleanFormulaForDisplay(formulas[0]),
      type: 'error',
      message: `计算主范式时发生错误: ${error.message}`
    })
  }
}

const generateFormula = async () => {
  try {
    // 清除之前的结果
    results.value = []
    feedback.value = []
    counter.value = 0

    if (!variableSet.value.trim()) {
      ElMessage.warning('请先输入变量集')
      return
    }

    // 调用后端API生成随机公式
    const result = await callBackendApi('/principal-normal-form/generate', {
      method: 'POST',
      body: JSON.stringify({
        variableSet: variableSet.value,
        logicalRelation: logicalRelation.value,
        count: 4
      })
    })

    if (result.success) {
      formulaInput.value = cleanFormulaForDisplay(result.formula)
      ElMessage.success(result.message || '已生成随机公式')
    } else {
      throw new Error(result.error || '生成公式失败')
    }

  } catch (error) {
    console.error('生成随机公式失败:', error)
    ElMessage.error('生成公式失败: ' + error.message)
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

  if (!variableSet.value.trim()) {
    ElMessage.warning('请先输入变量集')
    return
  }

  feedback.value = []
  const formulas = formulaInput.value.split('\n')
    .map(f => f.trim())
    .filter(f => f)
    .map(f => normalizeFormulaFormat(f))

  try {
    // 调用后端API检查公式合法性
    const result = await callBackendApi('/principal-normal-form/check', {
      method: 'POST',
      body: JSON.stringify({
        formulas: formulas,
        variableSet: variableSet.value
      })
    })

    if (result.valid) {
      // 显示所有公式的检查结果
      if (result.formulaResults) {
        result.formulaResults.forEach((formulaResult, index) => {
          feedback.value.push({
            formula: cleanFormulaForDisplay(formulaResult.formula),
            type: formulaResult.valid ? 'success' : 'error',
            message: formulaResult.message || formulaResult.error || '检查完成'
          })
        })
      } else {
        // 后备方案：简单的成功消息
        formulas.forEach(formula => {
          feedback.value.push({
            formula: cleanFormulaForDisplay(formula),
            type: 'success',
            message: '公式格式正确'
          })
        })
      }

      ElMessage.success(result.message || '公式合法性检查完成')
    } else {
      // 显示错误信息
      if (result.errors && result.errors.length > 0) {
        result.errors.forEach(error => {
          feedback.value.push({
            formula: '',
            type: 'error',
            message: error
          })
        })
      }
      ElMessage.error('公式检查发现问题')
    }

  } catch (error) {
    console.error('检查公式失败:', error)
    ElMessage.error(`检查失败: ${error.message}`)
  }
}

const clearResults = () => {
  results.value = []
  feedback.value = []
  counter.value = 0
  ElMessage.info('已清除所有结果')
}

const closeInterface = () => {
  emit('close')
}

const loadExample = (exampleKey) => {
  if (exampleFormulas[exampleKey]) {
    // 清除之前的结果
    results.value = []
    feedback.value = []
    counter.value = 0

    // 设置示例公式和参数
    formulaInput.value = cleanFormulaForDisplay(exampleFormulas[exampleKey])
    variableSet.value = exampleVariableSets[exampleKey]
    logicalRelation.value = exampleLogicalRelations[exampleKey]

    ElMessage.info(`已加载示例：${exampleKey}`)
  }
}

// 创建模拟结果
const createMockResult = (formula) => {
  counter.value++
  const mockSteps = [
    { formula: formula, comment: '原始公式' },
    { formula: '(p\\wedge q)\\vee(\\neg p\\wedge r)', comment: '应用分配律' },
    { formula: '(p\\wedge q)\\vee(\\neg p\\wedge r)', comment: '简化结果' }
  ]

  return {
    index: counter.value,
    formula: cleanFormulaForDisplay(formula),
    cnfResult: normalFormTypes.value.includes('cnf') ? {
      finalFormula: '(p\\vee\\neg p)\\wedge(p\\vee r)\\wedge(q\\vee\\neg p)\\wedge(q\\vee r)',
      pcnf: normalFormTypes.value.includes('pnf') ? '(p\\vee q\\vee r)\\wedge(p\\vee q\\vee\\neg r)\\wedge(p\\vee\\neg q\\vee r)\\wedge(\\neg p\\vee q\\vee r)' : null,
      pdnf: normalFormTypes.value.includes('pnf') ? '(p\\wedge q\\wedge r)\\vee(p\\wedge q\\wedge\\neg r)\\vee(p\\wedge\\neg q\\wedge r)\\vee(\\neg p\\wedge q\\wedge r)' : null
    } : null,
    dnfResult: normalFormTypes.value.includes('dnf') ? {
      finalFormula: '(p\\wedge q)\\vee(\\neg p\\wedge r)',
      pdnf: normalFormTypes.value.includes('pnf') ? '(p\\wedge q\\wedge r)\\vee(p\\wedge q\\wedge\\neg r)\\vee(\\neg p\\wedge q\\wedge r)\\vee(\\neg p\\wedge\\neg q\\wedge r)' : null,
      pcnf: normalFormTypes.value.includes('pnf') ? '(p\\vee q\\vee r)\\wedge(p\\vee q\\vee\\neg r)\\wedge(p\\vee\\neg q\\vee r)\\wedge(\\neg p\\vee q\\vee r)' : null
    } : null,
    cnfSteps: normalFormTypes.value.includes('cnf') && calculationMethods.includes('detailed') ? mockSteps : null,
    dnfSteps: normalFormTypes.value.includes('dnf') && calculationMethods.includes('detailed') ? mockSteps : null,
    truthTable: calculationMethods.includes('table') ?
      '\\begin{array}{ccc|c}\np & q & r & (p\\wedge q)\\vee(\\neg p\\wedge r) \\\\\\hline\nT & T & T & T \\\\\nT & T & F & T \\\\\nT & F & T & T \\\\\nT & F & F & F \\\\\nF & T & T & F \\\\\nF & T & F & F \\\\\nF & F & T & T \\\\\nF & F & F & F \\\\\n\\end{array}' : null
  }
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

// 转换后端结果格式为前端格式
const convertBackendResultToFrontend = (backendResult) => {
  counter.value++

  return {
    index: counter.value,
    formula: cleanFormulaForDisplay(backendResult.originalFormula || backendResult.combinedFormula),
    cnfResult: backendResult.cnfResult ? {
      finalFormula: backendResult.cnfResult.finalFormula,
      pcnf: backendResult.cnfResult.pcnf,
      pdnf: backendResult.cnfResult.pdnf
    } : null,
    dnfResult: backendResult.dnfResult ? {
      finalFormula: backendResult.dnfResult.finalFormula,
      pdnf: backendResult.dnfResult.pdnf,
      pcnf: backendResult.dnfResult.pcnf
    } : null,
    cnfSteps: backendResult.cnfSteps ? backendResult.cnfSteps.map(step => ({
      formula: step.formula,
      comment: step.comment
    })) : null,
    dnfSteps: backendResult.dnfSteps ? backendResult.dnfSteps.map(step => ({
      formula: step.formula,
      comment: step.comment
    })) : null,
    cnfExpansionSteps: backendResult.cnfExpansionSteps ? backendResult.cnfExpansionSteps.map(step => ({
      formula: step.formula,
      formulaCode: step.formulaCode,
      resultCodes: step.resultCodes,
      expansionDescription: step.expansionDescription,
      resultDescription: step.resultDescription
    })) : null,
    dnfExpansionSteps: backendResult.dnfExpansionSteps ? backendResult.dnfExpansionSteps.map(step => ({
      formula: step.formula,
      formulaCode: step.formulaCode,
      resultCodes: step.resultCodes,
      expansionDescription: step.expansionDescription,
      resultDescription: step.resultDescription
    })) : null,
    truthTable: backendResult.truthTable
  }
}

// 统一处理公式格式，将双反斜杠转换为单反斜杠
const normalizeFormulaFormat = (formula) => {
  return formula.replace(/\\\\([a-zA-Z]+)/g, '\\$1')
}

// 清理公式中的反斜杠，用于显示
const cleanFormulaForDisplay = (formula) => {
  return normalizeFormulaFormat(formula)
}
</script>

<style scoped>
.principal-normal-form-interface {
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

.settings-section {
  margin: 2rem 0;
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.variable-input {
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

.step-formula {
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

.expansion-step {
  margin-bottom: 1.5rem;
  padding: 1rem;
  background: white;
  border-radius: 6px;
  border: 1px solid #d0e3ff;
  box-shadow: 0 2px 4px rgba(74, 144, 226, 0.1);
}

.expansion-step:last-child {
  margin-bottom: 0;
}

.expansion-formula {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 0.75rem;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.formula-code {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  background: #f1f3f4;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.9em;
  color: #5f6368;
  border: 1px solid #dadce0;
}

.expansion-description {
  text-align: center;
  margin-bottom: 0.5rem;
}

.expansion-text {
  font-weight: 600;
  font-size: 1em;
}

.expansion-result {
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.result-text {
  font-weight: 600;
  font-size: 0.95em;
}

.result-codes {
  background: #f8fff8;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  border: 1px solid #c3e6c3;
}

.truth-table-result {
  background: white;
  padding: 1rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  margin: 1rem 0;
}

.truth-table-container {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 1rem 0;
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.truth-table-content {
  width: 100%;
  max-width: 100%;
  overflow-x: auto;
}

/* 滚动条样式 */
.principal-normal-form-interface::-webkit-scrollbar {
  width: 8px;
}

.principal-normal-form-interface::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.principal-normal-form-interface::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.principal-normal-form-interface::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .settings-section {
    padding: 1rem;
  }

  .results-content {
    padding: 1rem;
  }

  .normal-form-result,
  .truth-table-result {
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
}

@media (max-width: 480px) {
  .settings-section {
    padding: 0.5rem;
  }

  .results-content {
    padding: 0.5rem;
  }

  .button-row .el-col {
    margin-bottom: 0.5rem;
  }
}
</style>