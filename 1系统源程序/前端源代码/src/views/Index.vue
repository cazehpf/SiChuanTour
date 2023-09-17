<template>
  <el-container class="home-container">
    <el-header class="home-header">
      <el-row>
        <el-col
          :span="6"
          style="line-height: 30px; text-align: left; color: #fff"
        >
          <el-row :span="12">{{ userName }},欢迎您！</el-row>
          <el-row :span="12">{{ formattedTime }}</el-row>
        </el-col>
        <el-col
          :span="12"
          style="font-family: '楷体'; line-height: 60px;text-align=center ;color: #fff;font-size: 36px;"
          >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;四川省旅游资源服务平台</el-col
        >
        <el-col :span="6" style="line-height: 60px; text-align: right">
          <el-button @click="logout">退出</el-button></el-col
        >
      </el-row>
    </el-header>
    <el-container>
      <!-- 侧边栏 -->
      <el-aside :width="isCollapse ? '64px' : '240px'">
        <!-- 侧边栏菜单区 -->
        <div class="toggle-button" @click="toggleCollapse">|||</div>
        <template>
          <el-menu
            default-active="/index2/home"
            :collapse="isCollapse"
            text-color="#C6DFFF"
            active-text-color="#FFF"
            unique-opened
            :collapse-transition="false"
            router
          >
            <el-menu-item index="/home" @click="showHome">
              <template>
                <i class="el-icon-s-home"></i>
                <span slot="title">首页</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/interestsData" @click="showData">
              <template>
                <i class="el-icon-s-flag"></i>
                <span slot="title">景区数据</span>
              </template>
            </el-menu-item>
            <el-menu-item
              index="/mapExplorer"
              @click="showMap"
              :class="isMap ? 'active' : 'inactive'"
            >
              <template>
                <i class="el-icon-picture"></i>
                <span slot="title">地图服务</span>
              </template>
            </el-menu-item>
            <el-submenu index="3-1" v-show="isMap">
              <template slot="title">
                <i class="el-icon-s-help"></i>
                <span>地图查询</span>
              </template>
              <el-menu-item index="/mapExplorer/queryBySql">
                <i class="el-icon-menu"></i>

                <span>按行政区划查询景点</span></el-menu-item
              >
              <el-menu-item index="/mapExplorer/queryByBoundry">
                <i class="el-icon-menu"></i>

                <span>绘制查询景点</span></el-menu-item
              >
              <el-menu-item index="/mapExplorer/queryByMapClick">
                <i class="el-icon-menu"></i>

                <span>行政区点击查询旅行社</span></el-menu-item
              >
            </el-submenu>
            <el-submenu index="3-2" v-show="isMap">
              <template slot="title">
                <i class="el-icon-s-platform"></i>
                <span>地图可视化</span>
              </template>
              <el-menu-item index="/mapExplorer/scatterMap">
                <i class="el-icon-menu"></i>

                <span>景点散点图</span></el-menu-item
              >
              <el-menu-item index="/mapExplorer/heatMap">
                <i class="el-icon-menu"></i>

                <span>景点热力图</span></el-menu-item
              >
              <el-menu-item index="/mapExplorer/aggregationMap">
                <i class="el-icon-menu"></i>

                <span>景点聚合图</span></el-menu-item
              >
            </el-submenu>
            <el-submenu index="3-3" v-show="isMap">
              <template slot="title">
                <i class="el-icon-s-marketing"></i>
                <span>旅游出行</span>
              </template>
              <el-menu-item index="/mapExplorer/oneDirection">
                <i class="el-icon-menu"></i>

                <span>最佳自驾路径分析</span></el-menu-item
              >
              <el-menu-item index="/mapExplorer/mutiDirection">
                <i class="el-icon-menu"></i>

                <span>多景区自驾分析</span></el-menu-item
              >
            </el-submenu>

            <el-menu-item index="/me" @click="showMe">
              <template>
                <i class="el-icon-user-solid"></i>
                <span slot="title">我的</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/me/myCollection" v-show="isMe">
              <i class="el-icon-star-on"></i>

              <span slot="title">我的收藏</span></el-menu-item
            >
            <el-menu-item index="/me/myReply" v-show="isMe">
              <i class="el-icon-s-order"></i>

              <span slot="title">我的评论</span></el-menu-item
            >

            <el-menu-item index="/me/myLiked" v-show="isMe">
              <i class="el-icon-message-solid"></i>

              <span slot="title">收到的赞</span></el-menu-item
            >
          </el-menu>
        </template>
      </el-aside>

      <el-main>
        <QueryBySql
          v-if="this.$route.path == '/mapExplorer/queryBySql'"
        ></QueryBySql>
        <QueryByBoundry
          v-if="this.$route.path == '/mapExplorer/queryByBoundry'"
        ></QueryByBoundry>
        <QueryByMapClick
          v-if="this.$route.path == '/mapExplorer/queryByMapClick'"
        ></QueryByMapClick>
        <ScatterMap
          v-if="this.$route.path == '/mapExplorer/scatterMap'"
        ></ScatterMap>
        <HeatMap v-if="this.$route.path == '/mapExplorer/heatMap'"></HeatMap>
        <AggregationMap
          v-if="this.$route.path == '/mapExplorer/aggregationMap'"
        ></AggregationMap>
        <OneDirection
          v-if="this.$route.path == '/mapExplorer/oneDirection'"
        ></OneDirection>
        <MutiDirection
          v-if="this.$route.path == '/mapExplorer/mutiDirection'"
        ></MutiDirection>

        <Home v-if="this.$route.path == '/'"></Home>
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import Home from "@/components/Home.vue";
import QueryBySql from "@/components/Map/MapQuery/QueryBySql.vue";
import QueryByBoundry from "@/components/Map/MapQuery/QueryByBoundry.vue";
import QueryByMapClick from "@/components/Map/MapQuery/QueryByMapClick.vue";

import ScatterMap from "@/components/Map/MapVisualize/ScatterMap.vue";
import HeatMap from "@/components/Map/MapVisualize/HeatMap.vue";
import AggregationMap from "@/components/Map/MapVisualize/AggregationMap.vue";
import OneDirection from "@/components/Map/MapAnalysis/OneDirection.vue";
import MutiDirection from "@/components/Map/MapAnalysis/MutiDirection.vue";
export default {
  name: "IndexPage",
  components: {
    Home,
    QueryBySql,
    QueryByBoundry,
    QueryByMapClick,
    ScatterMap,
    HeatMap,
    AggregationMap,
    OneDirection,
    MutiDirection,
  }, //写在export default{}中与data同级表示使用组件

  data() {
    return {
      formattedTime: "",
      userName: "",
      isMap: false,
      isCollapse: false,
      isMe: false,
    };
  },
  mounted() {
    this.updateTime(); // 页面加载完成时先更新一次时间

    // 设置计时器，每秒更新时间
    setInterval(() => {
      this.updateTime();
    }, 1000);
    this.userName = window.sessionStorage.getItem("sysUserName");
    window.drawControl = null;
    window.map = null;
    //远程服务器服务
    window.networkUrl =
      "http://sicnu3s.club:8090/iserver/services/transportationAnalyst-SiChuan/rest/networkanalyst/Network@SiChuan";
    window.url =
      "http://sicnu3s.club:8090/iserver/services/map-SiChuan/rest/maps/SiChuan";
    window.dataUrl =
      "http://sicnu3s.club:8090/iserver/services/data-SiChuan/rest/data";
    //本地服务
    // window.networkUrl =
    //   "http://localhost:8090/iserver/services/transportationAnalyst-SiChuan/rest/networkanalyst/Network@SiChuan";
    // window.url =
    //   "http://localhost:8090/iserver/services/map-SiChuan/rest/maps/SiChuan";
    // window.dataUrl =
    //   "http://localhost:8090/iserver/services/data-SiChuan/rest/data";
  },
  methods: {
    updateTime() {
      const now = new Date();

      const year = now.getFullYear();
      const month = this.formatNumber(now.getMonth() + 1);
      const day = this.formatNumber(now.getDate());
      const hour = this.formatNumber(now.getHours());
      const minute = this.formatNumber(now.getMinutes());
      const second = this.formatNumber(now.getSeconds());
      const weekday = this.getWeekday(now.getDay());

      this.formattedTime = `${year}年${month}月${day}日 ${hour}:${minute}:${second} ${weekday}`;
    },
    formatNumber(number) {
      return number.toString().padStart(2, "0");
    },
    getWeekday(day) {
      const weekdays = [
        "星期日",
        "星期一",
        "星期二",
        "星期三",
        "星期四",
        "星期五",
        "星期六",
      ];
      return weekdays[day];
    },
    // 退出按钮
    logout() {
      this.$router.push("/login");
      this.$message.success("您已退出系统");
    },
    //点击按钮，切换菜单的折叠与展开
    toggleCollapse() {
      this.isCollapse = !this.isCollapse;
      this.$store.commit("setCollapse", this.isCollapse);
    },
    showMap() {
      this.isMap = true;
      this.isMe = false;
    },
    showMe() {
      this.isMap = false;
      this.isMe = true;
    },
    showHome() {
      this.isMap = false;
      this.isMe = false;
    },
    showData() {
      this.isMap = false;
      this.isMe = false;
    },
  },
};
</script>

<style scoped>
.home-header {
  background-color: #3498db;
}

.toggle-button {
  background-color: #023e66;
  font-size: 16px;
  line-height: 24px;
  color: #fff;
  text-align: center;
  letter-spacing: 0.2em;
  cursor: pointer;
}
.el-main {
  background-color: #fff;
  position: relative;
  padding: 5px;
}

.active {
  background-color: #3498db;
  color: #fff;
}
.inactive {
  color: #333;
}
.home-container {
  height: 100%;
}
</style>
