<template>
  <div class="partial-order-interface">
    <!-- 按钮操作区域 -->
    <div class="button-section">
      <!-- 第一行按钮：开始运算、生成随机集合、清除输入、合法性检查 -->
      <el-row :gutter="20" class="button-row">
        <el-col :span="4">
          <el-button type="primary" @click="startCalculation">
            <el-icon><Check /></el-icon>
            偏序关系计算(Y)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="success" @click="generateRandomPartialOrder">
            <el-icon><MagicStick /></el-icon>
            生成随机(G)
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
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem6_38')">例题6.38</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem6_40_1')">例题6.40(1)</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem6_40_2')">例题6.40(2)</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem6_40_3')">例题6.40(3)</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem6_42_1')">例题6.42(1)</el-button>
        </el-col>
        <el-col :span="4">
          <el-button size="small" @click="loadExample('problem6_42_2')">例题6.42(2)</el-button>
        </el-col>
      </el-row>

      <!-- 元素类型选择 -->
      <el-divider content-position="left">元素类型选择</el-divider>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-radio-group v-model="elementType" size="large">
            <el-radio label="char">单个字符</el-radio>
            <el-radio label="int">整数</el-radio>
          </el-radio-group>
        </el-col>
        <el-col :span="12">
          <el-checkbox
            v-model="useDivideRelation"
            :disabled="elementType !== 'int'"
            size="large"
          >
            使用整除关系
          </el-checkbox>
        </el-col>
      </el-row>

      <!-- 显示选项 -->
      <el-divider content-position="left">显示选项</el-divider>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-checkbox v-model="options.showRelationMatrix" size="large">
            显示关系矩阵
          </el-checkbox>
        </el-col>
        <el-col :span="8">
          <el-checkbox v-model="options.showRelationGraph" size="large">
            显示关系图
          </el-checkbox>
        </el-col>
        <el-col :span="8">
          <el-checkbox v-model="options.showHasseDiagram" size="large">
            显示哈斯图
          </el-checkbox>
        </el-col>
      </el-row>

      <!-- 计算选项 -->
      <el-divider content-position="left">选择要进行的计算</el-divider>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-checkbox v-model="options.calculateMinimum" size="large">
            极小元
          </el-checkbox>
        </el-col>
        <el-col :span="6">
          <el-checkbox v-model="options.calculateMaximum" size="large">
            极大元
          </el-checkbox>
        </el-col>
        <el-col :span="6">
          <el-checkbox v-model="options.calculateLeast" size="large">
            最小元
          </el-checkbox>
        </el-col>
        <el-col :span="6">
          <el-checkbox v-model="options.calculateGreatest" size="large">
            最大元
          </el-checkbox>
        </el-col>
      </el-row>
      <el-row :gutter="20" style="margin-top: 0.5rem;">
        <el-col :span="6">
          <el-checkbox v-model="options.calculateLower" size="large">
            下界
          </el-checkbox>
        </el-col>
        <el-col :span="6">
          <el-checkbox v-model="options.calculateUpper" size="large">
            上界
          </el-checkbox>
        </el-col>
        <el-col :span="6">
          <el-checkbox v-model="options.calculateGLB" size="large">
            最大下界
          </el-checkbox>
        </el-col>
        <el-col :span="6">
          <el-checkbox v-model="options.calculateLUB" size="large">
            最小上界
          </el-checkbox>
        </el-col>
      </el-row>
    </div>

    <!-- 集合和关系输入区域 -->
    <div class="input-section">
      <el-divider content-position="left">集合和关系的输入</el-divider>
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="input-group">
            <label>集合(A)：</label>
            <el-input
              v-model="setAInput"
              placeholder="例如：{1, 2, 3, 4}"
              class="set-input"
            />
          </div>
        </el-col>
        <el-col :span="12">
          <div class="input-group">
            <label>子集(S)：</label>
            <el-input
              v-model="setSInput"
              placeholder="例如：{2, 3}（可选）"
              class="set-input"
            />
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
              :rows="2"
              placeholder="例如：{<1, 1>, <1, 2>, <2, 2>, <2, 4>, <3, 3>, <3, 6>, <4, 4>, <4, 12>, <6, 6>, <6, 12>, <12, 12>}"
              class="set-input"
              :disabled="useDivideRelation"
            />
          </div>
        </el-col>
      </el-row>
      <div class="input-hint">
        <el-text type="info" size="small">
          集合格式：{element1, element2, element3}
          <br>
          整数示例：{1, 2, 3} 字符示例：{a, b, c}
          <br>
          关系格式：{&lt;a, b&gt;, &lt;c, d&gt;, ...}
          <br>
          注意：子集S必须是集合A的子集，用于计算界相关概念！
        </el-text>
      </div>
    </div>

    <!-- 内容显示区域 -->
    <div class="content-display-area">
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

      <!-- 偏序关系分析结果区域 -->
      <div v-if="results.length > 0" class="results-section">
        <el-divider content-position="left">偏序关系分析结果</el-divider>
        <div class="results-content">
          <div v-for="(result, index) in results" :key="index" class="result-item">
            <!-- 基本信息 -->
            <div class="result-basic">
              <h4>输入信息：</h4>
              <math-renderer
                :formula="result.formula"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
            </div>

            <!-- 关系性质分析 -->
            <div class="relation-properties">
              <h4>关系性质分析：</h4>
              <el-row :gutter="20">
                <el-col :span="8">
                  <el-tag :type="result.isReflexive ? 'success' : 'danger'">
                    自反性: {{ result.isReflexive ? '满足' : '不满足' }}
                  </el-tag>
                </el-col>
                <el-col :span="8">
                  <el-tag :type="result.isAntisymmetric ? 'success' : 'danger'">
                    反对称性: {{ result.isAntisymmetric ? '满足' : '不满足' }}
                  </el-tag>
                </el-col>
                <el-col :span="8">
                  <el-tag :type="result.isTransitive ? 'success' : 'danger'">
                    传递性: {{ result.isTransitive ? '满足' : '不满足' }}
                  </el-tag>
                </el-col>
              </el-row>
              <el-row :gutter="20" style="margin-top: 0.5rem;">
                <el-col :span="24">
                  <el-tag :type="result.isPartialOrder ? 'success' : 'warning'" size="large">
                    偏序关系: {{ result.isPartialOrder ? '是偏序关系' : '不是偏序关系' }}
                  </el-tag>
                </el-col>
              </el-row>
            </div>

            <!-- 矩阵和图显示 -->
            <div v-if="result.relationMatrix || result.relationGraphUrl || result.hasseDiagramUrl" class="visualization-section">
              <!-- 关系矩阵 -->
              <div v-if="result.relationMatrix" class="relation-matrix">
                <h4>关系矩阵：</h4>
                <math-renderer
                  :formula="result.relationMatrix"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="matrix-formula"
                />
              </div>

              <!-- 关系图 -->
              <div v-if="result.relationGraphUrl" class="relation-graph">
                <h4>关系图：</h4>
                <div class="graph-container">
                  <img :src="result.relationGraphUrl" alt="关系图" class="graph-image" />
                </div>
              </div>

              <!-- 哈斯图 -->
              <div v-if="result.hasseDiagramUrl" class="hasse-diagram">
                <h4>哈斯图：</h4>
                <div class="graph-container">
                  <img :src="result.hasseDiagramUrl" alt="哈斯图" class="graph-image" />
                </div>
              </div>
            </div>

            <!-- 极值元素结果（仅当是偏序关系时显示） -->
            <div v-if="result.isPartialOrder" class="extremal-elements">
              <h4>极值元素计算：</h4>
              <el-row :gutter="20">
                <el-col v-if="result.minimumElements" :span="12">
                  <div class="element-result">
                    <h5>极小元：</h5>
                    <math-renderer
                      :formula="result.minimumElements"
                      :type="'mathjax'"
                      :display-mode="true"
                      class="element-formula"
                    />
                  </div>
                </el-col>
                <el-col v-if="result.maximumElements" :span="12">
                  <div class="element-result">
                    <h5>极大元：</h5>
                    <math-renderer
                      :formula="result.maximumElements"
                      :type="'mathjax'"
                      :display-mode="true"
                      class="element-formula"
                    />
                  </div>
                </el-col>
              </el-row>
              <el-row :gutter="20" style="margin-top: 1rem;">
                <el-col v-if="result.leastElement" :span="12">
                  <div class="element-result">
                    <h5>最小元：</h5>
                    <math-renderer
                      :formula="result.leastElement"
                      :type="'mathjax'"
                      :display-mode="true"
                      class="element-formula"
                    />
                  </div>
                </el-col>
                <el-col v-if="result.greatestElement" :span="12">
                  <div class="element-result">
                    <h5>最大元：</h5>
                    <math-renderer
                      :formula="result.greatestElement"
                      :type="'mathjax'"
                      :display-mode="true"
                      class="element-formula"
                    />
                  </div>
                </el-col>
              </el-row>
            </div>

            <!-- 界计算结果（仅当有子集S且是偏序关系时显示） -->
            <div v-if="result.isPartialOrder && result.setSString" class="bounds-section">
              <h4>界计算（针对子集S）：</h4>
              <el-row :gutter="20">
                <el-col v-if="result.lowerBounds" :span="12">
                  <div class="bound-result">
                    <h5>下界：</h5>
                    <math-renderer
                      :formula="result.lowerBounds"
                      :type="'mathjax'"
                      :display-mode="true"
                      class="bound-formula"
                    />
                  </div>
                </el-col>
                <el-col v-if="result.upperBounds" :span="12">
                  <div class="bound-result">
                    <h5>上界：</h5>
                    <math-renderer
                      :formula="result.upperBounds"
                      :type="'mathjax'"
                      :display-mode="true"
                      class="bound-formula"
                    />
                  </div>
                </el-col>
              </el-row>
              <el-row :gutter="20" style="margin-top: 1rem;">
                <el-col v-if="result.greatestLowerBound" :span="12">
                  <div class="bound-result">
                    <h5>最大下界(GLB)：</h5>
                    <math-renderer
                      :formula="result.greatestLowerBound"
                      :type="'mathjax'"
                      :display-mode="true"
                      class="bound-formula"
                    />
                  </div>
                </el-col>
                <el-col v-if="result.leastUpperBound" :span="12">
                  <div class="bound-result">
                    <h5>最小上界(LUB)：</h5>
                    <math-renderer
                      :formula="result.leastUpperBound"
                      :type="'mathjax'"
                      :display-mode="true"
                      class="bound-formula"
                    />
                  </div>
                </el-col>
              </el-row>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
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
const setSInput = ref('')
const relationRInput = ref('')
const elementType = ref('char')
const useDivideRelation = ref(false)
const feedback = ref([])
const results = ref([])
const counter = ref(0)

// 计算选项
const options = ref({
  showRelationMatrix: false,
  showRelationGraph: true,
  showHasseDiagram: true,
  calculateMinimum: true,
  calculateMaximum: true,
  calculateLeast: true,
  calculateGreatest: true,
  calculateLower: false,
  calculateUpper: false,
  calculateGLB: false,
  calculateLUB: false
})

// 示例数据
const examples = {
  problem6_38: {
    setA: '{1, 2, 3, 4, 6, 12}',
    setS: '',
    relationR: '{<1, 1>, <1, 2>, <1, 3>, <1, 4>, <1, 6>, <1, 12>, <2, 2>, <2, 4>, <2, 6>, <2, 12>, <3, 3>, <3, 6>, <3, 12>, <4, 4>, <4, 12>, <6, 6>, <6, 12>, <12, 12>}',
    elementType: 'int',
    useDivideRelation: false,
    options: {
      showRelationMatrix: false,
      showRelationGraph: false,
      showHasseDiagram: true,
      calculateMinimum: true,
      calculateMaximum: true,
      calculateLeast: true,
      calculateGreatest: true,
      calculateLower: false,
      calculateUpper: false,
      calculateGLB: false,
      calculateLUB: false
    }
  },
  problem6_40_1: {
    setA: '{1, 2, 3, 4}',
    setS: '',
    relationR: '{<1, 1>, <2, 2>, <3, 3>, <4, 4>}',
    elementType: 'int',
    useDivideRelation: false,
    options: {
      showRelationMatrix: false,
      showRelationGraph: false,
      showHasseDiagram: true,
      calculateMinimum: true,
      calculateMaximum: true,
      calculateLeast: true,
      calculateGreatest: true,
      calculateLower: false,
      calculateUpper: false,
      calculateGLB: false,
      calculateLUB: false
    }
  },
  problem6_40_2: {
    setA: '{1, 2, 3, 4}',
    setS: '',
    relationR: '{<1, 2>, <1, 3>, <2, 4>, <1, 4>, <1, 1>, <2, 2>, <3, 3>, <4, 4>}',
    elementType: 'int',
    useDivideRelation: false,
    options: {
      showRelationMatrix: false,
      showRelationGraph: false,
      showHasseDiagram: true,
      calculateMinimum: true,
      calculateMaximum: true,
      calculateLeast: true,
      calculateGreatest: true,
      calculateLower: false,
      calculateUpper: false,
      calculateGLB: false,
      calculateLUB: false
    }
  },
  problem6_40_3: {
    setA: '{1, 2, 3, 4}',
    setS: '',
    relationR: '{<1, 2>, <1, 3>, <2, 4>, <3, 4>, <1, 4>, <1, 1>, <2, 2>, <3, 3>, <4, 4>}',
    elementType: 'int',
    useDivideRelation: false,
    options: {
      showRelationMatrix: false,
      showRelationGraph: false,
      showHasseDiagram: true,
      calculateMinimum: true,
      calculateMaximum: true,
      calculateLeast: true,
      calculateGreatest: true,
      calculateLower: false,
      calculateUpper: false,
      calculateGLB: false,
      calculateLUB: false
    }
  },
  problem6_42_1: {
    setA: '{1, 2, 3, 4, 6, 9, 12, 18}',
    setS: '{4, 6, 9}',
    relationR: '{<1, 1>, <1, 2>, <1, 3>, <1, 4>, <1, 6>, <1, 9>, <1, 12>, <1, 18>, <2, 2>, <2, 4>, <2, 6>, <2, 12>, <2, 18>, <3, 3>, <3, 6>, <3, 9>, <3, 12>, <3, 18>, <4, 4>, <4, 12>, <6, 6>, <6, 12>, <6, 18>, <9, 9>, <9, 18>, <12, 12>, <18, 18>}',
    elementType: 'int',
    useDivideRelation: false,
    options: {
      showRelationMatrix: false,
      showRelationGraph: false,
      showHasseDiagram: true,
      calculateMinimum: true,
      calculateMaximum: true,
      calculateLeast: true,
      calculateGreatest: true,
      calculateLower: true,
      calculateUpper: true,
      calculateGLB: true,
      calculateLUB: true
    }
  },
  problem6_42_2: {
    setA: '{1, 2, 3, 4, 6, 9, 12, 18}',
    setS: '{2, 3, 6}',
    relationR: '{<1, 1>, <1, 2>, <1, 3>, <1, 4>, <1, 6>, <1, 9>, <1, 12>, <1, 18>, <2, 2>, <2, 4>, <2, 6>, <2, 12>, <2, 18>, <3, 3>, <3, 6>, <3, 9>, <3, 12>, <3, 18>, <4, 4>, <4, 12>, <6, 6>, <6, 12>, <6, 18>, <9, 9>, <9, 18>, <12, 12>, <18, 18>}',
    elementType: 'int',
    useDivideRelation: false,
    options: {
      showRelationMatrix: false,
      showRelationGraph: false,
      showHasseDiagram: true,
      calculateMinimum: true,
      calculateMaximum: true,
      calculateLeast: true,
      calculateGreatest: true,
      calculateLower: true,
      calculateUpper: true,
      calculateGLB: true,
      calculateLUB: true
    }
  }
}

// 事件处理函数
const emit = defineEmits(['close', 'partial-order-result'])

const startCalculation = async () => {
  if (!setAInput.value.trim()) {
    ElMessage.warning('请输入集合A')
    return
  }

  if (!useDivideRelation.value && !relationRInput.value.trim()) {
    ElMessage.warning('请输入关系R或选择使用整除关系')
    return
  }

  // 清空之前的反馈和结果
  feedback.value = []
  results.value = []

  try {
    const request = {
      setAString: setAInput.value.trim(),
      setSString: setSInput.value.trim() || null,
      relationRString: useDivideRelation.value ? null : relationRInput.value.trim(),
      intTypeElement: elementType.value === 'int',
      useDivideRelation: useDivideRelation.value,
      ...options.value
    }

    console.log('PartialOrderInterface: 开始偏序关系计算', request)

    const response = await callBackendApi('/partial-order/calculate', {
      method: 'POST',
      body: JSON.stringify(request)
    })

    console.log('PartialOrderInterface: 收到计算结果', response)

    if (response.success) {
      const result = {
        success: response.success,
        type: response.type,
        formula: response.formula,
        setAString: response.setAString,
        setSString: response.setSString,
        relationRString: response.relationRString,
        intTypeElement: response.intTypeElement,
        useDivideRelation: response.useDivideRelation,
        isReflexive: response.reflexive,
        isAntisymmetric: response.antisymmetric,
        isTransitive: response.transitive,
        isPartialOrder: response.partialOrder,
        relationMatrix: response.relationMatrix,
        relationGraphUrl: response.relationGraphUrl,
        hasseDiagramUrl: response.hasseDiagramUrl,
        minimumElements: response.minimumElements,
        maximumElements: response.maximumElements,
        leastElement: response.leastElement,
        greatestElement: response.greatestElement,
        lowerBounds: response.lowerBounds,
        upperBounds: response.upperBounds,
        greatestLowerBound: response.greatestLowerBound,
        leastUpperBound: response.leastUpperBound,
        errorMessage: response.errorMessage,
        index: counter.value + 1
      }

      results.value.push(result)
      counter.value++

      // 添加成功反馈
      feedback.value.push({
        formula: response.formula,
        type: 'success',
        message: response.isPartialOrder ? '偏序关系分析完成' : '关系性质分析完成（非偏序关系）'
      })

      // 发送结果到主界面
      emit('partial-order-result', result)

      ElMessage.success('偏序关系计算完成')
    } else {
      feedback.value.push({
        formula: 'A = ?, R = ?',
        type: 'error',
        message: response.errorMessage || '偏序关系计算失败'
      })
    }
  } catch (error) {
    console.error('偏序关系计算失败:', error)
    feedback.value.push({
      formula: 'A = ?, R = ?',
      type: 'error',
      message: `计算失败: ${error.message}`
    })
  }
}

const generateRandomPartialOrder = async () => {
  try {
    console.log('PartialOrderInterface: 开始生成随机偏序关系')

    const response = await callBackendApi('/partial-order/generate', {
      method: 'POST'
    })

    console.log('PartialOrderInterface: 随机偏序关系生成结果', response)

    if (response.success) {
      setAInput.value = response.setA
      setSInput.value = response.setS
      relationRInput.value = response.relationR
      elementType.value = response.intTypeElement ? 'int' : 'char'
      useDivideRelation.value = response.useDivideRelation

      // 设置选项
      Object.assign(options.value, response.options)

      // 清除之前的结果
      feedback.value = []
      results.value = []

      ElMessage.success('已生成随机偏序关系')
    } else {
      ElMessage.error('生成随机偏序关系失败: ' + response.message)
    }
  } catch (error) {
    console.error('生成随机偏序关系失败:', error)
    ElMessage.error(`生成随机偏序关系失败: ${error.message}`)
  }
}

const clearInput = () => {
  setAInput.value = ''
  setSInput.value = ''
  relationRInput.value = ''
  ElMessage.info('已清空输入')
}

const checkInput = async () => {
  if (!setAInput.value.trim()) {
    ElMessage.warning('请输入集合A')
    return
  }

  if (!useDivideRelation.value && !relationRInput.value.trim()) {
    ElMessage.warning('请输入关系R或选择使用整除关系')
    return
  }

  feedback.value = []

  try {
    const response = await callBackendApi('/partial-order/validate', {
      method: 'POST',
      body: JSON.stringify({
        setAString: setAInput.value.trim(),
        setSString: setSInput.value.trim() || null,
        relationRString: useDivideRelation.value ? null : relationRInput.value.trim(),
        intTypeElement: elementType.value === 'int',
        useDivideRelation: useDivideRelation.value
      })
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
    setSInput.value = example.setS
    relationRInput.value = example.relationR
    elementType.value = example.elementType
    useDivideRelation.value = example.useDivideRelation

    // 设置选项
    Object.assign(options.value, example.options)

    console.log('PartialOrderInterface: 加载示例', exampleKey, example)
    ElMessage.info(`已加载示例：${exampleKey}`)
  }
}

// 监听元素类型变化
watch(elementType, (newValue) => {
  if (newValue === 'char') {
    useDivideRelation.value = false
  }
})

// 监听整除关系选项变化
watch(useDivideRelation, (newValue) => {
  if (newValue) {
    relationRInput.value = ''
  }
})

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
.partial-order-interface {
  display: flex;
  flex-direction: column;
  width: 100%;
}

/* 在大屏幕上完全自适应，无最大宽度限制 */
@media (min-width: 1200px) {
  .partial-order-interface {
    width: 100%;
    max-width: none;
  }
}

/* 在中等屏幕上设置合理的最大宽度 */
@media (max-width: 1199px) and (min-width: 900px) {
  .partial-order-interface {
    width: 100%;
    max-width: 1400px;
    margin: 0 auto;
  }
}

/* 在小屏幕上固定宽度，支持水平滚动 */
@media (max-width: 899px) {
  .partial-order-interface {
    width: 1400px;
    min-width: 1400px;
    max-width: 1400px;
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

.set-input {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
}

.input-hint {
  margin-top: 0.5rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

/* 内容显示区域 */
.content-display-area {
  padding: 1rem;
  background: #ffffff;
}

.feedback-section,
.results-section {
  margin-bottom: 2rem;
}

.feedback-content,
.results-content {
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.feedback-item,
.result-item {
  margin-bottom: 2rem;
  padding-bottom: 2rem;
  border-bottom: 1px solid #dee2e6;
}

.feedback-item:last-child,
.result-item:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.feedback-formula,
.result-formula {
  margin-bottom: 1rem;
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

.result-basic {
  margin-bottom: 1.5rem;
}

.relation-properties {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.visualization-section {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.relation-matrix,
.relation-graph,
.hasse-diagram {
  margin: 1rem 0;
}

.matrix-formula {
  margin: 1rem 0;
  font-size: 1rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  overflow-x: auto;
}

.graph-container {
  text-align: center;
  margin: 1rem 0;
}

.graph-image {
  max-width: 100%;
  height: auto;
  border: 1px solid #dee2e6;
  border-radius: 4px;
}

.extremal-elements,
.bounds-section {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.element-result,
.bound-result {
  margin: 1rem 0;
}

.element-formula,
.bound-formula {
  margin: 0.5rem 0;
  font-size: 1rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  overflow-x: auto;
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

/* 滚动条样式 */
.content-display-area::-webkit-scrollbar {
  width: 8px;
}

.content-display-area::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.content-display-area::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.content-display-area::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}

/* 公式容器确保不会溢出 */
.math-renderer {
  overflow-x: auto;
  max-width: 100%;
}

/* 内容区域响应式优化 */
@media (max-width: 768px) {
  .example-buttons .el-button {
    font-size: 0.8rem;
  }

  .relation-properties .el-col {
    margin-bottom: 0.5rem;
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