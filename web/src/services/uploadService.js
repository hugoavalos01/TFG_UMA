import axios from 'axios';

const instance = axios.create({
    baseURL: 'http://localhost:8080/api'
});

const uploadService = {
    uploadFile: (file) => {
        return instance.post('/uploadArchivo', file);
    },
    moveImages: () => {
        return instance.post('/moveImages');
    }
};

export default uploadService;



// const downloadService = {
//     downloadFile: (fileName) => {
//         return instance.get(`/download/${fileName}`);
//     },
// };




