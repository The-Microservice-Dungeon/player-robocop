import Vue from 'vue'
import VueRouter from 'vue-router'
import HomeView from '../views/HomeView.vue'
import ActuatorView from "@/views/ActuatorView";
import DebugGameManager from "@/views/DebugGameManager";

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Home',
    component: HomeView
  },
  {
    path: '/actuator',
    name: 'Actuator',
    component: ActuatorView
  },
  {
    path: '/game',
    name: 'Game Manager',
    component: DebugGameManager
  },
]

const router = new VueRouter({
  routes
})

export default router
