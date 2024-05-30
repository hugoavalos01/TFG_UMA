<template>
  <NavbarTop />
  <div class="home">
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
    <button @click="moverImagenes" class="clasificar-bttn">Clasificar imagenes ya</button>
  </div>
</template>

<script>
import uploadService from "@/services/uploadService";
import NavbarTop from '@/components/NavbarTop.vue';

export default {
  components: { NavbarTop },
  data() {
    return {
      file: null,
      isDragOver: false,
    };
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
      if (this.file) {
        const formData = new FormData();
        formData.append("file", this.file);

        try {
          const response = await uploadService.uploadFile(formData);
          console.log("File uploaded successfully:", response.data);

          // Resetear formulario
          this.file = null;
          this.$refs.fileInput.value = null;
        } catch (error) {
          console.error("Error uploading file:", error);
        }
      }
    },
    async moverImagenes() {
      try {
        const response = await uploadService.moveImages();
        console.log("Imagenes movidas correctamente:", response.data);
      } catch (error) {
        console.error("Error moviendo imagenes:", error);
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
.clasificar-bttn{
  color: white;
  padding: 10px 20px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s; /* Transición para el color de fondo */
  background-color: #42b983;
  margin-top: 20px;
  border: 2px solid #3a9b76; /* Borde para el botón "Escoger un archivo" */


}
</style>
