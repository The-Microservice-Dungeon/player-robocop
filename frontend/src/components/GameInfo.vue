<template>
  <div class="infoCard">
    <h3>Game Info</h3>
    <bounce-loader
      v-if="loading"
      :color="'red'"
    />
    <template v-else-if="noData">
      <h4>No Game data</h4>
    </template>
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
        </span>
      </div>
    </template>
  </div>
</template>

<script>
import { apiGet } from '@/utils'
import BounceLoader from 'vue-spinner/src/BounceLoader.vue'
import { EventBus } from '@/event-bus'

export default {
  name: 'GameInfo',
  components: {
    BounceLoader,
  },
  data () {
    return {
      game: undefined,
      loading: true,
      noData: false,
    }
  },
  mounted () {
    this.fetchGameData()

    EventBus.$on('round_status_change', () => {
      this.fetchGameData()
    })

    EventBus.$on('game_status_change', () => {
      this.fetchGameData()
    })

    EventBus.$on('game_created', () => {
      this.fetchGameData()
    })
  },
  methods: {
    fetchGameData () {
      this.loading = true
      this.noData = false
      apiGet('/game')
        .then((response) => {
          if (response.status !== 200) throw new Error('No game found' + response.status)
          return response
        })
        .then(response => response.json())
        .then(response => {
          if (response) {
            this.game = response.game
            this.loading = false
          }
        })
      .catch(e => {
        this.noData = true
        this.loading = false
        console.warn(e)
      })
    },
  },
}
</script>

<style scoped>
.infoCard {
  width: 50%;
  height: 13rem;
}
</style>
