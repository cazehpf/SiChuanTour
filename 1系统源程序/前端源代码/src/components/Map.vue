<template>
  <div class="Container">
    <div id="map"></div>
    <el-container id="search">
      <el-autocomplete
        v-model="searchName"
        placeholder="请输入景点名"
        @keyup.enter.native="searchHandler"
        :fetch-suggestions="querySearchAsync"
      />
      <el-button
        type="primary"
        size="small"
        icon="el-icon-search"
        class="search-button"
        @click="searchHandler"
        >搜索</el-button
      >
      <el-button
        size="small"
        :disabled="isDiasbled"
        type="info"
        class="clear-button"
        @click="clearHandler"
        >清除</el-button
      >
    </el-container>
  </div>
</template>

<script>
import L from "leaflet";
import "@supermap/iclient-leaflet/dist/iclient-leaflet";
import "leaflet-minimap";
export default {
  name: "MapPage",
  data() {
    return {
      searchName: "",
      //被激活的链接地址
      resultLayer: null,
      isDiasbled: true,
      marker: null,
      interests: [],
    };
  },

  mounted() {
    this.asyncLoading(this.loadMap);
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
    //加载地图
    loadMap() {
      //添加天地图矢量和影像服务图层
      var tianditu_vec = new L.supermap.TiandituTileLayer({
        key: "e30c2ae658b877688726a99957b380b6",
        layerType: "vec",
      });
      var tianditu_vec2 = new L.supermap.TiandituTileLayer({
        key: "e30c2ae658b877688726a99957b380b6",
        layerType: "vec",
      });
      var tianditu_img = new L.supermap.TiandituTileLayer({
        key: "e30c2ae658b877688726a99957b380b6",
        layerType: "img",
      });
      // var tianditu_img2 = new L.supermap.TiandituTileLayer({
      //   key: "e30c2ae658b877688726a99957b380b6",
      //   layerType: "img",
      // });
      var tianditu_lab = new L.supermap.TiandituTileLayer({
        key: "1da96a515929a928dba3f7e957ca7f0b",
        labelType: "cva",
        isLabel: true,
      });

      //添加iServer服务图层
      var chengdu = new L.supermap.TiledMapLayer(window.url, {
        transparent: true,
        opacity: 0.9,
      });

      //实例化map对象，最初将添加到地图的图层阵列layers: [tiandituvec, chengdu]
      window.map = L.map("map", {
        crs: L.CRS.EPSG4326,
        center: { lng: 104.06, lat: 30.67 },
        zoom: 6,
        // maxZoom: 16,
        // minZoom: 5,
        layers: [tianditu_vec, chengdu],
        preferCanvas: true,
        attributionControl: false, //版权控件
        logoControl: false, //logo控件
      });
      //添加图层切换控件
      var baseMaps = {
        矢量图层: tianditu_vec,
        影像图层: tianditu_img,
      };
      var overlayMaps = {
        四川省专题图: chengdu,
        地图标签: tianditu_lab,
      };
      L.control.layers(baseMaps, overlayMaps).addTo(window.map);
      L.control
        .minimap(tianditu_vec2, {
          mapOptions: { logoControl: false },
          toggleDisplay: true,
        })
        .addTo(window.map);
      L.control.scale().addTo(window.map);
    },
    //搜索点击事件
    searchHandler() {
      var sqlParam = new L.supermap.GetFeaturesBySQLParameters({
        queryParameter: {
          name: "A级旅游景区@SiChuan",
          attributeFilter: "名称 like'%" + this.searchName + "%'",
          // attributeFilter: "",
        },
        datasetNames: ["SiChuan:A级旅游景区"],
        toIndex: 10000,
      });
      new L.supermap.FeatureService(window.dataUrl).getFeaturesBySQL(
        sqlParam,

        (serviceResult) => {
          var featuresResult = serviceResult.result.features.features;
          if (this.resultLayer != null) {
            window.map.removeLayer(this.resultLayer); //搜索前清除已经搜索的图层
          }
          if (this.marker != null) {
            window.map.removeLayer(this.marker); //搜索前清除已经搜索的图层
          }
          if (featuresResult.length > 0) this.isDiasbled = false; //如果结果大于0，则清除可用
          if (featuresResult.length == 1) {
            var feature = featuresResult[0];
            window.map.flyTo(
              [
                //只有一个景点时就定位到中心
                feature.geometry.coordinates[1],
                feature.geometry.coordinates[0],
              ],
              10
            );
            var oneTouristIcon = L.icon({
              iconUrl: "./imgs/oneTourist.png",
              // iconUrl: "require(../assets/icon/marker.png)",引入这里面的会失败

              // shadowUrl: '../img/leaf-shadow.png',
              iconSize: [60, 40],
              // shadowSize: [50, 64],
              // iconAnchor: [22, 94],
              // shadowAnchor: [4, 62],
              // popupAnchor: [-3, -76]
            });
            this.marker = L.marker(
              [
                feature.geometry.coordinates[1],
                feature.geometry.coordinates[0],
              ],
              { icon: oneTouristIcon }
            )
              .bindPopup(
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
              .addTo(window.map)
              .openPopup();
          } else {
            window.map.flyTo(
              [
                //多个景点时的缩放效果
                30.67, 104.06,
              ],
              5
            );
            this.resultLayer = L.geoJSON(featuresResult, {
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
            }).addTo(window.map);
          }
        }
      );
    },
    //清空搜索景点事件
    clearHandler() {
      if (this.resultLayer != null) {
        window.map.removeLayer(this.resultLayer); //搜索前清除已经搜索的图层
      }
      if (this.marker != null) {
        window.map.removeLayer(this.marker); //搜索前清除已经搜索的图层
      }
      this.searchName = "";
      this.isDiasbled = true;
    },
  },
};
</script>

<style scoped>
.Container {
  width: 100%;
  height: 100%;
}
#map {
  position: sticky;
  z-index: 1;
  width: 100%;
  height: 100%;
}

#search {
  z-index: 2;
  position: absolute;
  top: 20px;
  right: 130px;
}

.search-button {
  display: "inline-block";
  margin-left: 2px;
}

.clear-button {
  display: "inline-block";
  margin-left: 2px;
}

@import "https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/leaflet.css";
@import "https://iclient.supermap.io/dist/leaflet/iclient-leaflet.min.css";
</style>
