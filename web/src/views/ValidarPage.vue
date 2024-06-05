<template>
  <div>
    <Navbar />
    <h1>Validar</h1>
    <div v-if="imagenes.length > 0">
      <h2>Imágenes Clasificadas</h2>
      <div v-for="(imagen, index) in imagenes" :key="index">
        <img :src="imagen" :alt="'Imagen ' + (index + 1)" style="width: 200px; height: 200px;">
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
      imagenes: []
    }
  },
  mounted() {
    console.log('ValidarPage mounted')
    this.cargarImagenes()
  },
  methods: {
    cargarImagenes() {
      uploadService.getImagenes().then((response) => {
        this.imagenes = response.data;
        console.log('Imágenes cargadas:', this.imagenes);
      }).catch((error) => {
        console.error('Error al cargar las imágenes:', error);
      })
    }
  }
}
</script>

<style>
/* Estilos CSS si es necesario */
</style>
