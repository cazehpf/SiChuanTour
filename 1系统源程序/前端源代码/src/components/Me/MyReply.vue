<template>
  <div class="Container">
    <template>
      <!-- 右侧卡片部分 -->
      <el-card class="right">
        <el-card header="查询工具" style="display: flex">
          <!-- 添加起始和终止日期选择器 -->
          <div class="card-item">
            <el-date-picker
              v-model="startDate"
              type="date"
              value-format="yyyy-MM-dd"
              placeholder="开始日期"
              style="margin-right: 10px"
            ></el-date-picker>
          </div>
          <div class="card-item">
            <el-date-picker
              v-model="endDate"
              type="date"
              value-format="yyyy-MM-dd"
              placeholder="结束日期"
              style="margin-right: 10px"
            ></el-date-picker>
          </div>

          <div class="card-item">
            <el-input
              v-model="filterInterestText"
              placeholder="按景点名称过滤查询"
            ></el-input>
          </div>

          <div class="card-item">
            <el-button type="primary" size="medium" @click="queryReplies"
              >查询</el-button
            >
            <el-button type="primary" size="medium" @click="clearFilters"
              >清除</el-button
            >
          </div>
        </el-card>
        <div>
          <el-pagination
            :page-size="pageSize"
            :pager-count="5"
            :page-count="totalPages"
            :current-page="currentPage"
            layout="total, prev, pager, next, jumper"
            @current-change="handlePageChange"
            :total="this.total"
          ></el-pagination>
        </div>
      </el-card>
    </template>
    <!-- 内容部分 -->
    <el-card class="content">
      <!-- 左侧卡片循环部分 -->
      <el-card class="left" v-for="index in myComments.length" :key="index">
        <div slot="header" class="clearfix">
          <span style="float: left">{{
            myComments[index - 1].commentTime | formatDate
          }}</span>

          <span
            v-for="starIndex in myComments[index - 1].commentScore"
            :key="starIndex"
            style="color: #ffdb58"
            class="star-filled"
          >
            ★
          </span>

          <el-button
            style="float: right; padding: 3px 0"
            @click="deleteComment(index)"
            type="text"
            >删除</el-button
          >
        </div>
        <div style="height: 60px">
          <p
            v-html="myComments[index - 1].commentContent"
            style="float: left"
          ></p>
          <span style="float: right; margin-top: 40px; margin-right: 20px"
            ><VIcon> mdi-thumb-up-outline </VIcon
            >{{ myComments[index - 1].count }}</span
          >
          <span
            class="spot"
            style="
              color: #df0404;
              font-weight: bold;
              float: right;
              font-size: 20px;
              margin-top: 40px;
              margin-right: 20px;
              font-family: 楷体;
            "
          >
            {{ myComments[index - 1].interestName }}
          </span>
        </div>
      </el-card>
    </el-card>
  </div>
</template>

<script>
export default {
  data() {
    return {
      userID: -1,
      totalPages: 1,
      total: 1,
      currentPage: 1,
      pageSize: 6, // 每页显示的图片数量
      myComments: [],
      //查询条件
      startDate: "",
      endDate: "",
      filterInterestText: "",
      isSearch: false,
    };
  },
  mounted() {
    this.userID = window.sessionStorage.getItem("sysUserID");
    this.getInitComment();
  },
  methods: {
    //初始化评论
    getInitComment() {
      var url = "/userComment?userID=" + this.userID;
      this.request.get(url).then((res) => {
        if (res.code === 200) {
          this.total = res.data.total;
          this.totalPages = Math.ceil(res.data.total / this.pageSize);
          this.myComments = res.data.rows;
        }
      });
    },
    //查询逻辑
    queryReplies() {
      this.isSearch = true;
      this.currentPage = 1;

      var url =
        "/userComment?page=" +
        this.currentPage +
        "&startTime=" +
        this.startDate +
        "&interestFilter=" +
        this.filterInterestText +
        "&endTime=" +
        this.endDate +
        "&userID=" +
        this.userID;
      this.request.get(url).then((res) => {
        if (res.code === 200) {
          this.total = res.data.total;

          this.totalPages = Math.ceil(res.data.total / this.pageSize);
          this.myComments = res.data.rows;
        }
      });
    },
    //分页换页逻辑
    handlePageChange(newPage) {
      this.currentPage = newPage;
      if (this.isSearch) {
        var url =
          "/userComment?page=" +
          this.currentPage +
          "&startTime=" +
          this.startDate +
          "&interestFilter=" +
          this.filterInterestText +
          "&endTime=" +
          this.endDate +
          "&userID=" +
          this.userID;
        this.request.get(url).then((res) => {
          if (res.code === 200) {
            this.total = res.data.total;

            this.totalPages = Math.ceil(res.data.total / this.pageSize);
            this.myComments = res.data.rows;
          }
        });
      } else {
        var url1 =
          "/userComment?page=" + this.currentPage + "&userID=" + this.userID;
        this.request.get(url1).then((res) => {
          if (res.code === 200) {
            this.total = res.data.total;
            this.totalPages = Math.ceil(res.data.total / this.pageSize);
            this.myComments = res.data.rows;
          }
        });
      }
    },
    clearFilters() {
      //清除逻辑
      this.startDate = "";
      this.endDate = "";
      this.filterInterestText = "";
    },
    //删除评论的逻辑
    deleteComment(index) {
      var url =
        "/deleteComment?commentID=" + this.myComments[index - 1].commentID;
      this.request.delete(url).then((res) => {
        if (res.code === 200) {
          this.$message.success("删除成功");
          this.getInitComment();
        } else {
          this.$message.error("网络异常,删除失败");
        }
      });
    },
  },
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
  name: "MyReply",
};
</script>

<style scoped>
.Container {
  height: 600px;
  width: 1200px;
  background: white;
  margin-top: 20px;
  opacity: 0.8;
  overflow: auto;
  margin-left: 50px;
}
/* .spot:hover {
  background: #09f0ed;
  cursor: pointer;
} */

.right {
  float: right;
  justify-content: right;
  align-items: center;
  /* height: 700px; */
  width: 550px;
  /* 确保右侧卡片不占据左侧卡片的高度 */

  position: sticky;
  top: 0;
}
.card-item {
  margin-left: 50px;
  margin-bottom: 10px;
}
.star-filled {
  color: #ffdb58; /* 填充星星的颜色 */
}
.left {
  width: 600px;
  float: left;
  margin-bottom: 5px;
}
.content {
  display: flex;
  flex-direction: row-reverse; /* 将右侧卡片放在左侧卡片之前并右对齐 */
}
</style>
