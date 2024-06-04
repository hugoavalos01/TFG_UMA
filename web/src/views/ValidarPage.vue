<template>
    <Navbar />
    <div>
        <h1>Validar</h1>
    </div>
</template>

<script>
import Navbar from '@/components/NavbarTop.vue'
import uploadService from '@/services/uploadService.js'
export default {
  components: { Navbar },
  mounted() {
    console.log('ValidarPage mounted')
    this.cargarImagenes()
  },
  methods: {
    cargarImagenes() {
      uploadService.getImagenes().then((response) => {
        console.log(response.data)
      })
    }
  },

}
</script>

<style>

</style>