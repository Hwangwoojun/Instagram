const chatRouters = [
  {
    path: '/chat',
    name: 'chat',
    component: () => import('../views/ChatView/Chat.vue')
  }
]

export default chatRouters
