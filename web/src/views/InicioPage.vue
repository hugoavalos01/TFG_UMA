<template>
  <Navbar />
  <div>
    <div>
    </div>
    <div class="intro-text">
      <p>
        <br/>
        Esta aplicación le permite subir imágenes de animales y clasificarlas automáticamente en diferentes categorías utilizando algoritmos de IA.
      </p>
      <p>
        Para comenzar, haga clic en el botón "Clasificar" en la barra de navegación.
      </p>
      <p>
        Puede subir imágenes individuales o un archivo ZIP comprimido con varias imágenes para una clasificación eficiente.
        <br/>Además, tendrá la opción de validar las clasificaciones realizadas por la IA, asegurando la precisión y mejorando continuamente el modelo.
      </p>
    </div>
    <div class="image-comparison">
      <div class="image-container">
        <p><b>Antes:</b></p>
        <img
          :src="ejemploSinClasificar"
          alt="Imagen sin clasificar"
          class="imagen-ejemplo"
        />
      </div>
      <div class="image-container">
        <p><b>Después:</b></p>
        <img
          :src="ejemploClasificado"
          alt="Imagen clasificada"
          class="imagen-ejemplo"
        />
      </div>
    </div>
  </div>
</template>

<script>
import Navbar from "@/components/NavbarTop.vue";
import ejemploClasificado from "../assets/ejemploClasificado.jpg";
import ejemploSinClasificar from "../assets/ejemploSinClasificar.jpg";

export default {
  components: { Navbar },
  data() {
    return {
      ejemploClasificado,
      ejemploSinClasificar
    };
  }
};
</script>

<style>
.intro-text {
  margin: 20px;
  line-height: 1.6;
  text-align: justify;
}

.intro-text p {
  margin-top: 10px;
}

.image-comparison {
  display: flex;
  justify-content: space-around;
  align-items: center;
  margin-top: 20px;
}

.image-container {
  text-align: center;
}

.imagen-ejemplo {
  max-width:90%;
  height: auto;
}
</style>
