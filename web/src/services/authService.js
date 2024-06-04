import axios from 'axios';

const instance = axios.create({
    baseURL: 'http://localhost:8080/auth'
});

const authService = {
    login: () => {
        const formData = new FormData();
        formData.append('username', 'hugo');
        formData.append('password', 'hugo');
    
        return instance.post('/login', formData)
            .then(response => {
                if (response.data.accessToken) {
                    localStorage.setItem('usuario', JSON.stringify(response.data));
                }
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