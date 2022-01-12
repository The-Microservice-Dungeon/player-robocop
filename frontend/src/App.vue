<template>
  <div id="app">
    <div id="nav">
      <router-link to="/">Home</router-link> |
      <router-link to="/map">Map</router-link> |
      <router-link to="/actuator" class="dev">Actuator</router-link> |
    </div>
    <router-view/>
  </div>
</template>

<style lang="scss">
$baseTextColor: #2c3e50;

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: $baseTextColor;
  height: 100%;
}

html, body {
  padding: 0;
  margin: 0;
}

#nav {
  padding: 30px;

  a {
    font-weight: bold;
    color: $baseTextColor;

    &:visited{
      color: $baseTextColor;
    }

    &.router-link-exact-active{
      color: #42b983;
    }

    &.dev{
      opacity: 0.8;
      text-decoration-style: dotted;
      font-style: italic;
    }
  }
}
</style>
