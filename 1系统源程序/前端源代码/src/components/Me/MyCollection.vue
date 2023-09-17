<template>
  <div class="container">
    <!-- 景点条件过滤 -->
    <el-row :span="2" :gutter="10">
      <el-col :span="9"><div class="search-empty"></div></el-col>
      <el-col :span="6"
        ><div class="place-filter">
          <el-col :span="6">
            <span>地区：</span>
          </el-col>
          <el-col :span="9">
            <el-select
              v-model="city"
              filterable
              placeholder="选择市州"
              @change="cityChange"
            >
              <el-option
                v-for="item in citys"
                :key="item.city"
                :label="item.label"
                :value="item.city"
              >
              </el-option>
            </el-select>
          </el-col>
          <el-col :span="9">
            <el-select v-model="county" filterable placeholder="选择区县">
              <el-option
                v-for="item in countys"
                :key="item.county"
                :label="item.label"
                :value="item.county"
              >
              </el-option>
            </el-select>
          </el-col></div
      ></el-col>

      <el-col :span="9"
        ><div class="level-filter">
          <el-col :span="5">
            <span>等级：</span>
          </el-col>
          <el-col :span="4">
            <el-checkbox
              :indeterminate="isIndeterminate"
              v-model="checkAll"
              @change="handleCheckAllChange"
              >全选</el-checkbox
            >
          </el-col>
          <el-col :span="15">
            <el-checkbox-group
              v-model="checkedLevels"
              @change="handleCheckedLevelsChange"
            >
              <el-col v-for="level in levels" :key="level" :span="4.2">
                <el-checkbox :label="level">{{ level }}</el-checkbox>
              </el-col>
            </el-checkbox-group>
          </el-col>
        </div></el-col
      >
    </el-row>
    <!-- 搜索 -->
    <el-row :span="2">
      <el-col :span="9">
        <div class="pagination">
          <el-pagination
            :page-size="pageSize"
            :page-count="totalPages"
            :current-page="currentPage"
            layout="total, prev, pager, next, jumper"
            @current-change="handlePageChange"
            :total="this.total"
          ></el-pagination>
        </div>
      </el-col>
      <el-col :span="6"><div class="search-empty"></div></el-col>
      <el-col :span="7"
        ><div class="text-filter">
          <el-col :span="7"><span>文字：</span></el-col>
          <el-col :span="17"
            ><el-input v-model="searchText" placeholder="请输入..."></el-input
          ></el-col></div
      ></el-col>
      <el-col :span="2"
        ><div class="search">
          <el-button
            type="primary"
            size="medium"
            icon="el-icon-search"
            @click="handleSearch"
            >搜索</el-button
          >
        </div></el-col
      >
    </el-row>
    <!-- 景点详情页 -->
    <el-row :gutter="16">
      <el-col v-for="image in Images" :key="image.interestID" :span="8">
        <div class="card">
          <div class="image-container">
            <router-link :to="getInterestPage(image.interestID)"
              ><MyImage class="card-image" :src="image.interestImg" alt="Image"
            /></router-link>

            <div class="text-overlay">
              <span class="text">{{ image.interestName }}</span>
              <p
                style="
                  font-size: 12px;
                  display: -webkit-box;
                  -webkit-box-orient: vertical;
                  -webkit-line-clamp: 2;
                  overflow: hidden;
                "
              >
                {{ image.interestDescription }}
              </p>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import MyImage from "@/components/MyImage.vue";

import L from "leaflet";
import "@supermap/iclient-leaflet/dist/iclient-leaflet";
const levelOptions = ["1A", "2A", "3A", "4A", "5A"];
export default {
  name: "MyCollection",
  components: { MyImage },
  data() {
    return {
      citys: [], //所有城市选项
      countys: [], //所有县选项
      city: "", //城市选项
      county: "", //区县选项
      checkAll: true, //
      checkedLevels: ["1A", "2A", "3A", "4A", "5A"],
      levels: levelOptions,
      isIndeterminate: false,
      searchText: "",
      isSearch: false,
      totalPages: 1,
      total: 1,
      currentPage: 1,
      pageSize: 6, // 每页显示的图片数量
      Images: [], // 正在加载的图片
    };
  },
  mounted() {
    var _citys = JSON.parse(window.sessionStorage.getItem("citys"));
    if (_citys == null) {
      this.asyncLoading(this.initCitys);
    }
    this.getImgInit();
  },
  created() {
    this.citys = JSON.parse(window.sessionStorage.getItem("citys"));
  },
  methods: {
    //页面跳转
    getInterestPage(id) {
      return `/interestsData/${id}`;
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
    //分页切换事件
    handlePageChange(newPage) {
      this.currentPage = newPage;
      if (this.isSearch) {
        var _city, _county;
        var level = this.checkedLevels.join(",");
        try {
          _city =
            this.citys[this.city].label == "所有"
              ? ""
              : this.citys[this.city].label;
          _county =
            this.countys[this.county].label == "所有"
              ? ""
              : this.countys[this.county].label;
        } catch {
          _city = "";
          _county = "";
        }
        var url =
          "/userCollect?page=" +
          this.currentPage +
          "&level=" +
          level +
          "&city=" +
          _city +
          "&county=" +
          _county +
          "&name=" +
          this.searchText +
          "&userID=" +
          window.sessionStorage.getItem("sysUserID");
        this.request.get(url).then((res) => {
          if (res.code === 200) {
            console.log(url, res);
            this.total = res.data.total;
            this.totalPages = Math.ceil(res.data.total / this.pageSize);
            this.Images = res.data.rows;
          }
        });
      } else {
        this.request
          .get(
            "/userCollect?page=" +
              this.currentPage +
              "&userID=" +
              window.sessionStorage.getItem("sysUserID")
          )
          .then((res) => {
            if (res.code === 200) {
              console.log("/interestPage?" + this.currentPage, res);
              this.total = res.data.total;

              this.totalPages = Math.ceil(res.data.total / this.pageSize);
              this.Images = res.data.rows;
            }
          });
      }
    },
    //搜索按钮事件
    handleSearch() {
      this.isSearch = true;
      this.currentPage = 1;
      var _city, _county;
      var level = this.checkedLevels.join(",");
      try {
        _city =
          this.citys[this.city].label == "所有"
            ? ""
            : this.citys[this.city].label;
        _county =
          this.countys[this.county].label == "所有"
            ? ""
            : this.countys[this.county].label;
      } catch {
        _city = "";
        _county = "";
      }
      var url =
        "/userCollect?page=" +
        this.currentPage +
        "&level=" +
        level +
        "&city=" +
        _city +
        "&county=" +
        _county +
        "&name=" +
        this.searchText +
        "&userID=" +
        window.sessionStorage.getItem("sysUserID");
      this.request.get(url).then((res) => {
        if (res.code === 200) {
          this.total = res.data.total;

          this.totalPages = Math.ceil(res.data.total / this.pageSize);
          this.Images = res.data.rows;
          console.log("this.Images" + this.currentPage, this.Images);
        }
      });
    },
    //初始化第一页景点
    getImgInit() {
      this.request
        .get(
          "/userCollect?" +
            "userID=" +
            window.sessionStorage.getItem("sysUserID")
        )
        .then((res) => {
          if (res.code === 200) {
            this.total = res.data.total;

            this.totalPages = Math.ceil(res.data.total / this.pageSize);
            this.Images = res.data.rows;
            console.log("this.Images", this.Images);
          }
        });
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
    //市选项改变时,定位到改变的市的位置，并存储sessionStorage
    cityChange() {
      this.getCountys(this.city);
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
  },
};
</script>

<style scoped>
.place-filter {
  height: 40px;
  line-height: 40px;
  font-size: 14px;
  text-align: right;
}
.level-filter {
  height: 40px;
  line-height: 40px;
  font-size: 14px;
  text-align: right;
}
.checkbox-group .el-checkbox {
  margin-right: 5px; /* 设置复选框之间的间隔为 20px */
}

.el-input__inner {
  height: 30px !important;
  line-height: 30px !important;
}

.text-filter {
  height: 40px;
  line-height: 40px;
  font-size: 14px;
  text-align: right;
}
.search-empty {
  height: 40px;
}

.card {
  position: relative;
  height: 260px;
  padding: 0px;
  margin-bottom: 10px;
  color: #c7c6c6;
}
.pagination {
  height: 40px;
  float: left;
}
.image-container {
  position: relative;
  overflow: hidden;
}

.card-image {
  width: 100%;
  height: 260px;
  display: block;
}

.text-overlay {
  position: absolute;
  width: 100%;
  height: 60px;
  bottom: 0;
  right: 0;
  background-color: rgba(0, 0, 0, 0.6);
  padding: 8px;
}

.text {
  color: rgb(243, 10, 10);
}
.search {
  height: 40px;
  float: right;
}
.interests-pic {
  height: 260px;
  background-color: #df0404;
}
.interests-empty {
  height: 10px;
  background-color: #09f0ed;
}
.container {
  color: #353434;
  background-color: #fff;
  border-radius: 5px;
  padding: 5px;
  box-shadow: 0 0 25px #909399;
}
.el-pagination .el-pagination__jump .el-pagination__editor {
  color: green;
}
</style>
