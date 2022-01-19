<template>
  <div class="infoCard">
    <h3>Game Info</h3>
    <div class="infoWrapper">
      <span>
        Status: {{ game.status }}
      </span>
      <span>
        Players: {{ game.playerCount }}
      </span>
      <span>
        Max. Rounds: {{ game.maxRounds }}
      </span>
    </div>
    <h4>Current Round Info</h4>
    <div class="infoWrapper">
      <span>
        Current Round: {{ round.currentRound }}
      </span>
      <span>
        Round Time: {{ round.roundTime }}
      </span>
    </div>
  </div>
</template>

<script>
export default {
  name: 'GameInfo',
  data () {
    return {
      game: {
        status: 'created',
        playerCount: 1,
        maxRounds: 100,
      },
      round: {
        currentRound: 1,
        roundTime: new Date().getTime(),
      },
    }
  },
}
</script>

<style scoped>

</style>
