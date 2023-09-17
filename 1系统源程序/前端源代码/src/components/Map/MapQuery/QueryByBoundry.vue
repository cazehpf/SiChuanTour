<template>
  <div>
    <div
      class="collapse"
      @click="collapseHandler"
      :style="{
        marginLeft: collapse2 ? '50px' : '430px',
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
    <div class="over-lay" style="width: 380px !important" v-show="!collapse2">
      <div class="tools-row">
        <el-button
          type="primary"
          size="small"
          class="clear-button"
          @click="drawHandler"
          >{{ drawText }}</el-button
        >
        <el-button
          type="warning"
          @click="settingsHandler"
          class="clear-button"
          size="small"
          >{{ settingsText }}</el-button
        >

        <el-button
          type="info"
          size="small"
          @click="clearHandler"
          class="clear-button"
          >清除</el-button
        >
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
import "leaflet-draw";
const levelOptions = ["1A", "2A", "3A", "4A", "5A"];

export default {
  data() {
    return {
      collapse2: false,
      drawText: "展开——绘图面板",
      settingsText: "追加绘制",
      drawAdd: false, //默认使用不追加绘制
      checkAll: false,
      checkedLevels: ["1A"],
      levels: levelOptions,
      isIndeterminate: true,
    };
  },
  created() {
    // this.citys = JSON.parse(window.sessionStorage.getItem("citys"));
    // console.log(2);
  },
  mounted() {
    console.log(window.drawControl);
    console.log(this.settingsText);
  },
  beforeRouteLeave(to, from, next) {
    console.log(to, from);
    if (window.drawControl != null) {
      window.map.removeControl(window.drawControl);
      window.drawControl = null;
    }
    window.map.off(L.Draw.Event.CREATED); //直接把所有事件移除，单独移除好像没有反应，有什么更优的解法吗？
    //上述问题已经解决
    next();
  },

  methods: {
    //全选事件
    handleCheckAllChange(val) {
      this.checkedLevels = val ? levelOptions : [];
      this.isIndeterminate = false;

      this.handleCheckedLevelsChange(this.checkedLevels);
    },
    //复选框事件
    handleCheckedLevelsChange(value) {
      let checkedCount = value.length;
      this.checkAll = checkedCount === this.levels.length;
      this.isIndeterminate =
        checkedCount > 0 && checkedCount < this.levels.length;
      this.levelChange();
    },
    //根据单个指定图层边界查询景点
    selectByLayer(_layer) {
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
      var geometryParam = new L.supermap.GetFeaturesByGeometryParameters({
        datasetNames: ["SiChuan:A级旅游景区"],
        geometry: _layer,
        spatialQueryMode: "INTERSECT",
        attributeFilter: attributeFilter,
        toIndex: 10000,
      });
      new L.supermap.FeatureService(window.dataUrl).getFeaturesByGeometry(
        geometryParam,
        (serviceResult) => {
          var resultTouristLayer = L.geoJSON(serviceResult.result.features, {
            onEachFeature: function (feature, layer) {
              // console.log(feature);
              layer.bindPopup(
                //这里写的比较简陋，后面可以进一步优化
                "<div style='font-size:16px;opacity:0.5;margin:0px'><p>景区名：" +
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
                  "</p></div>",

                {
                  minWidth: 200,
                  maxHeight: 400,
                  minHeight: 200,
                  maxWidth: 400,
                }
              );
            },
          });
          if (serviceResult.result.features.features.length != 0) {
            window.resultTouristLayersBou.addLayer(resultTouristLayer);
          }
        }
      );
    },

    //展开绘图控件时的事件
    drawHandler() {
      if (this.drawText == "展开——绘图面板") {
        //这里可能会有点问题

        if (window.resultTouristLayersBou == null) {
          //清空查询景点后会将其重置，所以这里要重新初始化
          window.resultTouristLayersBou = new L.FeatureGroup();
          window.map.addLayer(window.resultTouristLayersBou);
        }
        if (window.editableLayersBou == null) {
          //清空查询景点后会将其重置，所以这里要重新初始化
          window.editableLayersBou = new L.FeatureGroup();
          window.map.addLayer(window.editableLayersBou);
        }

        // window.map.addLayer(window.editableLayersBou);

        // window.map.addLayer(window.resultTouristLayersBou);

        var options = {
          position: "topleft",
          draw: {
            polyline: {}, // 线
            polygon: {}, // 面
            circle: {}, // 圆
            rectangle: {}, // 矩形
            marker: {}, // 标记点
            remove: {},
          },
          edit: {
            featureGroup: window.editableLayersBou,
            remove: true,
          },
        };
        window.drawControl = new L.Control.Draw(options); // 创建并添加绘制控件
        window.map.addControl(window.drawControl);
        this.handleMapEvent(window.drawControl._container, window.map);
        window.map.off(L.Draw.Event.CREATED);
        window.map.on(L.Draw.Event.CREATED, (e) => {
          if (window.resultTouristLayersBou == null) {
            //清空查询景点后会将其重置，所以这里要重新初始化
            window.resultTouristLayersBou = new L.FeatureGroup();
            window.map.addLayer(window.resultTouristLayersBou);
          }
          if (window.editableLayersBou == null) {
            //清空查询景点后会将其重置，所以这里要重新初始化
            window.editableLayersBou = new L.FeatureGroup();
            window.map.addLayer(window.editableLayersBou);
          }
          //画图时事件
          console.log(this.settingsText);
          if (this.settingsText == "追加绘制") {
            window.editableLayersBou.clearLayers();
            window.resultTouristLayersBou.clearLayers();
          }

          var layer = e.layer;

          window.editableLayersBou.addLayer(layer);

          this.selectByLayer(layer);
          // window.draw = "notnull";
        });

        this.drawText = "折叠——绘图面板";
      } else {
        window.map.removeControl(window.drawControl);
        window.drawControl = null;
        this.drawText = "展开——绘图面板";
      }
    },
    //地图事件函数
    handleMapEvent(div, map) {
      //鼠标经过绘图控件
      if (!div || !map) {
        return;
      }
      div.addEventListener("mouseover", function () {
        map.scrollWheelZoom.disable();
        map.doubleClickZoom.disable();
      });
      div.addEventListener("mouseout", function () {
        map.scrollWheelZoom.enable();
        map.doubleClickZoom.enable();
      });
    },
    //清除景点图层事件
    clearHandler() {
      window.map.removeLayer(window.resultTouristLayersBou);
      window.resultTouristLayersBou = null;
      window.map.removeLayer(window.editableLayersBou);
      window.editableLayersBou = null;
    },
    //是否追加绘制
    settingsHandler() {
      if (this.settingsText == "追加绘制") {
        this.settingsText = "不追加绘制";
      } else {
        this.settingsText = "追加绘制";
      }
    },
    //等级修改后，清空所有图层，重新对所有绘制图层按照几何进行查询
    levelChange() {
      // window.map.removeLayer(window.resultTouristLayersBou);
      // console.log(this.editableLayers);
      if (window.resultTouristLayersBou != null) {
        window.map.removeLayer(window.resultTouristLayersBou);
        window.resultTouristLayersBou = null;
        window.resultTouristLayersBou = new L.FeatureGroup();
        window.map.addLayer(window.resultTouristLayersBou);
        window.editableLayersBou.eachLayer((layer) => {
          this.selectByLayer(layer);
        });
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
.clear-button {
  text-align: center;
  color: white;
}
@import "leaflet-draw/dist/leaflet.draw.css";
</style>
