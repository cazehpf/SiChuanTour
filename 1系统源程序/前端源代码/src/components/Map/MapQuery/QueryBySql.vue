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
      <div class="tools-row">
        <el-select
          v-model="city"
          filterable
          placeholder="选择市州"
          @change="cityChange"
          style="margin-right: 5px"
        >
          <el-option
            v-for="item in citys"
            :key="item.city"
            :label="item.label"
            :value="item.city"
          >
          </el-option>
        </el-select>
        <el-select
          v-model="county"
          filterable
          placeholder="选择区县"
          @change="countyChange"
          style="margin-right: 3px"
        >
          <el-option
            v-for="item in countys"
            :key="item.county"
            :label="item.label"
            :value="item.county"
          >
          </el-option>
        </el-select>

        <el-button
          type="primary"
          size="small"
          icon="el-icon-search"
          class="search-button"
          @click="searchHandler"
          >搜索</el-button
        >
        <el-button
          type="info"
          size="small"
          class="clear-button"
          @click="clearHandler"
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
const levelOptions = ["1A", "2A", "3A", "4A", "5A"];

// import "@supermap/iclient-leaflet/dist/iclient-leaflet";
export default {
  data() {
    return {
      collapse2: false,
      citys: [], //所有城市选项
      countys: [], //所有县选项

      city: "", //城市选项
      county: "", //区县选项
      checkAll: false,
      checkedLevels: ["5A"],
      levels: levelOptions,
      isIndeterminate: true,
      // center:[],//地图定位中心
      // resultLayer: null, //定位的行政区划图层
      // resultTouristLayer: null, //定位行政区域的景点图层
    };
  },
  created() {
    this.citys = JSON.parse(window.sessionStorage.getItem("citys"));
    console.log(2);
  },
  mounted() {
    var _citys = JSON.parse(window.sessionStorage.getItem("citys"));
    if (_citys == null) {
      this.asyncLoading(this.initCitys);
    }
  },

  methods: {
    //异步加载
    async asyncLoading(fun) {
      const loading = this.$loading({
        lock: true,
        text: "加载中",
        spinner: "el-icon-loading",
        background: "rgba(0, 0, 0, 0.7)",
      });
      await loading.close();
      fun();
    },

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
    //初始化市行政区划选项
    initCitys() {
      var sqlParam = new L.supermap.GetFeaturesBySQLParameters({
        queryParameter: {
          name: "市@SiChuan",
          attributeFilter: "",
        },
        datasetNames: ["SiChuan:市"],
        toIndex: 10000,
      });

      new L.supermap.FeatureService(window.dataUrl).getFeaturesBySQL(
        sqlParam,

        (serviceResult) => {
          var featuresResult = serviceResult.result.features.features;
          var selectCity;
          var _citys = [{ city: "0", label: "所有" }];
          for (var i = 0; i < featuresResult.length; i++) {
            selectCity = {
              city: i + 1,
              label: featuresResult[i].properties.市名称,
            };
            _citys.push(selectCity);
            this.citys = _citys;
            window.sessionStorage.setItem("citys", JSON.stringify(this.citys));
          }
        }
      );
    },

    //初始化区县行政区划选项
    getCountys(_city) {
      var _cityLabel = this.citys[_city].label;
      var sqlParam;
      if (_cityLabel == "所有") {
        sqlParam = new L.supermap.GetFeaturesBySQLParameters({
          queryParameter: {
            name: "县@SiChuan",
            attributeFilter: "",
          },
          datasetNames: ["SiChuan:县"],
          toIndex: 10000,
        });
      } else {
        sqlParam = new L.supermap.GetFeaturesBySQLParameters({
          queryParameter: {
            name: "县@SiChuan",
            attributeFilter: "市名称='" + _cityLabel + "'",
          },
          datasetNames: ["SiChuan:县"],
          toIndex: 10000,
        });
      }
      new L.supermap.FeatureService(window.dataUrl).getFeaturesBySQL(
        sqlParam,
        (serviceResult) => {
          var featuresResult = serviceResult.result.features.features;
          var selectCounty;
          var _countys = [{ county: "0", label: "所有" }];
          for (var i = 0; i < featuresResult.length; i++) {
            selectCounty = {
              county: i + 1,
              label: featuresResult[i].properties.区县名称,
            };
            _countys.push(selectCounty);
          }
          this.countys = _countys;

          this.county = this.countys[0].county; //选定市州后，显示其第一个区县
        }
      );
    },

    //定位到所选的市行政区，并高亮显示
    flyToCity(_city) {
      var _cityLabel = this.citys[_city].label;
      var sqlParam;
      if (_cityLabel == "所有") {
        sqlParam = new L.supermap.GetFeaturesBySQLParameters({
          queryParameter: {
            name: "省@SiChuan",
            attributeFilter: "",
          },
          datasetNames: ["SiChuan:省"],
          toIndex: 10000,
        });

        new L.supermap.FeatureService(window.dataUrl).getFeaturesBySQL(
          sqlParam,

          (serviceResult) => {
            var featuresResult = serviceResult.result.features.features;
            window.map.flyTo([30.67, 104.06], 5);
            if (window.resultLayerSql != null)
              window.map.removeLayer(window.resultLayerSql); //搜索前清除已经搜索的图层
            if (window.resultTouristLayerSql != null) {
              window.map.removeLayer(window.resultTouristLayerSql); //搜索前清除已经搜索的图层
            }
            window.resultLayerSql = L.geoJSON(featuresResult).addTo(window.map);
          }
        );
      } else {
        sqlParam = new L.supermap.GetFeaturesBySQLParameters({
          queryParameter: {
            name: "市@SiChuan",
            attributeFilter: "市名称='" + _cityLabel + "'",
          },
          datasetNames: ["SiChuan:市"],
          toIndex: 10000,
        });

        new L.supermap.FeatureService(window.dataUrl).getFeaturesBySQL(
          sqlParam,

          (serviceResult) => {
            var featuresResult = serviceResult.result.features.features;

            window.map.flyTo(
              [
                featuresResult[0].properties.CENTER_Y,
                featuresResult[0].properties.CENTER_X,
              ],
              7
            );
            if (window.resultLayerSql != null)
              window.map.removeLayer(window.resultLayerSql); //搜索前清除已经搜索的图层
            if (window.resultTouristLayerSql != null) {
              window.map.removeLayer(window.resultTouristLayerSql); //搜索前清除已经搜索的图层
            }
            window.resultLayerSql = L.geoJSON(featuresResult).addTo(window.map);
          }
        );
      }
    },
    //定位到所选的区县行政区，并高亮显示
    flyToCounty(_county) {
      var _countyLabel = this.countys[_county].label;
      var sqlParam;
      if (_countyLabel == "所有") {
        this.flyToCity(this.city);
      } else {
        sqlParam = new L.supermap.GetFeaturesBySQLParameters({
          queryParameter: {
            name: "县@SiChuan",
            attributeFilter: "区县名称='" + _countyLabel + "'",
          },
          datasetNames: ["SiChuan:县"],
          toIndex: 10000,
        });

        new L.supermap.FeatureService(window.dataUrl).getFeaturesBySQL(
          sqlParam,

          (serviceResult) => {
            var featuresResult = serviceResult.result.features.features;
            console.log(featuresResult);
            window.map.flyTo(
              [
                featuresResult[0].properties.CENTER_Y,
                featuresResult[0].properties.CENTER_X,
              ],
              10
            );
            if (window.resultLayerSql != null)
              window.map.removeLayer(window.resultLayerSql); //搜索前清除已经搜索的图层
            if (window.resultTouristLayerSql != null) {
              window.map.removeLayer(window.resultTouristLayerSql); //搜索前清除已经搜索的图层
            }
            window.resultLayerSql = L.geoJSON(featuresResult).addTo(window.map);
          }
        );
      }
    },
    //市选项改变时,定位到改变的市的位置，并存储sessionStorage
    cityChange() {
      this.flyToCity(this.city);
      this.getCountys(this.city);
    },
    //市选项改变时,定位到改变的市的位置
    countyChange() {
      this.flyToCounty(this.county);
    },

    //搜索行政区划景点
    searchHandler() {
      if (window.resultTouristLayerSql != null) {
        window.map.removeLayer(window.resultTouristLayerSql); //搜索前清除已经搜索的图层
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
      var geometryParam = new L.supermap.GetFeaturesByGeometryParameters({
        datasetNames: ["SiChuan:A级旅游景区"],
        geometry: window.resultLayerSql,
        attributeFilter: attributeFilter,
        toIndex: 10000,
      });
      new L.supermap.FeatureService(window.dataUrl).getFeaturesByGeometry(
        geometryParam,
        (serviceResult) => {
          console.log(serviceResult);
          window.resultTouristLayerSql = L.geoJSON(
            serviceResult.result.features,
            {
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
            }
          ).addTo(window.map);
        }
      );
    },
    //清除行政区划图层和景点图层按钮事件
    clearHandler() {
      if (window.resultLayerSql != null) {
        window.map.removeLayer(window.resultLayerSql); //搜索前清除已经搜索的图层
      }
      if (window.resultTouristLayerSql != null) {
        window.map.removeLayer(window.resultTouristLayerSql); //搜索前清除已经搜索的图层
      }
    },
    //折叠功能栏处理事件
    collapseHandler() {
      this.collapse2 = !this.collapse2;
    },
  },
};
</script>

<style scoped>
.search-button {
  margin: 2px;
  text-align: center;
  background: #559ffb;
  color: white;
}
.clear-button {
  text-align: center;
  color: white;
}
</style>
