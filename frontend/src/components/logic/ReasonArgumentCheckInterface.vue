<template>
  <div class="reason-argument-check-interface">
    <!-- 按钮操作区域 -->
    <div class="button-section">
      <!-- 第一行按钮：演绎证明、移除、推理规则列表、合法性检查、清除结果 -->
      <el-row :gutter="20" class="button-row">
        <el-col :span="4">
          <el-button type="primary" @click="performReasoning">
            <el-icon><Check /></el-icon>
            演绎证明(Y)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="warning" @click="removeFormula">
            <el-icon><Delete /></el-icon>
            移除(V)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="info" @click="showRuleList">
            <el-icon><List /></el-icon>
            推理规则列表(N)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="success" @click="checkValidity">
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
        <el-col :span="3">
          <el-button size="small" @click="loadExample('problem2_21')">例题2.21</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('problem2_22')">例题2.22</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('problem2_23')">例题2.23</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('problem2_24')">例题2.24</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('problem2_25')">例题2.25</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('problem2_31')">例题2.31</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('problem2_32')">例题2.32</el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 要证明的推理前题集和结论公式输入区域 -->
    <div class="argument-input-section">
      <el-divider content-position="left">要证明的推理前题集和结论公式，前题公式的英文逗号分隔</el-divider>
      <el-form :model="argumentForm" label-width="140px">
        <el-form-item label="输入前题列表(P)：">
          <el-input
            v-model="argumentForm.premises"
            placeholder="请输入前题列表，用英文逗号分隔，例如：p→q, q→r, p"
            class="premise-input"
          />
        </el-form-item>
        <el-form-item label="输入结论式(S)：">
          <el-input
            v-model="argumentForm.consequent"
            placeholder="请输入结论公式，例如：r"
            class="consequent-input"
          />
        </el-form-item>
      </el-form>
    </div>

    <!-- 推理过程输入区域 -->
    <div class="reasoning-input-section">
      <el-divider content-position="left">验证推理每一公式为[(序号) & 公式  & (依据),(依据),注释，注释]</el-divider>
      <div class="input-tip">
        <el-alert
          title="输入格式说明：每一行为一个推理步骤，格式为：(序号) & 公式 & 依据规则"
          type="info"
          :closable="false"
          show-icon
        />
      </div>
      <div class="reasoning-input-container">
        <el-input
          v-model="currentReasoning"
          type="textarea"
          :rows="12"
          placeholder="请输入推理过程，每行一个步骤，格式如下：
(1) &  p & 前提 
(2) &  p\rightarrow q & 前提 
(3) &  q & (1), (2)假言推理 
(4) &  q\rightarrow r & 前提 
(5) &  r & (3), (4)假言推理"
          class="reasoning-textarea"
          @keyup.enter.ctrl="performReasoning"
        />
      </div>
    </div>


    <!-- 结果显示区域 -->
    <div v-if="reasoningResult" class="results-section">
      <el-divider content-position="left">推理有效性论证检查结果</el-divider>
      <div class="results-content">
        <div class="result-item">
          <div class="result-title">
            <strong>检查推理步骤</strong>
          </div>

          <!-- 推理步骤显示 -->
          <div class="reasoning-steps">
            <div v-if="reasoningResult.latexString" class="reasoning-overview">
              <h4>推理关系：</h4>
              <div class="reasoning-formula">
                <math-renderer
                  :formula="reasoningResult.latexString"
                  :type="'katex'"
                  :display-mode="true"
                  class="formula-renderer"
                />
              </div>
            </div>
            <div v-for="(step, index) in reasoningResult.steps" :key="index" class="reasoning-step">
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
          <div v-if="reasoningResult.checkSteps && reasoningResult.checkSteps.length > 0" class="check-process">
            <h4>检查过程：</h4>
            <div v-for="(step, index) in reasoningResult.checkSteps" :key="index" class="check-step">
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
            <div v-if="reasoningResult.valid" class="valid-result">
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
                :description="reasoningResult.message"
                :closable="false"
                show-icon
              />

              <!-- 反例信息 -->
              <div v-if="reasoningResult.counterExample" class="counter-example">
                <h4 class="counter-title">反例：</h4>
                <p>在真值赋值 {{ reasoningResult.counterExample }} 下，以下公式不是重言式：</p>
                <div class="checking-formula">
                  <math-renderer
                    :formula="reasoningResult.checkingFormula"
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

    <!-- 推理规则列表对话框 -->
    <el-dialog v-model="ruleDialogVisible" title="支持的推理逻辑规则名称列表" width="60%">
      <div class="rule-list">
        <el-table :data="ruleList" style="width: 100%">
          <el-table-column prop="chineseName" label="中文名称" width="200" />
          <el-table-column prop="englishName" label="英文名称" width="200" />
          <el-table-column prop="latexString" label="简化的LaTeX串" />
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Check,
  Delete,
  List,
  WarningFilled,
  RefreshRight,
  Close
} from '@element-plus/icons-vue'
import MathRenderer from '../common/MathRenderer.vue'

// 响应式数据
const emit = defineEmits(['close', 'result'])
const currentReasoning = ref('')
const reasoningResult = ref(null)
const stepCounter = ref(1)
const ruleDialogVisible = ref(false)

const argumentForm = ref({
  premises: '',
  consequent: ''
})

// 示例题库
const examples = {
  problem2_21: {
    reasoning: "(1) &  p & 前提 \n"+
              "(2) &  p\\rightarrow q & 前提 \n"+
              "(3) &  q & (1), (2)假言推理 \n"+
              "(4) &  q\\rightarrow r & 前提 \n"+
              "(5) &  r & (3), (4)假言推理",
    premises: "p\\rightarrow q, q\\rightarrow r, p",
    consequent: "r"
  },
  problem2_22: {
    reasoning: "(1) &  t\\wedge r  & 前提\n"+
              "(2) &  t  & (1)化简规则\n"+
              "(3) &  s\\leftrightarrow t  & 前提\n"+
              "(4) &  (s\\rightarrow t)\\wedge(t\\rightarrow s)  & (3)等值置换\n"+
              "(5) &  t\\rightarrow s  & (4)化简规则\n"+
              "(6) &  s  & (2),(5)假言推理\n"+
              "(7) &  q\\leftrightarrow s  & 前提\n"+
              "(8) &  (s\\rightarrow q)\\wedge(q\\rightarrow s)  & (7)等值置换\n"+
              "(9) &  s\\rightarrow q  & (8)化简规则\n"+
              "(10) &  q  & (6),(9)假言推理\n"+
              "(11) &  q\\rightarrow p  & 前提\n"+
              "(12) &  p  & (10),(11)假言推理\n"+
              "(13) &  p\\wedge q  & (10),(12)合取引入",
    premises: "q\\rightarrow p, q\\leftrightarrow s, s\\leftrightarrow t, t\\wedge r",
    consequent: "p\\wedge q"
  },
  problem2_23: {
    reasoning: "(1) &  p  & 附加前提\n"+
              "(2) &  p\\rightarrow q  & 前提\n"+
              "(3) &  q  & (1), (2)假言推理\n"+
              "(4) &  q\\rightarrow r  & 前提\n"+
              "(5) &  r  & (3), (4)假言推理\n"+
              "(6) &  p\\rightarrow r  & (1), (5)附加前提法",
    premises: "p\\rightarrow q, q\\rightarrow r",
    consequent: "p\\rightarrow r"
  },
  problem2_24: {
    reasoning: "(1) &  r  & 附加前提\n"+
              "(2) &  \\neg r\\vee p  & 前提\n"+
              "(3) &  p  & (1),(2)析取三段论\n"+
              "(4) &  p\\rightarrow(q\\rightarrow s)  & 前提\n"+
              "(5) &  q\\rightarrow s  & (3),(4)假言推理\n"+
              "(6) &  q  & 前提\n"+
              "(7) &  s  & (5),(6)假言推理\n"+
              "(8) &  r\\rightarrow s  & (1),(7)附加前提法",
    premises: "p\\rightarrow(q\\rightarrow s), \\neg r\\vee p, q",
    consequent: "r\\rightarrow s"
  },
  problem2_25: {
    reasoning: "(1) &  p\\wedge q  & 附加前提\n"+
              "(2) &  p  & (1) 化简规则\n"+
              "(3) &  \\neg p\\vee \\neg q  & 前提\n"+
              "(4) &  \\neg q  & (2),(3)析取三段论\n"+
              "(5) &  q  & (1)化简规则\n"+
              "(6) &  \\neg(p\\wedge q)  & (1), (4), (5)反证法",
    premises: "\\neg p\\vee \\neg q",
    consequent: "\\neg (p\\wedge q)"
  },
  problem2_31: {
    reasoning: "(1) &   w  & 附加前提\n"+
              "(2) &   w\\rightarrow(r\\wedge s)  & 前提\n"+
              "(3) &   r\\wedge s  & (1),(2)假言推理\n"+
              "(4) &   r  & (3)化简规则\n"+
              "(5) &   \\neg p\\rightarrow\\neg r  & 前提\n"+
              "(6) &   p  & (4),(5)假言易位\n"+
              "(7) &   t  & 前提\n"+
              "(8) &   t\\rightarrow(\\neg p\\vee\\neg q)  & 前提\n"+
              "(9) &   \\neg p\\vee\\neg q  & (7),(8)假言推理\n"+
              "(10) &  \\neg q  & (6),(9)析取三段论\n"+
              "(11) &  s  & (3)化简规则\n"+
              "(12) &  \\neg q\\rightarrow\\neg s  & 前提\n"+
              "(13) &  q  & (11),(12)假言易位\n"+
              "(14) &  \\neg w  & (1),(10),(13)反证法",
    premises: "\\neg p\\rightarrow\\neg r, \\neg q\\rightarrow\\neg s, t\\rightarrow(\\neg p\\vee \\neg q), t, w\\rightarrow(r\\wedge s)",
    consequent: "\\neg w"
  },
  problem2_32: {
    reasoning: "(1) &  p  & 附加前提\n"+
              "(2) &  p\\rightarrow q  & 前提\n"+
              "(3) &  q  & (1), (2) 假言推理\n"+
              "(4) &  s\\rightarrow \\neg q  & 前提\n"+
              "(5) &  \\neg s  & (3),(4) 假言易位\n"+
              "(6) &  r\\vee s  & 前提\n"+
              "(7) &  r  & (5),(6) 析取三段论\n"+
              "(8) &  \\neg q\\vee \\neg r  & 前提\n"+
              "(9) &  \\neg r  & (3), (8) 析取三段论\n"+
              "(10) &  \\neg p  & (1), (7), (9) 反证法",
    premises: "p\\rightarrow q, \\neg (q\\wedge r), \\neg(\\neg r\\wedge \\neg s), s\\rightarrow \\neg q",
    consequent: "\\neg p"
  }
}

// 推理规则列表
const ruleList = ref([
  { chineseName: "附加前提法", englishName: "Proof by Additional Premise", latexString: "\\textbf{PBAP}" },
  { chineseName: "附加前提", englishName: "Additional Premise", latexString: "\\textbf{AP}" },
  { chineseName: "前提", englishName: "Premise", latexString: "\\textbf{P}" },
  { chineseName: "反证法", englishName: "Proof by Contradiction", latexString: "\\textbf{PBC}" },
  { chineseName: "蕴涵引入", englishName: "Implication Introduction", latexString: "\\rightarrow+" },
  { chineseName: "假言推理", englishName: "Implication Elimination", latexString: "\\rightarrow-" },
  { chineseName: "假言三段论", englishName: "Implication Syllogism", latexString: "\\rightarrow3" },
  { chineseName: "假言易位", englishName: "Contraposition Law", latexString: "\\rightarrow\\neg" },
  { chineseName: "合取引入", englishName: "Conjunction Introduction", latexString: "\\wedge+" },
  { chineseName: "化简规则", englishName: "Conjunction Elimination", latexString: "\\wedge-" },
  { chineseName: "附加规则", englishName: "Disjunction Introduction", latexString: "\\vee+" },
  { chineseName: "析取消除", englishName: "Disjunction Elimination", latexString: "\\vee-" },
  { chineseName: "析取三段论", englishName: "Disjunction Syllogism", latexString: "\\vee\\neg" },
  { chineseName: "双蕴涵引入", englishName: "BiImplication Introduction", latexString: "\\leftrightarrow+" },
  { chineseName: "双蕴涵消除", englishName: "BiImplication Elimination", latexString: "\\leftrightarrow-" },
  { chineseName: "等值置换", englishName: "Equivalence", latexString: "\\textbf{EQ}" }
])

// 计算属性：预览步骤
const previewSteps = computed(() => {
  if (!currentReasoning.value.trim()) return []

  const lines = currentReasoning.value.split('\n').filter(line => line.trim())
  const steps = []

  for (const line of lines) {
    const parts = line.split('&').map(part => part.trim()).filter(part => part)
    if (parts.length < 3) continue

    // 解析序号
    const serialNoText = parts[0]
    const serialNo = parseInt(serialNoText.replace(/[()]/g, ''))

    // 解析公式
    const formula = parts[1]

    // 解析规则名称和依据
    const ruleAndReference = parts[2]
    let ruleName = ruleAndReference
    let prevSN = ""

    // 尝试分离引用序号和规则名称
    const match = ruleAndReference.match(/^([^(]*)(.*)$/)
    if (match) {
      ruleName = match[1].trim()
      prevSN = match[2].trim()
    }

    steps.push({
      serialNo,
      formula,
      ruleName,
      prevSN
    })
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
  currentReasoning.value = ''
  reasoningResult.value = null
  argumentForm.value.premises = ''
  argumentForm.value.consequent = ''
  ElMessage.info('已移除所有内容')
}

// 显示推理规则列表
const showRuleList = () => {
  ruleDialogVisible.value = true
}

// 合法性检查
const checkValidity = async () => {
  if (!currentReasoning.value.trim()) {
    ElMessage.warning('请先输入推理过程')
    return
  }

  if (!argumentForm.value.premises.trim() || !argumentForm.value.consequent.trim()) {
    ElMessage.warning('请先输入前题列表和结论公式')
    return
  }

  try {
    const result = await callBackendApi('/reason-argument-check/validate', {
      method: 'POST',
      body: JSON.stringify({
        premises: argumentForm.value.premises,
        consequent: argumentForm.value.consequent,
        content: currentReasoning.value
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

// 执行推理检查
const performReasoning = async () => {
  if (!currentReasoning.value.trim()) {
    ElMessage.warning('请先输入推理过程')
    return
  }

  if (!argumentForm.value.premises.trim() || !argumentForm.value.consequent.trim()) {
    ElMessage.warning('请先输入前题列表和结论公式')
    return
  }

  try {
    const result = await callBackendApi('/reason-argument-check/check', {
      method: 'POST',
      body: JSON.stringify({
        premises: argumentForm.value.premises,
        consequent: argumentForm.value.consequent,
        content: currentReasoning.value,
        stepNumber: stepCounter.value
      })
    })

    // 保存推理结果用于显示
    reasoningResult.value = result

    // 返回结果给主界面
    emit('result', {
      type: 'reason-argument-check',
      data: result
    })

    // 增加步骤计数器
    stepCounter.value++

    if (result.valid) {
      ElMessage.success(result.message || '推理检查完成')
    } else {
      ElMessage.warning(result.message || '推理检查发现问题')
    }
  } catch (error) {
    ElMessage.error('推理检查时发生错误: ' + error.message)
  }
}

// 加载示例
const loadExample = (exampleKey) => {
  const example = examples[exampleKey]
  if (example) {
    currentReasoning.value = example.reasoning
    argumentForm.value.premises = example.premises
    argumentForm.value.consequent = example.consequent
    ElMessage.success('已加载示例: ' + exampleKey)
  }
}

// 清除结果
const clearResults = () => {
  currentReasoning.value = ''
  reasoningResult.value = null
  stepCounter.value = 1
  argumentForm.value.premises = ''
  argumentForm.value.consequent = ''
  ElMessage.info('已清除所有结果')
}

// 关闭界面
const closeInterface = () => {
  emit('close')
}
</script>

<style scoped>
.reason-argument-check-interface {
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

.argument-input-section {
  margin-bottom: 20px;
}

.premise-input, .consequent-input {
  font-family: 'Courier New', monospace;
}

.reasoning-input-section {
  margin-bottom: 20px;
}

.input-tip {
  margin-bottom: 15px;
}

.reasoning-input-container {
  position: relative;
}

.reasoning-textarea {
  font-family: 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.6;
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

.reasoning-overview {
  margin-bottom: 20px;
  padding: 15px;
  background: #f0f8ff;
  border-radius: 8px;
  border: 1px solid #4a90e2;
}

.preview-step {
  display: flex;
  margin-bottom: 10px;
  align-items: center;
}

.preview-step:last-child {
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

.step-formula {
  font-size: 16px;
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

.check-formula {
  background: #fff;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  border: 1px solid #ddd;
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

.rule-list {
  max-height: 400px;
  overflow-y: auto;
}

:deep(.el-alert) {
  margin-bottom: 1rem;
}

:deep(.el-form-item__label) {
  font-weight: bold;
  color: #303133;
}
</style>