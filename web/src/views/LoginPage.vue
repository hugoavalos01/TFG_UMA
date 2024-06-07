<template>
  <div>
    <NavbarTop :login="true" />
    <div class="container">
      <div class="login-box">
        <h2>Iniciar sesión</h2>
        <form @submit.prevent="login">
          <div class="user-box">
            <input type="text" v-model="username" required />
            <label>Usuario</label>
          </div>
          <div class="user-box">
            <input :type="passwordFieldType" v-model="password" required />
            <label>Contraseña</label>
            <span class="password-toggle-icon" @click="togglePasswordVisibility" v-if="isPasswordVisible">
              <i class="fas fa-eye"></i>
            </span>
            <span class="password-toggle-icon" @click="togglePasswordVisibility" v-else>
              <i class="fas fa-eye-slash"></i>
            </span>
          </div>
          <button class="submit">Acceder</button>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import NavbarTop from "@/components/NavbarTop.vue";
import authServices from "@/services/authService";

export default {
  components: { NavbarTop },
  computed: {
    isPasswordVisible() {
      return this.passwordFieldType === "password";
    },
  },
  data() {
    return {
      username: "",
      password: "",
      passwordFieldType: "password",
    };
  },
  methods: {
    togglePasswordVisibility() {
      this.passwordFieldType =
        this.passwordFieldType === "password" ? "text" : "password";
    },
    async login() {
      try {
        const response = await authServices.login(this.username, this.password);
        localStorage.setItem("token", response.token);
        this.$router.push({ name: "Inicio" });
      } catch (error) {
        console.error("Error en el inicio de sesión:", error.message);
        // Puedes mostrar un mensaje de error al usuario aquí si lo deseas
      }
    },
  },
};
</script>

<style scoped>
.container {
  padding: 20px;
  display: flex; /* Agregado */
  align-items: center; 
  justify-content: center;
}

h1 {
  text-align: center;
  margin-bottom: 20px;
}

.submit {
  padding: 10px;
  background-color: #4caf50;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.submit:hover {
  background-color: #45a049;
}

.login-box {
  background-color: #fff;
  padding: 20px;
  border-radius: 5px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  width: 20%;
}

.login-box h2 {
  margin: 0 0 15px;
  padding: 0;
  color: #333;
  text-align: center;
  text-transform: uppercase;
}

.user-box {
  position: relative;
  margin-bottom: 30px;
}

.user-box input {
  width: 100%;
  padding: 10px 0;
  font-size: 16px;
  color: #333;
  margin-bottom: 30px;
  border: none;
  border-bottom: 2px solid #333;
  outline: none;
  background: transparent;
}

.user-box label {
  position: absolute;
  top: 0;
  left: 0;
  padding: 10px 0;
  font-size: 16px;
  color: #333;
  pointer-events: none;
  transition: 0.5s;
}

.user-box input:focus ~ label,
.user-box input:valid ~ label {
  transform: translateY(-20px);
  font-size: 14px;
  color: #333;
}

a {
  display: inline-block;
  background-color: #333;
  color: #fff;
  padding: 10px 20px;
  font-size: 16px;
  text-transform: uppercase;
  text-decoration: none;
  position: relative;
  overflow: hidden;
  transition: 0.5s;
  letter-spacing: 2px;
  border-radius: 5px;
}

a:hover {
  background-color: #fff;
  color: #333;
  border: 1px solid #333;
}

a span {
  position: absolute;
  display: block;
}

a span:nth-child(1) {
  top: 0;
  left: -100%;
  width: 100%;
  height: 2px;
  background-color: #333;
  animation: animate1 1s linear infinite;
}

@keyframes animate1 {
  0% {
    left: -100%;
  }
  50%,
  100% {
    left: 100%;
  }
}

a span:nth-child(2) {
  top: -100%;
  right: 0;
  width: 2px;
  height: 100%;
  background-color: #333;
  animation: animate2 1s linear infinite;
  animation-delay: 0.25s;
}

@keyframes animate2 {
  0% {
    top: -100%;
  }
  50%,
  100% {
    top: 100%;
  }
}

a span:nth-child(3) {
  bottom: 0;
  right: -100%;
  width: 100%;
  height: 2px;
  background-color: #333;
  animation: animate3 1s linear infinite;
  animation-delay: 0.5s;
}

@keyframes animate3 {
  0% {
    right: -100%;
  }
  50%,
  100% {
    right: 100%;
  }
}

a span:nth-child(4) {
  bottom: -100%;
  left: 0;
  width: 2px;
  height: 100%;
  background-color: #333;
  animation: animate4 1s linear infinite;
  animation-delay: 0.75s;
}

@keyframes animate4 {
  0% {
    bottom: -100%;
  }
  50%,
  100% {
    bottom: 100%;
  }
}

.password-toggle-icon {
  position: absolute;
  top: 50%;
  right: 10px;
  transform: translateY(-100%);
  cursor: pointer;
}

.password-toggle-icon i {
  font-size: 18px;
  line-height: 1;
  color: #333;
  transition: color 0.3s ease-in-out;
  margin-bottom: 20px;
}

.password-toggle-icon:hover {
  color: #000;
}
</style>