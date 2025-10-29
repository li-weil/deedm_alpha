<template>
  <!-- 范式扩展结果显示组件 -->
  <div v-if="result.type === 'normal-form-expansion'" class="normal-form-expansion-results">
    <!-- 变量集信息 -->
    <div class="variable-info">
      <h4 class="result-title">扩展变量集：</h4>
      <span>{{ result.variableSet }}</span>
    </div>

    <!-- 扩展步骤 -->
    <div v-if="result.expansionSteps && result.expansionSteps.length > 0" class="expansion-steps">
      <h4 class="expansion-title">{{ result.targetType }}扩展步骤：</h4>
      <div
        v-for="(step, stepIndex) in result.expansionSteps"
        :key="'expansion-step-' + stepIndex"
        class="expansion-step-inline"
      >
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

  <!-- 等值演算检查结果显示组件 -->
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

  <!-- 推理有效性论证检查结果显示组件 -->
  <div v-if="result.type === 'reason-argument-check'" class="reason-argument-check-results">
    <div class="result-title">
      <strong>检查推理步骤</strong>
    </div>

    <!-- 推理关系显示 -->
    <div v-if="result.latexString" class="reasoning-overview">
      <h4>推理关系：</h4>
      <div class="reasoning-formula">
        <math-renderer
          :formula="result.latexString"
          :type="'katex'"
          :display-mode="true"
          class="formula-renderer"
        />
      </div>
    </div>

    <!-- 推理步骤显示 -->
    <div class="reasoning-steps">
      <div v-for="(step, index) in result.steps" :key="index" class="reasoning-step">
        <div class="step-content">
          <span class="step-number">({{ step.serialNo }})</span>
          <math-renderer
            :formula="step.formula"
            :type="'katex'"
            :display-mode="false"
            class="step-formula"
          />
          <span v-if="step.ruleName" class="step-rule"> // {{ step.prevSN }}{{ step.ruleName }}</span>
        </div>
      </div>
    </div>

    <!-- 检查过程显示 -->
    <div v-if="result.checkSteps && result.checkSteps.length > 0" class="check-process">
      <h4>检查过程：</h4>
      <div v-for="(step, index) in result.checkSteps" :key="index" class="check-step">
        <span class="check-text">检验步骤({{ step.serialNo }}){{ step.checkType }}</span>
        <math-renderer
          :formula="step.formula"
          :type="'katex'"
          :display-mode="false"
          class="check-formula"
        />
      </div>
    </div>

    <!-- 检查结果 -->
    <div class="check-result">
      <div v-if="result.valid" class="valid-result">
        <el-alert
          title="✓ 检查通过"
          type="success"
          description="通过真值表检验，上述推理证明过程没有错误。"
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
</template>

<script setup>
import MathRenderer from '@/components/common/MathRenderer.vue'

defineProps({
  result: {
    type: Object,
    required: true
  }
})
</script>

<style scoped>
/* 范式扩展结果样式 */
.normal-form-expansion-results {
  margin: 1rem 0;
  padding: 1.5rem;
  background: linear-gradient(135deg, #f6f9ff 0%, #e8f4fd 100%);
  border-radius: 8px;
  border: 2px solid #4a90e2;
  box-shadow: 0 4px 12px rgba(74, 144, 226, 0.15);
}

.variable-info {
  margin: 1rem 0;
  padding: 1rem;
  background: white;
  border-radius: 6px;
  border: 1px solid #d0e3ff;
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

/* 推理有效性论证检查结果样式 */
.reason-argument-check-results {
  margin: 1rem 0;
  padding: 1.5rem;
  background: linear-gradient(135deg, #f0f8ff 0%, #e6f3ff 100%);
  border-radius: 8px;
  border: 2px solid #4a90e2;
  box-shadow: 0 4px 12px rgba(74, 144, 226, 0.15);
}

.reasoning-overview {
  margin-bottom: 20px;
  padding: 15px;
  background: #f8f9ff;
  border-radius: 8px;
  border: 1px solid #4a90e2;
}

.reasoning-steps {
  margin: 1.5rem 0;
  padding: 1.5rem;
  background: #f0f8ff;
  border-radius: 8px;
  border: 2px solid #4a90e2;
}

.reasoning-step {
  margin-bottom: 0.75rem;
  font-family: 'Times New Roman', serif;
}

.reasoning-step:last-child {
  margin-bottom: 0;
}

.step-content {
  display: flex;
  align-items: center;
  gap: 10px;
}

.step-number {
  font-weight: bold;
  color: #409eff;
  min-width: 40px;
}

.step-rule {
  font-style: italic;
  color: #666;
  font-size: 14px;
}

.check-process {
  margin: 1.5rem 0;
  padding: 1rem;
  background: #fff9e6;
  border-radius: 8px;
  border: 1px solid #ffc107;
}

.check-step {
  display: flex;
  align-items: center;
  margin-bottom: 0.5rem;
  gap: 10px;
}

.check-text {
  font-weight: bold;
  color: #856404;
}

.check-formula {
  background: #fff;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  border: 1px solid #ddd;
}
</style>