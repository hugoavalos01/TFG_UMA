<template>
  <NavbarTop />
  <div class="home">
    <h1>Subir y clasificar imágenes</h1>
    <div class="formulario">
      <div
        class="drop-area"
        @dragover.prevent="onDragOver"
        @dragleave.prevent="onDragLeave"
        @drop.prevent="onDrop"
        :class="{ 'is-dragover': isDragOver }"
        @click="onClick"
      >
        <p v-if="!file">
          Arrastra y suelta aquí los archivos que deseas subir o pulsa el botón
          Escoger un archivo
        </p>
        <p v-else>
          <i v-if="isImage(file)" class="fas fa-file-image"></i>
          <i v-else class="fas fa-file-archive"></i>
          {{ file.name }}
        </p>
        <input
          type="file"
          ref="fileInput"
          @change="onFileChange"
          accept=".zip,image/*"
          hidden
        />
      </div>
      <div class="button-container">
        <button @click="onClick" class="choose-file-button">
          Escoger un archivo
        </button>
        <button @click="onSubmit" :disabled="!file" class="submit-button">
          Enviar archivos
        </button>
      </div>
    </div>
    <button @click="clasificarImagenes" :disabled="clasificando" class="clasificar-bttn">
      <u>Clasificar imagenes ya</u>
    </button>
    <div v-if="clasificando || finalizado || resultadoSubida" style="margin-top: 20px;">{{ message }}</div>
  </div>
  <spinner-modal :show="loading" :message="messageModal"></spinner-modal>
</template>

<script>
import uploadService from "@/services/uploadService";
import NavbarTop from "@/components/NavbarTop.vue";
import SpinnerModal from "@/components/SpinnerModal.vue";

export default {
  components: { NavbarTop, SpinnerModal },
  data() {
    return {
      file: null,
      isDragOver: false,
      loading: false,
      clasificando: false,
      finalizado: false,
      message: "Cargando",
      resultadoSubida: false,
      messageModal: null,

    };
  },
  mounted() {
    this.checkInitialStatus();
  },
  methods: {
    onFileChange(event) {
      this.file = event.target.files[0];
    },
    onClick() {
      this.$refs.fileInput.click();
    },
    onDragOver() {
      this.isDragOver = true;
    },
    onDragLeave() {
      this.isDragOver = false;
    },
    onDrop(event) {
      this.isDragOver = false;
      const files = event.dataTransfer.files;
      if (files.length) {
        this.file = files[0];
      }
    },
    async onSubmit() {
      this.loading = true;
      if (this.file) {
        const formData = new FormData();
        formData.append("file", this.file);
        await uploadService
          .uploadFile(formData)
          .then((response) => {
            this.messageModal = "Cargando";
            this.resultadoSubida = true;
            this.message = response.data;
          })
          .catch((error) => {
            console.error("Error uploading file:", error);
          })
          .finally(() => {
            this.file = null;
            this.$refs.fileInput.value = null;
            this.loading = false;
          });
      }
    },
    async clasificarImagenes() {
      this.messageModal = "Clasificando imágenes...";
      this.clasificando = true;
      try {
        const response = await uploadService.moveImages(); // Llamada inicial para iniciar la clasificación
        if(response.data == "No hay imagenes por clasificar.") {
          this.message = response.data;
          this.clasificando = false;
          this.finalizado = true;
        } else {
          this.pollStatus(); // Iniciar el polling del estado
        }
      } catch (error) {
        console.error("Error al iniciar la clasificación:", error);
        this.message = "Error al iniciar la clasificación: " + error.message;
        this.clasificando = false;
      } 
    },
    async pollStatus() {
      const checkStatus = async () => {
        try {
          const response = await uploadService.getStatus(); // Llamada al endpoint de estado
          if (response.data === "Completado") {
            this.message = "Imágenes clasificadas con éxito.";
            this.finalizado = true;
            this.clasificando = false;
            clearInterval(this.pollInterval);
          }
        } catch (error) {
          console.error("Error al verificar el estado:", error);
          this.message = "Error al verificar el estado: " + error.message;
          this.clasificando = false;
          clearInterval(this.pollInterval);
        }
      };

      // Realiza el chequeo de estado cada 5 segundos
      this.pollInterval = setInterval(checkStatus, 5000);
      checkStatus(); // Llamada inicial inmediata
    },
    async checkInitialStatus() {
      try {
        const response = await uploadService.getStatus();
        if (response.data === "En progreso") {
          this.clasificando = true;
          this.messageModal = "Clasificando imágenes...";
          this.pollStatus(); // Iniciar el polling del estado
        } else {
          this.clasificando = false;
          this.message = "Imágenes clasificadas con éxito.";
        }
      } catch (error) {
        console.error("Error al verificar el estado inicial:", error);
        this.message = "Error al verificar el estado inicial: " + error.message;
      }
    },
    isImage(file) {
      return file && file.type.startsWith("image/");
    },
  },
};
</script>

<style scoped>
.home {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
}
.formulario {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
}

.drop-area {
  width: 75%;
  height: 150px;
  border: 2px dashed #ccc;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  margin-bottom: 20px;
  cursor: pointer;
  transition: border-color 0.3s;
}

.drop-area:hover {
  border-color: #42b983;
}

.drop-area.is-dragover {
  border-color: #42b983;
}

.button-container {
  display: flex;
  justify-content: space-between;
  width: 30%;
  margin-top: 20px; /* Espacio entre el área de soltar y los botones */
}

.choose-file-button {
  width: 40%;
  color: #3a9b76;
  padding: 10px 20px;
  cursor: pointer;
  font-size: 16px;
  background-color: white;
  transition: background-color 0.3s; /* Transición para el color de fondo */
  border: 2px solid #3a9b76; /* Borde para el botón "Escoger un archivo" */
}
.choose-file-button:hover {
  background-color: rgb(240, 240, 240); /* Color de fondo al pasar el ratón */
}
.submit-button {
  width: 40%;
  color: white;
  padding: 10px 20px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s; /* Transición para el color de fondo */
  background-color: #42b983;
}

.submit-button:hover {
  background-color: #3a9b76; /* Color de fondo al pasar el ratón */
}

.submit-button:disabled {
  cursor: not-allowed;
}
.clasificar-bttn {
  font-size: 16px;
  color: #42b983;
  margin-top: 25px;
  background-color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
.clasificar-bttn:disabled {
  cursor: not-allowed;
}
.clasificar-bttn:hover {
  color: #3a9b76; /* Color de fondo al pasar el ratón */
}
</style>
