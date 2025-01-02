// queueRouter.js
const queueRoutes = [
  {
    path: '/queue', // 대기열 페이지 경로
    name: 'queue', // 대기열 페이지의 이름
    component: () => import('../components/QueueComponents/Queue.vue') // 대기열 페이지 컴포넌트
  }
]

export default queueRoutes
