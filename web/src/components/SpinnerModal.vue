<template>
   <div v-if="show" class="spinner-overlay">
      <div class="spinner"></div>
      <div class="loading-text">Cargando<span class="loading-dots">
        <span>.</span>
        <span>.</span>
        <span>.</span>
      </span></div>
    </div>
  </template>
  
  <script>
  export default {
    props: {
      show: {
        type: Boolean,
        required: true
      },
      message: {
        type: String,
        default: "Cargando..."
      }
    }
  };
  </script>
  
  <style scoped>
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
    color: #555;
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