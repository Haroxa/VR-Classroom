import { createRouter, createWebHistory } from 'vue-router'

// 主页
import Home from '../views/Home.vue'

// 审核模块
import AuditHome from '../views/audit/Home.vue'
import AuditPostList from '../views/audit/PostList.vue'
import AuditCommentList from '../views/audit/CommentList.vue'
import AuditLogList from '../views/audit/LogList.vue'

// 用户模块
import UserHome from '../views/user/Home.vue'
import UserPostList from '../views/user/PostList.vue'
import UserPostDetail from '../views/user/PostDetail.vue'
import UserCommentList from '../views/user/CommentList.vue'
import UserProfile from '../views/user/Profile.vue'
import UserMyPosts from '../views/user/MyPosts.vue'
import UserLikedPosts from '../views/user/LikedPosts.vue'
import UserLikedComments from '../views/user/LikedComments.vue'
import UserOrderList from '../views/user/OrderList.vue'
import UserCreateOrder from '../views/user/CreateOrder.vue'
import UserSeatList from '../views/user/SeatList.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  // 审核模块（不需要登录）
  {
    path: '/audit',
    component: AuditHome,
    children: [
      {
        path: '',
        redirect: '/audit/posts'
      },
      {
        path: 'posts',
        name: 'AuditPostList',
        component: AuditPostList
      },
      {
        path: 'comments',
        name: 'AuditCommentList',
        component: AuditCommentList
      },
      {
        path: 'logs',
        name: 'AuditLogList',
        component: AuditLogList
      }

    ]
  },
  // 用户模块
  {
    path: '/user',
    component: UserHome,
    children: [
      {
        path: '',
        redirect: '/user/posts'
      },
      {
        path: 'posts',
        name: 'UserPostList',
        component: UserPostList
      },
      {
        path: 'post/:id',
        name: 'UserPostDetail',
        component: UserPostDetail
      },
      {
        path: 'comments',
        name: 'UserCommentList',
        component: UserCommentList
      },
      {
        path: 'orders',
        name: 'UserOrderList',
        component: UserOrderList
      },
      {
        path: 'profile',
        name: 'UserProfile',
        component: UserProfile
      },
      {
        path: 'my-posts',
        name: 'UserMyPosts',
        component: UserMyPosts
      },
      {
        path: 'liked-posts',
        name: 'UserLikedPosts',
        component: UserLikedPosts
      },
      {
        path: 'liked-comments',
        name: 'UserLikedComments',
        component: UserLikedComments
      },
      {
        path: 'order/create',
        name: 'UserCreateOrder',
        component: UserCreateOrder
      },
      {
        path: 'seats',
        name: 'UserSeatList',
        component: UserSeatList
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫 - 暂时移除，因为登录功能已内置在用户页面中
// router.beforeEach((to, from, next) => {
//   const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
//   const token = localStorage.getItem('token')
//   
//   if (requiresAuth && !token) {
//     next('/')
//   } else {
//     next()
//   }
// })

export default router