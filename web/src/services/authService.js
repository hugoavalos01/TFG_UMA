import axios from 'axios';

const instance = axios.create({
    baseURL: 'http://localhost:8080/auth'
});

const authService = {
    login: (username, password) => {
        return instance.post('/login', { username, password })
            .then(response => {
                return response.data;
            });
    },
    register: () => {
        return instance.post('/register', {
            username: "hugo",
            password: "hugo"
        });
    },
    logout: () => {
        return instance.post('/logout');
    },
    getCurrentUser: () => {
        return instance.get('/user');
    }
};

export default authService;