const profileRoutes = [
  {
    path: '/profile',
    name: 'profile',
    component: () => import('../views/ProfileViews/profile.vue')
  }
]

export default profileRoutes
