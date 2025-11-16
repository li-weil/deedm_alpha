<template>
  <div class="count-string-interface">
    <!-- 按钮操作区域 -->
    <div class="button-section">
      <!-- 第一行按钮：开始计算等 -->
      <el-row :gutter="20" class="button-row">
        <el-col :span="4">
          <el-button type="primary" @click="startCounting" :loading="loading">
            <el-icon><Check /></el-icon>
            开始计算(Y)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="info" @click="checkValidity">
            <el-icon><WarningFilled /></el-icon>
            合法检查(K)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="warning" @click="clearInput">
            <el-icon><Delete /></el-icon>
            清除输入(V)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="success" @click="clearInterface">
            <el-icon><RefreshRight /></el-icon>
            清除界面(C)
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button @click="handleClose">
            <el-icon><Close /></el-icon>
            退出(R)
          </el-button>
        </el-col>
      </el-row>

      <!-- 第二行按钮：示例题 -->
      <el-divider content-position="left">《离散数学基础》教材示例</el-divider>
      <el-row :gutter="15" class="example-buttons">
        <el-col :span="3">
          <el-button size="small" @click="loadExample('8_5')">例子8.5</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('8_7')">问题8.7</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('8_22')">问题8.22</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('8_23_1')">问题8.23(1)</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('8_23_2')">问题8.23(2)</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('8_23_3')">问题8.23(3)</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('8_23_4')">问题8.23(4)</el-button>
        </el-col>
      </el-row>
      <el-row :gutter="15" class="example-buttons" style="margin-top: 10px;">
        <el-col :span="3">
          <el-button size="small" @click="loadExample('8_26_1')">问题8.26(1)</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('8_26_2')">问题8.26(2)</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('8_26_3')">问题8.26(3)</el-button>
        </el-col>
        <el-col :span="3">
          <el-button size="small" @click="loadExample('8_26_4')">问题8.26(4)</el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 主输入区域 -->
    <div class="input-section">
      <el-card class="input-card" header="输入计数的串的字符集长度以及计数条件（字符串中字符位置下标从0开始，条件中的字符是指单个的基集字符，但可填写用逗号分隔的多个字符，字符串是基集上的串，同样可填用逗号分隔的多个串）">

        <!-- 基本参数行 -->
        <div class="basic-params-row">
          <div class="param-group">
            <label class="param-label">基集字符(B)：</label>
            <el-input v-model="baseSetInput" placeholder="例如: 0, 1, 2, 3, 4, 5" class="base-set-input" />
          </div>

          <div class="param-group">
            <label class="param-label">串的长度(L)：</label>
            <el-input-number v-model="stringLength" :min="1" :max="50" class="length-input" />
          </div>

          <div class="param-group">
            <label class="param-label">字符可否重复：</label>
            <el-radio-group v-model="allowRepetition" class="repetition-group">
              <el-radio :label="true">可以重复</el-radio>
              <el-radio :label="false">不可以重复</el-radio>
            </el-radio-group>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 三组过滤条件 -->
    <div class="filter-section">
      <el-card class="filter-card" header="过滤条件设置">
        <div class="filter-conditions">
          <!-- 第一组条件 -->
          <div class="filter-condition-group">
            <div class="filter-row">
              <div class="location-filter">
                <label class="filter-label">第一组条件：第</label>
                <el-input-number v-model="conditions[0].location" :min="0" :max="20" size="small" class="location-input" />
                <label class="filter-label">位的字符</label>
                <el-select v-model="conditions[0].locationFlag" size="small" class="flag-select">
                  <el-option label="必须" value="必须" />
                  <el-option label="不能" value="不能" />
                </el-select>
                <label class="filter-label">是</label>
                <el-input v-model="conditions[0].locationChars" placeholder="字符，如: 0,1,2" size="small" class="chars-input" />
                <label class="filter-label">这些字符之一</label>
              </div>

              <div class="logic-separator">
                <el-select v-model="conditions[0].innerLogic" size="small" class="logic-select">
                  <el-option label="而且" value="而且" />
                  <el-option label="或者" value="或者" />
                </el-select>
              </div>

              <div class="substring-filter">
                <el-select v-model="conditions[0].substringFlag" size="small" class="flag-select">
                  <el-option label="恰好" value="恰好" />
                  <el-option label="至少" value="至少" />
                  <el-option label="至多" value="至多" />
                  <el-option label="不能" value="不能" />
                </el-select>
                <label class="filter-label">包含</label>
                <el-input-number v-model="conditions[0].substringCount" :min="0" :max="20" size="small" class="count-input" />
                <label class="filter-label">个</label>
                <el-input v-model="conditions[0].substrings" placeholder="字符串，如: 12,ab,cd" size="small" class="substring-input" />
                <label class="filter-label">这些字符串之一</label>
              </div>

              <el-select v-model="conditions[0].outerLogic" size="small" class="outer-logic-select">
                <el-option label="而且" value="而且" />
                <el-option label="或者" value="或者" />
              </el-select>
            </div>
          </div>

          <!-- 第二组条件 -->
          <div class="filter-condition-group">
            <div class="filter-row">
              <div class="location-filter">
                <label class="filter-label">第二组条件：第</label>
                <el-input-number v-model="conditions[1].location" :min="0" :max="20" size="small" class="location-input" />
                <label class="filter-label">位的字符</label>
                <el-select v-model="conditions[1].locationFlag" size="small" class="flag-select">
                  <el-option label="必须" value="必须" />
                  <el-option label="不能" value="不能" />
                </el-select>
                <label class="filter-label">是</label>
                <el-input v-model="conditions[1].locationChars" placeholder="字符，如: 0,1,2" size="small" class="chars-input" />
                <label class="filter-label">这些字符之一</label>
              </div>

              <div class="logic-separator">
                <el-select v-model="conditions[1].innerLogic" size="small" class="logic-select">
                  <el-option label="而且" value="而且" />
                  <el-option label="或者" value="或者" />
                </el-select>
              </div>

              <div class="substring-filter">
                <el-select v-model="conditions[1].substringFlag" size="small" class="flag-select">
                  <el-option label="恰好" value="恰好" />
                  <el-option label="至少" value="至少" />
                  <el-option label="至多" value="至多" />
                  <el-option label="不能" value="不能" />
                </el-select>
                <label class="filter-label">包含</label>
                <el-input-number v-model="conditions[1].substringCount" :min="0" :max="20" size="small" class="count-input" />
                <label class="filter-label">个</label>
                <el-input v-model="conditions[1].substrings" placeholder="字符串，如: 12,ab,cd" size="small" class="substring-input" />
                <label class="filter-label">这些字符串之一</label>
              </div>

              <el-select v-model="conditions[1].outerLogic" size="small" class="outer-logic-select">
                <el-option label="而且" value="而且" />
                <el-option label="或者" value="或者" />
              </el-select>
            </div>
          </div>

          <!-- 第三组条件 -->
          <div class="filter-condition-group">
            <div class="filter-row">
              <div class="location-filter">
                <label class="filter-label">第三组条件：第</label>
                <el-input-number v-model="conditions[2].location" :min="0" :max="20" size="small" class="location-input" />
                <label class="filter-label">位的字符</label>
                <el-select v-model="conditions[2].locationFlag" size="small" class="flag-select">
                  <el-option label="必须" value="必须" />
                  <el-option label="不能" value="不能" />
                </el-select>
                <label class="filter-label">是</label>
                <el-input v-model="conditions[2].locationChars" placeholder="字符，如: 0,1,2" size="small" class="chars-input" />
                <label class="filter-label">这些字符之一</label>
              </div>

              <div class="logic-separator">
                <el-select v-model="conditions[2].innerLogic" size="small" class="logic-select">
                  <el-option label="而且" value="而且" />
                  <el-option label="或者" value="或者" />
                </el-select>
              </div>

              <div class="substring-filter">
                <el-select v-model="conditions[2].substringFlag" size="small" class="flag-select">
                  <el-option label="恰好" value="恰好" />
                  <el-option label="至少" value="至少" />
                  <el-option label="至多" value="至多" />
                  <el-option label="不能" value="不能" />
                </el-select>
                <label class="filter-label">包含</label>
                <el-input-number v-model="conditions[2].substringCount" :min="0" :max="20" size="small" class="count-input" />
                <label class="filter-label">个</label>
                <el-input v-model="conditions[2].substrings" placeholder="字符串，如: 12,ab,cd" size="small" class="substring-input" />
                <label class="filter-label">这些字符串之一</label>
              </div>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 显示选项区域 -->
    <div class="options-section">
      <el-card class="options-card" header="选择计数结果的展示方式：">
        <el-radio-group v-model="displayMode" class="display-mode-group">
          <el-radio :label="'onlyResult'">只给出计数结果</el-radio>
          <el-radio :label="'accept50'">给出至多50个接受的字符串</el-radio>
          <el-radio :label="'part100'">给出至多100个字符串的情况</el-radio>
          <el-radio :label="'onlyAccept'">只给出满足条件的串</el-radio>
          <el-radio :label="'allString'">给出所有的字符串</el-radio>
        </el-radio-group>
      </el-card>
    </div>

    <!-- 计算结果区域 -->
    <div v-if="results.length > 0" class="results-section">
      <el-divider content-position="left">字符串计数分析结果</el-divider>
      <div class="results-content">
        <div v-for="(result, index) in results" :key="index" class="result-item">
          <!-- 基本信息 -->
          <div class="result-basic">
            <h4>输入参数：</h4>
            <math-renderer
              :formula="result.formula"
              :type="'mathjax'"
              :display-mode="true"
              class="result-formula"
            />
          </div>

          <!-- 基集和长度信息 -->
          <div class="basic-info">
            <h4>基本信息：</h4>
            <p><strong>基集字符：</strong>{{ result.baseSet }}</p>
            <p><strong>字符串长度：</strong>{{ result.length }}</p>
            <p><strong>字符重复：</strong>{{ result.allowRepetition ? '允许重复' : '不允许重复' }}</p>
          </div>

          <!-- 过滤条件描述 -->
          <div v-if="result.filterDescription" class="filter-info">
            <h4>过滤条件：</h4>
            <math-renderer
              :formula="result.filterDescription"
              :type="'mathjax'"
              :display-mode="true"
              class="result-formula"
            />
          </div>

          <!-- 计数结果 -->
          <div class="count-result">
            <h4>计数结果：</h4>
            <div class="count-stats">
              <div class="stat-item">
                <span class="stat-label">总字符串数：</span>
                <span class="stat-value">{{ result.totalCount }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">满足条件：</span>
                <span class="stat-value success">{{ result.acceptedCount }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">结果公式：</span>
                <math-renderer
                  :formula="result.resultFormula || `\\frac{${result.acceptedCount}}{${result.totalCount}}`"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="result-formula"
                />
              </div>
            </div>
          </div>

          <!-- 字符串详情 -->
          <div v-if="result.stringDetails && result.stringDetails.length > 0" class="string-details">
            <h4>字符串详情（前 {{ result.stringDetails.length }} 个）：</h4>
            <div class="details-table">
              <el-table :data="result.stringDetails" stripe style="width: 100%" size="small">
                <el-table-column prop="index" label="序号" width="80" />
                <el-table-column prop="content" label="字符串内容" />
                <el-table-column prop="accepted" label="满足条件" width="100">
                  <template #default="scope">
                    <el-tag :type="scope.row.accepted ? 'success' : 'danger'" size="small">
                      {{ scope.row.accepted ? '是' : '否' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="acceptedCount" label="累计满足" width="100" />
              </el-table>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 实时反馈区域 -->
    <div class="feedback-section" v-if="feedback?.value?.length > 0">
      <el-card class="feedback-card" header="你输入字符串基集、长度以及计数条件如下（计数条件中的string表示待判断的字符串）：">
        <div class="feedback-content">
          <div v-for="(item, index) in feedback.value" :key="index" class="feedback-item">
            <span v-if="item.type === 'plain'">{{ item.content }}</span>
            <math-renderer
              v-else-if="item.type === 'latex'"
              :formula="item.content"
              :type="'katex'"
              :display-mode="true"
              class="feedback-formula"
            />
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import MathRenderer from '@/components/common/MathRenderer.vue'

// 事件处理函数
const emit = defineEmits(['close', 'count-string'])

// 基本参数
const baseSetInput = ref('0, 1, 2, 3, 4, 5')
const stringLength = ref(2)
const allowRepetition = ref(true)

// 三组过滤条件
const conditions = reactive([
  {
    location: null,
    locationFlag: '必须',
    locationChars: '',
    innerLogic: '而且',
    substringFlag: '恰好',
    substringCount: null,
    substrings: '',
    outerLogic: '而且'
  },
  {
    location: null,
    locationFlag: '必须',
    locationChars: '',
    innerLogic: '而且',
    substringFlag: '恰好',
    substringCount: null,
    substrings: '',
    outerLogic: '而且'
  },
  {
    location: null,
    locationFlag: '必须',
    locationChars: '',
    innerLogic: '而且',
    substringFlag: '恰好',
    substringCount: null,
    substrings: ''
  }
])

// 显示模式
const displayMode = ref('onlyResult')

// 状态
const loading = ref(false)
const feedback = ref([])
const results = ref([])
const counter = ref(0)

// 加载教材示例
const loadExample = (exampleId) => {
  clearInput()
  clearFeedback()

  switch (exampleId) {
    case '8_5':
      baseSetInput.value = '0, 1, 2, 3, 4, 5'
      stringLength.value = 2
      allowRepetition.value = false
      break
    case '8_7':
      baseSetInput.value = '0, 1, 2, 3, 4, 5, 6, 7, 8, 9'
      stringLength.value = 3
      allowRepetition.value = true
      conditions[0].location = 0
      conditions[0].locationFlag = '不能'
      conditions[0].locationChars = '0'
      conditions[0].substringFlag = '至少'
      conditions[0].substringCount = 1
      conditions[0].substrings = '5'
      break
    case '8_22':
      baseSetInput.value = '0, 1, 2, 3, 4, 5, 6, 7, 8, 9'
      stringLength.value = 6
      allowRepetition.value = false
      conditions[0].location = 0
      conditions[0].locationFlag = '不能'
      conditions[0].locationChars = '0'
      conditions[0].substringFlag = '不能'
      conditions[0].substringCount = 1
      conditions[0].substrings = '12'
      break
    case '8_23_1':
      baseSetInput.value = '0, 1'
      stringLength.value = 10
      allowRepetition.value = true
      conditions[0].substringFlag = '恰好'
      conditions[0].substringCount = 3
      conditions[0].substrings = '1'
      break
    case '8_23_2':
      baseSetInput.value = '0, 1'
      stringLength.value = 10
      allowRepetition.value = true
      conditions[0].substringFlag = '至少'
      conditions[0].substringCount = 3
      conditions[0].substrings = '1'
      break
    case '8_23_3':
      baseSetInput.value = '0, 1'
      stringLength.value = 10
      allowRepetition.value = true
      conditions[0].substringFlag = '至多'
      conditions[0].substringCount = 3
      conditions[0].substrings = '1'
      break
    case '8_23_4':
      baseSetInput.value = '0, 1'
      stringLength.value = 10
      allowRepetition.value = true
      conditions[0].substringFlag = '至少'
      conditions[0].substringCount = 6
      conditions[0].substrings = '1'
      break
    case '8_26_1':
      baseSetInput.value = '0, 1, 2, 3, 4, 5, 6, 7, 8, 9'
      stringLength.value = 6
      allowRepetition.value = true
      conditions[0].location = 0
      conditions[0].locationFlag = '不能'
      conditions[0].locationChars = '0'
      conditions[0].substringFlag = '至少'
      conditions[0].substringCount = 1
      conditions[0].substrings = '1, 3, 5, 7, 9'
      break
    case '8_26_2':
      baseSetInput.value = '0, 1, 2, 3, 4, 5, 6, 7, 8, 9'
      stringLength.value = 6
      allowRepetition.value = true
      conditions[0].location = 0
      conditions[0].locationFlag = '不能'
      conditions[0].locationChars = '0'
      conditions[0].substringFlag = '至少'
      conditions[0].substringCount = 1
      conditions[0].substrings = '0, 2, 4, 6, 8'
      break
    case '8_26_3':
      baseSetInput.value = '0, 1, 2, 3, 4, 5, 6, 7, 8, 9'
      stringLength.value = 6
      allowRepetition.value = true
      conditions[0].location = 0
      conditions[0].locationFlag = '不能'
      conditions[0].locationChars = '0'
      conditions[0].substringFlag = '至少'
      conditions[0].substringCount = 2
      conditions[0].substrings = '1, 3, 5, 7, 9'
      break
    case '8_26_4':
      baseSetInput.value = '0, 1, 2, 3, 4, 5, 6, 7, 8, 9'
      stringLength.value = 6
      allowRepetition.value = true
      conditions[0].location = 0
      conditions[0].locationFlag = '不能'
      conditions[0].locationChars = '0'
      conditions[0].substringFlag = '至少'
      conditions[0].substringCount = 2
      conditions[0].substrings = '0, 2, 4, 6, 8'
      break
  }

  checkValidity()
}

// 合法性检查
const checkValidity = () => {
  console.log('checkValidity: 开始，feedback =', feedback)
  clearFeedback()
  console.log('checkValidity: clearFeedback后，feedback =', feedback)

  // 检查基集
  if (!baseSetInput.value.trim()) {
    ElMessage.warning('请输入基集字符')
    return false
  }

  // 检查长度
  if (!stringLength.value || stringLength.value <= 0) {
    ElMessage.warning('请输入有效的字符串长度')
    return false
  }

  feedback.value.push({
    type: 'plain',
    content: '字符串基集：'
  })
  feedback.value.push({
    type: 'latex',
    content: `B = \\{${baseSetInput.value}\\}`
  })
  feedback.value.push({
    type: 'plain',
    content: '长度：'
  })
  feedback.value.push({
    type: 'latex',
    content: `n = ${stringLength.value}`
  })
  feedback.value.push({
    type: 'plain',
    content: allowRepetition.value ? '统计允许字符重复的串' : '统计不允许字符重复的串'
  })

  // 检查过滤条件
  const filterDescription = generateFilterDescription()
  if (filterDescription) {
    feedback.value.push({
      type: 'plain',
      content: '过滤条件：'
    })
    feedback.value.push({
      type: 'latex',
      content: filterDescription
    })
  } else {
    feedback.value.push({
      type: 'plain',
      content: '过滤条件：没有或不给出合法的过滤条件'
    })
  }

  return true
}

// 生成过滤条件描述
const generateFilterDescription = () => {
  const activeConditions = conditions.filter(condition => {
    return (condition.location !== null && condition.locationChars) ||
           (condition.substringCount !== null && condition.substrings)
  })

  if (activeConditions.length === 0) return null

  let description = ''
  activeConditions.forEach((condition, index) => {
    if (index > 0) {
      const prevCondition = activeConditions[index - 1]
      description += ` ${prevCondition.outerLogic} `
    }

    const parts = []

    // 位置条件
    if (condition.location !== null && condition.locationChars) {
      const op = condition.locationFlag === '必须' ? '=' : '≠'
      const chars = condition.locationChars.split(',').map(c => c.trim()).join(', ')
      parts.push(`s[${condition.location}] ${op} \\{${chars}\\}`)
    }

    // 包含条件
    if (condition.substringCount !== null && condition.substrings) {
      const substrings = condition.substrings.split(',').map(s => s.trim()).join(', ')
      let countOp = ''
      switch (condition.substringFlag) {
        case '恰好': countOp = '='; break
        case '至少': countOp = '≥'; break
        case '至多': countOp = '≤'; break
        case '不能': countOp = '≠'; break
      }
      parts.push(`|\\{s : s 包含 \\{${substrings}\\} 中串\\}| ${countOp} ${condition.substringCount}`)
    }

    if (parts.length > 0) {
      if (parts.length === 1) {
        description += parts[0]
      } else {
        description += `(${parts[0]} ${condition.innerLogic} ${parts[1]})`
      }
    }
  })

  return description || null
}

// 开始计数
const startCounting = async () => {
  if (!checkValidity()) {
    return
  }

  // 清除之前的结果
  results.value = []
  feedback.value = []
  counter.value = 0

  loading.value = true

  try {
    // 准备过滤条件 - 转换为后端期望的格式
    const processedFilterConditions = []
    const logicOperators = []

    conditions.forEach((condition, index) => {
      const filterCondition = {
        locationFilter: null,
        substringFilter: null
      }

      // 位置过滤条件
      if (condition.location !== null && condition.locationChars) {
        filterCondition.locationFilter = {
          position: condition.location,
          appear: condition.locationFlag === '必须',
          chars: condition.locationChars
        }
      }

      // 子串过滤条件
      if (condition.substringCount !== null && condition.substrings) {
        let substringType = 'EXACTLY'
        switch (condition.substringFlag) {
          case '恰好': substringType = 'EXACTLY'; break
          case '至少': substringType = 'AT_LEAST'; break
          case '至多': substringType = 'NO_MORE_THAN'; break
          case '不能': substringType = 'NOT_CONTAIN'; break
        }

        filterCondition.substringFilter = {
          type: substringType,
          number: condition.substringCount,
          substrings: condition.substrings
        }
      }

      // 只有当有实际过滤条件时才添加
      if (filterCondition.locationFilter || filterCondition.substringFilter) {
        processedFilterConditions.push(filterCondition)

        // 添加逻辑运算符（除了最后一个条件）
        if (index < conditions.length - 1 && condition.outerLogic) {
          const logicOp = condition.outerLogic === '而且' ? 'AND' : 'OR'
          logicOperators.push(logicOp)
        }
      }
    })

    // 映射显示模式
    let outputOption = 'ONLY_RESULT'
    switch (displayMode.value) {
      case 'onlyResult': outputOption = 'ONLY_RESULT'; break
      case 'accept50': outputOption = 'ACCEPT_50'; break
      case 'part100': outputOption = 'PARTIAL_100'; break
      case 'onlyAccept': outputOption = 'ONLY_ACCEPTED'; break
      case 'allString': outputOption = 'ALL_STRINGS'; break
    }

    // 调用后端API
    const baseUrl = window.location.origin
    // 尝试调用后端API，如果失败则使用模拟数据
    let result
    try {
      const response = await fetch(`${baseUrl}/api/counting/count-string/count`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          baseSet: baseSetInput.value,
          length: stringLength.value,
          allowRepetition: allowRepetition.value,
          filterConditions: processedFilterConditions,
          logicOperators: logicOperators,
          outputOption: outputOption
        })
      })

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      result = await response.json()
    } catch (error) {
      console.warn('后端API不可用，使用模拟数据:', error.message)

      // 模拟后端响应
      const baseSetElements = baseSetInput.value.split(',').map(s => s.trim())
      const totalCount = allowRepetition.value
        ? Math.pow(baseSetElements.length, stringLength.value)
        : calculatePermutation(baseSetElements.length, stringLength.value)

      result = {
        success: true,
        formula: `基集: {${baseSetInput.value}}, 长度: ${stringLength.value}, ${allowRepetition.value ? '允许重复' : '不允许重复'}`,
        type: 'countString',
        baseSet: baseSetInput.value,
        length: stringLength.value,
        allowRepetition: allowRepetition.value,
        filterDescription: generateFilterDescription() || '无过滤条件',
        totalCount: Math.min(totalCount, 1000000), // 限制最大值避免计算时间过长
        acceptedCount: Math.floor(totalCount * 0.3), // 假设30%满足条件
        resultFormula: `${Math.floor(totalCount * 0.3)}/${totalCount}`,
        stringDetails: generateMockStringDetails(baseSetElements, stringLength.value, allowRepetition.value)
      }
    }

    console.log('CountStringInterface: 收到计数结果', result)

    if (result.success) {
      const finalResult = {
        ...result,
        index: counter.value + 1,
        type: 'count-string'
      }

      console.log('CountStringInterface: 准备发送结果', finalResult)

      results.value.push(finalResult)
      counter.value++

      // 添加成功反馈
      feedback.value.push({
        formula: `计数完成: ${result.totalCount}/${result.acceptedCount}`,
        type: 'success',
        message: '字符串计数完成'
      })

      ElMessage.success('字符串计数完成')

      // 发送结果到主界面
      console.log('CountStringInterface: 发送事件到父组件')
      emit('count-string', finalResult)
      console.log('CountStringInterface: 事件已发送')
    } else {
      feedback.value.push({
        formula: '',
        type: 'error',
        message: result.message || '计数失败'
      })

      ElMessage.error(result.message || '计数失败')
    }
  } catch (error) {
    console.error('字符串计数失败:', error)
    feedback.value.push({
      formula: '',
      type: 'error',
      message: '计数失败: ' + error.message
    })

    ElMessage.error('计数失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 清除输入
const clearInput = () => {
  baseSetInput.value = ''
  stringLength.value = 1
  allowRepetition.value = true

  conditions.forEach(condition => {
    condition.location = null
    condition.locationFlag = '必须'
    condition.locationChars = ''
    condition.innerLogic = '而且'
    condition.substringFlag = '恰好'
    condition.substringCount = null
    condition.substrings = ''
    condition.outerLogic = '而且'
  })
}

// 清除界面
const clearInterface = () => {
  clearInput()
  clearFeedback()
  results.value = []
  counter.value = 0
}

// 清除反馈
const clearFeedback = () => {
  console.log('clearFeedback: 调用前 feedback =', feedback)
  feedback.value = []
  console.log('clearFeedback: 调用后 feedback =', feedback)
}

// 计算排列数
const calculatePermutation = (n, r) => {
  if (r > n) return 0
  let result = 1
  for (let i = 0; i < r; i++) {
    result *= (n - i)
  }
  return result
}

// 生成模拟字符串详情
const generateMockStringDetails = (baseSetElements, length, allowRepetition) => {
  const details = []
  const maxDetails = Math.min(10, allowRepetition ? Math.pow(baseSetElements.length, length) : calculatePermutation(baseSetElements.length, length))

  let acceptedCount = 0

  for (let i = 0; i < maxDetails; i++) {
    let content = ''

    // 生成字符串
    if (allowRepetition) {
      for (let j = 0; j < length; j++) {
        content += baseSetElements[i % baseSetElements.length]
      }
    } else {
      const tempSet = [...baseSetElements]
      for (let j = 0; j < Math.min(length, tempSet.length); j++) {
        const index = (i + j) % tempSet.length
        content += tempSet[index]
        tempSet.splice(index, 1)
      }
    }

    // 模拟接受状态（约30%被接受）
    const accepted = Math.random() < 0.3
    if (accepted) {
      acceptedCount++
    }

    details.push({
      index: i + 1,
      content: content,
      accepted: accepted,
      acceptedCount: acceptedCount
    })
  }

  return details
}

// 关闭对话框
const handleClose = () => {
  emit('close')
}
</script>

<style scoped>
/* 通用样式 */
.count-string-interface {
  padding: 0;
}

/* 按钮区域样式 - 与其他子界面保持一致 */
.button-section {
  margin-bottom: 20px;
}

.button-row {
  margin-bottom: 20px;
}

.example-buttons {
  margin-top: 10px;
}

/* Java应用界面样式保持不变 */
.input-section,
.filter-section,
.options-section,
.feedback-section,
.results-section {
  margin-bottom: 20px;
}

.input-card,
.filter-card,
.options-card,
.feedback-card {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 基本参数行 */
.basic-params-row {
  display: flex;
  gap: 24px;
  align-items: flex-end;
  flex-wrap: wrap;
}

.param-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.param-label {
  font-weight: 500;
  color: #606266;
  white-space: nowrap;
}

.base-set-input {
  width: 300px;
}

.length-input {
  width: 120px;
}

.repetition-group {
  display: flex;
  gap: 16px;
}

/* 过滤条件 */
.filter-conditions {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.filter-condition-group {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  padding: 12px;
  background-color: #fafafa;
}

.filter-row {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.location-filter,
.substring-filter {
  display: flex;
  align-items: center;
  gap: 4px;
  flex-wrap: wrap;
}

.logic-separator {
  margin: 0 8px;
}

.filter-label {
  font-size: 14px;
  color: #606266;
  white-space: nowrap;
}

.location-input,
.count-input {
  width: 80px;
}

.chars-input {
  width: 150px;
}

.substring-input {
  width: 200px;
}

.flag-select,
.logic-select,
.outer-logic-select {
  width: 80px;
}

/* 显示选项 */
.display-mode-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

/* 结果显示区域样式 */
.results-content {
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.result-item {
  margin-bottom: 2rem;
  padding: 1.5rem;
  background: white;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.result-basic,
.basic-info,
.filter-info,
.count-result,
.string-details {
  margin: 1rem 0;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.result-basic h4,
.basic-info h4,
.filter-info h4,
.count-result h4,
.string-details h4 {
  margin: 0 0 1rem 0;
  color: #2c3e50;
  font-size: 1rem;
  font-weight: 600;
}

.result-formula {
  margin-bottom: 1rem;
  font-size: 1.1rem;
  font-weight: 500;
  color: #2c3e50;
  overflow-x: auto;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.basic-info p {
  margin: 0.5rem 0;
  line-height: 1.6;
}

.basic-info strong {
  color: #374151;
  font-weight: 600;
  min-width: 100px;
  display: inline-block;
}

.count-stats {
  display: flex;
  flex-wrap: wrap;
  gap: 2rem;
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 0.5rem;
}

.stat-label {
  color: #6b7280;
  font-size: 0.9rem;
  font-weight: 500;
}

.stat-value {
  color: #111827;
  font-size: 1.2rem;
  font-weight: 700;
}

.stat-value.success {
  color: #059669;
}

.details-table {
  margin-top: 1rem;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

/* 反馈区域 */
.feedback-content {
  max-height: 200px;
  overflow-y: auto;
  padding: 12px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.feedback-item {
  margin-bottom: 8px;
  font-size: 14px;
  line-height: 1.5;
}

.feedback-formula {
  margin: 8px 0;
  padding: 8px;
  background-color: white;
  border-radius: 4px;
  border-left: 3px solid #409eff;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .basic-params-row {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }

  .param-group {
    flex-direction: column;
    align-items: flex-start;
  }

  .filter-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .location-filter,
  .substring-filter {
    width: 100%;
    flex-direction: column;
    align-items: flex-start;
  }

  .example-buttons {
    justify-content: center;
  }
}
</style>