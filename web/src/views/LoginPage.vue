<template>
  <div class="container">
    <div class="formulario">
      <h1>Login</h1>
      <form @submit="login">
        <label for="username">Username:</label>
        <input type="text" id="username" v-model="username" required />
        <br />
        <label for="password">Password:</label>
        <input type="password" id="password" v-model="password" required />
        <br />
        <button type="submit">Login</button>
      </form>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      username: "",
      password: "",
    };
  },
  methods: {
    login() {
      // Perform login logic here
      // You can access the entered username and password using this.username and this.password
      this.$router.push({ name: "Inicio"});
    },
  },
};
</script>

<style scoped>
/* Add your custom styles here */
.container {
  max-width: 400px;
  margin: 0 auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 5px;
  background-color: #f9f9f9;
}

h1 {
  text-align: center;
  margin-bottom: 20px;
}

form {
  display: flex;
  flex-direction: column;
}

label {
  margin-bottom: 10px;
}

input {
  padding: 10px;
  margin-bottom: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

button {
  padding: 10px;
  background-color: #4caf50;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

button:hover {
  background-color: #45a049;
}
</style>