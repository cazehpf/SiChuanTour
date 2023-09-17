import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import ElementUI from "element-ui";
import axios from "axios";
import "element-ui/lib/theme-chalk/index.css";
import "./assets/css/global.css";
import request from "@/util/request";
// 引入ECharts
import * as echarts from "echarts";

import VueParticles from "vue-particles"; // 背景粒子特效
import Vuetify from "vuetify";
import "vuetify/dist/vuetify.min.css"; // 导入 Vuetify 样式
import "@mdi/font/css/materialdesignicons.css"; // 导入 Material Design Icons 字体图标
import VueQuillEditor from "vue-quill-editor";
Vue.use(Vuetify);

const vuetify = new Vuetify({
  theme: {
    // 自定义主题配置
    themes: {
      light: {
        primary: "#007bff", // 主要颜色
        secondary: "#ff5722", // 次要颜色
        accent: "#ffc107", // 强调颜色
      },
    },
  },
  icons: {
    iconfont: "mdi", // 设置默认的图标字体为 Material Design Icons
  },
});

Vue.use(VueParticles);

Vue.config.productionTip = false;
Vue.prototype.request = request;
// 挂载到Vue原形上
Vue.prototype.$echarts = echarts;

Vue.prototype.$axios = axios;
axios.defaults.baseURL = "http://localhost:8088"; //后端地址
//注册插件
Vue.use(ElementUI);
Vue.use(VueQuillEditor);
new Vue({
  router,
  store,
  vuetify,
  render: (h) => h(App),
}).$mount("#app");
