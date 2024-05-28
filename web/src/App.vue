<template>
  <nav>
    <router-link to="/inicio">Inicio</router-link> |
    <router-link to="/inicio">About</router-link>
  </nav>
  <router-view/>
</template>

<script>
export default {
  created() {
    console.log('App created');
    this.$router.push({ path: '/inicio' })
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

nav {
  padding: 30px;
}

nav a {
  font-weight: bold;
  color: #2c3e50;
}

nav a.router-link-exact-active {
  color: #42b983;
}
</style>
