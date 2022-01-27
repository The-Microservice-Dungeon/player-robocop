<template>
  <div class="infoCard">
    <h3>Player Info</h3>
    <bounce-loader
      v-if="loading"
      :color="'red'"
    />
    <template v-else-if="noData">
      <h4>No Player Data</h4>
    </template>
    <template v-else>
      <div class="infoWrapper">
        <span>
          Name: {{ player.name }}
        </span>
        <br>
        <span>
          Email: {{ player.email }}
        </span>
        <br>
        <span>
          robots: {{ player.robots }}
        </span>
        <br>
        <span>
          Money: {{ player.money }}
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
  name: 'PlayerInfo',
  components: {
    BounceLoader,
  },
  data () {
    return {
      player: undefined,
      loading: true,
      noData: false,
    }
  },
  mounted () {
    this.fetchPlayerData()

    EventBus.$on('player_money_set', () => {
      this.fetchPlayerData()
    })

    EventBus.$on('player_money_changed', () => {
      this.fetchPlayerData()
    })
  },
  methods: {
    fetchPlayerData () {
      apiGet('/player')
        .then((response) => {
          if (response.status !== 200) throw new Error('Unexpected Response ' + response.status)
          return response
        })
        .then(response => response.json())
        .then(response => {
          if (response) {
            this.player = response.player
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

</style>
