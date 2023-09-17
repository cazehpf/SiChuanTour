const { defineConfig } = require("@vue/cli-service");
module.exports = defineConfig({
  transpileDependencies: true,
  lintOnSave: false,
});
// module.exports = {
//   publicPath: "./", //配置本地引用目录文件,如不配置，默认为`/`
// };
module.exports = {
  // 公共路径(必须有的)
  publicPath: "./",
  outputDir: "dist",
  assetsDir: "assets",
  lintOnSave: false,
  productionSourceMap: true, // 不需要生产环境的设置false可以减小dist文件大小，加速构建
  devServer: {
    open: true, // npm run serve后自动打开页面
    host: "0.0.0.0", // 匹配本机IP地址(默认是0.0.0.0)
    port: 8080, // 开发服务器运行端口号
    proxy: null,
  },
};
