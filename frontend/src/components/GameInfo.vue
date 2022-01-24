<template>
  <div class="infoCard">
    <h3>Game Info</h3>
    <bounce-loader
      v-if="loading"
      :color="'red'"
    />
    <template v-else>
      <div class="infoWrapper">
        <span>
          Status: {{ game.status }}
        </span>
        <br>
        <span>
          Players: {{ game.playerCount }}
        </span>
        <br>
        <span>
          Max. Rounds: {{ game.maxRounds }}
        </span>
      </div>
      <h4>Current Round Info</h4>
      <div class="infoWrapper">
        <span>
          Current Round: {{ game.currentRound.roundNumber }}
        </span>
        <br>
        <span>
          Round Status: {{ game.currentRound.roundStatus }}
        </span><br>
        <span>
          Round Time: {{ game.currentRound.roundTime }}
        </span>
      </div>
    </template>
  </div>
</template>

<script>
import { apiGet } from '@/utils'
import BounceLoader from 'vue-spinner/src/BounceLoader.vue'

export default {
  name: 'GameInfo',
  components: {
    BounceLoader,
  },
  data () {
    return {
      game: undefined,
      loading: true,
    }
  },
  mounted () {
    this.fetchGameData()
  },
  methods: {
    fetchGameData () {
      apiGet('/game')
        .then(response => response.json())
        .then(response => {
          if (response) {
            this.game = response.game
            this.loading = false
          }
        })
    },
  },
}
</script>

<style scoped>

</style>
