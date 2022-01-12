import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'
import Actuator from "@/views/Actuator";

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/actuator',
    name: 'Actuator',
    component: Actuator
  },
]

const router = new VueRouter({
  routes
})

export default router
