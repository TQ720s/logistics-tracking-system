<template>
  <div class="layout">
    <aside class="sidebar">
      <div :class="['menu-item', active==='info'?'active':'']" @click="active='info'">个人信息</div>
      <div :class="['menu-item', active==='user'?'active':'']" @click="active='user'">用户管理</div>
      <div :class="['menu-item', active==='courier'?'active':'']" @click="active='courier'">快递员管理</div>
      <div :class="['menu-item', active==='order'?'active':'']" @click="active='order'">订单管理</div>
      <div :class="['menu-item', active==='track'?'active':'']" @click="active='track'">物流轨迹</div>
      <div class="menu-item" @click="logout">退出登录</div>
    </aside>
    <main class="content">
      <AdminInfo v-if="active==='info'" />
      <AdminUser v-if="active==='user'" />
      <AdminCourier v-if="active==='courier'" />
      <AdminOrder v-if="active==='order'" />
      <AdminTrack v-if="active==='track'" />
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import AdminInfo from './AdminInfo.vue'
import AdminUser from './AdminUser.vue'
import AdminCourier from './AdminCourier.vue'
import AdminOrder from './AdminOrder.vue'
import AdminTrack from './AdminTrack.vue'

const router = useRouter()
const active = ref('info')

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
  width: 220px;
  background: #2c3e50;
  color: #fff;
  display: flex;
  flex-direction: column;
  padding-top: 40px;
  min-width: 220px;
  box-shadow: 2px 0 5px rgba(0,0,0,0.1);
  transition: width 0.3s;
}
.menu-item {
  padding: 16px 24px;
  cursor: pointer;
  font-size: 16px;
  border-left: 4px solid transparent;
  transition: background 0.2s, border-color 0.2s;
}
.menu-item.active, .menu-item:hover {
  background: #34495e;
  border-left: 4px solid #409eff;
}
.content {
  flex: 1;
  padding: 40px 50px;
  background: #f4f7f6;
  overflow-y: auto;
}
</style> 