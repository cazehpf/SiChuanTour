<template>
  <img :src="url" />
</template>

<script>
export default {
  props: ["src"], // 父组件传过来所需的url

  data() {
    return {
      url: "./imgs/loading.gif", // 先加载loading.gif
      src2: "",
    };
  },
  watch: {
    src: {
      immediate: true,
      handler(newValue) {
        this.src2 = newValue;
        var newImg = new Image();
        newImg.src = this.src2;
        newImg.onerror = () => {
          newImg.src = "./imgs/loadingFaild.jpg";
        };
        newImg.onload = () => {
          // 图片加载成功后把地址给原来的img
          this.url = newImg.src;
        };
      },
    },
  },
  mounted() {
    this.src2 = this.src;
    var newImg = new Image();

    newImg.src = this.src2;

    newImg.onerror = () => {
      newImg.src = "./imgs/lodingFaild.jpg";
    };

    newImg.onload = () => {
      // 图片加载成功后把地址给原来的img

      this.url = newImg.src;
    };
  },
};
</script>
