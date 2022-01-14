<template>
  <div>
    <div>
      <form>
        <input v-model="path">
        <button @click="fetchActuatorData">Update</button>
      </form>
    </div>
    <div class="outWrapper">
      <pre class="out">{{ response | pretty }}</pre>
    </div>
  </div>
</template>

<script>
export default {
  name: "ActuatorView",
  data () {
    return {
      response: undefined,
      path: '/actuator/logs'
    }
  },
  mounted() {
    this.fetchActuatorData()
  },
  methods: {
    fetchActuatorData (e) {
      if (e) e.preventDefault()
      fetch(this.path)
          .then(res => res.text())
          .then(body => {
            try {
              return JSON.parse(body);
            } catch {
              return body
            }
          })
          .then(result => {
            this.response = result
          })
    }
  },
  filters: {
    pretty: function (value) {
      if (typeof value === 'object') return JSON.stringify(value, null, 2);
      else return value
    }
  }
}
</script>

<style scoped>
.outWrapper{
  display: flex;
  justify-content: center;
  align-items: center;
}

.out {
  width: auto;
  max-width: 100%;
  overflow-x: auto;
  text-align: left;
}
</style>
