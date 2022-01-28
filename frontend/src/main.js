import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from '@/store/store'
import VueToast from 'vue-toast-notification'
import 'vue-toast-notification/dist/theme-sugar.css'

Vue.config.productionTip = false

Vue.use(VueToast, {
  position: 'top-right',
  type: 'default',
})

store.commit('initialiseStore')

window.onbeforeunload = () => {
  if (store.getters.isAuthenticated) {
    try {
      localStorage.setItem('store', store.getters.stringify())
    } catch (error) {
      console.error(error)
    }
  }
}

new Vue({
  router,
  store,
  render: h => h(App),
}).$mount('#app')
