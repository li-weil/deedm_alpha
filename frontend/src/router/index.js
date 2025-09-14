import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Main',
    component: () => import('@/views/MainView.vue'),
    meta: { title: 'Deedm - 数学教学辅助工具' }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/MainView.vue'),
    meta: { title: 'Deedm - 数学教学辅助工具' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

// Router guard for page titles
router.beforeEach((to, from, next) => {
  document.title = to.meta.title || 'Deedm - 数学教学辅助工具'
  next()
})

export default router