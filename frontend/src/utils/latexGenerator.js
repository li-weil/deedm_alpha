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
  if (result.nodesString && result.edgesString) {
    latexCode += `\\begin{array}{c}\n\\text{图遍历分析结果:}\n\\end{array}\n\n`

    // 显示图的基本信息
    if (result.formula) {
      latexCode += `\\begin{array}{c}\n\\text{图的基本信息:} ${result.formula}\n\\end{array}\n\n`
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

    // 显示图形可视化信息
    if (result.graphImageUrl) {
      latexCode += `\\begin{array}{c}\n\\text{图形可视化已生成 (图片路径: ${result.graphImageUrl})}\n\\end{array}\n\n`
    }
  }

  // 处理树遍历结果
  if (result.rootNode) {
    latexCode += `\\begin{array}{c}\n\\text{树遍历分析结果:}\n\\end{array}\n\n`

    // 显示树的基本信息
    if (result.formula) {
      latexCode += `\\begin{array}{c}\n\\text{树的基本信息:} ${result.formula}\n\\end{array}\n\n`
    }

    // 显示根节点信息
    if (result.rootNode) {
      latexCode += `\\begin{array}{c}\n\\text{根节点:} ${result.rootNode}\n\\end{array}\n\n`
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

    // 显示树形可视化信息
    if (result.graphImageUrl) {
      latexCode += `\\begin{array}{c}\n\\text{树形可视化已生成 (图片路径: ${result.graphImageUrl})}\n\\end{array}\n\n`
    }
  }

  return latexCode
}