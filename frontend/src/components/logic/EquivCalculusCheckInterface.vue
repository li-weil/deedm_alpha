<template>
  <div class="equiv-calculus-check-interface">
    <!-- 按钮操作区域 -->
    <div class="button-section">
      <!-- 第一行按钮：演算、移除、合法性检查、清除结果 -->
      <el-row :gutter="20" class="button-row">
        <el-col :span="4">
          <el-button type="primary" @click="performCalculus">
            <el-icon><Check /></el-icon>
            演算(Y)
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
          <el-button size="small" @click="loadExample('problem2_12_1')">例题2.12(1)</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem2_12_2')">例题2.12(2)</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem2_12_3')">例题2.12(3)</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem2_12_4')">例题2.12(4)</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem2_14_1')">例题2.14(1)</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem2_14_2')">例题2.14(2)</el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 公式输入区域 -->
    <div class="formula-input-section">
      <el-divider content-position="left">等值演算过程输入</el-divider>
      <div class="input-tip">
        <el-alert
          title="输入格式说明：每一行为一个演算步骤，格式为：& 公式 & 注释"
          type="info"
          :closable="false"
          show-icon
        />
      </div>
      <div class="formula-input-container">
        <el-input
          v-model="currentFormula"
          type="textarea"
          :rows="12"
          placeholder="请输入等值演算过程，每行一个步骤，格式如下：
& (p\rightarrow q)\wedge(p\rightarrow r)  &
\equiv & (\neg p\vee q)\wedge(\neg p\vee r)  & 蕴含析取等值式
\equiv & \neg p\vee(q\wedge r)  & 分配律
\equiv & p\rightarrow(q\wedge r)  & 蕴含析取等值式"
          class="formula-textarea"
          @keyup.enter.ctrl="performCalculus"
        />
      </div>
    </div>



    <!-- 结果显示区域 -->
    <div v-if="calculusResult" class="results-section">
      <el-divider content-position="left">等值演算检查结果</el-divider>
      <div class="results-content">
        <div class="result-item">
          <div class="result-title">
          </div>

          <!-- 演算步骤显示 -->
          <div class="calculus-steps">
            <div v-for="(step, index) in calculusResult.steps" :key="index" class="calculus-step">
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
            <div v-if="calculusResult.valid" class="valid-result">
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
                :description="calculusResult.errorMessage"
                :closable="false"
                show-icon
              />

              <!-- 反例信息 -->
              <div v-if="calculusResult.counterExample" class="counter-example">
                <h4 class="counter-title">反例：</h4>
                <p>在真值赋值 {{ calculusResult.counterExample }} 下，以下公式不是重言式：</p>
                <div class="checking-formula">
                  <math-renderer
                    :formula="calculusResult.checkingFormula"
                    :type="'katex'"
                    :display-mode="true"
                  />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Check,
  Delete,
  WarningFilled,
  RefreshRight,
  Close
} from '@element-plus/icons-vue'
import MathRenderer from '../common/MathRenderer.vue'

// 响应式数据
const emit = defineEmits(['close', 'result'])
const currentFormula = ref('')
const calculusResult = ref(null)
const stepCounter = ref(1)

// 示例题库
const examples = {
  problem2_12_1: {
    content: '& (p\\rightarrow q)\\wedge(p\\rightarrow r)  &\n \\equiv & (\\neg p\\vee q)\\wedge(\\neg p\\vee r)  & 蕴含析取等值式\n \\equiv & \\neg p\\vee(q\\wedge r)  & 分配律 \n \\equiv & p\\rightarrow(q\\wedge r)  & 蕴含析取等值式'
  },
  problem2_12_2: {
    content: '& (p\\rightarrow q)\\vee (p\\rightarrow r)  \n' +
    '\\equiv & (\\neg p\\vee q)\\vee(\\neg p\\vee r)  & 蕴含析取等值式 \n' +
    ' \\equiv &  (\\neg p\\vee \\neg p)\\vee (q\\vee r)   & 幂等律、结合律\n' +
    ' \\equiv &  \\neg p\\vee (q\\vee r)   & 幂等律\n' +
    ' \\equiv &  p\\rightarrow (q\\vee r)  & 蕴含析取等值式'
  },
  problem2_12_3: {
    content: '& (p\\rightarrow r)\\wedge(q\\rightarrow r) \n' +
    ' \\equiv &  (\\neg p\\vee r)\\wedge(\\neg q\\vee r)  & 蕴含析取等值式\n' +
    ' \\equiv &  (\\neg p\\wedge\\neg q)\\vee r  & 分配律\n' +
    ' \\equiv &  \\neg (p\\vee q)\\vee r  & 德摩根律\n' +
    ' \\equiv &  (p\\vee q)\\rightarrow r  & 蕴含析取等值式'
  },
  problem2_12_4: {
    content: '& (p\\rightarrow r)\\vee (q\\rightarrow r) & \n' +
    ' \\equiv &  (\\neg p\\vee r)\\vee (\\neg q\\vee r)  & 蕴含析取等值式\n' +
    ' \\equiv &  (\\neg p\\vee \\neg q)\\vee(r\\vee r)  & 交换律、结合律\n' +
    ' \\equiv &  (\\neg p\\vee \\neg q)\\vee r  & 幂等律\n' +
    ' \\equiv &  \\neg(p\\wedge q)\\vee r & 德摩根律\n' +
    ' \\equiv &  (p\\wedge q)\\rightarrow r  & 蕴含析取等值式'
  },
  problem2_14_1: {
    content: '& \\neg (p\\rightarrow q)\\wedge((\\neg q)\\leftrightarrow r) &\n' +
    '\\equiv & \\neg(p\\rightarrow q)\\wedge((\\neg q\\rightarrow r)\\wedge (r\\rightarrow\\neg q))  & 双条件等值式\n' +
    '\\equiv & \\neg(\\neg p\\vee q)\\wedge((\\neg (\\neg q)\\vee r)\\wedge (\\neg r\\vee \\neg q))  & 蕴含析取等值式\n' +
    '\\equiv & (\\neg (\\neg p)\\wedge \\neg q)\\wedge(\\neg (\\neg q)\\vee r)\\wedge (\\neg r\\vee \\neg q)  & 德摩根律\n' +
    '\\equiv & (p\\wedge \\neg q)\\wedge(q\\vee r)\\wedge (\\neg r\\vee \\neg q)  & 双重否定律'
  },
  problem2_14_2: {
    content: 'p\\wedge\\neg q\\wedge(q\\vee r)\\wedge(\\neg r\\vee\\neg q) &\n' +
    '\\equiv &  ((p\\wedge\\neg q\\wedge q)\\vee(p\\wedge\\neg q\\wedge r))\\wedge(\\neg r\\vee\\neg q)  & 分配律\n' +
    '\\equiv &  (p\\wedge\\neg q\\wedge r)\\wedge(\\neg r\\vee\\neg q)  & 矛盾式、同一律\n' +
    '\\equiv &  (p\\wedge\\neg q\\wedge r\\wedge\\neg r)\\vee(p\\wedge\\neg q\\wedge r\\wedge\\neg q)  & 分配律\n' +
    '\\equiv &  (p\\wedge\\neg q\\wedge r\\wedge\\neg q)  & 矛盾式、同一律\n' +
    '\\equiv &  p\\wedge\\neg q\\wedge r  & 幂等律、结合律'
  }
}

// 计算属性：预览步骤
const previewSteps = computed(() => {
  if (!currentFormula.value.trim()) return []

  const lines = currentFormula.value.split('\n').filter(line => line.trim())
  const steps = []

  for (const line of lines) {
    const parts = line.split('&').map(part => part.trim()).filter(part => part)
    if (parts.length === 0) continue

    if (parts[0] !== '\\equiv' && parts[0] !== '') {
      // 第一个公式（不包含equiv符号的行，或者第一个cell为空但第二个cell有内容）
      if (parts[0] !== '\\equiv') {
        steps.push({
          formula: parts[0],
          comment: parts[1] || ''
        })
      }
    } else if (parts[0] === '\\equiv' && parts.length > 1) {
      // 后续公式（包含equiv符号的行）
      steps.push({
        formula: parts[1],
        comment: parts[2] || ''
      })
    }
  }

  return steps
})

// API 调用函数
const callBackendApi = async (endpoint, options = {}) => {
  try {
    const baseUrl = window.location.origin
    const response = await fetch(`${baseUrl}/api${endpoint}`, {
      method: 'POST',
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

// 移除公式
const removeFormula = () => {
  currentFormula.value = ''
  calculusResult.value = null
  ElMessage.info('已移除所有内容')
}

// 合法性检查
const checkFormula = async () => {
  if (!currentFormula.value.trim()) {
    ElMessage.warning('请先输入等值演算过程')
    return
  }

  try {
    const result = await callBackendApi('/equiv-calculus/check', {
      method: 'POST',
      body: JSON.stringify({
        content: currentFormula.value
      })
    })

    if (result.valid) {
      ElMessage.success('输入格式正确')
    } else {
      ElMessage.error('输入格式错误: ' + result.message)
    }
  } catch (error) {
    ElMessage.error('检查时发生错误: ' + error.message)
  }
}

// 执行演算
const performCalculus = async () => {
  if (!currentFormula.value.trim()) {
    ElMessage.warning('请先输入等值演算过程')
    return
  }

  try {
    const result = await callBackendApi('/equiv-calculus/perform', {
      method: 'POST',
      body: JSON.stringify({
        content: currentFormula.value,
        stepNumber: stepCounter.value
      })
    })

    // 保存演算结果用于显示
    calculusResult.value = result

    // 返回结果给主界面
    emit('result', {
      type: 'equiv-calculus-check',
      data: result
    })

    // 增加步骤计数器
    stepCounter.value++

    if (result.valid) {
      ElMessage.success(result.message || '演算检查完成')
    } else {
      ElMessage.warning(result.message || '演算检查发现问题')
    }
  } catch (error) {
    ElMessage.error('演算检查时发生错误: ' + error.message)
  }
}

// 加载示例
const loadExample = (exampleKey) => {
  const example = examples[exampleKey]
  if (example) {
    currentFormula.value = example.content
    ElMessage.success('已加载示例: ' + exampleKey)
  }
}

// 清除结果
const clearResults = () => {
  currentFormula.value = ''
  calculusResult.value = null
  stepCounter.value = 1
  ElMessage.info('已清除所有结果')
}

// 关闭界面
const closeInterface = () => {
  emit('close')
}
</script>

<style scoped>
/* .equiv-calculus-check-interface {
  max-height: 80vh;
} */
.equiv-calculus-check-interface  {
  display: flex;
  flex-direction: column;
  width: 100%;
}

/* 在大屏幕上完全自适应，无最大宽度限制 */
@media (min-width: 1200px) {
  .equiv-calculus-check-interface  {
    width: 100%;
    max-width: none;
  }
}

/* 在中等屏幕上设置合理的最大宽度 */
@media (max-width: 1199px) and (min-width: 900px) {
  .equiv-calculus-check-interface  {
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
  }
}

/* 在小屏幕上固定宽度，支持水平滚动 */
@media (max-width: 899px) {
  .equiv-calculus-check-interface  {
    width: 900px;
    min-width: 900px;
    max-width: 900px;
    margin: 0 auto;
  }
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
  margin-bottom: 20px;
}

.input-tip {
  margin-bottom: 15px;
}

.formula-input-container {
  position: relative;
}

.formula-textarea {
  font-family: 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.6;
}

.formula-actions {
  margin-top: 10px;
  text-align: right;
}

.preview-section {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.preview-container {
  background: white;
  padding: 15px;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.preview-step {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.preview-step:last-child {
  margin-bottom: 0;
}

.step-formula {
  display: flex;
  align-items: center;
  gap: 10px;
}

.formula-renderer {
  font-size: 16px;
}

.step-comment {
  font-style: italic;
  color: #666;
  font-size: 14px;
}

.equiv-symbol {
  margin: 0 15px;
  font-size: 18px;
  font-weight: bold;
  color: #409eff;
}

/* 结果显示样式 */
.results-section {
  margin: 2rem 0;
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

.result-title {
  margin-bottom: 1rem;
  font-size: 1.1rem;
  color: #2c3e50;
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

.step-formula {
  background: #f8f9ff;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  border: 1px solid #d0e3ff;
  margin-right: 10px;
}

.step-comment {
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

:deep(.el-alert) {
  margin-bottom: 1rem;
}

:deep(.el-form-item__label) {
  font-weight: bold;
  color: #303133;
}
</style>