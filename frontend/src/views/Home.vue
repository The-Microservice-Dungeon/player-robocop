<template>
  <div class="home">
    <img alt="Vue logo" src="../assets/logo.png">
  </div>
</template>

<script>

export default {
  name: 'Home',
  mounted() {
    console.log("mode is " + process.env.NODE_ENV )
    console.log(process.env.NODE_ENV === 'development')
  }
}
</script>
