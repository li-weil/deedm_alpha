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
          <div class="result-formula">
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

          </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'
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

// 处理清空按钮点击
const handleClear = () => {
  emit('clear')
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
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.result-formula :deep(.math-renderer) {
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

/* 树遍历结果样式 */
.tree-traversal-result {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  margin-top: 1rem;
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

.visualization-hint {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
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
</style>
