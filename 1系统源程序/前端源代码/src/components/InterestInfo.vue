<template>
  <div id="container">
    <!-- 左上 -->
    <div class="left-top">
      <div style="float: left">
        <el-button
          size="small"
          type="primary"
          style="width: 80px"
          @click="handleBack"
          >《《返回</el-button
        >
      </div>
      <br />
      <br />
      <br />
      <span
        style="
          float: left;
          font-size: 30px;
          font-family: 黑体;
          font-weight: bold;
        "
        >{{ interestDetail.interestName }}</span
      >
      <span style="font-size: 16px; font-family: 黑体"> {{ Weather }}</span>
      <br />
      <br />
      <br />
      <br />
      <div class="star-rating">
        <div class="star-rating">
          <div class="left-info">
            <span style="font-size: 20px; font-family: 黑体"
              >{{ averageValue == 4.9 ? 5 : averageValue }}分</span
            >
            <br />
            <span style="font-size: 20px; font-family: 黑体"
              >{{ total }}人评价过</span
            >
          </div>
          <!-- 星星和收藏按钮容器 -->
          <div class="stars-and-favorite">
            <div class="stars">
              <el-rate
                v-model="averageValue"
                :max="5"
                disabled
                score-template="{value}"
              >
              </el-rate>
            </div>
            <div class="collect">
              <v-btn style="" @click="toggleFavorite(index)" light icon>
                <v-icon style="color: #df0404">{{
                  favorite ? "mdi-heart" : "mdi-heart-outline"
                }}</v-icon>
              </v-btn>
            </div>
          </div>
        </div>
      </div>
      <div
        class="spot-info"
        style="display: flex; text-align: left; font-family: 黑体"
      >
        <span>等级: {{ interestDetail.interestLevel }}</span>
        <span>所属市州: {{ interestDetail.interestCity }}</span>
        <span>所属区县: {{ interestDetail.interestCounty }}</span>
        <span> 详细地址: {{ interestDetail.interestAddress }}</span>
      </div>
      <br />
      <br /><br />
      <br />
      <br />
      <div style="float: right">
        <el-button
          @click="this.showDialog"
          class="rating-button"
          type="primary"
          size="small"
          >写评价
        </el-button>
      </div>
    </div>

    <!-- 左下 -->
    <div class="left-bottom">
      <img
        :src="interestDetail.interestImg"
        height="250"
        width="400"
        alt="Spot Image"
      />
    </div>
    <!-- 右侧 -->

    <div class="right">
      <div class="right-top">
        <div class="right-head-title">
          <span style="text-align: left">评论区</span>
        </div>

        <div class="pagination">
          <el-pagination
            :page-size="pageSize"
            :pager-count="5"
            :page-count="totalPages"
            :current-page="currentPage"
            layout="total, prev, pager, next, jumper"
            @current-change="handlePageChange2"
            :total="this.total"
          ></el-pagination>
        </div>
      </div>
      <div class="right-bottom">
        <el-card
          class="content"
          v-for="index in interestComments.length"
          :key="index"
        >
          <!-- 右上角按钮容器 div -->
          <div class="toolbt">
            <!-- 浮动按钮 -->
            <el-popover placement="left" trigger="hover" width="120">
              <el-button
                slot="reference"
                style="background: transparent; border: none"
                >。。。</el-button
              >
              <div>
                <!--判断当前登录的用户名和评论用户名是否相同，若不相同删除按钮就不能使用-->
                <el-button
                  :disabled="interestComments[index - 1].userName != userName"
                  @click="deleteItem(index)"
                  type="danger"
                  size="mini"
                  >删除
                </el-button>
                <el-button
                  :disabled="interestComments[index - 1].userName == userName"
                  @click="reportItem(index)"
                  type="warning"
                  size="mini"
                  >举报</el-button
                >
              </div>
            </el-popover>
          </div>
          <!--头像及其发布时间和名字-->
          <div class="avatar-and-info">
            <div class="circular-avatar">
              <img :src="interestComments[index - 1].userImg" alt="Avatar" />
            </div>
            <div class="userinfo">
              <span style="font-size: 20px; font-family: 楷体">{{
                interestComments[index - 1].userName
              }}</span>
              <span style="font-size: 8px; font-family: 楷体">{{
                interestComments[index - 1].commentTime | formatDate
              }}</span>
              <div class="stars">
                <span
                  v-for="starIndex in interestComments[index - 1].commentScore"
                  :key="starIndex"
                  style="color: #ffdb58"
                  class="star-filled"
                >
                  ★
                </span>
              </div>
            </div>
          </div>
          <!-- 显示评论内容 -->
          <div
            class="comment"
            style="
              margin-top: 15px;
              float: left;
              font-size: 20px;
              font-family: 楷体;
            "
          >
            <p v-html="interestComments[index - 1].commentContent"></p>
          </div>
          <span
            style="
              float: right;

              margin-top: 10px;
              margin-left: 10px;
            "
            >{{ interestComments[index - 1].count }}</span
          >
          <v-btn
            style="float: right; margin-right: 10px"
            v-if="!interestComments[index - 1].myLike"
            @click="toggleLiked(index)"
            light
            icon
            color="primary"
          >
            <v-icon>mdi-thumb-up-outline</v-icon>
          </v-btn>
          <v-btn
            style="float: right; color: #333; margin-right: 10px"
            v-else
            light
            @click="toggleLiked(index)"
            icon
          >
            <v-icon>mdi-thumb-up</v-icon>
          </v-btn>
        </el-card>
      </div>
    </div>
    <div>
      <el-dialog :visible.sync="dialogVisible" center width="60%">
        <SubArticles
          @commentRefresh-event="commentRefresh"
          :interestID="interestID"
        ></SubArticles>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import SubArticles from "@/components/SubArticles.vue";
import { getWeather } from "@/util/location_weather";
export default {
  components: { SubArticles },
  data() {
    return {
      interestID: 0,

      userID: 0, //当前登录用户ID
      userName: "", //当前登录用户名称
      totalPages: 1,
      total: 1,
      currentPage: 1,
      pageSize: 6, // 每页显示的图片数量
      averageValue: 0, //景点总评分
      interestDetail: [], //景点信息数据
      interestComments: [], //当前加载的评论

      Weather: "",
      dialogVisible: false,
      // SpotComPeople: "49",
      // SpotScore: "4.9",

      filledStars: 4.3, // 动态评分数据，支持小数评分
      favorite: false,
    };
  },
  mounted() {
    this.interestID = this.$route.params.id;
    this.userID = window.sessionStorage.getItem("sysUserID");
    this.userName = window.sessionStorage.getItem("sysUserName");
    this.getAverageScore(); //获取景点平均分
    this.getUserCollect(); //获取用户是否收藏景点
    this.getInterestDetail(); //获取景点数据

    this.getCommentInit(); //初始化第一页评论
    // this.loadWeather();
  },
  methods: {
    async loadWeather(adCode) {
      this.Weather = await getWeather(adCode);
    },
    //分页切换事件
    handlePageChange2(newPage) {
      this.currentPage = newPage;
      var url =
        "/interestComment?page=" +
        this.currentPage +
        "&interestID=" +
        this.interestID +
        "&userID=" +
        this.userID;
      console.log(url);
      this.request.get(url).then((res) => {
        if (res.code === 200) {
          this.total = res.data.total;
          this.totalPages = Math.ceil(res.data.total / this.pageSize);
          this.interestComments = res.data.rows;
        }
      });
    },
    //评论提交时促发的父组件
    commentRefresh() {
      this.getCommentInit();
      this.getAverageScore(); //获取景点平均分
    },
    //分页控件，页码改变时的事件
    handlePageChange() {},
    //返回上一级路由
    handleBack() {
      this.$router.go(-1);
    },
    //根据景点ID，获取用户是否进行收藏
    getUserCollect() {
      var url =
        "/isUserCollect?collectUserID=" +
        this.userID +
        "&collectInterestID=" +
        this.interestID;
      this.request.get(url).then((res) => {
        if (res.code === 200) {
          this.favorite = true;
        }
      });
    },
    //根据景点ID，获取景点平均分
    getAverageScore() {
      var url = "/averageScore?interestID=" + this.interestID;
      this.request.get(url).then((res) => {
        if (res.code === 200) {
          if (res.data != null) {
            this.averageValue = res.data.toFixed(1);
            if (Math.abs(this.averageValue - 5) < 0.01) this.averageValue = 4.9;
          }
        }
      });
    },
    //根据景点ID,获取景点数据
    getInterestDetail() {
      var url = "/getInterestInfo?interestID=" + this.interestID;
      this.request.get(url).then((res) => {
        if (res.code === 200) {
          this.interestDetail = res.data;
          this.loadWeather(this.interestDetail.interestAdcode);
        }
      });
    },
    //根据景点ID和当前用户ID初始化第一页评论
    getCommentInit() {
      var url =
        "/interestComment?interestID=" +
        this.interestID +
        "&userID=" +
        this.userID;
      this.request.get(url).then((res) => {
        if (res.code === 200) {
          console.log(res);

          this.total = res.data.total;
          console.log(Math.ceil(res.data.total / this.pageSize));
          this.totalPages = Math.ceil(res.data.total / this.pageSize);
          this.interestComments = res.data.rows;
        }
      });
    },
    // 处理删除操作
    deleteItem(index) {
      var url =
        "/deleteComment?commentID=" +
        this.interestComments[index - 1].commentID;
      this.request.delete(url).then((res) => {
        if (res.code === 200) {
          this.$message.success("删除成功");
          this.commentRefresh();
        } else {
          this.$message.error("网络异常,删除失败");
        }
      });
    },
    // 处理举报操作
    reportItem(index) {
      var url =
        "/addUserReport?reportUserID=" +
        this.userID +
        "&reportCommentID=" +
        this.interestComments[index - 1].commentID;
      this.request.post(url).then((res) => {
        if (res.code === 200) {
          this.$message.success("举报成功");
        } else {
          this.$message.error("网络异常");
        }
      });
    },
    // 切换点赞状态
    toggleLiked(index) {
      //如果其等于0，则发送请求，然后将其改成1
      if (this.interestComments[index - 1].myLike == 0) {
        var url1 =
          "/addUserLiked?likeUserID=" +
          this.userID +
          "&likeCommentID=" +
          this.interestComments[index - 1].commentID;
        this.request.post(url1).then((res) => {
          if (res.code === 200) {
            this.interestComments[index - 1].myLike = 1;
            this.interestComments[index - 1].count++;
          } else {
            this.$message.error("网络异常");
          }
        });
      } else {
        var url2 =
          "/deleteUserLiked?likeUserID=" +
          this.userID +
          "&likeCommentID=" +
          this.interestComments[index - 1].commentID;
        this.request.delete(url2).then((res) => {
          if (res.code === 200) {
            this.interestComments[index - 1].myLike = 0;
            this.interestComments[index - 1].count--;
          } else {
            this.$message.error("网络异常");
          }
        });
      }
    },
    //切换收藏状态
    toggleFavorite() {
      //如果一开始是收藏状态，则取消收藏，将收藏状态置为false
      if (this.favorite) {
        var url =
          "/deleteUserCollect?collectUserID=" +
          this.userID +
          "&collectInterestID=" +
          this.interestID;
        this.request.delete(url).then((res) => {
          if (res.code == 200) {
            this.favorite = false;
          } else {
            this.$message.error("网络异常");
          }
        });
      } else {
        var url2 =
          "/addUserCollect?collectUserID=" +
          this.userID +
          "&collectInterestID=" +
          this.interestID;
        this.request.post(url2).then((res) => {
          if (res.code == 200) {
            this.favorite = true;
          } else {
            this.$message.error("网络异常");
          }
        });
      }
    },
    //展示评论框
    showDialog() {
      this.dialogVisible = !this.dialogVisible;
      console.log(this.dialogVisible);
    },
  },
  name: "InterestInfo",
  filters: {
    formatDate(dateTime) {
      const dateObj = new Date(dateTime);
      const options = {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
      };
      return dateObj.toLocaleString("zh-CN", options);
    },
  },
};
</script>

<style scoped>
.right-head {
  justify-content: space-between;
  align-items: center;
  display: flex;
}
.right-head-title {
  margin-left: 50px;
}
.pagination {
  height: 40px;
  /* float: left; */
}
#container {
  height: 85vh;
  width: 100%;
  display: grid;
  grid-template-areas: "left-top right" "left-bottom right";
  grid-template-rows: 3fr 2fr;
  grid-template-columns: 1fr 2fr;
}

.left-top {
  grid-area: left-top;
}
.left-bottom {
  margin-top: 10px;
  grid-area: left-bottom;
}
.right {
  padding: 10px;
  grid-area: right;
  height: auto;
  background: transparent;
  overflow: auto;
  display: grid;
  grid-template-areas: "right-top" "right-bottom";
  grid-template-rows: 0.1fr 1fr;
}
.right-top {
  grid-area: auto;
  position: sticky;
  grid-area: right-top;
  top: 0; /* 将元素固定在顶部 */

  background: transparent; /* 可选：设置背景颜色 */
  z-index: 1; /* 可选：提高层级以覆盖其他内容 */
}
.right-bottom {
  grid-area: right-bottom;
  background: transparent;

  overflow: auto;
}

.star-rating {
  display: flex;
  align-items: center;
}

.left-info {
  height: 60px;
  width: 100px;
  font-family: "黑体", serif;
  margin-right: 10px; /* 调整左侧信息与星星之间的间距 */
  border-radius: 20px;
  flex-direction: column;
  align-items: flex-start;
}
.stars-and-favorite {
  height: 60px;
  width: 240px;
  justify-content: space-between;
  align-items: center;
  display: flex;
  padding-right: 2px;
}
.stars {
  align-items: center;
  display: flex;
  margin-top: 10px;
}
.collect {
  align-items: center;
  display: flex;
  margin-top: 25px;
}

.star-filled {
  color: red; /* 填充星星的颜色 */
}

.half-star {
  color: red; /* 半颗星星的颜色 */
  position: relative;
}

.half-star::before {
  content: "★";
  position: absolute;
  left: 0;
}

.rating-button {
  margin-top: 10px; /* 调整按钮与星星之间的间距 */
  margin-right: 50px;
}

.spot-info {
  font-size: 20px;
  float: left;
  padding: 0;
  position: relative;
  text-align: left;
  margin-top: 10px;

  flex-direction: column;
}

.avatar-and-info {
  grid-area: left-top;
  display: flex;
}

.circular-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: #09f0ed;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
}

.circular-avatar img {
  width: 60px;
  height: 60px;
}

.userinfo {
  display: flex;
  margin-top: 0;
  flex-direction: column; /* 垂直排列内容 */
}

.comment {
  float: left;

  grid-area: left-bottom;
}

.circular-avatar img {
  max-width: 100%; /* 限制图片宽度不超过容器大小 */
  max-height: 100%; /* 限制图片高度不超过容器大小 */
}

.toolbt {
  grid-area: right;
  display: flex;
  display: flex;
  float: right;
  align-items: flex-start; /* 垂直对齐到容器的顶部 */
}

.toolbt .button-container {
  display: flex;
  flex-direction: column;
  align-items: center; /* 水平对齐按钮和点赞span */
}

.favorbt {
  margin: 0 0 0 0;
}

.reportbt {
  text-align: left;
  margin: 10 auto;
}

.content {
  background: transparent;
  border-radius: 20px;
  margin-left: 5px;
  margin-right: 5px;
  margin-top: 6px;
  margin-bottom: 6px;
  display: grid;
  grid-template-areas: "left-top right" "left-bottom right";
  grid-auto-rows: 1fr 0fr;
  grid-template-columns: 2fr 0fr;
}
.comment {
  text-align: left;
}
</style>
