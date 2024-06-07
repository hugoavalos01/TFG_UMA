<template>
  <nav class="navbar">
    <div class="navbar-logo">
      <router-link to="/" class="navbar-brand">TFG</router-link>
    </div>
    <div class="navbar-links" v-if="!login">
      <router-link to="/inicio">Inicio</router-link>
      <router-link to="/clasificar">Clasificar</router-link>
      <router-link to="/validar">Validar</router-link>
    </div>
    <div class="navbar-user" v-if="!login">
      <div class="dropdown">
        <a style="color: white; margin-right: 10px;">{{ username }}</a>
        <button class="dropbtn">
          <i class="fas fa-user"></i>
        </button>
        <div class="dropdown-content">
          <a @click="logout">Cerrar sesión</a>
        </div>
      </div>
    </div>
  </nav>
</template>

<script>
export default {
  props: {
    login: {
      type: Boolean,
      default: false,
    },
  },
  mounted() {
    this.username = localStorage.getItem("username");
  },
  data() {
    return {
      username: "",
    };
  },
  methods: {
    logout() {
      console.log("Sesión cerrada exitosamente");
      this.$store.commit("CLEAR_TOKEN");
      localStorage.removeItem("token");
      localStorage.removeItem("username");
      this.$router.push("/login");
    },
  },
};
</script>

<style scoped>
body {
  font-family: Arial, sans-serif;
  margin: 0;
  padding: 0;
}

.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #333;
  padding: 10px 20px;
  position: fixed;
  width: 100%;
  top: 0;
  left: 0; /* Asegúrate de que el navbar se alinee a la izquierda */
  box-sizing: border-box; /* Incluye el padding y el border en el ancho total */
  z-index: 1000;
}

.navbar-logo .navbar-brand {
  color: white;
  text-decoration: none;
  font-size: 24px;
}

.navbar-links a {
  color: white;
  text-decoration: none;
  padding: 0 15px;
  display: inline-block;
}

.navbar-links a:hover {
  background-color: #575757;
  border-radius: 5px;
}

.navbar-user {
  position: relative;
}

.dropdown {
  position: relative;
  display: inline-block;
}

.dropbtn {
  background-color: #333;
  color: white;
  border: none;
  cursor: pointer;
  font-size: 20px;
}

.dropdown-content {
  display: none;
  position: absolute;
  background-color: white;
  box-shadow: 0px 8px 16px 0px rgba(0, 0, 0, 0.2);
  right: 0;
  z-index: 1;
  width: 130px

}

.dropdown-content a {
  color: black;
  padding: 12px 16px;
  text-decoration: none;
  display: block;
}

.dropdown-content a:hover {
  background-color: #ddd;
}

.dropdown:hover .dropdown-content {
  display: block;
}

.dropdown:hover .dropbtn {
  background-color: #575757;
}
</style>
