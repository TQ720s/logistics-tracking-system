import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: () => import('./views/Login.vue') },
  { path: '/user', component: () => import('./views/user/UserHome.vue') },
  { path: '/courier', component: () => import('./views/courier/CourierHome.vue') },
  { path: '/admin', component: () => import('./views/admin/AdminHome.vue') },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router 