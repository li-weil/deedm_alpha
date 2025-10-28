<template>
  <!-- 相当于HTML的div容器，包含整个页面内容 -->
  <div class="main-view">
    <!-- 顶部导航栏 - 类似HTML的header，使用Element Plus组件库 -->
    <el-header class="header">
      <!-- 水平菜单，类似HTML的nav -->
      <el-menu
        mode="horizontal"
        :default-active="activeMenu"
        class="main-menu"
        @select="handleMenuSelect"
      >
        <!-- 命题逻辑(P)菜单 -->
        <el-sub-menu index="propositional-logic">
          <template #title>命题逻辑(P)</template>
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
          <template #title>集合关系函数(S)</template>
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
          <template #title>组合计数(C)</template>
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
          <template #title>图与树(G)</template>
          <el-menu-item index="graph-travel">图的遍历</el-menu-item>
          <el-menu-item index="tree-travel">树的遍历</el-menu-item>
          <el-menu-item index="shortest-path">带权图最短路径计算</el-menu-item>
          <el-menu-item index="spanning-tree">带权图最小生成树计算</el-menu-item>
          <el-menu-item index="huffman-tree">哈夫曼树构造</el-menu-item>
          <el-menu-item index="special-graph">展示特殊的图</el-menu-item>
        </el-sub-menu>

        <!-- 代数结构(B)菜单 -->
        <el-sub-menu index="algebra-structure">
          <template #title>代数结构(B)</template>
          <el-menu-item index="binary-operator">运算性质的判断</el-menu-item>
          <el-menu-item index="group-um">群U(m)及其子群与陪集</el-menu-item>
          <el-menu-item index="group-perm">置换群及其子群与陪集</el-menu-item>
          <el-menu-item index="lattice">偏序关系是否格的判断</el-menu-item>
          <el-menu-item index="boolean">整除与布尔代数的判断</el-menu-item>
        </el-sub-menu>

        <!-- 帮助(H)菜单 -->
        <el-sub-menu index="help">
          <template #title>帮助(H)</template>
          <el-menu-item index="about">关于</el-menu-item>
          <el-menu-item index="usage">使用说明</el-menu-item>
          <el-menu-item index="config">首选项</el-menu-item>
          <el-menu-item index="clear">清理屏幕</el-menu-item>
          <el-menu-item index="exit">退出</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-header>

    <!-- 主要内容区域 - 左右分屏布局，类似HTML的main -->
    <el-container class="main-container">
      <!-- 左侧面板 - 显示公式内容，类似HTML的aside侧边栏 -->
      <el-aside class="left-panel" @wheel="handleLeftWheel">  <!-- 监听鼠标滚轮事件 -->
        <div class="panel-header">
          <h3>公式内容</h3>
          <!-- 按钮，点击时调用clearFormulaContent函数 -->
          <el-button size="small" type="warning" @click="clearFormulaContent">
            <el-icon><Delete /></el-icon>  <!-- 删除图标 -->
            清空
          </el-button>
        </div>
        <div class="panel-content" ref="leftContent">  <!-- DOM元素引用，用于JavaScript操作 -->
          <!-- 公式内容将在这里显示 -->
          <div class="formula-display">
            <!-- 数学公式渲染组件 -->
            <math-renderer
              :formula="cleanFormulaForDisplay(currentFormula)"
              :type="'katex'"
              :display-mode="true"
              @rendered="onFormulaRendered"
              @error="onFormulaError"
            />
          </div>

  
          <!-- 显示所有公式和真值表 - 条件渲染：只有当formulaResults数组有内容时才显示 -->
          <div v-if="formulaResults.length > 0" class="formula-results">
            <!-- 循环遍历formulaResults数组，v-for类似for循环，:key用于Vue性能优化 -->
            <div v-for="(result, index) in formulaResults" :key="index" class="result-item">
              <div class="result-formula">
                <strong>公式 {{ index + 1 }}: </strong>
                <!-- 数学公式渲染组件 -->
                <math-renderer
                  :formula="cleanFormulaForDisplay(result.formula)"
                  :type="'katex'"
                  :display-mode="false"
                />
              </div>

    
              <!-- 范式扩展结果 -->
              <div v-if="result.type === 'normal-form-expansion'" class="normal-form-expansion-results">
                <!-- 变量集信息 -->
                <div class="variable-info">
                  <h4 class="result-title">扩展变量集：</h4>
                  <span>{{ result.variableSet }}</span>
                </div>

                <!-- 扩展步骤 -->
                <div v-if="result.expansionSteps && result.expansionSteps.length > 0" class="expansion-steps">
                  <h4 class="expansion-title">{{ result.targetType }}扩展步骤：</h4>
                  <div v-for="(step, stepIndex) in result.expansionSteps" :key="'expansion-step-' + stepIndex" class="expansion-step-inline">
                    <el-text type="primary" class="step-description">{{ step.description }}</el-text>
                    <math-renderer
                      :formula="step.formula"
                      :type="'katex'"
                      :display-mode="false"
                      class="step-formula-inline"
                    />
                    <span v-if="step.binaryCode" class="formula-code-inline">[{{ step.binaryCode }}]</span>
                    <el-text type="success" class="step-description">得到</el-text>
                    <math-renderer
                      :formula="step.resultCodes"
                      :type="'mathjax'"
                      :display-mode="false"
                      class="result-codes-inline"
                    />
                  </div>
                </div>

                <!-- 最终结果 -->
                <div class="pnf-result">
                  <div v-if="result.pdnfResult">
                    <h5 class="pnf-title">最终的主析取范式(PDNF)是：</h5>
                    <math-renderer
                      :formula="result.pdnfResult"
                      :type="'mathjax'"
                      :display-mode="false"
                    />
                  </div>
                  <div v-if="result.pcnfResult">
                    <h5 class="pnf-title">相应的主合取范式(PCNF)是：</h5>
                    <math-renderer
                      :formula="result.pcnfResult"
                      :type="'mathjax'"
                      :display-mode="false"
                    />
                  </div>
                </div>
              </div>

              <!-- 等值演算检查结果 -->
              <div v-if="result.type === 'equiv-calculus-check'" class="equiv-calculus-results">
                <div class="result-title">
                  <strong>检查演算步骤</strong>
                </div>

                <!-- 演算步骤显示 -->
                <div class="calculus-steps">
                  <div v-for="(step, index) in result.steps" :key="index" class="calculus-step">
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
                  <div v-if="result.valid" class="valid-result">
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
                      :description="result.errorMessage"
                      :closable="false"
                      show-icon
                    />

                    <!-- 反例信息 -->
                    <div v-if="result.counterExample" class="counter-example">
                      <h4 class="counter-title">反例：</h4>
                      <p>在真值赋值 {{ result.counterExample }} 下，以下公式不是重言式：</p>
                      <div class="checking-formula">
                        <math-renderer
                          :formula="result.checkingFormula"
                          :type="'katex'"
                          :display-mode="true"
                        />
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 显示真值表 - 优先使用新的LaTeX表格格式 -->
              <div class="truth-table">
                <!-- 如果有latexTable，使用MathRenderer渲染 -->
                <div v-if="result.tableData && result.tableData.latexTable" class="truth-table-container">
                  <math-renderer
                    :key="'main-table-' + index + '-' + Date.now()"
                    :formula="result.tableData.latexTable"
                    :type="'katex'"
                    :display-mode="true"
                    class="truth-table-content"
                  />
                </div>
                <!-- 保持原有的HTML表格作为后备 -->
                <div v-else-if="result.tableData && result.tableData.headers" class="truth-table-legacy">
                  <table class="truth-table-html">
                    <thead>  <!-- 表头 -->
                      <tr>
                        <!-- 循环生成表头单元格 -->
                        <th v-for="(header, headerIndex) in result.tableData.headers" :key="headerIndex" class="header-cell">
                          <math-renderer
                            :formula="cleanFormulaForDisplay(header)"
                            :type="'katex'"
                            :display-mode="false"
                          />
                        </th>
                      </tr>
                    </thead>
                    <tbody>  <!-- 表格内容 -->
                      <!-- 循环生成表格行 -->
                      <tr v-for="(row, rowIndex) in result.tableData.rows" :key="rowIndex">
                        <!-- 循环生成表格单元格：合并变量值和结果值 -->
                        <td v-for="(cell, cellIndex) in [...row.variableValues, row.resultValue]" :key="cellIndex" class="data-cell">
                          <math-renderer
                            :formula="cleanFormulaForDisplay(cell)"
                            :type="'katex'"
                            :display-mode="false"
                          />
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
                <!-- 最终后备：显示原始truthTable字符串 -->
                <div v-else-if="result.truthTable" class="truth-table-fallback">
                  <math-renderer
                    :formula="result.truthTable"
                    :type="'katex'"
                    :display-mode="true"
                    class="truth-table-content"
                  />
                </div>

                <!-- 显示真值计算结果 - 条件渲染：只有当result.truthValue存在时才显示 -->
                <div v-else-if="result.truthValue !== undefined" class="truth-value-result">
                  <div class="truth-value-display">
                    <span class="truth-value-label">公式真值 = </span>
                    <el-tag
                      :type="result.truthValue ? 'success' : 'danger'"
                      size="large"
                      class="truth-value-tag"
                    >
                      {{ result.truthValue ? '1' : '0' }}
                    </el-tag>
                  </div>
                </div>
              </div>

              <!-- 显示详细计算过程 - 条件渲染：只有当result.detailedSteps存在时才显示 -->
              <div v-if="result.detailedSteps && result.detailedSteps.length > 0" class="detailed-steps">
                <div class="steps-content">
                  <div v-for="(step, stepIndex) in result.detailedSteps" :key="stepIndex" class="step-item">
                    <div v-if="step.explanation" class="step-explanation">
                      {{ step.explanation }}
                    </div>
                    <div class="step-formula">
                      <math-renderer
                        :formula="step.formula"
                        :type="'katex'"
                        :display-mode="true"
                      />
                    </div>
                  </div>
                </div>
              </div>

              <!-- 显示公式类型 - 条件渲染：只有当result.formulaType存在时才显示 -->
              <div v-if="result.formulaType" class="formula-type">
                <!-- 标签组件，显示公式类型（重言式、矛盾式等） -->
                <el-tag :type="getFormulaTypeTag(result.formulaType)" class="type-tag">
                  {{ result.formulaType }}
                </el-tag>
              </div>

              <!-- 显示严格形式公式 - 条件渲染：只有当result.syntaxData存在时才显示 -->
              <div v-if="result.syntaxData" class="syntax-content">
                <h5 class="syntax-title">严格形式公式：</h5>
                <div class="syntax-row">
                  <span class="syntax-label">严格形式：</span>
                  <math-renderer
                    :formula="cleanFormulaForDisplay(result.syntaxData.strictForm)"
                    :type="'katex'"
                    :display-mode="false"
                    class="syntax-formula"
                  />
                </div>
                <div class="syntax-row">
                  <span class="syntax-label">简化写为：</span>
                  <math-renderer
                    :formula="cleanFormulaForDisplay(result.syntaxData.simpleForm)"
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

              <!-- 显示抽象语法树 - 条件渲染：只有当result.astData存在时才显示 -->
              <div v-if="result.astData" class="ast-content">
                <h5 class="ast-title">抽象语法树：</h5>
                <div class="ast-image-container" v-if="result.astData.imageUrl">
                  <img
                    :src="result.astData.imageUrl"
                    :alt="'公式' + result.index + '的抽象语法树'"
                    class="ast-image"
                    @error="handleASTImageError"
                    @load="handleASTImageLoad"
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
              </div>

              <!-- 显示范式计算结果 - 条件渲染：只有当result.cnfResult或result.dnfResult存在时才显示 -->
              <div v-if="result.cnfResult || result.dnfResult" class="normal-form-results">
                <!-- CNF 结果 -->
                <div v-if="result.cnfResult" class="normal-form-result">
                  <h5 class="result-title">合取范式结果：</h5>
                  <div class="calculation-steps" v-if="result.cnfSteps && result.cnfSteps.length > 0">
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
                </div>

                <!-- DNF 结果 -->
                <div v-if="result.dnfResult" class="normal-form-result">
                  <h5 class="result-title">析取范式结果：</h5>
                  <div class="calculation-steps" v-if="result.dnfSteps && result.dnfSteps.length > 0">
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
                </div>

                <!-- CNF 扩展步骤 -->
                <div v-if="result.cnfExpansionSteps && result.cnfExpansionSteps.length > 0" class="expansion-steps">
                  <h5 class="expansion-title">主合取范式扩展步骤：</h5>
                  <div v-for="(step, stepIndex) in result.cnfExpansionSteps" :key="'cnf-expansion-' + stepIndex" class="expansion-step-inline">
                    <el-text type="primary" class="step-description">{{ step.expansionDescription }}</el-text>
                    <math-renderer
                      :formula="step.formula"
                      :type="'katex'"
                      :display-mode="false"
                      class="step-formula-inline"
                    />
                    <span v-if="step.formulaCode" class="formula-code-inline">[{{ step.formulaCode }}]</span>
                    <el-text type="success" class="step-description">{{ step.resultDescription }}</el-text>
                    <math-renderer
                      :formula="step.resultCodes"
                      :type="'mathjax'"
                      :display-mode="false"
                      class="result-codes-inline"
                    />
                  </div>
                </div>

                <!-- DNF 扩展步骤 -->
                <div v-if="result.dnfExpansionSteps && result.dnfExpansionSteps.length > 0" class="expansion-steps">
                  <h5 class="expansion-title">主析取范式扩展步骤：</h5>
                  <div v-for="(step, stepIndex) in result.dnfExpansionSteps" :key="'dnf-expansion-' + stepIndex" class="expansion-step-inline">
                    <el-text type="primary" class="step-description">{{ step.expansionDescription }}</el-text>
                    <math-renderer
                      :formula="step.formula"
                      :type="'katex'"
                      :display-mode="false"
                      class="step-formula-inline"
                    />
                    <span v-if="step.formulaCode" class="formula-code-inline">[{{ step.formulaCode }}]</span>
                    <el-text type="success" class="step-description">{{ step.resultDescription }}</el-text>
                    <math-renderer
                      :formula="step.resultCodes"
                      :type="'mathjax'"
                      :display-mode="false"
                      class="result-codes-inline"
                    />
                  </div>
                </div>

                <!-- 主范式结果 -->
                <div v-if="(result.cnfResult && (result.cnfResult.pcnf || result.cnfResult.pdnf)) || (result.dnfResult && (result.dnfResult.pdnf || result.dnfResult.pcnf))" class="pnf-result">
                  <div v-if="result.cnfResult && result.cnfResult.pcnf">
                    <h5 class="pnf-title">最终的主合取范式是：</h5>
                    <math-renderer
                      :formula="result.cnfResult.pcnf"
                      :type="'mathjax'"
                      :display-mode="false"
                    />
                  </div>
                  <div v-if="result.cnfResult && result.cnfResult.pdnf">
                    <h5 class="pnf-title">相应的主析取范式是：</h5>
                    <math-renderer
                      :formula="result.cnfResult.pdnf"
                      :type="'mathjax'"
                      :display-mode="false"
                    />
                  </div>
                  <div v-if="result.dnfResult && result.dnfResult.pdnf">
                    <h5 class="pnf-title">最终的主析取范式是：</h5>
                    <math-renderer
                      :formula="result.dnfResult.pdnf"
                      :type="'mathjax'"
                      :display-mode="false"
                    />
                  </div>
                  <div v-if="result.dnfResult && result.dnfResult.pcnf">
                    <h5 class="pnf-title">相应的主合取范式是：</h5>
                    <math-renderer
                      :formula="result.dnfResult.pcnf"
                      :type="'mathjax'"
                      :display-mode="false"
                    />
                  </div>
                </div>

  
                <!-- 真值表结果 -->
                <div v-if="result.truthTable" class="truth-table-result">
                  <h5 class="result-title">真值表计算结果：</h5>
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

        </el-aside>

      <!-- 右侧面板 - LaTeX代码 -->
      <el-aside class="right-panel" @wheel="handleRightWheel">  <!-- 监听鼠标滚轮事件 -->
        <div class="panel-header">
          <h3>LaTeX 代码</h3>
          <!-- 清空按钮 -->
          <el-button size="small" type="warning" @click="clearLatexCode">
            <el-icon><Delete /></el-icon>
            清空
          </el-button>
        </div>
        <div class="panel-content" ref="rightContent">  <!-- DOM元素引用 -->
          <!-- 文本输入框组件，只读显示LaTeX代码 -->
          <el-input
            v-model="latexCode"  
            type="textarea"      
            :rows="1"
            placeholder="LaTeX代码将在这里显示..."
            readonly             
            class="latex-textarea"
            :autosize="{ minRows: 10, maxRows: 50 }"  
          />
        </div>
      </el-aside>
    </el-container>

    <!-- 真值表界面模态框 - 弹出对话框 -->
    <el-dialog
      v-model="showTruthTable" 
      title="构造公式真值表"
      width="90%"
      :before-close="handleTruthTableClose" 
      class="truth-table-dialog"
    >
      <!-- 引入真值表界面组件 -->
      <truth-table-interface
        @close="showTruthTable = false"
        @formula-calculated="onFormulaCalculated"
      />
      <!-- @监听时间，formula-calculated由 TruthTableInterface.vue emit过来-->
    </el-dialog>

    <!-- 真值表构造器界面模态框 - 弹出对话框 -->
    <el-dialog
      v-model="showTruthTableConstructor"
      title="构造公式的真值表"
      width="90%"
      :before-close="handleTruthTableConstructorClose"
      class="truth-table-constructor-dialog"
    >
      <!-- 引入真值表构造器界面组件 -->
      <truth-table-constructor
        @close="showTruthTableConstructor = false"
        @formula-calculated="onFormulaCalculated"
      />
    </el-dialog>

    <!-- 主范式界面模态框 - 弹出对话框 -->
    <el-dialog
      v-model="showPrincipalNormalForm"
      title="计算与公式逻辑等值的范式"
      width="90%"
      :before-close="handlePrincipalNormalFormClose"
      class="principal-normal-form-dialog"
    >
      <!-- 引入主范式界面组件 -->
      <principal-normal-form-interface
        @close="showPrincipalNormalForm = false"
        @formula-calculated="onFormulaCalculated"
      />
      <!-- @监听时间，formula-calculated由 PrincipalNormalFormInterface.vue emit过来-->
    </el-dialog>

    <!-- 范式扩展界面模态框 - 弹出对话框 -->
    <el-dialog
      v-model="showNormalFormulaExpansion"
      title="将范式扩展为主范式"
      width="90%"
      :before-close="handleNormalFormulaExpansionClose"
      class="normal-form-expansion-dialog"
    >
      <!-- 引入范式扩展界面组件 -->
      <normal-formula-expansion-interface
        @close="showNormalFormulaExpansion = false"
        @result="onNormalFormulaExpansionResult"
      />
    </el-dialog>

    <!-- 公式语法分析界面模态框 - 弹出对话框 -->
    <el-dialog
      v-model="showFormulaSyntax"
      title="分析公式的语法"
      width="90%"
      :before-close="handleFormulaSyntaxClose"
      class="formula-syntax-dialog"
    >
      <!-- 引入公式语法分析界面组件 -->
      <formula-syntax-interface
        @close="showFormulaSyntax = false"
        @formula-calculated="onFormulaCalculated"
      />
      <!-- @监听时间，formula-calculated由 FormulaSyntaxInterface.vue emit过来-->
    </el-dialog>

    <!-- 计算公式真值界面模态框 - 弹出对话框 -->
    <el-dialog
      v-model="showTruthValue"
      title="计算公式的真值"
      width="90%"
      :before-close="handleTruthValueClose"
      class="truth-value-dialog"
    >
      <!-- 引入计算公式真值界面组件 -->
      <truth-value-calculator
        @close="showTruthValue = false"
        @formula-calculated="onFormulaCalculated"
      />
      <!-- @监听时间，formula-calculated由 TruthValueCalculator.vue emit过来-->
    </el-dialog>

    <!-- 等值演算过程检查界面模态框 - 弹出对话框 -->
    <el-dialog
      v-model="showEquivCalculusCheck"
      title="等值演算过程检查"
      width="90%"
      :before-close="handleEquivCalculusCheckClose"
      class="equiv-calculus-check-dialog"
    >
      <!-- 引入等值演算过程检查界面组件 -->
      <equiv-calculus-check-interface
        @close="showEquivCalculusCheck = false"
        @result="onEquivCalculusResult"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'
import MathRenderer from '@/components/common/MathRenderer.vue'
import TruthTableInterface from '@/components/logic/TruthTableInterface.vue'
import TruthTableConstructor from '@/components/logic/TruthTableConstructor.vue'
import PrincipalNormalFormInterface from '@/components/logic/PrincipalNormalFormInterface.vue'
import NormalFormulaExpansionInterface from '@/components/logic/NormalFormulaExpansionInterface.vue'
import FormulaSyntaxInterface from '@/components/logic/FormulaSyntaxInterface.vue'
import TruthValueCalculator from '@/components/logic/TruthValueCalculator.vue'
import EquivCalculusCheckInterface from '@/components/logic/EquivCalculusCheckInterface.vue'

// 响应式数据
const activeMenu = ref('')
const currentFormula = ref('\\forall x \\in S, P(x) \\rightarrow Q(x)')
const latexCode = ref('\\forall x \\in S, P(x) \\rightarrow Q(x)')

// 存储公式和真值表结果
const formulaResults = ref([])

// 控制真值表界面的显示
const showTruthTable = ref(false)

// 控制真值表构造器界面的显示
const showTruthTableConstructor = ref(false)

// 控制主范式界面的显示
const showPrincipalNormalForm = ref(false)

// 控制范式扩展界面的显示
const showNormalFormulaExpansion = ref(false)

// 控制公式语法分析界面的显示
const showFormulaSyntax = ref(false)

// 控制等值演算过程检查界面的显示
const showEquivCalculusCheck = ref(false)

// 控制计算公式真值界面的显示
const showTruthValue = ref(false)

// 左右面板内容区域的引用
const leftContent = ref(null)
const rightContent = ref(null)

// 处理左侧面板鼠标滚轮事件
const handleLeftWheel = (event) => {
  event.preventDefault()
  event.stopPropagation()

  if (leftContent.value) {
    leftContent.value.scrollTop += event.deltaY
  }
}

// 处理右侧面板鼠标滚轮事件
const handleRightWheel = (event) => {
  event.preventDefault()
  event.stopPropagation()

  if (rightContent.value) {
    rightContent.value.scrollTop += event.deltaY
  }
}

// 公式渲染完成回调
const onFormulaRendered = () => {
  console.log('Formula rendered successfully on left panel of mainview')
}

// 公式渲染错误回调
const onFormulaError = (error) => {
  console.error('Formula rendering error on left panel of mainview:', error)
  ElMessage.error('公式渲染失败')
}

// 处理菜单项点击
const handleMenuSelect = (index) => {
  console.log('Selected menu item:', index)

  switch (index) {
    // 命题逻辑菜单项
    case 'formula-syntax':
      showFormulaSyntax.value = true
      ElMessage.info('分析公式的语法界面已打开')
      break
    case 'truth-value':
      showTruthValue.value = true
      ElMessage.info('计算公式的真值界面已打开')
      break
    case 'truth-table':
      showTruthTableConstructor.value = true
      ElMessage.info('构造公式的真值表界面已打开')
      break
    case 'calculate-nf':
      showPrincipalNormalForm.value = true
      ElMessage.info('计算与公式逻辑等值的范式界面已打开')
      break
    case 'expand-nf':
      showNormalFormulaExpansion.value = true
      ElMessage.info('将范式扩展为主范式界面已打开')
      break
    case 'calculus-check':
      showEquivCalculusCheck.value = true
      break
    case 'argument-check':
      ElMessage.info('验证推理有效性论证检查功能')
      break

    // 集合关系函数菜单项
    case 'set-operation':
      ElMessage.info('单个集合的运算功能')
      break
    case 'set-expr-operation':
      ElMessage.info('集合表达式运算功能')
      break
    case 'relation-operation':
      ElMessage.info('单个关系的运算功能')
      break
    case 'relation-property':
      ElMessage.info('关系性质判断功能')
      break
    case 'relation-closure':
      ElMessage.info('关系闭包的计算功能')
      break
    case 'equivalence-relation':
      ElMessage.info('等价关系的计算功能')
      break
    case 'partial-order':
      ElMessage.info('偏序关系的计算功能')
      break
    case 'function':
      ElMessage.info('函数性质的判断功能')
      break

    // 组合计数菜单项
    case 'comb-calculator':
      ElMessage.info('排列组合数计算功能')
      break
    case 'expr-calculator':
      ElMessage.info('组合表达式计算功能')
      break
    case 'recu-expr-calculator':
      ElMessage.info('递归表达式计算功能')
      break
    case 'count-string':
      ElMessage.info('字符串计数功能')
      break
    case 'count-integer':
      ElMessage.info('基于整除性质的整数计数功能')
      break
    case 'count-solver':
      ElMessage.info('不定方程非负整数解计数功能')
      break
    case 'count-function':
      ElMessage.info('不同性质的函数计数功能')
      break
    case 'generate-permutation':
      ElMessage.info('排列的生成功能')
      break
    case 'generate-combination':
      ElMessage.info('不重复组合的生成功能')
      break
    case 'generate-repcomb':
      ElMessage.info('允许重复组合的生成功能')
      break

    // 图与树菜单项
    case 'graph-travel':
      ElMessage.info('图的遍历功能')
      break
    case 'tree-travel':
      ElMessage.info('树的遍历功能')
      break
    case 'shortest-path':
      ElMessage.info('带权图最短路径计算功能')
      break
    case 'spanning-tree':
      ElMessage.info('带权图最小生成树计算功能')
      break
    case 'huffman-tree':
      ElMessage.info('哈夫曼树构造功能')
      break
    case 'special-graph':
      ElMessage.info('展示特殊的图功能')
      break

    // 代数结构菜单项
    case 'binary-operator':
      ElMessage.info('运算性质的判断功能')
      break
    case 'group-um':
      ElMessage.info('群U(m)及其子群与陪集功能')
      break
    case 'group-perm':
      ElMessage.info('置换群及其子群与陪集功能')
      break
    case 'lattice':
      ElMessage.info('偏序关系是否格的判断功能')
      break
    case 'boolean':
      ElMessage.info('整除与布尔代数的判断功能')
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

// 处理真值表界面关闭
const handleTruthTableClose = () => {
  showTruthTable.value = false
}

// 处理真值表构造器界面关闭
const handleTruthTableConstructorClose = () => {
  showTruthTableConstructor.value = false
}

// 处理主范式界面关闭
const handlePrincipalNormalFormClose = () => {
  showPrincipalNormalForm.value = false
}

// 处理范式扩展界面关闭
const handleNormalFormulaExpansionClose = () => {
  showNormalFormulaExpansion.value = false
}

// 处理公式语法分析界面关闭
const handleFormulaSyntaxClose = () => {
  showFormulaSyntax.value = false
}

// 处理计算公式真值界面关闭
const handleTruthValueClose = () => {
  showTruthValue.value = false
}

const handleEquivCalculusCheckClose = () => {
  showEquivCalculusCheck.value = false
}

// 处理公式计算完成事件
const onFormulaCalculated = (result) => {
  // 确保结果有索引信息，如果没有则自动分配
  if (!result.index) {
    result.index = formulaResults.value.length + 1
  }

  // 直接添加到结果列表，包含所有数据
  formulaResults.value.push(result)
  // 更新当前显示的公式
  currentFormula.value = result.formula

  console.log('MainView: 添加完整公式结果:', {
    formula: result.formula,
    index: result.index,
    hasSyntaxData: !!result.syntaxData
  })

  // 生成LaTeX代码（公式、真值表和严格形式公式）
  const latexString = generateLaTeXCode(result)
  // 追加到LaTeX代码区域，不清空之前的内容
  if (latexCode.value) {
    latexCode.value += '\n\n' + latexString
  } else {
    latexCode.value = latexString
  }

  // 根据结果类型显示不同的成功消息
  if (result.cnfResult || result.dnfResult) {
    ElMessage.success('范式计算结果已添加到主界面')
  } else if (result.syntaxData && !result.tableData) {
    ElMessage.success('公式语法分析结果已添加到主界面')
  } else if (result.tableData && !result.syntaxData) {
    ElMessage.success('公式和真值表已添加到主界面')
  } else if (result.truthValue !== undefined) {
    ElMessage.success('公式真值计算结果已添加到主界面')
  } else {
    ElMessage.success('公式分析结果已添加到主界面')
  }
}

// 处理范式扩展结果
const onEquivCalculusResult = (result) => {
  if (result && result.data) {
    // 创建一个符合现有格式的结果对象
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

    // 添加到结果列表
    formulaResults.value.push(formattedResult)

    // 更新当前显示的公式
    currentFormula.value = `等值演算检查步骤${result.data.stepNumber}`

    console.log('MainView: 添加等值演算检查结果:', formattedResult)

    // 生成LaTeX代码
    const latexString = generateLaTeXCode(formattedResult)
    // 追加到LaTeX代码区域
    if (latexCode.value) {
      latexCode.value += '\n\n' + latexString
    } else {
      latexCode.value = latexString
    }
  }
}

const onNormalFormulaExpansionResult = (result) => {
  if (result && result.data) {
    // 创建一个符合现有格式的结果对象
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

    // 添加到结果列表
    formulaResults.value.push(formattedResult)

    // 更新当前显示的公式
    currentFormula.value = result.data.originalFormula

    console.log('MainView: 添加范式扩展结果:', formattedResult)

    // 生成LaTeX代码
    const latexString = generateLaTeXCode(formattedResult)
    // 追加到LaTeX代码区域
    if (latexCode.value) {
      latexCode.value += '\n\n' + latexString
    } else {
      latexCode.value = latexString
    }

    ElMessage.success('范式扩展结果已添加到主界面')
  }
}

// AST图片加载成功处理
const handleASTImageLoad = (event) => {
  console.log('MainView: AST图片加载成功:', event.target.src)
}

// AST图片加载失败处理
const handleASTImageError = (event) => {
  console.error('MainView: AST图片加载失败:', event.target.src)
  ElMessage.error('抽象语法树图片加载失败')
}

// 获取公式类型标签样式
const getFormulaTypeTag = (type) => {
  switch (type) {
    case '重言式': return 'success'
    case '可满足式': return 'warning'
    case '矛盾式': return 'danger'
    default: return 'info'
  }
}

// 清空公式内容
const clearFormulaContent = async () => {
  try {
    // 同时清理后端data目录
    await cleanupBackendData()

    // 清空前端数据
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
    // 使用绝对路径，确保移动端也能正确访问
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
    // 不抛出错误，避免影响前端清空操作
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

// 生成LaTeX代码（公式和真值表），用于右侧面板展示
const generateLaTeXCode = (result) => {
  let latexCode = `\\begin{array}{c}\n\\text{公式: } ${result.formula}\n\\end{array}\n\n`

  if (result.formulaType) {
    latexCode += `\\begin{array}{c}\n\\text{公式类型: } ${result.formulaType}\n\\end{array}\n\n`
  }

  
  // 添加严格形式公式的LaTeX代码
  if (result.syntaxData) {
    latexCode += `\\begin{array}{c}\n\\text{严格形式公式:} ${result.syntaxData.strictForm} \\text{，简化写为:} ${result.syntaxData.simpleForm}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n\\text{语法公式类型: } ${result.syntaxData.formulaType}\n\\end{array}\n\n`
  }

  // 添加抽象语法树的LaTeX代码
  if (result.astData) {
    if (result.astData.imageUrl) {
      latexCode += `\\begin{array}{c}\n\\text{抽象语法树已生成 (图片路径: ${result.astData.imageUrl})}\n\\end{array}\n\n`
    } else if (result.astData.error) {
      latexCode += `\\begin{array}{c}\n\\text{抽象语法树生成失败: ${result.astData.error}}\n\\end{array}\n\n`
    }
  }

  // 生成真值表LaTeX代码 - 优先使用新的latexTable格式
  if (result.tableData && result.tableData.latexTable) {
    // 直接使用后端返回的LaTeX表格
    latexCode += result.tableData.latexTable
  }
  // 保持原有的表格格式作为后备
  else if (result.tableData && result.tableData.headers && result.tableData.rows) {
    const headers = result.tableData.headers
    const rows = result.tableData.rows

    // 计算列数
    const colCount = headers.length
    const colSpec = 'c'.repeat(colCount)

    latexCode += `\\begin{array}{${colSpec}}\n`

    // 添加表头 - 使用数学符号格式
    const formattedHeaders = headers.map(header => {
      // 如果是单个变量，添加$符号
      if (/^[a-zA-Z]$/.test(header)) {
        return `$${header}$`
      }
      return header
    })
    latexCode += formattedHeaders.join(' & ') + ' \\\\\n'
    latexCode += '\\hline\n'

    // 添加表格行 - 使用粗体数学符号格式
    rows.forEach(row => {
      const rowData = [...row.variableValues, row.resultValue]
      const formattedRow = rowData.map(cell => {
        // 如果是T/F值，转换为0/1并添加粗体数学符号
        if (cell === 'T' || cell === 'F') {
          const value = cell === 'T' ? '1' : '0'
          return `$\\mathbf{${value}}$`
        }
        // 如果是单个变量，添加$符号
        if (/^[a-zA-Z]$/.test(cell)) {
          return `$${cell}$`
        }
        return cell
      })
      latexCode += formattedRow.join(' & ') + ' \\\\\n'
    })

    latexCode += '\\end{array}'
  }

  // 添加真值计算结果的LaTeX代码
  if (result.truthValue !== undefined) {
    latexCode += `\\begin{array}{c}\n\\text{公式真值: } ${result.formula} = ${result.truthValue ? '\\mathbf{1}' : '\\mathbf{0}'}\n\\end{array}\n\n`
  }

  // 添加范式计算结果的LaTeX代码
  if (result.cnfResult || result.dnfResult) {
    // CNF 结果
    if (result.cnfResult) {
      latexCode += `\\begin{array}{c}\n\\text{合取范式(CNF)计算结果:}\n\\end{array}\n\n`

      // CNF 计算步骤
      if (result.cnfSteps && result.cnfSteps.length > 0) {
        latexCode += `\\begin{array}{c}\n\\text{CNF计算步骤:}\n\\end{array}\n\n`
        result.cnfSteps.forEach(step => {
          latexCode += `\\begin{array}{c}\n\\text{${step.comment}}\n${step.formula}\n\\end{array}\n\n`
        })
      }

      // CNF 最终结果
      latexCode += `\\begin{array}{c}\n\\text{最终简化为CNF:}\n${result.cnfResult.finalFormula}\n\\end{array}\n\n`
    }

    // DNF 结果
    if (result.dnfResult) {
      latexCode += `\\begin{array}{c}\n\\text{析取范式(DNF)计算结果:}\n\\end{array}\n\n`

      // DNF 计算步骤
      if (result.dnfSteps && result.dnfSteps.length > 0) {
        latexCode += `\\begin{array}{c}\n\\text{DNF计算步骤:}\n\\end{array}\n\n`
        result.dnfSteps.forEach(step => {
          latexCode += `\\begin{array}{c}\n\\text{${step.comment}}\n${step.formula}\n\\end{array}\n\n`
        })
      }

      // DNF 最终结果
      latexCode += `\\begin{array}{c}\n\\text{最终简化为DNF:}\n${result.dnfResult.finalFormula}\n\\end{array}\n\n`
    }

    // CNF 扩展步骤
    if (result.cnfExpansionSteps && result.cnfExpansionSteps.length > 0) {
      latexCode += `\\begin{array}{c}\n\\text{主合取范式扩展步骤:}\n\\end{array}\n\n`
      result.cnfExpansionSteps.forEach(step => {
        latexCode += `\\begin{array}{c}\n\\text{${step.expansionDescription}}\n${step.formula}[${step.formulaCode}]\n\\text{${step.resultDescription}}\n${step.resultCodes}\n\\end{array}\n\n`
      })
    }

    // DNF 扩展步骤
    if (result.dnfExpansionSteps && result.dnfExpansionSteps.length > 0) {
      latexCode += `\\begin{array}{c}\n\\text{主析取范式扩展步骤:}\n\\end{array}\n\n`
      result.dnfExpansionSteps.forEach(step => {
        latexCode += `\\begin{array}{c}\n\\text{${step.expansionDescription}}\n${step.formula}[${step.formulaCode}]\n\\text{${step.resultDescription}}\n${step.resultCodes}\n\\end{array}\n\n`
      })
    }

    // 最后显示主范式结果
    if (result.cnfResult && (result.cnfResult.pcnf || result.cnfResult.pdnf)) {
      // 主合取范式
      if (result.cnfResult.pcnf) {
        latexCode += `\\begin{array}{c}\n\\text{主合取范式(PCNF):}\n${result.cnfResult.pcnf}\n\\end{array}\n\n`
      }

      // 对应的主析取范式
      if (result.cnfResult.pdnf) {
        latexCode += `\\begin{array}{c}\n\\text{对应的主析取范式(PDNF):}\n${result.cnfResult.pdnf}\n\\end{array}\n\n`
      }
    }

    if (result.dnfResult && (result.dnfResult.pdnf || result.dnfResult.pcnf)) {
      // 主析取范式
      if (result.dnfResult.pdnf) {
        latexCode += `\\begin{array}{c}\n\\text{主析取范式(PDNF):}\n${result.dnfResult.pdnf}\n\\end{array}\n\n`
      }

      // 对应的主合取范式
      if (result.dnfResult.pcnf) {
        latexCode += `\\begin{array}{c}\n\\text{对应的主合取范式(PCNF):}\n${result.dnfResult.pcnf}\n\\end{array}\n\n`
      }
    }
  }

  // 添加范式扩展结果的LaTeX代码
  if (result.type === 'normal-form-expansion') {
    latexCode += `\\begin{array}{c}\n\\text{${result.targetType}扩展结果:}\n\\end{array}\n\n`

    // 原始公式和变量集
    latexCode += `\\begin{array}{c}\n\\text{原始公式: } ${result.originalFormula}\n\\text{变量集: } \\{${result.variableSet}\\}\n\\end{array}\n\n`

    // 扩展步骤
    if (result.expansionSteps && result.expansionSteps.length > 0) {
      latexCode += `\\begin{array}{c}\n\\text{扩展步骤:}\n\\end{array}\n\n`
      result.expansionSteps.forEach(step => {
        latexCode += `\\begin{array}{c}\n\\text{${step.description}}\n${step.formula}[${step.binaryCode}]\n\\text{得到} ${step.resultCodes}\n\\end{array}\n\n`
      })
    }

    // 最终结果
    if (result.pdnfResult) {
      latexCode += `\\begin{array}{c}\n\\text{最终的主析取范式(PDNF):}\n${result.pdnfResult}\n\\end{array}\n\n`
    }
    if (result.pcnfResult) {
      latexCode += `\\begin{array}{c}\n\\text{最终的主合取范式(PCNF):}\n${result.pcnfResult}\n\\end{array}\n\n`
    }
  }

  // 处理等值演算检查结果
  if (result.type === 'equiv-calculus-check') {
    latexCode += `\\begin{array}{c}\n\\text{等值演算过程检查结果:}\n\\end{array}\n\n`

    // 检查步骤
    if (result.steps && result.steps.length > 0) {
      latexCode += `\\begin{array}{c}\n\\text{演算步骤${result.stepNumber}: 检查以下等值演算过程}\n\\end{array}\n\n`

      result.steps.forEach((step, index) => {
        if (index === 0) {
          latexCode += `\\begin{array}{c}\n\\quad${step.formula}`
          if (step.comment) {
            latexCode += ` // ${step.comment}`
          }
          latexCode += `\\end{array}\n\n`
        } else {
          latexCode += `\\begin{array}{c}\n\\quad\\quad\\equiv\\quad\\quad${step.formula}`
          if (step.comment) {
            latexCode += ` // ${step.comment}`
          }
          latexCode += `\\end{array}\n\n`
        }
      })
    }

    // 检查结果
    if (result.valid) {
      latexCode += `\\begin{array}{c}\n\\text{✓ 检查通过: 通过真值表检验，上述等值演算过程没有错误。}\n\\end{array}\n\n`
    } else {
      latexCode += `\\begin{array}{c}\n\\text{✗ 检查失败: ${result.errorMessage || '等值演算过程存在错误'}}\n\\end{array}\n\n`

      // 反例信息
      if (result.counterExample) {
        latexCode += `\\begin{array}{c}\n\\text{反例: 在真值赋值 ${result.counterExample} 下，以下公式不是重言式:}\n\\end{array}\n\n`
        if (result.checkingFormula) {
          latexCode += `\\begin{array}{c}\n${result.checkingFormula}\n\\end{array}\n\n`
        }
      }
    }
  }

  // 添加真值表结果的LaTeX代码（范式计算的真值表）
  if (result.truthTable) {
    latexCode += `\\begin{array}{c}\n\\text{真值表计算结果:}\n\\end{array}\n\n`
    latexCode += result.truthTable + '\n\n'
  }

  // 添加详细计算步骤的LaTeX代码
  if (result.detailedSteps && result.detailedSteps.length > 0) {
    result.detailedSteps.forEach(step => {
      if (step.formula) {
        latexCode += `\\begin{array}{c}\n\\text{${step.explanation || '计算步骤'}}\n${step.formula}\n\\end{array}\n\n`
      }
    })
  }

  return latexCode
}


// 显示关于对话框
const showAboutDialog = () => {
  ElMessage.info('离散数学教学演示辅助工具展示(Deedm)\n\n(Demonstrator of Examples in Elementary Discrete Mathematics)\n\n(C) 版权所有，中山大学， 2020--2030\n\n中山大学数学学院和中山大学数据科学与计算机学院')
}

// 显示使用说明对话框
const showUsageDialog = () => {
  ElMessage.info('Deedm(Demonstrator of Examples in Elementary Discrete Mathematics)是一个用于展示教材《离散数学（上）：集合、关系、函数、组合计数》（中山大学数学学院，2018）中的计算例子的工具。')
}

onMounted(() => {
  // 组件挂载后的初始化逻辑
  console.log('Main view mounted')
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

.left-panel,
.right-panel {
  width: 50%;
  height: 100%;
  border: 1px solid #e4e7ed;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  min-width: 300px;
  flex: 1;
}

.left-panel {
  border-right: 2px solid #dcdfe6;
}

.panel-header {
  background: #f5f7fa;
  padding: 1rem;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.panel-header h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 1.1rem;
  flex: 1;
}

.panel-content {
  flex: 1;
  overflow-y: auto;
  padding: 1rem;
  scroll-behavior: smooth;
}

.formula-display {
  min-height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  background: #fafafa;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.latex-textarea {
  width: 100%;
  height: 100%;
  min-height: 200px;
}

.latex-textarea :deep(.el-textarea__inner) {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.5;
  resize: none;
  border: none;
  background: #fafafa;
  height: 100% !important;
  min-height: 200px;
  max-height: 80vh;
}

/* 滚动条样式 */
.panel-content::-webkit-scrollbar {
  width: 8px;
}

.panel-content::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.panel-content::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.panel-content::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}

/* 公式结果样式 */
.formula-results {
  margin-top: 2rem;
}

.result-item {
  margin-bottom: 2rem;
  padding: 1.5rem;
  background: white;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.result-formula {
  margin-bottom: 1rem;
  font-size: 1.1rem;
  font-weight: 500;
}

.result-formula strong {
  color: #2c3e50;
}

.truth-table {
  margin: 1rem 0;
  background: white;
  padding: 1rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

/* 新的LaTeX表格容器样式 */
.truth-table-container {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.truth-table-container .truth-table-content {
  width: 100%;
  max-width: 100%;
  overflow-x: auto;
}

/* 后备样式 */
.truth-table-legacy,
.truth-table-fallback {
  width: 100%;
}

/* HTML表格样式 */
.truth-table-html {
  width: 100%;
  border-collapse: collapse;
  border: 2px solid #dee2e6;
  border-radius: 8px;
  overflow: hidden;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
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

/* 真值计算结果样式 */
.truth-value-result {
  margin: 1rem 0;
  padding: 1rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.truth-value-display {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.truth-value-label {
  font-weight: 600;
  color: #2c3e50;
}

.truth-value-tag {
  font-size: 1.1rem;
  font-weight: bold;
}

/* 详细计算步骤样式 */
.detailed-steps {
  margin: 1rem 0;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.steps-content {
  background: white;
  padding: 1rem;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.step-item {
  margin-bottom: 1rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #dee2e6;
}

.step-item:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.step-explanation {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 1rem;
}

.step-formula {
  margin: 1rem 0;
  padding: 1rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}

.formula-type {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-top: 1rem;
}

.type-tag {
  margin-left: 0.5rem;
}

/* 严格形式公式结果样式 */
.syntax-content {
  background: white;
  padding: 1rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  margin: 1rem 0;
}

.syntax-title {
  margin: 0 0 1rem 0;
  color: #2c3e50;
  font-size: 1rem;
  font-weight: 600;
}

/* AST图片样式 */
.ast-content {
  background: white;
  padding: 1rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  margin: 1rem 0;
}

.ast-title {
  margin: 0 0 1rem 0;
  color: #2c3e50;
  font-size: 1rem;
  font-weight: 600;
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

/* 范式计算结果样式 */
.normal-form-results {
  margin: 1rem 0;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
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

.expansion-step-inline {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  margin-bottom: 0.75rem;
  padding: 0.5rem 1rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #d0e3ff;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.expansion-step-inline:last-child {
  margin-bottom: 0;
}

.step-description {
  font-weight: 600;
  font-size: 0.95em;
  white-space: nowrap;
}

.step-formula-inline {
  background: #f8f9ff;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  border: 1px solid #d0e3ff;
}

.formula-code-inline {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  background: #f1f3f4;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.9em;
  color: #5f6368;
  border: 1px solid #dadce0;
  white-space: nowrap;
}

.result-codes-inline {
  background: #f8fff8;
  padding: 0.25rem 0.5rem;
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

/* 响应式设计 */
@media (max-width: 768px) {
  .main-container {
    flex-direction: column;
  }

  .left-panel,
  .right-panel {
    width: 100%;
    height: 50%;
    min-width: unset;
  }

  .left-panel {
    border-right: 1px solid #e4e7ed;
    border-bottom: 2px solid #dcdfe6;
  }

  .latex-textarea :deep(.el-textarea__inner) {
    max-height: 40vh;
  }

  .normal-form-result {
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

  .expansion-step-inline {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.25rem;
  }

  .step-description {
    white-space: normal;
  }
}

@media (max-width: 480px) {
  .panel-header {
    padding: 0.5rem;
  }

  .panel-header h3 {
    font-size: 1rem;
  }

  .panel-content {
    padding: 0.5rem;
  }

  .latex-textarea :deep(.el-textarea__inner) {
    font-size: 12px;
  }
}

/* 范式扩展结果样式 */
.normal-form-expansion-results {
  margin: 1rem 0;
  padding: 1.5rem;
  background: linear-gradient(135deg, #f6f9ff 0%, #e8f4fd 100%);
  border-radius: 8px;
  border: 2px solid #4a90e2;
  box-shadow: 0 4px 12px rgba(74, 144, 226, 0.15);
}

.expansion-result-title {
  margin: 0 0 1rem 0;
  color: #2c3e50;
  font-size: 1.2rem;
  font-weight: 600;
  text-align: center;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid #4a90e2;
}

.expansion-info {
  margin: 1rem 0;
  padding: 1rem;
  background: white;
  border-radius: 6px;
  border: 1px solid #d0e3ff;
}

.info-item {
  margin: 0.5rem 0;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.info-item strong {
  color: #2c3e50;
  min-width: 100px;
}

.expansion-steps-detail {
  margin: 1.5rem 0;
  padding: 1rem;
  background: #f8fbff;
  border-radius: 6px;
  border: 1px solid #b8d4f1;
}

.steps-title {
  margin: 0 0 1rem 0;
  color: #2c3e50;
  font-size: 1.1rem;
  font-weight: 600;
  text-align: center;
}

.expansion-step-detail {
  margin: 1rem 0;
  padding: 1rem;
  background: white;
  border-radius: 6px;
  border: 1px solid #d0e3ff;
  box-shadow: 0 2px 6px rgba(74, 144, 226, 0.1);
}

.step-header {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  margin-bottom: 0.75rem;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.step-formula-detail {
  margin: 0 0.5rem;
  font-size: 1.1em;
}

.binary-code {
  color: #e74c3c;
  font-weight: 600;
  font-family: 'Courier New', monospace;
  background: #ffe6e6;
  padding: 2px 6px;
  border-radius: 4px;
}

.step-result {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 0.5rem;
  padding: 0.5rem 0;
  border-top: 1px dashed #d0e3ff;
}

.result-codes-detail {
  font-size: 1.1em;
  color: #27ae60;
}

.final-expansion-results {
  margin: 1.5rem 0;
  padding: 1.5rem;
  background: linear-gradient(135deg, #e8f5e8 0%, #d4f1d4 100%);
  border-radius: 8px;
  border: 2px solid #27ae60;
}

.final-pdnf,
.final-pcnf {
  margin: 1rem 0;
  padding: 1rem;
  background: white;
  border-radius: 6px;
  border: 1px solid #c3e6c3;
}

.final-title {
  margin: 0 0 0.75rem 0;
  color: #27ae60;
  font-size: 1.1rem;
  font-weight: 600;
  text-align: center;
}

.final-formula {
  font-size: 1.2em;
  color: #2c3e50;
  text-align: center;
  padding: 0.5rem;
  background: #f8fff8;
  border-radius: 4px;
  border: 1px solid #e6f7e6;
}

/* 等值演算检查结果样式 */
.equiv-calculus-results {
  margin: 1rem 0;
  padding: 1.5rem;
  background: linear-gradient(135deg, #fff8f0 0%, #fef3e2 100%);
  border-radius: 8px;
  border: 2px solid #ff9800;
  box-shadow: 0 4px 12px rgba(255, 152, 0, 0.15);
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
</style>