import { createRouter, createWebHistory } from 'vue-router'
import auth from '@/router/middleware/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // {
    //   path: '/:pathMatch(.*)*',
    //   redirect: '/404',
    // },
    // {
    //   path: '/404',
    //   name: '404',
    //   meta: { layout: 'error', roles: ['ADMIN', 'USER'] },
    //   component: () => import('@/views/auth/404.vue'),
    // },
    {
      path: '/login',
      name: 'login',
      meta: { layout: 'auth', anonymousCallable: true, roles: [] },
      component: () => import('@/views/auth/LoginView.vue'),
    },
    {
      path: '/signup',
      name: 'signUp',
      meta: { layout: 'auth', anonymousCallable: true, roles: [] },
      component: () => import('@/views/auth/RegisterView.vue'),
    },
    {
      path: '/home',
      name: 'home',
      meta: { layout: 'default', roles: ['USER'] },
      component: () => import('@/views/HomeView.vue'),
    },
    {
      path: '/routes/user/list',
      name: 'userList',
      meta: { layout: 'default', roles: ['ADMIN'] },
      component: () => import('@/views/routes/user/list.vue'),
    },
    {
      path: '/routes/user/:id',
      name: 'userIndex',
      meta: { layout: 'default', roles: ['ADMIN'] },
      component: () => import('@/views/routes/user/_id.vue'),
    },
    {
      path: '/routes/shop/list',
      name: 'shopList',
      meta: { layout: 'default', roles: ['ADMIN'] },
      component: () => import('@/views/routes/shop/list.vue'),
    },
    {
      path: '/routes/shop/:id',
      name: 'shopIndex',
      meta: { layout: 'default', roles: ['ADMIN'] },
      component: () => import('@/views/routes/shop/_id.vue'),
    },
    {
      path: '/routes/menu/list',
      name: 'menuList',
      meta: { layout: 'default', roles: ['ADMIN'] },
      component: () => import('@/views/routes/menu/list.vue'),
    },
    {
      path: '/routes/menu/:id',
      name: 'menuIndex',
      meta: { layout: 'default', roles: ['ADMIN'] },
      component: () => import('@/views/routes/menu/_id.vue'),
    },
    {
      path: '/routes/review/list',
      name: 'reviewList',
      meta: { layout: 'default', roles: ['ADMIN'] },
      component: () => import('@/views/routes/review/list.vue'),
    },
    {
      path: '/routes/review/:id',
      name: 'reviewIndex',
      meta: { layout: 'default', roles: ['ADMIN'] },
      component: () => import('@/views/routes/review/_id.vue'),
    },
    {
      path: '/routes/reserve/list',
      name: 'reserveList',
      meta: { layout: 'default', roles: ['ADMIN'] },
      component: () => import('@/views/routes/reserve/list.vue'),
    },
  ],
})

/** 전역 네비게이션 가드 */
router.beforeEach(auth)

export default router
