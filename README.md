# Deedm 项目 Web 迁移指南

## 项目概述

Deedm 是一个数学教学辅助工具，包含组合计数、命题逻辑、数论、图论等数学模块。原项目为Java桌面应用，计划迁移为基于Vue+SpringBoot的Web应用。

## 现有项目结构分析

### 原项目核心模块
- **counting/** - 组合计数模块 (12个文件)
  - StringCounter.java: 字符串计数核心类
  - RecurrenceCaculator.java: 递推关系计算器
  - 各种计算器和求解器

- **proplogic/** - 命题逻辑模块 (37个文件)
  - 核心类:
    - Formula.java: 逻辑表达式抽象基类
    - FormulaBuilder.java: 公式构建器（解析器）
    - TruthAssignment.java: 真值指派类
    - FormulaTruthTable.java: 真值表生成器
  - formula/: 逻辑公式实现
    - AtomicFormula.java: 原子公式（变量）
    - AndFormula.java: 合取公式（AND）
    - OrFormula.java: 析取公式（OR）
    - NegFormula.java: 否定公式（NOT）
    - ImpFormula.java: 蕴含公式（IMP）
    - BiImpFormula.java: 双蕴含公式（EQ）
    - Symbol.java: 逻辑符号常量
  - normalFormula/: 范式计算
    - NormalFormulaCalculator.java: 范式计算器
    - ConjunctiveNormalFormula.java: 合取范式
    - DisjunctiveNormalFormula.java: 析取范式
    - ResolutionSolver.java: 消解法求解器
  - equiv/: 等价演算
    - EquivCalculusChecker.java: 等价性检查器
    - EquivCalculusRecorder.java: 演算步骤记录器
  - reason/: 逻辑推理
    - LogicReasoning.java: 逻辑推理引擎
    - ReasonArgumentChecker.java: 推理论证检查器

- **algebra/** - 代数模块
- **disbook/** - 离散数学算法模块
- **graph/** - 图论模块
- **setrelfun/** - 集合与函数模块
- **dataTable/** - 数据表格模块
- **graphicAnimation/** - 图形动画模块

### 依赖项
- jlatexmath-0.9.6.jar (数学公式渲染)
- Java 8 运行环境

## Vue+SpringBoot Web 架构设计

### 整体架构
```
deedm-web/
├── backend/                    # SpringBoot 后端
│   ├── src/main/java/
│   │   └── com/deedm/
│   │       ├── config/         # 配置类
│   │       ├── controller/     # REST 控制器
│   │       ├── service/        # 业务逻辑层
│   │       ├── model/          # 数据模型
│   │       ├── dto/           # 数据传输对象
│   │       └── DeedmApplication.java
│   ├── src/main/resources/
│   │   ├── application.yml    # 应用配置
│   │   └── static/            # 静态资源
│   └── pom.xml                # Maven 配置
│
├── frontend/                   # Vue 前端
│   ├── src/
│   │   ├── components/         # Vue 组件
│   │   ├── views/             # 页面视图
│   │   ├── router/            # 路由配置
│   │   ├── store/             # 状态管理
│   │   ├── utils/             # 工具函数
│   │   └── App.vue
│   ├── package.json           # npm 配置
│   └── vue.config.js          # Vue 配置
│
└── README.md                  # 项目文档
```

## 主要迁移文件清单

### 需要重构的核心文件

#### 后端迁移文件
1. **StringCounter.java** → CountingService.java + CountingController.java
2. **RecurrenceCaculator.java** → RecurrenceService.java
3. **Formula.java** → LogicFormulaService.java
4. **TruthAssignment.java** → TruthTableService.java
5. **MathSolver.java** → MathProblemService.java

#### 前端需要创建的组件
1. **计数模块组件**
   - StringCounter.vue (字符串计数器)
   - RecurrenceCalculator.vue (递推关系计算器)
   - CombinationCalculator.vue (组合计算器)

2. **逻辑模块组件**
   - FormulaBuilder.vue (公式构建器)
   - TruthTableGenerator.vue (真值表生成器)
   - LogicValidator.vue (逻辑验证器)

3. **通用组件**
   - MathRenderer.vue (数学公式渲染器)
   - ResultDisplay.vue (结果显示器)
   - InputForm.vue (输入表单)

### 迁移策略

#### 阶段一：后端API化 (1-2周)
1. 将现有的核心算法类转换为Spring Service
2. 创建REST控制器提供API接口
3. 设计统一的数据传输对象(DTO)
4. 集成数学公式渲染库

#### 阶段二：前端界面开发 (2-3周)
1. 创建Vue项目基础框架
2. 实现各功能模块的UI组件
3. 集成数学公式显示库 (MathJax/KaTeX)
4. 实现前后端数据交互

#### 阶段三：集成测试 (1周)
1. 单元测试覆盖核心算法
2. 集成测试验证API接口
3. 前端组件测试
4. 端到端测试



## 关键迁移挑战

### 1. 数学公式渲染
- **挑战**: 原项目使用jlatexmath，Web端需要替代方案
- **解决方案**: 前端使用MathJax/KaTeX，后端生成LaTeX字符串

### 2. 复杂状态管理
- **挑战**: 桌面应用状态管理简单，Web应用需要更复杂的状态同步
- **解决方案**: 使用Pinia进行状态管理，WebSocket实现实时更新

### 3. 用户交互体验
- **挑战**: 将桌面应用的交互方式适配Web界面
- **解决方案**: 采用响应式设计，提供键盘快捷键支持

### 4. 性能优化
- **挑战**: 复杂计算可能导致页面卡顿
- **解决方案**: Web Worker处理密集计算，分页和懒加载



---

## 命题逻辑模块详细分析

### 模块功能概述
命题逻辑模块提供完整的命题逻辑计算和推理功能，包括：
- 逻辑表达式解析和构建
- 真值表生成和分析
- 逻辑等价性检查
- 范式转换（合取范式、析取范式）
- 逻辑推理和论证验证

### 核心类作用详解

#### 1. 公式体系（formula包）
- **Formula.java**: 抽象基类，定义所有逻辑表达式的公共接口
  - 提供真值计算、子公式获取、语法比较等核心方法
  - 支持LaTeX输出，为数学公式渲染提供支持
  - 实现递归的公式遍历和操作

- **FormulaBuilder.java**: 公式解析和构建器
  - 支持字符串到公式对象的转换
  - 处理LaTeX格式的公式输入
  - 实现算符优先级解析算法
  - 提供随机公式生成功能

- **AtomicFormula.java**: 原子公式（命题变量）
- **AndFormula.java/OrFormula.java**: 逻辑合取/析取运算
- **NegFormula.java**: 逻辑否定运算
- **ImpFormula.java/BiImpFormula.java**: 蕴含/双蕴含运算

#### 2. 真值处理
- **TruthAssignment.java**: 真值指派，表示变量到真值的映射
- **TruthAssignmentFunction.java**: 真值指派函数，管理多个变量的真值组合
- **FormulaTruthTable.java**: 真值表生成器
  - 自动枚举所有可能的真值指派
  - 计算公式在各种真值指派下的结果
  - 生成详细的真值表和简化的真值表
  - 判断公式的永真性、可满足性等性质

#### 3. 范式计算（normalFormula包）
- **NormalFormulaCalculator.java**: 范式转换计算器
  - 将任意公式转换为合取范式(CNF)和析取范式(DNF)
  - 支持等价演算步骤的记录和展示
- **ConjunctiveNormalFormula.java**: 合取范式实现
- **DisjunctiveNormalFormula.java**: 析取范式实现
- **ResolutionSolver.java**: 基于消解法的可满足性求解器

#### 4. 等价演算（equiv包）
- **EquivCalculusChecker.java**: 检查两个公式是否逻辑等价
- **EquivCalculusRecorder.java**: 记录等价演算的每个步骤
- **EquivCalculusStep.java**: 演算步骤的数据结构

#### 5. 逻辑推理（reason包）
- **LogicReasoning.java**: 逻辑推理引擎
- **ReasonArgumentChecker.java**: 推理论证的验证器

---

## 命题逻辑模块前后端分离实现方案

### 后端架构设计（SpringBoot）

#### 1. 服务层架构
```
backend/src/main/java/com/deedm/logic/
├── service/
│   ├── FormulaService.java           # 公式服务
│   ├── TruthTableService.java        # 真值表服务
│   ├── NormalFormulaService.java     # 范式计算服务
│   ├── EquivalenceService.java       # 等价性检查服务
│   └── ReasoningService.java         # 逻辑推理服务
├── model/
│   ├── Formula.java                  # 公式数据模型
│   ├── TruthAssignment.java          # 真值指派模型
│   └── TruthTable.java              # 真值表模型
├── dto/
│   ├── FormulaParseDTO.java         # 公式解析DTO
│   ├── TruthTableDTO.java           # 真值表DTO
│   ├── NormalFormDTO.java           # 范式转换DTO
│   └── EquivalenceCheckDTO.java     # 等价性检查DTO
└── controller/
    ├── LogicController.java         # 逻辑模块控制器
    └── FormulaController.java       # 公式操作控制器
```



### 前端架构设计（Vue 3）

#### 1. 组件架构
```
frontend/src/
├── components/
│   ├── logic/
│   │   ├── FormulaBuilder.vue        # 公式构建器组件
│   │   ├── TruthTable.vue           # 真值表组件
│   │   ├── FormulaRenderer.vue      # 公式渲染器
│   │   ├── NormalFormConverter.vue  # 范式转换器
│   │   ├── EquivalenceChecker.vue   # 等价性检查器
│   │   └── ReasoningEngine.vue       # 推理引擎组件
│   ├── common/
│   │   ├── MathRenderer.vue         # 数学公式渲染
│   │   ├── ResultDisplay.vue        # 结果显示
│   │   └── LoadingSpinner.vue       # 加载动画
├── views/
│   ├── LogicView.vue               # 逻辑模块主视图
│   ├── FormulaBuilderView.vue      # 公式构建视图
│   ├── TruthTableView.vue          # 真值表视图
│   └── NormalFormView.vue          # 范式转换视图
├── store/
│   ├── logic/
│   │   ├── formulaStore.js         # 公式状态管理
│   │   ├── truthTableStore.js      # 真值表状态管理
│   │   └── calculationStore.js     # 计算状态管理
│   └── common/
│       └── loadingStore.js         # 加载状态管理
└── utils/
    ├── formulaParser.js            # 公式解析工具
    ├── latexConverter.js           # LaTeX转换工具
    └── truthTableGenerator.js      # 真值表生成工具
```

#### 2. 核心组件设计

**FormulaBuilder.vue**
```vue
<template>
  <div class="formula-builder">
    <div class="input-section">
      <el-input
        v-model="formulaInput"
        placeholder="输入逻辑公式，如：(p AND q) OR r"
        @input="handleInputChange"
      />
      <el-radio-group v-model="inputFormat">
        <el-radio label="text">文本格式</el-radio>
        <el-radio label="latex">LaTeX格式</el-radio>
      </el-radio-group>
    </div>

    <div class="toolbar">
      <el-button-group>
        <el-button @click="insertOperator('AND')">AND</el-button>
        <el-button @click="insertOperator('OR')">OR</el-button>
        <el-button @click="insertOperator('NOT')">NOT</el-button>
        <el-button @click="insertOperator('IMP')">→</el-button>
        <el-button @click="insertOperator('EQ')">↔</el-button>
      </el-button-group>
      <el-button @click="insertVariable">变量</el-button>
      <el-button @click="generateRandom">随机生成</el-button>
    </div>

    <div class="preview-section">
      <div class="formula-preview">
        <FormulaRenderer :formula="parsedFormula" />
      </div>
      <div class="action-buttons">
        <el-button
          type="primary"
          @click="generateTruthTable"
          :disabled="!isValidFormula"
        >
          生成真值表
        </el-button>
        <el-button
          @click="convertToNormalForm"
          :disabled="!isValidFormula"
        >
          转换为范式
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useFormulaStore } from '@/store/logic/formulaStore'
import FormulaRenderer from './FormulaRenderer.vue'

const formulaStore = useFormulaStore()
const formulaInput = ref('')
const inputFormat = ref('text')
const parsedFormula = computed(() => formulaStore.currentFormula)
const isValidFormula = computed(() => formulaStore.isValidFormula)

// 处理输入变化
const handleInputChange = (value) => {
  formulaStore.parseFormula(value, inputFormat.value)
}

// 插入运算符
const insertOperator = (operator) => {
  // 实现运算符插入逻辑
}

// 生成真值表
const generateTruthTable = () => {
  formulaStore.generateTruthTable()
}

// 转换为范式
const convertToNormalForm = () => {
  formulaStore.convertToNormalForm()
}
</script>
```

**TruthTable.vue**
```vue
<template>
  <div class="truth-table">
    <div class="table-controls">
      <el-switch
        v-model="showDetailed"
        active-text="详细真值表"
        inactive-text="简化真值表"
      />
      <el-button @click="exportTable">导出表格</el-button>
    </div>

    <div class="formula-info">
      <h3>公式: <FormulaRenderer :formula="formula" /></h3>
      <p>类型: {{ formulaType }}</p>
    </div>

    <div class="table-container">
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column
          v-for="variable in variables"
          :key="variable"
          :label="variable"
          prop="variables[variable]"
        />
        <el-table-column
          label="结果"
          prop="result"
          :formatter="formatResult"
        />
      </el-table>
    </div>

    <div class="statistics">
      <el-card>
        <h4>统计信息</h4>
        <p>总行数: {{ totalRows }}</p>
        <p>满足条件: {{ satisfyingCount }}</p>
        <p>不满足: {{ notSatisfyingCount }}</p>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useTruthTableStore } from '@/store/logic/truthTableStore'
import FormulaRenderer from './FormulaRenderer.vue'

const truthTableStore = useTruthTableStore()
const showDetailed = ref(false)

const formula = computed(() => truthTableStore.formula)
const variables = computed(() => truthTableStore.variables)
const tableData = computed(() => truthTableStore.tableData)
const formulaType = computed(() => truthTableStore.formulaType)
const totalRows = computed(() => tableData.value.length)
const satisfyingCount = computed(() =>
  tableData.value.filter(row => row.result).length
)
const notSatisfyingCount = computed(() =>
  totalRows.value - satisfyingCount.value
)

// 格式化结果显示
const formatResult = (row, column, value) => {
  return value ? 'T' : 'F'
}

// 导出表格
const exportTable = () => {
  // 实现导出逻辑
}
</script>
```

#### 3. 状态管理（Pinia）

**formulaStore.js**
```javascript
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useFormulaStore = defineStore('formula', () => {
  const currentFormula = ref(null)
  const formulaHistory = ref([])
  const isLoading = ref(false)
  const error = ref(null)

  const isValidFormula = computed(() => currentFormula.value !== null)

  // 解析公式
  async function parseFormula(formulaString, format = 'text') {
    try {
      isLoading.value = true
      const response = await fetch('/api/logic/formula/parse', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ formula: formulaString, format })
      })
      const data = await response.json()
      currentFormula.value = data
      return data
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      isLoading.value = false
    }
  }

  // 生成随机公式
  async function generateRandomFormula(variableCount, complexity) {
    const response = await fetch('/api/logic/formula/generate', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ variableCount, complexity })
    })
    const data = await response.json()
    currentFormula.value = data
    return data
  }

  return {
    currentFormula,
    formulaHistory,
    isLoading,
    error,
    isValidFormula,
    parseFormula,
    generateRandomFormula
  }
})
```

### 数据流设计

#### 1. API调用流程
```
用户输入 → Vue组件验证 → 调用API → SpringBoot处理 → 返回结果 → 更新状态 → 重新渲染
```

#### 2. 实时计算优化
- 防抖处理：用户输入时延迟API调用
- 缓存机制：缓存已计算的公式和真值表
- 增量更新：只更新变化的部分

#### 3. 错误处理
- 输入验证：前端验证公式格式
- 错误提示：友好的错误信息展示
- 降级处理：服务不可用时的本地计算

### 性能优化策略

#### 1. 前端优化
- 虚拟滚动：处理大量真值表数据
- 计算缓存：缓存中间计算结果
- 组件懒加载：按需加载功能模块

#### 2. 后端优化
- 算法优化：优化真值表生成算法
- 并发处理：多线程处理复杂计算
- 结果缓存：Redis缓存计算结果

#### 3. 网络优化
- 数据压缩：压缩传输的JSON数据
- 增量更新：只传输变化的数据
- WebSocket：实时更新计算状态
