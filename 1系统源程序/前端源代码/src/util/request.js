import axios from "axios";
import router from "@/router";
import ElementUI from "element-ui";

const request = axios.create({
  baseURL: `http://62.234.29.93:8088`, //远程服务
  // baseURL: `http://localhost:8088`, //本地服务
  timeout: 30000,
  // withCredentials: true, //设置跨域携带cookie
});

// request 拦截器
// 可以自请求发送前对请求做一些处理
// 比如统一加token，对请求参数统一加密
request.interceptors.request.use(
  (config) => {
    if (config.url == "/upload") {
      config.headers["Content-Type"] = "multipart/form-data";
    } else {
      config.headers["Content-Type"] = "application/json;charset=utf-8";
    }
    let token = sessionStorage.getItem("token")
      ? sessionStorage.getItem("token")
      : null;
    if (token) {
      config.headers["token"] = token; // 设置请求头
    }

    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// response 拦截器
// 可以在接口响应后统一处理结果
request.interceptors.response.use(
  (response) => {
    let res = response.data;
    // // 如果是返回的文件
    // if (response.config.responseType === "blob") {
    //   return res;
    // }
    // // 兼容服务端返回的字符串数据
    // if (typeof res === "string") {
    //   res = res ? JSON.parse(res) : res;
    // }
    // 当权限验证不通过的时候给出提示
    if (res.code === 401) {
      ElementUI.Message({
        message: res.msg,
        type: "error",
      });
      console.log("this.router", router);
      if (router.currentRoute.path !== "/login") {
        router.push("/login");
      }
      // router.replace("/login");
    }
    console.log("res", res);

    return res;
  },
  (error) => {
    console.log("err" + error); // for debug
    return Promise.reject(error);
  }
);

export default request;
