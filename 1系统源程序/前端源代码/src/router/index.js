import Vue from "vue";
import VueRouter from "vue-router";
import request from "@/util/request";

Vue.use(VueRouter);

const routes = [
  {
    path: "/index",
    redirect: "/",
  },

  {
    path: "/",
    name: "Index",
    component: () => import("@/views/Index.vue"),
    children: [
      {
        path: "/home",
        name: "Home",
        component: () => import("@/components/Home.vue"),
      },
      {
        path: "/interestsData",
        name: "InterestsData",
        component: () => import("@/components/InterestsData.vue"),
      },
      {
        path: "/interestsData/:id",
        name: "InterestInfo",
        component: () => import("@/components/InterestInfo.vue"),
      },
      {
        path: "/mapExplorer",
        name: "InterestsData",
        component: () => import("@/components/Map.vue"),
        children: [
          {
            path: "/mapExplorer/queryBySql",
            name: "QueryBySql",
            component: () => import("@/components/Map/MapQuery/QueryBySql.vue"),
          },
          {
            path: "/mapExplorer/queryByBoundry",
            name: "QueryByBoundry",
            component: () =>
              import("@/components/Map/MapQuery/QueryByBoundry.vue"),
          },
          {
            path: "/mapExplorer/queryByMapClick",
            name: "QueryByMapClick",
            component: () =>
              import("@/components/Map/MapQuery/QueryByMapClick.vue"),
          },
          {
            path: "/mapExplorer/scatterMap",
            name: "ScatterMap",
            component: () =>
              import("@/components/Map/MapVisualize/ScatterMap.vue"),
          },
          {
            path: "/mapExplorer/heatMap",
            name: "HeatMap",
            component: () =>
              import("@/components/Map/MapVisualize/HeatMap.vue"),
          },
          {
            path: "/mapExplorer/aggregationMap",
            name: "AggregationMap",
            component: () =>
              import("@/components/Map/MapVisualize/AggregationMap.vue"),
          },
          {
            path: "/mapExplorer/oneDirection",
            name: "OneDirection",
            component: () =>
              import("@/components/Map/MapAnalysis/OneDirection.vue"),
          },
          {
            path: "/mapExplorer/mutiDirection",
            name: "MutiDirecion",
            component: () =>
              import("@/components/Map/MapAnalysis/MutiDirection.vue"),
          },
        ],
      },
      {
        path: "/me/myCollection",
        name: "MyCollection",
        component: () => import("@/components/Me/MyCollection.vue"),
      },
      {
        path: "/me/myReply",
        name: "MyReply",
        component: () => import("@/components/Me/MyReply.vue"),
      },

      {
        path: "/me/myLiked",
        name: "MyLiked",
        component: () => import("@/components/Me/MyLiked.vue"),
      },
      {
        path: "/me",
        name: "Me",
        component: () => import("@/components/Me.vue"),
      },
    ],
  },

  {
    path: "/login",
    name: "Login",
    component: () => import("@/views/Login.vue"),
  },
  {
    path: "/register",
    name: "Register",
    component: () => import("@/views/Register.vue"),
  },
];

const router = new VueRouter({
  routes,
  mode: "hash",
});

// 检查是否存在于免登陆白名单
function inWhiteList(toPath) {
  const whiteList = [
    "/login",
    "/register",
    "/404",
    "/checkEmailCode",
    "/sendEmailCode",
    "/checkName",
    "/checkMail",
    "/upload",
  ];
  const path = whiteList.find((value) => {
    // 使用正则匹配
    const reg = new RegExp("^" + value);
    return reg.test(toPath);
  });
  return !!path;
}

router.beforeEach((to, from, next) => {
  const token = sessionStorage.getItem("token");
  if (inWhiteList(to.path)) {
    next();
  } else {
    //用户已登录
    if (token) {
      request.post("/authentication").then((res) => {
        console.log("/authentication", res);
        if (res.code === 200) {
          next();
        } else {
          next("/login");
        }
      }); //我觉得没有必要要
    } else {
      next(`/login`);
    }
  }
});

export default router;
