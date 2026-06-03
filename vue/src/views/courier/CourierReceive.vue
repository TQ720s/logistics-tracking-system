<template>
  <div class="courier-receive">
    <h2>接单中心</h2>
    <div class="order-list">
      <div v-if="orders.length === 0" class="no-orders">
        暂无待接取订单
      </div>
      <div v-for="order in orders" :key="order.orderNo" class="order-card">
        <div class="card-header">
          <span class="order-no">订单号：{{ order.orderNo }}</span>
          <span class="fee">¥ {{ order.fee }}</span>
        </div>
        <div class="card-body">
          <div class="address-info">
            <div class="address sender">
              <div class="tag">寄</div>
              <div class="details">
                <p>{{ order.sender.name }} {{ order.sender.phone }}</p>
                <p>{{ order.sender.address }}</p>
              </div>
            </div>
            <div class="address receiver">
              <div class="tag">收</div>
              <div class="details">
                <p>{{ order.receiver.name }} {{ order.receiver.phone }}</p>
                <p>{{ order.receiver.address }}</p>
              </div>
            </div>
          </div>
          <div class="actions">
            <button @click="receiveOrder(order.orderNo)" class="receive-btn">接单</button>
          </div>
        </div>
      </div>
    </div>
    <div v-if="message" class="message" :class="messageType">{{ message }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessageBox } from 'element-plus'

const orders = ref([])
const message = ref('')
const messageType = ref('')

const fetchOrders = async () => {
  try {
    const res = await axios.get('/api/courier/order/unassigned')
    orders.value = res.data
    if (orders.value.length === 0) {
      message.value = '暂无待接取订单（后端返回空）';
      messageType.value = 'info';
    } else {
      message.value = '';
      messageType.value = '';
    }
  } catch (e) {
    let errMsg = '获取待接取订单失败：';
    if (e.response) {
      errMsg += e.response.data ? JSON.stringify(e.response.data) : e.response.statusText;
    } else {
      errMsg += e.message;
    }
    message.value = errMsg;
    messageType.value = 'error';
    orders.value = [];
  }
}

onMounted(fetchOrders)

const receiveOrder = async (orderNo) => {
  const username = localStorage.getItem('username')
  try {
    const res = await axios.post('/api/courier/order/receive', { orderNo, courierUsername: username })
    message.value = res.data
    if (res.data === '接单成功') {
      messageType.value = 'success'
      await fetchOrders()
      ElMessageBox.alert('接单成功！', '提示', { confirmButtonText: '确定' })
    } else {
      messageType.value = 'error'
      ElMessageBox.alert(res.data || '接单失败', '错误', { confirmButtonText: '确定', type: 'error' })
    }
  } catch (e) {
    message.value = e.response?.data || '接单失败'
    messageType.value = 'error'
    ElMessageBox.alert(message.value, '错误', { confirmButtonText: '确定', type: 'error' })
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
.order-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 24px;
}
.no-orders {
  padding: 40px;
  text-align: center;
  color: #999;
  background: #fff;
  border-radius: 8px;
  grid-column: 1 / -1;
}
.order-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  border: 1px solid #f0f0f0;
  overflow: hidden;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
}
.order-no {
  font-weight: 500;
  color: #555;
}
.fee {
  font-weight: bold;
  font-size: 18px;
  color: #f56c6c;
}
.card-body {
  padding: 20px;
  display: flex;
  justify-content: space-between;
}
.address-info {
  flex-grow: 1;
}
.address {
  display: flex;
  align-items: flex-start;
  margin-bottom: 16px;
}
.address:last-child {
  margin-bottom: 0;
}
.tag {
  width: 24px;
  height: 24px;
  line-height: 24px;
  text-align: center;
  border-radius: 50%;
  color: #fff;
  margin-right: 12px;
  font-size: 14px;
}
.sender .tag {
  background: #409eff;
}
.receiver .tag {
  background: #67c23a;
}
.details p {
  margin: 0;
  line-height: 1.5;
  color: #666;
}
.details p:first-child {
  font-weight: 500;
  color: #333;
}
.actions {
  display: flex;
  align-items: center;
  margin-left: 20px;
}
.receive-btn {
  background: #409eff;
  color: #fff;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 15px;
  transition: background-color 0.3s;
}
.receive-btn:hover {
  background: #1976d2;
}
.message {
  margin-top: 20px;
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
.message.info {
  background: #f4f8fb;
  color: #409eff;
}
</style> 