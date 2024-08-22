import instance from './authInterceptor';

const uploadService = {
    uploadFile: (file) => {
        return instance.post('/uploadArchivo', file);
    },
    moveImages: () => {
        return instance.post('/moveImages');
    },
    getStatus: () => {
        return instance.get('/moveImages/status');
    },
    getImagenesSinValidar: () => {
        return instance.get('/noValidado');
    },
    getImagenesValidadas: () => {
        return instance.get('/classified');
    },
    getInfoImagenes: () => {
        return instance.get('/infoImagenes');
    },
    validarImagen: (fileName, validado)  => {
        return instance.post('/validarImagen', { fileName, validado });
      },
      descargarImagen: (fileName) => {
        return instance.get(`/downloadFile/${fileName}`, {
            responseType: 'blob'
        });
    }
};

export default uploadService;



// const downloadService = {
//     downloadFile: (fileName) => {
//         return instance.get(`/download/${fileName}`);
//     },
// };




