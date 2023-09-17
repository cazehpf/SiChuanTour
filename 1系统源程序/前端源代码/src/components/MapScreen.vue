<template>
  <div class="map" />
</template>

<script scoped>
// 城市坐标文件
import City from "@/util/City";
import "@/util/china";
export default {
  name: "MapScreen",
  data() {
    return {
      // 终点位置位置
      end: "四川",
      provinces: [
        { name: "四川", value: 1 },
        { name: "北京", value: 1 },
        { name: "天津", value: 1 },
        { name: "河北", value: 1 },
        { name: "山西", value: 1 },
        { name: "内蒙古", value: 1 },
        { name: "辽宁", value: 1 },
        { name: "吉林", value: 1 },
        { name: "黑龙江", value: 1 },
        { name: "上海", value: 1 },
        { name: "江苏", value: 1 },
        { name: "浙江", value: 1 },
        { name: "安徽", value: 1 },
        { name: "福建", value: 1 },
        { name: "江西", value: 1 },
        { name: "山东", value: 1 },
        { name: "河南", value: 1 },
        { name: "湖北", value: 1 },
        { name: "湖南", value: 1 },
        { name: "广东", value: 1 },
        { name: "广西", value: 1 },
        { name: "海南", value: 1 },
        { name: "重庆", value: 1 },

        { name: "贵州", value: 1 },
        { name: "云南", value: 1 },
        { name: "西藏", value: 1 },
        { name: "陕西", value: 1 },
        { name: "甘肃", value: 1 },
        { name: "青海", value: 1 },
        { name: "宁夏", value: 1 },
        { name: "新疆", value: 1 },
        { name: "台湾", value: 1 },
        { name: "香港", value: 1 },
        { name: "澳门", value: 1 },
      ],
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
        this.getMap();
      }, 300);
    },
    provinces: {
      handler() {
        this.getMap();
      },
      deep: true,
    },
  },
  mounted() {
    this.getMap();
  },
  methods: {
    // 地图
    getMap() {
      // 地图的坐标信息
      var geoCoordMap = City;
      // const a = [{ 'name': '北京', 'value': 6 }, { 'name': '湖南', 'value': 3 }, { 'name': '广东', 'value': 2 }, { 'name': '山西', 'value': 2 }, { 'name': '上海', 'value': 1 }, { 'name': '重庆', 'value': 1 }, { 'name': '河北', 'value': 1 }, { 'name': '陕西', 'value': 1 }, { 'name': '山东', 'value': 0 }, { 'name': '黑龙江', 'value': 0 }, { 'name': '贵州', 'value': 0 }]
      // 数据信息
      var BJData = this.provinces
        .filter((item) => item.value > 0)
        .map((item) => {
          return [
            {
              name: this.end,
            },
            {
              name: item.name,
              value: item.value,
            },
          ];
        });
      // var planePath = 'path://M.6,1318.313v-89.254l-319.9-221.799l0.073-208.063c0.521-84.662-26.629-121.796-63.961-121.491c-37.332-0.305-64.482,36.829-63.961,121.491l0.073,208.063l-319.9,221.799v89.254l330.343-157.288l12.238,241.308l-134.449,92.931l0.531,42.034l175.125-42.917l175.125,42.917l0.531-42.034l-134.449-92.931l12.238-241.308L1705'
      var planePath =
        "path://M1705.06,1318.313v-89.254l-319.9-221.799l0.073-208.063c0.521-84.662-26.629-121.796-63.961-121.491c-37.332-0.305-64.482,36.829-63.961,121.491l0.073,208.063l-319.9,221.799v89.254l330.343-157.288l12.238,241.308l-134.449,92.931l0.531,42.034l175.125-42.917l175.125,42.917l0.531-42.034l-134.449-92.931l12.238-241.308L1705.06,1318.313z";
      var convertData = function (data) {
        // data BJData
        var res = [];
        for (var i = 0; i < data.length; i++) {
          // BJData
          var dataItem = data[i];
          var fromCoord = geoCoordMap[dataItem[0].name]; // 北京
          var toCoord = geoCoordMap[dataItem[1].name]; // 成都
          if (fromCoord && toCoord) {
            res.push([
              {
                coord: toCoord,
                value: dataItem[1].value,
              },
              {
                coord: fromCoord,
              },
            ]);
          }
        }
        return res;
      };
      var series = [];
      [["四川", BJData]].forEach(function (item) {
        series.push(
          {
            // 设置飞机后的烟雾
            type: "lines",
            zlevel: 1,
            effect: {
              show: true,
              period: 6,
              trailLength: 0.7,
              color: "white",
              symbolSize: 3,
              symbol: planePath,
            },
            // 线宽
            lineStyle: {
              normal: {
                color: "#fff",
                width: 0,
                curveness: 0.2,
              },
            },
            data: convertData(item[1]), // 坐标
          },
          {
            name: "",
            type: "lines",
            zlevel: 2,
            effect: {
              show: true,
              period: 6,
              trailLength: 0,
              symbol: planePath,
              symbolSize: 15,
            },
            lineStyle: {
              normal: {
                color: "rgb(122,255,158)",
                width: 1,
                // opacity: 0.4,
                curveness: 0.2,
              },
            },
            data: convertData(item[1]),
          },
          {
            // 设置圆圈属性的
            name: item[0] + "",
            type: "effectScatter",
            coordinateSystem: "geo",

            zlevel: 2,
            rippleEffect: {
              brushType: "stroke",
            },
            label: {
              normal: {
                show: true,
                position: "right",
                formatter: "{b}",
              },
            },
            // 设置圆圈大小
            symbolSize: function () {
              // val的值是data的值
              return 10;
            },
            itemStyle: {
              normal: {
                color: function (params) {
                  if (params.data.name == "四川") {
                    return "#ff0";
                  } else {
                    return "#74ebd5";
                  }
                },
              },
            },
            data: item[1].map(function (dataItem) {
              return {
                name: dataItem[1].name,
                value: geoCoordMap[dataItem[1].name].concat([
                  dataItem[1].value,
                ]),
              };
            }),
          }
        );
      });

      const option = {
        // 设置为半透明
        backgroundColor: "transparent",
        title: {
          left: "left",
          textStyle: {
            color: "#fff",
          },
        },
        tooltip: {
          trigger: "item",
        },
        // 设置地图默认大小
        geo: {
          zoom: 1.2,
          map: "china",
          label: {
            normal: {
              show: false,
              color: "#fff",
            },
            emphasis: {
              show: true,
              color: "#fff",
            },
          },
          roam: true,
          itemStyle: {
            normal: {
              // 设置区域颜色
              areaColor: "rgb(78,122,231)",
              // 设置边框颜色
              borderColor: "rgb(19,34,134)",
            },
            emphasis: {
              // 设置鼠标悬停时候的样式
              areaColor: "rgb(255,124,0)",
            },
          },
        },

        series: series,
      };
      // this.$echarts.init(document.querySelector(".map")).setOption(option);

      const myMap = this.$echarts.init(document.querySelector(".map"));
      myMap.setOption(option);
      //随着屏幕大小调节图表
      window.addEventListener("resize", () => {
        myMap.resize();
      });
      myMap.resize();
    },
  },
  beforeDestroy() {
    window.removeEventListener("resize", this.getMap);
  },
};
</script>

<style scoped>
.map {
  width: 100%;
  height: 500px;
  /* background-color: rgb(4, 148, 188); */
}
</style>
