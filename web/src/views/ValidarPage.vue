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
            <button class="validar-button" @click="validarImagen">Validar</button>
            <button class="corregir-button">Corregir</button>
          </div>
        </div>
        <button @click="nextImage" class="carousel-button next-button">&#9654;</button>
      </div>
    </div>
    <div v-else>
      <p>No hay imágenes clasificadas disponibles.</p>
    </div>
    <div v-if="loading" class="spinner-overlay">
      <div class="spinner"></div>
      <div class="loading-text">Cargando<span class="loading-dots">
        <span>.</span>
        <span>.</span>
        <span>.</span>
      </span></div>
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
      currentImageIndex: 0,
      loading: false
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
      this.loading = true;
      uploadService.getImagenesSinValidar().then((response) => {
        this.imagenes = response.data;
        console.log('Imágenes cargadas:', this.imagenes);
        
        // Asociar cada imagen con su anotación correspondiente
        this.imagenesConAnotaciones = this.imagenes.map(imagen => {
          const anotacion = this.infoImagenes.find(info => info.pathMinIO === imagen.fileName)?.anotaciones || 'Anotación no disponible';
          return { imagen: imagen.url, fileName: imagen.fileName , anotacion: anotacion };
        });
        
        console.log('Imágenes con anotaciones:', this.imagenesConAnotaciones);
      }).catch((error) => {
        console.error('Error al cargar las imágenes:', error);
      }).finally(() => {
        this.loading = false;
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
    validarImagen() {
      console.log('Validar imagen', this.imagenesConAnotacionActual)
      const fileName = this.imagenesConAnotacionActual.fileName;
      uploadService.validarImagen(fileName, "Validado").then(() => {
        console.log('Imagen validada:', fileName);
        // Puedes remover la imagen de la lista después de validar
        this.imagenesConAnotaciones.splice(this.currentImageIndex, 1);
        // Ajustar el índice de la imagen actual si es necesario
        if (this.currentImageIndex >= this.imagenesConAnotaciones.length) {
          this.currentImageIndex = this.imagenesConAnotaciones.length - 1;
        }
      }).catch((error) => {
        console.error('Error al validar la imagen:', error);
      });
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

/* Spinner CSS */
.spinner-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-color: rgba(255, 255, 255, 0.8);
  z-index: 9999;
}

.spinner {
  border: 16px solid #f3f3f3;
  border-top: 16px solid #42b983;
  border-radius: 50%;
  width: 120px;
  height: 120px;
  animation: spin 2s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-text {
  margin-top: 20px;
  font-size: 1.5em;
  color: #000;
}

.loading-dots {
  display: inline-block;
  margin-left: 5px;
}

@keyframes blink {
  0%, 20% {
    opacity: 0;
  }
  40% {
    opacity: 1;
  }
  60% {
    opacity: 0;
  }
}

.loading-dots span {
  opacity: 0;
  animation: blink 1.5s infinite;
}

.loading-dots span:nth-child(1) {
  animation-delay: 0.3s;
}

.loading-dots span:nth-child(2) {
  animation-delay: 0.6s;
}

.loading-dots span:nth-child(3) {
  animation-delay: 0.9s;
}

</style>
