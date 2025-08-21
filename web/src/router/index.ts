import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', component: () => import('@/pages/Home.vue'), meta: { title: '首页' } },
  { path: '/editor', component: () => import('@/pages/Editor.vue'), meta: { title: '行程编辑器' } },
  { path: '/discover', component: () => import('@/pages/Discover.vue'), meta: { title: '发现推荐' } },
  { path: '/ask', component: () => import('@/pages/Ask.vue'), meta: { title: '问答系统' } },
  { path: '/plan/:id', component: () => import('@/pages/PlanView.vue'), meta: { title: '行程' } },
]

const router = createRouter({ history: createWebHistory(), routes })
router.afterEach((to) => { document.title = (to.meta?.title as string) || 'YuManus' })
export default router


