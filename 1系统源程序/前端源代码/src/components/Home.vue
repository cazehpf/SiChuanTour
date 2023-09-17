<template>
  <div>
    <el-row :span="6" :gutter="100">
      <el-col :span="6"><div class="num-interests-empty"></div></el-col>
      <!-- A级景点个数 -->
      <el-col :span="6"
        ><div class="num-interests">
          <p class="p-discription">A级景区总数(个)</p>
          <p class="p-num">819</p>
        </div></el-col
      >
      <!-- 旅行社总数 -->
      <el-col :span="6"
        ><div class="num-interests">
          <p class="p-discription">旅行社总数(个)</p>
          <p class="p-num">1483</p>
        </div></el-col
      >
      <el-col :span="6"><div class="num-interests-empty"></div></el-col>
    </el-row>

    <el-row :span="18" :gutter="20">
      <!-- 统计图1 -->
      <el-col :span="6">
        <!-- 统计图1-1 -->
        <el-row :span="11"
          ><div class="chart-box">
            <div class="chart-show" id="interestsOfPlace"></div>
          </div>
        </el-row>
        <el-row :span="2"><div class="chart-show-empty"></div></el-row>
        <!-- 统计图1-2 -->
        <el-row :span="11">
          <div class="chart-box">
            <div class="chart-show" id="hotInterests"></div></div
        ></el-row>
      </el-col>
      <!-- 地图大屏 -->
      <el-col :span="12">
        <div class="map-box">
          <MapScreen></MapScreen>
        </div>
      </el-col>
      <!-- 统计图2 -->
      <el-col :span="6">
        <!-- 统计图2-1 -->
        <el-row :span="8"
          ><div class="chart-box">
            <div class="chart-show" id="agencyOfPlace"></div>
          </div>
        </el-row>
        <el-row :span="8"><div class="chart-show-empty"></div></el-row>
        <!-- 统计图2-2 -->
        <el-row :span="8">
          <div class="chart-box">
            <div class="chart-show" id="temperatureOfPlace"></div></div
        ></el-row>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import MapScreen from "@/components/MapScreen.vue";
import { getAllCityWeathers } from "@/util/location_weather";
export default {
  name: "HomePage",
  components: {
    MapScreen,
  },
  data() {
    return {
      myChart1: null,
      myChart2: null,
      myChart3: null,
      myChart4: null,
      pieLengend: ["成都市", "乐山市", "南充市", "绵阳市", "凉山州", "其他"],

      xData1: ["成都市", "甘孜州", "宜宾市", "阿坝州", "凉山州"], //横坐标1
      yData1: [95, 87, 57, 55, 52], //数据1
      xData2: [], //横坐标2
      yData2: [], //数据2
      data3: [
        { value: 824, name: "成都市" },
        { value: 79, name: "乐山市" },
        { value: 63, name: "南充市" },
        { value: 59, name: "绵阳市" },
        { value: 39, name: "凉山州" },
        { value: 419, name: "其他市州" },
      ],
      xData4: [
        "成都市",
        "甘孜州",
        "宜宾市",
        "阿坝州",
        "凉山州",
        "广元市",
        "雅安市",
        "乐山市",
        "南充市",
        "绵阳市",
        "达州市",
        "眉山市",
        "泸州市",
        "巴中市",
        "广安市",
        "德阳市",
        "内江市",
        "攀枝花市",
        "自贡市",
        "遂宁市",
        "资阳市",
      ],
      yData4: [],
    };
  },

  computed: {
    isCollapsede() {
      return this.$store.state.isCollapse;
    },
  },
  watch: {
    isCollapsede() {
      setTimeout(() => {
        this.initEcharts1();
        this.initEcharts2();
        this.initEcharts3();
        this.initEcharts4();
      }, 300);
    },
  },
  mounted() {
    this.getData2Init();
    this.initEcharts1();

    this.initEcharts3();
    this.load();
  },
  beforeDestroy() {
    window.removeEventListener("resize", this.initEcharts1);
    window.removeEventListener("resize", this.initEcharts2);
    window.removeEventListener("resize", this.initEcharts3);
    window.removeEventListener("resize", this.initEcharts4);
  },
  methods: {
    async load() {
      this.yData4 = await getAllCityWeathers();
      this.initEcharts4();
    },
    getData2Init() {
      this.request.get("/interestTop").then((res) => {
        if (res.code === 200) {
          for (var i = 0; i < res.data.length; i++) {
            this.yData2.push(res.data[i].count);
            this.xData2.push(res.data[i].interestName);
          }
          this.initEcharts2();
        }
      });
    },
    initEcharts1() {
      // 基本柱状图
      const option1 = {
        title: {
          text: "A级景区数目top5",
          textStyle: {
            color: "rgb(0,0,0)", // 标题文本颜色
            fontSize: 16, // 标题文本字体大小
            //  fontWeight: 'bold' // 标题文本字体粗细
            // 其他可用的文本样式属性
          },
        },
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "shadow", //鼠标悬停显示样式
            shadowStyle: {
              shadowColor: "rgba(0, 0, 0, 0.5)",
              shadowBlur: 2,
            },
          },
        },
        grid: {
          left: "3%",
          right: "4%",
          bottom: "3%",
          containLabel: true,
        },
        xAxis: {
          type: "category",
          data: this.xData1,
          axisLabel: {
            fontSize: 12,
            color: "rgb(0,0,0)", // 标签的颜色
          },
        },
        yAxis: {
          type: "value",
          name: "单位/个", // 设置单位文本
          nameTextStyle: {
            color: "rgb(0,0,0)", // 单位文本颜色
            fontSize: 12, // 单位文本字体大小
            // fontWeight: "bold", // 单位文本字体粗细
            // 其他可用的文本样式属性
          },
          axisLabel: {
            color: "rgb(0,0,0)", // 标签的颜色
          },
        },
        series: [
          {
            name: "景点数",
            type: "bar", //形状为柱状图
            data: this.yData1,
            itemStyle: {
              color: {
                type: "linear",
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [
                  {
                    offset: 0,
                    color: "rgb(89, 159, 242)", // 渐变结束颜色
                  },
                  {
                    offset: 1,
                    color: "rgb(40, 91, 240)", // 渐变起始颜色
                  },
                ],
              },
              // 其他可用的样式属性
            },
          },
        ],
      };
      this.myChart1 = this.$echarts.init(
        document.getElementById("interestsOfPlace")
      );
      this.myChart1.setOption(option1);
      //随着屏幕大小调节图表
      window.addEventListener("resize", () => {
        this.myChart1.resize();
      });
      this.myChart1.resize();
    },
    initEcharts2() {
      // 基本柱状图
      const option2 = {
        graphic: {
          type: "text",
          right: 20,
          top: 40,
          style: {
            text: "评论数/条",
            fill: "black",
            fontSize: 12,
          },
        },
        title: {
          text: "热门景点top10",
          textStyle: {
            color: "rgb(0,0,0)", // 标题文本颜色
            fontSize: 16, // 标题文本字体大小
            //  fontWeight: 'bold' // 标题文本字体粗细
            // 其他可用的文本样式属性
          },
        },
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "shadow", //鼠标悬停显示样式
            shadowStyle: {
              shadowColor: "rgba(0, 0, 0, 0.5)",
              shadowBlur: 2,
            },
          },
        },
        grid: {
          left: "3%",
          bottom: "3%",
          containLabel: true,
        },
        xAxis: {
          type: "value",

          axisLine: {
            show: false,
          },
          axisTick: {
            show: false,
          },
          splitLine: { show: false },
          axisLabel: { show: false },
        },
        yAxis: {
          inverse: true,
          type: "category",
          data: this.xData2,
          splitLine: { show: false },
          axisLine: {
            show: false,
          },
          axisTick: {
            show: false,
          },
          nameTextStyle: {
            fontSize: 8,
          },
          axisLabel: {
            color: "rgb(0,0,0)", // 标签的颜色
          },
        },
        series: [
          {
            name: "评论数",
            type: "bar",
            data: this.yData2,
            // barWidth: 14,
            // barGap: 10,
            // smooth: true,
            label: {
              normal: {
                show: true,
                position: "right",
                offset: [5, -2],
                textStyle: {
                  color: "rgb(0,0,0)",
                  fontSize: 10,
                },
              },
            },
            itemStyle: {
              color: {
                type: "linear",
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [
                  {
                    offset: 0,
                    color: "rgb(89, 159, 242)", // 渐变结束颜色
                  },
                  {
                    offset: 1,
                    color: "rgb(40, 91, 240)", // 渐变起始颜色
                  },
                ],
              },
              // 其他可用的样式属性
            },
          },
        ],
      };

      this.myChart2 = this.$echarts.init(
        document.getElementById("hotInterests")
      );
      this.myChart2.setOption(option2);
      // //随着屏幕大小调节图表
      window.addEventListener("resize", () => {
        this.myChart2.resize();
      });
      this.myChart2.resize();
    },
    initEcharts3() {
      const option3 = {
        legend: {
          itemGap: 5, // 设置图例项之间的间隔为 20
          // 图例
          data: this.pieLengend,
          right: "3%",
          textStyle: {
            fontSize: 10, // 设置图例字体的大小为 14
            color: "black", // 设置图例字体的颜色为红色
          },
          orient: "vertical",
        },
        title: {
          text: "旅行社数目占比top5",
          textStyle: {
            color: "rgb(0,0,0)", // 标题文本颜色
            fontSize: 16, // 标题文本字体大小
            //  fontWeight: 'bold' // 标题文本字体粗细
            // 其他可用的文本样式属性
          },
        },
        color: [
          "rgb(4, 169, 255)",
          "rgb(60, 254, 236)",
          "rgb(0, 228, 53)",
          "rgb(122, 255, 0)",
          "rgb(250, 112, 18)",
          "rgb(252, 235, 1)",
        ],
        tooltip: {
          trigger: "item",
          formatter: "{b}:{c}个 ({d})%", // 显示数据项名称和数值
        },
        series: [
          {
            type: "pie",
            label: {
              color: "rgb(0,0,0)",
              fontSize: 10,
              show: true,
              formatter: "{d}%", // b代表名称，c代表对应值，d代表百分比
            },
            radius: ["30%", "60%"], //圆环内半径40%，外半径70%
            data: this.data3,
            itemStyle: {},
          },
        ],
      };
      this.myChart3 = this.$echarts.init(
        document.getElementById("agencyOfPlace")
      );
      this.myChart3.setOption(option3);
      //随着屏幕大小调节图表
      window.addEventListener("resize", () => {
        this.myChart3.resize();
      });
      this.myChart3.resize();
    },
    initEcharts4() {
      const option4 = {
        title: {
          text: "各市当前气温情况",

          textStyle: {
            color: "rgb(0,0,0)", // 标题文本颜色
            fontSize: 16, // 标题文本字体大小
            //  fontWeight: 'bold' // 标题文本字体粗细
            // 其他可用的文本样式属性
          },
        },
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "cross",
          },
          formatter: function (params) {
            var tooltipContent = "";
            params.forEach(function (item) {
              tooltipContent += item.seriesName + ": " + item.value + " ℃"; // 将单位添加到数值后面
            });
            return tooltipContent;
          },
        },
        grid: {
          left: "3%",
          right: "4%",
          bottom: "3%",
          containLabel: true,
        },
        xAxis: {
          type: "category",
          boundaryGap: false,
          axisLabel: {
            fontSize: 14,
            color: "rgb(0,0,0)", // 标签的颜色
          },
          data: this.xData4,
        },
        yAxis: {
          type: "value",
          name: "单位/℃", // 设置单位文本
          nameTextStyle: {
            color: "rgb(0,0,0)", // 单位文本颜色
            fontSize: 12, // 单位文本字体大小
            // fontWeight: "bold", // 单位文本字体粗细
            // 其他可用的文本样式属性
          },
          axisLabel: {
            formatter: "{value} ",
            color: "rgb(0,0,0)", // 标签的颜色
          },
          axisPointer: {
            snap: true,
          },
        },

        series: [
          {
            name: "气温",
            type: "line", //折线图line;柱形图bar;饼图pie
            itemStyle: {
              color: "rgb(255,96,64)", //改变折线点的颜色
              lineStyle: {
                color: "rgb(255,96,64)", //改变折线颜色
              },
            },

            data: this.yData4,
          },
        ],
      };
      this.myChart4 = this.$echarts.init(
        document.getElementById("temperatureOfPlace")
      );
      this.myChart4.setOption(option4);
      //随着屏幕大小调节图表
      window.addEventListener("resize", () => {
        this.myChart4.resize();
      });
      this.myChart4.resize();
    },
  },
};
</script>

<style scoped>
.num-interests {
  background-color: white;
  height: 80px;
  /* border-radius: 5px; */
  line-height: 40px;
  border: 1px solid grey;
  border-radius: 20px;
}
.num-interests-empty {
  background: white;
  height: 100px;
}
.p-num {
  color: rgb(0, 0, 0);
  font-size: 36px;
  text-align: center;
}
.p-discription {
  text-align: center;
  color: #2c3e50;
  font-size: 24px;
}
.chart-box {
  background-color: rgb(0, 13, 47);
  height: 240px;
}
.map-box {
  background-color: rgb(255, 255, 255);
  height: 500px;
  box-shadow: 0 0 25px grey;
}
.chart-show {
  line-height: 40px;
  height: 240px;
  /* padding-left: 20px; */
  border-radius: 5px;
  background-color: white;
  box-shadow: 0 0 20px grey;
}
.chart-show-empty {
  background-color: transparent;
  height: 20px;
}
</style>
