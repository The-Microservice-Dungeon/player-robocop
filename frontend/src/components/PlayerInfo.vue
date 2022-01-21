<template>
  <div class="infoCard">
    <h3>Player Info</h3>
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
  </div>
</template>

<script>
export default {
  name: 'PlayerInfo',
  data () {
    return {
      player: {
        name: 'Test',
        email: 'TestMail',
        money: 200,
        robots: 1,
      },
    }
  },
}
</script>

<style scoped>

</style>
