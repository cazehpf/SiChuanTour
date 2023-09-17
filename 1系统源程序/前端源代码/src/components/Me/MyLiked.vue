<template>
  <div>
    <div class="Container">
      <el-card class="content">
        <!-- 左侧卡片循环部分 -->
        <el-card
          class="ReceiveInfo"
          v-for="liked in receiveLiked"
          :key="liked.commentInterestID"
        >
          <div class="avatar-and-info">
            <div class="circular-avatar">
              <img :src="liked.userImg" alt="Avatar" />
            </div>
            <div
              class="info"
              style="font-weight: bold; font-size: 16px; font-family: 黑体"
            >
              <span>{{ liked.userName }}赞了你的评论</span>
              <div>
                <span style="float: left; font-size: 10px; color: #9b9b9b">{{
                  liked.likeTime | formatDate
                }}</span>
              </div>
            </div>
          </div>
          <el-divider></el-divider>
          <div style="height: 60px">
            <p v-html="liked.commentContent" style="text-align: left"></p>
            <!-- <span style="float: left">
              {{ liked.commentContent }}
            </span> -->
            <span
              class="spot"
              style="
                color: #df0404;
                font-size: 20px;
                font-weight: bold;
                margin-right: 0;
                float: right;
                margin-top: 40px;
                font-family: 楷体;
              "
            >
              {{ liked.interestName }}
            </span>
          </div>
        </el-card>
      </el-card>
    </div>
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
</template>

<script>
export default {
  name: "MyLiked",
  data() {
    return {
      totalPages: 1,
      total: 1,
      currentPage: 1,
      pageSize: 6, // 每页显示的图片数量
      receiveLiked: [],
    };
  },
  mounted() {
    this.getInitLike();
  },
  methods: {
    //初始化点赞
    getInitLike() {
      var url =
        "/userLiked?userID=" + window.sessionStorage.getItem("sysUserID");
      this.request.get(url).then((res) => {
        if (res.code === 200) {
          this.total = res.data.total;

          this.totalPages = Math.ceil(res.data.total / this.pageSize);
          this.receiveLiked = res.data.rows;
        }
      });
    },
    //分页码改变时事件
    handlePageChange(newPage) {
      this.currentPage = newPage;
      var url =
        "/userLiked?page=" +
        this.currentPage +
        "&userID=" +
        window.sessionStorage.getItem("sysUserID");
      this.request.get(url).then((res) => {
        if (res.code === 200) {
          this.total = res.data.total;
          this.totalPages = Math.ceil(res.data.total / this.pageSize);
          this.receiveLiked = res.data.rows;
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
};
</script>

<style scoped>
.Container {
  height: 600px;
  width: 1000px;
  background: white;
  margin-left: 150px;
  margin-top: 20px;
  opacity: 0.8;

  overflow: auto;
  align-items: center;
  justify-content: center;
}

.content {
  justify-items: center;
  align-items: center;
}

.ReceiveInfo {
  display: flex;
  margin-bottom: 5px;
  /* width: 900px; */
}

.avatar-and-info {
  display: flex;
  align-items: center;
  width: 900px;
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

.info {
  margin-left: 10px; /* 调整信息的间距 */
}
</style>
