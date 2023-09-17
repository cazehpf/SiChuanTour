<template>
  <div>
    <!-- 使用vue插件制造背景效果 -->
    <vue-particles
      color="#00aeff"
      :particleOpacity="0.7"
      :particleOpacityRandom="true"
      :particlesNumber="140"
      shapeType="circle"
      :particleSize="4"
      linesColor="#8EB5C9"
      :linesWidth="1"
      :lineLinked="true"
      :lineOpacity="0.4"
      :linesDistance="120"
      :moveSpeed="1"
      :hoverEffect="true"
      hoverMode="grab"
      :clickEffect="true"
      clickMode="push"
    >
    </vue-particles>
    <el-form
      :model="form"
      :rules="rules"
      label
      width=" 100px"
      class="login-box_"
      status-icon
      style="
        background-color: rgb(23, 29, 80);
        border-radius: 10px;
        position: relative;
        top: 50px;
      "
      ref="loginForm"
    >
      <h3 class="login-title" style="color: #fff">欢迎登录</h3>
      <el-form-item prop="userName" style="color: #fff">
        <span slot="label">
          <span style="color: #fff">用户名</span>
        </span>
        <el-input
          prefix-icon="el-icon-user"
          type="text"
          placeholder=" 请输入用户名"
          v-model="form.userName"
        />
      </el-form-item>
      <el-form-item prop="userPSW">
        <span slot="label">
          <span style="color: #fff">密码</span>
        </span>
        <el-input
          prefix-icon="el-icon-lock"
          type="userPSW"
          placeholder=" 请输入密码"
          v-model="form.userPSW"
        />
      </el-form-item>
      <el-row :span="24">
        <el-col :span="13">
          <el-input
            v-model="form.code"
            auto-complete="off"
            placeholder="请输入验证码"
            size=""
          ></el-input>
        </el-col>
        <el-col :span="11">
          <div class="login-code" width="100%" @click="refreshCode">
            <!--验证码组件-->
            <s-identify :identifyCode="identifyCode"></s-identify>
          </div>
        </el-col>
      </el-row>

      <el-form-item style="margin-top: 20px">
        <el-button class="login-button_" v-on:click="onSubmit('loginForm')"
          >登录</el-button
        >
        <el-button class="login-button_" v-on:click="$router.push('/register')"
          >注册</el-button
        >
      </el-form-item>
    </el-form>
    <el-dialog
      title="温馨提示"
      :visible.sync="dialogVisible"
      width="30%"
      :before-close="handleClose"
    >
      <span>请输入用户名和密码</span>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="dialogVisible = false"
          >确 定</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>
<script>
import SIdentify from "@/components/Identify.vue";

export default {
  name: "LoginPage",
  components: { SIdentify }, //写在export default{}中与data同级表示使用组件
  mounted() {
    // 初始化验证码
    this.identifyCode = "";
    this.makeCode(this.identifyCodes, 4);
  },

  data() {
    return {
      form: {
        userName: "",
        userPSW: "",
        code: "",
      },
      identifyCodes: "1234567890abcdefjhijklinopqrsduvwxyz", //随机串内容
      identifyCode: "",
      // 表单验证， 需要在el-form-item- 元素中增加prop属性
      rules: {
        userName: [
          { required: true, message: "账号不可为空", trigger: "blur" },
        ],
        userPSW: [{ required: true, message: "密码不可为空", trigger: "blur" }],
        code: [{ required: true, message: "请输入验证码", trigger: "blur" }],
      },
      //对话框显示和隐藏
      dialogVisible: false,
    };
  },
  methods: {
    handleClose: function () {
      console.log("Handle Close，空函数");
    },
    // 重置验证码
    refreshCode() {
      this.identifyCode = "";
      this.makeCode(this.identifyCodes, 4);
    },
    makeCode(o, l) {
      for (let i = 0; i < l; i++) {
        this.identifyCode +=
          this.identifyCodes[this.randomNum(0, this.identifyCodes.length)];
      }
    },
    randomNum(min, max) {
      return Math.floor(Math.random() * (max - min) + min);
    },

    onSubmit(formName) {
      if (this.form.code.toLowerCase() !== this.identifyCode.toLowerCase()) {
        this.$message.error("请填写正确验证码");
        this.refreshCode();
        return;
      }

      //为表单绑定验证功能
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.request
            .post("/login", {
              userName: this.form.userName,
              userPSW: this.form.userPSW,
            })
            .then((res) => {
              if (res.code === 200) {
                // console.log(res);
                sessionStorage.setItem("sysUserName", res.data.userName);
                sessionStorage.setItem("sysUserID", res.data.userID);
                sessionStorage.setItem("token", res.data.token);
                //使用vue-router 路由到指定页面，该方式称之为编程式导航
                this.$router.replace("/index");
                this.$message.success("登录成功");
              } else {
                this.$message.error(this.form.userName + res.msg);
              }
            });
        } else {
          this.dialogVisible = true;
          return false;
        }
      });
    },
  },
};
</script>
<style scoped>
#particles-js {
  width: 100%;
  height: 100%;
  position: absolute;
  background-color: rgb(6, 14, 67);
  color: white;
}
.login-box_ {
  border: 1px solid #dcdfe6;
  width: 400px;
  margin: auto;
  padding: 15px 35px 35px 35px;
  border-radius: 5px;
  -webkit-border-radius: 5px;
  -moz-border-radius: 5px;
  box-shadow: 0 0 25px #909399;
}

.login-title {
  text-align: center;
  margin: 0 auto 40px auto;
  color: #303133;
}
</style>
