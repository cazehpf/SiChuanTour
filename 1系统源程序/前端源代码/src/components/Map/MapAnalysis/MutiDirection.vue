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

window.networkUrl =
  "http://58.87.89.148:8090/iserver/services/transportationAnalyst-SiChuan/rest/networkanalyst/Network@SiChuan";
// const directionIcon = L.icon({
//   iconUrl: "./imgs/direction.png",
//   iconSize: [60, 40],
// });
const startIcon = L.icon.pulse({
  iconSize: [10, 10],
  color: "blue",
  fillColor: "blue",
});
const directionIcon = L.icon.pulse({
  iconSize: [10, 10],
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
      collapse2: false,
      routeDetail: [],
      searchName: "",
      directionTemp: "",
      directions: [],
      directionLayerTemp: null,
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
      if (window.directionLayers == null) {
        window.directionLayers = new L.FeatureGroup();
        window.map.addLayer(window.directionLayers);
      }
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
          this.directionTemp = feature.properties.名称; //如果搜索之后，没有点击添加，则下次搜索时删除上一次搜索的结果
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
        window.map.removeLayer(this.directionLayerTemp); //地图上移除单图层

        window.directionLayers.addLayer(this.directionLayerTemp); //目的景点图层组加上他
        this.directionLayerTemp = null;
        this.directions.push(this.directionTemp);
        this.$message.success("目的景点添加成功！");
      } else {
        this.$message.warning(
          "目的景点已经添加或不存在，请重新点击搜索定位景点！"
        );
      }
    },
    //添加出发点事件
    addStart() {
      window.map.off("click");
      window.map.on("click", (e) => {
        if (window.startLayerMul != null) {
          window.map.removeLayer(window.startLayerMul);
          window.startLayerMul = null;
        }
        window.startLayerMul = L.marker(e.latlng, { icon: startIcon }).addTo(
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
      if (window.startLayerMul == null) {
        this.$message.warning("您还没有添加出发地点，无法进行路径分析");
        return;
      }
      if (this.directions.length == 0) {
        //如果目的景点数组长度为0，这里没有判断景点图层数组的长度，因为不知道他的方法，相当于重新映射了一下，换了个思路
        this.$message.warning("您还没有添加目的景点，无法进行路径分析");
        return;
      }
      if (window.routeLayerMul != null) {
        window.map.removeLayer(window.routeLayerMul);
        window.routeLayerMul = null;
      }
      if (window.routeDirectLayerMul != null) {
        window.map.removeLayer(window.routeDirectLayerMul);
        window.routeDirectLayerMul = null;
      }
      var nodesTSP = [];
      nodesTSP.push(window.startLayerMul.getLatLng()); //起点
      window.directionLayers.eachLayer(function (layer) {
        //中间点
        nodesTSP.push(layer.getLatLng());
      });
      nodesTSP.push(window.startLayerMul.getLatLng()); //终点
      //创建多旅行商分析服务实例
      var findTSPPathsService = new L.supermap.NetworkAnalystService(
        window.networkUrl
      );
      //创建多旅行商分析参数实例
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
      var findTSPPathsParameter = new L.supermap.FindTSPPathsParameters({
        //是否指定终止点,这里可以用一个设置按钮控制，默认指定
        endNodeAssigned: true,
        isAnalyzeById: false,
        //旅行商分析途经点数组，必设字段
        nodes: nodesTSP,
        parameter: analystParameter,
      });
      //进行查找
      findTSPPathsService.findTSPPaths(
        findTSPPathsParameter,
        (serviceResult) => {
          var result = serviceResult.result;
          result.tspPathList.map((result) => {
            window.routeLayerMul = L.geoJSON(result.route, {
              style: function () {
                return { color: "#0f0", weight: 8 };
              },
            }).addTo(window.map);
            var lnglats = [];
            var features = result.route.geometry.coordinates[0];
            for (var k = 0, leng = features.length; k < leng; k++) {
              lnglats.push([features[k][1], features[k][0]]);
            }

            var routePolyLine = L.polyline(lnglats, {
              color: "green",
              weight: 3,
            });
            window.routeDirectLayerMul = L.polylineDecorator(routePolyLine, {
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

            // console.log("result", result);

            // console.log("stopIndexes", result.stopIndexes); //这个stopindexes就是directions的排序
            // console.log("stopWeights", result.stopWeights);
            // console.log(this.directions);
            var directionsText = "";
            var totolTime = 0;
            var len2 = result.stopIndexes.length;
            var len3 = result.stopWeights.length;
            for (var i3 = 0; i3 < len3; i3++) {
              totolTime += result.stopWeights[i3];
            }
            this.routeDetail.push({
              description: this.directions.length + "个景点的路径规划报告",
              time: (totolTime / 60).toFixed(2),
            });
            this.routeDetail.push({
              description:
                "从此地出发——" + this.directions[result.stopIndexes[1] - 1],
              time: (result.stopWeights[0] / 60).toFixed(2),
            });
            for (var i2 = 1; i2 < len2 - 1; i2++) {
              directionsText +=
                this.directions[result.stopIndexes[i2] - 1] + "——";
              if (i2 == len2 - 2) continue; //这个就是尾巴景点到出发点
              this.routeDetail.push({
                description:
                  this.directions[result.stopIndexes[i2] - 1] +
                  "——" +
                  this.directions[result.stopIndexes[i2 + 1] - 1],
                time: (result.stopWeights[i2] / 60).toFixed(2),
              });
            }
            this.routeDetail.push({
              description:
                this.directions[result.stopIndexes[len2 - 2] - 1] +
                "——回到此地",
              time: (result.stopWeights[len3 - 1] / 60).toFixed(2),
            });

            this.$notify({
              title: "成功",
              duration: 30000,
              message:
                "最短路径分析成功！【从此地出发—— " +
                directionsText +
                "回到此地】用时" +
                (totolTime / 60).toFixed(2) +
                "小时。详情请点击【生成行程报告】。",
              type: "success",
            });
          });
        }
      );
    },
    //清除生成的路径
    clearRoute() {
      if (window.routeLayerMul != null) {
        window.map.removeLayer(window.routeLayerMul);
        window.routeLayerMul = null;
      }
      if (window.routeDirectLayerMul != null) {
        window.map.removeLayer(window.routeDirectLayerMul);
        window.routeDirectLayerMul = null;
      }
      this.routeDetail = [];
      this.directions = [];
    },
    //清除图层，主要是起点和终点
    clearLayer() {
      if (window.startLayerMul != null) {
        window.map.removeLayer(window.startLayerMul);
        window.startLayerMul = null;
      }
      if (window.directionLayers != null) {
        window.map.removeLayer(window.directionLayers); //搜索前清除已经搜索的图层
        window.directionLayers = null;
      }
      if (this.directionLayerTemp != null) {
        //如果没有加入到图层组，就从map中移除它
        window.map.removeLayer(this.directionLayerTemp);
        this.directionLayerTemp = null;
      }
      this.routeDetail = [];
      this.directions = [];
    },
    //生成行程报告
    createReport() {
      if (this.routeDetail.length == 0) {
        this.$message.info("还未进行路径分析，没有报告!");
        return;
      }
      var len = this.routeDetail.length;
      var title = this.routeDetail[0]["description"];
      var title2 = "总用时:" + this.routeDetail[0]["time"] + "小时";
      var steps = "";
      for (var i = 1; i < len; i++) {
        steps +=
          i +
          "." +
          this.routeDetail[i]["description"] +
          ",耗时:" +
          this.routeDetail[i]["time"] +
          "小时" +
          "</br>";
      }
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
