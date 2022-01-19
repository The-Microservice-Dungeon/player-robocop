<template>
  <div class="home">
    <img
      alt="Robocop logo"
      src="../assets/logo.webp"
    >
    <h1 style="margin-top: 0">
      Robocop Dashboard
    </h1>
    <div class="generalInfo">
      <player-info/>
      <game-info/>
    </div>
  </div>
</template>

<script>

import PlayerInfo from '@/components/PlayerInfo'
import GameInfo from '@/components/GameInfo'
export default {
  name: 'HomeView',
  components: { GameInfo, PlayerInfo },
}
</script>

<style scoped>
.generalInfo {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>

<style lang="scss">
.infoCard {
  border: 2px solid white;
  border-radius: 32px;
  padding: 8px;
  min-width: 45%;
  margin: 8px auto 0;
  background: #1a103a87;
  min-height: 116px;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;

  h3, h4 {
    margin: 0.5rem 0;
  }

  .infoWrapper {
    display: flex;
    justify-content: space-evenly;
    align-items: center;
    width: 100%;
  }
}
</style>
