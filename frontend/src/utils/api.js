import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useLoadingStore } from '@/store/common/loadingStore'

// Create axios instance
// ==================================== BACKEND API CLIENT ====================================
const api = axios.create({
  // 使用绝对路径确保移动端也能正确访问
  baseURL: `${window.location.origin}/api`,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Request interceptor
api.interceptors.request.use(
  (config) => {
    // Add loading state if specified
    if (config.metadata?.loadingKey) {
      const loadingStore = useLoadingStore()
      loadingStore.setLoading(config.metadata.loadingKey, true)
    }

    // Add auth token if available
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }

    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor
api.interceptors.response.use(
  (response) => {
    // Remove loading state
    if (response.config.metadata?.loadingKey) {
      const loadingStore = useLoadingStore()
      loadingStore.setLoading(response.config.metadata.loadingKey, false)
    }

    return response.data
  },
  (error) => {
    // Remove loading state
    if (error.config?.metadata?.loadingKey) {
      const loadingStore = useLoadingStore()
      loadingStore.setLoading(error.config.metadata.loadingKey, false)
    }

    // Handle error responses
    let errorMessage = '请求失败，请稍后重试'

    if (error.response) {
      const { status, data } = error.response

      switch (status) {
        case 400:
          errorMessage = data.message || '请求参数错误'
          break
        case 401:
          errorMessage = '未授权，请重新登录'
          // Redirect to login if needed
          localStorage.removeItem('token')
          break
        case 403:
          errorMessage = '权限不足'
          break
        case 404:
          errorMessage = '请求的资源不存在'
          break
        case 500:
          errorMessage = '服务器内部错误'
          break
        default:
          errorMessage = data.message || `请求失败 (${status})`
      }
    } else if (error.code === 'ECONNABORTED') {
      errorMessage = '请求超时，请检查网络连接'
    } else if (error.message === 'Network Error') {
      errorMessage = '网络连接错误，请检查网络设置'
    }

    // Show error message
    ElMessage.error(errorMessage)

    return Promise.reject(error)
  }
)

// API utility functions
export const apiClient = {
  // GET request
  get: (url, config = {}) => {
    return api.get(url, config)
  },

  // POST request
  post: (url, data = {}, config = {}) => {
    return api.post(url, data, config)
  },

  // PUT request
  put: (url, data = {}, config = {}) => {
    return api.put(url, data, config)
  },

  // DELETE request
  delete: (url, config = {}) => {
    return api.delete(url, config)
  },

  // Request with loading indicator
  withLoading: (loadingKey) => {
    return {
      get: (url, config = {}) => {
        return api.get(url, {
          ...config,
          metadata: { ...config.metadata, loadingKey }
        })
      },
      post: (url, data = {}, config = {}) => {
        return api.post(url, data, {
          ...config,
          metadata: { ...config.metadata, loadingKey }
        })
      },
      put: (url, data = {}, config = {}) => {
        return api.put(url, data, {
          ...config,
          metadata: { ...config.metadata, loadingKey }
        })
      },
      delete: (url, config = {}) => {
        return api.delete(url, {
          ...config,
          metadata: { ...config.metadata, loadingKey }
        })
      }
    }
  }
}

// Specific API modules
// ==================================== BACKEND API ENDPOINTS ====================================
export const logicApi = {
  // Formula operations
  parseFormula: (formula, format = 'text') =>
    apiClient.post('/logic/formula/parse', { formula, format }),

  generateFormula: (variableCount, complexity) =>
    apiClient.post('/logic/formula/generate', { variableCount, complexity }),

  evaluateFormula: (formula, assignments) =>
    apiClient.post('/logic/formula/evaluate', { formula, assignments }),

  // Truth table operations
  generateTruthTable: (formula) =>
    apiClient.post('/logic/truth-table/generate', { formula }),

  checkFormulaType: (formulaId) =>
    apiClient.get(`/logic/truth-table/check-type/${formulaId}`),

  // Normal form operations
  convertToCNF: (formula) =>
    apiClient.post('/logic/normal-form/cnf', { formula }),

  convertToDNF: (formula) =>
    apiClient.post('/logic/normal-form/dnf', { formula })
}

// ==================================== COUNTING API ENDPOINTS ====================================
export const countingApi = {
  // String counting
  countStrings: (baseChars, length, filterType) =>
    apiClient.post('/counting/strings', { baseChars, length, filterType }),

  generateStrings: (baseChars, length, filterType) =>
    apiClient.post('/counting/strings/generate', { baseChars, length, filterType }),

  // Combination and permutation
  calculateCombination: (n, r, repetition = false) =>
    apiClient.post('/counting/combination', { n, r, repetition }),

  calculatePermutation: (n, r, repetition = false) =>
    apiClient.post('/counting/permutation', { n, r, repetition }),

  // Recurrence relations
  solveRecurrence: (recurrence, initialConditions) =>
    apiClient.post('/counting/recurrence', { recurrence, initialConditions })
}

// ==================================== GRAPH API ENDPOINTS ====================================
export const graphApi = {
  // Graph operations
  createGraph: (type, nodes, edges) =>
    apiClient.post('/graph/create', { type, nodes, edges }),

  findShortestPath: (graphId, start, end, algorithm = 'dijkstra') =>
    apiClient.post('/graph/shortest-path', { graphId, start, end, algorithm }),

  findSpanningTree: (graphId, algorithm = 'kruskal') =>
    apiClient.post('/graph/spanning-tree', { graphId, algorithm }),

  // Graph visualization
  getGraphLayout: (graphId, layout = 'force') =>
    apiClient.get(`/graph/layout/${graphId}?layout=${layout}`)
}

export default apiClient