<template>
  <div>
    <input v-model="path">
    <button @click="fetchActuatorData">Update</button>
    <pre class="jsonOut">{{ response | pretty }}</pre>
  </div>
</template>

<script>
export default {
  name: "Actuator",
  data () {
    return {
      response: '',
      path: '/actuator'
    }
  },
  mounted() {
    this.fetchActuatorData()
  },
  methods: {
    fetchActuatorData () {
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
            console.log(result)
            this.response = result
          })
    }
  },
  filters: {
    pretty: function (value) {
      try {
        return JSON.stringify(value, null, 2);
      }
      catch (e) {
        console.warn(e)
        return value
      }
    }
  }
}
</script>

<style scoped>
.jsonOut {
  width: 50%;
  text-align: left;
  margin: 0 auto;
}
</style>
