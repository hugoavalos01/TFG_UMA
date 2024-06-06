<template>
  <div>
    <Navbar />
    <h1>Validar</h1>
    <div v-if="imagenesConAnotaciones.length > 0">
      <h2>Imágenes Clasificadas</h2>
      <div class="carousel">
        <button @click="prevImage" class="carousel-button prev-button">&#9664;</button>
        <div class="image-wrapper">
          <img :src="imagenesConAnotacionActual.imagen" :alt="'Imagen ' + (currentImageIndex + 1)" class="imagen-grande">
          <p>Clasificación de la IA: {{ imagenesConAnotacionActual.anotacion }}</p>
          <div class="button-container">
            <button class="validar-button">Validar</button>
            <button class="corregir-button">Corregir</button>
          </div>
        </div>
        <button @click="nextImage" class="carousel-button next-button">&#9654;</button>
      </div>
    </div>
    <div v-else>
      <p>No hay imágenes clasificadas disponibles.</p>
    </div>
  </div>
</template>


<script>
import Navbar from '@/components/NavbarTop.vue'
import uploadService from '@/services/uploadService.js'

export default {
  components: { Navbar },
  data() {
    return {
      imagenesConAnotaciones: [],
      infoImagenes: [],
      imagenes: [],
      currentImageIndex: 0
    }
  },
  computed: {
    imagenesConAnotacionActual() {
      return this.imagenesConAnotaciones[this.currentImageIndex] || {};
    }
  },
  mounted() {
    console.log('ValidarPage mounted')
    this.cargarInfoImagenes()
    this.cargarImagenes()
  },
  methods: {
    cargarImagenes() {
      uploadService.getImagenes().then((response) => {
        this.imagenes = response.data;
        console.log('Imágenes cargadas:', this.imagenes);
        
        // Asociar cada imagen con su anotación correspondiente
        this.imagenesConAnotaciones = this.imagenes.map(imagen => {
          const anotacion = this.infoImagenes.find(info => info.pathMinIO === imagen.fileName)?.anotaciones || 'Anotación no disponible';
          return { imagen: imagen.url, anotacion: anotacion };
        });
        
        console.log('Imágenes con anotaciones:', this.imagenesConAnotaciones);
      }).catch((error) => {
        console.error('Error al cargar las imágenes:', error);
      })
    },
    cargarInfoImagenes() {
      uploadService.getInfoImagenes().then((response) => {
        this.infoImagenes = response.data;
        console.log('Información de imágenes cargada:', this.infoImagenes);
      }).catch((error) => {
        console.error('Error al cargar la información de las imágenes:', error);
      })
    },
    nextImage() {
      if (this.currentImageIndex < this.imagenesConAnotaciones.length - 1) {
        this.currentImageIndex++;
      }
    },
    prevImage() {
      if (this.currentImageIndex > 0) {
        this.currentImageIndex--;
      }
    }
  }
}
</script>

<style>
.carousel {
  display: flex;
  align-items: center;
  justify-content: center;
}

.carousel-button {
  background-color: transparent;
  border: none;
  font-size: 2em;
  cursor: pointer;
}

.image-wrapper {
  flex-basis: 100%;
  text-align: center;
}

.imagen-grande {
  width: 80%;
  max-width: 800px;
  height: auto;
}

.button-container {
  display: flex;
  justify-content: center;
  margin-top: 10px;
}

.validar-button,
.corregir-button {
  margin: 0 10px;
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  cursor: pointer;
}

.corregir-button {
  background-color: #dc3545;
}
</style>

