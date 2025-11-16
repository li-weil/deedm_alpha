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
        :collapse="isMobile"
        @click.stop="handleMenuClick"
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
        @clear="clearFormulaContent"
      />

      <!-- 右侧面板 - 分离的组件 -->
      <RightPanel
        :latex-code="latexCode"
        @clear="clearLatexCode"
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
    <!-- :formula-results="formulaResults" -将父组件的formulaResults变量传递给子组件的formulaResults prop -->
    <!-- @formula-calculated="onFormulaCalculated" - 监听子组件触发的formula-calculated事件，调用父组件的onFormulaCalculated方法 -->
    <!-- ref创建对组件实例的引用，可以在父组件 -->
    <!-- 中通过propositionalLogicModalRef.value访问子组件的方法和属性 -->

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
      @function-property-result="onFunctionPropertyResult"
      @update-current-formula="updateCurrentFormula"
      @update-latex-code="updateLatexCode"
      ref="setRelationFunctionModalRef"
    />

    <CombinatoricsView
      :formula-results="formulaResults"
      :current-formula="currentFormula"
      :latex-code="latexCode"
      @comb-calculator-result="onCombCalculatorResult"
      @expression-calculator-result="onExpressionCalculatorResult"
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
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Tools, Connection, Histogram, Share, Notebook, QuestionFilled } from '@element-plus/icons-vue'

// 导入布局组件
import LeftPanel from '@/components/common/LeftPanel.vue'
import RightPanel from '@/components/common/RightPanel.vue'

// 导入模态框组件
import PropositionalLogicView from '@/views/PropositionalLogicView.vue'

// 导入次级界面组件（用于左侧面板显示）
import SetRelationFunctionView from '@/views/SetRelationFunctionView.vue'
import CombinatoricsView from '@/views/CombinatoricsView.vue'

// 导入LaTeX代码生成工具
import { generateLaTeXCode } from '@/utils/latexGenerator.js'
import GraphTheoryView from '@/views/GraphTheoryView.vue'
import AlgebraStructureView from '@/views/AlgebraStructureView.vue'

// 响应式数据
const activeMenu = ref('')
const currentFormula = ref('\\forall x \\in S, P(x) \\rightarrow Q(x)')
const latexCode = ref('')
const formulaResults = ref([])
const isMobile = ref(false)

// 组件引用
const propositionalLogicModalRef = ref(null)
const setRelationFunctionModalRef = ref(null)
const combinatoricsModalRef = ref(null)
const graphTheoryModalRef = ref(null)
const algebraStructureModalRef = ref(null)

// 检测是否为移动端设备
const checkMobile = () => {
  isMobile.value = window.innerWidth <= 768 || /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)
}

// 处理窗口大小变化
const handleResize = () => {
  checkMobile()
}

// 处理菜单点击事件（移动端优化）
const handleMenuClick = (event) => {
  // 在移动端，阻止菜单项点击后立即重新打开子菜单
  if (isMobile.value) {
    event.stopPropagation()
    event.preventDefault()
  }
}

// 处理菜单项点击
const handleMenuSelect = (index) => {
  console.log('Selected menu item:', index)
  activeMenu.value = index

  // 防止移动端重复触发菜单
  if (isMobile.value) {
    // 移动端延迟处理菜单，避免与子菜单关闭事件冲突
    setTimeout(() => {
      processMenuSelection(index)
    }, 100)
  } else {
    processMenuSelection(index)
  }
}

// 处理菜单选择的实际逻辑
const processMenuSelection = (index) => {
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
      if (window.confirm('确定要退出吗？')) {
        window.close()
      }
      break

    default:
      ElMessage.info(`选择了菜单项: ${index}`)
      break
  }
}

// 处理公式计算完成事件
const onFormulaCalculated = (data) => {
  const { result, latexString } = data
  console.log('MainView: 收到公式计算结果:', result)
  console.log('MainView: AST数据:', result.astData)
  console.log('MainView: 接收到的latexString:', latexString)
  console.log('MainView: latexString长度:', latexString?.length || 0)

  handleResultWithLatex(result, latexString, '公式分析结果已添加到主界面')
}

// 处理等值演算检查结果
const onEquivCalculusResult = (data) => {
  const { result, latexString } = data
  handleResultWithLatex(result, latexString)
}

// 处理推理有效性论证检查结果
const onReasonArgumentCheckResult = (data) => {
  const { result, latexString } = data
  handleResultWithLatex(result, latexString, '推理有效性论证检查结果已添加到主界面')
}

// 处理范式扩展结果
const onNormalFormulaExpansionResult = (data) => {
  const { result, latexString } = data
  handleResultWithLatex(result, latexString, '范式扩展结果已添加到主界面')
}

// 集合关系函数结果处理函数
const onSetOperationResult = ({ result, latexString }) => {
  console.log('MainView: 接收到集合运算结果', { result, latexString })
  handleResultWithLatex(result, latexString, '集合运算结果已添加到主界面')
}

const onSetExprOperationResult = ({ result, latexString }) => {
  console.log('MainView: 接收到集合表达式运算结果', { result, latexString })
  handleResultWithLatex(result, latexString, '集合表达式运算结果已添加到主界面')
}

const onRelationOperationResult = ({ result, latexString }) => {
  console.log('MainView: 接收到关系运算结果', { result, latexString })
  handleResultWithLatex(result, latexString, '关系运算结果已添加到主界面')
}

const onRelationPropertyResult = ({ result, latexString }) => {
  console.log('MainView: 接收到关系性质判断结果', { result, latexString })
  handleResultWithLatex(result, latexString, '关系性质判断结果已添加到主界面')
}

const onRelationClosureResult = ({ result, latexString }) => {
  console.log('MainView: 接收到关系闭包计算结果', { result, latexString })
  handleResultWithLatex(result, latexString, '关系闭包计算结果已添加到主界面')
}

const onEquivalenceRelationResult = ({ result, latexString }) => {
  console.log('MainView: 接收到等价关系计算结果', { result, latexString })
  handleResultWithLatex(result, latexString, '等价关系计算结果已添加到主界面')
}

const onPartialOrderResult = ({ result, latexString }) => {
  console.log('MainView: 接收到偏序关系计算结果', result)

  handleResultWithLatex(result, latexString, '偏序关系计算结果已添加到主界面')

  ElMessage.success('偏序关系计算完成')
}

const onFunctionPropertyResult = ({ result, latexString }) => {
  console.log('MainView: 接收到函数性质判断结果', result)
  console.log('MainView: result.isFunction =', result.isFunction)
  console.log('MainView: result.keys =', Object.keys(result))

  handleResultWithLatex(result, latexString, '函数性质判断结果已添加到主界面')

  ElMessage.success('函数性质判断完成')
}

// 排列组合数计算结果处理函数
const onCombCalculatorResult = (result) => {
  console.log('MainView: 接收到排列组合数计算结果', result)

  // 生成LaTeX代码
  const latexString = generateLaTeXCode(result)

  handleResultWithLatex(result, latexString, '排列组合数计算结果已添加到主界面')

  ElMessage.success('排列组合数计算完成')
}

const onExpressionCalculatorResult = (result) => {
  console.log('MainView: 接收到组合表达式计算结果', result)

  // 生成LaTeX代码
  const latexString = generateLaTeXCode(result)

  handleResultWithLatex(result, latexString, '组合表达式计算结果已添加到主界面')

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
const onGraphTravelResult = ({ result, latexString }) => {
  console.log('MainView: 接收到图遍历结果', { result, latexString })
  handleResultWithLatex(result, latexString, '图遍历分析结果已添加到主界面')
}

const onTreeTravelResult = ({ result, latexString }) => {
  console.log('MainView: 接收到树遍历结果', { result, latexString })
  handleResultWithLatex(result, latexString, '树遍历分析结果已添加到主界面')
}

const onShortestPathResult = ({ result, latexString }) => {
  console.log('MainView: 接收最短路计算结果', { result, latexString })
  handleResultWithLatex(result, latexString, '带权图最短路径计算结果已添加到主界面')
}

const onSpanningTreeResult = ({ result, latexString }) => {
  console.log('MainView: 接收到最小生成树计算结果', { result, latexString })
  handleResultWithLatex(result, latexString, '带权图最小生成树计算结果已添加到主界面')
}

const onHuffmanTreeResult = ({ result, latexString }) => {
  handleResultWithLatex(result, latexString, '哈夫曼树构造结果已添加到主界面')
}

const onSpecialGraphResult = ({ result, latexString }) => {
  console.log('MainView: 接收到特殊图展示结果', { result, latexString })
  handleResultWithLatex(result, latexString, '特殊图展示结果已添加到主界面')
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
  if (!code) return

  if (latexCode.value && latexCode.value.trim()) {
    latexCode.value += '\n\n' + code
  } else {
    latexCode.value = code
  }

  console.log('MainView: LaTeX代码已更新:', latexCode.value)
}

// 通用的结果处理函数
const handleResultWithLatex = (result, latexString, successMessage) => {
  formulaResults.value.push(result)
  currentFormula.value = result.formula

  if (latexString) {
    updateLatexCode(latexString)
  }

  if (successMessage) {
    ElMessage.success(successMessage)
  }
}

// 清空公式内容
const clearFormulaContent = async () => {
  await handleAsyncError(async () => {
    await cleanupBackendData()

    formulaResults.value = []
    currentFormula.value = '\\forall x \\in S, P(x) \\rightarrow Q(x)'

    ElMessage.success('公式内容和数据文件已清空')
  }, '清空操作失败')
}

// 清空LaTeX代码
const clearLatexCode = () => {
  latexCode.value = ''
  console.log('MainView: LaTeX代码已清空')
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

// 错误边界处理函数
const handleAsyncError = async (asyncFunction, errorMessage) => {
  try {
    return await asyncFunction()
  } catch (error) {
    console.error(`${errorMessage}:`, error)
    ElMessage.error(`${errorMessage}: ${error.message || '未知错误'}`)
    return null
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
  checkMobile()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
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