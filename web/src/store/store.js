import { createStore } from 'vuex';

export default createStore({
  state: {
    user: JSON.parse(localStorage.getItem('user')) || null,
    token: localStorage.getItem('token') || ''
  },
  getters: {
    isAuthenticated: state => !!state.token,
    getUser: state => state.user,
  },
  mutations: {
    SET_USER(state, user) {
      state.user = user;
    },
    SET_TOKEN(state, token) {
      state.token = token;
    },
    CLEAR_USER(state) {
      state.user = null;
      state.token = '';
    }
  },
  actions: {},
  modules: {}
});
