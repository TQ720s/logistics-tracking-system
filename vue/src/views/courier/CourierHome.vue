<template>
  <div class="layout">
    <aside class="sidebar">
      <div :class="['menu-item', active==='info'?'active':'']" @click="active='info'">个人信息</div>
      <div :class="['menu-item', active==='performance'?'active':'']" @click="active='performance'">绩效统计</div>
      <div :class="['menu-item', active==='orders'?'active':'']" @click="active='orders'">我的订单</div>
      <div :class="['menu-item', active==='receive'?'active':'']" @click="active='receive'">接单</div>
      <div :class="['menu-item', active==='station'?'active':'']" @click="active='station'">上传站点</div>
      <div :class="['menu-item', active==='finish'?'active':'']" @click="active='finish'">配送完成</div>
      <div :class="['menu-item', active==='track'?'active':'']" @click="active='track'">物流轨迹</div>
      <div class="menu-item" @click="logout">退出登录</div>
    </aside>
    <main class="content">
      <CourierInfo v-if="active==='info'" />
      <CourierPerformance v-if="active==='performance'" />
      <CourierOrders v-if="active==='orders'" />
      <CourierReceive v-if="active==='receive'" />
      <CourierStation v-if="active==='station'" />
      <CourierFinish v-if="active==='finish'" />
      <CourierTrack v-if="active==='track'" />
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import CourierInfo from './CourierInfo.vue'
import CourierPerformance from './CourierPerformance.vue'
import CourierOrders from './CourierOrders.vue'
import CourierReceive from './CourierReceive.vue'
import CourierStation from './CourierStation.vue'
import CourierFinish from './CourierFinish.vue'
import CourierTrack from './CourierTrack.vue'

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
  min-height: 100vh;
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