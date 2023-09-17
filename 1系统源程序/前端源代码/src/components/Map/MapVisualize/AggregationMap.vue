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
      <div class="tools-row">
        <el-button
          type="primary"
          size="small"
          @click="createAggregateMap"
          style="color: white"
          >生成聚合图</el-button
        >

        <el-button
          style="color: white"
          size="small"
          type="info"
          @click="clearAggregateMap"
          >清除聚合图</el-button
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

// 引入 leaflet.markercluster
import "leaflet.markercluster/dist/MarkerCluster.css";
import "leaflet.markercluster/dist/MarkerCluster.Default.css";
import "leaflet.markercluster";
const levelOptions = ["1A", "2A", "3A", "4A", "5A"];

export default {
  data() {
    return {
      collapse2: false,
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

    //生成聚合图
    createAggregateMap() {
      if (window.aggregateLayer != null) {
        window.map.removeLayer(window.aggregateLayer);
        window.aggregateLayer = null;
      }
      window.aggregateLayer = L.markerClusterGroup({
        spiderfyOnMaxZoom: false,
        showCoverageOnHover: false,
        zoomToBoundsOnClick: false,
      });
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
          if (!result || !result.features || result.features.length < 1) {
            return;
          }
          result.features.map((feature) => {
            window.aggregateLayer.addLayer(
              L.marker([
                feature.geometry.coordinates[1],
                feature.geometry.coordinates[0],
              ]).bindPopup(
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
                  "</p></div>"
              )
            );
          });
          window.aggregateLayer.addTo(window.map);
        }
      );
    },
    //清除聚合图
    clearAggregateMap() {
      if (window.aggregateLayer != null) {
        window.map.removeLayer(window.aggregateLayer);
        window.aggregateLayer = null;
      }
    },
    //折叠功能栏处理事件
    collapseHandler() {
      this.collapse2 = !this.collapse2;
    },
  },
};
</script>

<style lang="less" scoped></style>
