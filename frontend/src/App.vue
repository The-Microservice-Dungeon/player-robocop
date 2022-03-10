<template>
  <div id="app">
    <template v-if="!isAuthenticated()">
      <authentication-view/>
    </template>

    <template v-else>
      <div id="nav">
        <a
          class="logout"
          @click="end"
        >logout</a>
        <router-link to="/">
          Home
        </router-link>
        |
        <router-link
          to="/game"
          class="dev"
        >
          Game Manager
        </router-link>
        |
        <router-link
          to="/actuator"
          class="dev"
        >
          Actuator
        </router-link>
      </div>
      <router-view
        :key="viewKey"
        class="view"
      />
    </template>
  </div>
</template>

<script>
import { mapGetters, mapMutations } from 'vuex'
import AuthenticationView from '@/views/AuthenticationView'
import SockJS from 'sockjs-client'
import Stomp from 'webstomp-client'
import { apiLink } from '@/utils'
import { EventBus } from '@/event-bus'
import Vue from 'vue'

export default {
  name: 'App',
  components: { AuthenticationView },
  data () {
    return {
      stompClient: undefined,
      connected: false,
      viewKey: 0,
    }
  },
  computed: {
    ...mapGetters([
      'isAuthenticated',
    ]),
  },
  mounted () {
    this.connectToWebsocket()

    EventBus.$on('game_created', () => {
      // force rerender the entire application
      this.viewKey++
    })
  },
  methods: {
    ...mapMutations([
      'logout',
    ]),
    end () {
      this.logout()
    },
    connectToWebsocket () {
      const options = { debug: false, heartbeat: { incoming: 10000, outgoing: 10000 }, protocols: ['v10.stomp', 'v11.stomp', 'v12.stomp'] }
      const socket = new SockJS(apiLink('/robocop-websocket').toString())
      this.stompClient = Stomp.over(socket, options)
      this.stompClient.connect(
          {},
          frame => {
            if (frame.command === 'CONNECTED') {
              this.connected = true
              this.stompClient.subscribe('game_events', tick => this.handleEvent(tick))
              this.stompClient.subscribe('player_events', tick => this.handleEvent(tick))
              this.stompClient.subscribe('map_events', tick => this.handleEvent(tick))
              this.stompClient.subscribe('errors', tick => this.handleErrorEvent(tick))
            }
          },
          error => {
            this.connected = false

            if (error.type === 'close') {
              console.info('Websocket connection closed')
            } else {
              console.error(error)
            }
          }
      )
    },
    handleEvent (tick) {
      let info = tick.body
      console.log('Event Received: ' + info)
      Vue.$toast.open('Received ' + info + ' Event!')
      EventBus.$emit(info)
    },
    handleErrorEvent (tick) {
      const messageParts = tick.body.split('|')
      console.log(messageParts[0] + ' error payload: ' + messageParts[1])
      Vue.$toast.error('Received ' + messageParts[0] + ' Error!')
    },
  },
}
</script>

<style scoped>
.view {
  padding-bottom: 16px;
}
</style>

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
  background: rgb(9, 9, 121);
  background: linear-gradient(180deg, rgba(9, 9, 121, 1) 0%, rgba(86, 75, 102, 1) 100%);
  height: 100%;
  overflow-y: auto;
  overflow-scrolling: touch;
}

#nav {
  padding: 30px;

  a {
    font-weight: bold;
    color: $baseTextColor;

    &:visited {
      color: $baseTextColor;
    }

    &.router-link-exact-active {
      color: #f1ebff;
      text-decoration-color: #f600ff;
    }

    &.dev {
      opacity: 0.8;
      text-decoration-style: dotted;
      font-style: italic;
    }
  }

  .logout {
    float: left;
  }
}
</style>
