<template>
  <div>
    <img alt="Vue logo" src="../assets/logo_angry.webp">
    <h1 style="margin-bottom: 0">Authenticate yourself to Robocop Or DIE!</h1>
    <small>Disclaimer: not authenticating won't actually kill you. You will just be thrown into a tiny cell for the rest of your life.</small>
    <form style="margin-top: 2rem">
      <input v-model="password" type="password">
      <button @click="authenticate">Authenticate</button>
    </form>
  </div>
</template>

<script>
import { apiGet } from "@/utils";
import { mapMutations } from "vuex";

export default {
  name: "AuthenticationView",
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

      apiGet('/authentication', {
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