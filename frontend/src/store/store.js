import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

const storeVersion = 1

export default new Vuex.Store({
    state: {
        authenticated: false
    },
    getters: {
        isAuthenticated: state => () => {
            return state.authenticated
        },
        stringify: state => () => {
            return JSON.stringify(state)
        },
    },
    mutations: {
        initialiseStore (state) {
            if (localStorage.getItem('store')) {
                let store = JSON.parse(localStorage.getItem('store'))
                if (store.version === storeVersion) {
                    this.replaceState(Object.assign(state, store))
                } else {
                    state.version = storeVersion
                }
            }
        },
        login (state) {
            state.authenticated = true
        },
        logout (state) {
            state.authenticated = false
        }
    },
    actions: {},
})