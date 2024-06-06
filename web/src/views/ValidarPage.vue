<template>
  <div>
    <Navbar />
    <h1>Validar</h1>
    <div v-if="imagenesConAnotaciones.length > 0">
      <h2>Imágenes Clasificadas</h2>
      <div class="image-grid">
        <div v-for="(imagenConAnotacion, index) in imagenesConAnotaciones" :key="index" class="image-wrapper">
          <img :src="imagenConAnotacion.imagen" :alt="'Imagen ' + (index + 1)" class="imagen-grande">
          <p>Clasificación de la IA: {{ imagenConAnotacion.anotacion }}</p>
          <div class="button-container">
            <button class="validar-button">Validar</button>
            <button class="corregir-button">Corregir</button>
          </div>
        </div>
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
      imagenes: []
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
          // Buscar la anotación correspondiente en this.infoImagenes
          const anotacion = this.infoImagenes.find(info => info.pathMinIO === imagen.fileName)?.anotaciones || 'Anotación no disponible'; // Si no se encuentra la anotación, mostrar un mensaje predeterminado
          
          // Devolver un objeto con la imagen y su anotación
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
    }
  }
}
</script>

<style>
.image-grid {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
}

.image-wrapper {
  flex-basis: 100%;
  margin-bottom: 40px; /* Aumentar el margen inferior */
}

.imagen-grande {
  width: 80%; /* Ajustar el ancho según sea necesario */
  max-width: 800px; /* Ancho máximo */
  height: auto; /* Mantener la proporción de aspecto */
}

.button-container {
  display: flex;
  justify-content: center; /* Centrar los botones horizontalmente */
  margin-top: 10px; /* Agregar espacio arriba de los botones */
}

.validar-button,
.corregir-button {
  margin: 0 10px; /* Añadir espacio entre los botones */
  padding: 10px 20px;
  background-color: #007bff; /* Color del botón */
  color: white;
  border: none;
  cursor: pointer;
}

.corregir-button {
  background-color: #dc3545; /* Color del botón */
}
</style>
