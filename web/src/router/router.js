import { createRouter, createWebHashHistory } from 'vue-router'
import LoginPage from '../views/LoginPage.vue'
import ClasificarPage from '@/views/ClasificarPage.vue'
import InicioPage from '@/views/InicioPage.vue'
import ValidarPage from '@/views/ValidarPage.vue'
import store from '@/store/store'
import ResultadosPage from '@/views/ResultadosPage.vue'

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
    path: '/resultados',
    name: 'Resultados',
    component: ResultadosPage
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
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const publicPages = ['/login'];
  const authRequired = !publicPages.includes(to.path);
  console.log('authRequired', store.getters.isAuthenticated);
  const loggedIn = store.getters.isAuthenticated;

  if (authRequired && !loggedIn) {
    console.log('No estás logueado');
    return next('/login');
  }
  next();
});

export default router
