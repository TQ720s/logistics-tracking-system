<template>
  <div class="courier-finish">
    <h2>配送完成</h2>
    <div class="form-card">
      <div class="form-group">
        <label>选择订单：</label>
        <select v-model="selectedOrderNo">
          <option value="">请选择要完成的订单</option>
          <option v-for="order in myOrders" :key="order.orderNo" :value="order.orderNo">
            {{ order.orderNo }} - {{ order.receiver.name }}
          </option>
        </select>
      </div>
      <button @click="finishOrder" :disabled="!selectedOrderNo">确认送达</button>
      <div v-if="message" class="message" :class="messageType">{{ message }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const myOrders = ref([])
const selectedOrderNo = ref('')
const message = ref('')
const messageType = ref('')

const fetchMyOrders = async () => {
  const username = localStorage.getItem('username')
  try {
    const res = await axios.get(`/api/courier/order/list?courierUsername=${username}`)
    // Filter for orders that are '运输中'
    myOrders.value = res.data.filter(o => o.status === '运输中');
  } catch (e) {
    console.error('获取我的订单列表失败：', e)
  }
}

onMounted(fetchMyOrders)

const finishOrder = async () => {
  const username = localStorage.getItem('username')
  try {
    const res = await axios.post('/api/courier/order/finish', {
      orderNo: selectedOrderNo.value,
      courierUsername: username,
    })
    message.value = res.data
    if (res.data === '配送完成') {
      messageType.value = 'success'
      await fetchMyOrders() // Refresh the order list
    } else {
      messageType.value = 'error'
    }
  } catch (e) {
    message.value = e.response?.data || '操作失败'
    messageType.value = 'error'
  }
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
.form-card {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  max-width: 600px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  border: 1px solid #f0f0f0;
}
.form-group {
  margin-bottom: 20px;
}
.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #555;
}
.form-group select {
  width: 100%;
  box-sizing: border-box;
  padding: 10px 14px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
}
button {
  background: #67c23a;
  color: #fff;
  border: none;
  padding: 12px 24px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 15px;
  font-weight: 500;
}
button:hover:not(:disabled) {
    background: #5daf34;
}
button:disabled {
  background: #b3e19d;
  cursor: not-allowed;
}
.message {
  margin-top: 16px;
  padding: 10px 14px;
  border-radius: 6px;
  font-size: 14px;
  border: 1px solid;
}
.message.success {
    background-color: #f0f9eb;
    color: #67c23a;
    border-color: #e1f3d8;
}
.message.error {
    background-color: #fef0f0;
    color: #f56c6c;
    border-color: #fde2e2;
}
</style> 