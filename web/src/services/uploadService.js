import axios from 'axios';

// Create an instance of Axios with custom configuration
const instance = axios.create({
    baseURL: 'http://localhost:8080/api'
});

// Define your service methods
const uploadService = {
    uploadFile: (file) => {
        // Implement your file upload logic here
        // You can use the instance to make the API request
        return instance.post('/upload', file);
    },
};

export default uploadService;
