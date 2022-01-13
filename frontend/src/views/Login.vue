<template>
  <div>
    <img alt="Vue logo" src="../assets/logo_angry.webp">
    <h1>Authenticate yourself to Robocop Or DIE!</h1>
    <form>
      <input v-model="password" type="password">
      <button @click="authenticate">Authenticate</button>
    </form>
  </div>
</template>

<script>
import {apiCall} from "@/utils";
import { mapMutations} from "vuex";

export default {
  name: "Login",
  data () {
    return {
      password: undefined
    }
  },
  methods: {
    ...mapMutations([
      'login',
    ]),
    authenticate (e) {
      if (e) e.preventDefault()
      
      apiCall('POST', '/authentication', {
        password: this.password,
      })
      .then(response => {
        if (response.status === 200) this.login()
        else throw new Error('' + response.status)
      })
    }
  }
}
</script>

<style scoped>

</style>