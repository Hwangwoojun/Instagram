import { createRouter, createWebHistory } from 'vue-router'
import postRouters from './postRouter'
import lgreRouters from './signRouter'
import profileRoutes from './postRouter'

// 모든 라우터를 하나의 배열로 통합
const routes = [...postRouters, ...lgreRouters, ...profileRoutes]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes // 통합된 라우트 배열 사용
})

export default router
