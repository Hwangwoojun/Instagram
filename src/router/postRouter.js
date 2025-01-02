const postRouters = [
  {
    path: '/home',
    name: 'home',
    component: () => import('../components/HomeViews/home.vue')
  }
]

export default postRouters
