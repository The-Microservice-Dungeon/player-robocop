<template>
  <div class="infoCard">
    <h3>Game Info</h3>
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
        currentRound: {
          roundNumber: 1,
          roundTime: new Date().getTime(),
          roundStatus: 'CREATED',
        },
      },
    }
  },
}
</script>

<style scoped>

</style>
