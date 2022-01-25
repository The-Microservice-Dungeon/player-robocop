<template>
  <div class="infoCard">
    <h3>Player Info</h3>
    <bounce-loader
      v-if="loading"
      :color="'red'"
    />
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

export default {
  name: 'PlayerInfo',
  components: {
    BounceLoader,
  },
  data () {
    return {
      player: undefined,
      loading: true,
    }
  },
  mounted () {
    this.fetchPlayerData()
  },
  methods: {
    fetchPlayerData () {
      apiGet('/player')
        .then(response => response.json())
        .then(response => {
          if (response) {
            this.player = response.player
            this.loading = false
          }
        })
    },
  },
}
</script>

<style scoped>

</style>
