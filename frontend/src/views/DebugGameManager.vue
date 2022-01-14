<template>
  <div>
    <h1>Manage the Game Service here</h1>
    <h2 style="color: red">ONLY USE THIS WITH THE LOCAL DEV ENVIRONMENT!</h2>
    <form>
      <input type="number" v-model="maxRounds" placeholder="maxRounds">
      <input type="number" v-model="maxPlayers" placeholder="maxPlayers">
      <button @click="createNewGame">Create new Game</button>
    </form>
    <div>
      <h5>Current Games</h5>
      <div v-text="games"></div>
    </div>
  </div>
</template>

<script>
import {apiGet, apiPost} from "@/utils";

export default {
  name: "DebugGameManager",
  data () {
    return {
      games: [],
      maxRounds: 1337,
      maxPlayers: 1,
    }
  },
  mounted() {
    this.fetchGames()
  },
  methods: {
    fetchGames () {
      apiGet('games')
      .then(response => response.json())
      .then(response => {
        this.games = response._embedded.games
      })
    },
    createNewGame(e) {
      if (e) e.preventDefault()
      console.log(this.maxRounds)
      console.log(this.maxPlayers)

      const params = {
        maxRounds: this.maxRounds,
        maxPlayers: this.maxPlayers
      }

      apiPost('/games/create', params)
      .then(response => response.text())
      .then(response => {
        console.log(response)
      })

      this.fetchGames()
    }
  }
}
</script>

<style scoped>

</style>