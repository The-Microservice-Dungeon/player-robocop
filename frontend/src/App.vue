<template>
  <div id="app">

    <template v-if="!isAuthenticated()">
      <authentication-view/>
    </template>

    <template v-else>
      <div id="nav">
        <router-link to="/">Home</router-link>
        |
        <router-link to="/game" class="dev">Game Manager</router-link>
        |
        <router-link to="/actuator" class="dev">Actuator</router-link>
        <a @click="end" class="logout">logout</a>
      </div>
      <router-view/>
    </template>
  </div>
</template>

<script>
import {mapGetters, mapMutations} from "vuex";
import AuthenticationView from "@/views/AuthenticationView";

export default {
  name: 'App',
  components: {AuthenticationView},
  computed: {
    ...mapGetters([
      'isAuthenticated',
    ])
  },
  methods: {
    ...mapMutations([
        'logout'
    ]),
    end () {
      this.logout()
    }
  }
}
</script>

<style lang="scss">
$baseTextColor: #fbfbfb;

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
  background: rgb(9,9,121);
  background: linear-gradient(180deg, rgba(9,9,121,1) 0%, rgba(86,75,102,1) 100%);
  height: 100%;
  overflow-y: auto;
  overflow-scrolling: touch;
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
      color: #f1ebff;
      text-decoration-color: #f600ff;
    }

    &.dev{
      opacity: 0.8;
      text-decoration-style: dotted;
      font-style: italic;
    }
  }

  .logout {
    float: right;
  }
}
</style>
