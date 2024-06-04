import { createRouter, createWebHashHistory } from 'vue-router'
import LoginPage from '../views/LoginPage.vue'
import ClasificarPage from '@/views/ClasificarPage.vue'
import InicioPage from '@/views/InicioPage.vue'
import ValidarPage from '@/views/ValidarPage.vue'

const routes = [
  {
    path: '/clasificar',
    name: 'Clasificar',
    component: ClasificarPage
  },
  {
    path: '/validar',
    name: 'Validar',
    component: ValidarPage
  },
  {
    path: '/inicio',
    name: 'Inicio',
    component: InicioPage
  },
  {
    path: '/login',
    name: 'Login',
    component: LoginPage
  },
  // {
  //   path: '/about',
  //   name: 'about',
  //   // route level code-splitting
  //   // this generates a separate chunk (about.[hash].js) for this route
  //   // which is lazy-loaded when the route is visited.
  //   component: () => import(/* webpackChunkName: "about" */ '../views/AboutView.vue')
  // }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const publicPages = ['/login']
  const authRequired = !publicPages.includes(to.path)
  const loggedIn = localStorage.getItem('usuario')

  if (authRequired && !loggedIn) {
    return next('/login')
  }

  next()
})

export default router
