import Vue from "vue";
import Vuex from "vuex";

Vue.use(Vuex);

const app = new Vuex.Store({
  state: {
    isCollapse: false,
  },
  getters: {},
  mutations: {
    setCollapse(state, value) {
      state.isCollapse = value;
    },
  },
  actions: {},
  modules: {},
});
export default app;
