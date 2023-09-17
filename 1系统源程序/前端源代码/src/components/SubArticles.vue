<template>
  <div>
    <div>
      <span>评分：</span>
      <el-rate v-model="commentScore"></el-rate>
    </div>
    <br />
    <div class="card" id="card">
      <div class="blog-editor" style="height: 480px; overflow-y: auto">
        <div class="quill-toolbar">
          <quill-editor
            ref="editor"
            v-model="content"
            :options="editorOptions"
          ></quill-editor>
        </div>
      </div>
      <!-- 内容容器，可滚动 -->
      <div class="content-container" style="height: 480px; overflow-y: auto">
        <!-- 内容区域 -->
      </div>
      <v-btn @click="publishBlog" class="btn btn-primary">发表评论</v-btn>
      <!-- 添加对话框部分 -->
      <div v-if="dialogVisible" class="dialog">
        <div class="dialog-content">
          <h3 class="headline">{{ dialogTitle }}</h3>
          <p>{{ dialogMessage }}</p>
          <v-btn @click="dialogVisible = false" class="btn btn-primary"
            >确定</v-btn
          >
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import "quill/dist/quill.core.css";
import "quill/dist/quill.snow.css";
import "quill/dist/quill.bubble.css";

export default {
  props: ["interestID"], //父组件传递过来的当前景点的ID
  mounted() {
    this.commentInterestID = this.interestID;
  },
  data() {
    return {
      commentInterestID: -1,
      commentScore: 0,
      dialogVisible: false,
      dialogTitle: "",
      dialogMessage: "",
      content: "",
      editorOptions: {
        placeholder: "在此处编写文章内容...",
        modules: {
          toolbar: [
            ["bold", "italic", "underline", "strike"],
            ["blockquote", "code-block"],
            [{ header: 1 }, { header: 2 }],
            [{ list: "ordered" }, { list: "bullet" }],
            [{ script: "sub" }, { script: "super" }],
            [{ indent: "-1" }, { indent: "+1" }],
            [{ direction: "rtl" }],
            [{ size: ["small", false, "large", "huge"] }],
            [{ header: [1, 2, 3, 4, 5, 6, false] }],
            ["link", "image"],
            ["clean"],
            [{ align: [] }],
            [{ color: [] }, { background: [] }],
            [{ font: [] }],
          ],
        },
      },
    };
  },
  methods: {
    publishBlog() {
      // 获取编辑器实例
      const quillInstance = this.$refs.editor.quill;
      const htmldata = quillInstance.root.innerHTML;
      this.content = htmldata;
      console.log(this.content);

      this.request
        .post("/addComment", {
          commentInterestID: this.commentInterestID,
          commentContent: this.content,
          commentUserID: window.sessionStorage.getItem("sysUserID"),
          commentScore: this.commentScore,
        })
        .then((res) => {
          if (res.code === 200) {
            // 在这里可以添加发布文章后的逻辑
            // 例如显示成功对话框
            this.$message.success("评论发布成功");
            // 清空标题和内容
            this.title = "";
            quillInstance.setContents([]);
            //将星星数置为0
            this.commentScore = 0;
            this.$emit("commentRefresh-event");
          } else if (res.code === 400) {
            this.$message.warning(
              "评论过长或者图片过大(图片总大小最好不要超过16kb),评论发布失败"
            );
          } else {
            this.$message.error("评论发布失败！可能网络不通畅");
          }
        });
    },
  },
};
</script>

<style scoped>
#card {
  display: flex;
  max-height: 700px;
  background-color: #f7f7f7;
  border-radius: 30px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  justify-items: center;
}

.blog-editor {
  width: 100%;
  max-height: 700px;
  position: relative;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* Add styling for the dialog here */
.dialog {
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  z-index: 9999;
}
.dialog-content {
  text-align: center;
}
</style>
