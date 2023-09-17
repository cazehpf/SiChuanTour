<template>
  <div>
    <div
      class="collapse"
      @click="collapseHandler"
      :style="{
        marginLeft: collapse2 ? '50px' : '400px',
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
    <div class="over-lay" v-show="!collapse2" style="width: 350px !important">
      <!-- 生成散点突出按钮 -->
      <div class="tools-row">
        <el-button
          size="small"
          type="primary"
          @click="createAggregateMap"
          style="color: white"
          >生成散点图</el-button
        >
        <el-button
          size="small"
          style="color: white"
          type="info"
          @click="clearAggregateMap"
          >清除散点图</el-button
        >
      </div>

      <!-- 图例 -->
      <div class="lengend">
        <el-col :span="4">
          <span><strong>图例：</strong></span>
        </el-col>

        <div
          v-for="item in colorCount"
          :key="item"
          :style="{
            'background-color': colors[item - 1],
            display: 'inline-block',
            width: '30px',
            height: '30px',
            lineHeight: '30px',
            textAlign: 'center',
            'border-radius': '50%',
            'margin-left': '15px',
            'margin-top': '5px',
          }"
        >
          {{ levels[item - 1] }}
        </div>
      </div>
      <!-- 景点等级 -->
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
const levelOptions = ["1A", "2A", "3A", "4A", "5A"];
export default {
  data() {
    return {
      collapse2: false,
      colors: ["#646cf5", "#65acfa", "#68d7e5", "#65fccc", "#5df580"],
      // radius: [1, 2, 3, 4, 5],
      colorCount: 5,
      checkAll: false,
      checkedLevels: ["5A"],
      levels: levelOptions,
      isIndeterminate: true,
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

    //生成高效率点图层
    createAggregateMap() {
      if (window.graphicLayer != null) {
        window.map.removeLayer(window.graphicLayer);
        window.graphicLayer = null;
      }
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
      var getFeatureBySQLParams = new L.supermap.GetFeaturesBySQLParameters({
        queryParameter: {
          name: "A级旅游景区@SiChuan",
          attributeFilter: attributeFilter,
        },
        datasetNames: ["SiChuan:A级旅游景区"],
        toIndex: 10000,
      });
      new L.supermap.FeatureService(window.dataUrl).getFeaturesBySQL(
        getFeatureBySQLParams,
        (serviceResult) => {
          var result = serviceResult.result.features;

          var features = result.features;
          var count = features.length;
          var graphics = [];
          var circleStyles = [];
          // 定义要素类样式
          for (var i = 0; i < this.colorCount; i++) {
            circleStyles.push(
              new L.supermap.CircleStyle({
                color: this.colors[i],
                opacity: 0.5,
                radius: 4,
                weight: 0,
                fill: true,
                fillColor: this.colors[i],
                fillOpacity: 0.5,
              })
            );
          }
          // 构建Graphic要素类
          for (var j = 0; j < count; j++) {
            var coordinates = [
              Number(features[j].geometry.coordinates[0]),
              Number(features[j].geometry.coordinates[1]),
            ];
            var index = parseInt(features[j].properties.等级[0]); //根据不同的等级赋予不同的样式
            graphics[j] = new L.supermap.Graphic({
              latLng: L.latLng(coordinates[1], coordinates[0]),
              style: circleStyles[index - 1].getStyle(),
              attributes: {
                名称: features[j].properties.名称,
                等级: features[j].properties.等级,
                详细地址: features[j].properties.地址,
              },
            });
          }
          // 创建高效率点图层GraphicLayer，并添加到地图中
          window.graphicLayer = new L.supermap.GraphicLayer(graphics, {
            render: "canvas",
            onClick: (graphic) => {
              L.popup()
                .setLatLng(graphic.getLatLng())
                .setContent(
                  "<div style='font-size:16px;opacity:0.5;margin:0px'><p>景区名：" +
                    graphic.attributes.名称 +
                    "</p>" +
                    "<p>等级：<strong>" +
                    graphic.attributes.等级 +
                    "</strong></p>" +
                    "<p>详细地址：" +
                    graphic.attributes.详细地址 +
                    "</p></div>"
                )
                .openOn(window.map);
            },
          }).addTo(window.map);
        }
      );
    },

    //清除高效率点图层
    clearAggregateMap() {
      if (window.graphicLayer != null) {
        window.map.removeLayer(window.graphicLayer);
        window.graphicLayer = null;
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
.lengend {
  padding: 10px;
  text-align: left;
  line-height: 40px;
  height: 40px;
  font-size: 16px;
  color: #fff;
}
</style>
