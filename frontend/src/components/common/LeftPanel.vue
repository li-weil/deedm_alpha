<template>
  <div class="left-panel" @wheel="handleWheel">
    <div class="panel-header">
      <h3>公式内容</h3>
      <el-button size="small" type="warning" @click="handleClear">
        <el-icon><Delete /></el-icon>
        清空
      </el-button>
    </div>
    <div class="panel-content" ref="content">

      <!-- 显示所有公式和真值表 -->
      <div v-if="formulaResults.length > 0" class="formula-results">
        <div v-for="(result, index) in formulaResults" :key="index" class="result-item">
          <!-- 群U(m)类型使用独立容器，不进行公式清理 -->
          <div v-if="result.type === 'groupUm'" class="result-formula group-um-formula">
            <strong>公式 {{ index + 1 }}: </strong>
            <math-renderer
              :formula="result.formula"
              :type="'katex'"
              :display-mode="true"
            />
          </div>

          <!-- 其他类型使用原有容器 -->
          <div v-else class="result-formula">
            <strong>公式 {{ index + 1 }}: </strong>
            <math-renderer
              :formula="cleanFormulaForDisplay(result.formula)"
              :type="'mathjax'"
              :display-mode="false"
            />
          </div>

          <!-- 范式扩展结果  -->
          <div v-if="result.type === 'normal-form-expansion'" class="normal-form-expansion-result">
            <h4 class="result-title">扩展变量集：{{ result.variableSet }}</h4>

            <div v-if="result.expansionSteps && result.expansionSteps.length > 0" class="expansion-steps">
              <h5 class="expansion-title">{{ result.targetType }}扩展步骤：</h5>
              <div v-for="(step, stepIndex) in result.expansionSteps" :key="'expansion-step-' + stepIndex" class="expansion-step">
                <span class="step-description">{{ step.description }}</span>
                <math-renderer :formula="step.formula" :type="'katex'" :display-mode="false" />
                <span v-if="step.binaryCode" class="formula-code">[{{ step.binaryCode }}]</span>
                <span class="step-result">得到</span>
                <math-renderer :formula="step.resultCodes" :type="'mathjax'" :display-mode="false" />
              </div>
            </div>

            <div v-if="result.pdnfResult" class="pnf-result">
              <h5>最终的主析取范式(PDNF)是：</h5>
              <math-renderer :formula="result.pdnfResult" :type="'mathjax'" :display-mode="false" />
            </div>

            <div v-if="result.pcnfResult" class="pcnf-result">
              <h5>相应的主合取范式(PCNF)是：</h5>
              <math-renderer :formula="result.pcnfResult" :type="'mathjax'" :display-mode="false" />
            </div>
          </div>

          <!-- 等值演算检查结果 -->
          <div v-else-if="result.type === 'equiv-calculus-check'" class="equiv-calculus-result">
            <h4 class="result-title">检查演算步骤</h4>

            <div v-for="(step, index) in result.steps" :key="index" class="calculus-step">
              <div v-if="index === 0" class="step-first">
                <math-renderer :formula="step.formula" :type="'katex'" :display-mode="false" />
                <span v-if="step.comment" class="step-comment">// {{ step.comment }}</span>
              </div>
              <div v-else class="step-subsequent">
                <span class="equiv-prefix">≡</span>
                <math-renderer :formula="step.formula" :type="'katex'" :display-mode="false" />
                <span v-if="step.comment" class="step-comment">// {{ step.comment }}</span>
              </div>
            </div>

            <div class="check-result">
              <el-alert v-if="result.valid" title="✓ 检查通过" type="success" description="通过真值表检验，上述等值演算过程没有错误。" :closable="false" show-icon />
              <div v-else class="invalid-result">
                <el-alert title="✗ 检查失败" type="error" :description="result.errorMessage" :closable="false" show-icon />
                <div v-if="result.counterExample" class="counter-example">
                  <h5>反例：</h5>
                  <p>在真值赋值 {{ result.counterExample }} 下，以下公式不是重言式：</p>
                  <div class="checking-formula">
                    <math-renderer :formula="result.checkingFormula" :type="'katex'" :display-mode="true" />
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 推理有效性论证检查结果  -->
          <div v-else-if="result.type === 'reason-argument-check'" class="reason-argument-result">
            <h4 class="result-title">检查推理步骤</h4>

            <div v-if="result.latexString" class="reasoning-overview">
              <h5>推理关系：</h5>
              <math-renderer :formula="result.latexString" :type="'katex'" :display-mode="true" />
            </div>

            <div v-for="(step, index) in result.steps" :key="index" class="reasoning-step">
              <span class="step-number">({{ step.serialNo }})</span>
              <math-renderer :formula="step.formula" :type="'katex'" :display-mode="false" />
              <span v-if="step.ruleName" class="step-rule">// {{ step.prevSN }}{{ step.ruleName }}</span>
            </div>

            <div v-if="result.checkSteps && result.checkSteps.length > 0" class="check-process">
              <h5>检查过程：</h5>
              <div v-for="(step, index) in result.checkSteps" :key="index" class="check-step">
                <span class="check-text">检验步骤({{ step.serialNo }}){{ step.checkType }}</span>
                <math-renderer :formula="step.formula" :type="'katex'" :display-mode="false" />
              </div>
            </div>

            <div class="check-result">
              <el-alert v-if="result.valid" title="✓ 检查通过" type="success" description="通过真值表检验，上述推理证明过程没有错误。" :closable="false" show-icon />
              <div v-else class="invalid-result">
                <el-alert title="✗ 检查失败" type="error" :description="result.errorMessage" :closable="false" show-icon />
                <div v-if="result.counterExample" class="counter-example">
                  <h5>反例：</h5>
                  <p>在真值赋值 {{ result.counterExample }} 下，以下公式不是重言式：</p>
                  <div class="checking-formula">
                    <math-renderer :formula="result.checkingFormula" :type="'katex'" :display-mode="true" />
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

          </div>

          <!-- 显示详细计算过程 -->
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

          <!-- 显示特殊图结果 -->
          <div v-else-if="result.type === 'special-graph'" class="special-graph-result">
            <h5 class="result-title">特殊图展示结果：</h5>

            <!-- 图形展示网格 -->
            <div v-if="result.graphResults && result.graphResults.length > 0" class="graphs-grid">
              <div v-for="graph in result.graphResults" :key="graph.graphType" class="graph-item">
                <div class="graph-header">
                  <h6>{{ graph.description }}：</h6>
                  <math-renderer
                    :formula="graph.name"
                    :type="'mathjax'"
                    :display-mode="false"
                    class="graph-name"
                  />
                </div>

                <div class="graph-content">
                  <!-- 图形可视化 -->
                  <div v-if="graph.generated && graph.imageUrl" class="graph-visualization">
                    <div class="graph-image-container">
                      <img
                        :src="graph.imageUrl"
                        :alt="graph.description"
                        class="graph-image"
                        @error="handleGraphImageError"
                      />
                    </div>
                  </div>

                  <!-- 生成失败提示 -->
                  <div v-else class="graph-error">
                    <el-alert
                      :title="`${graph.description}：无法生成`"
                      type="error"
                      :closable="false"
                      show-icon
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 显示集合运算结果 -->
          <div v-else-if="result.type === 'set-operation'" class="set-operation-result">
            <h5 class="result-title">集合运算分析结果：</h5>

            <!-- 集合基本信息 -->
            <div class="result-basic">
              <h6>输入集合：</h6>
              <math-renderer
                :formula="result.formula"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
            </div>

            <!-- 交集结果 -->
            <div v-if="result.intersectionResult" class="operation-result">
              <h6>交集运算：</h6>
              <math-renderer
                :formula="result.intersectionResult"
                :type="'mathjax'"
                :display-mode="true"
                class="operation-formula"
              />
            </div>

            <!-- 并集结果 -->
            <div v-if="result.unionResult" class="operation-result">
              <h6>并集运算：</h6>
              <math-renderer
                :formula="result.unionResult"
                :type="'mathjax'"
                :display-mode="true"
                class="operation-formula"
              />
            </div>

            <!-- 差集结果 -->
            <div v-if="result.subtractResult" class="operation-result">
              <h6>差集运算：</h6>
              <math-renderer
                :formula="result.subtractResult"
                :type="'mathjax'"
                :display-mode="true"
                class="operation-formula"
              />
            </div>

            <!-- 补集结果 -->
            <div v-if="result.complementAResult || result.complementBResult" class="operation-result">
              <h6>补集运算：</h6>
              <div v-if="result.complementAResult">
                <math-renderer
                  :formula="result.complementAResult"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="operation-formula"
                />
              </div>
              <div v-if="result.complementBResult">
                <math-renderer
                  :formula="result.complementBResult"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="operation-formula"
                />
              </div>
            </div>

            <!-- 对称差结果 -->
            <div v-if="result.differenceResult" class="operation-result">
              <h6>对称差运算：</h6>
              <math-renderer
                :formula="result.differenceResult"
                :type="'mathjax'"
                :display-mode="true"
                class="operation-formula"
              />
            </div>

            <!-- 幂集结果 -->
            <div v-if="result.powerSetAResult && result.powerSetAResult.length > 0" class="power-set-result">
              <h6>幂集运算：</h6>
              <div class="power-set-item">
                <h6>集合A的幂集：</h6>
                <math-renderer
                  :formula="`\\wp(A) = \\{` + result.powerSetAResult.join(', ') + `\\}`"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="power-set-formula"
                />
              </div>
              <div v-if="result.powerSetBResult && result.powerSetBResult.length > 0" class="power-set-item">
                <h6>集合B的幂集：</h6>
                <math-renderer
                  :formula="`\\wp(B) = \\{` + result.powerSetBResult.join(', ') + `\\}`"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="power-set-formula"
                />
              </div>
            </div>
          </div>

          <!-- 格判断结果 -->
          <div v-else-if="result.type === 'lattice-judge'" class="lattice-judge-result">
            <h5 class="result-title">格判断分析结果：</h5>

            <!-- 输入信息 -->
            <div class="result-basic">
              <h6>输入信息：</h6>
              <math-renderer
                :formula="result.formula"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
            </div>

            <!-- 哈斯图 -->
            <div v-if="result.hasseDiagramUrl" class="hasse-diagram-result">
              <h6>哈斯图：</h6>
              <div class="diagram-container">
                <img :src="result.hasseDiagramUrl" alt="哈斯图" class="diagram-image" />
              </div>
            </div>

            <!-- 偏序关系判断 -->
            <div class="partial-order-result">
              <h6>偏序关系判断：</h6>
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

            <!-- 格判断结果 -->
            <div v-if="result.isPartialOrder" class="lattice-result">
              <h6>格判断：</h6>
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

            <!-- 格运算表 -->
            <div v-if="result.isLattice && result.supOperatorTable" class="operator-tables-result">
              <h6>格运算表：</h6>
              <div class="operator-table">
                <h6>最小上界运算：</h6>
                <math-renderer
                  :formula="result.supOperatorTable"
                  :type="'katex'"
                  :display-mode="true"
                  class="operator-formula"
                />
              </div>
              <div class="operator-table">
                <h6>最大下界运算：</h6>
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
              <h6>分配格判断：</h6>
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

            <!-- 有界格判断 -->
            <div v-if="result.isLattice && result.isBounded !== null" class="bounded-result">
              <h6>有界格判断：</h6>
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

            <!-- 有补格判断 -->
            <div v-if="result.isLattice && result.isComplemented !== null" class="complemented-result">
              <h6>有补格判断：</h6>
              <el-tag :type="result.isComplemented ? 'success' : 'danger'">
                {{ result.isComplemented ? '是有补格' : '不是有补格' }}
              </el-tag>
              <div v-if="result.complementedReason" class="reason-text">
                原因：{{ result.complementedReason }}
              </div>
              <div v-if="result.isComplemented && result.complements" class="complements-list">
                <h6>元素补元列表：</h6>
                <div v-for="(complement, idx) in result.complements" :key="idx" class="complement-item">
                  元素 <math-renderer :formula="complement.element" :type="'mathjax'" :display-mode="false" />
                  的补元：<math-renderer :formula="complement.complementElements" :type="'mathjax'" :display-mode="false" />
                </div>
              </div>
            </div>

            <!-- 布尔代数判断 -->
            <div v-if="result.isLattice && result.isBooleanAlgebra !== null" class="boolean-result">
              <h6>布尔代数判断：</h6>
              <el-tag :type="result.isBooleanAlgebra ? 'success' : 'danger'">
                {{ result.isBooleanAlgebra ? '是布尔代数' : '不是布尔代数' }}
              </el-tag>
              <div v-if="result.booleanAlgebraReason" class="reason-text">
                原因：{{ result.booleanAlgebraReason }}
              </div>
            </div>
          </div>

          <!-- 置换群分析结果 -->
          <div v-else-if="result.type === 'group-perm'" class="group-perm-result">
            <h5 class="result-title">置换群分析结果：</h5>

            <!-- 基本信息 -->
            <div class="result-basic">
              <h6>置换群信息：</h6>
              <math-renderer
                :formula="result.formula"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
            </div>

            <!-- 元素表格和运算表格 -->
            <div v-if="result.elementTable || result.operatorTable" class="operation-result">
              <h6>群元素和运算表：</h6>
              <div v-if="result.elementTable">
                <math-renderer
                  :formula="result.elementTable"
                  :type="'katex'"
                  :display-mode="true"
                  class="operation-formula"
                />
              </div>
              <div v-if="result.operatorTable">
                <math-renderer
                  :formula="result.operatorTable"
                  :type="'katex'"
                  :display-mode="true"
                  class="operation-formula"
                />
              </div>
            </div>

            <!-- 循环群分析 -->
            <div v-if="result.showCycleGroup && result.cycleGroup !== undefined" class="operation-result">
              <h6>循环群分析：</h6>
              <div v-if="result.cycleGroup">
                <math-renderer
                  :formula="'置换群 S(' + result.m + ') 是循环群，生成元为：' + result.generators"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="operation-formula"
                />
              </div>
              <div v-else>
                <math-renderer
                  :formula="'置换群 S(' + result.m + ') 不是循环群'"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="operation-formula"
                />
              </div>
            </div>

            <!-- 群元素的幂 -->
            <div v-if="result.elementPowers && result.elementPowers.length > 0" class="operation-result">
              <h6>群元素的幂（包括群元素的逆）：</h6>
              <div v-for="(power, idx) in result.elementPowers" :key="idx" class="power-item">
                <math-renderer
                  :formula="power"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="operation-formula"
                />
              </div>
            </div>

            <!-- 群元素的阶 -->
            <div v-if="result.elementOrders && result.elementOrders.length > 0" class="operation-result">
              <h6>群元素的阶：</h6>
              <div class="orders-container">
                <math-renderer
                  :formula="result.elementOrders.join('')"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="operation-formula"
                />
              </div>
            </div>

            <!-- 子群分析 -->
            <div v-if="result.subgroups && result.subgroups.length > 0" class="operation-result">
              <h6>群的所有非平凡子群：</h6>
              <div v-for="(subgroup, idx) in result.subgroups" :key="idx" class="subgroup-item">
                <h6>子群 {{ idx + 1 }}：</h6>
                <math-renderer
                  :formula="'\\{' + subgroup.subgroupElements + '\\}'"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="operation-formula"
                />
                <div v-if="subgroup.cycleSubgroup">
                  <math-renderer
                    :formula="'是循环子群，生成元为：' + subgroup.generators"
                    :type="'mathjax'"
                    :display-mode="true"
                    class="operation-formula"
                  />
                </div>
                <div v-else>
                  <math-renderer
                    :formula="'不是循环子群'"
                    :type="'mathjax'"
                    :display-mode="true"
                    class="operation-formula"
                  />
                </div>
                <div v-if="subgroup.operatorTable">
                  <math-renderer
                    :formula="subgroup.operatorTable"
                    :type="'katex'"
                    :display-mode="true"
                    class="operation-formula"
                  />
                </div>
              </div>
            </div>

            <!-- 陪集分析 -->
            <div v-if="result.cosets && result.cosets.length > 0" class="operation-result">
              <h6>群的所有非平凡子群的陪集：</h6>
              <div v-for="(coset, idx) in result.cosets" :key="idx" class="coset-item">
                <h6>
                <span>子群 {{ idx + 1 }}：</span>
                <math-renderer
                  :formula="'\\{' + coset.subgroupElements + '\\}'"
                  :type="'mathjax'"
                  :display-mode="false"
                  class="inline-formula"
                />
                <span>的陪集：</span>
              </h6>
                <div class="coset-section">
                  <h6>左陪集包括：</h6>
                  <math-renderer
                    :formula="coset.leftCosets.join('')"
                    :type="'mathjax'"
                    :display-mode="true"
                    class="operation-formula"
                  />
                </div>
                <div class="coset-section">
                  <h6>右陪集包括：</h6>
                  <math-renderer
                    :formula="coset.rightCosets.join('')"
                    :type="'mathjax'"
                    :display-mode="true"
                    class="operation-formula"
                  />
                </div>
              </div>
            </div>

            <!-- 正规子群分析 -->
            <div v-if="result.normalSubgroups && result.normalSubgroups.length > 0" class="operation-result">
              <h6>群的正规子群分析：</h6>
              <div v-for="(normal, idx) in result.normalSubgroups" :key="idx" class="normal-item">
                <h6>
                <span>子群 {{ idx + 1 }}：</span>
                <math-renderer
                  :formula="'\\{' + normal.subgroupElements + '\\}'"
                  :type="'mathjax'"
                  :display-mode="false"
                  class="inline-formula"
                />
              </h6>
                <div v-if="normal.normal">
                  <h6>是正规子群，其商群的运算表如下：</h6>
                  <math-renderer
                    :formula="normal.quotientGroupTable"
                    :type="'katex'"
                    :display-mode="true"
                    class="operation-formula"
                  />
                </div>
                <div v-else>
                  <h6>不是正规子群</h6>
                </div>
              </div>
            </div>
          </div>

          <!-- 等价关系计算结果 -->
          <div v-else-if="result.type === 'equivalence-relation'" class="equivalence-relation-result">
            <h5 class="result-title">等价关系分析结果：</h5>

            <!-- 基本信息结果 -->
            <div class="result-basic">
              <h6>输入信息：</h6>
              <math-renderer
                :formula="result.formula"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
            </div>

            <!-- 关系性质分析结果 -->
            <div class="properties-result">
              <h6>关系性质分析：</h6>
              <div class="property-item">
                <el-tag :type="result.isReflexive ? 'success' : 'danger'" size="small">
                  自反性: {{ result.isReflexive ? '是' : '否' }}
                </el-tag>
                <math-renderer
                  v-if="result.reflexiveResult"
                  :formula="result.reflexiveResult"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="property-formula"
                />
              </div>

              <div class="property-item">
                <el-tag :type="result.isSymmetric ? 'success' : 'danger'" size="small">
                  对称性: {{ result.isSymmetric ? '是' : '否' }}
                </el-tag>
                <math-renderer
                  v-if="result.symmetricResult"
                  :formula="result.symmetricResult"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="property-formula"
                />
              </div>

              <div class="property-item">
                <el-tag :type="result.isTransitive ? 'success' : 'danger'" size="small">
                  传递性: {{ result.isTransitive ? '是' : '否' }}
                </el-tag>
                <math-renderer
                  v-if="result.transitiveResult"
                  :formula="result.transitiveResult"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="property-formula"
                />
              </div>

              <div class="property-item">
                <el-tag :type="result.isEquivalenceRelation ? 'success' : 'warning'" size="small">
                  等价关系: {{ result.isEquivalenceRelation ? '是' : '否' }}
                </el-tag>
              </div>
            </div>

            <!-- 关系矩阵结果 -->
            <div v-if="result.relationMatrix" class="matrix-result">
              <h6>关系矩阵：</h6>
              <math-renderer
                :formula="result.relationMatrix"
                :type="'mathjax'"
                :display-mode="true"
                class="matrix-formula"
              />
            </div>

            <!-- 关系图结果 -->
            <div v-if="result.relationGraphUrl" class="graph-result">
              <h6>关系图：</h6>
              <div class="graph-image">
                <el-image
                  :src="result.relationGraphUrl"
                  :preview-src-list="[result.relationGraphUrl]"
                  fit="contain"
                  style="max-width: 100%; max-height: 200px;"
                >
                  <template #error>
                    <div class="image-error">
                      <el-icon><Picture /></el-icon>
                      <span>关系图加载失败</span>
                    </div>
                  </template>
                </el-image>
              </div>
            </div>

            <!-- 等价关系闭包结果 -->
            <div v-if="result.equivalenceClosure" class="closure-result">
              <h6>等价关系闭包（最小等价关系）：</h6>
              <math-renderer
                :formula="result.equivalenceClosure"
                :type="'mathjax'"
                :display-mode="true"
                class="closure-formula"
              />

              <div v-if="result.equivalenceClosureMatrix" class="closure-matrix">
                <h6>闭包关系矩阵：</h6>
                <math-renderer
                  :formula="result.equivalenceClosureMatrix"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="matrix-formula"
                />
              </div>

              <div v-if="result.equivalenceClosureGraphUrl" class="closure-graph">
                <h6>闭包关系图：</h6>
                <div class="graph-image">
                  <el-image
                    :src="result.equivalenceClosureGraphUrl"
                    :preview-src-list="[result.equivalenceClosureGraphUrl]"
                    fit="contain"
                    style="max-width: 100%; max-height: 200px;"
                  >
                    <template #error>
                      <div class="image-error">
                        <el-icon><Picture /></el-icon>
                        <span>闭包关系图加载失败</span>
                      </div>
                    </template>
                  </el-image>
                </div>
              </div>
            </div>

            <!-- 等价类结果 -->
            <div v-if="result.equivalenceClasses && result.equivalenceClasses.length > 0" class="classes-result">
              <h6>等价类：</h6>
              <div v-for="(eqClass, classIndex) in result.equivalenceClasses" :key="classIndex" class="class-item">
                <math-renderer
                  :formula="'[' + eqClass.element + ']_R'"
                  :type="'mathjax'"
                  :display-mode="false"
                  class="class-title"
                />
                <math-renderer
                  :formula="eqClass.equivalenceClassLaTeX"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="class-formula"
                />
              </div>
            </div>

            <!-- 商集结果 -->
            <div v-if="result.quotientSet" class="quotient-result">
              <h6>商集：</h6>
              <math-renderer
                :formula="`A/R = ${result.quotientSet}`"
                :type="'mathjax'"
                :display-mode="true"
                class="quotient-formula"
              />
            </div>
          </div>

          <!-- 关系性质判断结果 -->
          <div v-else-if="result.type === 'relation-property'" class="relation-property-result">
            <!-- 基本信息 -->
            <div class="result-basic">
              <h6>输入集合和关系：</h6>
              <math-renderer
                :formula="result.formula"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
            </div>

            <!-- 关系矩阵 -->
            <div v-if="result.matrixString" class="matrix-result">
              <h6>关系矩阵：</h6>
              <math-renderer
                :formula="result.matrixString"
                :type="'mathjax'"
                :display-mode="true"
                class="matrix-formula"
              />
            </div>

            <!-- 关系图 -->
            <div v-if="result.graphImageUrl" class="graph-result">
              <h6>关系图：</h6>
              <div class="graph-image-container">
                <img :src="result.graphImageUrl" alt="关系图" class="graph-image" />
              </div>
            </div>

            <!-- 自反性结果 -->
            <div v-if="result.reflexiveResult" class="property-result">
              <h6>自反性分析：</h6>

              <math-renderer
                :formula="result.reflexiveResult.explanation"
                :type="'mathjax'"
                :display-mode="true"
                class="property-formula"
              />
              <div v-if="result.reflexiveResult.counterExample" class="counter-example">
                <el-text type="danger" size="small">反例：</el-text>
                <math-renderer
                  :formula="result.reflexiveResult.counterExample"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="counter-example-formula"
                />
              </div>
            </div>

            <!-- 反自反性结果 -->
            <div v-if="result.irreflexiveResult" class="property-result">
              <h6>反自反性分析：</h6>
 
              <math-renderer
                :formula="result.irreflexiveResult.explanation"
                :type="'mathjax'"
                :display-mode="true"
                class="property-formula"
              />
              <div v-if="result.irreflexiveResult.counterExample" class="counter-example">
                <el-text type="danger" size="small">反例：</el-text>
                <math-renderer
                  :formula="result.irreflexiveResult.counterExample"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="counter-example-formula"
                />
              </div>
            </div>

            <!-- 对称性结果 -->
            <div v-if="result.symmetricResult" class="property-result">
              <h6>对称性分析：</h6>
  
              <math-renderer
                :formula="result.symmetricResult.explanation"
                :type="'mathjax'"
                :display-mode="true"
                class="property-formula"
              />
              <div v-if="result.symmetricResult.counterExample1" class="counter-example">
                <el-text type="danger" size="small">反例：</el-text>
                <math-renderer
                  :formula="result.symmetricResult.counterExample1 + ' \\wedge ' + result.symmetricResult.counterExample2"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="counter-example-formula"
                />
              </div>
            </div>

            <!-- 反对称性结果 -->
            <div v-if="result.antisymmetricResult" class="property-result">
              <h6>反对称性分析：</h6>
    
              <math-renderer
                :formula="result.antisymmetricResult.explanation"
                :type="'mathjax'"
                :display-mode="true"
                class="property-formula"
              />
              <div v-if="result.antisymmetricResult.counterExample1" class="counter-example">
                <el-text type="danger" size="small">反例：</el-text>
                <math-renderer
                  :formula="result.antisymmetricResult.counterExample1 + ' \\wedge ' + result.antisymmetricResult.counterExample2"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="counter-example-formula"
                />
              </div>
            </div>

            <!-- 传递性结果 -->
            <div v-if="result.transitiveResult" class="property-result">
              <h6>传递性分析：</h6>

              <math-renderer
                :formula="result.transitiveResult.explanation"
                :type="'mathjax'"
                :display-mode="true"
                class="property-formula"
              />
              <div v-if="result.transitiveResult.counterExample1" class="counter-example">
                <el-text type="danger" size="small">反例：</el-text>
                <math-renderer
                  :formula="result.transitiveResult.counterExample1 + ' \\wedge ' + result.transitiveResult.counterExample2 + ' \\wedge ' + result.transitiveResult.counterExample3"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="counter-example-formula"
                />
              </div>
            </div>
          </div>

          <!-- 偏序关系计算结果 -->
          <div v-else-if="result.type === 'partial-order'" class="partial-order-result">
            <h5 class="result-title">偏序关系分析结果：</h5>

            <!-- 基本信息结果 -->
            <div class="result-basic">
              <h6>输入信息：</h6>
              <math-renderer
                :formula="result.formula"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
              <div v-if="result.setSString" class="subset-info">
                <strong>子集S：</strong>
                <math-renderer
                  :formula="result.setSString"
                  :type="'mathjax'"
                  :display-mode="false"
                />
              </div>
            </div>

            <!-- 偏序关系性质验证 -->
            <div class="properties-result">
              <h6>偏序关系性质验证：</h6>
              <div class="property-item">
                <el-tag :type="result.isReflexive ? 'success' : 'danger'" size="small">
                  自反性: {{ result.isReflexive ? '是' : '否' }}
                </el-tag>
              </div>
              <div class="property-item">
                <el-tag :type="result.isAntisymmetric ? 'success' : 'danger'" size="small">
                  反对称性: {{ result.isAntisymmetric ? '是' : '否' }}
                </el-tag>
              </div>
              <div class="property-item">
                <el-tag :type="result.isTransitive ? 'success' : 'danger'" size="small">
                  传递性: {{ result.isTransitive ? '是' : '否' }}
                </el-tag>
              </div>
              <div class="partial-order-verdict">
                <el-tag :type="result.isPartialOrder ? 'success' : 'danger'" size="large">
                  {{ result.isPartialOrder ? '是偏序关系' : '不是偏序关系' }}
                </el-tag>
              </div>
            </div>

            <!-- 关系矩阵 -->
            <div v-if="result.relationMatrix" class="matrix-result">
              <h6>关系矩阵：</h6>
              <math-renderer
                :formula="result.relationMatrix"
                :type="'mathjax'"
                :display-mode="true"
                class="matrix-formula"
              />
            </div>

            <!-- 关系图和哈斯图 -->
            <div v-if="result.relationGraphUrl || result.hasseDiagramUrl" class="graph-section">
              <div v-if="result.relationGraphUrl" class="graph-result">
                <h6>关系图：</h6>
                <div class="graph-image">
                  <el-image
                    :src="result.relationGraphUrl"
                    :preview-src-list="[result.relationGraphUrl]"
                    fit="contain"
                    style="max-width: 100%; max-height: 200px;"
                  >
                    <template #error>
                      <div class="image-error">
                        <el-icon><Picture /></el-icon>
                        <span>关系图加载失败</span>
                      </div>
                    </template>
                  </el-image>
                </div>
              </div>

              <div v-if="result.hasseDiagramUrl" class="graph-result">
                <h6>哈斯图：</h6>
                <div class="graph-image">
                  <el-image
                    :src="result.hasseDiagramUrl"
                    :preview-src-list="[result.hasseDiagramUrl]"
                    fit="scale-down"
                    style="width: auto; height: auto; max-width: 100%; max-height: 400px;"
                  >
                    <template #error>
                      <div class="image-error">
                        <el-icon><Picture /></el-icon>
                        <span>哈斯图加载失败</span>
                      </div>
                    </template>
                  </el-image>
                </div>
              </div>
            </div>

            <!-- 只有偏序关系才显示以下内容 -->
            <template v-if="result.isPartialOrder">
              <!-- 元素计算结果 -->
              <div v-if="result.minimumElements || result.maximumElements || result.leastElement || result.greatestElement" class="element-calculation">
                <h6>元素计算：</h6>
                <div v-if="result.minimumElements" class="element-item">
                  <strong>极小元：</strong>
                  <math-renderer
                    :formula="result.minimumElements"
                    :type="'mathjax'"
                    :display-mode="false"
                    class="element-formula"
                  />
                </div>
                <div v-if="result.maximumElements" class="element-item">
                  <strong>极大元：</strong>
                  <math-renderer
                    :formula="result.maximumElements"
                    :type="'mathjax'"
                    :display-mode="false"
                    class="element-formula"
                  />
                </div>
                <div v-if="result.leastElement" class="element-item">
                  <strong>最小元：</strong>
                  <math-renderer
                    :formula="result.leastElement"
                    :type="'mathjax'"
                    :display-mode="false"
                    class="element-formula"
                  />
                </div>
                <div v-if="result.greatestElement" class="element-item">
                  <strong>最大元：</strong>
                  <math-renderer
                    :formula="result.greatestElement"
                    :type="'mathjax'"
                    :display-mode="false"
                    class="element-formula"
                  />
                </div>
              </div>

              <!-- 界和确界计算结果（仅当提供了子集S时） -->
              <div v-if="result.setSString && (result.lowerBounds || result.upperBounds || result.greatestLowerBound || result.leastUpperBound)" class="bound-calculation">
                <h6>界和确界计算（子集S）：</h6>
                <div v-if="result.lowerBounds" class="bound-item">
                  <strong>下界：</strong>
                  <math-renderer
                    :formula="result.lowerBounds"
                    :type="'mathjax'"
                    :display-mode="false"
                    class="bound-formula"
                  />
                </div>
                <div v-if="result.upperBounds" class="bound-item">
                  <strong>上界：</strong>
                  <math-renderer
                    :formula="result.upperBounds"
                    :type="'mathjax'"
                    :display-mode="false"
                    class="bound-formula"
                  />
                </div>
                <div v-if="result.greatestLowerBound" class="bound-item">
                  <strong>最大下界：</strong>
                  <math-renderer
                    :formula="result.greatestLowerBound"
                    :type="'mathjax'"
                    :display-mode="false"
                    class="bound-formula"
                  />
                </div>
                <div v-if="result.leastUpperBound" class="bound-item">
                  <strong>最小上界：</strong>
                  <math-renderer
                    :formula="result.leastUpperBound"
                    :type="'mathjax'"
                    :display-mode="false"
                    class="bound-formula"
                  />
                </div>
              </div>
            </template>
          </div>

          <!-- 函数性质判断结果 -->
          <div v-else-if="result.type === 'function-property'" class="function-property-result">
            <h5 class="result-title">函数性质分析结果：</h5>

            <!-- 集合和函数基本信息 -->
            <div class="result-basic">
              <h6>输入集合和函数：</h6>
              <math-renderer
                :formula="result.formula"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
            </div>

            <!-- 函数判断结果 -->
            <div class="function-judgment">
              <h6>函数性质判断：</h6>
              <el-alert
                :type="result.function ? 'success' : 'error'"
                :title="result.function ? '该关系是函数' : '该关系不是函数'"
                :closable="false"
                show-icon
                class="function-alert"
              />
            </div>

            <!-- 关系矩阵 -->
            <div v-if="result.relationMatrix" class="matrix-result">
              <h6>关系矩阵：</h6>
              <div class="matrix-formula">
                <math-renderer
                  :formula="result.relationMatrix"
                  :type="'mathjax'"
                  :display-mode="true"
                />
              </div>
            </div>

            <!-- 关系图 -->
            <div v-if="result.relationGraphUrl" class="graph-result">
              <h6>关系图：</h6>
              <div class="graph-image">
                <el-image
                  :src="result.relationGraphUrl"
                  :preview-src-list="[result.relationGraphUrl]"
                  fit="contain"
                  style="max-width: 100%; max-height: 300px;"
                  class="relation-graph"
                >
                  <template #error>
                    <div class="image-error">
                      <el-icon><Picture /></el-icon>
                      <span>关系图加载失败</span>
                    </div>
                  </template>
                </el-image>
              </div>
            </div>

            <!-- 单射性判断 -->
            <div v-if="result.injectionResult" class="property-result">
              <h6>单射性判断：</h6>
              <el-alert
                :type="result.injectionResult.isProperty ? 'success' : 'warning'"
                :title="result.injectionResult.description"
                :closable="false"
                show-icon
              >
                <template v-if="result.injectionResult.counterExample" #default>
                  <div class="counter-example">
                    反例：元素 {{ result.injectionResult.counterExample }}
                  </div>
                </template>
              </el-alert>
            </div>

            <!-- 满射性判断 -->
            <div v-if="result.surjectionResult" class="property-result">
              <h6>满射性判断：</h6>
              <el-alert
                :type="result.surjectionResult.isProperty ? 'success' : 'warning'"
                :title="result.surjectionResult.description"
                :closable="false"
                show-icon
              >
                <template v-if="result.surjectionResult.counterExample" #default>
                  <div class="counter-example">
                    反例：元素 {{ result.surjectionResult.counterExample }}
                  </div>
                </template>
              </el-alert>
            </div>

            <!-- 双射性判断 -->
            <div v-if="result.bijectionResult" class="property-result">
              <h6>双射性判断：</h6>
              <el-alert
                :type="result.bijectionResult.isProperty ? 'success' : 'warning'"
                :title="result.bijectionResult.description"
                :closable="false"
                show-icon
              >
                <template v-if="result.bijectionResult.counterExample" #default>
                  <div class="counter-example">
                    反例：元素 {{ result.bijectionResult.counterExample }}
                  </div>
                </template>
              </el-alert>
            </div>
          </div>

          <!-- 显示集合表达式运算结果 -->
          <div v-else-if="result.type === 'setExpressionOperation'" class="set-expr-operation-result">
            <h5 class="result-title">集合表达式运算结果：</h5>

            <!-- 集合基本信息 -->
            <div class="result-basic">
              <h6>输入集合与表达式：</h6>
              <math-renderer
                :formula="result.formula"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
            </div>

            <!-- 表达式运算结果 -->
            <div v-if="result.latexResult" class="operation-result">
              <h6>表达式运算结果：</h6>
              <math-renderer
                :formula="`result = ${result.latexResult}`"
                :type="'mathjax'"
                :display-mode="true"
                class="operation-formula"
              />
            </div>
          </div>

          <!-- 显示关系运算结果 -->
          <div v-else-if="result.type === 'relationOperation'" class="relation-operation-result">
            <h5 class="result-title">关系运算分析结果：</h5>

            <!-- 集合和关系基本信息 -->
            <div class="result-basic">
              <h6>输入集合和关系：</h6>
              <math-renderer
                :formula="result.formula"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
            </div>

            <!-- 关系矩阵显示 -->
            <div v-if="result.relationRMatrix" class="matrix-result">
              <h6>关系R的矩阵：</h6>
              <math-renderer
                :formula="`M_R = ` + result.relationRMatrix"
                :type="'mathjax'"
                :display-mode="true"
                class="matrix-formula"
              />
            </div>

            <div v-if="result.relationSMatrix" class="matrix-result">
              <h6>关系S的矩阵：</h6>
              <math-renderer
                :formula="`M_S = ` + result.relationSMatrix"
                :type="'mathjax'"
                :display-mode="true"
                class="matrix-formula"
              />
            </div>

            <!-- 关系交结果 -->
            <div v-if="result.intersectionResult" class="operation-result">
              <h6>关系交运算：</h6>
              <math-renderer
                :formula="`R \\cap S = ` + result.intersectionResult"
                :type="'mathjax'"
                :display-mode="true"
                class="operation-formula"
              />
            </div>

            <!-- 关系并结果 -->
            <div v-if="result.unionResult" class="operation-result">
              <h6>关系并运算：</h6>
              <math-renderer
                :formula="`R \\cup S = ` + result.unionResult"
                :type="'mathjax'"
                :display-mode="true"
                class="operation-formula"
              />
            </div>

            <!-- 关系差结果 -->
            <div v-if="result.subtractResult" class="operation-result">
              <h6>关系差运算：</h6>
              <math-renderer
                :formula="`R - S = ` + result.subtractResult"
                :type="'mathjax'"
                :display-mode="true"
                class="operation-formula"
              />
            </div>

            <!-- 关系逆结果 -->
            <div v-if="result.inverseRResult || result.inverseSResult" class="operation-result">
              <h6>关系逆运算：</h6>
              <div v-if="result.inverseRResult">
                <math-renderer
                  :formula="`R^{-1} = ` + result.inverseRResult"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="operation-formula"
                />
              </div>
              <div v-if="result.inverseSResult">
                <math-renderer
                  :formula="`S^{-1} = ` + result.inverseSResult"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="operation-formula"
                />
              </div>
            </div>

            <!-- 关系复合结果 -->
            <div v-if="result.compositeResult" class="operation-result">
              <h6>关系复合运算：</h6>
              <math-renderer
                :formula="result.compositeResult"
                :type="'mathjax'"
                :display-mode="true"
                class="operation-formula"
              />
            </div>

            <!-- 逆的复合结果 -->
            <div v-if="result.invcompResult" class="operation-result">
              <h6>逆的复合运算：</h6>
              <math-renderer
                :formula="result.invcompResult"
                :type="'mathjax'"
                :display-mode="true"
                class="operation-formula"
              />
            </div>
          </div>

          <!-- 显示关系闭包计算结果 -->
          <div v-else-if="result.type === 'relation-closure'" class="relation-closure-result">
            <h5 class="result-title">关系闭包计算结果：</h5>

            <!-- 集合和关系基本信息 -->
            <div class="result-basic">
              <h6>输入集合和关系：</h6>
              <math-renderer
                :formula="result.formula"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
            </div>

            <!-- 关系矩阵显示 -->
            <div v-if="result.relationMatrix" class="matrix-result">
              <h6>关系R的矩阵：</h6>
              <math-renderer
                :formula="`M_R = ` + result.relationMatrix"
                :type="'mathjax'"
                :display-mode="true"
                class="matrix-formula"
              />
            </div>

            <!-- 关系图显示 -->
            <div v-if="result.relationGraphUrl" class="graph-result">
              <h6>关系R的关系图：</h6>
              <div class="graph-image-container">
                <img :src="result.relationGraphUrl" alt="关系图" class="graph-image" />
              </div>
            </div>

            <!-- 自反闭包结果 -->
            <div v-if="result.reflexiveClosureResult" class="closure-result">
              <h6>自反闭包 r(R)：</h6>
              <math-renderer
                :formula="`r(R) = ` + result.reflexiveClosureResult.closureLaTeX"
                :type="'mathjax'"
                :display-mode="true"
                class="operation-formula"
              />
              <div v-if="result.reflexiveClosureResult.closureMatrix" class="matrix-result">
                <h6>自反闭包矩阵：</h6>
                <math-renderer
                  :formula="`M_{r(R)} = ` + result.reflexiveClosureResult.closureMatrix"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="matrix-formula"
                />
              </div>
              <div v-if="result.reflexiveClosureResult.closureGraphUrl" class="graph-result">
                <h6>自反闭包关系图：</h6>
                <div class="graph-image-container">
                  <img :src="result.reflexiveClosureResult.closureGraphUrl" alt="自反闭包关系图" class="graph-image" />
                </div>
              </div>
            </div>

            <!-- 对称闭包结果 -->
            <div v-if="result.symmetricClosureResult" class="closure-result">
              <h6>对称闭包 s(R)：</h6>
              <math-renderer
                :formula="`s(R) = ` + result.symmetricClosureResult.closureLaTeX"
                :type="'mathjax'"
                :display-mode="true"
                class="operation-formula"
              />
              <div v-if="result.symmetricClosureResult.closureMatrix" class="matrix-result">
                <h6>对称闭包矩阵：</h6>
                <math-renderer
                  :formula="`M_{s(R)} = ` + result.symmetricClosureResult.closureMatrix"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="matrix-formula"
                />
              </div>
              <div v-if="result.symmetricClosureResult.closureGraphUrl" class="graph-result">
                <h6>对称闭包关系图：</h6>
                <div class="graph-image-container">
                  <img :src="result.symmetricClosureResult.closureGraphUrl" alt="对称闭包关系图" class="graph-image" />
                </div>
              </div>
            </div>

            <!-- 传递闭包结果 -->
            <div v-if="result.transitiveClosureResult" class="closure-result">
              <h6>传递闭包 t(R) ({{ result.transitiveClosureResult.algorithmUsed }})：</h6>
              <math-renderer
                :formula="`t(R) = ` + result.transitiveClosureResult.closureLaTeX"
                :type="'mathjax'"
                :display-mode="true"
                class="operation-formula"
              />
              <div v-if="result.transitiveClosureResult.closureMatrix" class="matrix-result">
                <h6>传递闭包矩阵：</h6>
                <math-renderer
                  :formula="`M_{t(R)} = ` + result.transitiveClosureResult.closureMatrix"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="matrix-formula"
                />
              </div>
              <!-- 传递闭包详细计算过程 -->
              <div v-if="result.transitiveClosureResult.detailMatrices && result.transitiveClosureResult.detailMatrices.length > 0" class="transitive-details">
                <h6>传递闭包计算过程：</h6>
                <div v-for="(matrix, index) in result.transitiveClosureResult.detailMatrices" :key="index" class="detail-step">
                  <p class="step-description">{{ result.transitiveClosureResult.detailDescriptions[index] }}：</p>
                  <math-renderer
                    :formula="matrix"
                    :type="'mathjax'"
                    :display-mode="true"
                    class="detail-matrix"
                  />
                </div>
              </div>
              <div v-if="result.transitiveClosureResult.closureGraphUrl" class="graph-result">
                <h6>传递闭包关系图：</h6>
                <div class="graph-image-container">
                  <img :src="result.transitiveClosureResult.closureGraphUrl" alt="传递闭包关系图" class="graph-image" />
                </div>
              </div>
            </div>

            <!-- 等价闭包结果 -->
            <div v-if="result.equivalenceClosureResult" class="closure-result">
              <h6>等价闭包 tsr(R)：</h6>
              <math-renderer
                :formula="`tsr(R) = ` + result.equivalenceClosureResult.closureLaTeX"
                :type="'mathjax'"
                :display-mode="true"
                class="operation-formula"
              />
              <div v-if="result.equivalenceClosureResult.closureMatrix" class="matrix-result">
                <h6>等价闭包矩阵：</h6>
                <math-renderer
                  :formula="`M_{tsr(R)} = ` + result.equivalenceClosureResult.closureMatrix"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="matrix-formula"
                />
              </div>
              <div v-if="result.equivalenceClosureResult.closureGraphUrl" class="graph-result">
                <h6>等价闭包关系图：</h6>
                <div class="graph-image-container">
                  <img :src="result.equivalenceClosureResult.closureGraphUrl" alt="等价闭包关系图" class="graph-image" />
                </div>
              </div>
            </div>
          </div>

          <!-- 显示公式类型 -->
          <div v-if="result.formulaType" class="formula-type">
            <el-tag :type="getFormulaTypeTag(result.formulaType)" class="type-tag">
              {{ result.formulaType }}
            </el-tag>
          </div>

          <!-- 显示严格形式公式 -->
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

          <!-- 显示抽象语法树 -->
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

          <!-- 显示范式计算结果 -->
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
          </div>

          <!-- 显示原图可视化结果 -->
          <div v-else-if="result.type === 'graph-visualization'" class="graph-visualization-result">
            <h5 class="result-title">带权图图形化表示：</h5>

            <!-- 基本信息 -->
            <div class="result-basic">
              <h6>图的基本信息：</h6>
              <math-renderer
                :formula="result.formula"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
            </div>

            <!-- 原图可视化 -->
            <div v-if="result.graphImageUrl" class="graph-visualization">
              <h6>原图可视化：</h6>
              <div class="graph-image-container">
                <img
                  :src="result.graphImageUrl"
                  alt="带权图的可视化"
                  class="graph-image"
                  @error="handleGraphImageError"
                />
              </div>
            </div>
          </div>

          <!-- 显示最短路径图结果 -->
          <div v-else-if="result.type === 'path-visualization'" class="path-visualization-result">
            <h5 class="result-title">最短路径图可视化：</h5>

            <!-- 基本信息 -->
            <div class="result-basic">
              <h6>最短路径图信息：</h6>
              <math-renderer
                :formula="result.formula"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
              <div v-if="result.startNode" class="start-node-info">
                <math-renderer
                  :formula="`起始节点: ${result.startNode}`"
                  :type="'mathjax'"
                  :display-mode="false"
                  class="start-node-formula"
                />
              </div>
            </div>

            <!-- 最短路径图可视化 -->
            <div v-if="result.pathGraphImageUrl" class="path-graph-visualization">
              <h6>最短路径图：</h6>
              <div class="graph-image-container">
                <img
                  :src="result.pathGraphImageUrl"
                  alt="最短路径图的可视化"
                  class="graph-image"
                  @error="handleGraphImageError"
                />
              </div>
            </div>
          </div>

          <!-- 显示最短路径计算结果 -->
          <div v-else-if="result.type === 'shortest-path'" class="shortest-path-result">
            <h5 class="result-title">带权图最短路径计算结果：</h5>

            <!-- 起始节点信息 -->
            <div v-if="result.startNode" class="start-node-info">
              <h6>起始节点：</h6>
              <math-renderer
                :formula="result.startNode"
                :type="'mathjax'"
                :display-mode="false"
                class="start-node-formula"
              />
            </div>

            <!-- 原图可视化 -->
            <div v-if="result.graphImageUrl" class="graph-visualization">
              <h6>图形化表示：</h6>
              <div class="graph-image-container">
                <img
                  :src="result.graphImageUrl"
                  alt="带权图的可视化"
                  class="graph-image"
                  @error="handleGraphImageError"
                />
              </div>
            </div>
            
            <!-- 带权邻接矩阵 -->
            <div v-if="result.adjacencyMatrix" class="weight-matrix">
              <h6>带权图的矩阵表示 D：</h6>
              <math-renderer
                :formula="result.adjacencyMatrix"
                :type="'mathjax'"
                :display-mode="true"
                class="matrix-formula"
              />
            </div>

            <!-- Dijkstra算法详细过程 -->
            <div v-if="result.dijkstraDetails" class="dijkstra-details">
              <h6>Dijkstra算法详细过程：</h6>
              <div class="algorithm-content">
                <math-renderer
                  :formula="result.dijkstraDetails"
                  :type="'katex'"
                  :display-mode="true"
                  class="algorithm-formula"
                />
              </div>
            </div>

            <!-- 最短路径结果 -->
            <div v-if="result.shortestPaths && result.shortestPaths.length > 0" class="shortest-paths">
              <h6>得到的最短路径结果距离如下：</h6>
              <div class="paths-grid">
                <div v-for="path in result.shortestPaths" :key="path.target" class="path-item">
                  <math-renderer
                    :formula="path.formula"
                    :type="'mathjax'"
                    :display-mode="true"
                    class="path-formula"
                  />
                </div>
              </div>
            </div>



            <!-- 最短路径图可视化 -->
            <div v-if="result.pathGraphImageUrl" class="path-graph-visualization">
              <h6>最短路径图：</h6>
              <div class="graph-image-container">
                <img
                  :src="result.pathGraphImageUrl"
                  alt="最短路径图的可视化"
                  class="graph-image"
                  @error="handleGraphImageError"
                />
              </div>
            </div>
          </div>

          <!-- 显示图遍历结果 -->
          <div v-else-if="result.type === 'graph-travel'" class="graph-travel-result">
            <h5 class="result-title">图遍历分析结果：</h5>
            <!-- 图形可视化 -->
            <div v-if="result.graphImageUrl" class="graph-visualization">
              <h6>图形可视化：</h6>
              <div class="graph-image-container">
                <img
                  :src="result.graphImageUrl"
                  alt="图的可视化"
                  class="graph-image"
                  @error="handleGraphImageError"
                />
              </div>
            </div>

            <!-- 度数计算结果 -->
            <div v-if="result.nodeDegrees && result.nodeDegrees.length > 0" class="node-degrees">
              <h6>顶点度数：</h6>
              <div class="degrees-grid">
                <div v-for="node in result.nodeDegrees" :key="node.nodeId" class="degree-item">
                  <math-renderer
                    :formula="`d(${node.nodeLabel}) = ${node.degree}`"
                    :type="'mathjax'"
                    :display-mode="false"
                  />
                  <div v-if="result.directed" class="directed-degrees">
                    <math-renderer
                      :formula="`d^+(${node.nodeLabel}) = ${node.outDegree}`"
                      :type="'mathjax'"
                      :display-mode="false"
                      class="indegree"
                    />
                    <math-renderer
                      :formula="`d^-(${node.nodeLabel}) = ${node.inDegree}`"
                      :type="'mathjax'"
                      :display-mode="false"
                      class="outdegree"
                    />
                  </div>
                </div>
              </div>
            </div>

            <!-- 矩阵显示 -->
            <div v-if="result.adjacencyMatrix || result.incidenceMatrix" class="matrices">
              <div v-if="result.adjacencyMatrix" class="matrix-item">
                <h6>邻接矩阵 A：</h6>
                <math-renderer
                  :formula="result.adjacencyMatrix"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="matrix-formula"
                />
              </div>
              <div v-if="result.incidenceMatrix" class="matrix-item">
                <h6>关联矩阵 I：</h6>
                <math-renderer
                  :formula="result.incidenceMatrix"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="matrix-formula"
                />
              </div>
            </div>

            <!-- 路径矩阵 -->
            <div v-if="result.pathMatrices && result.pathMatrices.length > 0" class="path-matrices">
              <h6>邻接矩阵的幂：</h6>
              <div v-for="(matrix, idx) in result.pathMatrices" :key="idx" class="path-matrix">
                <math-renderer
                  :formula="matrix"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="path-matrix-formula"
                />
              </div>
            </div>

            <!-- DFS遍历结果 -->
            <div v-if="result.dfsResult" class="dfs-result">
              <h6>深度优先遍历(DFS)：</h6>
              <math-renderer
                :formula="result.dfsResult.traversalOrder"
                :type="'mathjax'"
                :display-mode="true"
                class="traversal-result"
              />
              <div v-if="result.dfsResult.steps && result.dfsResult.steps.length > 0" class="travel-steps">
                <div v-for="step in result.dfsResult.steps" :key="step.step" class="step-item">
                  <math-renderer
                    :formula="`步骤${step.step}: T = ${step.visitedNodes} \\quad S = ${step.auxNodes}`"
                    :type="'mathjax'"
                    :display-mode="true"
                    class="step-formula"
                  />
                </div>
              </div>
            </div>

            <!-- BFS遍历结果 -->
            <div v-if="result.bfsResult" class="bfs-result">
              <h6>广度优先遍历(BFS)：</h6>
              <math-renderer
                :formula="result.bfsResult.traversalOrder"
                :type="'mathjax'"
                :display-mode="true"
                class="traversal-result"
              />
              <div v-if="result.bfsResult.steps && result.bfsResult.steps.length > 0" class="travel-steps">
                <div v-for="step in result.bfsResult.steps" :key="step.step" class="step-item">
                  <math-renderer
                    :formula="`步骤${step.step}: T = ${step.visitedNodes} \\quad Q = ${step.auxNodes}`"
                    :type="'mathjax'"
                    :display-mode="true"
                    class="step-formula"
                  />
                </div>
              </div>
            </div>


          </div>

          <!-- 显示运算性质判断结果 -->
          <div v-else-if="result.type === 'operation-property'" class="operation-property-result">
            <h5 class="result-title">运算性质分析结果：</h5>

            <!-- 基本信息 -->
            <div class="operation-basic-info">
              <h6>输入信息：</h6>
              <math-renderer
                :formula="result.formula"
                :type="'mathjax'"
                :display-mode="true"
                class="basic-formula"
              />
            </div>

            <!-- 运算符表格 -->
            <div v-if="result.operator1Table || result.operator2Table" class="operation-tables">
              <h6>运算表格：</h6>
              <div v-if="result.operator1Table" class="operator-table">
                <math-renderer
                  :formula="result.operator1Table"
                  :type="'katex'"
                  :display-mode="true"
                  class="operation-formula"
                />
              </div>
              <div v-if="result.operator2Table" class="operator-table">
                <math-renderer
                  :formula="result.operator2Table"
                  :type="'katex'"
                  :display-mode="true"
                  class="operation-formula"
                />
              </div>
            </div>

            <!-- 运算符一的性质 -->
            <div v-if="result.operator1Properties && result.operator1Properties.length > 0" class="operator-properties">
              <h6>运算符一(∘)的性质：</h6>
              <div v-for="property in result.operator1Properties" :key="property.propertyType" class="property-item">
                <div class="property-header">
                  <strong>{{ getPropertyDisplayName(property.propertyType) }}：</strong>
                  <el-tag :type="property.hasProperty ? 'success' : 'danger'" size="small">
                    {{ property.hasProperty ? '满足' : '不满足' }}
                  </el-tag>
                </div>
                <div class="property-reason">
                  <math-renderer
                    :formula="formatMathExpression(property.reason)"
                    :type="'katex'"
                    :display-mode="false"
                    :inline="true"
                  />
                </div>
                <div v-if="property.details" class="property-details">{{ property.details }}</div>
              </div>
            </div>

            <!-- 运算符二的性质 -->
            <div v-if="result.operator2Properties && result.operator2Properties.length > 0" class="operator-properties">
              <h6>运算符二(*)的性质：</h6>
              <div v-for="property in result.operator2Properties" :key="property.propertyType" class="property-item">
                <div class="property-header">
                  <strong>{{ getPropertyDisplayName(property.propertyType) }}：</strong>
                  <el-tag :type="property.hasProperty ? 'success' : 'danger'" size="small">
                    {{ property.hasProperty ? '满足' : '不满足' }}
                  </el-tag>
                </div>
                <div class="property-reason">
                  <math-renderer
                    :formula="formatMathExpression(property.reason)"
                    :type="'katex'"
                    :display-mode="false"
                    :inline="true"
                  />
                </div>
                <div v-if="property.details" class="property-details">{{ property.details }}</div>
              </div>
            </div>

            <!-- 运算符之间的关系性质 -->
            <div v-if="result.relationProperties && result.relationProperties.length > 0" class="relation-properties">
              <h6>运算符之间的关系性质：</h6>
              <div v-for="relation in result.relationProperties" :key="`${relation.relationType}_${relation.direction}`" class="relation-item">
                <div class="relation-header">
                  <strong>{{ getRelationDisplayName(relation.relationType) }} - {{ relation.direction }}：</strong>
                  <el-tag :type="relation.hasRelation ? 'success' : 'danger'" size="small">
                    {{ relation.hasRelation ? '满足' : '不满足' }}
                  </el-tag>
                </div>
                <div class="relation-reason">
                  <math-renderer
                    :formula="formatMathExpression(relation.reason)"
                    :type="'katex'"
                    :display-mode="false"
                    :inline="true"
                  />
                </div>
              </div>
            </div>
          </div>

          <!-- 显示最小生成树计算结果 -->
          <div v-else-if="result.type === 'spanning-tree'" class="spanning-tree-result">
            <h5 class="result-title">带权图最小生成树计算结果：</h5>

            <!-- 原图可视化 -->
            <div v-if="result.graphImageUrl" class="graph-visualization">
              <h6>原图可视化：</h6>
              <div class="graph-image-container">
                <img
                  :src="result.graphImageUrl"
                  alt="原图的可视化"
                  class="graph-image"
                  @error="handleGraphImageError"
                />
              </div>
            </div>
            
            <!-- 带权图的距离矩阵 -->
            <div v-if="result.weightMatrix" class="weight-matrix">
              <h6>带权图的距离矩阵：</h6>
              <math-renderer
                :formula="result.weightMatrix"
                :type="'mathjax'"
                :display-mode="true"
                class="matrix-formula"
              />
            </div>

            <!-- Kruskal算法结果 -->
            <div v-if="result.kruskalResult" class="kruskal-result">
              <h6>Kruskal算法结果：</h6>
              <div class="algorithm-summary">
                <math-renderer
                  :formula="`使用Kruskal算法发现最小生成树（森林），总权重 = ${result.kruskalResult.totalWeight}`"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="summary-formula"
                />
              </div>

              <div v-if="result.kruskalResult.edges" class="algorithm-edges">
                <h6>生成树的边集：</h6>
                <math-renderer
                  :formula="result.kruskalResult.edges"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="edges-formula"
                />
              </div>

              <div v-if="result.kruskalResult.steps" class="algorithm-steps">
                <h6>算法计算过程：</h6>
                <math-renderer
                  :formula="result.kruskalResult.steps"
                  :type="'katex'"
                  :display-mode="true"
                  class="steps-formula"
                />
              </div>

              <div v-if="result.kruskalTreeImageUrl" class="tree-visualization">
                <h6>Kruskal最小生成树图形：</h6>
                <div class="tree-image-container">
                  <img
                    :src="result.kruskalTreeImageUrl"
                    alt="Kruskal最小生成树"
                    class="tree-image"
                    @error="handleGraphImageError"
                  />
                </div>
              </div>
            </div>

            <!-- Prim算法结果 -->
            <div v-if="result.primResult" class="prim-result">
              <h6>Prim算法结果：</h6>
              <div class="algorithm-summary">
                <math-renderer
                  :formula="`使用Prim算法发现最小生成树（森林），总权重 = ${result.primResult.totalWeight}`"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="summary-formula"
                />
              </div>

              <div v-if="result.primResult.edges" class="algorithm-edges">
                <h6>生成树的边集：</h6>
                <math-renderer
                  :formula="result.primResult.edges"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="edges-formula"
                />
              </div>

              <div v-if="result.primResult.steps" class="algorithm-steps">
                <h6>算法计算过程：</h6>
                <math-renderer
                  :formula="result.primResult.steps"
                  :type="'katex'"
                  :display-mode="true"
                  class="steps-formula"
                />
              </div>

              <div v-if="result.primTreeImageUrl" class="tree-visualization">
                <h6>Prim最小生成树图形：</h6>
                <div class="tree-image-container">
                  <img
                    :src="result.primTreeImageUrl"
                    alt="Prim最小生成树"
                    class="tree-image"
                    @error="handleGraphImageError"
                  />
                </div>
              </div>
            </div>


          </div>

          <!-- 显示树遍历结果 -->
          <div v-else-if="result.rootNode" class="tree-traversal-result">
            <h5 class="result-title">树遍历分析结果：</h5>

            <!-- 根节点信息 -->
            <div v-if="result.rootNode" class="root-info">
              <h6>根节点：</h6>
              <math-renderer
                :formula="result.rootNode"
                :type="'mathjax'"
                :display-mode="false"
                class="root-formula"
              />
            </div>

            <!-- 树形可视化 -->
            <div v-if="result.graphImageUrl" class="graph-visualization">
              <h6>树形可视化：</h6>
              <div class="graph-image-container">
                <img
                  :src="result.graphImageUrl"
                  alt="树的可视化"
                  class="graph-image"
                  @error="handleGraphImageError"
                />
              </div>
            </div>

            <!-- 矩阵显示 -->
            <div v-if="result.adjacencyMatrix || result.incidenceMatrix" class="matrices">
              <div v-if="result.adjacencyMatrix" class="matrix-item">
                <h6>邻接矩阵 A：</h6>
                <math-renderer
                  :formula="result.adjacencyMatrix"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="matrix-formula"
                />
              </div>
              <div v-if="result.incidenceMatrix" class="matrix-item">
                <h6>关联矩阵 I：</h6>
                <math-renderer
                  :formula="result.incidenceMatrix"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="matrix-formula"
                />
              </div>
            </div>

            <!-- 前序遍历结果 -->
            <div v-if="result.preorderResult" class="preorder-result">
              <h6>前序遍历 (Preorder)：</h6>
              <math-renderer
                :formula="result.preorderResult.traversalOrder"
                :type="'mathjax'"
                :display-mode="true"
                class="traversal-result"
              />
            </div>

            <!-- 中序遍历结果 -->
            <div v-if="result.inorderResult" class="inorder-result">
              <h6>中序遍历 (Inorder)：</h6>
              <math-renderer
                :formula="result.inorderResult.traversalOrder"
                :type="'mathjax'"
                :display-mode="true"
                class="traversal-result"
              />
            </div>

            <!-- 后序遍历结果 -->
            <div v-if="result.postorderResult" class="postorder-result">
              <h6>后序遍历 (Postorder)：</h6>
              <math-renderer
                :formula="result.postorderResult.traversalOrder"
                :type="'mathjax'"
                :display-mode="true"
                class="traversal-result"
              />
            </div>

          </div>

          <!-- 显示Huffman树构造结果 -->
          <div v-else-if="result.type === 'huffmanTree'" class="huffman-tree-result">
            <h5 class="result-title">哈夫曼树构造结果：</h5>


            <!-- 算法步骤显示 -->
            <div v-if="result.steps && result.steps.length > 0" class="algorithm-steps">
              <h6>算法构造步骤：</h6>
              <div v-for="step in result.steps" :key="step.step" class="huffman-step-item">
                <h6>{{ step.prompt }}</h6>
                <math-renderer
                  :formula="`步骤${step.step}: ${step.forestLaTeX}`"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="step-formula"
                />
                <div v-if="step.forestImageUrl" class="step-image">
                  <img
                    :src="step.forestImageUrl"
                    :alt="`步骤${step.step}的森林图`"
                    class="forest-image"
                    @error="handleGraphImageError"
                  />
                </div>
              </div>
            </div>

            <!-- 最终Huffman树显示 -->
            <div v-if="result.treeImageUrl" class="final-tree">
              <h6>最终构造结果：</h6>
              <div class="tree-image-container">
                <img
                  :src="result.treeImageUrl"
                  alt="最终Huffman树"
                  class="tree-image"
                  @error="handleGraphImageError"
                />
              </div>
            </div>

            <!-- 总权值计算 -->
            <div v-if="result.totalWeightLaTeX" class="weight-calculation">
              <h6>总权值计算：</h6>
              <math-renderer
                :formula="result.totalWeightLaTeX + ' = ' + result.totalWeight"
                :type="'mathjax'"
                :display-mode="true"
                class="weight-formula"
              />
              <p class="weight-result">构造得到的Huffman树总权值是：{{ result.totalWeight }}</p>
            </div>

            <!-- 叶节点编码显示 -->
            <div v-if="result.leafCodes && Object.keys(result.leafCodes).length > 0" class="leaf-codes">
              <h6>带权叶节点的编码如下：</h6>
              <div class="codes-grid">
                <div v-for="(code, label) in result.leafCodes" :key="label" class="code-item">
                  <math-renderer
                    :formula="`${label} : ${code}`"
                    :type="'mathjax'"
                    :display-mode="false"
                    class="code-formula"
                  />
                </div>
              </div>
            </div>
          </div>

          <!-- 显示排列组合数计算结果 -->
          <div v-else-if="result.type === 'comb-calculator'" class="comb-calculator-result">
            <h5 class="result-title">排列组合数计算结果：</h5>

            <!-- 基本信息 -->
            <div class="basic-info">
              <h6>输入参数：</h6>
              <math-renderer
                :formula="result.formula"
                :type="'mathjax'"
                :display-mode="true"
                class="basic-formula"
              />
            </div>

            <!-- 幂运算结果 -->
            <div v-if="result.powerResult" class="power-result">
              <h6>幂运算结果：</h6>
              <div class="calculation-item">
                <math-renderer
                  :formula="`n^{m} = ${result.powerResult}`"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="calculation-formula"
                />
              </div>
            </div>

            <!-- 组合数结果 -->
            <div v-if="result.combinationResult" class="combination-result">
              <h6>组合数结果：</h6>
              <div class="calculation-item">
                <math-renderer
                  :formula="`C(n, m) = ${result.combinationResult}`"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="calculation-formula"
                />
              </div>
            </div>

            <!-- 排列数结果 -->
            <div v-if="result.permutationResult" class="permutation-result">
              <h6>排列数结果：</h6>
              <div class="calculation-item">
                <math-renderer
                  :formula="`P(n, m) = ${result.permutationResult}`"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="calculation-formula"
                />
              </div>
            </div>

            <!-- 阶乘结果 -->
            <div v-if="result.factorialNResult || result.factorialMResult" class="factorial-result">
              <h6>阶乘结果：</h6>
              <div v-if="result.factorialNResult" class="calculation-item">
                <math-renderer
                  :formula="`n! = ${result.factorialNResult}`"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="calculation-formula"
                />
              </div>
              <div v-if="result.factorialMResult" class="calculation-item">
                <math-renderer
                  :formula="`m! = ${result.factorialMResult}`"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="calculation-formula"
                />
              </div>
            </div>
          </div>

          <!-- 组合表达式计算结果 -->
          <div v-else-if="result.type === 'expression-calculator'" class="expression-calculator-result">
            <h5 class="result-title">组合表达式计算结果（第 {{ result.index }} 次计算）：</h5>

            <!-- 原始表达式 -->
            <div class="expression-info">
              <h6>原始表达式：</h6>
              <math-renderer
                :formula="`\\text{表达式} = ${result.originalExpression}`"
                :type="'mathjax'"
                :display-mode="true"
                class="expression-formula"
              />
            </div>

            <!-- 计算结果 -->
            <div class="calculation-result">
              <h6>计算结果：</h6>
              <div class="result-item-content">
                <math-renderer
                  :formula="`${result.originalExpression} = ${result.result}`"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="result-formula-detail"
                />
              </div>
            </div>
          </div>

          <!-- 递归表达式计算结果 -->
          <div v-else-if="result.type === 'recu-expr-calculator'" class="recursion-expression-result">
            <h5 class="result-title">递归表达式计算结果（第 {{ result.index }} 次计算）：</h5>

            <!-- 递归表达式参数 -->
            <div class="recursion-params">
              <h6>递归表达式参数：</h6>
              <div class="param-item">
                <strong>递归表达式：</strong>{{ result.originalExpression }}
              </div>
              <div class="param-item">
                <strong>递归次数：</strong>n = {{ result.n }}
              </div>
              <div class="param-item">
                <strong>初始值：</strong>a₁ = {{ result.a }}, b₁ = {{ result.b }}
              </div>
            </div>

            <!-- 计算结果 -->
            <div class="recursion-result">
              <h6>计算结果：</h6>
              <math-renderer
                :formula="result.formula"
                :type="'mathjax'"
                :display-mode="true"
                class="recursion-formula-detail"
              />
            </div>
          </div>

          <!-- 字符串计数结果 -->
          <div v-else-if="result.type === 'count-string'" class="count-string-result">
            <h5 class="result-title">字符串计数分析结果：</h5>

            <!-- 基本信息 -->
            <div class="result-basic">
              <h6>输入参数：</h6>
              <math-renderer
                :formula="result.formula"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
              <div class="parameter-info">
                <p><strong>基础字符集：</strong> {{ result.baseSet }}</p>
                <p><strong>字符串长度：</strong> {{ result.length }}</p>
                <p><strong>重复允许：</strong> {{ result.allowRepetition ? '允许重复' : '不允许重复' }}</p>
                <p><strong>过滤条件：</strong></p>
                  
                  <math-renderer
                    :formula="result.filterDescription"
                    :type="'mathjax'"
                    :display-mode="true"
                    class="filter-formula"
                  />
              </div>
            </div>

            <!-- 统计结果 -->
            <div class="result-statistics">
              <h6>统计结果：</h6>
              <div class="statistics-grid">
                <div class="stat-item">
                  <span class="stat-label">总字符串数：</span>
                  <span class="stat-value">{{ result.totalCount }}</span>
                </div>
                <div class="stat-item">
                  <span class="stat-label">满足条件的字符串数：</span>
                  <span class="stat-value">{{ result.acceptedCount }}</span>
                </div>
                <div class="stat-item">
                  <span class="stat-label">结果公式：</span>
                  <math-renderer
                    :formula="result.resultFormula"
                    :type="'mathjax'"
                    :display-mode="false"
                    class="result-formula-inline"
                  />
                </div>
              </div>
            </div>

            <!-- 详细结果 -->
            <div v-if="result.stringDetails && result.stringDetails.length > 0" class="result-details">
              <h6>字符串详情：
                <el-text type="info" size="small">
                  ({{ getDisplayModeDescription(result.displayMode) }})
                </el-text>
              </h6>
              <div class="string-details-table">
                <el-table
                  :data="getTableData(result)"
                  stripe
                  size="small"
                  class="details-table"
                  :max-height="getMaxHeight()"
                >
                  <el-table-column prop="index" label="序号" width="70" />
                  <el-table-column prop="content" label="字符串" width="120" />
                  <el-table-column prop="accepted" label="是否接受" width="90">
                    <template #default="scope">
                      <el-tag :type="scope.row.accepted ? 'success' : 'danger'" size="small">
                        {{ scope.row.accepted ? '接受' : '不接受' }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column prop="acceptedCount" label="接受计数" width="90" />
                </el-table>
                <div v-if="hasMoreResults(result)" class="more-indicator">
                  <el-text type="info" size="small">
                    {{ getMoreIndicatorText(result) }}
                  </el-text>
                </div>
              </div>
            </div>
            <!-- 当选择了只显示结果时显示提示 -->
            <div v-else-if="result.type === 'count-string' && (result.displayMode === 'ONLY_RESULT' || result.displayMode === 'onlyResult')" class="result-details">
              <h6>字符串详情：
                <el-text type="info" size="small">
                  ({{ getDisplayModeDescription(result.displayMode) }})
                </el-text>
              </h6>
              <el-alert
                title="当前显示模式为'只给出计数结果'"
                description="如需查看字符串详情，请选择其他显示模式"
                type="info"
                :closable="false"
                show-icon
              />
            </div>
            <!-- 当没有详细数据但显示模式需要数据时显示提示 -->
            <div v-else-if="result.type === 'count-string' && result.displayMode && result.displayMode !== 'ONLY_RESULT' && result.displayMode !== 'onlyResult'" class="result-details">
              <h6>字符串详情：
                <el-text type="info" size="small">
                  ({{ getDisplayModeDescription(result.displayMode) }})
                </el-text>
              </h6>
              <el-alert
                title="字符串详情数据不可用"
                description="当前没有可显示的字符串详情数据"
                type="warning"
                :closable="false"
                show-icon
              />
            </div>
          </div>

          <!-- 整数计数结果 -->
          <div v-else-if="result.type === 'countInteger'" class="count-integer-result">
            <h5 class="result-title">整数计数分析结果：</h5>

            <!-- 基本信息 -->
            <div class="result-basic">
              <h6>输入参数：</h6>
              <math-renderer
                :formula="result.formula"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
              <div class="parameter-info">
                <p><strong>计数范围：</strong> [{{ result.start }}, {{ result.end }}]</p>
                <p><strong>总整数个数：</strong> {{ result.totalCount }}</p>
                <p><strong>过滤条件：</strong></p>
                  
                <math-renderer
                  :formula="result.filterDescription"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="filter-formula"
                />

              </div>
            </div>

            <!-- 统计结果 -->
            <div class="result-statistics">
              <h6>统计结果：</h6>
              <div class="statistics-grid">
                <div class="stat-item">
                  <span class="stat-label">满足条件的整数数：</span>
                  <span class="stat-value">{{ result.acceptedCount }}</span>
                </div>
                <div class="stat-item">
                  <span class="stat-label">占比：</span>
                  <span class="stat-value">{{ result.totalCount > 0 ? ((result.acceptedCount / result.totalCount * 100).toFixed(2) + '%') : '0%' }}</span>
                </div>
              </div>
            </div>

            <!-- 详细结果 - 按原Java应用风格显示 -->
            <div v-if="result.detailedResults && result.detailedResults.length > 0" class="result-details">
              <h6>整数处理详情：</h6>
              <div class="integer-processing-list">
                <div v-for="(detail, index) in result.detailedResults" :key="index" class="integer-item">
                  <math-renderer
                    :formula="`${detail.index} : ${detail.value}, \\textrm{${detail.acceptMessage}}`"
                    :type="'mathjax'"
                    :display-mode="false"
                    class="integer-formula"
                  />
                </div>

                <!-- 显示省略标记 -->
                <div v-if="result.detailedResults.length < result.totalCount" class="ellipsis-indicator">
                  <math-renderer
                    :formula="'\\vdots'"
                    :type="'mathjax'"
                    :display-mode="true"
                    class="ellipsis-formula"
                  />
                  <el-text type="info" size="small" class="ellipsis-text">
                    {{ result.totalCount - result.detailedResults.length }} 个整数未显示
                  </el-text>
                </div>
              </div>
            </div>
          </div>

          <!-- 显示不定方程非负整数解计数结果 -->
          <div v-else-if="result.type === 'count-equation-solver'" class="count-equation-solver-result">
            <h5 class="result-title">不定方程非负整数解计数结果：</h5>

            <!-- 方程信息 -->
            <div class="result-basic">
              <h6>方程：</h6>
              <math-renderer
                :formula="result.formula"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
            </div>

            <!-- 约束条件 -->
            <div v-if="result.filterLaTeX" class="filter-condition">
              <h6>约束条件：</h6>
              <math-renderer
                :formula="result.filterLaTeX"
                :type="'mathjax'"
                :display-mode="true"
                class="filter-formula"
              />
            </div>

            <!-- 统计结果 -->
            <div class="result-statistics">
              <h6>统计结果：</h6>
              <div class="statistics-grid">
                <div class="stat-item">
                  <span class="stat-label">总解数：</span>
                  <span class="stat-value">{{ result.totalCount }}</span>
                </div>
                <div class="stat-item">
                  <span class="stat-label">符合条件的解数：</span>
                  <span class="stat-value">{{ result.acceptedCount }}</span>
                </div>
              </div>
              <div class="combination-formula">
                <math-renderer
                  :formula="result.combinationFormula"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="combination-formula-content"
                />
              </div>
            </div>

            <!-- 详细解列表 -->
            <div v-if="result.solutions && result.solutions.length > 0" class="solutions-display">
              <h6>解列表：</h6>
              <div class="solutions-list">
                <div
                  v-for="solution in result.solutions"
                  :key="solution.number"
                  class="solution-item"
                  :class="{ 'accepted': solution.accepted, 'not-accepted': !solution.accepted }"
                >
                  <div class="solution-header">
                    <span class="solution-number">No.{{ solution.number }}</span>
                    <span class="solution-status" v-if="solution.accepted">
                      , accepted {{ getAcceptedNumber(solution.number, result.solutions) }} solver:
                    </span>
                    <span class="solution-status" v-else>
                      , NOT accepted solver:
                    </span>
                  </div>
                  <div class="solution-content">
                    <math-renderer
                      :formula="solution.solutionLaTeX"
                      :type="'mathjax'"
                      :display-mode="false"
                      class="solution-formula"
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 显示不同性质的函数计数结果 -->
          <div v-else-if="result.type === 'countFunction'" class="count-function-result">
            <h5 class="result-title">不同性质的函数计数结果：</h5>

            <!-- 集合信息 -->
            <div class="result-basic">
              <h6>输入集合：</h6>
              <div class="set-info">
                <math-renderer
                  :formula="'A = ' + result.setALaTeX"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="result-formula"
                />
                <math-renderer
                  :formula="'B = ' + result.setBLaTeX"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="result-formula"
                />
              </div>
            </div>

            <!-- 统计信息 -->
            <div class="result-statistics">
              <h6>函数统计：</h6>
              <div class="statistics-grid">
                <div class="stat-item">
                  <span class="stat-label">总数：</span>
                  <span class="stat-value">{{ result.totalCount }}</span>
                </div>
                <div class="stat-item">
                  <span class="stat-label">双射：</span>
                  <span class="stat-value bijection">{{ result.bijectionCount }}</span>
                </div>
                <div class="stat-item">
                  <span class="stat-label">满射：</span>
                  <span class="stat-value surjection">{{ result.surjectionCount }}</span>
                </div>
                <div class="stat-item">
                  <span class="stat-label">单射：</span>
                  <span class="stat-value injection">{{ result.injectionCount }}</span>
                </div>
              </div>

              <!-- 统计公式 -->
              <div class="count-formula">
                <math-renderer
                  :formula="'|B|^{|A|} = ' + result.bSize + '^{' + result.aSize + '} = ' + result.totalCount"
                  :type="'mathjax'"
                  :display-mode="true"
                  class="result-formula"
                />
              </div>
            </div>

            <!-- 函数列表 -->
            <div v-if="result.functionList && result.functionList.length > 0" class="functions-display">
              <h6>具体函数列表：</h6>
              <div class="functions-list">
                <div
                  v-for="func in result.functionList"
                  :key="'func-' + func.totalNumber"
                  class="function-item"
                >
                  <span class="function-label">
                    {{ getFunctionLabel(func) }}
                  </span>
                  <math-renderer
                    :formula="func.laTeX"
                    :type="'mathjax'"
                    :display-mode="true"
                    class="function-formula"
                  />
                </div>
                <div v-if="result.hasMoreFunctions" class="more-functions">
                  <span class="ellipsis-text">... 还有更多函数未显示 ...</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 排列生成结果 -->
          <div v-else-if="result.type === 'gen-permutation'" class="gen-permutation-result">
            <h5 class="result-title">排列生成分析结果：</h5>

            <!-- 基本信息 -->
            <div class="result-basic">
              <h6>生成参数：</h6>
              <math-renderer
                :formula="result.formula"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
              <div class="parameter-info">
                <p><strong>集合 B：</strong> {{ result.baseSet }}</p>
                <p><strong>排列长度：</strong> {{ result.length }}</p>
                <p><strong>起始排列：</strong> {{ result.startString || '从第一个开始' }}</p>
                <p><strong>生成个数：</strong> {{ result.number }}</p>
                <p><strong>总排列数：</strong> {{ result.totalCount }}</p>
              </div>
            </div>

            <!-- 生成结果 -->
            <div v-if="result.permutations && result.permutations.length > 0" class="permutations-result">
              <h6>生成的排列序列：</h6>
              <div class="permutations-grid">
                <div
                  v-for="(perm, permIndex) in result.permutations"
                  :key="'perm-' + permIndex"
                  class="permutation-item"
                >
                  <math-renderer
                    :formula="perm"
                    :type="'mathjax'"
                    :display-mode="false"
                    class="permutation-formula"
                  />
                  <span v-if="permIndex < result.permutations.length - 1" class="arrow">→</span>
                </div>
                <div v-if="result.permutations.length >= result.number" class="continuation">
                  <span>··· ···</span>
                </div>
              </div>
            </div>

            <!-- 警告信息 -->
            <div v-if="!result.startStringFound" class="warning-section">
              <el-alert
                title="起始字符串警告"
                :description="'要生成的起始字符串 ' + result.startString + ' 不在生成的排列之中！'"
                type="warning"
                :closable="false"
                show-icon
              />
            </div>

            <!-- 统计信息 -->
            <div class="statistics-section">
              <h6>统计信息：</h6>
              <p>{{ result.message }}</p>
            </div>
          </div>

          <!-- 组合生成结果 -->
          <div v-else-if="result.type === 'genCombination'" class="gen-combination-result">
            <h5 class="result-title">组合生成分析结果：</h5>

            <!-- 基本信息 -->
            <div class="result-basic">
              <h6>生成参数：</h6>
              <math-renderer
                :formula="result.latexDescription"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
              <div class="parameter-info">
                <p><strong>基础集 B：</strong> {{ result.baseSet }}</p>
                <p><strong>组合长度：</strong> {{ result.length }}</p>
                <p><strong>起始组合：</strong> {{ result.startString || '从第一个开始' }}</p>
                <p><strong>生成个数：</strong> {{ result.number }}</p>
                <p><strong>总组合数：</strong> {{ result.totalCombinations }}</p>
              </div>
            </div>

            <!-- 生成结果 -->
            <div v-if="result.generatedCombinations && result.generatedCombinations.length > 0" class="combinations-result">
              <h6>生成的组合序列：</h6>
              <div class="combinations-grid">
                <div
                  v-for="(combination, combIndex) in result.generatedCombinations"
                  :key="'combination-' + combIndex"
                  class="combination-item"
                >
                  <math-renderer
                    :formula="combination"
                    :type="'mathjax'"
                    :display-mode="false"
                    class="combination-formula"
                  />
                  <span v-if="combIndex < result.generatedCombinations.length - 1" class="arrow">→</span>
                </div>
              </div>
              <div v-if="result.generatedCombinations.length >= result.number" class="continuation">
                ... (更多组合)
              </div>
            </div>

            <!-- 警告信息 -->
            <div v-if="result.errorMessage && result.errorMessage.includes('警告')" class="warning-section">
              <el-alert
                :title="result.errorMessage"
                type="warning"
                :closable="false"
                show-icon
              />
            </div>

            <!-- 统计信息 -->
            <div class="statistics-section">
              <h6>统计信息：</h6>
              <math-renderer
                :formula="`总组合数 = ${result.latexFormula}`"
                :type="'mathjax'"
                :display-mode="true"
                class="statistics-formula"
              />
              <p>从基础集 {{ result.baseSet }} 中取 {{ result.length }} 个元素进行组合，总共有 {{ result.totalCombinations }} 种不同的组合。</p>
            </div>
          </div>

          <!-- 允许重复组合的生成结果 -->
          <div v-else-if="result.type === 'gen-rep-comb'" class="gen-rep-comb-result">
            <h5 class="result-title">允许重复组合的生成分析结果：</h5>

            <!-- 基本信息 -->
            <div class="result-basic">
              <h6>生成参数：</h6>
              <math-renderer
                :formula="`B = ${result.baseSetLaTeX}, \\quad n = ${result.length}, \\quad \\text{从} \\, ${result.startString} \\, \\text{开始生成} \\, ${result.number} \\, \\text{个组合}`"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
              <div class="parameter-info">
                <p><strong>基础集 B：</strong> {{ result.baseSetLaTeX }}</p>
                <p><strong>组合长度：</strong> {{ result.length }}</p>
                <p><strong>起始组合：</strong> {{ result.startString || '从第一个开始' }}</p>
                <p><strong>生成个数：</strong> {{ result.number }}</p>
                <p><strong>总组合数：</strong> {{ result.totalCombinations }}</p>
              </div>
            </div>

            <!-- 生成结果 -->
            <div v-if="result.generatedCombinations && result.generatedCombinations.length > 0" class="combinations-result">
              <h6>生成的组合序列：</h6>
              <div class="combinations-grid">
                <div v-for="(combo, index) in result.generatedCombinations" :key="index" class="combination-item">
                  <math-renderer
                    :formula="combo"
                    :type="'mathjax'"
                    :display-mode="true"
                    class="combination-formula"
                  />
                  <span v-if="index < result.generatedCombinations.length - 1 && index < result.number" class="arrow">→</span>
                  <span v-if="index === result.number - 1" class="continuation">⋯⋯</span>
                </div>
              </div>
            </div>

            <!-- 警告信息 -->
            <div v-if="result.errorMessage" class="warning-section">
              <el-alert
                :title="result.errorMessage"
                type="error"
                :closable="false"
                show-icon
              />
            </div>

            <!-- 统计信息 -->
            <div class="statistics-section">
              <h6>统计信息：</h6>
              <math-renderer
                :formula="result.combinationCountLaTeX"
                :type="'mathjax'"
                :display-mode="true"
                class="statistics-formula"
              />
              <p>从基础集 {{ result.baseSetLaTeX }} 中允许重复地取 {{ result.length }} 个元素进行组合，总共有 {{ result.totalCombinations }} 种不同的组合。</p>
            </div>
          </div>

          <!-- 群U(m)分析结果 -->
          <div v-else-if="result.type === 'groupUm'" class="group-um-result">
            <!-- 基本信息 -->
            <div class="result-basic">
              <h6>群的基本信息：</h6>
              <math-renderer
                :formula="result.formula"
                :type="'katex'"
                :display-mode="true"
                class="result-formula"
              />
            </div>

            <!-- U(m)运算表 -->
            <div v-if="result.operatorTable" class="operator-table-result">
              <h6>群U({{ result.m }})的运算表：</h6>
              <div class="operator-table-container">
                <math-renderer
                  :formula="result.operatorTable"
                  :type="'katex'"
                  :display-mode="true"
                  class="operator-formula"
                />
              </div>
            </div>

            <!-- 循环群信息 -->
            <div v-if="result.showCycleGroup" class="cycle-group-result">
              <h6>循环群分析：</h6>
              <div v-if="result.cycleGroup" class="cycle-group-info">
                <p>群 U({{ result.m }}) 是循环群，生成元：{{ result.generators }}</p>
              </div>
              <div v-else class="cycle-group-info">
                <p>群 U({{ result.m }}) 不是循环群</p>
              </div>
            </div>

            <!-- 群元素的幂 -->
            <div v-if="result.showPower && result.elementPowers" class="power-result">
              <h6>群元素的幂：</h6>
              <div v-for="(powerInfo, idx) in result.elementPowers" :key="idx" class="power-item">
                <div class="power-item-container">
                  <p><strong>元素 {{ powerInfo.element }} 的幂：</strong></p>
                  <div class="power-list">
                    <math-renderer
                      v-for="(power, powerIdx) in powerInfo.powers"
                      :key="powerIdx"
                      :formula="power"
                      :type="'mathjax'"
                      :display-mode="false"
                      class="power-formula-inline"
                    />
                  </div>
                </div>
              </div>
            </div>

            <!-- 群元素的阶 -->
            <div v-if="result.showOrder && result.elementOrders" class="order-result">
              <h6>群元素的阶：</h6>
              <div class="order-grid">
                <math-renderer
                  v-for="(orderInfo, idx) in result.elementOrders"
                  :key="idx"
                  :formula="orderInfo.formula"
                  :type="'mathjax'"
                  :display-mode="false"
                  class="order-formula"
                />
              </div>
            </div>

            <!-- 子群信息 -->
            <div v-if="result.showSubgroups && result.subgroups" class="subgroup-result">
              <h6>群的非平凡子群：</h6>
              <div v-for="(subgroup, idx) in result.subgroups" :key="idx" class="subgroup-item">
                <div class="subgroup-info">
                  <p><strong>子群：</strong> { {{ subgroup.elements }} }</p>
                  <div v-if="subgroup.isCycleSubgroup">
                    <p>它是循环子群，生成元：{{ subgroup.generators }}</p>
                  </div>
                  <div v-else>
                    <p>它不是循环子群</p>
                  </div>
                  <div class="subgroup-operator">
                    <math-renderer
                      :formula="subgroup.operatorTable"
                      :type="'katex'"
                      :display-mode="true"
                      class="operator-formula"
                    />
                  </div>
                </div>

                <!-- 陪集信息 -->
                <div v-if="result.showCosets && subgroup.cosets && subgroup.cosets.length > 0" class="coset-info">
                  <p><strong>子群 { {{ subgroup.elements }} } 的陪集包括：</strong></p>
                  <div class="coset-grid">
                    <math-renderer
                      v-for="(coset, cosetIdx) in subgroup.cosets"
                      :key="cosetIdx"
                      :formula="coset"
                      :type="'mathjax'"
                      :display-mode="false"
                      class="coset-formula"
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 布尔代数判断结果 -->
          <div v-else-if="result.type === 'bool-algebra'" class="bool-algebra-result">
            <h4 class="result-title">{{ result.index }}: 布尔代数分析结果 - F({{ result.m }})</h4>

            <!-- 格的基本信息 -->
            <div class="lattice-basic-info">
              <h5>格的基本信息：</h5>
              <math-renderer
                :formula="result.formula"
                :type="'mathjax'"
                :display-mode="true"
                class="result-formula"
              />
              <div class="boolean-judgment">
                <el-tag :type="result.booleanAlgebra ? 'success' : 'warning'" size="large">
                  {{ result.booleanAlgebra ? '该格是布尔代数' : '该格不是布尔代数' }}
                </el-tag>
              </div>
            </div>

            <!-- 最大元和最小元 -->
            <div v-if="result.greatestElement && result.leastElement" class="extreme-elements">
              <h5>极值元素：</h5>
              <div class="elements-display">
                <span>最大元：</span>
                <math-renderer
                  :formula="result.greatestElement"
                  :type="'mathjax'"
                  :display-mode="false"
                  class="element-formula"
                />
                <span>，最小元：</span>
                <math-renderer
                  :formula="result.leastElement"
                  :type="'mathjax'"
                  :display-mode="false"
                  class="element-formula"
                />
              </div>
            </div>

            <!-- 哈斯图 -->
            <div v-if="result.hasseDiagramUrl" class="hasse-diagram-display">
              <h5>哈斯图：</h5>
              <div class="diagram-container">
                <img
                  :src="result.hasseDiagramUrl"
                  alt="哈斯图"
                  class="hasse-image"
                  @error="onImageError"
                />
              </div>
            </div>

            <!-- 运算表 -->
            <div v-if="result.supOperatorTable && result.subOperatorTable" class="operation-tables-display">
              <h5>运算表（上确界和下确界运算）：</h5>

              <div class="operation-table">
                <h6>上确界运算表：</h6>
                <math-renderer
                  :formula="result.supOperatorTable"
                  :type="'katex'"
                  :display-mode="true"
                  class="table-formula"
                />
              </div>

              <div class="operation-table">
                <h6>下确界运算表：</h6>
                <math-renderer
                  :formula="result.subOperatorTable"
                  :type="'katex'"
                  :display-mode="true"
                  class="table-formula"
                />
              </div>
            </div>

            <!-- 补元信息 -->
            <div v-if="result.complementInfos && result.complementInfos.length > 0" class="complements-display">
              <h5>补元信息：</h5>
              <div class="complements-list">
                <div v-for="(compInfo, compIndex) in result.complementInfos" :key="compIndex" class="complement-item">
                  <div class="element-info">
                    元素
                    <math-renderer
                      :formula="compInfo.element"
                      :type="'mathjax'"
                      :display-mode="false"
                      class="element-formula"
                    />
                    <span v-if="compInfo.hasComplement"> 的补元：</span>
                    <span v-else> 没有补元</span>
                  </div>
                  <div v-if="compInfo.hasComplement" class="complement-formula">
                    <math-renderer
                      :formula="compInfo.complements"
                      :type="'mathjax'"
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
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Delete, Picture } from '@element-plus/icons-vue'
import MathRenderer from '@/components/common/MathRenderer.vue'

// 定义 props
const props = defineProps({
  formulaResults: {
    type: Array,
    default: () => []
  },
  currentFormula: {
    type: String,
    default: '\\forall x \\in S, P(x) \\rightarrow Q(x)'
  }
})

// 定义 emits
const emit = defineEmits(['clear'])

// 引用
const content = ref(null)

// 处理鼠标滚轮事件
const handleWheel = (event) => {
  // 检查鼠标是否位于可滚动的内部元素上
  const target = event.target
  let isScrollableElement = false

  // 检查目标元素或其父元素是否是可滚动的
  let currentElement = target
  while (currentElement && currentElement !== event.currentTarget) {
    const computedStyle = window.getComputedStyle(currentElement)
    const overflowY = computedStyle.overflowY
    const overflow = computedStyle.overflow

    if ((overflowY === 'scroll' || overflowY === 'auto' || overflow === 'scroll' || overflow === 'auto') &&
        currentElement.scrollHeight > currentElement.clientHeight) {
      isScrollableElement = true
      break
    }
    currentElement = currentElement.parentElement
  }

  // 如果鼠标位于可滚动元素上，允许默认滚动行为
  if (isScrollableElement) {
    return // 不阻止默认行为，让内部元素处理滚动
  }

  // 确保事件存在有效的内容引用
  if (!content.value) return

  // 阻止默认滚动行为和事件冒泡
  event.preventDefault()
  event.stopPropagation()

  // 获取滚动增量，支持不同浏览器和设备
  const delta = event.deltaY || event.detail || event.wheelDelta

  // 根据不同浏览器处理滚动方向
  // 现代浏览器deltaY向下为正值，wheelDelta向下为负值
  const scrollDirection = delta > 0 ? 1 : -1

  // 动态计算滚动步长，考虑以下因素：
  // 1. 基础步长
  // 2. 滚动强度（delta值大小）
  // 3. 系统滚动偏好（如果可用）
  const baseStepSize = 30
  const intensityMultiplier = Math.min(Math.abs(delta) / 50, 5) // 限制最大倍数
  const stepSize = baseStepSize * (1 + intensityMultiplier * 0.5)

  // 计算最终滚动距离
  const scrollDistance = scrollDirection * stepSize

  // 使用平滑滚动
  const currentScrollTop = content.value.scrollTop
  const targetScrollTop = currentScrollTop + scrollDistance

  // 确保不超出边界
  const maxScrollTop = content.value.scrollHeight - content.value.clientHeight
  const finalScrollTop = Math.max(0, Math.min(targetScrollTop, maxScrollTop))

  // 使用requestAnimationFrame实现平滑滚动
  if (content.value.smoothScrolling) {
    // 如果正在滚动中，取消之前的滚动
    cancelAnimationFrame(content.value.smoothScrolling)
  }

  const animateScroll = () => {
    const distance = finalScrollTop - content.value.scrollTop
    if (Math.abs(distance) > 1) {
      content.value.scrollTop += distance  // 平滑系数，和右面板保持一致
      content.value.smoothScrolling = requestAnimationFrame(animateScroll)
    } else {
      content.value.scrollTop = finalScrollTop
      content.value.smoothScrolling = null
    }
  }

  content.value.smoothScrolling = requestAnimationFrame(animateScroll)
}

// 计算到指定位置为止被接受的解的数量
const getAcceptedNumber = (currentNumber, solutions) => {
  let acceptedCount = 0
  for (let i = 0; i < solutions.length; i++) {
    if (solutions[i].number === currentNumber) {
      if (solutions[i].accepted) {
        acceptedCount++
      }
      break
    }
    if (solutions[i].accepted) {
      acceptedCount++
    }
  }
  return acceptedCount
}

// 处理清空按钮点击
const handleClear = () => {
  emit('clear')
}

// 生成函数标签
const getFunctionLabel = (func) => {
  if (func.type === 'bijection') {
    return `第 ${func.totalNumber} 个函数，双射第 ${func.countNumber} 个：`
  } else if (func.type === 'injection') {
    return `第 ${func.totalNumber} 个函数，单射第 ${func.countNumber} 个：`
  } else if (func.type === 'surjection') {
    return `第 ${func.totalNumber} 个函数，满射第 ${func.countNumber} 个：`
  } else {
    return `第 ${func.totalNumber} 个函数：`
  }
}

// AST图片加载成功处理
const handleASTImageLoad = (event) => {
  console.log('LeftPanel: AST图片加载成功:', event.target.src)
}

// AST图片加载失败处理
const handleASTImageError = (event) => {
  console.error('LeftPanel: AST图片加载失败:', event.target.src)
  ElMessage.error('抽象语法树图片加载失败')
}

// 图形可视化图片加载失败处理
const handleGraphImageError = (event) => {
  console.error('LeftPanel: 图形可视化加载失败:', event.target.src)
  ElMessage.error('图形可视化加载失败')
}

// 哈斯图图片加载失败处理
const onImageError = (event) => {
  console.error('LeftPanel: 哈斯图加载失败:', event.target.src)
  ElMessage.error('哈斯图加载失败')
  event.target.style.display = 'none'
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

// 统一处理公式格式，将双反斜杠转换为单反斜杠
const normalizeFormulaFormat = (formula) => {
  if (!formula) return ''

  return formula
    // 处理双反斜杠的命令
    .replace(/\\\\([a-zA-Z]+)/g, '\\$1')
    // 处理多余的反斜杠
    .replace(/\\+/g, '\\')
    // 处理LaTeX环境
    .replace(/\\begin\{([^}]+)\}\\s*\\end\{\1\}/g, '')
    // 清理多余的空格和换行
    .replace(/\s+/g, ' ')
    .trim()
}

// 清理公式中的反斜杠，用于显示
const cleanFormulaForDisplay = (formula) => {
  if (!formula) return ''

  return normalizeFormulaFormat(formula)
    // 处理特殊符号显示
    .replace(/\\text\{([^}]+)\}/g, '$1')
    // 处理数学模式
    .replace(/\$([^$]+)\$/g, '$1')
    // 处理LaTeX命令
    .replace(/\\([a-zA-Z]+)/g, (match, command) => {
      const commandMap = {
        'forall': '∀',
        'exists': '∃',
        'in': '∈',
        'notin': '∉',
        'subset': '⊂',
        'subseteq': '⊆',
        'cup': '∪',
        'cap': '∩',
        'emptyset': '∅',
        'land': '∧',
        'lor': '∨',
        'neg': '¬',
        'rightarrow': '→',
        'leftrightarrow': '↔',
        'implies': '⇒',
        'equiv': '≡',
        'times': '×',
        'div': '÷',
        'pm': '±',
        'neq': '≠',
        'geq': '≥',
        'leq': '≤',
        'alpha': 'α',
        'beta': 'β',
        'gamma': 'γ',
        'delta': 'δ',
        'epsilon': 'ε',
        'theta': 'θ',
        'lambda': 'λ',
        'mu': 'μ',
        'pi': 'π',
        'sigma': 'σ',
        'phi': 'φ',
        'omega': 'ω'
      }
      return commandMap[command] || match
    })
}

// 辅助函数：获取性质显示名称
const getPropertyDisplayName = (propertyType) => {
  const names = {
    commutative: '交换律',
    associative: '结合律',
    idempotent: '幂等律',
    cancellation: '消去律',
    identity: '单位元',
    zero: '零元',
    inverse: '逆元'
  }
  return names[propertyType] || propertyType
}

// 辅助函数：获取关系显示名称
const getRelationDisplayName = (relationType) => {
  const names = {
    distributive: '分配律',
    absorption: '吸收律'
  }
  return names[relationType] || relationType
}

// 辅助函数：格式化数学表达式，添加适当的空格
const formatMathExpression = (formula) => {
  if (!formula) return formula

  return formula
    // 在运算符周围添加空格
    .replace(/([a-zA-Z0-9\)}])(\\circ|\\times|\\neq|=|≠|\\leq|\\geq|<|>)([a-zA-Z0-9\({])/g, '$1 $2 $3')
    // 在\circ后特别添加空格
    .replace(/\\circ(?!\s)/g, '\\circ ')
    // 在\neq前后添加空格
    .replace(/\\neq(?!\s)/g, ' \\neq ')
    // 在=前后添加空格
    .replace(/=(?!\s)/g, ' = ')
    // 清理多余的空格
    .replace(/\s+/g, ' ')
    .trim()
}

// 字符串计数相关方法
const getDisplayModeDescription = (displayMode) => {
  const modeMap = {
    'ONLY_RESULT': '只显示结果',
    'ACCEPT_50': '前50个接受的字符串',
    'PARTIAL_100': '前100个字符串',
    'ONLY_ACCEPTED': '所有接受的字符串',
    'ALL_STRINGS': '所有字符串',
    // 支持小写版本
    'onlyResult': '只显示结果',
    'accept50': '前50个接受的字符串',
    'part100': '前100个字符串',
    'onlyAccept': '所有接受的字符串',
    'allString': '所有字符串'
  }
  return modeMap[displayMode] || '未知模式'
}

const getTableData = (result) => {
  // 调试信息
  console.log('LeftPanel getTableData - result:', {
    type: result.type,
    hasStringDetails: !!result.stringDetails,
    stringDetailsLength: result.stringDetails?.length,
    displayMode: result.displayMode,
    totalCount: result.totalCount,
    acceptedCount: result.acceptedCount
  })

  if (!result.stringDetails || !result.displayMode) {
    console.log('LeftPanel getTableData - 返回空数组，原因：stringDetails或displayMode缺失')
    return []
  }

  let tableData = []

  switch (result.displayMode) {
    case 'ACCEPT_50':
    case 'accept50':
      // 显示前50个被接受的字符串
      tableData = result.stringDetails.filter(item => item.accepted).slice(0, 50)
      console.log('LeftPanel getTableData - accept50模式，返回', tableData.length, '个数据')
      break
    case 'PARTIAL_100':
    case 'part100':
      // 显示前100个字符串
      tableData = result.stringDetails.slice(0, 100)
      console.log('LeftPanel getTableData - part100模式，返回', tableData.length, '个数据')
      break
    case 'ONLY_ACCEPTED':
    case 'onlyAccept':
      // 只显示被接受的字符串
      tableData = result.stringDetails.filter(item => item.accepted)
      console.log('LeftPanel getTableData - onlyAccept模式，返回', tableData.length, '个数据')
      break
    case 'ALL_STRINGS':
    case 'allString':
      // 显示所有字符串
      tableData = result.stringDetails
      console.log('LeftPanel getTableData - allString模式，返回', tableData.length, '个数据')
      break
    case 'ONLY_RESULT':
    case 'onlyResult':
      console.log('LeftPanel getTableData - onlyResult模式，不显示详细数据')
      break
    default:
      console.log('LeftPanel getTableData - 未知模式', result.displayMode)
      break
  }

  return tableData
}

const getMaxHeight = () => {
  // 返回表格的最大高度，与子界面保持一致
  return 300
}

const hasMoreResults = (result) => {
  const totalData = result.stringDetails || []

  if (!result.displayMode) {
    return false
  }

  switch (result.displayMode) {
    case 'ACCEPT_50':
    case 'accept50':
      // 如果被接受的字符串超过50个
      const acceptedCount = totalData.filter(item => item.accepted).length
      return acceptedCount > 50
    case 'PARTIAL_100':
    case 'part100':
      // 如果总字符串超过100个
      return totalData.length > 100
    case 'ONLY_ACCEPTED':
    case 'onlyAccept':
    case 'ALL_STRINGS':
    case 'allString':
      // 显示全部，没有更多
      return false
    default:
      return false
  }
}

const getMoreIndicatorText = (result) => {
  if (!result.stringDetails || !result.displayMode) {
    return ''
  }

  const totalData = result.stringDetails

  switch (result.displayMode) {
    case 'ACCEPT_50':
    case 'accept50':
      const acceptedCount = totalData.filter(item => item.accepted).length
      return `... 还有 ${acceptedCount - 50} 个被接受的字符串 ...`
    case 'PARTIAL_100':
    case 'part100':
      return `... 还有 ${totalData.length - 100} 个字符串 ...`
    default:
      return ''
  }
}
</script>


<style scoped>
.left-panel {
  width: 50%;
  height: 100%;
  border: 1px solid #e4e7ed;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  min-width: 300px;
  flex: 1;
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
  margin-bottom: 2rem;
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

/* 公式结果通用样式 */
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
  color: #2c3e50;
  overflow-x: auto;
  padding: 0.5rem;
  background: #ffffff;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.result-formula :deep(.math-renderer) {
  white-space: nowrap;
  max-width: none;
}

/* 群U(m)专用公式容器样式 - 保持与原来一致 */
.result-formula.group-um-formula {
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

.result-formula.group-um-formula :deep(.math-renderer) {
  white-space: nowrap;
  max-width: none;
}

.result-title {
  margin: 0 0 1rem 0;
  color: #2c3e50;
  font-size: 1rem;
  font-weight: 600;
}

/* 内容区域通用样式 */
.syntax-content,
.ast-content,
.detailed-steps {
  margin: 1rem 0;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

/* 范式结果容器样式 */
.normal-form-results {
  margin: 1.5rem 0;
  padding: 1.5rem;
  background: #ffffff;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.normal-form-result {
  margin-bottom: 1.5rem;
  padding: 1.25rem;
  background: #fafafa;
  border-radius: 6px;
  border: 1px solid #e9ecef;
  transition: all 0.2s ease;
}

.normal-form-result:hover {
  background: #f5f7fa;
  border-color: #d4d7dc;
}

.normal-form-result:last-child {
  margin-bottom: 0;
}

.calculation-steps {
  margin: 1rem 0;
  padding: 1rem;
  background: #ffffff;
  border-radius: 4px;
  border: 1px solid #f0f0f0;
}

.calculation-step {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin: 0.75rem 0;
  padding: 0.75rem;
  background: #f8f9fa;
  border-radius: 4px;
  border-left: 3px solid #6c757d;
  transition: all 0.2s ease;
  overflow-x: auto;
  overflow-y: hidden;
}

.calculation-step::-webkit-scrollbar {
  height: 6px;
  width: 6px;
}

.calculation-step::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.calculation-step::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.calculation-step::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}

.calculation-step:hover {
  background: #f1f3f4;
  border-left-color: #495057;
}

.step-formula {
  flex: 1;
  font-size: 1rem;
}

.step-comment {
  color: #6c757d;
  font-size: 0.9rem;
  font-style: italic;
  background: #e9ecef;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  white-space: nowrap;
}

.final-result {
  margin-top: 1rem;
  padding: 1rem;
  background: #ffffff;
  border-radius: 6px;
  border: 1px solid #e9ecef;
  font-weight: 500;
}

.final-result strong {
  color: #495057;
  font-size: 1.05rem;
}

.pnf-result {
  margin-top: 1.5rem;
  padding: 1.25rem;
  background: #fafafa;
  border-radius: 6px;
  border: 1px solid #e9ecef;
}
.pcnf-result {
  margin-top: 1.5rem;
  padding: 1.25rem;
  background: #fafafa;
  border-radius: 6px;
  border: 1px solid #e9ecef;
}

.pnf-title {
  margin: 0 0 1rem 0;
  color: #495057;
  font-size: 1.1rem;
  font-weight: 600;
}

.pnf-result .math-renderer {
  background: #ffffff;
  padding: 0.5rem;
  border-radius: 4px;
  border: 1px solid #f0f0f0;
}

/* 特殊结果容器样式 */
.expansion-steps,
.calculus-steps {
  margin: 1.5rem 0;
  padding: 1.5rem;
  background: #f0f8ff;
  border-radius: 8px;
  border: 2px solid #4a90e2;
}

/* 真值表样式 */
.truth-table-title {
  margin: 0 0 1rem 0;
  color: #2c3e50;
  font-size: 1rem;
  font-weight: 600;
}

.truth-table-container {
  overflow-x: auto;
  overflow-y: hidden;
  padding: 1rem 0;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  background: #fafafa;
  margin: 1rem 0;
}

.truth-table-container::-webkit-scrollbar {
  height: 8px;
  width: 8px;
}

.truth-table-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.truth-table-container::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.truth-table-container::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}

.truth-table-content {
  min-width: max-content;
}

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

/* 图遍历结果样式 */
.graph-travel-result {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  margin-top: 1rem;
}

.graph-basic-info {
  margin: 1rem 0;
}

.graph-formula {
  font-size: 1.1rem;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.node-degrees {
  margin: 1rem 0;
  background: white;
  padding: 1rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.degrees-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 0.5rem;
  margin-top: 0.5rem;
}

.degree-item {
  background: #f8f9fa;
  padding: 0.5rem;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.directed-degrees {
  margin-top: 0.25rem;
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  font-size: 0.9em;
}

.matrices {
  margin: 1rem 0;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1rem;
}

.matrix-item {
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.matrix-formula {
  overflow-x: auto;
  font-size: 0.9em;
}

/* 最小生成树相关样式 */
.spanning-tree-result {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 6px;
  border: 1px solid #e9ecef;
  margin-bottom: 1rem;
}

.algorithm-summary {
  margin: 1rem 0;
}

.summary-formula {
  padding: 0.5rem;
  background: #e3f2fd;
  border-radius: 4px;
  border: 1px solid #2196f3;
  font-weight: 600;
  font-size: 0.95em;
}

.algorithm-edges {
  margin: 1rem 0;
}

.edges-formula {
  padding: 0.5rem;
  background: #f1f8e9;
  border-radius: 4px;
  border: 1px solid #4caf50;
  font-size: 0.9em;
}

.algorithm-steps {
  margin: 1rem 0;
  overflow-x: auto;
}

.steps-formula {
  padding: 0.75rem;
  background: #fff3e0;
  border-radius: 4px;
  border: 1px solid #ff9800;
  font-size: 0.85em;
  line-height: 1.4;
}

.tree-visualization {
  margin: 1rem 0;
}

.tree-image-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 0.75rem;
  background: #f5f5f5;
  border-radius: 4px;
  border: 1px solid #ddd;
  min-height: 80px;
}

.tree-image {
  max-width: 100%;
  height: auto;
  max-height: 200px;
  border-radius: 3px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  object-fit: contain;
}

.path-matrices {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.path-matrix {
  margin: 0.5rem 0;
  overflow-x: auto;
  font-size: 0.9em;
}

.dfs-result,
.bfs-result {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.traversal-result {
  margin: 0.5rem 0;
  font-size: 1.05em;
}

.travel-steps {
  margin-top: 0.5rem;
}

.step-item {
  margin: 0.5rem 0;
  padding: 0.25rem 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border-left: 3px solid #409eff;
  font-size: 0.9em;
  overflow-x: auto;
  white-space: nowrap;
}

.step-formula {
  font-size: 0.9em;
}

/* 推理论证检查结果样式 */
.reason-argument-result {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  margin-top: 1rem;
}

.reasoning-overview {
  margin: 1rem 0;
  padding: 1rem;
  background: #ffffff;
  border-radius: 6px;
  border: 1px solid #f0f0f0;
  overflow-x: auto;
  overflow-y: hidden;
}

.reasoning-overview::-webkit-scrollbar {
  height: 6px;
  width: 6px;
}

.reasoning-overview::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.reasoning-overview::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.reasoning-overview::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}

.reasoning-step {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin: 0.75rem 0;
  padding: 0.75rem;
  background: #ffffff;
  border-radius: 4px;
  border-left: 3px solid #dddada;
  transition: all 0.2s ease;
  overflow-x: auto;
  overflow-y: hidden;
}

.reasoning-step::-webkit-scrollbar {
  height: 6px;
  width: 6px;
}

.reasoning-step::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.reasoning-step::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.reasoning-step::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}

.reasoning-step:hover {
  background: #f1f3f4;
  border-left-color: #495057;
}

.step-number {
  color: #495057;
  font-weight: 600;
  font-size: 0.9rem;
  min-width: 30px;
}

.step-rule {
  color: #6c757d;
  font-size: 0.85rem;
  font-style: italic;
  background: #e9ecef;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  white-space: nowrap;
}

.check-process {
  margin: 1.5rem 0;
  padding: 1rem;
  background: #ffffff;
  border-radius: 6px;
  border: 1px solid #f0f0f0;
}

.check-step {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin: 0.75rem 0;
  padding: 0.75rem;
  background: #f8f9fa;
  border-radius: 4px;
  border-left: 3px solid #6c757d;
  transition: all 0.2s ease;
  overflow-x: auto;
  overflow-y: hidden;
}

.check-step::-webkit-scrollbar {
  height: 6px;
  width: 6px;
}

.check-step::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.check-step::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.check-step::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}

.check-step:hover {
  background: #f1f3f4;
  border-left-color: #495057;
}

.check-text {
  color: #495057;
  font-weight: 500;
  font-size: 0.9rem;
  min-width: 120px;
}

.check-result {
  margin: 1.5rem 0;
}

.invalid-result {
  margin-top: 1rem;
}

.counter-example {
  margin-top: 1rem;
  padding: 1rem;
  background: #fafafa;
  border-radius: 6px;
  border: 1px solid #e9ecef;
}

.counter-example h5 {
  margin: 0 0 0.5rem 0;
  color: #495057;
  font-size: 1rem;
  font-weight: 600;
}

.counter-example p {
  margin: 0 0 0.75rem 0;
  color: #6c757d;
}

.checking-formula {
  padding: 0.75rem;
  background: #ffffff;
  border-radius: 4px;
  border: 1px solid #f0f0f0;
}

.graph-visualization {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.graph-image-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  min-height: 80px;
}

.graph-image {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  object-fit: contain;
}

/* 特殊图结果样式 */
.special-graph-result {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  margin-top: 1rem;
}

.params-info {
  margin: 1rem 0;
}

.params-formula {
  font-size: 1.1rem;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.graphs-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1rem;
  margin-top: 1rem;
}

.graph-item {
  background: white;
  padding: 1rem;
  border-radius: 6px;
  border: 1px solid #dee2e6;
}

.graph-header {
  margin-bottom: 1rem;
  text-align: center;
}

.graph-name {
  font-size: 1.1rem;
  color: #409eff;
}

.graph-content {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 120px;
}

.graph-error {
  width: 100%;
}

/* 树遍历结果样式 */
.tree-traversal-result {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  margin-top: 1rem;
}

/* Huffman树结果样式 */
.huffman-tree-result {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 6px;
  border: 1px solid #e9ecef;
  margin-top: 1rem;
  border-left: 4px solid #ffffff;
}

.huffman-tree-result .leaf-set-display {
  margin: 1rem 0;
  background: white;
  padding: 1rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.huffman-tree-result .leaf-set-formula {
  overflow-x: auto;
  font-size: 1.1rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.huffman-tree-result .algorithm-steps {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.huffman-tree-result .huffman-step-item {
  margin: 1.5rem 0;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 6px;
  border-left: 4px solid #28a745;
  overflow-x: auto;
}

.huffman-tree-result .step-image {
  margin-top: 1rem;
  text-align: center;
}

.huffman-tree-result .forest-image {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.huffman-tree-result .final-tree {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.huffman-tree-result .weight-calculation {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  overflow-x: auto;
}

.huffman-tree-result .weight-formula {
  margin: 1rem 0;
  font-size: 1.1rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  overflow-x: auto;
}

.huffman-tree-result .weight-result {
  margin-top: 0.5rem;
  font-size: 1rem;
  color: #374151;
  font-weight: 500;
}

.huffman-tree-result .leaf-codes {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.huffman-tree-result .codes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 0.5rem;
  margin-top: 1rem;
}

.huffman-tree-result .code-item {
  background: #d4edda;
  padding: 0.8rem;
  border-radius: 6px;
  border: 1px solid #c3e6cb;
  text-align: center;
}

.huffman-tree-result .code-formula {
  font-size: 0.9rem;
}

.root-info {
  margin: 1rem 0;
  background: #ffffff;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dbd8d8;
}

.root-formula {
  display: inline-block;
  margin-left: 0.5rem;
  font-size: 1.1rem;
}

.preorder-result,
.inorder-result,
.postorder-result {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}



/* 最短路径结果样式 */
.shortest-path-result {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  margin-top: 1rem;
}

.start-node-info {
  margin: 1rem 0;
  background: #fff3cd;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #ffeaa7;
}

.start-node-formula {
  display: inline-block;
  margin-left: 0.5rem;
  font-size: 1.1rem;
  color: #856404;
}

.weight-matrix {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.dijkstra-details {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.algorithm-content {
  overflow-x: auto;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.algorithm-formula {
  font-size: 0.9em;
}

.shortest-paths {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.paths-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 0.5rem;
  margin-top: 0.5rem;
}

.path-item {
  background: #d4edda;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #c3e6cb;
  text-align: center;
  overflow-x: auto;
  white-space: nowrap;
}

.path-formula {
  font-size: 1rem;
  color: #155724;
}

.path-graph-visualization {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

/* 原图可视化结果样式 */
.graph-visualization-result {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  margin-top: 1rem;
  border-left: 4px solid #17a2b8;
}

/* 最短路径图可视化结果样式 */
.path-visualization-result {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  margin-top: 1rem;
  border-left: 4px solid #6f42c1;
}

/* 集合运算结果样式 */
.set-operation-result {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 6px;
  border: 1px solid #e9ecef;
  margin-top: 1rem;
  border-left: 4px solid #28a745;
}

.set-operation-result .result-basic {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  overflow-x: auto;
}

.set-operation-result .operation-result {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  overflow-x: auto;
}

.set-operation-result .operation-formula {
  margin: 0.5rem 0;
  font-size: 1.05rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  overflow-x: auto;
}

.set-operation-result .power-set-result {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.set-operation-result .power-set-item {
  margin: 0.5rem 0;
  overflow-x: auto;
}

.set-operation-result .power-set-formula {
  margin: 0.5rem 0;
  font-size: 0.9rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  overflow-x: auto;
}

/* 置换群分析结果样式 */
.group-perm-result {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 6px;
  border: 1px solid #e9ecef;
  margin-top: 1rem;
  border-left: 4px solid #fd7e14;
  overflow-x: auto;
}

.group-perm-result .result-basic {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  overflow-x: auto;
}

.group-perm-result .operation-result {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  overflow-x: auto;
}

.group-perm-result .operation-formula {
  margin: 0.5rem 0;
  font-size: 1.05rem;
  padding: 0.5rem;
  background: #ffffff;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  overflow-x: auto;
}

.group-perm-result .power-item,
.group-perm-result .subgroup-item,
.group-perm-result .coset-item,
.group-perm-result .normal-item {
  margin: 1rem 0;
  background: #f8f9fa;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  overflow-x: auto;
}

.group-perm-result .coset-section {
  margin: 0.8rem 0;
  background: white;
  padding: 0.5rem;
  border-radius: 3px;
  border: 1px solid #dee2e6;
  overflow-x: auto;
}

.group-perm-result .orders-container {
  overflow-x: auto;
}

/* 置换群结果中的内联公式样式 */
.group-perm-result .inline-formula {
  display: inline;
  vertical-align: baseline;
  margin: 0 0.2rem;
  font-size: inherit;
  line-height: 1;
}

/* 针对 h6 标签中的内联公式进行特殊对齐 */
.group-perm-result h6 .inline-formula {
  vertical-align: baseline;
  margin: 0 0.15rem;
}

/* h6标签中的所有元素统一对齐 */
.group-perm-result h6 span,
.group-perm-result h6 .inline-formula {
  display: inline;
  vertical-align: baseline;
}

/* 深度修复MathJax内联公式的对齐问题 */
.group-perm-result .inline-formula :deep(.MathJax) {
  vertical-align: baseline !important;
  display: inline !important;
  margin: 0 !important;
}

.group-perm-result .inline-formula :deep(.MathJax > mjx-container) {
  vertical-align: baseline !important;
  display: inline !important;
  margin: 0 !important;
  font-size: inherit !important;
}

.group-perm-result .inline-formula :deep(.MathJax > mjx-container > svg) {
  vertical-align: baseline !important;
  display: inline !important;
}

/* 等价关系计算结果样式 */
.equivalence-relation-result {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 6px;
  border: 1px solid #e9ecef;
  margin-top: 1rem;
  border-left: 4px solid #17a2b8;
}

.equivalence-relation-result .result-basic {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  overflow-x: auto;
}

.equivalence-relation-result .properties-result {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.equivalence-relation-result .property-item {
  margin: 0.5rem 0;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.equivalence-relation-result .property-formula {
  margin: 0.5rem 0;
  font-size: 0.9rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  overflow-x: auto;
  flex: 1;
  min-width: 0;
}

.equivalence-relation-result .matrix-result,
.equivalence-relation-result .closure-result,
.equivalence-relation-result .classes-result,
.equivalence-relation-result .quotient-result {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.equivalence-relation-result .matrix-formula,
.equivalence-relation-result .closure-formula,
.equivalence-relation-result .class-formula,
.equivalence-relation-result .quotient-formula {
  margin: 0;
  font-size: 1rem;
  padding: 0.5rem;
  background: #ffffff;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  overflow-x: auto;
}

.equivalence-relation-result .class-title {
  font-size: 1rem;
  font-weight: 600;
  margin: 0 0 0.5rem 0;
  color: #2c3e50;
  line-height: 1.4;
}

.equivalence-relation-result .closure-matrix,
.equivalence-relation-result .closure-graph {
  margin-top: 0.75rem;
}

.equivalence-relation-result .graph-image {
  margin: 0.5rem 0;
  text-align: center;
}

.equivalence-relation-result .image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 120px;
  background: #f5f5f5;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  color: #999;
  font-size: 0.9rem;
}

.equivalence-relation-result .class-item {
  margin: 0.75rem 0;
  padding: 0.75rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

/* 关系性质判断结果样式 */
.relation-property-result {
  margin-bottom: 1rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.relation-property-result .result-basic {
  margin-bottom: 1rem;
  overflow-x: auto;
}

.relation-property-result .result-basic h6 {
  color: #374151;
  margin: 0.5rem 0;
  font-size: 1rem;
  font-weight: 600;
}

.relation-property-result .result-formula {
  margin: 0.5rem 0;
  font-size: 1rem;
  padding: 0.5rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  overflow-x: auto;
}

.relation-property-result .matrix-result,
.relation-property-result .graph-result {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 6px;
  border: 1px solid #dee2e6;
}

.relation-property-result .matrix-result h6,
.relation-property-result .graph-result h6 {
  color: #374151;
  margin: 0 0 0.5rem 0;
  font-size: 0.95rem;
  font-weight: 600;
}

.relation-property-result .matrix-formula {
  margin: 0.5rem 0;
  font-size: 0.9rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  overflow-x: auto;
}

.relation-property-result .graph-image-container {
  margin-top: 0.5rem;
  text-align: center;
}

.relation-property-result .graph-image {
  max-width: 100%;
  height: auto;
  max-height: 200px;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.relation-property-result .property-result {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 6px;
  border: 1px solid #dee2e6;
  overflow-x: auto;
}

.relation-property-result .property-result h6 {
  color: #374151;
  margin: 0 0 0.5rem 0;
  font-size: 0.95rem;
  font-weight: 600;
}

.relation-property-result .property-formula {
  margin: 0.5rem 0;
  font-size: 0.9rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  overflow-x: auto;
}

.relation-property-result .counter-example {
  margin-top: 0.5rem;
  padding: 0.5rem;
  background: #fef2f2;
  border-radius: 4px;
  border: 1px solid #fecaca;
}

.relation-property-result .counter-example-formula {
  margin: 0.25rem 0;
  font-size: 0.85rem;
  padding: 0.4rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #fecaca;
  overflow-x: auto;
}

/* 函数性质判断结果样式 */
.function-property-result {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 6px;
  border: 1px solid #e9ecef;
  margin-top: 1rem;
  border-left: 4px solid #6610f2;
}

.function-property-result .result-basic {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  overflow-x: auto;
}

.function-property-result .function-judgment {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.function-property-result .function-alert {
  margin: 0.5rem 0;
}

.function-property-result .matrix-result {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.function-property-result .matrix-formula {
  margin: 0.5rem 0;
  font-size: 1.05rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  overflow-x: auto;
}

.function-property-result .graph-result {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.function-property-result .graph-image {
  text-align: center;
  margin: 0.5rem 0;
}

.function-property-result .relation-graph {
  border: 1px solid #dee2e6;
  border-radius: 4px;
}

.function-property-result .property-result {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.function-property-result .counter-example {
  margin-top: 0.5rem;
  font-weight: 500;
}

/* 集合表达式运算结果样式 */
.set-expr-operation-result {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 6px;
  border: 1px solid #e9ecef;
  margin-top: 1rem;
  border-left: 4px solid #17a2b8;
}

.set-expr-operation-result .result-basic {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  overflow-x: auto;
}

.set-expr-operation-result .operation-result {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  overflow-x: auto;
}

.set-expr-operation-result .operation-formula {
  margin: 0.5rem 0;
  font-size: 1.05rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  overflow-x: auto;
}

/* 关系运算结果样式 */
.relation-operation-result {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 6px;
  border: 1px solid #e9ecef;
  margin-top: 1rem;
  border-left: 4px solid #6f42c1;
}

.relation-operation-result .result-basic {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  overflow-x: auto;
}

/* 偏序关系结果样式 */
.partial-order-result {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 6px;
  border: 1px solid #e9ecef;
  margin-top: 1rem;
  border-left: 4px solid #fd7e14;
}

.partial-order-result .result-basic {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  overflow-x: auto;
}

.partial-order-result .subset-info {
  margin-top: 0.5rem;
  padding: 0.5rem;
  background: #e3f2fd;
  border-radius: 4px;
  font-size: 0.9rem;
}

.partial-order-result .properties-result {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.partial-order-result .property-item {
  margin: 0.5rem 0;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.partial-order-result .property-text {
  font-size: 0.85rem;
  color: #6c757d;
  margin-left: 0.5rem;
}

.partial-order-result .partial-order-verdict {
  margin-top: 1rem;
  text-align: center;
  padding: 0.5rem;
  background: #fff3cd;
  border-radius: 4px;
  border: 1px solid #ffeaa7;
}

.partial-order-result .matrix-result {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  overflow-x: auto;
}

.partial-order-result .matrix-formula {
  margin: 0.5rem 0;
  font-size: 1.05rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  overflow-x: auto;
}

.partial-order-result .graph-section {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.partial-order-result .graph-result {
  margin: 0.5rem 0;
}

.partial-order-result .graph-image {
  text-align: center;
  margin: 0.5rem 0;
}

.partial-order-result .image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100px;
  color: #6c757d;
  font-size: 0.9rem;
}

.partial-order-result .element-calculation,
.partial-order-result .bound-calculation {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.partial-order-result .element-item,
.partial-order-result .bound-item {
  margin: 0.5rem 0;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.partial-order-result .element-formula,
.partial-order-result .bound-formula {
  font-size: 0.95rem;
  overflow-x: auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .partial-order-result .property-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.25rem;
  }

  .partial-order-result .element-item,
  .partial-order-result .bound-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.25rem;
  }
}

.relation-operation-result .matrix-result {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  overflow-x: auto;
}

.relation-operation-result .operation-result {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  overflow-x: auto;
}

.relation-operation-result .matrix-formula,
.relation-operation-result .operation-formula {
  margin: 0.5rem 0;
  font-size: 1.05rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  overflow-x: auto;
}

.visualization-hint {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}

/* 关系闭包计算结果样式 */
.relation-closure-result {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 6px;
  border: 1px solid #e9ecef;
  margin-top: 1rem;
  border-left: 4px solid #e83e8c;
}

.relation-closure-result .result-basic {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  overflow-x: auto;
}

.relation-closure-result .matrix-result {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  overflow-x: auto;
}

.relation-closure-result .matrix-formula {
  margin: 0.5rem 0;
  font-size: 0.9rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  overflow-x: auto;
}

.relation-closure-result .graph-result {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  overflow-x: auto;
}

.relation-closure-result .graph-image-container {
  margin-top: 0.5rem;
  text-align: center;
}

.relation-closure-result .graph-image {
  max-width: 100%;
  height: auto;
  border: 1px solid #dee2e6;
  border-radius: 4px;
}

.relation-closure-result .closure-result {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  overflow-x: auto;
}

.relation-closure-result .operation-formula {
  margin: 0.5rem 0;
  font-size: 1rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  overflow-x: auto;
}

.relation-closure-result .transitive-details {
  margin: 1rem 0;
  background: #f8f9fa;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  overflow-x: auto;
}

.relation-closure-result .detail-step {
  margin-bottom: 1rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #dee2e6;
}

.relation-closure-result .detail-step:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.relation-closure-result .step-description {
  color: #374151;
  font-weight: 500;
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
}

.relation-closure-result .detail-matrix {
  font-size: 0.85rem;
  padding: 0.4rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  overflow-x: auto;
}

h6 {
  color: #374151;
  margin: 0.5rem 0 0.25rem 0;
  font-size: 1rem;
  font-weight: 600;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .left-panel {
    width: 100%;
    height: 50%;
    min-width: unset;
    border-right: 1px solid #e4e7ed;
    border-bottom: 2px solid #dcdfe6;
  }

  .degrees-grid {
    grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  }

  .matrices {
    grid-template-columns: 1fr;
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
}

/* 排列组合数计算结果样式 */
.comb-calculator-result {
  margin-bottom: 1.5rem;
  padding: 1.5rem;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.basic-info {
  margin-bottom: 1.5rem;
  padding: 1rem;
  background: white;
  border-radius: 6px;
  border: 1px solid #dee2e6;
}

.basic-formula {
  margin: 0.5rem 0;
  font-size: 1.1rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.power-result,
.combination-result,
.permutation-result,
.factorial-result {
  margin: 1rem 0;
  padding: 1rem;
  background: white;
  border-radius: 6px;
  border: 1px solid #dee2e6;
}

.calculation-item {
  margin: 0.5rem 0;
  overflow-x:auto;
}

.calculation-formula {
  font-size: 1.05rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  overflow-x: auto;
}

.comb-calculator-result h6 {
  color: #374151;
  margin: 0.5rem 0;
  font-size: 1rem;
  font-weight: 600;
}

.expression-calculator-result {
  margin-bottom: 1.5rem;
  padding: 1.5rem;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.expression-info,
.calculation-result {
  margin-bottom: 1.5rem;
  padding: 1rem;
  background: white;
  border-radius: 6px;
  border: 1px solid #dee2e6;
}

.expression-formula {
  margin: 0.5rem 0;
  font-size: 1.1rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.result-item-content {
  margin: 0.5rem 0;
}

.result-formula-detail {
  font-size: 1.05rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  overflow-x: auto;
}

.expression-calculator-result h6 {
  color: #374151;
  margin: 0.5rem 0;
  font-size: 1rem;
  font-weight: 600;
}

/* 递归表达式计算结果样式 */
.recursion-expression-result {
  margin-bottom: 1.5rem;
  padding: 1.5rem;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
  border-left: 4px solid #ff6b6b;
}

.recursion-params,
.recursion-result {
  margin-bottom: 1.5rem;
  padding: 1rem;
  background: white;
  border-radius: 6px;
  border: 1px solid #dee2e6;
}

.param-item {
  margin: 0.5rem 0;
  font-size: 0.95rem;
  color: #6c757d;
  line-height: 1.4;
}

.param-item strong {
  color: #374151;
  font-weight: 600;
}

.recursion-formula-detail {
  font-size: 1.05rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  overflow-x: auto;
}

.recursion-expression-result h6 {
  color: #374151;
  margin: 0.5rem 0;
  font-size: 1rem;
  font-weight: 600;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .comb-calculator-result,
  .expression-calculator-result {
    padding: 1rem;
  }

  /* 字符串计数结果移动端优化 */
  .count-string-result .string-details-table {
    max-height: 250px;
  }

  .count-string-result .details-table {
    max-height: 230px;
    font-size: 0.8rem;
  }

  /* 移动端触摸滚动优化 */
  .count-string-result .details-table {
    -webkit-overflow-scrolling: touch;
  }

  .calculation-formula,
  .result-formula-detail {
    font-size: 0.9rem;
  }
}

/* 字符串计数结果样式 */
.count-string-result {
  margin-bottom: 1.5rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.count-string-result .result-basic {
  margin-bottom: 1.5rem;
  background: white;
  padding: 1rem;
  border-radius: 6px;
  border: 1px solid #dee2e6;
}

.count-string-result .parameter-info {
  margin-top: 1rem;
  font-size: 0.95rem;
}

.count-string-result .parameter-info p {
  margin: 0.5rem 0;
  color: #6c757d;
}

.count-string-result .parameter-info strong {
  color: #374151;
  font-weight: 600;
}

.count-string-result .filter-condition {
  margin-top: 0.5rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.count-string-result .filter-formula {
  margin-top: 0.5rem;
  font-size: 1rem;
  padding: 0.5rem;
  background: #f9f9f9;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  overflow-x: auto;
}

.count-string-result .result-statistics {
  margin-bottom: 1.5rem;
  background: white;
  padding: 1rem;
  border-radius: 6px;
  border: 1px solid #dee2e6;
}

.count-string-result .statistics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
  margin-top: 1rem;
}

.count-string-result .stat-item {
  display: flex;
  align-items: center;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.count-string-result .stat-label {
  font-weight: 600;
  margin-right: 0.5rem;
  color: #374151;
}

.count-string-result .stat-value {
  font-weight: 700;
  color: #2563eb;
}

.count-string-result .result-formula-inline {
  display: inline-block;
  font-size: 0.9rem;
}

.count-string-result .result-details {
  background: white;
  padding: 1rem;
  border-radius: 6px;
  border: 1px solid #dee2e6;
}

.count-string-result .string-details-table {
  margin-top: 1rem;
  max-height: 300px;
  overflow: hidden;
}

.count-string-result .details-table {
  width: 100%;
  font-size: 0.85rem;
  max-height: 280px;
  overflow-y: auto;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
}

/* 自定义滚动条样式 */
.count-string-result .details-table::-webkit-scrollbar {
  width: 6px;
}

.count-string-result .details-table::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.count-string-result .details-table::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
  border: 1px solid #f1f1f1;
}

.count-string-result .details-table::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.count-string-result .details-table::-webkit-scrollbar-thumb:active {
  background: #999999;
}

.count-string-result .more-indicator {
  text-align: center;
  margin-top: 1rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.count-string-result h6 {
  color: #374151;
  margin: 0.5rem 0;
  font-size: 1rem;
  font-weight: 600;
}

.count-integer-result {
  margin-bottom: 1.5rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.count-integer-result .result-basic {
  margin-bottom: 1rem;
}

.count-integer-result .parameter-info {
  margin-top: 0.5rem;
  font-size: 0.9rem;
  color: #6b7280;
}

.count-integer-result .parameter-info p {
  margin: 0.25rem 0;
}

.count-integer-result .parameter-info strong {
  color: #374151;
}

.count-integer-result .filter-condition {
  margin-top: 0.5rem;
  padding: 0.5rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.count-integer-result .filter-formula {
  margin: 0.5rem 0;
  font-size: 1rem;
  padding: 0.5rem;
  background: #ffffff;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  overflow-x: auto;
}

.count-integer-result .result-statistics {
  margin-bottom: 1rem;
}

.count-integer-result .statistics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
  margin-top: 0.5rem;
}

.count-integer-result .stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.count-integer-result .stat-label {
  font-weight: 500;
  color: #6b7280;
}

.count-integer-result .stat-value {
  font-weight: 600;
  color: #059669;
}

.count-integer-result .result-details {
  margin-top: 1rem;
}

.count-integer-result .integer-processing-list {
  margin-top: 0.5rem;
  padding: 0.5rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 0.9rem;
  max-height: 400px;
  overflow-y: scroll;
  overflow-x: hidden;
}

/* 确保滚动条样式正确显示 */
.count-integer-result .integer-processing-list::-webkit-scrollbar {
  width: 8px;
}

.count-integer-result .integer-processing-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.count-integer-result .integer-processing-list::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.count-integer-result .integer-processing-list::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}

.count-integer-result .integer-item {
  padding: 0.25rem 0;
  line-height: 1.4;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 确保鼠标悬停时显示工具提示 */
.count-integer-result .integer-item:hover {
  background: #f0f9ff;
  border-radius: 2px;
  padding: 0.25rem 0.25rem;
}

.count-integer-result .integer-formula {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 0.85rem;
}

.count-integer-result .ellipsis-indicator {
  text-align: center;
  margin-top: 1rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.count-integer-result .ellipsis-formula {
  margin-bottom: 0.25rem;
}

.count-integer-result .ellipsis-text {
  font-size: 0.8rem;
  color: #6b7280;
}

.count-integer-result h6 {
  color: #374151;
  margin: 0.5rem 0;
  font-size: 1rem;
  font-weight: 600;
}

/* 不定方程非负整数解计数结果样式 */
.count-equation-solver-result {
  margin-bottom: 1.5rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.count-equation-solver-result .result-basic {
  margin-bottom: 1rem;
}

.count-equation-solver-result .filter-condition {
  margin-bottom: 1rem;
}

.count-equation-solver-result .result-formula,
.count-equation-solver-result .filter-formula {
  margin: 0.5rem 0;
  padding: 0.5rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  overflow-x: auto;
}

.count-equation-solver-result .result-statistics {
  margin-bottom: 1rem;
}

.count-equation-solver-result .statistics-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
  margin: 1rem 0;
}

.count-equation-solver-result .stat-item {
  display: flex;
  align-items: center;
  padding: 0.5rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.count-equation-solver-result .stat-label {
  font-weight: 600;
  color: #6b7280;
  margin-right: 0.5rem;
}

.count-equation-solver-result .stat-value {
  font-weight: bold;
  color: #1f2937;
  font-size: 1.1rem;
}

.count-equation-solver-result .combination-formula {
  margin-top: 1rem;
  padding: 0.75rem;
  background: #fff3cd;
  border: 1px solid #ffeaa7;
  border-radius: 6px;
}

.count-equation-solver-result .combination-formula-content {
  font-size: 1.05rem;
  font-weight: 500;
}

.count-equation-solver-result .solutions-display {
  margin-top: 1rem;
}

.count-equation-solver-result .solutions-list {
  margin-top: 1rem;
  max-height: 300px;
  overflow-y: auto;
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  padding: 0.75rem;
}

.count-equation-solver-result .solution-item {
  margin-bottom: 0.75rem;
  padding: 0.75rem;
  border-radius: 8px;
  background: white;
  border: 1px solid #e5e7eb;
  transition: all 0.2s ease;
}

.count-equation-solver-result .solution-item:last-child {
  margin-bottom: 0;
}

.count-equation-solver-result .solution-item.accepted {
  border-color: #10b981;
  background: #ecfdf5;
  box-shadow: 0 1px 3px rgba(16, 185, 129, 0.1);
}

.count-equation-solver-result .solution-item.not-accepted {
  border-color: #f59e0b;
  background: #fffbeb;
  opacity: 0.8;
}

.count-equation-solver-result .solution-item:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.count-equation-solver-result .solution-header {
  display: flex;
  align-items: center;
  margin-bottom: 0.5rem;
  flex-wrap: wrap;
  gap: 0.25rem;
}

.count-equation-solver-result .solution-number {
  font-weight: 600;
  color: #374151;
  font-size: 0.85rem;
}

.count-equation-solver-result .solution-status {
  color: #6b7280;
  font-size: 0.85rem;
  font-weight: 500;
}

.count-equation-solver-result .solution-content {
  display: flex;
  align-items: center;
}

.count-equation-solver-result .solution-formula {
  font-size: 0.9rem;
  overflow-x: auto;
  padding: 0.25rem 0.5rem;
  background: #f9fafb;
  border-radius: 4px;
  border: 1px solid #e5e7eb;
  flex: 1;
}

/* 滚动条样式 */
.count-equation-solver-result .solutions-list::-webkit-scrollbar {
  width: 6px;
}

.count-equation-solver-result .solutions-list::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 3px;
}

.count-equation-solver-result .solutions-list::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}

.count-equation-solver-result .solutions-list::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

.count-equation-solver-result h5 {
  color: #374151;
  margin: 0 0 1rem 0;
  font-size: 1.1rem;
  font-weight: 600;
}

.count-equation-solver-result h6 {
  color: #374151;
  margin: 0.5rem 0;
  font-size: 1rem;
  font-weight: 600;
}

/* 不同性质的函数计数结果样式 */
.count-function-result {
  margin-bottom: 1.5rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.count-function-result .result-basic {
  margin-bottom: 1rem;
}

.count-function-result .set-info {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.count-function-result .result-statistics {
  margin-bottom: 1rem;
}

.count-function-result .statistics-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
  margin: 1rem 0;
}

.count-function-result .stat-item {
  display: flex;
  align-items: center;
  padding: 0.5rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.count-function-result .stat-label {
  font-weight: 600;
  color: #6b7280;
  margin-right: 0.5rem;
}

.count-function-result .stat-value {
  font-weight: bold;
  color: #1f2937;
  font-size: 1.1rem;
}

.count-function-result .stat-value.bijection {
  color: #8b5cf6; /* 紫色 */
}

.count-function-result .stat-value.surjection {
  color: #10b981; /* 绿色 */
}

.count-function-result .stat-value.injection {
  color: #f59e0b; /* 橙色 */
}

.count-function-result .count-formula {
  margin-top: 1rem;
  padding: 0.75rem;
  background: #fff3cd;
  border: 1px solid #ffeaa7;
  border-radius: 6px;
}

.count-function-result .functions-display {
  margin-top: 1rem;
}

.count-function-result .functions-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  max-height: 400px;
  overflow-y: auto;
  padding: 0.5rem;
  background: white;
  border-radius: 6px;
  border: 1px solid #dee2e6;
}

.count-function-result .functions-list::-webkit-scrollbar {
  width: 6px;
}

.count-function-result .functions-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.count-function-result .functions-list::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.count-function-result .functions-list::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.count-function-result .function-item {
  padding: 0.75rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.count-function-result .function-item:hover {
  background: #e3f2fd;
  border-color: #2196f3;
}

.count-function-result .function-label {
  font-weight: bold;
  color: #2196f3;
  margin-bottom: 0.5rem;
  display: block;
}

.count-function-result .function-formula {
  padding: 0.5rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.count-function-result .more-functions {
  text-align: center;
  margin-top: 1rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 4px;
}

.count-function-result .ellipsis-text {
  color: #6b7280;
  font-style: italic;
  font-size: 0.9rem;
}

.count-function-result h5 {
  color: #374151;
  margin: 0 0 1rem 0;
  font-size: 1.2rem;
  font-weight: 600;
}

.count-function-result h6 {
  color: #374151;
  margin: 0.5rem 0;
  font-size: 1rem;
  font-weight: 600;
}

/* 组合生成结果样式 */
.gen-combination-result {
  margin: 1rem 0;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.gen-combination-result .result-basic {
  margin: 1rem 0;
}

.gen-combination-result .parameter-info {
  margin: 1rem 0;
  padding: 0.5rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.gen-combination-result .parameter-info p {
  margin: 0.5rem 0;
  font-size: 0.9rem;
}

.gen-combination-result .combinations-result {
  margin: 1rem 0;
}

.gen-combination-result .combinations-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(80px, 1fr));
  gap: 0.5rem;
  align-items: center;
}

.gen-combination-result .combination-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.gen-combination-result .combination-formula {
  font-size: 1rem;
  padding: 0.25rem 0.5rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
}

.gen-combination-result .arrow {
  font-size: 1.2rem;
  color: #6b7280;
  font-weight: bold;
}

.gen-combination-result .continuation {
  font-size: 1.2rem;
  color: #6b7280;
  font-weight: bold;
  padding: 0.5rem;
}

.gen-combination-result .warning-section {
  margin: 1.5rem 0;
}

.gen-combination-result .statistics-section {
  margin: 1.5rem 0;
  padding: 1rem;
  background: white;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.gen-combination-result .statistics-formula {
  margin: 0.5rem 0;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.gen-combination-result .statistics-section p {
  margin: 0.5rem 0;
  font-size: 0.9rem;
  line-height: 1.5;
}

/* 排列生成结果样式 */
.gen-permutation-result {
  margin-bottom: 1.5rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.gen-permutation-result .result-basic {
  margin-bottom: 1rem;
}

.gen-permutation-result .parameter-info {
  margin-top: 1rem;
  padding: 1rem;
  background: white;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.gen-permutation-result .parameter-info p {
  margin: 0.5rem 0;
  font-size: 0.9rem;
}

.gen-permutation-result .permutations-result {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.gen-permutation-result .permutations-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  align-items: center;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 4px;
  overflow-x: auto;
}

.gen-permutation-result .permutation-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.gen-permutation-result .permutation-formula {
  font-size: 1rem;
  padding: 0.25rem 0.5rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
}

.gen-permutation-result .arrow {
  font-size: 1.2rem;
  color: #6b7280;
  font-weight: bold;
}

.gen-permutation-result .continuation {
  font-size: 1.2rem;
  color: #6b7280;
  font-weight: bold;
  padding: 0.5rem;
}

.gen-permutation-result .warning-section {
  margin: 1.5rem 0;
}

.gen-permutation-result .statistics-section {
  margin: 1.5rem 0;
  padding: 1rem;
  background: white;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.gen-permutation-result .statistics-section p {
  margin: 0.5rem 0;
  font-size: 0.9rem;
  line-height: 1.5;
}

/* 允许重复组合的生成结果样式 */
.gen-rep-comb-result {
  margin: 1rem 0;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.gen-rep-comb-result .result-basic {
  margin: 1rem 0;
}

.gen-rep-comb-result .parameter-info {
  margin: 1rem 0;
  padding: 0.5rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.gen-rep-comb-result .parameter-info p {
  margin: 0.5rem 0;
  font-size: 0.9rem;
}

.gen-rep-comb-result .combinations-result {
  margin: 1rem 0;
}

.gen-rep-comb-result .combinations-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(80px, 1fr));
  gap: 0.5rem;
  align-items: center;
}

.gen-rep-comb-result .combination-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.gen-rep-comb-result .combination-formula {
  font-size: 1rem;
  padding: 0.25rem 0.5rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #dee2e6;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
}

.gen-rep-comb-result .arrow {
  font-size: 1.2rem;
  color: #6b7280;
  font-weight: bold;
}

.gen-rep-comb-result .continuation {
  font-size: 1.2rem;
  color: #6b7280;
  font-weight: bold;
  padding: 0.5rem;
}

.gen-rep-comb-result .warning-section {
  margin: 1.5rem 0;
}

.gen-rep-comb-result .statistics-section {
  margin: 1.5rem 0;
  padding: 1rem;
  background: white;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.gen-rep-comb-result .statistics-section p {
  margin: 0.5rem 0;
  font-size: 0.9rem;
  line-height: 1.5;
}

/* 运算性质判断结果样式 */
.operation-property-result {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  margin-top: 1rem;
}

.operation-basic-info {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.basic-formula {
  margin: 0.5rem 0;
  font-size: 1.05em;
  overflow-x: auto;
}

.operation-tables {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.operator-table {
  margin: 0.5rem 0;
}

.operation-formula {
  margin: 0.5rem 0;
  font-size: 1.05em;
  overflow-x: auto;
}

.operator-properties,
.relation-properties {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.property-item,
.relation-item {
  margin: 0.5rem 0;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.property-header,
.relation-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
  font-size: 0.95em;
}

.property-reason,
.relation-reason {
  font-size: 0.85rem;
  color: #6b7280;
  margin-bottom: 0.25rem;
  line-height: 1.4;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 0.25rem;
}

.property-reason .math-renderer,
.relation-reason .math-renderer {
  display: inline-flex;
  align-items: center;
}

.property-details {
  font-size: 0.85rem;
  color: #374151;
  font-style: italic;
  margin-top: 0.25rem;
}

/* 群U(m)分析结果样式 */
.group-um-result {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  margin-top: 1rem;
}

/* 置换群分析结果样式 */
.group-perm-result {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  margin-top: 1rem;
}

.group-um-result .result-basic {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.group-um-result .cycle-group-result,
.group-um-result .power-result,
.group-um-result .order-result,
.group-um-result .subgroup-result {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.group-um-result .cycle-group-info p {
  margin: 0.5rem 0;
  color: #374151;
  font-weight: 500;
}

.group-um-result .power-item {
  margin: 0.5rem 0;
}

.group-um-result .power-item-container {
  background: #f8f9fa;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  margin: 0.5rem 0;
  overflow-x: auto;
}

.group-um-result .power-formula {
  margin: 0;
  font-size: 1.05em;
  min-width: max-content;
}

.group-um-result .power-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.3rem;
  margin: 0.3rem 0;
}

.group-um-result .power-formula-inline {
  padding: 0.2rem 0.4rem;
  background: #f8f9fa;
  border-radius: 3px;
  border: 1px solid #e9ecef;
  font-size: 0.85em;
}

.group-um-result .order-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin: 0.5rem 0;
}

.group-um-result .order-formula {
  padding: 0.25rem 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.group-um-result .subgroup-item {
  margin: 1rem 0;
  padding: 0.75rem;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.group-um-result .subgroup-info {
  margin-bottom: 0.75rem;
}

.group-um-result .subgroup-info p {
  margin: 0.5rem 0;
  color: #374151;
  line-height: 1.5;
}

.group-um-result .subgroup-operator {
  margin: 0.75rem 0;
}

.group-um-result .operator-table-result {
  margin: 1rem 0;
}

.group-um-result .operator-table-container {
  margin: 0.5rem 0;
  overflow-x: auto;
}

.group-um-result .operator-formula {
  margin: 0.5rem 0;
  font-size: 1em;
  overflow-x: auto;
  min-width: max-content;
  background: #f8f9fa;
  padding: 0.5rem;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.group-um-result .coset-info {
  margin-top: 0.75rem;
  padding: 0.5rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.group-um-result .coset-info p {
  margin: 0.5rem 0;
  color: #374151;
  font-weight: 500;
}

.group-perm-result .result-basic,
.group-perm-result .element-operator-result,
.group-perm-result .cycle-group-result,
.group-perm-result .power-result,
.group-perm-result .order-result,
.group-perm-result .subgroup-result,
.group-perm-result .coset-result,
.group-perm-result .normal-subgroup-result {
  margin: 1rem 0;
  background: white;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.group-perm-result .cycle-group-info p,
.group-perm-result .normal-subgroup-info p {
  margin: 0.5rem 0;
  color: #374151;
  font-weight: 500;
}

.group-perm-result .power-item {
  margin: 0.5rem 0;
}

.group-perm-result .power-item-container {
  background: #f8f9fa;
  padding: 0.75rem;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  margin: 0.5rem 0;
  overflow-x: auto;
}

.group-perm-result .power-formula {
  margin: 0;
  font-size: 1.05em;
  min-width: max-content;
}

.group-perm-result .order-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin: 0.5rem 0;
}

.group-perm-result .order-formula,
.group-perm-result .coset-formula {
  padding: 0.25rem 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.group-perm-result .subgroup-item,
.group-perm-result .coset-item,
.group-perm-result .normal-subgroup-item {
  margin: 1rem 0;
  padding: 0.75rem;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.group-perm-result .subgroup-info,
.group-perm-result .coset-info,
.group-perm-result .normal-subgroup-info {
  margin-bottom: 0.75rem;
}

.group-perm-result .subgroup-info p,
.group-perm-result .coset-info p {
  margin: 0.5rem 0;
  color: #374151;
  line-height: 1.5;
}

.group-perm-result .subgroup-operator,
.group-perm-result .quotient-group-table {
  margin: 0.75rem 0;
  overflow-x: auto;
}

.group-perm-result .operator-formula {
  margin: 0.5rem 0;
  font-size: 1em;
  overflow-x: auto;
}

.group-perm-result .coset-section {
  margin: 0.75rem 0;
}

.group-perm-result .coset-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin: 0.5rem 0;
}

.group-um-result .coset-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 0.25rem;
  margin-top: 0.5rem;
}

.group-um-result .coset-item {
  padding: 0.25rem 0.5rem;
  background: #e9ecef;
  border-radius: 4px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 0.85rem;
  color: #374151;
}

.group-um-result .coset-formula {
  padding: 0.25rem 0.5rem;
  background: #e9ecef;
  border-radius: 4px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 0.85rem;
  color: #374151;
}

.group-um-result h6 {
  color: #374151;
  margin: 0.75rem 0 0.5rem 0;
  font-size: 1rem;
  font-weight: 600;
}

.group-um-result strong {
  color: #374151;
  font-weight: 600;
}

/* 格判断结果样式 */
.lattice-judge-result .result-basic {
  margin-bottom: 1rem;
}

.lattice-judge-result .hasse-diagram-result {
  margin: 1rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.lattice-judge-result .diagram-container {
  text-align: center;
  margin: 1rem 0;
}

.lattice-judge-result .diagram-image {
  max-width: 100%;
  height: auto;
  border: 1px solid #e9ecef;
  border-radius: 4px;
}

.lattice-judge-result .partial-order-result,
.lattice-judge-result .lattice-result,
.lattice-judge-result .distributive-result,
.lattice-judge-result .bounded-result,
.lattice-judge-result .complemented-result,
.lattice-judge-result .boolean-result {
  margin: 1rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.lattice-judge-result .reason-text {
  margin: 0.5rem 0;
  color: #6b7280;
  font-size: 0.9rem;
}

.lattice-judge-result .counterexample-text {
  margin: 0.5rem 0;
  color: #dc2626;
  font-size: 0.9rem;
}

.lattice-judge-result .counterexample-formula,
.lattice-judge-result .reason-formula {
  display: inline-block;
  margin: 0.2rem 0;
  padding: 0.2rem 0.5rem;
  background: #fef2f2;
  border-radius: 4px;
  border: 1px solid #fecaca;
}

.lattice-judge-result .operator-tables-result {
  margin: 1rem 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.lattice-judge-result .operator-table {
  margin: 1rem 0;
}

.lattice-judge-result .operator-formula {
  margin: 1rem 0;
  font-size: 1.1rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  overflow-x: auto;
}

.lattice-judge-result .bounded-elements {
  margin: 0.5rem 0;
  font-size: 0.9rem;
}

.lattice-judge-result .complements-list {
  margin-top: 1rem;
}

.lattice-judge-result .complement-item {
  margin: 0.5rem 0;
  font-size: 0.9rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.lattice-judge-result h6 {
  color: #374151;
  margin: 1rem 0 0.5rem 0;
  font-size: 1rem;
  font-weight: 600;
}

/* 布尔代数结果样式 */
.bool-algebra-result {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 1.5rem;
  margin-bottom: 1rem;
}

.bool-algebra-result .lattice-basic-info {
  margin-bottom: 1.5rem;
}

.bool-algebra-result .boolean-judgment {
  margin-top: 1rem;
  text-align: center;
}

.bool-algebra-result .extreme-elements {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 6px;
  border: 1px solid #dee2e6;
}

.bool-algebra-result .elements-display {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.bool-algebra-result .element-formula {
  font-size: 1.1rem;
  margin: 0 0.25rem;
}

.bool-algebra-result .hasse-diagram-display {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 6px;
  border: 1px solid #dee2e6;
}

.bool-algebra-result .diagram-container {
  text-align: center;
}

.bool-algebra-result .hasse-image {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.bool-algebra-result .operation-tables-display {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 6px;
  border: 1px solid #dee2e6;
}

.bool-algebra-result .operation-table {
  margin: 1rem 0;
}

.bool-algebra-result .table-formula {
  font-size: 1rem;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  overflow-x: auto;
}

.bool-algebra-result .complements-display {
  margin: 1.5rem 0;
  background: white;
  padding: 1rem;
  border-radius: 6px;
  border: 1px solid #dee2e6;
}

.bool-algebra-result .complements-list {
  margin-top: 1rem;
}

.bool-algebra-result .complement-item {
  margin: 0.75rem 0;
  padding: 0.75rem;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.bool-algebra-result .element-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-wrap: wrap;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.bool-algebra-result .complement-formula {
  margin-top: 0.5rem;
  padding: 0.5rem;
  background: white;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.bool-algebra-result h5 {
  color: #374151;
  margin: 1rem 0 0.75rem 0;
  font-size: 1.1rem;
  font-weight: 600;
}

.bool-algebra-result h6 {
  color: #6b7280;
  margin: 0.75rem 0 0.5rem 0;
  font-size: 1rem;
  font-weight: 500;
}
</style>
