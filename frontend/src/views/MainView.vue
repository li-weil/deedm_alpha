<template>
  <div class="main-view">
    <!-- 顶部导航栏 - 保持原来的下拉菜单结构 -->
    <el-header class="header">
      <!-- 水平菜单，保持原有的下拉菜单结构 -->
      <el-menu
        mode="horizontal"
        :default-active="activeMenu"
        class="main-menu"
        @select="handleMenuSelect"
      >
        <!-- 命题逻辑(P)菜单 -->
        <el-sub-menu index="propositional-logic">
          <template #title>
            <el-icon><Tools /></el-icon>
            命题逻辑(P)
          </template>
          <el-menu-item index="formula-syntax">分析公式的语法</el-menu-item>
          <el-menu-item index="truth-value">计算公式的真值</el-menu-item>
          <el-menu-item index="truth-table">构造公式的真值表</el-menu-item>
          <el-menu-item index="calculate-nf">计算与公式逻辑等值的范式</el-menu-item>
          <el-menu-item index="expand-nf">将范式扩展为主范式</el-menu-item>
          <el-menu-item index="calculus-check">等值演算过程检查</el-menu-item>
          <el-menu-item index="argument-check">验证推理有效性论证检查</el-menu-item>
        </el-sub-menu>

        <!-- 集合关系函数(S)菜单 -->
        <el-sub-menu index="set-relation-function">
          <template #title>
            <el-icon><Connection /></el-icon>
            集合关系函数(S)
          </template>
          <el-menu-item index="set-operation">单个集合的运算</el-menu-item>
          <el-menu-item index="set-expr-operation">集合表达式运算</el-menu-item>
          <el-menu-item index="relation-operation">单个关系的运算</el-menu-item>
          <el-menu-item index="relation-property">关系性质判断</el-menu-item>
          <el-menu-item index="relation-closure">关系闭包的计算</el-menu-item>
          <el-menu-item index="equivalence-relation">等价关系的计算</el-menu-item>
          <el-menu-item index="partial-order">偏序关系的计算</el-menu-item>
          <el-menu-item index="function">函数性质的判断</el-menu-item>
        </el-sub-menu>

        <!-- 组合计数(C)菜单 -->
        <el-sub-menu index="combinatorics">
          <template #title>
            <el-icon><Histogram /></el-icon>
            组合计数(C)
          </template>
          <el-menu-item index="comb-calculator">排列组合数计算</el-menu-item>
          <el-menu-item index="expr-calculator">组合表达式计算</el-menu-item>
          <el-menu-item index="recu-expr-calculator">递归表达式计算</el-menu-item>
          <el-menu-item index="count-string">字符串计数</el-menu-item>
          <el-menu-item index="count-integer">基于整除性质的整数计数</el-menu-item>
          <el-menu-item index="count-solver">不定方程非负整数解计数</el-menu-item>
          <el-menu-item index="count-function">不同性质的函数计数</el-menu-item>
          <el-menu-item index="generate-permutation">排列的生成(G)</el-menu-item>
          <el-menu-item index="generate-combination">不重复组合的生成(N)</el-menu-item>
          <el-menu-item index="generate-repcomb">允许重复组合的生成(F)</el-menu-item>
        </el-sub-menu>

        <!-- 图与树(G)菜单 -->
        <el-sub-menu index="graph-theory">
          <template #title>
            <el-icon><Share /></el-icon>
            图与树(G)
          </template>
          <el-menu-item index="graph-travel">图的遍历</el-menu-item>
          <el-menu-item index="tree-travel">树的遍历</el-menu-item>
          <el-menu-item index="shortest-path">带权图最短路径计算</el-menu-item>
          <el-menu-item index="spanning-tree">带权图最小生成树计算</el-menu-item>
          <el-menu-item index="huffman-tree">哈夫曼树构造</el-menu-item>
          <el-menu-item index="special-graph">展示特殊的图</el-menu-item>
        </el-sub-menu>

        <!-- 代数结构(B)菜单 -->
        <el-sub-menu index="algebra-structure">
          <template #title>
            <el-icon><Notebook /></el-icon>
            代数结构(B)
          </template>
          <el-menu-item index="binary-operator">运算性质的判断</el-menu-item>
          <el-menu-item index="group-um">群U(m)及其子群与陪集</el-menu-item>
          <el-menu-item index="group-perm">置换群及其子群与陪集</el-menu-item>
          <el-menu-item index="lattice">偏序关系是否格的判断</el-menu-item>
          <el-menu-item index="boolean">整除与布尔代数的判断</el-menu-item>
        </el-sub-menu>

        <!-- 帮助(H)菜单 -->
        <el-sub-menu index="help">
          <template #title>
            <el-icon><QuestionFilled /></el-icon>
            帮助(H)
          </template>
          <el-menu-item index="about">关于</el-menu-item>
          <el-menu-item index="usage">使用说明</el-menu-item>
          <el-menu-item index="config">首选项</el-menu-item>
          <el-menu-item index="clear">清理屏幕</el-menu-item>
          <el-menu-item index="exit">退出</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-header>

    <!-- 主要内容区域 - 左右分屏布局 -->
    <el-container class="main-container">
      <!-- 左侧面板 - 分离的组件 -->
      <LeftPanel
        :formula-results="formulaResults"
        :current-formula="currentFormula"
        :latex-code="latexCode"
        :current-view-component="currentViewComponent"
        @clear-formula="clearFormulaContent"
        @formula-calculated="onFormulaCalculated"
        @equiv-calculus-result="onEquivCalculusResult"
        @reason-argument-check-result="onReasonArgumentCheckResult"
        @normal-form-expansion-result="onNormalFormulaExpansionResult"
        @update-current-formula="updateCurrentFormula"
        @update-latex-code="updateLatexCode"
      />

      <!-- 右侧面板 - 分离的组件 -->
      <RightPanel
        :latex-code="latexCode"
        @clear-latex="clearLatexCode"
        @update-latex-code="updateLatexCode"
        ref="rightPanelRef"
      />
    </el-container>

    <!-- 命题逻辑的模态框组件 -->
    <PropositionalLogicView
      :formula-results="formulaResults"
      :current-formula="currentFormula"
      :latex-code="latexCode"
      @formula-calculated="onFormulaCalculated"
      @equiv-calculus-result="onEquivCalculusResult"
      @reason-argument-check-result="onReasonArgumentCheckResult"
      @normal-form-expansion-result="onNormalFormulaExpansionResult"
      @update-current-formula="updateCurrentFormula"
      @update-latex-code="updateLatexCode"
      ref="propositionalLogicModalRef"
    />

    <!-- 其他学科的模态框组件 -->
    <SetRelationFunctionView
      :formula-results="formulaResults"
      :current-formula="currentFormula"
      :latex-code="latexCode"
      @set-operation-result="onSetOperationResult"
      @set-expr-operation-result="onSetExprOperationResult"
      @relation-operation-result="onRelationOperationResult"
      @relation-property-result="onRelationPropertyResult"
      @relation-closure-result="onRelationClosureResult"
      @equivalence-relation-result="onEquivalenceRelationResult"
      @partial-order-result="onPartialOrderResult"
      @function-result="onFunctionResult"
      @update-current-formula="updateCurrentFormula"
      @update-latex-code="updateLatexCode"
      ref="setRelationFunctionModalRef"
    />

    <CombinatoricsView
      :formula-results="formulaResults"
      :current-formula="currentFormula"
      :latex-code="latexCode"
      @comb-calculator-result="onCombCalculatorResult"
      @expr-calculator-result="onExprCalculatorResult"
      @recu-expr-calculator-result="onRecuExprCalculatorResult"
      @count-string-result="onCountStringResult"
      @count-integer-result="onCountIntegerResult"
      @count-solver-result="onCountSolverResult"
      @count-function-result="onCountFunctionResult"
      @generate-permutation-result="onGeneratePermutationResult"
      @generate-combination-result="onGenerateCombinationResult"
      @generate-repcomb-result="onGenerateRepcombResult"
      @update-current-formula="updateCurrentFormula"
      @update-latex-code="updateLatexCode"
      ref="combinatoricsModalRef"
    />

    <GraphTheoryView
      :formula-results="formulaResults"
      :current-formula="currentFormula"
      :latex-code="latexCode"
      @graph-travel-result="onGraphTravelResult"
      @tree-travel-result="onTreeTravelResult"
      @shortest-path-result="onShortestPathResult"
      @spanning-tree-result="onSpanningTreeResult"
      @huffman-tree-result="onHuffmanTreeResult"
      @special-graph-result="onSpecialGraphResult"
      @update-current-formula="updateCurrentFormula"
      @update-latex-code="updateLatexCode"
      ref="graphTheoryModalRef"
    />

    <AlgebraStructureView
      :formula-results="formulaResults"
      :current-formula="currentFormula"
      :latex-code="latexCode"
      @binary-operator-result="onBinaryOperatorResult"
      @group-um-result="onGroupUmResult"
      @group-perm-result="onGroupPermResult"
      @lattice-result="onLatticeResult"
      @boolean-result="onBooleanResult"
      @update-current-formula="updateCurrentFormula"
      @update-latex-code="updateLatexCode"
      ref="algebraStructureModalRef"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Tools, Connection, Histogram, Share, Notebook, QuestionFilled } from '@element-plus/icons-vue'

// 导入布局组件
import LeftPanel from '@/components/layout/LeftPanel.vue'
import RightPanel from '@/components/layout/RightPanel.vue'

// 导入模态框组件（现在 PropositionalLogicView 就是模态框版本）
import PropositionalLogicView from '@/views/PropositionalLogicView.vue'

// 导入次级界面组件（用于左侧面板显示）
import SetRelationFunctionView from '@/views/SetRelationFunctionView.vue'
import CombinatoricsView from '@/views/CombinatoricsView.vue'
import GraphTheoryView from '@/views/GraphTheoryView.vue'
import AlgebraStructureView from '@/views/AlgebraStructureView.vue'

// 响应式数据
const activeMenu = ref('')
const currentFormula = ref('\\forall x \\in S, P(x) \\rightarrow Q(x)')
const latexCode = ref('\\forall x \\in S, P(x) \\rightarrow Q(x)')
const formulaResults = ref([])

// 组件引用
const rightPanelRef = ref(null)
const propositionalLogicModalRef = ref(null)
const setRelationFunctionModalRef = ref(null)
const combinatoricsModalRef = ref(null)
const graphTheoryModalRef = ref(null)
const algebraStructureModalRef = ref(null)

// 计算当前要显示的组件 - 根据一级菜单项确定
const currentViewComponent = computed(() => {
  // 从 activeMenu 中提取一级菜单项
  let parentMenu = activeMenu.value.split('-')[0]

  switch (parentMenu) {
    case 'formula':
    case 'truth':
    case 'calculate':
    case 'expand':
    case 'calculus':
    case 'argument':
      return PropositionalLogicView
    case 'set':
    case 'relation':
    case 'equivalence':
    case 'partial':
    case 'function':
      return SetRelationFunctionView
    case 'comb':
    case 'expr':
    case 'recu':
    case 'count':
    case 'generate':
      return CombinatoricsView
    case 'graph':
    case 'tree':
    case 'shortest':
    case 'spanning':
    case 'huffman':
    case 'special':
      return GraphTheoryView
    case 'binary':
    case 'group':
    case 'lattice':
    case 'boolean':
      return AlgebraStructureView
    default:
      return PropositionalLogicView
  }
})

// 处理菜单项点击
const handleMenuSelect = (index) => {
  console.log('Selected menu item:', index)
  activeMenu.value = index

  // 根据菜单项进行相应的处理和打开对应的模态框
  switch (index) {
    // 命题逻辑菜单项 - 打开对应的模态框
    case 'formula-syntax':
      propositionalLogicModalRef.value?.openFormulaSyntax()
      break
    case 'truth-value':
      propositionalLogicModalRef.value?.openTruthValue()
      break
    case 'truth-table':
      propositionalLogicModalRef.value?.openTruthTable()
      break
    case 'calculate-nf':
      propositionalLogicModalRef.value?.openPrincipalNormalForm()
      break
    case 'expand-nf':
      propositionalLogicModalRef.value?.openNormalFormulaExpansion()
      break
    case 'calculus-check':
      propositionalLogicModalRef.value?.openEquivCalculusCheck()
      break
    case 'argument-check':
      propositionalLogicModalRef.value?.openReasonArgumentCheck()
      break

    // 集合关系函数菜单项 - 打开对应的模态框
    case 'set-operation':
      setRelationFunctionModalRef.value?.openSetOperation()
      break
    case 'set-expr-operation':
      setRelationFunctionModalRef.value?.openSetExprOperation()
      break
    case 'relation-operation':
      setRelationFunctionModalRef.value?.openRelationOperation()
      break
    case 'relation-property':
      setRelationFunctionModalRef.value?.openRelationProperty()
      break
    case 'relation-closure':
      setRelationFunctionModalRef.value?.openRelationClosure()
      break
    case 'equivalence-relation':
      setRelationFunctionModalRef.value?.openEquivalenceRelation()
      break
    case 'partial-order':
      setRelationFunctionModalRef.value?.openPartialOrder()
      break
    case 'function':
      setRelationFunctionModalRef.value?.openFunction()
      break

    // 组合计数菜单项 - 打开对应的模态框
    case 'comb-calculator':
      combinatoricsModalRef.value?.openCombCalculator()
      break
    case 'expr-calculator':
      combinatoricsModalRef.value?.openExprCalculator()
      break
    case 'recu-expr-calculator':
      combinatoricsModalRef.value?.openRecuExprCalculator()
      break
    case 'count-string':
      combinatoricsModalRef.value?.openCountString()
      break
    case 'count-integer':
      combinatoricsModalRef.value?.openCountInteger()
      break
    case 'count-solver':
      combinatoricsModalRef.value?.openCountSolver()
      break
    case 'count-function':
      combinatoricsModalRef.value?.openCountFunction()
      break
    case 'generate-permutation':
      combinatoricsModalRef.value?.openGeneratePermutation()
      break
    case 'generate-combination':
      combinatoricsModalRef.value?.openGenerateCombination()
      break
    case 'generate-repcomb':
      combinatoricsModalRef.value?.openGenerateRepcomb()
      break

    // 图与树菜单项 - 打开对应的模态框
    case 'graph-travel':
      graphTheoryModalRef.value?.openGraphTravel()
      break
    case 'tree-travel':
      graphTheoryModalRef.value?.openTreeTravel()
      break
    case 'shortest-path':
      graphTheoryModalRef.value?.openShortestPath()
      break
    case 'spanning-tree':
      graphTheoryModalRef.value?.openSpanningTree()
      break
    case 'huffman-tree':
      graphTheoryModalRef.value?.openHuffmanTree()
      break
    case 'special-graph':
      graphTheoryModalRef.value?.openSpecialGraph()
      break

    // 代数结构菜单项 - 打开对应的模态框
    case 'binary-operator':
      algebraStructureModalRef.value?.openBinaryOperator()
      break
    case 'group-um':
      algebraStructureModalRef.value?.openGroupUm()
      break
    case 'group-perm':
      algebraStructureModalRef.value?.openGroupPerm()
      break
    case 'lattice':
      algebraStructureModalRef.value?.openLattice()
      break
    case 'boolean':
      algebraStructureModalRef.value?.openBoolean()
      break

    // 帮助菜单项
    case 'about':
      showAboutDialog()
      break
    case 'usage':
      showUsageDialog()
      break
    case 'config':
      ElMessage.info('首选项功能')
      break
    case 'clear':
      clearFormulaContent()
      break
    case 'exit':
      if (confirm('确定要退出吗？')) {
        window.close()
      }
      break

    default:
      ElMessage.info(`选择了菜单项: ${index}`)
      break
  }
}

// 处理公式计算完成事件
const onFormulaCalculated = (result) => {
  if (!result.index) {
    result.index = formulaResults.value.length + 1
  }

  formulaResults.value.push(result)
  currentFormula.value = result.formula

  // 使用右侧面板的 LaTeX 生成函数
  if (rightPanelRef.value) {
    const latexString = rightPanelRef.value.generateLaTeXCode(result)
    if (latexCode.value) {
      latexCode.value += '\n\n' + latexString
    } else {
      latexCode.value = latexString
    }
  }

  ElMessage.success('公式分析结果已添加到主界面')
}

// 处理等值演算检查结果
const onEquivCalculusResult = (result) => {
  if (result && result.data) {
    const formattedResult = {
      index: formulaResults.value.length + 1,
      formula: `等值演算检查步骤${result.data.stepNumber}`,
      type: 'equiv-calculus-check',
      stepNumber: result.data.stepNumber,
      steps: result.data.steps,
      valid: result.data.valid,
      errorMessage: result.data.errorMessage,
      counterExample: result.data.counterExample,
      checkingFormula: result.data.checkingFormula,
      success: result.data.success,
      message: result.data.message
    }

    formulaResults.value.push(formattedResult)
    currentFormula.value = `等值演算检查步骤${result.data.stepNumber}`

    // 使用右侧面板的 LaTeX 生成函数
    if (rightPanelRef.value) {
      const latexString = rightPanelRef.value.generateLaTeXCode(formattedResult)
      if (latexCode.value) {
        latexCode.value += '\n\n' + latexString
      } else {
        latexCode.value = latexString
      }
    }
  }
}

// 处理推理有效性论证检查结果
const onReasonArgumentCheckResult = (result) => {
  if (result && result.data) {
    const formattedResult = {
      index: formulaResults.value.length + 1,
      formula: `推理有效性论证检查步骤${result.data.stepNumber || formulaResults.value.length + 1}`,
      type: 'reason-argument-check',
      stepNumber: result.data.stepNumber || formulaResults.value.length + 1,
      steps: result.data.steps,
      checkSteps: result.data.checkSteps,
      valid: result.data.valid,
      errorMessage: result.data.message,
      counterExample: result.data.counterExample,
      checkingFormula: result.data.checkingFormula,
      success: result.data.success,
      latexString: result.data.latexString,
      premises: result.data.premises,
      consequent: result.data.consequent
    }

    formulaResults.value.push(formattedResult)
    currentFormula.value = formattedResult.formula

    // 使用右侧面板的 LaTeX 生成函数
    if (rightPanelRef.value) {
      const latexString = rightPanelRef.value.generateLaTeXCode(formattedResult)
      if (latexCode.value) {
        latexCode.value += '\n\n' + latexString
      } else {
        latexCode.value = latexString
      }
    }

    ElMessage.success('推理有效性论证检查结果已添加到主界面')
  }
}

// 处理范式扩展结果
const onNormalFormulaExpansionResult = (result) => {
  if (result && result.data) {
    const formattedResult = {
      index: formulaResults.value.length + 1,
      formula: result.data.originalFormula,
      type: 'normal-form-expansion',
      targetType: result.data.targetType,
      originalFormula: result.data.originalFormula,
      variableSet: result.data.variableSet,
      expansionSteps: result.data.expansionSteps,
      pdnfResult: result.data.pdnfResult,
      pcnfResult: result.data.pcnfResult,
      success: result.data.success,
      message: result.data.message
    }

    formulaResults.value.push(formattedResult)
    currentFormula.value = result.data.originalFormula

    // 使用右侧面板的 LaTeX 生成函数
    if (rightPanelRef.value) {
      const latexString = rightPanelRef.value.generateLaTeXCode(formattedResult)
      if (latexCode.value) {
        latexCode.value += '\n\n' + latexString
      } else {
        latexCode.value = latexString
      }
    }

    ElMessage.success('范式扩展结果已添加到主界面')
  }
}

// 集合关系函数结果处理函数
const onSetOperationResult = (result) => {
  console.log('集合运算结果:', result)
  ElMessage.success('集合运算完成')
}

const onSetExprOperationResult = (result) => {
  console.log('集合表达式运算结果:', result)
  ElMessage.success('集合表达式运算完成')
}

const onRelationOperationResult = (result) => {
  console.log('关系运算结果:', result)
  ElMessage.success('关系运算完成')
}

const onRelationPropertyResult = (result) => {
  console.log('关系性质判断结果:', result)
  ElMessage.success('关系性质判断完成')
}

const onRelationClosureResult = (result) => {
  console.log('关系闭包计算结果:', result)
  ElMessage.success('关系闭包计算完成')
}

const onEquivalenceRelationResult = (result) => {
  console.log('等价关系计算结果:', result)
  ElMessage.success('等价关系计算完成')
}

const onPartialOrderResult = (result) => {
  console.log('偏序关系计算结果:', result)
  ElMessage.success('偏序关系计算完成')
}

const onFunctionResult = (result) => {
  console.log('函数性质判断结果:', result)
  ElMessage.success('函数性质判断完成')
}

// 组合计数结果处理函数
const onCombCalculatorResult = (result) => {
  console.log('排列组合数计算结果:', result)
  ElMessage.success('排列组合数计算完成')
}

const onExprCalculatorResult = (result) => {
  console.log('组合表达式计算结果:', result)
  ElMessage.success('组合表达式计算完成')
}

const onRecuExprCalculatorResult = (result) => {
  console.log('递归表达式计算结果:', result)
  ElMessage.success('递归表达式计算完成')
}

const onCountStringResult = (result) => {
  console.log('字符串计数结果:', result)
  ElMessage.success('字符串计数完成')
}

const onCountIntegerResult = (result) => {
  console.log('整数计数结果:', result)
  ElMessage.success('整数计数完成')
}

const onCountSolverResult = (result) => {
  console.log('不定方程求解结果:', result)
  ElMessage.success('不定方程求解完成')
}

const onCountFunctionResult = (result) => {
  console.log('函数计数结果:', result)
  ElMessage.success('函数计数完成')
}

const onGeneratePermutationResult = (result) => {
  console.log('排列生成结果:', result)
  ElMessage.success('排列生成完成')
}

const onGenerateCombinationResult = (result) => {
  console.log('组合生成结果:', result)
  ElMessage.success('组合生成完成')
}

const onGenerateRepcombResult = (result) => {
  console.log('重复组合生成结果:', result)
  ElMessage.success('重复组合生成完成')
}

// 图论结果处理函数
const onGraphTravelResult = (result) => {
  console.log('图遍历结果:', result)
  ElMessage.success('图遍历完成')
}

const onTreeTravelResult = (result) => {
  console.log('树遍历结果:', result)
  ElMessage.success('树遍历完成')
}

const onShortestPathResult = (result) => {
  console.log('最短路径计算结果:', result)
  ElMessage.success('最短路径计算完成')
}

const onSpanningTreeResult = (result) => {
  console.log('最小生成树计算结果:', result)
  ElMessage.success('最小生成树计算完成')
}

const onHuffmanTreeResult = (result) => {
  console.log('哈夫曼树构造结果:', result)
  ElMessage.success('哈夫曼树构造完成')
}

const onSpecialGraphResult = (result) => {
  console.log('特殊图展示结果:', result)
  ElMessage.success('特殊图展示完成')
}

// 代数结构结果处理函数
const onBinaryOperatorResult = (result) => {
  console.log('运算性质判断结果:', result)
  ElMessage.success('运算性质判断完成')
}

const onGroupUmResult = (result) => {
  console.log('群U(m)分析结果:', result)
  ElMessage.success('群U(m)分析完成')
}

const onGroupPermResult = (result) => {
  console.log('置换群分析结果:', result)
  ElMessage.success('置换群分析完成')
}

const onLatticeResult = (result) => {
  console.log('格判断结果:', result)
  ElMessage.success('格判断完成')
}

const onBooleanResult = (result) => {
  console.log('布尔代数判断结果:', result)
  ElMessage.success('布尔代数判断完成')
}

// 更新当前公式和LaTeX代码
const updateCurrentFormula = (formula) => {
  currentFormula.value = formula
}

const updateLatexCode = (code) => {
  if (latexCode.value) {
    latexCode.value += '\n\n' + code
  } else {
    latexCode.value = code
  }
}

// 清空公式内容
const clearFormulaContent = async () => {
  try {
    await cleanupBackendData()

    formulaResults.value = []
    currentFormula.value = '\\forall x \\in S, P(x) \\rightarrow Q(x)'

    ElMessage.success('公式内容和数据文件已清空')
  } catch (error) {
    console.error('清空操作失败:', error)
    ElMessage.error('清空操作失败: ' + (error.message || '未知错误'))
  }
}

// 清空LaTeX代码
const clearLatexCode = () => {
  latexCode.value = ''
  ElMessage.success('LaTeX代码已清空')
}

// 清理后端data目录
const cleanupBackendData = async () => {
  try {
    const baseUrl = window.location.origin
    const response = await fetch(`${baseUrl}/api/cleanup/data`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
    })

    const result = await response.json()

    if (result.success) {
      console.log(`清理了 ${result.deletedCount} 个数据文件`)
    } else {
      console.warn('后端数据清理失败:', result.message)
    }
  } catch (error) {
    console.error('调用后端清理接口失败:', error)
  }
}

// 显示关于对话框
const showAboutDialog = () => {
  ElMessage.info('离散数学教学演示辅助工具展示(Deedm)\n\n(Demonstrator of Examples in Elementary Discrete Mathematics)\n\n(C) 版权所有，中山大学， 2020--2030\n\n中山大学数学学院和中山大学数据科学与计算机学院')
}

// 显示使用说明对话框
const showUsageDialog = () => {
  ElMessage.info('Deedm(Demonstrator of Examples in Elementary Discrete Mathematics)是一个用于展示教材《离散数学（上）：集合、关系、函数、组合计数》（中山大学数学学院， 2018）中的计算例子的工具。')
}

onMounted(() => {
  console.log('Main view mounted with new architecture')
})
</script>

<style scoped>
.main-view {
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.main-menu {
  background: none;
  border: none;
}

.main-menu :deep(.el-sub-menu__title),
.main-menu :deep(.el-menu-item) {
  color: white;
  border-bottom: 2px solid transparent;
}

.main-menu :deep(.el-sub-menu__title:hover),
.main-menu :deep(.el-menu-item:hover),
.main-menu :deep(.el-sub-menu__title.is-active),
.main-menu :deep(.el-menu-item.is-active) {
  background: rgba(255, 255, 255, 0.1);
  border-bottom-color: white;
}

.main-container {
  flex: 1;
  display: flex;
  overflow: hidden;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .main-container {
    flex-direction: column;
  }
}
</style>