<template>
  <div class="home">
    <img
      alt="Robocop logo"
      src="../assets/logo.webp"
    >
    <h1 style="margin-top: 0">
      Robocop Dashboard
    </h1>
    <template v-if="!isMobile">
      <div class="infos">
        <div class="infoLeft">
          <div class="generalInfo">
            <player-info/>
            <game-info/>
          </div>
          <map-info/>
        </div>
        <div class="infoRight">
          <robot-info/>
        </div>
      </div>
    </template>
    <template v-else>
      <player-info/>
      <game-info/>
      <map-info :is-mobile="isMobile"/>
      <robot-info/>
    </template>
  </div>
</template>

<script>

import PlayerInfo from '@/components/PlayerInfo'
import GameInfo from '@/components/GameInfo'
import MapInfo from '@/components/MapInfo'
import RobotInfo from '@/components/RobotInfo'
import { isScreenMobile } from '@/utils'

export default {
  name: 'HomeView',
  components: { RobotInfo, MapInfo, GameInfo, PlayerInfo },
  data () {
    return {
      isMobile: isScreenMobile(),
    }
  },
  mounted () {
    window.addEventListener('resize', () => {
      this.isMobile = isScreenMobile()
    })
  },
}
</script>

<style scoped>
.infos {
  display: flex;
  justify-content: space-evenly;
  align-items: flex-start
}

.generalInfo {
  display: flex;
  justify-content: space-between;
  max-width: 90%;
  margin: 0 auto;
}

.infoLeft {
  min-width: 50%;
}

.infoRight {
  min-width: 25vw;
}
</style>

<style lang="scss">
.infoCard {
  border: 2px solid white;
  border-radius: 32px;
  padding: 8px;
  max-width: 90%;
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
    min-width: 20vw;
  }
}
</style>
