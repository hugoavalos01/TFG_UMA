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
            <button class="corregir-button" @click="corregir">{{ showInput ? 'No corregir' : 'Corregir' }}</button>
            <button class="validar-button" @click="validarImagen">Validar</button>
            <input v-if="showInput" v-model="userInput" type="text" placeholder="Escribe aquí tu corrección y haz click en Validar" class="correccion-input custom-input">
          </div>
        </div>
        <button @click="nextImage" class="carousel-button next-button">&#9654;</button>
      </div>
    </div>
    <div v-else>
      <p class="no-images-message">No hay imágenes clasificadas disponibles.</p>
    </div>
    <SpinnerModal :show="loading" message="Cargando imágenes..."></SpinnerModal>
  </div>
</template>



<script>
import Navbar from '@/components/NavbarTop.vue'
import uploadService from '@/services/uploadService.js'
import SpinnerModal from "@/components/SpinnerModal.vue";


export default {
  components: { Navbar, SpinnerModal },
  data() {
    return {
      imagenesConAnotaciones: [],
      infoImagenes: [],
      imagenes: [],
      currentImageIndex: 0,
      loading: false,
      showInput: false,
      userInput: ''
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
      const anotacion = this.userInput || "Validado";
      uploadService.validarImagen(fileName, anotacion).then(() => {
        console.log('Imagen validada:', fileName);
        // Puedes remover la imagen de la lista después de validar
        this.imagenesConAnotaciones.splice(this.currentImageIndex, 1);
        // Ajustar el índice de la imagen actual si es necesario
        if (this.currentImageIndex >= this.imagenesConAnotaciones.length) {
          this.currentImageIndex = this.imagenesConAnotaciones.length - 1;
        }
        this.userInput = '';
        this.showInput = false;
      }).catch((error) => {
        console.error('Error al validar la imagen:', error);
      });
    },
    nextImage() {
      this.userInput = '';
      if (this.currentImageIndex < this.imagenesConAnotaciones.length - 1) {
        this.currentImageIndex++;
      }
    },
    prevImage() {
      this.userInput = '';
      if (this.currentImageIndex > 0) {
        this.currentImageIndex--;
      }
    },
    corregir() {
    this.showInput = !this.showInput;
    if (!this.showInput) {
        this.userInput = ''; // Reiniciar el valor del input cuando se oculta
    }
  }
  }
}
</script>


<style scoped>
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
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.validar-button:hover {
  background-color: #2ecc71;
}
.corregir-button:hover {
  background-color: #c0392b;
}

.corregir-button {
  background-color: #e74c3c;
}

.no-images-message {
  color: #888;
}

.correccion-input {
  padding: 5px 10px; /* Reducido el padding para hacerlo más pequeño */
  border: 1px solid #ced4da;
  border-radius: 5px;
  font-size: 12px;
  width: 300px; /* Ancho original */
  box-sizing: border-box;
  transition: border-color 0.3s ease;
}

/* Agregado el nuevo estilo para el input cuando está en foco */
.correccion-input:focus {
  outline: none;
  border-color: #007bff;
}

</style>