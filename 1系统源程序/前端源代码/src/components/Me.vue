<template>
  <div class="centered-card">
    <!-- Head 卡片 -->
    <el-card class="rounded-card">
      <div
        class="ImgDiv"
        style="
          height: 120px;
          width: 120px;
          border: #2c3e50, 25px, solid;
          background: #09f0ed;
          float: left;
        "
      >
        <MyImage
          :src="user.userImg"
          style="width: 120px; height: 120px"
          alt="头像"
        />
      </div>
      <div class="info-parama" style="float: left">
        <div class="InfoDiv" style="float: left">
          <div class="user_name">
            <p style="font-size: 14px; text-align: left; font-weight: bold">
              {{ user.userName }}
            </p>
          </div>
          <br />
          <div class="user_signature">
            <span style="font-size: 20px; font-family: 楷体">
              {{ user.signature }}</span
            >
          </div>
        </div>
      </div>
      <div style="float: right; line-height: 100px; margin-right: 50px">
        获赞：{{ likedNum }}
      </div>
    </el-card>

    <el-card class="rounded-card">
      <el-descriptions title="我的信息" :column="2" border>
        <template slot="extra">
          <el-button
            type="primary"
            style="color: #07f5f1"
            icon="el-icon-edit"
            @click="popEditDialog"
            size="small"
            >编辑</el-button
          >
        </template>
        <el-descriptions-item label="头像">
          <MyImage class="img" :src="user.userImg" alt="" />
        </el-descriptions-item>
        <el-descriptions-item label="账户ID">{{
          user.userID
        }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{
          user.userName
        }}</el-descriptions-item>
        <el-descriptions-item label="年龄">{{ user.age }}</el-descriptions-item>
        <el-descriptions-item label="性别">
          <el-tag size="small">{{ user.sex == 1 ? "男" : "女" }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="邮箱Email">{{
          user.userMail
        }}</el-descriptions-item>
        <el-descriptions-item label="手机号码">{{
          user.tel
        }}</el-descriptions-item>
        <el-descriptions-item label="地区">{{
          user.address
        }}</el-descriptions-item>
        <el-descriptions-item label="职业">{{
          user.work
        }}</el-descriptions-item>
        <el-descriptions-item label="兴趣爱好">{{
          user.hobby
        }}</el-descriptions-item>
        <el-descriptions-item label="个性签名">
          {{ user.signature }}
        </el-descriptions-item>
        <el-descriptions-item label="注册日期">
          {{ user.createTime | formatDate }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 编辑信息的对话框 -->
    <div>
      <el-dialog
        title="修改个人信息"
        :visible.sync="dialogVisible"
        width="60%"
        :before-close="handleClose"
        class="EditDialog"
        right
      >
        <el-form :model="form" :rules="rules" ref="form" label-width="150px">
          <div class="updateinfo">
            <div class="left">
              <el-form-item label="头像" prop="avatar">
                <!-- 这里用myImg会出错？只能使用img -->
                <img
                  alt="头像加载失败"
                  style="width: 150px; height: 110px"
                  :src="form.userImg"
                />
              </el-form-item>

              <el-form-item label="昵称" prop="userName">
                <el-input v-model="form.userName"></el-input>
              </el-form-item>
              <el-form-item label="账号密码" prop="userPSW">
                <el-input v-model="form.userPSW"></el-input>
              </el-form-item>

              <el-form-item label="年龄" prop="age">
                <el-input v-model="form.age"></el-input>
              </el-form-item>
              <el-form-item label="地区" prop="area">
                <el-input v-model="form.address"></el-input>
              </el-form-item>
            </div>
            <div class="right">
              <div style="float: left">
                <el-upload
                  ref="upload"
                  accept="image/jpg,image/jpeg,image/png"
                  :http-request="myUpload"
                  action="null"
                  :before-upload="beforeAvatarUpload"
                  :on-change="handleLimit"
                  :auto-upload="false"
                  limit:1
                  :show-file-list="false"
                >
                  <el-button slot="trigger" size="small">选取文件</el-button>
                </el-upload>
              </div>

              <el-form-item label="性别" prop="sex">
                <el-switch
                  v-model="form.sex"
                  active-color="#13ce66"
                  inactive-color="#555"
                  active-text="男"
                  inactive-text="女"
                  :active-value="1"
                  :inactive-value="0"
                ></el-switch>
              </el-form-item>

              <el-form-item label="兴趣爱好" prop="hobby">
                <el-input v-model="form.hobby"></el-input>
              </el-form-item>
              <el-form-item label="职业" prop="work">
                <el-input v-model="form.work"></el-input>
              </el-form-item>
              <el-form-item label="个性签名" prop="design">
                <el-input v-model="form.signature"></el-input>
              </el-form-item>
              <el-form-item label="手机号码" prop="mobilePhoneNumber">
                <el-input v-model="form.tel"></el-input>
              </el-form-item>
            </div>
          </div>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="handleClose">取消</el-button>
          <el-button type="primary" @click="submit('form')">提交</el-button>
        </span>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import MyImage from "@/components/MyImage.vue";
export default {
  name: "meInfo",
  components: { MyImage },
  data() {
    return {
      fileList: [],
      likedNum: -9999, //获赞数目
      dialogVisible: false,
      form: {},
      user: null,
      // 表单验证， 需要在el-form-item- 元素中增加prop属性
      rules: {
        userName: [
          { required: true, message: "用户名不可为空", trigger: "blur" },
          { min: 3, max: 10, message: "长度在 3 到 5 个字符", trigger: "blur" },
        ],
        userPSW: [
          { required: true, message: "密码不可为空", trigger: "blur" },
          {
            min: 1,
            max: 20,
            message: "长度在 1 到 20 个字符",
            trigger: "blur",
          },
        ],
      },
    };
  },
  filters: {
    formatDate(timeStamp) {
      const date = new Date(timeStamp);
      return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;
    },
  },
  mounted() {
    this.getUserInfo();
    this.getUserLiked();
  },
  methods: {
    handleLimit(file, fileList) {
      this.fileList = fileList;
      if (fileList.length > 1 && file.status !== "fail") {
        fileList.splice(0, 1);
      } else if (file.status === "fail") {
        this.$message.error("上传失败，请重新上传！");
        fileList.splice(0, 1);
      }
      let url = URL.createObjectURL(file.raw);
      console.log(file, "onchange的输出");
      this.form.userImg = url;
      console.log("fileList", fileList);
    },

    // 判断上传的是否为图片
    beforeAvatarUpload(file) {
      console.log(" beforeAvatarUpload(file)", file);
      //   console.log("上传前", file);
      const isImg = file.size / 1024 / 1024 < 2;
      if (!isImg) {
        this.$message.error("上传头像图片大小不能超过 2MB!");
      }

      const isType = file.type === "image/png";
      const isType2 = file.type === "image/jpeg";

      if (!isType && !isType2) {
        this.$message.error("上传头像图片格式为png或jpg");
      }
      return (isType || isType2) && isImg;
    },
    myUpload(parama) {
      var formData = new FormData();
      formData.append("file", parama.file);

      formData.append("token", sessionStorage.getItem("token"));
      console.log(parama);
      console.log(parama.file, "parama.file");

      this.request.post("/upload", formData).then((res) => {
        if (res.code === 200) {
          this.updateImg = res.data;
          this.form.userImg = res.data;
          this.request.put("/modifyUser", this.form).then((res) => {
            if (res.code === 200) {
              //使用vue-router 路由到指定页面，该方式称之为编程式导航
              // this.$router.push("/login");
              this.$message.success("修改成功！");
            } else {
              console.log(res.data);
              this.$message.error("用户名已存在");
            }
          });
        }
      });
    },

    submitUpload() {
      this.$refs.upload.submit();
      console.log("this.$refs.upload.submit();");
    },

    //获取当前用户所有信息
    getUserInfo() {
      var url = "/user?userID=" + window.sessionStorage.getItem("sysUserID");
      this.request.get(url).then((res) => {
        if (res.code === 200) {
          this.user = res.data;
        }
      });
    },
    //获取当前用户点赞数目
    getUserLiked() {
      var url =
        "/userLikedNum?userID=" + window.sessionStorage.getItem("sysUserID");
      console.log(url);
      this.request.get(url).then((res) => {
        if (res.code === 200) {
          console.log(url, res);
          this.likedNum = res.data;
        }
      });
    },
    popEditDialog() {
      // 点击编辑按钮时，弹出编辑对话框
      this.form = JSON.parse(JSON.stringify(this.user)); //深拷贝
      // this.form = this.user; // 复制用户信息到编辑表单
      this.dialogVisible = true;
    },
    handleClose() {
      // 关闭对话框时触发
      this.dialogVisible = false;
    },
    submit(formName) {
      // 提交编辑后的表单数据，这里可以调用接口保存数据
      //为表单绑定验证功能
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (this.fileList.length > 0) {
            //先上传图片得到返回的url；
            this.submitUpload();
          } else {
            this.request.put("/modifyUser", this.form).then((res) => {
              if (res.code === 200) {
                //使用vue-router 路由到指定页面，该方式称之为编程式导航
                // this.$router.push("/login");
                this.$message.success("修改成功！");
              } else {
                console.log(res.data);
                this.$message.error("用户名已存在");
              }
            });
          }

          this.user = this.form;
          this.dialogVisible = false;
          this.fileList = [];
        } else {
          this.dialogVisible = true;
          this.fileList = [];
          return false;
        }
      });
    },
  },
};
</script>

<style scoped>
.EditDialog {
  border-radius: 20px;
}
.img {
  width: 80px;
  height: 80px;
}
.centered-card {
  display: grid;
  /* justify-parama: center; */
  align-items: center;
  height: calc(100vh-90px);
  width: 100%;
}
.rounded-card {
  background: whitesmoke;
  opacity: 0.8;
  margin: 0 auto;
  border-radius: 15px;
  width: 1200px;
  height: auto;
  margin-bottom: 30px;
}
.updateinfo {
  overflow: auto;
}
.left {
  float: left;
}
.right {
  overflow: hidden;
  margin-top: 85px;
}
.info-parama {
  padding: 20px;
  text-align: center;
  font-size: 18px;
  color: #333;
}
</style>
