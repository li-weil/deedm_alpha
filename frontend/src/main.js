import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'
import './assets/main.css'

const VISITOR_ID_KEY = 'deedm_visitor_id'

const generateVisitorId = () => {
  if (window.crypto && typeof window.crypto.randomUUID === 'function') {
    return window.crypto.randomUUID()
  }
  return `vid-${Date.now()}-${Math.random().toString(16).slice(2)}`
}

const getOrCreateVisitorId = () => {
  const cached = localStorage.getItem(VISITOR_ID_KEY)
  if (cached && cached.trim()) {
    return cached
  }

  const visitorId = generateVisitorId()
  localStorage.setItem(VISITOR_ID_KEY, visitorId)
  return visitorId
}

const installVisitorIdFetchInterceptor = () => {
  if (window.__deedmFetchPatched) {
    return
  }

  const originalFetch = window.fetch.bind(window)
  window.fetch = (input, init = {}) => {
    const visitorId = getOrCreateVisitorId()
    const request = new Request(input, init)
    const headers = new Headers(request.headers)
    headers.set('X-Visitor-Id', visitorId)

    return originalFetch(
      new Request(request, { headers })
    )
  }
  window.__deedmFetchPatched = true
}

installVisitorIdFetchInterceptor()

const app = createApp(App)
const pinia = createPinia()

// Register Element Plus icons
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(pinia)
app.use(router)
app.use(ElementPlus)

app.mount('#app')
