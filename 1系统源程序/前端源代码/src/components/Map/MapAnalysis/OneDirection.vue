<template>
  <div>
    <div
      class="collapse"
      @click="collapseHandler"
      :style="{
        marginLeft: collapse2 ? '50px' : '600px',
      }"
    >
      <img
        :src="
          collapse2
            ? require('@/assets/img/right_icon.png')
            : require('@/assets/img/left_icon.png')
        "
      />
    </div>
    <div class="over-lay" v-show="!collapse2" style="width: 550px !important">
      <div class="tools-column">
        <div class="tools-main">
          <el-button
            style="background: transparent; border: none"
            type="primary"
            round
            size="small"
            disabled
            >出发地点：</el-button
          >
          <el-button
            type="primary"
            @click="addStart"
            size="small"
            style="margin-right: 5px; color: white"
            >添加出发点</el-button
          >
        </div>
        <div class="tools-second">
          <el-button
            style="background: transparent; border: none"
            type="primary"
            size="small"
            disabled
            >目的景点：</el-button
          >
          <el-autocomplete
            v-model="searchName"
            placeholder="请输入景点名"
            @keyup.enter.native="searchHandler"
            :fetch-suggestions="querySearchAsync"
            style="margin-right: 10px"
          />

          <el-button
            type="primary"
            icon="el-icon-search"
            class="search-button"
            size="small"
            @click="searchHandler"
            style="margin-right: 10px; color: white"
            >搜索</el-button
          >
          <el-button
            type="primary"
            class="clear-button"
            @click="addDirection"
            size="small"
            style="margin-right: 5px !important; color: white"
            >添加目的景点</el-button
          >
        </div>
      </div>

      <div class="tools-row">
        <el-button
          size="small"
          type="primary"
          @click="createRoute"
          style="color: white"
          >生成出行路线</el-button
        >
        <el-button
          size="small"
          type="warning"
          @click="createReport"
          style="color: white"
          >生成行程报告</el-button
        >
        <el-button
          size="small"
          type="info"
          @click="clearRoute"
          style="color: white"
          >清除路线</el-button
        >
        <el-button
          size="small"
          type="info"
          @click="clearLayer"
          style="color: white"
          >清除图层</el-button
        >
      </div>
    </div>
  </div>
</template>

<script>
import L from "leaflet";
import "@ansur/leaflet-pulse-icon";
import "@ansur/leaflet-pulse-icon/dist/L.Icon.Pulse.css";
import "leaflet-polylinedecorator";

const startIcon = L.icon.pulse({
  iconSize: [15, 15],
  color: "blue",
  fillColor: "blue",
});
const directionIcon = L.icon.pulse({
  iconSize: [15, 15],
  color: "red",
  fillColor: "red",
});
// const startIcon = L.icon({
//   iconUrl: "./imgs/start.png",
//   iconSize: [60, 40],
// });
export default {
  data() {
    return {
      interests: [],
      routeDetail: [],
      searchName: "",
      directionTemp: "",
      direction: "",
      directionLayerTemp: null,
      collapse2: false,
    };
  },
  mounted() {
    this.getInterestsInit();
  },
  methods: {
    getInterestsInit() {
      this.request.get("/interests").then((res) => {
        if (res.code === 200) {
          this.interests = res.data;
          console.log(this.interests);
        }
      });
    },

    querySearchAsync(queryString, cb) {
      var interests = this.interests;
      var results = queryString
        ? interests.filter(this.createStateFilter(queryString))
        : interests;

      clearTimeout(this.timeout);
      this.timeout = setTimeout(() => {
        cb(results);
      }, 3000 * Math.random());
    },
    createStateFilter(queryString) {
      return (state) => {
        return state.value.indexOf(queryString) === 0;
      };
    },
    //搜索景点事件
    searchHandler() {
      var sqlParam = new L.supermap.GetFeaturesBySQLParameters({
        queryParameter: {
          name: "A级旅游景区@SiChuan",
          attributeFilter: "名称 like'%" + this.searchName + "%'",
        },
        datasetNames: ["SiChuan:A级旅游景区"],
        toIndex: 1,
      });
      new L.supermap.FeatureService(window.dataUrl).getFeaturesBySQL(
        sqlParam,
        (serviceResult) => {
          var featuresResult = serviceResult.result.features.features;
          if (this.directionLayerTemp != null) {
            //如果没有加入到图层组，就从map中移除它
            window.map.removeLayer(this.directionLayerTemp);
            this.directionLayerTemp = null;
          }

          if (featuresResult.length == 0) {
            this.$message.error("您查找的目的景点不存在！");
            return;
          }

          var feature = featuresResult[0];
          this.directionTemp = feature.properties.名称;
          this.directionLayerTemp = L.marker(
            [feature.geometry.coordinates[1], feature.geometry.coordinates[0]],
            {
              icon: directionIcon,
            }
          )
            .bindPopup(
              "<div style='font-size:16px;opacity:0.5;background-color:yellow;color:red;margin:0px'><p>景区名：" +
                feature.properties.名称 +
                "</p>" +
                "<p>等级：<strong>" +
                feature.properties.等级 +
                "</strong></p>" +
                "<p>所属市州：" +
                feature.properties.所在市州 +
                "</p>" +
                "<p>所属区县：" +
                feature.properties.所在区县 +
                "</p>" +
                "<p>详细地址：" +
                feature.properties.地址 +
                "</p></div>"
            )
            .addTo(window.map);
        }
      );
    },
    //添加目的地景点事件
    addDirection() {
      if (this.directionLayerTemp != null) {
        window.directionLayer = this.directionLayerTemp;
        window.map.removeLayer(this.directionLayerTemp); //地图上移除单图层
        window.map.addLayer(window.directionLayer);
        this.directionLayerTemp = null;
        this.direction = this.directionTemp;
        this.$message.success("目的景点添加成功！");
      } else {
        this.$message.warning(
          "目的景点重复添加或不存在，请重新点击搜索定位景点！"
        );
      }
    },
    //添加出发点事件,这里不会改变鼠标的指针
    addStart() {
      window.map.off("click");
      window.map.on("click", (e) => {
        if (window.startLayerOne != null) {
          window.map.removeLayer(window.startLayerOne);
          window.startLayerOne = null;
        }
        window.startLayerOne = L.marker(e.latlng, { icon: startIcon }).addTo(
          window.map
        );
      });
    },
    //异步加载
    async asyncLoading(fun) {
      const loading = this.$loading({
        lock: true,
        text: "分析中...",
        spinner: "el-icon-loading",
        background: "rgba(0, 0, 0, 0.7)",
      });
      await loading.close();
      fun();
    },
    //异步生成路径事件
    createRoute() {
      this.asyncLoading(this.creatRoutePath);
    },
    creatRoutePath() {
      if (window.startLayerOne == null) {
        this.$message.warning("您还没有添加出发地点，无法进行路径分析");
        return;
      }
      if (this.direction == "") {
        this.$message.warning("您还没有添加目的景点，无法进行路径分析");
        return;
      }
      if (window.routeLayerOne != null) {
        window.map.removeLayer(window.routeLayerOne);
        window.routeLayerOne = null;
      }
      if (window.routeDirectLayerOne != null) {
        window.map.removeLayer(window.routeDirectLayerOne);
        window.routeDirectLayerOne = null;
      }
      //创建最佳路径分析服务实例
      var findPathService = new L.supermap.NetworkAnalystService(
        window.networkUrl
      );
      //创建最佳路径分析参数实例
      var resultSetting = new L.supermap.TransportationAnalystResultSetting({
        returnEdgeFeatures: true,
        returnEdgeGeometry: true,
        returnEdgeIDs: true,
        returnNodeFeatures: true,
        returnNodeGeometry: true,
        returnNodeIDs: true,
        returnPathGuides: true,
        returnRoutes: true,
      });
      var analystParameter = new L.supermap.TransportationAnalystParameter({
        resultSetting: resultSetting,
        weightFieldName: "时间",
      });
      var findPathParameter = new L.supermap.FindPathParameters({
        isAnalyzeById: false,
        nodes: [
          window.startLayerOne.getLatLng(),
          window.directionLayer.getLatLng(),
        ],
        parameter: analystParameter,
      });

      //进行查找
      findPathService.findPath(findPathParameter, (serviceResult) => {
        var result = serviceResult.result;
        result.pathList.map((result) => {
          window.routeLayerOne = L.geoJSON(result.route, {
            style: function () {
              return { color: "#0f0", weight: 8 };
            },
          }).addTo(window.map);
          // console.log(result.route);
          // console.log(window.routeLayerOne);
          var lnglats = [];
          var features = result.route.geometry.coordinates[0];
          for (var k = 0, leng = features.length; k < leng; k++) {
            lnglats.push([features[k][1], features[k][0]]);
          }

          var routePolyLine = L.polyline(lnglats, {
            color: "green",
            weight: 3,
          });
          window.routeDirectLayerOne = L.polylineDecorator(routePolyLine, {
            patterns: [
              {
                offset: 50, // 箭头起始位置距离线条两端的距离
                repeat: 50, // 箭头重复的间隔
                symbol: L.Symbol.arrowHead({
                  pixelSize: 5, // 箭头大小
                  headAngle: 75, // 箭头角度
                  polygon: false, //箭头是否为多边形
                  pathOptions: {
                    // stroke: true,
                    weight: 2, //箭头粗细
                    color: "#FFFF", //箭头颜色
                  },
                }),
              },
            ],
          }).addTo(window.map);

          var pathItemFeatures = result.pathGuideItems.features;
          var _routeDetail = [];
          var len = pathItemFeatures.length;
          var totolTime = 0,
            totolLength = 0,
            _time = 0,
            _length = 0;
          var directionType = "";
          var directionDict = {
            SOURTH: "南",
            NORTH: "北",
            WEST: "西",
            EAST: "东",
          };
          //起点和终点得要
          _routeDetail.push({ description: "从此地出发", time: 0, length: 0 });
          _routeDetail.push({
            description: "到达" + this.direction,
            time: 0,
            length: 0,
          });

          for (var i = 1; i < len - 1; i++) {
            //只要成本大于0的
            if (pathItemFeatures[i].properties.weight != 0) {
              if (
                pathItemFeatures[i].properties.directionType != directionType
              ) {
                if (directionType != "") {
                  _routeDetail.push({
                    description:
                      "向" +
                      directionDict[directionType] +
                      "行驶" +
                      _length.toFixed(2) +
                      "米,用时" +
                      _time.toFixed(2) +
                      "分钟",
                    weight: _time,
                    length: _length,
                  });
                  _time = 0;
                  _length = 0;
                }
                directionType = pathItemFeatures[i].properties.directionType;
                _time += pathItemFeatures[i].properties.weight;
                _length += pathItemFeatures[i].properties.length;
              } else {
                _time += pathItemFeatures[i].properties.weight;
                _length += pathItemFeatures[i].properties.length;
              }
              totolTime += pathItemFeatures[i].properties.weight;
              totolLength += pathItemFeatures[i].properties.length;
            }
          }
          _routeDetail.push({
            description: "从此地出发——" + this.direction,
            weight: totolTime,
            length: totolLength,
          });
          this.routeDetail = _routeDetail;
          this.$notify({
            title: "成功",
            duration: 30000,
            message:
              "最短路径分析成功！【从此地出发—— " +
              this.direction +
              "】行程" +
              (totolLength / 1000).toFixed(2) +
              "千米,用时" +
              (totolTime / 60).toFixed(2) +
              "小时。详情请点击【生成行程报告】。",
            type: "success",
          });
        });
      });
    },
    //清除生成的路径
    clearRoute() {
      if (window.routeLayerOne != null) {
        window.map.removeLayer(window.routeLayerOne);
        window.routeLayerOne = null;
      }
      if (window.routeDirectLayerOne != null) {
        window.map.removeLayer(window.routeDirectLayerOne);
        window.routeDirectLayerOne = null;
      }
      this.routeDetail = [];
      this.direction = "";
    },
    //清除图层，主要是起点和终点
    clearLayer() {
      if (window.startLayerOne != null) {
        window.map.removeLayer(window.startLayerOne);
        window.startLayerOne = null;
      }
      if (window.directionLayer != null) {
        window.map.removeLayer(window.directionLayer); //搜索前清除已经搜索的图层
        window.directionLayer = null;
      }
      if (this.directionLayerTemp != null) {
        //如果没有加入到图层组，就从map中移除它
        window.map.removeLayer(this.directionLayerTemp);
        this.directionLayerTemp = null;
      }
      this.routeDetail = [];
      this.direction = "";
    },
    //生成行程报告
    createReport() {
      if (this.routeDetail.length == 0) {
        this.$message.info("还未进行路径分析，没有报告!");
        return;
      }
      var len = this.routeDetail.length;
      var title = this.routeDetail[len - 1]["description"];
      var title2 =
        "总行程:" +
        (this.routeDetail[len - 1]["length"] / 1000).toFixed(2) +
        "千米;总用时:" +
        (this.routeDetail[len - 1]["weight"] / 60).toFixed(2) +
        "小时";
      var steps = "";
      for (var i = 2; i < len - 1; i++) {
        steps += i - 1 + "." + this.routeDetail[i]["description"] + "</br>";
      }
      steps += len - 1 + "." + this.routeDetail[1]["description"] + "</br>";
      this.$alert(
        "<div style='overflow:auto;height: 250px;'><p style='color:red;font-size:16px;text-align:center'><strong>" +
          title +
          "</strong></p>" +
          "<p style='color:black;font-size:14px;text-align:center'><strong>" +
          title2 +
          "</strong></p>" +
          "<p style='color:black;font-size:12px'>" +
          steps +
          "</p></div>",
        {
          dangerouslyUseHTMLString: true,
          showConfirmButton: false,
          closeOnPressEscape: true,
          callback: () => {
            this.$message({
              type: "info",
              message: `已关闭行程分析报告`,
            });
          },
        }
      );
    },
    //折叠功能栏处理事件
    collapseHandler() {
      this.collapse2 = !this.collapse2;
    },
  },
};
</script>

<style lang="less" scoped>
.tools-main {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.tools-second {
  width: 100%;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
}
.el-input {
  margin-top: 4px;
  margin-left: 2px;
}
.el-button {
  margin-top: 4px;
  margin-left: 2px;
}
</style>
