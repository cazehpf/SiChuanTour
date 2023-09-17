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
      ref="loginForm"
      class="login-box_2"
      :model="form"
      status-icon
      :rules="rules"
      label
      width=" 80px"
      style="
        background-color: rgb(23, 29, 80);
        border-radius: 10px;
        position: relative;
        top: 50px;
      "
    >
      <h3 class="login-title" style="color: #fff">欢迎注册</h3>
      <el-form-item prop="userName">
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
      <el-form-item prop="checkPass">
        <span slot="label">
          <span style="color: #fff">确认密码</span>
        </span>
        <el-input
          prefix-icon="el-icon-lock"
          type="userPSW"
          placeholder=" 请确认密码"
          v-model="form.checkPass"
        />
      </el-form-item>
      <el-form-item prop="userMail" :inline="true">
        <span slot="label">
          <span style="color: #fff">邮箱</span>
        </span>
        <el-input
          @change="mailChange"
          prefix-icon="el-icon-coffee"
          type="text"
          placeholder=" 请输入邮箱"
          v-model="form.userMail"
          style="width: 150px; margin-right: 10px"
        />
        <el-button
          class="check-button_"
          size="small"
          :disabled="disable"
          :type="{ disable: info }"
          @click="getEmailCode"
          >{{ getCode }}</el-button
        >
      </el-form-item>

      <el-form-item prop="mailCode" :inline="true">
        <span slot="label">
          <span style="color: #fff">验证码</span>
        </span>
        <el-input
          prefix-icon="el-icon-magic-stick"
          type="text"
          placeholder=" 请输入邮箱验证码"
          v-model="form.mailCode"
          style="width: 200px; margin-right: 10px"
        />
        <el-button size="small" @click="checkCode" class="check-button_"
          >验证</el-button
        >
      </el-form-item>

      <el-form-item>
        <el-button class="login-button_" v-on:click="onSubmit('loginForm')"
          >注册</el-button
        >
        <el-button class="login-button_" v-on:click="$router.push('/login')"
          >去登录</el-button
        >
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
export default {
  name: "RegisterPage",
  data() {
    var checkuserName = (rule, value, callback) => {
      if (value) {
        this.request.get("/checkName?" + "userName=" + value).then((res) => {
          console.log(res);
          if (res.code !== 200) {
            callback("用户名已存在！");
          } else {
            callback();
          }
        });
      }
    };
    var checkuserMail = (rule, value, callback) => {
      if (value) {
        this.request.get("/checkMail?" + "userMail=" + value).then((res) => {
          console.log(res);
          if (res.code !== 200) {
            callback("邮箱被注册！");
            this.mailHasRegister = true;
          } else {
            callback();
          }
        });
      }
    };
    var validatePass = (rule, value, callback) => {
      if (value === "") {
        callback(new Error("请再次输入密码"));
      } else if (value !== this.form.userPSW) {
        callback(new Error("两次输入密码不一致!"));
      } else {
        callback();
      }
    };
    return {
      count: 60,
      disable: false,
      getCode: "获取验证码",
      mailHasRegister: false, //如果邮箱已经注册，则无法发送验证码
      mailIdentify: false, //这个是为了解决用户在邮箱验证成功后，篡改邮箱再进行注册，
      //思路如下
      //1.给邮箱input框框加上change事件，事件内容为：如果邮箱没有验证成功，则无事发生，如果邮箱验证成功，如果用户修改了，
      //则将mailIdentify置为false，用户注册时需要重新进行验证。
      //2.用户邮箱验证成功后将mailIdentify置为true
      //3.用户在注册时检查mailIdentify是否为true进行注册
      form: {
        userName: "",
        userPSW: "",
        userMail: "",
        checkPass: "",
        mailCode: "",
      },
      // 表单验证， 需要在el-form-item- 元素中增加prop属性
      rules: {
        userName: [
          { required: true, message: "用户名不可为空", trigger: "blur" },
          { min: 3, max: 10, message: "长度在 3 到 5 个字符", trigger: "blur" },
          { validator: checkuserName, trigger: "blur" },
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
        userMail: [
          { required: true, message: "邮箱不可为空", trigger: "blur" },
          { validator: checkuserMail, trigger: "blur" },
        ],
        checkPass: [
          { validator: validatePass, trigger: "blur" },
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
  methods: {
    handleClose: function () {
      console.log("Handle Close，空函数");
    },
    //获取验证码
    getEmailCode() {
      if (this.form.userMail == "") {
        this.$message.warning({
          message: "邮箱为空！",
        });
        return;
      }
      if (this.mailHasRegister) {
        this.$message.warning({
          message: "邮箱已被注册！无法发送验证码！",
        });
        return;
      }
      console.log("/sendEmailCode?email=" + this.form.userMail);
      this.request
        .get("/sendEmailCode?email=" + this.form.userMail)
        .then((result) => {
          if (result.code === 200) {
            this.$message.success({
              message: "邮箱验证码发送成功！",
            });
          } else {
            this.$message.error({
              message: "邮箱验证码发送失败！请检查网络是否通畅或者邮箱是否正确",
            });
          }
        });
      var countDown = setInterval(() => {
        if (this.count < 1) {
          this.isGeting = false;
          this.disable = false;
          this.getCode = "获取验证码";
          this.count = 60;
          clearInterval(countDown);
        } else {
          this.disable = true;
          this.getCode = this.count-- + "s后重发";
        }
      }, 1000);
    },
    //验证验证码事件
    checkCode() {
      if (this.form.mailCode === "") {
        this.$message.warning({
          message: "请输入验证码！",
        });
        return;
      }
      if (this.mailHasRegister) {
        this.$message.warning({
          message: "邮箱已被注册！无法验证！",
        });
        return;
      }
      this.request
        .get(
          "/checkEmailCode?email=" +
            this.form.userMail +
            "&code=" +
            this.form.mailCode
        )
        .then((result) => {
          if (result.code === 200) {
            this.$message.success({
              message: "验证成功！可以注册",
            });
            this.mailIdentify = true;
          } else {
            this.$message.error({
              message:
                "验证失败！请检查邮箱和验证码是否被篡改或重新发送进行验证",
            });
          }
        });
    },
    //邮箱input框change事件
    mailChange() {
      if (this.mailIdentify === false) return;
      this.mailIdentify = true;
      this.$message.warning({
        message: "你已经篡改了验证后的邮箱，后面注册将需要重新验证！",
      });
    },
    //表单提交，进行注册
    onSubmit(formName) {
      if (this.mailIdentify === false) {
        this.$message.warning({
          message: "邮箱还未验证！",
        });
      }
      //为表单绑定验证功能
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.request
            .post("/register", {
              userName: this.form.userName,
              userPSW: this.form.userPSW,
              userMail: this.form.userMail,
            })
            .then((res) => {
              if (res.code === 200) {
                //使用vue-router 路由到指定页面，该方式称之为编程式导航
                // this.$router.push("/login");
                this.$message.success("注册成功！");
              } else {
                console.log(res.data);
                this.$message.error(this.form.userName + "已被注册");
              }
            });
        } else {
          this.dialogVisible = true;
          return false;
        }
      });
    },
  },
  created() {
    console.log(this);
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
.login-box_2 {
  border: 1px solid #dcdfe6;
  width: 450px;
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
