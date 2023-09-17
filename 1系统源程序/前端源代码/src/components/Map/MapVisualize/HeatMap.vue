<template>
  <div>
    <div
      class="collapse"
      @click="collapseHandler"
      :style="{
        marginLeft: collapse2 ? '50px' : '500px',
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
    <div class="over-lay" v-show="!collapse2">
      <div class="tools-column">
        <div class="tools-main">
          <el-button
            style="background: #559ffb; color: white"
            type="primary"
            @click="createHeatMap"
            size="small"
            >生成热力图</el-button
          >
          <el-button
            type="warning"
            @click="dynamicHeatMap"
            size="small"
            style="color: white"
            >{{ dynamicText }}</el-button
          >
          <el-button
            style="color: white; margin-right: 5px"
            type="info"
            size="small"
            @click="clearHeatMap"
            >清除热力图</el-button
          >
        </div>
        <div class="tools-second">
          <span style="color: white; margin-left: 40px; font-size: 14px"
            >热力图生成半径：</span
          >
          <el-slider
            v-model="radius"
            show-input
            @input="danamicCreate"
            style="margin-right: 5px"
            input-size="mini"
          >
          </el-slider>
        </div>
      </div>

      <div class="levels">
        <span style="font-size: 16px; color: white; font-weight: bold"
          >景区等级</span
        >
        <el-checkbox
          :indeterminate="isIndeterminate"
          v-model="checkAll"
          @change="handleCheckAllChange"
          style="margin-left: 20px"
          >全选</el-checkbox
        >
        <el-checkbox-group
          v-model="checkedLevels"
          @change="handleCheckedLevelsChange"
        >
          <el-checkbox v-for="level in levels" :label="level" :key="level">{{
            level
          }}</el-checkbox>
        </el-checkbox-group>
      </div>
    </div>
  </div>
</template>

<script>
import L from "leaflet";
import HeatmapOverlay from "heatmap.js/plugins/leaflet-heatmap";
const levelOptions = ["1A", "2A", "3A", "4A", "5A"];

export default {
  data() {
    return {
      collapse2: false,
      checkAll: false,
      checkedLevels: ["5A"],
      levels: levelOptions,
      isIndeterminate: true,
      dynamicText: "动态生成热力图",
      radius: 0,
    };
  },
  methods: {
    //全选事件
    handleCheckAllChange(val) {
      this.checkedLevels = val ? levelOptions : [];
      this.isIndeterminate = false;
    },
    //复选框事件
    handleCheckedLevelsChange(value) {
      let checkedCount = value.length;
      this.checkAll = checkedCount === this.levels.length;
      this.isIndeterminate =
        checkedCount > 0 && checkedCount < this.levels.length;
    },
    //创建热力图
    createHeatMap() {
      var heatPoints = [];
      var radius = this.radius <= 5 ? 0.05 : this.radius / 100;
      // 配置
      var cfg = {
        radius: radius, //每一个热力点的半径
        maxOpacity: 0.8, //设置最大不透明度
        minOpacity: 0.3, //设置最小不透明度
        scaleRadius: true, //设置热力点是否平滑过渡
        useLocalExtrema: true, //使用局部极值
        blur: 0.95, //系数越高，渐变越平滑，默认是0.85
        latField: "lat", //纬度
        lngField: "lng", //经度
        valueField: "count", //热力点的值
        gradient: {
          0.99: "rgba(255,0,0,1)", // 颜色过渡和过渡比例
          0.9: "rgba(255,255,0,1)",
          0.8: "rgba(0,255,0,1 )",
          0.5: "rgba(0,255,255,1)",
          0: "rgba(0,0,255,1)",
        },
      };
      if (window.heatmapLayer != null) {
        window.map.removeLayer(window.heatmapLayer);
        window.heatmapLayer = null;
      }
      window.heatmapLayer = new HeatmapOverlay(cfg);
      window.heatmapLayer.addTo(window.map);
      var attributeFilter = "";

      if (this.checkedLevels.length == 0) return;
      else {
        for (var i = 0, len = this.checkedLevels.length; i < len; i++)
          attributeFilter += "等级='" + this.checkedLevels[i] + "' OR ";
        attributeFilter = attributeFilter.substring(
          0,
          attributeFilter.length - 3
        ); //去掉最后的OR
      }
      var sqlParam = new L.supermap.GetFeaturesBySQLParameters({
        queryParameter: {
          name: "A级旅游景区@SiChuan",
          attributeFilter: attributeFilter,
        },
        datasetNames: ["SiChuan:A级旅游景区"],
        toIndex: 10000,
      });
      new L.supermap.FeatureService(window.dataUrl).getFeaturesBySQL(
        sqlParam,
        (serviceResult) => {
          console.log(serviceResult.result.features.features);
          L.geoJSON(serviceResult.result.features, {
            //遍历获取热力图点
            onEachFeature: function (feature) {
              var point = feature.geometry;

              heatPoints.push({
                lng: point.coordinates[0],
                lat: point.coordinates[1],
                count: 1,
              });
            },
          });
          console.log(heatPoints);
          var heatData = {
            max: 8,
            data: heatPoints,
          };
          window.heatmapLayer.setData(heatData);
        }
      );
    },
    //通过滑条是否动态生成热力图
    dynamicHeatMap() {
      if (this.dynamicText == "动态生成热力图") {
        this.dynamicText = "取消动态生成热力图";
      } else {
        this.dynamicText = "动态生成热力图";
      }
    },
    //
    danamicCreate() {
      if (this.dynamicText == "取消动态生成热力图") {
        this.createHeatMap(); //这里有闪烁的效果，目前不知道怎么解决
      }
    },
    //清除热力图
    clearHeatMap() {
      if (window.heatmapLayer != null) {
        window.map.removeLayer(window.heatmapLayer);
        window.heatmapLayer = null;
      }
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

.el-slider {
  width: 60%;
}
.el-button {
  margin-top: 4px;
  margin-left: 4px;
}
</style>
