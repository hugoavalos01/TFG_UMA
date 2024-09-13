<template>
  <div id="app">
    <div class="content">
      <router-view/>
    </div>
  </div>
</template>

<script>

export default {
  created() {
    console.log('App created');
    this.$router.push({ path: '/login' })
  },
  components: {
    
  }
}
</script>

<style>
@import '@fortawesome/fontawesome-free/css/all.css';

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

.content {
  margin-top: 60px;
}
</style>
