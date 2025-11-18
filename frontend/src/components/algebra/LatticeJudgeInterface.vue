<template>
  <div class="lattice-judge-interface">
    <!-- 按钮操作区域 -->
    <div class="button-section">
      <!-- 第一行按钮：开始判断、生成随机、清除输入、合法性检查 -->
      <el-row :gutter="20" class="button-row">
        <el-col :span="4">
          <el-button type="primary" @click="startJudge">
            <el-icon><Check /></el-icon>
            格判断(Y)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="success" @click="generateExample">
            <el-icon><MagicStick /></el-icon>
            生成示例(G)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="warning" @click="clearInput">
            <el-icon><Delete /></el-icon>
            清除输入(V)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="info" @click="checkInput">
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

      <!-- 第二行按钮：示例题 -->
      <el-divider content-position="left">《离散数学基础》教材示例展示</el-divider>
      <el-row :gutter="15" class="example-buttons">
        <el-col :span="3">
          <el-button size="small" @click="loadExample('example10_44_a')">例10.44(a)</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('example10_44_b')">例10.44(b)</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('example10_44_c')">例10.44(c)</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('example10_45_a')">例10.45(a)</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('example10_45_b')">例10.45(b)</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('example10_45_c')">例10.45(c)</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('example10_45_d')">例10.45(d)</el-button>
        </el-col>
      </el-row>
      <el-row :gutter="15" class="example-buttons" style="margin-top: 0.5rem;">
        <el-col :span="3">
          <el-button size="small" @click="loadExample('example10_47_1')">例10.47(L1)</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('example10_47_2')">例10.47(L2)</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('example10_47_3')">例10.47(L3)</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('example10_47_4')">例10.47(L4)</el-button>
        </el-col>
      </el-row>

      <!-- 元素类型选择 -->
      <el-divider content-position="left">集合元素类型选择</el-divider>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-radio-group v-model="elementType" size="large">
            <el-radio label="char">单个字符</el-radio>
            <el-radio label="int">整数</el-radio>
          </el-radio-group>
        </el-col>
        <el-col :span="12">
          <el-checkbox v-model="useHasseDiagram" size="large">
            基于哈斯图输入偏序关系
          </el-checkbox>
        </el-col>
      </el-row>

      <!-- 格属性选择 -->
      <el-divider content-position="left">选择要判断的格属性</el-divider>
      <el-row :gutter="20">
        <el-col :span="4">
          <el-checkbox v-model="checkOptions.distributive" size="large">
            是否分配格
          </el-checkbox>
        </el-col>
        <el-col :span="4">
          <el-checkbox v-model="checkOptions.bounded" size="large">
            是否有界格
          </el-checkbox>
        </el-col>
        <el-col :span="4">
          <el-checkbox v-model="checkOptions.complemented" size="large">
            是否有补格
          </el-checkbox>
        </el-col>
        <el-col :span="4">
          <el-checkbox v-model="checkOptions.boolean" size="large">
            是否布尔代数
          </el-checkbox>
        </el-col>
      </el-row>
    </div>

    <!-- 集合和关系输入区域 -->
    <div class="input-section">
      <el-divider content-position="left">输入集合和关系</el-divider>
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="input-group">
            <label>集合(A)：</label>
            <el-input
              v-model="setAInput"
              placeholder="例如：{a, b, c, d, e}"
              class="set-input"
            />
          </div>
        </el-col>
        <el-col :span="12">
          <div class="input-group">
            <label>元素类型：</label>
            <el-radio-group v-model="elementType" size="small">
              <el-radio label="char">字符</el-radio>
              <el-radio label="int">整数</el-radio>
            </el-radio-group>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20" style="margin-top: 1rem;">
        <el-col :span="24">
          <div class="input-group">
            <label>关系(R)：</label>
            <el-input
              v-model="relationRInput"
              type="textarea"
              :rows="3"
              placeholder="例如：{&lt;a, b&gt;, &lt;a, c&gt;, &lt;b, d&gt;, &lt;c, d&gt;}"
              class="relation-input"
            />
          </div>
        </el-col>
      </el-row>
      <div class="input-hint">
        <el-text type="info" size="small">
          集合格式：{element1, element2, element3}
          <br>
          关系格式：{&lt;element1, element2&gt;, &lt;element3, element4&gt;}
          <br>
          整数示例：{1, 2, 3} 字符示例：{a, b, c}
          <br>
          关系示例：{&lt;1, 2&gt;, &lt;2, 3&gt;}
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
            :type="'mathjax'"
            :display-mode="false"
            class="feedback-formula"
          />
          <el-text v-if="item.message" :type="item.type" class="feedback-message">
            {{ item.message }}
          </el-text>
        </div>
      </div>
    </div>

    <!-- 格判断结果区域 -->
    <div v-if="results.length > 0" class="results-section">
      <el-divider content-position="left">格判断分析结果</el-divider>
      <div class="results-content">
        <div v-for="(result, index) in results" :key="index" class="result-item">
          <!-- 输入信息 -->
          <div class="result-basic">
            <h4>输入信息：</h4>
            <math-renderer
              :formula="result.formula"
              :type="'mathjax'"
              :display-mode="true"
              class="result-formula"
            />
          </div>

          <!-- 哈斯图 -->
          <div v-if="result.hasseDiagramUrl" class="hasse-diagram-result">
            <h4>哈斯图：</h4>
            <div class="diagram-container">
              <img :src="result.hasseDiagramUrl" alt="哈斯图" class="diagram-image" />
            </div>
          </div>

          <!-- 偏序关系判断 -->
          <div class="partial-order-result">
            <h4>偏序关系判断：</h4>
            <div class="judgment-result">
              <el-tag :type="result.isPartialOrder ? 'success' : 'danger'">
                {{ result.isPartialOrder ? '是偏序关系' : '不是偏序关系' }}
              </el-tag>
              <div v-if="result.partialOrderReason" class="reason-text">
                原因：{{ result.partialOrderReason }}
              </div>
              <div v-if="result.partialOrderCounterexample" class="counterexample-text">
                反例：
                <math-renderer
                  :formula="result.partialOrderCounterexample"
                  :type="'mathjax'"
                  :display-mode="false"
                  class="counterexample-formula"
                />
              </div>
            </div>
          </div>

          <!-- 格判断结果 -->
          <div v-if="result.isPartialOrder" class="lattice-result">
            <h4>格判断：</h4>
            <div class="judgment-result">
              <el-tag :type="result.isLattice ? 'success' : 'danger'">
                {{ result.isLattice ? '是格' : '不是格' }}
              </el-tag>
              <div v-if="result.latticeReason" class="reason-text">
                原因：{{ result.latticeReason }}
              </div>
              <div v-if="result.latticeCounterexample" class="counterexample-text">
                反例：
                <math-renderer
                  :formula="result.latticeCounterexample"
                  :type="'mathjax'"
                  :display-mode="false"
                  class="counterexample-formula"
                />
              </div>
            </div>
          </div>

          <!-- 格运算表 -->
          <div v-if="result.isLattice && result.supOperatorTable" class="operator-tables-result">
            <h4>格运算表：</h4>
            <div class="operator-table">
              <h5>最小上界运算：</h5>
              <math-renderer
                :formula="result.supOperatorTable"
                :type="'katex'"
                :display-mode="true"
                class="operator-formula"
              />
            </div>
            <div class="operator-table">
              <h5>最大下界运算：</h5>
              <math-renderer
                :formula="result.subOperatorTable"
                :type="'katex'"
                :display-mode="true"
                class="operator-formula"
              />
            </div>
          </div>

          <!-- 分配格判断 -->
          <div v-if="result.isLattice && result.isDistributive !== null" class="distributive-result">
            <h4>分配格判断：</h4>
            <div class="judgment-result">
              <el-tag :type="result.isDistributive ? 'success' : 'danger'">
                {{ result.isDistributive ? '是分配格' : '不是分配格' }}
              </el-tag>
              <div v-if="result.distributiveReason" class="reason-text">
                原因：
                <math-renderer
                  :formula="result.distributiveReason"
                  :type="'mathjax'"
                  :display-mode="false"
                  class="reason-formula"
                />
              </div>
            </div>
          </div>

          <!-- 有界格判断 -->
          <div v-if="result.isLattice && result.isBounded !== null" class="bounded-result">
            <h4>有界格判断：</h4>
            <div class="judgment-result">
              <el-tag :type="result.isBounded ? 'success' : 'danger'">
                {{ result.isBounded ? '是有界格' : '不是有界格' }}
              </el-tag>
              <div v-if="result.boundedReason" class="reason-text">
                原因：{{ result.boundedReason }}
              </div>
              <div v-if="result.isBounded && result.greatestElement && result.leastElement" class="bounded-elements">
                最大元：<math-renderer :formula="result.greatestElement" :type="'mathjax'" :display-mode="false" />
                ，最小元：<math-renderer :formula="result.leastElement" :type="'mathjax'" :display-mode="false" />
              </div>
            </div>
          </div>

          <!-- 有补格判断 -->
          <div v-if="result.isLattice && result.isComplemented !== null" class="complemented-result">
            <h4>有补格判断：</h4>
            <div class="judgment-result">
              <el-tag :type="result.isComplemented ? 'success' : 'danger'">
                {{ result.isComplemented ? '是有补格' : '不是有补格' }}
              </el-tag>
              <div v-if="result.complementedReason" class="reason-text">
                原因：{{ result.complementedReason }}
              </div>
              <div v-if="result.isComplemented && result.complements" class="complements-list">
                <h5>元素补元列表：</h5>
                <div v-for="(complement, idx) in result.complements" :key="idx" class="complement-item">
                  元素 <math-renderer :formula="complement.element" :type="'mathjax'" :display-mode="false" />
                  的补元：<math-renderer :formula="complement.complementElements" :type="'mathjax'" :display-mode="false" />
                </div>
              </div>
            </div>
          </div>

          <!-- 布尔代数判断 -->
          <div v-if="result.isLattice && result.isBooleanAlgebra !== null" class="boolean-result">
            <h4>布尔代数判断：</h4>
            <div class="judgment-result">
              <el-tag :type="result.isBooleanAlgebra ? 'success' : 'danger'">
                {{ result.isBooleanAlgebra ? '是布尔代数' : '不是布尔代数' }}
              </el-tag>
              <div v-if="result.booleanAlgebraReason" class="reason-text">
                原因：{{ result.booleanAlgebraReason }}
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
const setAInput = ref('')
const relationRInput = ref('')
const elementType = ref('char')
const useHasseDiagram = ref(true)
const feedback = ref([])
const results = ref([])
const counter = ref(0)

// 检查选项
const checkOptions = ref({
  distributive: false,
  bounded: false,
  complemented: false,
  boolean: false
})

// 示例数据
const examples = {
  example10_44_a: {
    setA: '{a, b, c, d, e}',
    relationR: '{<a, b>, <a, c>, <a, d>, <b, e>, <c, e>, <d, e>}',
    elementType: 'char',
    useHasseDiagram: true,
    checkOptions: { distributive: false, bounded: false, complemented: false, boolean: false }
  },
  example10_44_b: {
    setA: '{a, b, c, d, e, f}',
    relationR: '{<a, b>, <a, c>, <c, d>, <b, d>, <b, e>, <d, f>, <e, f>}',
    elementType: 'char',
    useHasseDiagram: true,
    checkOptions: { distributive: false, bounded: false, complemented: false, boolean: false }
  },
  example10_44_c: {
    setA: '{a, b, c, d, e, f}',
    relationR: '{<a, b>, <a, c>, <a, d>, <b, e>, <c, e>, <d, f>, <e, f>}',
    elementType: 'char',
    useHasseDiagram: true,
    checkOptions: { distributive: false, bounded: false, complemented: false, boolean: false }
  },
  example10_45_a: {
    setA: '{a, b, c, d, e, f}',
    relationR: '{<a, c>, <b, c>, <c, d>, <d, e>, <d, f>}',
    elementType: 'char',
    useHasseDiagram: true,
    checkOptions: { distributive: false, bounded: false, complemented: false, boolean: false }
  },
  example10_45_b: {
    setA: '{a, b, c, d, e}',
    relationR: '{<a, b>, <a, d>, <b, c>, <d, c>, <b, e>, <d, e>}',
    elementType: 'char',
    useHasseDiagram: true,
    checkOptions: { distributive: false, bounded: false, complemented: false, boolean: false }
  },
  example10_45_c: {
    setA: '{a, b, c, d, e, f}',
    relationR: '{<a, b>, <a, c>, <b, d>, <b, e>, <c, d>, <c, e>, <d, f>, <e, f>}',
    elementType: 'char',
    useHasseDiagram: true,
    checkOptions: { distributive: false, bounded: false, complemented: false, boolean: false }
  },
  example10_45_d: {
    setA: '{a, b, c, d, e, f, g}',
    relationR: '{<a, b>, <a, c>, <b, d>, <c, d>, <g, f>, <f, e>}',
    elementType: 'char',
    useHasseDiagram: true,
    checkOptions: { distributive: false, bounded: false, complemented: false, boolean: false }
  },
  example10_47_1: {
    setA: '{a, b, c}',
    relationR: '{<a, b>, <b, c>}',
    elementType: 'char',
    useHasseDiagram: true,
    checkOptions: { distributive: true, bounded: true, complemented: true, boolean: false }
  },
  example10_47_2: {
    setA: '{a, b, c, d}',
    relationR: '{<a, b>, <a, c>, <b, d>, <c, d>}',
    elementType: 'char',
    useHasseDiagram: true,
    checkOptions: { distributive: true, bounded: true, complemented: true, boolean: false }
  },
  example10_47_3: {
    setA: '{a, b, c, d, e}',
    relationR: '{<a, b>, <a, c>, <a, d>, <b, e>, <c, e>, <d, e>}',
    elementType: 'char',
    useHasseDiagram: true,
    checkOptions: { distributive: true, bounded: true, complemented: true, boolean: false }
  },
  example10_47_4: {
    setA: '{a, b, c, d, e}',
    relationR: '{<a, b>, <a, c>, <b, e>, <c, d>, <d, e>}',
    elementType: 'char',
    useHasseDiagram: true,
    checkOptions: { distributive: true, bounded: true, complemented: true, boolean: false }
  }
}

// 事件处理函数
const emit = defineEmits(['close', 'lattice-judge-result'])

const startJudge = async () => {
  if (!setAInput.value.trim() || !relationRInput.value.trim()) {
    ElMessage.warning('请输入集合A和关系R')
    return
  }

  // 清空之前的反馈和结果
  feedback.value = []
  results.value = []

  try {
    const request = {
      setAString: setAInput.value.trim(),
      relationRString: relationRInput.value.trim(),
      intTypeElement: elementType.value === 'int',
      useHasseDiagram: useHasseDiagram.value,
      checkDistributive: checkOptions.value.distributive,
      checkBounded: checkOptions.value.bounded,
      checkComplemented: checkOptions.value.complemented,
      checkBoolean: checkOptions.value.boolean
    }

    console.log('LatticeJudgeInterface: 开始格判断', request)

    const response = await callBackendApi('/algebra/lattice-judge', {
      method: 'POST',
      body: JSON.stringify(request)
    })

    console.log('LatticeJudgeInterface: 收到判断结果', response)
    console.log('LatticeJudgeInterface: 详细调试信息', {
      success: response.success,
      isPartialOrder: response.isPartialOrder,
      partialOrderReason: response.partialOrderReason,
      type: response.type
    })

    if (response.success) {
      const result = {
        ...response,
        index: counter.value + 1
      }

      results.value.push(result)
      counter.value++

      // 添加成功反馈
      feedback.value.push({
        formula: result.formula,
        type: 'success',
        message: '格判断完成'
      })

      // 发送结果到主界面
      emit('lattice-judge-result', result)

      ElMessage.success('格判断完成')
    } else {
      feedback.value.push({
        formula: 'A = ?, R = ?',
        type: 'error',
        message: response.errorMessage || '格判断失败'
      })
    }
  } catch (error) {
    console.error('格判断失败:', error)
    feedback.value.push({
      formula: 'A = ?, R = ?',
      type: 'error',
      message: `判断失败: ${error.message}`
    })
  }
}

const generateExample = async () => {
  try {
    console.log('LatticeJudgeInterface: 开始生成示例')

    const response = await callBackendApi('/algebra/lattice-judge/generate', {
      method: 'POST'
    })

    console.log('LatticeJudgeInterface: 示例生成结果', response)

    if (response.success) {
      setAInput.value = response.setA
      relationRInput.value = response.relationR
      elementType.value = response.intTypeElement ? 'int' : 'char'
      useHasseDiagram.value = response.useHasseDiagram

      // 设置检查选项
      Object.assign(checkOptions.value, response.checkOptions)

      // 清除之前的结果
      feedback.value = []
      results.value = []

      ElMessage.success('已生成格判断示例')
    } else {
      ElMessage.error('生成示例失败: ' + response.message)
    }
  } catch (error) {
    console.error('生成示例失败:', error)
    ElMessage.error(`生成示例失败: ${error.message}`)
  }
}

const clearInput = () => {
  setAInput.value = ''
  relationRInput.value = ''
  ElMessage.info('已清空输入')
}

const checkInput = async () => {
  if (!setAInput.value.trim() || !relationRInput.value.trim()) {
    ElMessage.warning('请输入集合A和关系R')
    return
  }

  feedback.value = []

  try {
    const request = {
      setAString: setAInput.value.trim(),
      relationRString: relationRInput.value.trim(),
      intTypeElement: elementType.value === 'int'
    }

    const response = await callBackendApi('/algebra/lattice-judge/validate', {
      method: 'POST',
      body: JSON.stringify(request)
    })

    if (response.valid) {
      feedback.value.push({
        formula: 'A = ?, R = ?',
        type: 'success',
        message: response.message
      })
      ElMessage.success('输入格式正确')
    } else {
      feedback.value.push({
        formula: 'A = ?, R = ?',
        type: 'error',
        message: response.message
      })
      ElMessage.error('输入格式不正确')
    }
  } catch (error) {
    console.error('检查输入失败:', error)
    feedback.value.push({
      formula: 'A = ?, R = ?',
      type: 'error',
      message: `检查失败: ${error.message}`
    })
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
  if (examples[exampleKey]) {
    const example = examples[exampleKey]

    // 清除之前的结果
    results.value = []
    feedback.value = []
    counter.value = 0

    // 设置示例数据
    setAInput.value = example.setA
    relationRInput.value = example.relationR
    elementType.value = example.elementType
    useHasseDiagram.value = example.useHasseDiagram

    // 设置检查选项
    Object.assign(checkOptions.value, example.checkOptions)

    console.log('LatticeJudgeInterface: 加载示例', exampleKey, example)
    ElMessage.info(`已加载示例：${exampleKey}`)
  }
}

const selectAllProperties = () => {
  checkOptions.value = {
    distributive: true,
    bounded: true,
    complemented: true,
    boolean: true
  }
}

const clearAllProperties = () => {
  checkOptions.value = {
    distributive: false,
    bounded: false,
    complemented: false,
    boolean: false
  }
}

// 通用API调用函数
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
</script>

<style scoped>
.lattice-judge-interface {
  display: flex;
  flex-direction: column;
  width: 100%;
}

/* 响应式布局 */
@media (min-width: 1200px) {
  .lattice-judge-interface {
    width: 100%;
    max-width: none;
  }
}

@media (max-width: 1199px) and (min-width: 900px) {
  .lattice-judge-interface {
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
  }
}

@media (max-width: 899px) {
  .lattice-judge-interface {
    width: 900px;
    min-width: 900px;
    max-width: 900px;
    margin: 0 auto;
  }
}

.button-section {
  margin-bottom: 1rem;
  flex-shrink: 0;
}

.button-row {
  margin-bottom: 1rem;
}

.example-buttons .el-button {
  width: 100%;
  margin-bottom: 0.5rem;
}

.input-section {
  margin-top: 1rem;
  flex-shrink: 0;
}

.input-group {
  margin-bottom: 1rem;
}

.input-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #374151;
}

.set-input,
.relation-input {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
}

.input-hint {
  margin-top: 0.5rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

/* 反馈信息区域 */
.feedback-section {
  margin-top: 1rem;
}

.feedback-content {
  background: #f8f9fa;
  padding: 1.5rem;
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
  font-size: 1.1rem;
  overflow-x: auto;
  padding: 0.5rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.feedback-message {
  font-size: 0.9rem;
  margin-top: 0.5rem;
}

/* 结果显示区域 */
.results-section {
  margin-top: 1rem;
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

.result-basic {
  margin-bottom: 1.5rem;
}

.result-formula {
  margin-bottom: 1rem;
  font-size: 1.1rem;
  overflow-x: auto;
  padding: 0.5rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

/* 哈斯图显示 */
.hasse-diagram-result {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.diagram-container {
  text-align: center;
  margin: 1rem 0;
}

.diagram-image {
  max-width: 100%;
  height: auto;
  border: 1px solid #e9ecef;
  border-radius: 4px;
}

/* 判断结果样式 */
.partial-order-result,
.lattice-result,
.distributive-result,
.bounded-result,
.complemented-result,
.boolean-result {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.judgment-result {
  margin-top: 0.5rem;
}

.reason-text {
  margin: 0.5rem 0;
  color: #6b7280;
  font-size: 0.9rem;
}

.counterexample-text {
  margin: 0.5rem 0;
  color: #dc2626;
  font-size: 0.9rem;
}

.counterexample-formula,
.reason-formula {
  display: inline-block;
  margin: 0.2rem 0;
  padding: 0.2rem 0.5rem;
  background: #fef2f2;
  border-radius: 4px;
  border: 1px solid #fecaca;
}

/* 运算表样式 */
.operator-tables-result {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.operator-table {
  margin: 1rem 0;
}

.operator-formula {
  margin: 1rem 0;
  font-size: 1.1rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  overflow-x: auto;
}

/* 有界格和有补格样式 */
.bounded-elements {
  margin: 0.5rem 0;
  font-size: 0.9rem;
}

.complements-list {
  margin-top: 1rem;
}

.complement-item {
  margin: 0.5rem 0;
  font-size: 0.9rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

h4 {
  color: #374151;
  margin: 1rem 0 0.5rem 0;
  font-size: 1.1rem;
  font-weight: 600;
}

h5 {
  color: #6b7280;
  margin: 1rem 0 0.5rem 0;
  font-size: 1rem;
  font-weight: 500;
}

/* 响应式优化 */
@media (max-width: 768px) {
  .example-buttons .el-button {
    font-size: 0.8rem;
  }

  .operator-formula,
  .result-formula {
    font-size: 0.9rem;
  }
}

@media (max-width: 480px) {
  .button-row .el-col {
    margin-bottom: 0.5rem;
  }

  .result-item {
    padding: 1rem;
  }
}
</style>