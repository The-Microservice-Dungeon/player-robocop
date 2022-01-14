import Vue from 'vue'
import VueRouter from 'vue-router'
import HomeView from '../views/HomeView.vue'
import ActuatorView from "@/views/ActuatorView";

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
]

const router = new VueRouter({
  routes
})

export default router
