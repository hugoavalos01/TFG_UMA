<template>
  <div>
    <Navbar />
    <h1>Resultados</h1>
    <div v-if="imagenesConAnotaciones.length > 0">
      <h2>Imágenes clasificadas y validadas</h2>
      <div class="carousel">
        <button @click="prevImage" class="carousel-button prev-button">
          &#9664;
        </button>
        <div class="image-wrapper">
          <img
            :src="imagenesConAnotacionActual.imagen"
            :alt="'Imagen ' + (currentImageIndex + 1)"
            class="imagen-grande"
          />
          <p>
            <b>Clasificación de la IA:</b>
            {{ imagenesConAnotacionActual.anotacion }}
            <br />
            <b>Validación:</b> {{ imagenesConAnotacionActual.validacion }}
          </p>
          <button @click="descargarImagen" class="download-button">
            <i class="fas fa-download"></i> Descargar
          </button>
        </div>
        <button @click="nextImage" class="carousel-button next-button">
          &#9654;
        </button>
      </div>
    </div>
    <div v-else>
      <p class="no-images-message">No hay imágenes validadas disponibles.</p>
    </div>
    <SpinnerModal :show="loading" message="Cargando imágenes..."></SpinnerModal>
  </div>
</template>
  
  
  
  <script>
import Navbar from "@/components/NavbarTop.vue";
import uploadService from "@/services/uploadService.js";
import SpinnerModal from "@/components/SpinnerModal.vue";
import { NumberToClassUtil } from "@/utils/NumberToClassUtil.js";

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
      userInput: "",
      clases: [],
    };
  },
  computed: {
    imagenesConAnotacionActual() {
      return this.imagenesConAnotaciones[this.currentImageIndex] || {};
    },
  },
  created() {
    this.clases = Object.values(NumberToClassUtil.numberToClassMap);
  },
  mounted() {
    console.log("Resultados mounted");
    this.cargarInfoImagenes();
    this.cargarImagenes();
  },
  methods: {
    cargarImagenes() {
      this.loading = true;
      uploadService
        .getImagenesValidadas()
        .then((response) => {
          this.imagenes = response.data;
          console.log("Imágenes cargadas:", this.imagenes);

          // Asociar cada imagen con su anotación correspondiente
          this.imagenesConAnotaciones = this.imagenes.map((imagen) => {
            const anotacion =
              this.infoImagenes.find(
                (info) => info.pathMinIO === imagen.fileName
              )?.anotaciones || "Anotación no disponible";
            const validacion =
              this.infoImagenes.find(
                (info) => info.pathMinIO === imagen.fileName
              )?.validado || "Validación no disponible";
            return {
              imagen: imagen.url,
              fileName: imagen.fileName,
              anotacion: NumberToClassUtil.transformNumberToWord(anotacion),
              validacion:
                validacion === "Validado"
                  ? "Validado"
                  : NumberToClassUtil.transformNumberToWord(validacion),
            };
          });

          console.log("Imágenes con anotaciones:", this.imagenesConAnotaciones);
        })
        .catch((error) => {
          console.error("Error al cargar las imágenes:", error);
        })
        .finally(() => {
          this.loading = false;
        });
    },
    cargarInfoImagenes() {
      uploadService
        .getInfoImagenes()
        .then((response) => {
          this.infoImagenes = response.data;
          console.log("Información de imágenes cargada:", this.infoImagenes);
        })
        .catch((error) => {
          console.error(
            "Error al cargar la información de las imágenes:",
            error
          );
        });
    },
    descargarImagen() {
      uploadService
        .descargarImagen(this.imagenesConAnotacionActual.fileName)
        .then((response) => {
          // response.data es un Blob porque configuramos responseType: 'blob'
          const blob = response.data;

          // Crea una URL para el Blob
          const url = URL.createObjectURL(blob);

          // Crea un enlace de descarga
          const a = document.createElement("a");
          a.href = url;
          a.download = this.imagenesConAnotacionActual.fileName + ".zip"; // Nombre del archivo para la descarga
          document.body.appendChild(a); // Agrega el enlace al DOM
          a.click(); // Simula el clic en el enlace para iniciar la descarga

          // Limpia el DOM y revoca el objeto URL
          document.body.removeChild(a);
          URL.revokeObjectURL(url);
        })
        .catch((error) => {
          console.error("Error al descargar la imagen:", error);
        });
    },
    nextImage() {
      this.userInput = "";
      if (this.currentImageIndex < this.imagenesConAnotaciones.length - 1) {
        this.currentImageIndex++;
      }
    },
    prevImage() {
      this.userInput = "";
      if (this.currentImageIndex > 0) {
        this.currentImageIndex--;
      }
    },
  },
};
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

.no-images-message {
  color: #888;
}

.download-button {
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px; 
  padding: 5px 10px; 
  cursor: pointer; 

}

.download-button i {
  margin-right: 8px; 
}

.download-button:hover {
  background-color: #2ecc71; 
}

.download-button:focus {
  outline: none; 
}
</style>