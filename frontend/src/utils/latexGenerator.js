// LaTeX代码生成工具函数

// 生成LaTeX代码（公式和真值表），用于右侧面板展示
export const generateLaTeXCode = (result) => {
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

    // 主范式结果
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

  // 处理推理有效性论证检查结果
  if (result.type === 'reason-argument-check') {
    latexCode += `\\begin{array}{c}\n\\text{推理有效性论证检查结果:}\n\\end{array}\n\n`

    // 显示推理关系
    if (result.latexString) {
      latexCode += `\\begin{array}{c}\n\\text{验证推理关系:} ${result.latexString}\n\\end{array}\n\n`
    }

    // 检查步骤
    if (result.steps && result.steps.length > 0) {
      latexCode += `\\begin{array}{c}\n\\text{检查推理步骤${result.stepNumber}: 验证以下推理过程}\n\\end{array}\n\n`

      result.steps.forEach((step) => {
        latexCode += `\\begin{array}{c}\n\\quad(${step.serialNo})\\quad${step.formula}`
        if (step.prevSN && step.ruleName) {
          latexCode += ` // ${step.prevSN}${step.ruleName}`
        }
        latexCode += `\\end{array}\n\n`
      })
    }

    // 检查过程
    if (result.checkSteps && result.checkSteps.length > 0) {
      latexCode += `\\begin{array}{c}\n\\text{检查过程:}\n\\end{array}\n\n`

      result.checkSteps.forEach((step) => {
        latexCode += `\\begin{array}{c}\n\\quad检验步骤(${step.serialNo})${step.checkType}${step.formula}\n\\end{array}\n\n`
      })
    }

    // 检查结果
    if (result.valid) {
      latexCode += `\\begin{array}{c}\n\\text{✓ 检查通过: 通过真值表检验，上述推理证明过程没有错误。}\n\\end{array}\n\n`
    } else {
      latexCode += `\\begin{array}{c}\n\\text{✗ 检查失败: ${result.errorMessage || '推理证明过程存在错误'}}\n\\end{array}\n\n`

      // 反例信息
      if (result.counterExample && result.checkingFormula) {
        latexCode += `\\begin{array}{c}\n\\text{反例: 在真值赋值 ${result.counterExample} 下，以下公式不是重言式:}\n\\end{array}\n\n`
        latexCode += `\\begin{array}{c}\n${result.checkingFormula}\n\\end{array}\n\n`
      }
    }
  }

  // 处理图遍历结果
  if (result.type === 'graph-travel') {
    latexCode += `\\begin{array}{c}\n\\text{图遍历分析结果:}\n\\end{array}\n\n`

    // 显示图形可视化信息
    if (result.graphImageUrl) {
      latexCode += `\\begin{array}{c}\n\\text{图形可视化已生成 (图片路径: ${result.graphImageUrl})}\n\\end{array}\n\n`
    }

    // 显示度数计算结果
    if (result.nodeDegrees && result.nodeDegrees.length > 0) {
      latexCode += `\\begin{array}{c}\n\\text{顶点度数计算结果:}\n\\end{array}\n\n`
      result.nodeDegrees.forEach(node => {
        latexCode += `\\begin{array}{c}\nd(${node.nodeLabel}) = ${node.degree}\n\\end{array}\n\n`

        if (result.directed) {
          latexCode += `\\begin{array}{c}\nd^+(${node.nodeLabel}) = ${node.outDegree} \\quad\\quad d^-(${node.nodeLabel}) = ${node.inDegree}\n\\end{array}\n\n`
        }
      })
    }

    // 显示邻接矩阵
    if (result.adjacencyMatrix) {
      latexCode += `\\begin{array}{c}\n\\text{邻接矩阵 A:}\n\\end{array}\n\n`
      latexCode += result.adjacencyMatrix + '\n\n'
    }

    // 显示关联矩阵
    if (result.incidenceMatrix) {
      latexCode += `\\begin{array}{c}\n\\text{关联矩阵 I:}\n\\end{array}\n\n`
      latexCode += result.incidenceMatrix + '\n\n'
    }

    // 显示路径矩阵
    if (result.pathMatrices && result.pathMatrices.length > 0) {
      latexCode += `\\begin{array}{c}\n\\text{邻接矩阵的幂计算结果:}\n\\end{array}\n\n`
      result.pathMatrices.forEach(matrix => {
        latexCode += `\\begin{array}{c}\n${matrix}\n\\end{array}\n\n`
      })
    }

    // 显示DFS遍历结果
    if (result.dfsResult) {
      latexCode += `\\begin{array}{c}\n\\text{深度优先遍历(DFS)结果:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n遍历顺序: ${result.dfsResult.traversalOrder}\n\\end{array}\n\n`

      if (result.dfsResult.steps && result.dfsResult.steps.length > 0) {
        latexCode += `\\begin{array}{c}\n\\text{DFS遍历详细步骤:}\n\\end{array}\n\n`
        result.dfsResult.steps.forEach(step => {
          latexCode += `\\begin{array}{c}\n\\quad步骤${step.step}: T = ${step.visitedNodes} \\quad S = ${step.auxNodes}\n\\end{array}\n\n`
        })
      }
    }

    // 显示BFS遍历结果
    if (result.bfsResult) {
      latexCode += `\\begin{array}{c}\n\\text{广度优先遍历(BFS)结果:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n遍历顺序: ${result.bfsResult.traversalOrder}\n\\end{array}\n\n`

      if (result.bfsResult.steps && result.bfsResult.steps.length > 0) {
        latexCode += `\\begin{array}{c}\n\\text{BFS遍历详细步骤:}\n\\end{array}\n\n`
        result.bfsResult.steps.forEach(step => {
          latexCode += `\\begin{array}{c}\n\\quad步骤${step.step}: T = ${step.visitedNodes} \\quad Q = ${step.auxNodes}\n\\end{array}\n\n`
        })
      }
    }


  }

  // 处理特殊图展示结果
  if (result.type === 'special-graph') {
    latexCode += `\\begin{array}{c}\n\\text{特殊图展示结果:}\n\\end{array}\n\n`


    // 显示各个特殊图的结果
    if (result.graphResults && result.graphResults.length > 0) {
      result.graphResults.forEach(graph => {
        latexCode += `\\begin{array}{c}\n\\text{${graph.description}:}\n\\end{array}\n\n`
        latexCode += `\\begin{array}{c}\n${graph.name}\n\\end{array}\n\n`

        if (graph.generated && graph.imageUrl) {
          latexCode += `\\begin{array}{c}\n\\text{图形可视化已生成 (图片路径: ${graph.imageUrl})}\n\\end{array}\n\n`
        } else {
          latexCode += `\\begin{array}{c}\n\\text{${graph.description}：无法生成}\n\\end{array}\n\n`
        }
      })
    }
  }

  // 处理树遍历结果
  if (result.type === 'tree-travel') {
    latexCode += `\\begin{array}{c}\n\\text{树遍历分析结果:}\n\\end{array}\n\n`
    
    // 显示根节点信息
    if (result.rootNode) {
      latexCode += `\\begin{array}{c}\n\\text{根节点:} ${result.rootNode}\n\\end{array}\n\n`
    }

   // 显示树形可视化信息
    if (result.graphImageUrl) {
      latexCode += `\\begin{array}{c}\n\\text{树形可视化已生成 (图片路径: ${result.graphImageUrl})}\n\\end{array}\n\n`
    }    

    // 显示邻接矩阵
    if (result.adjacencyMatrix) {
      latexCode += `\\begin{array}{c}\n\\text{邻接矩阵 A:}\n\\end{array}\n\n`
      latexCode += result.adjacencyMatrix + '\n\n'
    }

    // 显示关联矩阵
    if (result.incidenceMatrix) {
      latexCode += `\\begin{array}{c}\n\\text{关联矩阵 I:}\n\\end{array}\n\n`
      latexCode += result.incidenceMatrix + '\n\n'
    }

    // 显示前序遍历结果
    if (result.preorderResult) {
      latexCode += `\\begin{array}{c}\n\\text{前序遍历 (Preorder) 结果:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n遍历顺序: ${result.preorderResult.traversalOrder}\n\\end{array}\n\n`
    }

    // 显示中序遍历结果
    if (result.inorderResult) {
      latexCode += `\\begin{array}{c}\n\\text{中序遍历 (Inorder) 结果:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n遍历顺序: ${result.inorderResult.traversalOrder}\n\\end{array}\n\n`
    }

    // 显示后序遍历结果
    if (result.postorderResult) {
      latexCode += `\\begin{array}{c}\n\\text{后序遍历 (Postorder) 结果:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n遍历顺序: ${result.postorderResult.traversalOrder}\n\\end{array}\n\n`
    }

 
  }

  // 处理最短路径计算结果
  if (result.type === 'shortest-path') {
    latexCode += `\\begin{array}{c}\n\\text{带权图最短路径计算结果:}\n\\end{array}\n\n`

    // 显示起始节点
    if (result.startNode) {
      latexCode += `\\begin{array}{c}\n\\text{起始节点:} ${result.startNode}\n\\end{array}\n\n`
    }

    // 显示原图可视化信息
    if (result.graphImageUrl) {
      latexCode += `\\begin{array}{c}\n\\text{图位置图示已生成 (图片路径: ${result.graphImageUrl})}\n\\end{array}\n\n`
    }

    // 显示邻接矩阵（带权矩阵）
    if (result.adjacencyMatrix) {
      latexCode += `\\begin{array}{c}\n\\text{带权图的矩阵表示 D:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n${result.adjacencyMatrix}\n\\end{array}\n\n`
    }

    // 显示Dijkstra算法详细过程
    if (result.dijkstraDetails) {
      latexCode += `\\begin{array}{c}\n\\text{Dijkstra算法详细过程:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n${result.dijkstraDetails}\n\\end{array}\n\n`
    }

    // 显示最短路径结果
    if (result.shortestPaths && result.shortestPaths.length > 0) {
      latexCode += `\\begin{array}{c}\n\\text{得到的最短路径结果距离如下:}\n\\end{array}\n\n`
      result.shortestPaths.forEach(path => {
        latexCode += `\\begin{array}{c}\n${path.formula}\n\\end{array}\n\n`
      })
    }



    // 显示最短路径图可视化信息
    if (result.pathGraphImageUrl) {
      latexCode += `\\begin{array}{c}\n\\text{最短路径图已生成 (图片路径: ${result.pathGraphImageUrl})}\n\\end{array}\n\n`
    }
  }

  // 处理最小生成树计算结果
  if (result.type === 'spanning-tree') {
    latexCode += `\\begin{array}{c}\n\\text{带权图最小生成树计算结果:}\n\\end{array}\n\n`

    //显示原图图像
    if (result.graphImageUrl) {
        latexCode += `\\begin{array}{c}\n\\text{原图图像已生成 (图片路径: ${result.graphImageUrl})}\n\\end{array}\n\n`
    }

    // 显示带权图的距离矩阵
    if (result.weightMatrix) {
      latexCode += `\\begin{array}{c}\n\\text{带权图的距离矩阵:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n${result.weightMatrix}\n\\end{array}\n\n`
    }

    // 显示Kruskal算法结果
    if (result.kruskalResult) {
      latexCode += `\\begin{array}{c}\n\\text{Kruskal算法结果:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n\\text{使用Kruskal算法发现最小生成树（森林），总权重 = ${result.kruskalResult.totalWeight}}\n\\end{array}\n\n`

      if (result.kruskalResult.edges) {
        latexCode += `\\begin{array}{c}\n\\text{生成树的边集:}\n\\end{array}\n\n`
        latexCode += `\\begin{array}{c}\n${result.kruskalResult.edges}\n\\end{array}\n\n`
      }

      if (result.kruskalResult.steps) {
        latexCode += `\\begin{array}{c}\n\\text{算法计算过程:}\n\\end{array}\n\n`
        latexCode += `\\begin{array}{c}\n${result.kruskalResult.steps}\n\\end{array}\n\n`
      }

      if (result.kruskalTreeImageUrl) {
        latexCode += `\\begin{array}{c}\n\\text{Kruskal最小生成树图形已生成 (图片路径: ${result.kruskalTreeImageUrl})}\n\\end{array}\n\n`
      }
    }

    // 显示Prim算法结果
    if (result.primResult) {
      latexCode += `\\begin{array}{c}\n\\text{Prim算法结果:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n\\text{使用Prim算法发现最小生成树（森林），总权重 = ${result.primResult.totalWeight}}\n\\end{array}\n\n`

      if (result.primResult.edges) {
        latexCode += `\\begin{array}{c}\n\\text{生成树的边集:}\n\\end{array}\n\n`
        latexCode += `\\begin{array}{c}\n${result.primResult.edges}\n\\end{array}\n\n`
      }

      if (result.primResult.steps) {
        latexCode += `\\begin{array}{c}\n\\text{算法计算过程:}\n\\end{array}\n\n`
        latexCode += `\\begin{array}{c}\n${result.primResult.steps}\n\\end{array}\n\n`
      }

      if (result.primTreeImageUrl) {
        latexCode += `\\begin{array}{c}\n\\text{Prim最小生成树图形已生成 (图片路径: ${result.primTreeImageUrl})}\n\\end{array}\n\n`
      }
    }

  }

  // 处理Huffman树构造结果
  if (result.type === 'huffmanTree') {
    latexCode += `\\begin{array}{c}\n\\text{哈夫曼树构造结果:}\n\\end{array}\n\n`

    // 显示算法步骤
    if (result.steps && result.steps.length > 0) {
      latexCode += `\\begin{array}{c}\n\\text{算法构造步骤:}\n\\end{array}\n\n`

      result.steps.forEach(step => {
        latexCode += `\\begin{array}{c}\n\\text{${step.prompt}}\n\\end{array}\n\n`
        latexCode += `\\begin{array}{c}\n\\text{步骤${step.step}:} ${step.forestLaTeX}\n\\end{array}\n\n`

        if (step.forestImageUrl) {
          latexCode += `\\begin{array}{c}\n\\text{步骤${step.step}森林图已生成 (图片路径: ${step.forestImageUrl})}\n\\end{array}\n\n`
        }
      })
    }

    // 显示最终Huffman树
    if (result.treeImageUrl) {
      latexCode += `\\begin{array}{c}\n\\text{最终构造结果:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n\\text{最终Huffman树图已生成 (图片路径: ${result.treeImageUrl})}\n\\end{array}\n\n`
    }

    // 显示总权值计算
    if (result.totalWeightLaTeX) {
      latexCode += `\\begin{array}{c}\n\\text{总权值计算:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n${result.totalWeightLaTeX} = ${result.totalWeight}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n\\text{构造得到的Huffman树总权值是: ${result.totalWeight}}\n\\end{array}\n\n`
    }

    // 显示叶节点编码
    if (result.leafCodes && Object.keys(result.leafCodes).length > 0) {
      latexCode += `\\begin{array}{c}\n\\text{带权叶节点的编码如下:}\n\\end{array}\n\n`

      Object.entries(result.leafCodes).forEach(([label, code], index) => {
        if (index % 4 === 0) {
          if (index > 0) {
            latexCode += `\\\\\n`
          }
          latexCode += `\\begin{array}{cccc}\n`
        }

        latexCode += `${label} : ${code}`

        if (index % 4 === 3 || index === Object.keys(result.leafCodes).length - 1) {
          latexCode += `\n\\end{array}\n\n`
        } else {
          latexCode += ` & `
        }
      })
    }
  }

  // 处理集合运算结果
  if (result.type === 'set-operation') {
    latexCode += `\\begin{array}{c}\n\\text{集合运算分析结果:}\n\\end{array}\n\n`

    // 显示输入集合信息
    latexCode += `\\begin{array}{c}\n\\text{输入集合:}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n${result.formula}\n\\end{array}\n\n`

    // 显示交集结果
    if (result.intersectionResult) {
      latexCode += `\\begin{array}{c}\n\\text{交集运算结果:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n${result.intersectionResult}\n\\end{array}\n\n`
    }

    // 显示并集结果
    if (result.unionResult) {
      latexCode += `\\begin{array}{c}\n\\text{并集运算结果:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n${result.unionResult}\n\\end{array}\n\n`
    }

    // 显示差集结果
    if (result.subtractResult) {
      latexCode += `\\begin{array}{c}\n\\text{差集运算结果:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n${result.subtractResult}\n\\end{array}\n\n`
    }

    // 显示补集结果
    if (result.complementAResult || result.complementBResult) {
      latexCode += `\\begin{array}{c}\n\\text{补集运算结果:}\n\\end{array}\n\n`
      if (result.complementAResult) {
        latexCode += `\\begin{array}{c}\n${result.complementAResult}\n\\end{array}\n\n`
      }
      if (result.complementBResult) {
        latexCode += `\\begin{array}{c}\n${result.complementBResult}\n\\end{array}\n\n`
      }
    }

    // 显示对称差结果
    if (result.differenceResult) {
      latexCode += `\\begin{array}{c}\n\\text{对称差运算结果:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n${result.differenceResult}\n\\end{array}\n\n`
    }

    // 显示幂集结果
    if (result.powerSetAResult && result.powerSetAResult.length > 0) {
      latexCode += `\\begin{array}{c}\n\\text{幂集运算结果:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n\\text{集合A的幂集:}\n\\wp(A) = \\{${result.powerSetAResult.join(', ')}\\}\n\\end{array}\n\n`
    }

    if (result.powerSetBResult && result.powerSetBResult.length > 0) {
      if (!result.powerSetAResult || result.powerSetAResult.length === 0) {
        latexCode += `\\begin{array}{c}\n\\text{幂集运算结果:}\n\\end{array}\n\n`
      }
      latexCode += `\\begin{array}{c}\n\\text{集合B的幂集:}\n\\wp(B) = \\{${result.powerSetBResult.join(', ')}\\}\n\\end{array}\n\n`
    }
  }

  // 处理等价关系计算结果
  if (result.type === 'equivalence-relation') {
    latexCode += `\\begin{array}{c}\n\\text{等价关系分析结果:}\n\\end{array}\n\n`

    // 显示输入信息
    latexCode += `\\begin{array}{c}\n\\text{输入信息:}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n${result.formula}\n\\end{array}\n\n`

    // 显示关系性质分析结果
    latexCode += `\\begin{array}{c}\n\\text{关系性质分析:}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n\\text{自反性:} ${result.isReflexive ? '是' : '否'}\n\\end{array}\n\n`
    if (result.reflexiveResult) {
      latexCode += `\\begin{array}{c}\n${result.reflexiveResult}\n\\end{array}\n\n`
    }
    latexCode += `\\begin{array}{c}\n\\text{对称性:} ${result.isSymmetric ? '是' : '否'}\n\\end{array}\n\n`
    if (result.symmetricResult) {
      latexCode += `\\begin{array}{c}\n${result.symmetricResult}\n\\end{array}\n\n`
    }
    latexCode += `\\begin{array}{c}\n\\text{传递性:} ${result.isTransitive ? '是' : '否'}\n\\end{array}\n\n`
    if (result.transitiveResult) {
      latexCode += `\\begin{array}{c}\n${result.transitiveResult}\n\\end{array}\n\n`
    }
    latexCode += `\\begin{array}{c}\n\\text{等价关系:} ${result.isEquivalenceRelation ? '是' : '否'}\n\\end{array}\n\n`

    // 显示关系矩阵
    if (result.relationMatrix) {
      latexCode += `\\begin{array}{c}\n\\text{关系矩阵:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n${result.relationMatrix}\n\\end{array}\n\n`
    }

    // 显示等价关系闭包
    if (result.equivalenceClosure) {
      latexCode += `\\begin{array}{c}\n\\text{等价关系闭包（最小等价关系）:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n${result.equivalenceClosure}\n\\end{array}\n\n`

      // 显示闭包关系矩阵
      if (result.equivalenceClosureMatrix) {
        latexCode += `\\begin{array}{c}\n\\text{闭包关系矩阵:}\n\\end{array}\n\n`
        latexCode += `\\begin{array}{c}\n${result.equivalenceClosureMatrix}\n\\end{array}\n\n`
      }
    }

    // 显示等价类
    if (result.equivalenceClasses && result.equivalenceClasses.length > 0) {
      latexCode += `\\begin{array}{c}\n\\text{等价类:}\n\\end{array}\n\n`
      result.equivalenceClasses.forEach(eqClass => {
        latexCode += `\\begin{array}{c}\n[${eqClass.element}]_R = ${eqClass.equivalenceClassLaTeX}\n\\end{array}\n\n`
      })
    }

    // 显示商集
    if (result.quotientSet) {
      latexCode += `\\begin{array}{c}\n\\text{商集:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\nA/R = ${result.quotientSet}\n\\end{array}\n\n`
    }
  }

  // 处理偏序关系计算结果
  if (result.type === 'partial-order') {
    latexCode += `\\begin{array}{c}\n\\text{偏序关系分析结果:}\n\\end{array}\n\n`

    // 显示输入信息
    latexCode += `\\begin{array}{c}\n\\text{输入信息:}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n${result.formula}\n\\end{array}\n\n`

    // 显示子集信息
    if (result.setSString) {
      latexCode += `\\begin{array}{c}\n\\text{子集S:} ${result.setSString}\n\\end{array}\n\n`
    }

    // 显示偏序关系性质验证
    latexCode += `\\begin{array}{c}\n\\text{偏序关系性质验证:}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n\\text{自反性:} ${result.isReflexive ? '是' : '否'}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n\\text{反对称性:} ${result.isAntisymmetric ? '是' : '否'}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n\\text{传递性:} ${result.isTransitive ? '是' : '否'}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n\\text{偏序关系:} ${result.isPartialOrder ? '是' : '否'}\n\\end{array}\n\n`

    // 显示关系矩阵
    if (result.relationMatrix) {
      latexCode += `\\begin{array}{c}\n\\text{关系矩阵:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\nM_R = ${result.relationMatrix}\n\\end{array}\n\n`
    }

    // 只有偏序关系才显示以下内容
    if (result.isPartialOrder) {
      // 显示元素计算结果
      if (result.minimumElements) {
        latexCode += `\\begin{array}{c}\n\\text{极小元:} ${result.minimumElements}\n\\end{array}\n\n`
      }
      if (result.maximumElements) {
        latexCode += `\\begin{array}{c}\n\\text{极大元:} ${result.maximumElements}\n\\end{array}\n\n`
      }
      if (result.leastElement) {
        latexCode += `\\begin{array}{c}\n\\text{最小元:} ${result.leastElement}\n\\end{array}\n\n`
      }
      if (result.greatestElement) {
        latexCode += `\\begin{array}{c}\n\\text{最大元:} ${result.greatestElement}\n\\end{array}\n\n`
      }

      // 显示界和确界计算结果（仅当提供了子集S时）
      if (result.setSString) {
        if (result.lowerBounds) {
          latexCode += `\\begin{array}{c}\n\\text{子集S的下界:} ${result.lowerBounds}\n\\end{array}\n\n`
        }
        if (result.upperBounds) {
          latexCode += `\\begin{array}{c}\n\\text{子集S的上界:} ${result.upperBounds}\n\\end{array}\n\n`
        }
        if (result.greatestLowerBound) {
          latexCode += `\\begin{array}{c}\n\\text{子集S的最大下界:} ${result.greatestLowerBound}\n\\end{array}\n\n`
        }
        if (result.leastUpperBound) {
          latexCode += `\\begin{array}{c}\n\\text{子集S的最小上界:} ${result.leastUpperBound}\n\\end{array}\n\n`
        }
      }
    }

    // 显示图形信息
    if (result.relationGraphUrl) {
      latexCode += `\\begin{array}{c}\n\\text{关系图已生成:} ${result.relationGraphUrl}\n\\end{array}\n\n`
    }
    if (result.hasseDiagramUrl) {
      latexCode += `\\begin{array}{c}\n\\text{哈斯图已生成:} ${result.hasseDiagramUrl}\n\\end{array}\n\n`
    }
  }

  // 处理集合表达式运算结果
  if (result.type === 'setExpressionOperation') {
    latexCode += `\\begin{array}{c}\n\\text{集合表达式运算结果:}\n\\end{array}\n\n`

    // 显示输入集合与表达式信息
    latexCode += `\\begin{array}{c}\n\\text{输入集合与表达式:}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n${result.formula}\n\\end{array}\n\n`

    // 显示表达式运算结果
    if (result.latexResult) {
      latexCode += `\\begin{array}{c}\n\\text{表达式运算结果:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n\\text{result} = ${result.latexResult}\n\\end{array}\n\n`
    }
  }

  // 处理关系运算结果
  if (result.type === 'relationOperation') {
    latexCode += `\\begin{array}{c}\n\\text{关系运算分析结果:}\n\\end{array}\n\n`

    // 显示输入集合和关系信息
    latexCode += `\\begin{array}{c}\n\\text{输入集合和关系:}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n${result.formula}\n\\end{array}\n\n`

    // 显示关系矩阵
    if (result.relationRMatrix) {
      latexCode += `\\begin{array}{c}\n\\text{关系R的矩阵:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\nM_R = ${result.relationRMatrix}\n\\end{array}\n\n`
    }

    if (result.relationSMatrix) {
      latexCode += `\\begin{array}{c}\n\\text{关系S的矩阵:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\nM_S = ${result.relationSMatrix}\n\\end{array}\n\n`
    }

    // 显示关系交运算结果
    if (result.intersectionResult) {
      latexCode += `\\begin{array}{c}\n\\text{关系交运算结果:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\nR \\cap S = ${result.intersectionResult}\n\\end{array}\n\n`
    }

    // 显示关系并运算结果
    if (result.unionResult) {
      latexCode += `\\begin{array}{c}\n\\text{关系并运算结果:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\nR \\cup S = ${result.unionResult}\n\\end{array}\n\n`
    }

    // 显示关系差运算结果
    if (result.subtractResult) {
      latexCode += `\\begin{array}{c}\n\\text{关系差运算结果:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\nR - S = ${result.subtractResult}\n\\end{array}\n\n`
    }

    // 显示关系逆运算结果
    if (result.inverseRResult || result.inverseSResult) {
      latexCode += `\\begin{array}{c}\n\\text{关系逆运算结果:}\n\\end{array}\n\n`
      if (result.inverseRResult) {
        latexCode += `\\begin{array}{c}\nR^{-1} = ${result.inverseRResult}\n\\end{array}\n\n`
      }
      if (result.inverseSResult) {
        latexCode += `\\begin{array}{c}\nS^{-1} = ${result.inverseSResult}\n\\end{array}\n\n`
      }
    }

    // 显示关系复合运算结果
    if (result.compositeResult) {
      latexCode += `\\begin{array}{c}\n\\text{关系复合运算结果:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n${result.compositeResult}\n\\end{array}\n\n`
    }

    // 显示逆的复合运算结果
    if (result.invcompResult) {
      latexCode += `\\begin{array}{c}\n\\text{逆的复合运算结果:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n${result.invcompResult}\n\\end{array}\n\n`
    }
  }

  // 处理关系性质判断结果
  if (result.type === 'relation-property') {
    latexCode += `\\begin{array}{c}\n\\text{关系性质判断分析结果:}\n\\end{array}\n\n`

    // 显示输入集合和关系信息
    latexCode += `\\begin{array}{c}\n\\text{输入集合和关系:}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n${result.formula}\n\\end{array}\n\n`

    // 显示关系矩阵
    if (result.matrixString) {
      latexCode += `\\begin{array}{c}\n\\text{关系矩阵:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n${result.matrixString}\n\\end{array}\n\n`
    }

    // 显示关系图
    if (result.graphImageUrl) {
      latexCode += `\\begin{array}{c}\n\\text{关系图已生成 (图片路径: ${result.graphImageUrl})}\n\\end{array}\n\n`
    }

    // 自反性分析
    if (result.reflexiveResult) {
      const reflexiveText = result.reflexiveResult.isReflexive ? '自反关系' : '不是自反关系'
      latexCode += `\\begin{array}{c}\n\\text{自反性分析: ${reflexiveText}}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n${result.reflexiveResult.explanation}\n\\end{array}\n\n`
      if (result.reflexiveResult.counterExample) {
        latexCode += `\\begin{array}{c}\n\\text{反例:} ${result.reflexiveResult.counterExample}\n\\end{array}\n\n`
      }
    }

    // 反自反性分析
    if (result.irreflexiveResult) {
      const irreflexiveText = result.irreflexiveResult.isReflexive ? '反自反关系' : '不是反自反关系'
      latexCode += `\\begin{array}{c}\n\\text{反自反性分析: ${irreflexiveText}}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n${result.irreflexiveResult.explanation}\n\\end{array}\n\n`
      if (result.irreflexiveResult.counterExample) {
        latexCode += `\\begin{array}{c}\n\\text{反例:} ${result.irreflexiveResult.counterExample}\n\\end{array}\n\n`
      }
    }

    // 对称性分析
    if (result.symmetricResult) {
      const symmetricText = result.symmetricResult.isSymmetric ? '对称关系' : '不是对称关系'
      latexCode += `\\begin{array}{c}\n\\text{对称性分析: ${symmetricText}}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n${result.symmetricResult.explanation}\n\\end{array}\n\n`
      if (result.symmetricResult.counterExample1 && result.symmetricResult.counterExample2) {
        latexCode += `\\begin{array}{c}\n\\text{反例:} ${result.symmetricResult.counterExample1} \\wedge ${result.symmetricResult.counterExample2}\n\\end{array}\n\n`
      }
    }

    // 反对称性分析
    if (result.antisymmetricResult) {
      const antisymmetricText = result.antisymmetricResult.isSymmetric ? '反对称关系' : '不是反对称关系'
      latexCode += `\\begin{array}{c}\n\\text{反对称性分析: ${antisymmetricText}}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n${result.antisymmetricResult.explanation}\n\\end{array}\n\n`
      if (result.antisymmetricResult.counterExample1 && result.antisymmetricResult.counterExample2) {
        latexCode += `\\begin{array}{c}\n\\text{反例:} ${result.antisymmetricResult.counterExample1} \\wedge ${result.antisymmetricResult.counterExample2}\n\\end{array}\n\n`
      }
    }

    // 传递性分析
    if (result.transitiveResult) {
      const transitiveText = result.transitiveResult.isTransitive ? '传递关系' : '不是传递关系'
      latexCode += `\\begin{array}{c}\n\\text{传递性分析: ${transitiveText}}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n${result.transitiveResult.explanation}\n\\end{array}\n\n`
      if (result.transitiveResult.counterExample1 && result.transitiveResult.counterExample2 && result.transitiveResult.counterExample3) {
        latexCode += `\\begin{array}{c}\n\\text{反例:} ${result.transitiveResult.counterExample1} \\wedge ${result.transitiveResult.counterExample2} \\wedge ${result.transitiveResult.counterExample3}\n\\end{array}\n\n`
      }
    }
  }

  // 处理关系闭包计算结果
  if (result.type === 'relation-closure') {
    latexCode += `\\begin{array}{c}\n\\text{关系闭包计算结果:}\n\\end{array}\n\n`

    // 显示输入集合和关系信息
    latexCode += `\\begin{array}{c}\n\\text{输入集合和关系:}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n${result.formula}\n\\end{array}\n\n`

    // 显示关系矩阵
    if (result.relationMatrix) {
      latexCode += `\\begin{array}{c}\n\\text{关系R的矩阵:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\nM_R = ${result.relationMatrix}\n\\end{array}\n\n`
    }

    // 显示关系图
    if (result.relationGraphUrl) {
      latexCode += `\\begin{array}{c}\n\\text{关系图已生成 (图片路径: ${result.relationGraphUrl})}\n\\end{array}\n\n`
    }

    // 自反闭包结果
    if (result.reflexiveClosureResult) {
      latexCode += `\\begin{array}{c}\n\\text{自反闭包计算结果:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\nr(R) = ${result.reflexiveClosureResult.closureLaTeX}\n\\end{array}\n\n`

      if (result.reflexiveClosureResult.closureMatrix) {
        latexCode += `\\begin{array}{c}\n\\text{自反闭包矩阵:}\n\\end{array}\n\n`
        latexCode += `\\begin{array}{c}\nM_{r(R)} = ${result.reflexiveClosureResult.closureMatrix}\n\\end{array}\n\n`
      }

      if (result.reflexiveClosureResult.closureGraphUrl) {
        latexCode += `\\begin{array}{c}\n\\text{自反闭包关系图已生成 (图片路径: ${result.reflexiveClosureResult.closureGraphUrl})}\n\\end{array}\n\n`
      }
    }

    // 对称闭包结果
    if (result.symmetricClosureResult) {
      latexCode += `\\begin{array}{c}\n\\text{对称闭包计算结果:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\ns(R) = ${result.symmetricClosureResult.closureLaTeX}\n\\end{array}\n\n`

      if (result.symmetricClosureResult.closureMatrix) {
        latexCode += `\\begin{array}{c}\n\\text{对称闭包矩阵:}\n\\end{array}\n\n`
        latexCode += `\\begin{array}{c}\nM_{s(R)} = ${result.symmetricClosureResult.closureMatrix}\n\\end{array}\n\n`
      }

      if (result.symmetricClosureResult.closureGraphUrl) {
        latexCode += `\\begin{array}{c}\n\\text{对称闭包关系图已生成 (图片路径: ${result.symmetricClosureResult.closureGraphUrl})}\n\\end{array}\n\n`
      }
    }

    // 传递闭包结果
    if (result.transitiveClosureResult) {
      latexCode += `\\begin{array}{c}\n\\text{传递闭包计算结果 (${result.transitiveClosureResult.algorithmUsed}):}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\nt(R) = ${result.transitiveClosureResult.closureLaTeX}\n\\end{array}\n\n`

      if (result.transitiveClosureResult.closureMatrix) {
        latexCode += `\\begin{array}{c}\n\\text{传递闭包矩阵:}\n\\end{array}\n\n`
        latexCode += `\\begin{array}{c}\nM_{t(R)} = ${result.transitiveClosureResult.closureMatrix}\n\\end{array}\n\n`
      }

      // 传递闭包详细计算过程
      if (result.transitiveClosureResult.detailMatrices && result.transitiveClosureResult.detailMatrices.length > 0) {
        latexCode += `\\begin{array}{c}\n\\text{传递闭包详细计算过程:}\n\\end{array}\n\n`

        result.transitiveClosureResult.detailMatrices.forEach((matrix, index) => {
          const description = result.transitiveClosureResult.detailDescriptions[index] || `步骤${index + 1}`
          latexCode += `\\begin{array}{c}\n\\text{${description}:}\n\\end{array}\n\n`
          latexCode += `\\begin{array}{c}\n${matrix}\n\\end{array}\n\n`
        })
      }

      if (result.transitiveClosureResult.closureGraphUrl) {
        latexCode += `\\begin{array}{c}\n\\text{传递闭包关系图已生成 (图片路径: ${result.transitiveClosureResult.closureGraphUrl})}\n\\end{array}\n\n`
      }
    }

    // 等价闭包结果
    if (result.equivalenceClosureResult) {
      latexCode += `\\begin{array}{c}\n\\text{等价闭包计算结果:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\ntsr(R) = ${result.equivalenceClosureResult.closureLaTeX}\n\\end{array}\n\n`

      if (result.equivalenceClosureResult.closureMatrix) {
        latexCode += `\\begin{array}{c}\n\\text{等价闭包矩阵:}\n\\end{array}\n\n`
        latexCode += `\\begin{array}{c}\nM_{tsr(R)} = ${result.equivalenceClosureResult.closureMatrix}\n\\end{array}\n\n`
      }

      if (result.equivalenceClosureResult.closureGraphUrl) {
        latexCode += `\\begin{array}{c}\n\\text{等价闭包关系图已生成 (图片路径: ${result.equivalenceClosureResult.closureGraphUrl})}\n\\end{array}\n\n`
      }
    }
  }

  // 处理函数性质判断结果
  if (result.type === 'function-property') {
    latexCode += `\\begin{array}{c}\n\\text{函数性质判断分析结果:}\n\\end{array}\n\n`

    // 显示输入集合和函数信息
    latexCode += `\\begin{array}{c}\n\\text{输入集合和函数:}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n${result.formula}\n\\end{array}\n\n`

    // 显示函数判断结果
    const functionText = result.function ? '该关系是函数' : '该关系不是函数'
    latexCode += `\\begin{array}{c}\n\\text{函数性质判断: ${functionText}}\n\\end{array}\n\n`

    // 如果不是函数，就不显示后续的性质分析
    if (!result.function) {
      return latexCode
    }

    // 显示关系矩阵
    if (result.relationMatrix) {
      latexCode += `\\begin{array}{c}\n\\text{关系矩阵:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\nM_F = ${result.relationMatrix}\n\\end{array}\n\n`
    }

    // 显示关系图
    if (result.relationGraphUrl) {
      latexCode += `\\begin{array}{c}\n\\text{函数关系图已生成 (图片路径: ${result.relationGraphUrl})}\n\\end{array}\n\n`
    }

    // 单射性判断结果
    if (result.injectionResult) {
      const injectionText = result.injectionResult.isProperty ? '单射函数（入射）' : '不是单射函数（入射）'
      latexCode += `\\begin{array}{c}\n\\text{单射性判断: ${injectionText}}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n${result.injectionResult.description}\n\\end{array}\n\n`
      if (result.injectionResult.counterExample) {
        latexCode += `\\begin{array}{c}\n\\text{反例: 元素} ${result.injectionResult.counterExample} \\text{有多个原像}\n\\end{array}\n\n`
      }
    }

    // 满射性判断结果
    if (result.surjectionResult) {
      const surjectionText = result.surjectionResult.isProperty ? '满射函数' : '不是满射函数'
      latexCode += `\\begin{array}{c}\n\\text{满射性判断: ${surjectionText}}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n${result.surjectionResult.description}\n\\end{array}\n\n`
      if (result.surjectionResult.counterExample) {
        latexCode += `\\begin{array}{c}\n\\text{反例: 元素} ${result.surjectionResult.counterExample} \\text{没有原像}\n\\end{array}\n\n`
      }
    }

    // 双射性判断结果
    if (result.bijectionResult) {
      const bijectionText = result.bijectionResult.isProperty ? '双射函数' : '不是双射函数'
      latexCode += `\\begin{array}{c}\n\\text{双射性判断: ${bijectionText}}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n${result.bijectionResult.description}\n\\end{array}\n\n`
      if (result.bijectionResult.counterExample) {
        latexCode += `\\begin{array}{c}\n\\text{反例: 元素} ${result.bijectionResult.counterExample} \\text{没有唯一的原像}\n\\end{array}\n\n`
      }
    }
  }

  if (result.type === 'comb-calculator') {
    latexCode += `\\begin{array}{c}\n\\text{排列组合数计算结果:}\n\\end{array}\n\n`

    // 显示输入参数
    latexCode += `\\begin{array}{c}\n\\text{输入参数:}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n${result.formula}\n\\end{array}\n\n`

    // 幂运算结果
    if (result.powerResult) {
      latexCode += `\\begin{array}{c}\n\\text{幂运算结果:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\nn^{m} = ${result.powerResult}\n\\end{array}\n\n`
    }

    // 组合数结果
    if (result.combinationResult) {
      latexCode += `\\begin{array}{c}\n\\text{组合数结果:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\nC(n, m) = ${result.combinationResult}\n\\end{array}\n\n`
    }

    // 排列数结果
    if (result.permutationResult) {
      latexCode += `\\begin{array}{c}\n\\text{排列数结果:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\nP(n, m) = ${result.permutationResult}\n\\end{array}\n\n`
    }

    // 阶乘结果
    if (result.factorialNResult || result.factorialMResult) {
      latexCode += `\\begin{array}{c}\n\\text{阶乘结果:}\n\\end{array}\n\n`
      if (result.factorialNResult) {
        latexCode += `\\begin{array}{c}\nn! = ${result.factorialNResult}\n\\end{array}\n\n`
      }
      if (result.factorialMResult) {
        latexCode += `\\begin{array}{c}\nm! = ${result.factorialMResult}\n\\end{array}\n\n`
      }
    }
  }

  if (result.type === 'expression-calculator') {
    latexCode += `\\begin{array}{c}\n\\text{组合表达式计算结果（第 ${result.index} 次）:}\n\\end{array}\n\n`

    // 显示原始表达式
    latexCode += `\\begin{array}{c}\n\\text{原始表达式:}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n\\text{表达式} = ${result.originalExpression}\n\\end{array}\n\n`

    // 显示计算结果
    latexCode += `\\begin{array}{c}\n\\text{计算结果:}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n${result.originalExpression} = ${result.result}\n\\end{array}\n\n`
  }

  if (result.type === 'recu-expr-calculator') {
    latexCode += `\\begin{array}{c}\n\\text{递归表达式计算结果（第 ${result.index} 次）:}\n\\end{array}\n\n`

    // 显示递归表达式参数
    latexCode += `\\begin{array}{c}\n\\text{递归表达式参数:}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n\\text{递归表达式:} ${result.originalExpression}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n\\text{递归次数:} n = ${result.n}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n\\text{初始值:} a_1 = ${result.a}, b_1 = ${result.b}\n\\end{array}\n\n`

    // 显示计算结果
    latexCode += `\\begin{array}{c}\n\\text{计算结果:}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\na_{${result.n}} = ${result.result}\n\\end{array}\n\n`

    // 添加递归计算过程的LaTeX描述
    latexCode += `\\begin{array}{c}\n\\text{递归关系:}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\na_{n} = ${result.originalExpression}, \\quad a_1 = ${result.a}, b_1 = ${result.b}\n\\end{array}\n\n`
  }

  // 处理字符串计数结果
  if (result.type === 'count-string') {
    latexCode += `\\begin{array}{c}\n\\text{字符串计数分析结果:}\n\\end{array}\n\n`

    // 基本信息
    latexCode += `\\begin{array}{c}\n\\text{输入参数:}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n${result.formula}\n\\end{array}\n\n`

    latexCode += `\\begin{array}{c}\n\\text{基础字符集:} ${result.baseSet}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n\\text{字符串长度:} n = ${result.length}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n\\text{重复允许:} ${result.allowRepetition ? '\\text{允许重复}' : '\\text{不允许重复}'}\n\\end{array}\n\n`

    // 过滤条件
    latexCode += `\\begin{array}{c}\n\\text{过滤条件:}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n${result.filterDescription}\n\\end{array}\n\n`

    // 统计结果
    latexCode += `\\begin{array}{c}\n\\text{统计结果:}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n\\text{总字符串数:} ${result.totalCount}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n\\text{满足条件的字符串数:} ${result.acceptedCount}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n\\text{结果公式:} ${result.resultFormula}\n\\end{array}\n\n`

    // 详细结果（只显示前20个）
    if (result.stringDetails && result.stringDetails.length > 0) {
      latexCode += `\\begin{array}{c}\n\\text{字符串详情（前20个）:}\n\\end{array}\n\n`

      // 创建表格头部
      latexCode += `\\begin{array}{c|c|c|c}\n`
      latexCode += `\\text{序号} & \\text{字符串} & \\text{是否接受} & \\text{接受计数} \\\\\n`
      latexCode += `\\hline\n`

      // 添加数据行（最多20行）
      const maxRows = Math.min(result.stringDetails.length, 20)
      for (let i = 0; i < maxRows; i++) {
        const detail = result.stringDetails[i]
        const accepted = detail.accepted ? '\\text{接受}' : '\\text{不接受}'
        latexCode += `${detail.index} & ${detail.content} & ${accepted} & ${detail.acceptedCount} \\\\\n`
      }

      latexCode += `\\end{array}\n\n`

      // 如果有更多数据，显示提示
      if (result.stringDetails.length > 20) {
        latexCode += `\\begin{array}{c}\n\\text{... 还有 ${result.stringDetails.length - 20} 个字符串 ...}\n\\end{array}\n\n`
      }
    }
  }

  // 整数计数结果处理
  if (result.type === 'countInteger') {
    latexCode += `\\begin{array}{c}\n\\text{整数计数分析结果:}\n\\end{array}\n\n`

    // 基本信息
    latexCode += `\\begin{array}{c}\n\\text{输入参数:}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n${result.formula}\n\\end{array}\n\n`

    latexCode += `\\begin{array}{c}\n\\text{计数范围:} [${result.start}, ${result.end}]\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n\\text{总整数个数:} ${result.totalCount}\n\\end{array}\n\n`

    // 过滤条件
    latexCode += `\\begin{array}{c}\n\\text{过滤条件:}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n${result.filterDescription}\n\\end{array}\n\n`

    // 统计结果
    latexCode += `\\begin{array}{c}\n\\text{统计结果:}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n\\text{满足条件的整数数:} ${result.acceptedCount}\n\\end{array}\n\n`

    if (result.totalCount > 0) {
      const percentage = ((result.acceptedCount / result.totalCount) * 100).toFixed(2)
      latexCode += `\\begin{array}{c}\n\\text{占比:} ${percentage}\\% \n\\end{array}\n\n`
    }

    // 详细结果 - 按原Java应用风格显示每个整数
    if (result.detailedResults && result.detailedResults.length > 0) {
      latexCode += `\\begin{array}{c}\n\\text{整数处理详情:}\n\\end{array}\n\n`

      // 逐个显示每个整数的处理结果
      latexCode += `\\begin{array}{l}\n`
      for (let i = 0; i < result.detailedResults.length; i++) {
        const detail = result.detailedResults[i]
        const acceptMessage = detail.acceptMessage.replace(/\s+/g, '\\ ') // 替换空格为LaTeX空格
        latexCode += `\\quad ${detail.index} : ${detail.value}, \\textrm{${acceptMessage}} \\\\\n`
      }
      latexCode += `\\end{array}\n\n`

      // 如果有更多数据未显示，添加省略号
      if (result.detailedResults.length < result.totalCount) {
        latexCode += `\\begin{array}{c}\n\\vdots \\\\\n\\text{${result.totalCount - result.detailedResults.length} 个整数未显示}\n\\end{array}\n\n`
      }
    }
  }

  // 处理不定方程非负整数解计数结果
  if (result.type === 'count-equation-solver') {
    latexCode += `\\begin{array}{c}\n\\text{不定方程非负整数解计数结果:}\n\\end{array}\n\n`

    // 方程信息
    latexCode += `\\begin{array}{c}\n\\text{方程:}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n${result.formula}\n\\end{array}\n\n`

    // 约束条件
    if (result.filterLaTeX) {
      latexCode += `\\begin{array}{c}\n\\text{约束条件:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n${result.filterLaTeX}\n\\end{array}\n\n`
    } else {
      latexCode += `\\begin{array}{c}\n\\text{约束条件: 无}\n\\end{array}\n\n`
    }

    // 统计结果
    latexCode += `\\begin{array}{c}\n\\text{统计结果:}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n\\text{总解数:} ${result.totalCount} \\quad \\text{符合条件的解数:} ${result.acceptedCount}\n\\end{array}\n\n`

    // 组合数学公式
    latexCode += `\\begin{array}{c}\n\\text{组合数学公式:}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n${result.combinationFormula}\n\\end{array}\n\n`

    // 详细解列表
    if (result.solutions && result.solutions.length > 0) {
      latexCode += `\\begin{array}{c}\n\\text{解列表:}\n\\end{array}\n\n`

      // 最多显示前10个解，避免LaTeX过长
      const maxDisplay = 10
      const displayCount = Math.min(result.solutions.length, maxDisplay)

      latexCode += `\\begin{array}{l}\n`
      for (let i = 0; i < displayCount; i++) {
        const solution = result.solutions[i]
        const solutionLaTeX = solution.solutionLaTeX.replace(/\s+/g, '\\ ') // 替换空格为LaTeX空格
        const statusText = solution.accepted ? '' : ', \\text{不符合条件}'
        latexCode += `\\quad \\text{No.}${solution.number}: ${solutionLaTeX}${statusText} \\\\\n`
      }
      latexCode += `\\end{array}\n\n`

      // 如果有更多解未显示，添加省略号
      if (result.solutions.length > maxDisplay) {
        latexCode += `\\begin{array}{c}\n\\vdots \\\\\n\\text{${result.solutions.length - maxDisplay} 个解未显示}\n\\end{array}\n\n`
      }
    }
  }

  // 处理不同性质的函数计数结果
  if (result.type === 'countFunction') {
    latexCode += `\\begin{array}{c}\n\\text{不同性质的函数计数分析结果:}\n\\end{array}\n\n`

    // 集合信息
    latexCode += `\\begin{array}{c}\n\\text{输入集合:}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\nA = ${result.setALaTeX}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\nB = ${result.setBLaTeX}\n\\end{array}\n\n`

    // 统计信息
    latexCode += `\\begin{array}{c}\n\\text{函数统计:}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n|B|^{|A|} = ${result.bSize}^\\{${result.aSize}\\} = ${result.totalCount}\n\\end{array}\n\n`

    latexCode += `\\begin{array}{l}\n`
    latexCode += `\\quad \\text{总函数个数:} ${result.totalCount} \\\\\n`
    latexCode += `\\quad \\text{双射函数个数:} ${result.bijectionCount} \\\\\n`
    latexCode += `\\quad \\text{满射函数个数:} ${result.surjectionCount} \\\\\n`
    latexCode += `\\quad \\text{单射函数个数:} ${result.injectionCount} \\\\\n`
    latexCode += `\\end{array}\n\n`

    // 具体函数列表
    if (result.functionList && result.functionList.length > 0) {
      latexCode += `\\begin{array}{c}\n\\text{具体函数列表:}\n\\end{array}\n\n`

      const maxDisplay = 20
      const displayCount = Math.min(result.functionList.length, maxDisplay)

      latexCode += `\\begin{array}{l}\n`
      for (let i = 0; i < displayCount; i++) {
        const func = result.functionList[i]
        const funcLaTeX = func.laTeX.replace(/\s+/g, '\\ ') // 替换空格为LaTeX空格
        let label = ''

        if (func.type === 'bijection') {
          label = `\\text{第 ${func.totalNumber} 个函数，双射第 ${func.countNumber} 个:}`
        } else if (func.type === 'injection') {
          label = `\\text{第 ${func.totalNumber} 个函数，单射第 ${func.countNumber} 个:}`
        } else if (func.type === 'surjection') {
          label = `\\text{第 ${func.totalNumber} 个函数，满射第 ${func.countNumber} 个:}`
        } else {
          label = `\\text{第 ${func.totalNumber} 个函数:}`
        }

        latexCode += `\\quad ${label} \\\\\n`
        latexCode += `\\quad ${funcLaTeX} \\\\\n\n`
      }
      latexCode += `\\end{array}\n\n`

      // 如果有更多函数未显示，添加省略号
      if (result.functionList.length > maxDisplay) {
        latexCode += `\\begin{array}{c}\n\\vdots \\\\\n\\text{${result.functionList.length - maxDisplay} 个函数未显示}\n\\end{array}\n\n`
      }
    }
  }

  // 排列生成结果处理
  if (result.type === 'gen-permutation') {
    latexCode += `\\begin{array}{c}\n\\text{排列生成分析结果:}\n\\end{array}\n\n`

    // 基本信息
    latexCode += `\\begin{array}{c}\n\\text{生成参数:}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n${result.formula}\n\\end{array}\n\n`

    // 详细参数信息
    latexCode += `\\begin{array}{l}\n`
    latexCode += `\\quad \\text{集合 B:} ${result.baseSet} \\\\\n`
    latexCode += `\\quad \\text{排列长度:} ${result.length} \\\\\n`
    latexCode += `\\quad \\text{起始排列:} ${result.startString || '从第一个开始'} \\\\\n`
    latexCode += `\\quad \\text{生成个数:} ${result.number} \\\\\n`
    latexCode += `\\quad \\text{总排列数:} ${result.totalCount} \\\\\n`
    latexCode += `\\end{array}\n\n`

    // 生成结果
    if (result.permutations && result.permutations.length > 0) {
      latexCode += `\\begin{array}{c}\n\\text{生成的排列序列:}\n\\end{array}\n\n`

      // 按行显示排列，每行最多8个
      const maxPerLine = 8
      const permutations = result.permutations.slice(0, result.number)

      latexCode += `\\begin{array}{l}\n`
      for (let i = 0; i < permutations.length; i++) {
        if (i > 0 && i % maxPerLine === 0) {
          latexCode += `\\\\\n`
        } else if (i > 0) {
          latexCode += `\\quad \\longrightarrow \\quad `
        }

        if (i % maxPerLine === 0) {
          latexCode += `\\quad`
        }

        latexCode += `${permutations[i]}`

        if (i === permutations.length - 1 && permutations.length >= result.number) {
          latexCode += ` \\quad \\cdots \\cdots`
        }
      }
      latexCode += `\n\\end{array}\n\n`
    }

    // 警告信息
    if (!result.startStringFound) {
      latexCode += `\\begin{array}{c}\n\\text{警告: 起始字符串 ${result.startString} 不在生成的排列之中!}\n\\end{array}\n\n`
    }

    // 统计信息
    if (result.message) {
      latexCode += `\\begin{array}{c}\n\\text{统计信息:}\n\\end{array}\n\n`

      // 将消息转换为LaTeX格式
      const message = result.message
        .replace(/集合 B = ([^,]+)/, '集合 B = $1$')
        .replace(/P\((\d+),\s*(\d+)\) = (\d+)/, 'P($1, $2) = $3')
        .replace(/(\d+) 个排列/, '$1$ 个排列')
        .replace(/(\d+) 个/, '$1$ 个')

      latexCode += `\\begin{array}{c}\n${message}\n\\end{array}\n\n`
    }
  }

  // 组合生成结果处理
  if (result.type === 'genCombination') {
    latexCode += `\\begin{array}{c}\n\\text{组合生成分析结果:}\n\\end{array}\n\n`

    // 基本信息
    latexCode += `\\begin{array}{c}\n\\text{生成参数:}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n${result.latexDescription}\n\\end{array}\n\n`

    // 详细参数信息
    latexCode += `\\begin{array}{l}\n`
    latexCode += `\\quad \\text{基础集 B:} ${result.baseSet} \\\\\n`
    latexCode += `\\quad \\text{组合长度:} ${result.length} \\\\\n`
    latexCode += `\\quad \\text{起始组合:} ${result.startString || '从第一个开始'} \\\\\n`
    latexCode += `\\quad \\text{生成个数:} ${result.number} \\\\\n`
    latexCode += `\\quad \\text{总组合数:} ${result.totalCombinations} \\\\\n`
    latexCode += `\\end{array}\n\n`

    // 生成结果
    if (result.generatedCombinations && result.generatedCombinations.length > 0) {
      latexCode += `\\begin{array}{c}\n\\text{生成的组合序列:}\n\\end{array}\n\n`

      // 按行显示组合，每行最多8个
      const maxPerLine = 8
      const combinations = result.generatedCombinations.slice(0, result.number)

      latexCode += `\\begin{array}{l}\n`
      for (let i = 0; i < combinations.length; i++) {
        if (i > 0 && i % maxPerLine === 0) {
          latexCode += `\\\\\n`
        } else if (i > 0) {
          latexCode += `\\quad \\longrightarrow \\quad `
        }

        if (i % maxPerLine === 0) {
          latexCode += `\\quad`
        }

        latexCode += `${combinations[i]}`

        if (i === combinations.length - 1 && combinations.length >= result.number) {
          latexCode += ` \\quad \\cdots \\cdots`
        }
      }
      latexCode += `\n\\end{array}\n\n`
    }

    // 警告信息
    if (result.errorMessage && result.errorMessage.includes('警告')) {
      latexCode += `\\begin{array}{c}\n\\text{警告: ${result.errorMessage}}\n\\end{array}\n\n`
    }

    // 统计信息
    latexCode += `\\begin{array}{c}\n\\text{统计信息:}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n\\text{总组合数} = ${result.latexFormula}\n\\end{array}\n\n`

    // 添加描述信息
    const description = `从基础集 ${result.baseSet} 中取 ${result.length} 个元素进行组合，总共有 ${result.totalCombinations} 种不同的组合。`
    latexCode += `\\begin{array}{c}\n${description}\n\\end{array}\n\n`
  }

  // 处理允许重复组合的生成结果
  if (result.type === 'gen-rep-comb') {
    latexCode += `\\begin{array}{c}\n\\text{允许重复组合的生成分析结果:}\n\\end{array}\n\n`

    // 基本信息
    latexCode += `\\begin{array}{c}\n\\text{生成参数:}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\nB = ${result.baseSetLaTeX}, \\quad n = ${result.length}, \\quad \\text{从} \\, ${result.startString} \\, \\text{开始生成} \\, ${result.number} \\, \\text{个组合}\n\\end{array}\n\n`

    // 详细参数信息
    latexCode += `\\begin{array}{l}\n`
    latexCode += `\\quad \\text{基础集 B:} ${result.baseSetLaTeX} \\\\\n`
    latexCode += `\\quad \\text{组合长度:} ${result.length} \\\\\n`
    latexCode += `\\quad \\text{起始组合:} ${result.startString || '从第一个开始'} \\\\\n`
    latexCode += `\\quad \\text{生成个数:} ${result.number} \\\\\n`
    latexCode += `\\quad \\text{总组合数:} ${result.totalCombinations} \\\\\n`
    latexCode += `\\end{array}\n\n`

    // 生成结果
    if (result.generatedCombinations && result.generatedCombinations.length > 0) {
      latexCode += `\\begin{array}{c}\n\\text{生成的组合序列:}\n\\end{array}\n\n`

      // 按行显示组合，每行最多8个
      const maxPerLine = 8
      const combinations = result.generatedCombinations.slice(0, result.number)

      latexCode += `\\begin{array}{l}\n`
      for (let i = 0; i < combinations.length; i++) {
        if (i > 0 && i % maxPerLine === 0) {
          latexCode += `\\\\\n`
        } else if (i > 0) {
          latexCode += `\\quad \\longrightarrow \\quad `
        }

        if (i % maxPerLine === 0) {
          latexCode += `\\quad`
        }

        latexCode += `${combinations[i]}`

        if (i === combinations.length - 1 && combinations.length >= result.number) {
          latexCode += ` \\quad \\cdots \\cdots`
        }
      }
      latexCode += `\n\\end{array}\n\n`
    }

    // 错误信息
    if (result.errorMessage) {
      latexCode += `\\begin{array}{c}\n\\text{错误信息: ${result.errorMessage}}\n\\end{array}\n\n`
    }

    // 统计信息
    latexCode += `\\begin{array}{c}\n\\text{统计信息:}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n${result.combinationCountLaTeX}\n\\end{array}\n\n`

    // 添加描述信息
    const description = `从基础集 ${result.baseSetLaTeX} 中允许重复地取 ${result.length} 个元素进行组合，总共有 ${result.totalCombinations} 种不同的组合。`
    latexCode += `\\begin{array}{c}\n${description}\n\\end{array}\n\n`
  }

  // 处理运算性质判断结果
  if (result.type === 'operation-property') {
    latexCode += `\\begin{array}{c}\n\\text{运算性质分析结果:}\n\\end{array}\n\n`

    // 显示基本信息
    latexCode += `\\begin{array}{c}\n\\text{集合和运算符:} ${result.formula}\n\\end{array}\n\n`

    // 显示运算符表格
    if (result.operator1Table) {
      latexCode += `\\begin{array}{c}\n\\text{运算符一(∘)的运算表:}\n\\end{array}\n\n`
      latexCode += result.operator1Table + '\n\n'
    }

    if (result.operator2Table) {
      latexCode += `\\begin{array}{c}\n\\text{运算符二(*)的运算表:}\n\\end{array}\n\n`
      latexCode += result.operator2Table + '\n\n'
    }

    // 显示运算符一的性质
    if (result.operator1Properties && result.operator1Properties.length > 0) {
      latexCode += `\\begin{array}{c}\n\\text{运算符一(∘)的性质:}\n\\end{array}\n\n`

      result.operator1Properties.forEach(property => {
        const propertyName = getPropertyNameChinese(property.propertyType)
        const resultText = property.hasProperty ? '✓ 满足' : '✗ 不满足'
        latexCode += `\\begin{array}{c}\n\\text{${propertyName}:} ${resultText}\n\\end{array}\n\n`

        if (property.reason) {
          latexCode += `\\begin{array}{c}\n\\text{原因:} ${property.reason}\n\\end{array}\n\n`
        }

        if (property.details) {
          latexCode += `\\begin{array}{c}\n\\text{详细信息:} ${property.details}\n\\end{array}\n\n`
        }
      })
    }

    // 显示运算符二的性质
    if (result.operator2Properties && result.operator2Properties.length > 0) {
      latexCode += `\\begin{array}{c}\n\\text{运算符二(*)的性质:}\n\\end{array}\n\n`

      result.operator2Properties.forEach(property => {
        const propertyName = getPropertyNameChinese(property.propertyType)
        const resultText = property.hasProperty ? '✓ 满足' : '✗ 不满足'
        latexCode += `\\begin{array}{c}\n\\text{${propertyName}:} ${resultText}\n\\end{array}\n\n`

        if (property.reason) {
          latexCode += `\\begin{array}{c}\n\\text{原因:} ${property.reason}\n\\end{array}\n\n`
        }

        if (property.details) {
          latexCode += `\\begin{array}{c}\n\\text{详细信息:} ${property.details}\n\\end{array}\n\n`
        }
      })
    }

    // 显示运算符之间的关系性质
    if (result.relationProperties && result.relationProperties.length > 0) {
      latexCode += `\\begin{array}{c}\n\\text{运算符之间的关系性质:}\n\\end{array}\n\n`

      result.relationProperties.forEach(relation => {
        const relationName = getRelationNameChinese(relation.relationType)
        const resultText = relation.hasRelation ? '✓ 满足' : '✗ 不满足'
        latexCode += `\\begin{array}{c}\n\\text{${relationName} - ${relation.direction}:} ${resultText}\n\\end{array}\n\n`

        if (relation.reason) {
          latexCode += `\\begin{array}{c}\n\\text{原因:} ${relation.reason}\n\\end{array}\n\n`
        }
      })
    }
  }

  // 处理群U(m)分析结果
  if (result.type === 'groupUm') {
    latexCode += `\\begin{array}{c}\n\\text{群U(${result.m})分析结果:}\n\\end{array}\n\n`

    // 基本信息
    latexCode += `\\begin{array}{c}\n\\text{群的基本信息:}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n${result.formula}\n\\end{array}\n\n`

    // 循环群信息
    if (result.showCycleGroup) {
      latexCode += `\\begin{array}{c}\n\\text{循环群分析:}\n\\end{array}\n\n`
      if (result.cycleGroup) {
        latexCode += `\\begin{array}{c}\n\\text{群 U(${result.m}) 是循环群，生成元: ${result.generators}}\n\\end{array}\n\n`
      } else {
        latexCode += `\\begin{array}{c}\n\\text{群 U(${result.m}) 不是循环群}\n\\end{array}\n\n`
      }
    }

    // 群元素的幂
    if (result.showPower && result.elementPowers) {
      latexCode += `\\begin{array}{c}\n\\text{群元素的幂:}\n\\end{array}\n\n`
      result.elementPowers.forEach(powerInfo => {
        latexCode += `\\begin{array}{c}\n\\text{元素 ${powerInfo.element} 的幂:}\n\\end{array}\n\n`
        latexCode += `\\begin{array}{c}\n`
        powerInfo.powers.forEach(power => {
          latexCode += `${power} \\quad `
        })
        latexCode += `\n\\end{array}\n\n`
      })
    }

    // 群元素的阶
    if (result.showOrder && result.elementOrders) {
      latexCode += `\\begin{array}{c}\n\\text{群元素的阶:}\n\\end{array}\n\n`
      let orderString = ''
      result.elementOrders.forEach((orderInfo, index) => {
        orderString += orderInfo.formula
        if ((index + 1) % 5 === 0) {
          orderString += ' \\\\\n'
        }
      })
      latexCode += `\\begin{array}{c}\n${orderString}\n\\end{array}\n\n`
    }

    // 子群信息
    if (result.showSubgroups && result.subgroups) {
      latexCode += `\\begin{array}{c}\n\\text{群的非平凡子群:}\n\\end{array}\n\n`

      result.subgroups.forEach((subgroup, index) => {
        latexCode += `\\begin{array}{c}\n\\text{子群${index + 1}: \\{${subgroup.elements}\\}}\n\\end{array}\n\n`

        if (subgroup.isCycleSubgroup) {
          latexCode += `\\begin{array}{c}\n\\text{它是循环子群，生成元: ${subgroup.generators}}\n\\end{array}\n\n`
        } else {
          latexCode += `\\begin{array}{c}\n\\text{它不是循环子群}\n\\end{array}\n\n`
        }

        latexCode += `\\begin{array}{c}\n\\text{子群运算表:}\n\\end{array}\n\n`
        latexCode += `\\begin{array}{c}\n${subgroup.operatorTable}\n\\end{array}\n\n`

        // 陪集信息
        if (result.showCosets && subgroup.cosets && subgroup.cosets.length > 0) {
          latexCode += `\\begin{array}{c}\n\\text{子群 \\{${subgroup.elements}\\} 的陪集包括:}\n\\end{array}\n\n`

          let cosetString = ''
          subgroup.cosets.forEach((coset, cosetIndex) => {
            cosetString += coset
            if ((cosetIndex + 1) % 3 === 0) {
              cosetString += ' \\\\\n'
            } else {
              cosetString += '\\quad'
            }
          })
          latexCode += `\\begin{array}{c}\n${cosetString}\n\\end{array}\n\n`
        }
      })
    }
  }

  // 处理置换群分析结果
  if (result.type === 'group-perm') {
    latexCode += `\\begin{array}{c}\n\\text{置换群分析结果:}\n\\end{array}\n\n`

    // 基本信息
    latexCode += `\\begin{array}{c}\n\\text{置换群基本信息:}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n${result.formula}\n\\end{array}\n\n`

    // 元素表格和运算表格
    if (result.elementTable || result.operatorTable) {
      latexCode += `\\begin{array}{c}\n\\text{群元素和运算表:}\n\\end{array}\n\n`
      if (result.elementTable) {
        latexCode += `\\begin{array}{c}\n\\text{元素表格:}\n\\end{array}\n\n`
        latexCode += `\\begin{array}{c}\n${result.elementTable}\n\\end{array}\n\n`
      }
      if (result.operatorTable) {
        latexCode += `\\begin{array}{c}\n\\text{运算表格:}\n\\end{array}\n\n`
        latexCode += `\\begin{array}{c}\n${result.operatorTable}\n\\end{array}\n\n`
      }
    }

    // 循环群分析
    if (result.showCycleGroup !== undefined) {
      latexCode += `\\begin{array}{c}\n\\text{循环群分析:}\n\\end{array}\n\n`
      if (result.isCycleGroup) {
        latexCode += `\\begin{array}{c}\n\\text{置换群 S(${result.m}) 是循环群，生成元为: ${result.generators}}\n\\end{array}\n\n`
      } else {
        latexCode += `\\begin{array}{c}\n\\text{置换群 S(${result.m}) 不是循环群}\n\\end{array}\n\n`
      }
    }

    // 群元素的幂
    if (result.elementPowers && result.elementPowers.length > 0) {
      latexCode += `\\begin{array}{c}\n\\text{群元素的幂（包括群元素的逆）:}\n\\end{array}\n\n`
      result.elementPowers.forEach(power => {
        latexCode += `\\begin{array}{c}\n${power}\n\\end{array}\n\n`
      })
    }

    // 群元素的阶
    if (result.elementOrders && result.elementOrders.length > 0) {
      latexCode += `\\begin{array}{c}\n\\text{群元素的阶:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n${result.elementOrders.join('')}\n\\end{array}\n\n`
    }

    // 子群分析
    if (result.subgroups && result.subgroups.length > 0) {
      latexCode += `\\begin{array}{c}\n\\text{群的所有非平凡子群:}\n\\end{array}\n\n`

      result.subgroups.forEach((subgroup, index) => {
        latexCode += `\\begin{array}{c}\n\\text{子群${index + 1}: \\{${subgroup.subgroupElements}\\}}\n\\end{array}\n\n`

        if (subgroup.isCycleSubgroup) {
          latexCode += `\\begin{array}{c}\n\\text{是循环子群，生成元为: ${subgroup.generators}}\n\\end{array}\n\n`
        } else {
          latexCode += `\\begin{array}{c}\n\\text{不是循环子群}\n\\end{array}\n\n`
        }

        if (subgroup.operatorTable) {
          latexCode += `\\begin{array}{c}\n\\text{子群运算表:}\n\\end{array}\n\n`
          latexCode += `\\begin{array}{c}\n${subgroup.operatorTable}\n\\end{array}\n\n`
        }
      })
    }

    // 陪集分析
    if (result.cosets && result.cosets.length > 0) {
      latexCode += `\\begin{array}{c}\n\\text{群的所有非平凡子群的陪集:}\n\\end{array}\n\n`

      result.cosets.forEach((coset, index) => {
        latexCode += `\\begin{array}{c}\n\\text{子群${index + 1}: \\{${coset.subgroupElements}\\} 的陪集:}\n\\end{array}\n\n`

        latexCode += `\\begin{array}{c}\n\\text{左陪集包括:}\n\\end{array}\n\n`
        latexCode += `\\begin{array}{c}\n${coset.leftCosets.join('')}\n\\end{array}\n\n`

        latexCode += `\\begin{array}{c}\n\\text{右陪集包括:}\n\\end{array}\n\n`
        latexCode += `\\begin{array}{c}\n${coset.rightCosets.join('')}\n\\end{array}\n\n`
      })
    }

    // 正规子群分析
    if (result.normalSubgroups && result.normalSubgroups.length > 0) {
      latexCode += `\\begin{array}{c}\n\\text{群的正规子群分析:}\n\\end{array}\n\n`

      result.normalSubgroups.forEach((normal, index) => {
        latexCode += `\\begin{array}{c}\n\\text{子群${index + 1}: \\{${normal.subgroupElements}\\}}\n\\end{array}\n\n`

        if (normal.isNormal) {
          latexCode += `\\begin{array}{c}\n\\text{是正规子群，其商群的运算表如下:}\n\\end{array}\n\n`
          latexCode += `\\begin{array}{c}\n${normal.quotientGroupTable}\n\\end{array}\n\n`
        } else {
          latexCode += `\\begin{array}{c}\n\\text{不是正规子群}\n\\end{array}\n\n`
        }
      })
    }
  }

  // 处理格判断结果
  if (result.type === 'lattice-judge') {
    latexCode += `\\begin{array}{c}\n\\text{格判断分析结果:}\n\\end{array}\n\n`

    // 基本输入信息
    latexCode += `\\begin{array}{c}\n\\text{输入信息:}\n${result.formula}\n\\end{array}\n\n`

    // 哈斯图
    if (result.hasseDiagramUrl) {
      latexCode += `\\begin{array}{c}\n\\text{哈斯图已生成 (图片路径: ${result.hasseDiagramUrl})}\n\\end{array}\n\n`
    }

    // 偏序关系判断
    latexCode += `\\begin{array}{c}\n\\text{偏序关系判断:}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n\\text{${result.isPartialOrder ? '是偏序关系' : '不是偏序关系'}}\n\\end{array}\n\n`

    if (result.partialOrderReason) {
      latexCode += `\\begin{array}{c}\n\\text{原因: ${result.partialOrderReason}}\n\\end{array}\n\n`
    }

    if (result.partialOrderCounterexample) {
      latexCode += `\\begin{array}{c}\n\\text{反例: ${result.partialOrderCounterexample}}\n\\end{array}\n\n`
    }

    // 格判断（仅当是偏序关系时）
    if (result.isPartialOrder) {
      latexCode += `\\begin{array}{c}\n\\text{格判断:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n\\text{${result.isLattice ? '是格' : '不是格'}}\n\\end{array}\n\n`

      if (result.latticeReason) {
        latexCode += `\\begin{array}{c}\n\\text{原因: ${result.latticeReason}}\n\\end{array}\n\n`
      }

      if (result.latticeCounterexample) {
        latexCode += `\\begin{array}{c}\n\\text{反例: ${result.latticeCounterexample}}\n\\end{array}\n\n`
      }

      // 格运算表（仅当是格时）
      if (result.isLattice && result.supOperatorTable) {
        latexCode += `\\begin{array}{c}\n\\text{格运算表:}\n\\end{array}\n\n`

        latexCode += `\\begin{array}{c}\n\\text{最小上界运算:}\n\\end{array}\n\n`
        latexCode += `\\begin{array}{c}\n${result.supOperatorTable}\n\\end{array}\n\n`

        latexCode += `\\begin{array}{c}\n\\text{最大下界运算:}\n\\end{array}\n\n`
        latexCode += `\\begin{array}{c}\n${result.subOperatorTable}\n\\end{array}\n\n`
      }

      // 分配格判断（仅当是格且需要判断时）
      if (result.isLattice && result.isDistributive !== null) {
        latexCode += `\\begin{array}{c}\n\\text{分配格判断:}\n\\end{array}\n\n`
        latexCode += `\\begin{array}{c}\n\\text{${result.isDistributive ? '是分配格' : '不是分配格'}}\n\\end{array}\n\n`

        if (result.distributiveReason) {
          latexCode += `\\begin{array}{c}\n\\text{原因: ${result.distributiveReason}}\n\\end{array}\n\n`
        }
      }

      // 有界格判断（仅当是格且需要判断时）
      if (result.isLattice && result.isBounded !== null) {
        latexCode += `\\begin{array}{c}\n\\text{有界格判断:}\n\\end{array}\n\n`
        latexCode += `\\begin{array}{c}\n\\text{${result.isBounded ? '是有界格' : '不是有界格'}}\n\\end{array}\n\n`

        if (result.boundedReason) {
          latexCode += `\\begin{array}{c}\n\\text{原因: ${result.boundedReason}}\n\\end{array}\n\n`
        }

        if (result.isBounded && result.greatestElement && result.leastElement) {
          latexCode += `\\begin{array}{c}\n\\text{最大元: ${result.greatestElement}，最小元: ${result.leastElement}}\n\\end{array}\n\n`
        }
      }

      // 有补格判断（仅当是格且需要判断时）
      if (result.isLattice && result.isComplemented !== null) {
        latexCode += `\\begin{array}{c}\n\\text{有补格判断:}\n\\end{array}\n\n`
        latexCode += `\\begin{array}{c}\n\\text{${result.isComplemented ? '是有补格' : '不是有补格'}}\n\\end{array}\n\n`

        if (result.complementedReason) {
          latexCode += `\\begin{array}{c}\n\\text{原因: ${result.complementedReason}}\n\\end{array}\n\n`
        }

        if (result.isComplemented && result.complements) {
          latexCode += `\\begin{array}{c}\n\\text{元素补元列表:}\n\\end{array}\n\n`
          result.complements.forEach((complement, idx) => {
            latexCode += `\\begin{array}{c}\n\\text{元素 ${complement.element} 的补元: ${complement.complementElements}}\n\\end{array}\n\n`
          })
        }
      }

      // 布尔代数判断（仅当是格且需要判断时）
      if (result.isLattice && result.isBooleanAlgebra !== null) {
        latexCode += `\\begin{array}{c}\n\\text{布尔代数判断:}\n\\end{array}\n\n`
        latexCode += `\\begin{array}{c}\n\\text{${result.isBooleanAlgebra ? '是布尔代数' : '不是布尔代数'}}\n\\end{array}\n\n`

        if (result.booleanAlgebraReason) {
          latexCode += `\\begin{array}{c}\n\\text{原因: ${result.booleanAlgebraReason}}\n\\end{array}\n\n`
        }
      }
    }
  }

  // 布尔代数判断结果处理
  if (result.type === 'bool-algebra') {
    latexCode += `\\begin{array}{c}\n\\text{整除与布尔代数分析结果:}\n\\end{array}\n\n`

    // 基本输入信息
    latexCode += `\\begin{array}{c}\n\\text{输入信息:} F(${result.m}) = ${result.latticeDescription}\n\\end{array}\n\n`

    // 布尔代数判断
    latexCode += `\\begin{array}{c}\n\\text{布尔代数判断:}\n\\end{array}\n\n`
    latexCode += `\\begin{array}{c}\n\\text{${result.booleanAlgebra ? '该格是布尔代数' : '该格不是布尔代数'}}\n\\end{array}\n\n`

    // 最大元和最小元
    if (result.greatestElement && result.leastElement) {
      latexCode += `\\begin{array}{c}\n\\text{极值元素:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n\\text{最大元: ${result.greatestElement}，最小元: ${result.leastElement}}\n\\end{array}\n\n`
    }

    // 哈斯图
    if (result.hasseDiagramUrl) {
      latexCode += `\\begin{array}{c}\n\\text{哈斯图: (图片路径: ${result.hasseDiagramUrl})}\n\\end{array}\n\n`
    }

    // 运算表
    if (result.supOperatorTable && result.subOperatorTable) {
      latexCode += `\\begin{array}{c}\n\\text{运算表（上确界和下确界运算）:}\n\\end{array}\n\n`

      latexCode += `\\begin{array}{c}\n\\text{上确界运算表:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n${result.supOperatorTable}\n\\end{array}\n\n`

      latexCode += `\\begin{array}{c}\n\\text{下确界运算表:}\n\\end{array}\n\n`
      latexCode += `\\begin{array}{c}\n${result.subOperatorTable}\n\\end{array}\n\n`
    }

    // 补元信息
    if (result.complementInfos && result.complementInfos.length > 0) {
      latexCode += `\\begin{array}{c}\n\\text{补元信息:}\n\\end{array}\n\n`

      result.complementInfos.forEach((compInfo, idx) => {
        if (compInfo.hasComplement) {
          latexCode += `\\begin{array}{c}\n\\text{元素 ${compInfo.element} 的补元: ${compInfo.complements}}\n\\end{array}\n\n`
        } else {
          latexCode += `\\begin{array}{c}\n\\text{元素 ${compInfo.element} 没有补元}\n\\end{array}\n\n`
        }
      })
    }
  }

  return latexCode
}

// 辅助函数：获取性质中文名称
function getPropertyNameChinese(propertyType) {
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

// 辅助函数：获取关系中文名称
function getRelationNameChinese(relationType) {
  const names = {
    distributive: '分配律',
    absorption: '吸收律'
  }
  return names[relationType] || relationType
}