<template>
  <div class="courier-orders">
    <h2>我的订单</h2>
    <div class="order-table">
      <table>
        <thead>
          <tr>
            <th>订单号</th>
            <th>寄件人</th>
            <th>收件人</th>
            <th>重量(kg)</th>
            <th>运费(元)</th>
            <th>状态</th>
            <th>创建时间</th>
          </tr>
        </thead>
        <tbody>
           <tr v-if="orders.length === 0">
            <td colspan="7" class="no-orders">暂无订单</td>
          </tr>
          <tr v-for="order in orders" :key="order.orderNo">
            <td>{{ order.orderNo }}</td>
            <td>{{ order.sender?.name }} - {{ order.sender?.phone }}</td>
            <td>{{ order.receiver?.name }} - {{ order.receiver?.phone }}</td>
            <td>{{ order.weight }}</td>
            <td>{{ order.fee }}</td>
            <td>
              <span :class="['status', getStatusClass(order.status)]">
                {{ order.status }}
              </span>
            </td>
            <td>{{ formatDate(order.createTime) }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const orders = ref([])

onMounted(async () => {
  const username = localStorage.getItem('username')
  try {
    const res = await axios.get(`/api/courier/order/list?courierUsername=${username}`)
    orders.value = res.data
  } catch (e) {
    console.error('获取订单列表失败：', e)
  }
})

const getStatusClass = (status) => {
  switch (status) {
    case '已揽收': return 'status-received'
    case '运输中': return 'status-transit'
    case '已签收': return 'status-delivered'
    default: return 'status-default'
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}
</script>

<style scoped>
h2 {
  color: #333;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
  margin-bottom: 20px;
  font-weight: 500;
}
.courier-orders {
  width: 100%;
}
.order-table {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  border: 1px solid #f0f0f0;
}
table {
  width: 100%;
  border-collapse: collapse;
}
th, td {
  padding: 16px;
  text-align: left;
  border-bottom: 1px solid #f0f0f0;
}
th {
  background: #fafafa;
  font-weight: 500;
  color: #333;
}
.no-orders {
  text-align: center;
  color: #999;
  padding: 40px;
}
.status {
  padding: 5px 10px;
  border-radius: 12px;
  font-size: 12px;
  color: #fff;
  font-weight: 500;
}
.status-received {
  background: #409eff;
}
.status-transit {
  background: #e6a23c;
}
.status-delivered {
  background: #67c23a;
}
.status-default {
  background: #909399;
}
</style> 