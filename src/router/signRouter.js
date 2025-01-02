const lgreRouters = [
  {
    path: '/',
    name: 'login',
    component: () => import('../views/LoginViews/login.vue')
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('../views/RegsiterViews/Register.vue')
  }
]

export default lgreRouters
