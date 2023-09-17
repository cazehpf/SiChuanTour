<template>
  <div>
    <div
      class="collapse"
      @click="collapseHandler"
      :style="{
        marginLeft: collapse2 ? '50px' : '700px',
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
    <div class="over-lay" style="width: 650px !important" v-show="!collapse2">
      <div>
        <el-card
          style="
            display: flex;
            float: left;
            border: none;
            background-color: rgba(0, 0, 0, 0);
          "
        >
          <el-button
            type="primary"
            size="small"
            @click="clicks"
            class="clear-button"
            >{{ mapClickOnText }}</el-button
          >
          <el-button
            type="warning"
            style="color: white"
            size="small"
            @click="open"
            >{{ showTable ? "关闭属性表" : " 显示属性表" }}</el-button
          >
          <el-button
            @click="close"
            type="info"
            size="small"
            class="clear-button"
            >清除</el-button
          >
        </el-card>
      </div>

      <transition name="fade">
        <el-table
          v-show="showTable"
          :data="travels"
          height="250"
          style="max-height: 400px; width: 650px; overflow-y: auto; float: left"
        >
          <el-table-column prop="id" label="序号"></el-table-column>
          <el-table-column prop="name" label="名称"></el-table-column>
          <el-table-column prop="address" label="地址"></el-table-column>
          <el-table-column prop="licence" label="许可证编号"></el-table-column>
          <el-table-column prop="phone" label="电话"></el-table-column>
          <el-table-column prop="faxnumber" label="传真"></el-table-column>
          <el-table-column prop="code" label="邮编"></el-table-column>
        </el-table>
      </transition>
    </div>
  </div>
  <!-- <div class="over-lay">
    <el-button type="info" @click="clicks">查询</el-button>
  </div> -->
</template>

<script>
import L from "leaflet";
export default {
  data() {
    return {
      collapse2: false,
      mapClickOnText: "启动地图点击查询",
      showTable: false,
      // tableData: [],
      travels: [],
    };
  },

  mounted() {
    window.resultTouristLayerSql2 = null;
  },

  methods: {
    //折叠功能栏处理事件
    collapseHandler() {
      this.collapse2 = !this.collapse2;
    },
    open() {
      this.showTable = !this.showTable;
    },
    close() {
      this.showTable = false;
      this.travels = [];
      if (window.resultTouristLayerSql2 != null) {
        window.map.removeLayer(window.resultTouristLayerSql2); //搜索前清除已经搜索的图层
        window.resultTouristLayerSql2 = null;
      }
      if (window.resultLayerSql2 != null) {
        window.map.removeLayer(window.resultLayerSql2); //搜索前清除已经搜索的图层
        window.resultLayerSql2 = null;
      }
    },
    clicks() {
      var onMapClick = (ev) => {
        if (window.resultTouristLayerSql2 != null) {
          window.map.removeLayer(window.resultTouristLayerSql2); //搜索前清除已经搜索的图层
          window.resultTouristLayerSql2 = null;
        }
        if (window.resultLayerSql2 != null) {
          window.map.removeLayer(window.resultLayerSql2); //搜索前清除已经搜索的图层
          window.resultLayerSql2 = null;
        }
        var polygon2 = L.marker(ev.latlng);
        var geometryParam = new L.supermap.GetFeaturesByGeometryParameters({
          datasetNames: ["SiChuan:市"],
          geometry: polygon2,
          spatialQueryMode: "INTERSECT",
          toIndex: 10000,
        });
        new L.supermap.FeatureService(window.dataUrl).getFeaturesByGeometry(
          geometryParam,
          (serviceResult) => {
            var featuresResult = serviceResult.result.features.features;

            window.resultLayerSql2 = L.geoJSON(featuresResult).addTo(
              window.map
            );
            var attributeFilter = "";
            var geometryParam = new L.supermap.GetFeaturesByGeometryParameters({
              datasetNames: ["SiChuan:旅行社点位"],
              geometry: window.resultLayerSql2,
              attributeFilter: attributeFilter,
              toIndex: 10000,
            });
            var _travels = [];
            new L.supermap.FeatureService(window.dataUrl).getFeaturesByGeometry(
              geometryParam,
              (serviceResult) => {
                console.log(serviceResult);
                window.resultTouristLayerSql2 = L.geoJSON(
                  serviceResult.result.features,
                  {
                    onEachFeature: function (feature, layer) {
                      // console.log(feature);
                      layer.bindPopup(
                        //这里写的比较简陋，后面可以进一步优化
                        "<div style='font-size:16px;opacity:0.5;margin:0px'><p>旅行社名：<strong>" +
                          feature.properties.旅行社中文 +
                          "</strong></p>" +
                          "<p>旅行社地址：" +
                          feature.properties.地址 +
                          "</p>" +
                          "<p>许可证编号：" +
                          feature.properties.许可证编号 +
                          "</p>" +
                          "<p>固定电话：" +
                          feature.properties.固定电话 +
                          "</p>" +
                          "<p>传真号码：" +
                          feature.properties.传真号码 +
                          "</p>" +
                          "<p>邮政编码：" +
                          feature.properties.邮政编码 +
                          "</p></div>",

                        {
                          minWidth: 200,
                          maxHeight: 400,
                          minHeight: 200,
                          maxWidth: 400,
                        }
                      );
                    },
                  }
                );
                var selecttravel;
                var featuresResult = serviceResult.result.features.features;
                for (var i = 0; i < featuresResult.length; i++) {
                  selecttravel = {
                    id: i + 1,
                    name: featuresResult[i].properties.旅行社中文,
                    address: featuresResult[i].properties.地址,
                    licence: featuresResult[i].properties.许可证编号,
                    phone: featuresResult[i].properties.固定电话,
                    faxnumber: featuresResult[i].properties.传真号码,
                    code: featuresResult[i].properties.邮政编码,
                  };
                  _travels.push(selecttravel);
                }
                if (serviceResult.result.features.features.length != 0) {
                  window.map.addLayer(window.resultTouristLayerSql2);
                }
              }
            );
            this.travels = _travels;
            console.log("this.travels", this.travels);
          }
        );
      };
      if (this.mapClickOnText == "启动地图点击查询") {
        window.map.off("click");
        window.map.on("click", onMapClick);
        this.mapClickOnText = "关闭地图查询";
      } else {
        window.map.off("click");
        this.mapClickOnText = "启动地图点击查询";
      }
    },
  },
};
</script>

<style lang="less" scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s;
}
.fade-enter, .fade-leave-to /* .fade-leave-active 在版本里已经被废弃 */ {
  opacity: 0;
}
.clear-button {
  text-align: center;
  color: white;
}
</style>
