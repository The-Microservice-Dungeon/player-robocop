import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from "@/store/store";

Vue.config.productionTip = false

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
  render: h => h(App)
}).$mount('#app')
