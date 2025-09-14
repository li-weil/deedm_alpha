<template>
  <div class="home">
    <el-container>
      <el-header>
        <el-row justify="space-between" align="middle">
          <el-col :span="8">
            <h1 class="app-title">
              <el-icon><DataLine /></el-icon>
              Deedm
            </h1>
          </el-col>
          <el-col :span="8">
            <el-menu
              mode="horizontal"
              :default-active="activeRoute"
              class="main-nav"
              router
            >
              <el-menu-item index="/logic">命题逻辑</el-menu-item>
              <el-menu-item index="/counting">组合计数</el-menu-item>
              <el-menu-item index="/graph">图论</el-menu-item>
              <el-menu-item index="/algebra">代数</el-menu-item>
            </el-menu>
          </el-col>
          <el-col :span="8" class="text-right">
            <el-switch
              v-model="isDarkMode"
              inline-prompt
              active-text="🌙"
              inactive-text="☀️"
              @change="toggleDarkMode"
            />
          </el-col>
        </el-row>
      </el-header>

      <el-main>
        <div class="hero-section">
          <h2 class="hero-title">数学教学辅助工具</h2>
          <p class="hero-subtitle">基于Vue+SpringBoot的在线数学学习平台</p>
        </div>

        <div class="features-grid">
          <el-row :gutter="20">
            <el-col :span="6" v-for="feature in features" :key="feature.title">
              <el-card class="feature-card" @click="navigateTo(feature.route)">
                <el-icon size="40" :class="feature.icon">
                  <component :is="feature.icon" />
                </el-icon>
                <h3>{{ feature.title }}</h3>
                <p>{{ feature.description }}</p>
              </el-card>
            </el-col>
          </el-row>
        </div>

        <div class="quick-actions">
          <h3>快速开始</h3>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-button type="primary" size="large" @click="navigateTo('/logic/formula-builder')">
                构建逻辑公式
              </el-button>
            </el-col>
            <el-col :span="8">
              <el-button type="success" size="large" @click="navigateTo('/counting/string-counter')">
                字符串计数
              </el-button>
            </el-col>
            <el-col :span="8">
              <el-button type="warning" size="large" @click="navigateTo('/graph')">
                图论可视化
              </el-button>
            </el-col>
          </el-row>
        </div>
      </el-main>

      <el-footer>
        <p>&copy; 2024 Deedm. 基于 Vue 3 + Spring Boot 构建</p>
      </el-footer>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  DataLine,
  Connection,
  Calculate,
  Share,
  Operation,
  Grid
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const isDarkMode = ref(false)

const activeRoute = computed(() => route.path)

const features = [
  {
    title: '命题逻辑',
    description: '公式构建、真值表生成、逻辑推理',
    icon: 'Connection',
    route: '/logic'
  },
  {
    title: '组合计数',
    description: '排列组合、字符串计数、递推关系',
    icon: 'Calculate',
    route: '/counting'
  },
  {
    title: '图论',
    description: '图算法、最短路径、生成树',
    icon: 'Share',
    route: '/graph'
  },
  {
    title: '代数',
    description: '群论、环论、域论',
    icon: 'Operation',
    route: '/algebra'
  }
]

const navigateTo = (path) => {
  router.push(path)
}

const toggleDarkMode = (value) => {
  if (value) {
    document.documentElement.classList.add('dark')
  } else {
    document.documentElement.classList.remove('dark')
  }
}
</script>

<style scoped>
.home {
  min-height: 100vh;
}

.el-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 1rem 2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.app-title {
  font-size: 1.8rem;
  font-weight: 700;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.main-nav {
  background: none;
  border: none;
}

.main-nav :deep(.el-menu-item) {
  color: white;
  border-bottom: 2px solid transparent;
}

.main-nav :deep(.el-menu-item:hover),
.main-nav :deep(.el-menu-item.is-active) {
  background: rgba(255, 255, 255, 0.1);
  border-bottom-color: white;
}

.hero-section {
  text-align: center;
  padding: 4rem 2rem;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  margin: -2rem -2rem 2rem -2rem;
}

.hero-title {
  font-size: 3rem;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 1rem;
}

.hero-subtitle {
  font-size: 1.2rem;
  color: #5a6c7d;
  margin: 0;
}

.features-grid {
  margin: 3rem 0;
}

.feature-card {
  text-align: center;
  padding: 2rem;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #e4e7ed;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  border-color: #409eff;
}

.feature-card h3 {
  margin: 1rem 0 0.5rem;
  color: #2c3e50;
}

.feature-card p {
  color: #5a6c7d;
  font-size: 0.9rem;
  line-height: 1.5;
}

.feature-card .el-icon {
  color: #409eff;
  margin-bottom: 1rem;
}

.quick-actions {
  text-align: center;
  margin: 3rem 0;
}

.quick-actions h3 {
  margin-bottom: 2rem;
  color: #2c3e50;
}

.el-footer {
  text-align: center;
  padding: 2rem;
  background: #f8f9fa;
  border-top: 1px solid #e4e7ed;
  color: #5a6c7d;
}

/* Responsive design */
@media (max-width: 768px) {
  .el-header {
    padding: 1rem;
  }

  .app-title {
    font-size: 1.5rem;
  }

  .hero-title {
    font-size: 2rem;
  }

  .features-grid {
    margin: 2rem 0;
  }

  .feature-card {
    margin-bottom: 1rem;
  }

  .el-menu-item {
    font-size: 0.9rem;
    padding: 0 1rem;
  }
}
</style>