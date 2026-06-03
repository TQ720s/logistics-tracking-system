<template>
  <div class="layout">
    <aside class="sidebar">
      <div :class="['menu-item', active==='order'?'active':'']" @click="active='order'">我要下单</div>
      <div :class="['menu-item', active==='order-list'?'active':'']" @click="active='order-list'">我的订单</div>
      <div :class="['menu-item', active==='track'?'active':'']" @click="active='track'">物流查询</div>
      <div :class="['menu-item', active==='address'?'active':'']" @click="active='address'">常用地址</div>
      <div :class="['menu-item', active==='info'?'active':'']" @click="active='info'">个人信息</div>
      <div class="menu-item logout" @click="logout">退出登录</div>
    </aside>
    <main class="content">
      <UserOrder v-if="active==='order'" />
      <UserOrderList v-if="active==='order-list'" />
      <UserTrack v-if="active==='track'" />
      <UserAddress v-if="active==='address'" />
      <UserInfo v-if="active==='info'" />
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import UserInfo from './UserInfo.vue'
import UserAddress from './UserAddress.vue'
import UserOrder from './UserOrder.vue'
import UserOrderList from './UserOrderList.vue'
import UserTrack from './UserTrack.vue'

const router = useRouter()
const active = ref('order')
const username = localStorage.getItem('username')

function logout() {
  localStorage.removeItem('role')
  localStorage.removeItem('username')
  router.push('/login')
}
</script>

<style scoped>
.layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
}
.sidebar {
  width: 240px;
  background: #ffffff;
  border-right: 1px solid #eef0f2;
  display: flex;
  flex-direction: column;
  padding: 24px 0;
  min-width: 240px;
  transition: width 0.3s;
}
.user-profile {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid #eef0f2;
  margin-bottom: 16px;
}
.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  margin: 0 auto 12px;
  background: #f0f0f0;
}
.avatar img {
  width: 100%;
  height: 100%;
}
.username {
  font-weight: 600;
  font-size: 18px;
  color: #333;
}
.menu-item {
  padding: 16px 24px;
  cursor: pointer;
  font-size: 16px;
  color: #555;
  border-left: 4px solid transparent;
  transition: background 0.2s, border-color 0.2s, color 0.2s;
}
.menu-item.active, .menu-item:hover {
  background: #f4f7f6;
  border-left: 4px solid #409eff;
  color: #409eff;
  font-weight: 500;
}
.logout {
  margin-top: auto;
  border-top: 1px solid #eef0f2;
}
.content {
  flex: 1;
  padding: 40px 50px;
  background: #f4f7f6;
  overflow-y: auto;
}
</style> 