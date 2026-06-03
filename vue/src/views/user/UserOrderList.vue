<template>
  <div class="user-order-list">
    <h2>我的订单</h2>
    <div class="order-table-container">
      <table>
        <thead>
          <tr>
            <th>订单号</th>
            <th>寄件人</th>
            <th>收件人</th>
            <th>重量(kg)</th>
            <th>运费(元)</th>
            <th>状态</th>
            <th>下单时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="orders.length === 0">
            <td colspan="8" class="no-orders">您还没有订单</td>
          </tr>
          <tr v-for="order in orders" :key="order.orderNo">
            <td class="order-no">{{ order.orderNo }}</td>
            <td>{{ order.sender?.name }}</td>
            <td>{{ order.receiver?.name }}</td>
            <td>{{ order.weight }}</td>
            <td>{{ order.fee }}</td>
            <td>
              <span :class="['status', getStatusClass(order.status)]">{{ order.status }}</span>
            </td>
            <td>{{ formatDate(order.createTime) }}</td>
            <td>
              <button @click="viewOrderDetail(order.orderNo)" class="track-btn">查看详情</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <!-- 订单详情弹窗 -->
    <div v-if="showDetailModal" class="modal">
      <div class="modal-content">
        <h3>订单详情</h3>
        <div v-if="orderDetail" class="order-detail">
          <div class="detail-section">
            <h4>基本信息</h4>
            <div class="detail-item"><label>订单号：</label><span>{{ orderDetail.orderNo }}</span></div>
            <div class="detail-item"><label>状态：</label><span>{{ orderDetail.status }}</span></div>
            <div class="detail-item"><label>重量：</label><span>{{ orderDetail.weight }}kg</span></div>
            <div class="detail-item"><label>运费：</label><span>{{ orderDetail.fee }}元</span></div>
          </div>
          <div class="detail-section">
            <h4>寄件信息</h4>
            <div class="detail-item"><label>姓名：</label><span>{{ orderDetail.sender?.name }}</span></div>
            <div class="detail-item"><label>电话：</label><span>{{ orderDetail.sender?.phone }}</span></div>
            <div class="detail-item"><label>地址：</label><span>{{ orderDetail.sender?.address }}</span></div>
          </div>
          <div class="detail-section">
            <h4>收件信息</h4>
            <div class="detail-item"><label>姓名：</label><span>{{ orderDetail.receiver?.name }}</span></div>
            <div class="detail-item"><label>电话：</label><span>{{ orderDetail.receiver?.phone }}</span></div>
            <div class="detail-item"><label>地址：</label><span>{{ orderDetail.receiver?.address }}</span></div>
          </div>
          <div class="detail-section">
            <h4>物流轨迹</h4>
            <div class="track-list">
              <div v-for="track in orderDetail.trackList" :key="track.time" class="track-item">
                <div class="track-time">{{ formatDate(track.time) }}</div>
                <div class="track-content">
                  <div class="track-location">{{ track.location }}</div>
                  <div class="track-status">{{ track.status }}</div>
                  <div class="track-operator">操作员：{{ track.operator }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-actions">
          <button @click="closeDetailModal" class="cancel-btn">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const orders = ref([])
const router = useRouter()
const showDetailModal = ref(false)
const orderDetail = ref(null)

const fetchOrders = async () => {
  const username = localStorage.getItem('username')
  try {
    const res = await axios.get(`/api/user/order/list?username=${username}`)
    orders.value = res.data
  } catch (e) {
    console.error('获取订单列表失败:', e)
  }
}

onMounted(fetchOrders)

const viewOrderDetail = async (orderNo) => {
  try {
    const res = await axios.get(`/api/user/order/detail?orderNo=${orderNo}`)
    orderDetail.value = res.data
    showDetailModal.value = true
  } catch (e) {
    console.error('获取订单详情失败：', e)
  }
}

const closeDetailModal = () => {
  showDetailModal.value = false
  orderDetail.value = null
}

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
.user-order-list {
  width: 100%;
}
h2 {
  color: #333;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
  margin-bottom: 20px;
  font-weight: 500;
}
.order-table-container {
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
  vertical-align: middle;
}
th {
  background: #fafafa;
  font-weight: 500;
  color: #333;
}
.no-orders {
  text-align: center;
  color: #999;
  padding: 40px 0;
}
.order-no {
  font-family: 'Courier New', Courier, monospace;
  font-weight: 600;
  color: #333;
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
.track-btn {
  background: #409eff;
  color: #fff;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  transition: background-color 0.3s;
}
.track-btn:hover {
  background: #1976d2;
}
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.modal-content {
  background: #fff;
  padding: 32px 36px;
  border-radius: 10px;
  max-width: 900px;
  min-width: 600px;
  width: 900px;
  max-height: 70vh;
  overflow-y: auto;
  box-shadow: 0 5px 24px rgba(0,0,0,0.18);
}
.modal-content h3 {
  margin-top: 0;
  margin-bottom: 24px;
  font-weight: 500;
}
.detail-section {
  margin-bottom: 24px;
}
.detail-section h4 {
  margin-bottom: 12px;
  color: #333;
  border-bottom: 1px solid #eee;
  padding-bottom: 8px;
  font-size: 16px;
  font-weight: 500;
}
.detail-item {
  display: flex;
  margin-bottom: 10px;
  font-size: 14px;
}
.detail-item label {
  width: 90px;
  font-weight: 500;
  color: #666;
}
.track-list {
  padding-right: 10px;
}
.track-item {
  display: flex;
  margin-bottom: 16px;
  padding: 12px;
  background: #fafafa;
  border-radius: 6px;
  border-left: 3px solid #409eff;
}
.track-time {
  width: 140px;
  font-size: 13px;
  color: #666;
}
.track-content {
  flex: 1;
}
.track-location {
  font-weight: 500;
  margin-bottom: 4px;
}
.track-status {
  color: #409eff;
  font-size: 13px;
  margin-bottom: 4px;
}
.track-operator {
  color: #888;
  font-size: 12px;
}
.modal-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
.cancel-btn {
  background: #f0f0f0;
  color: #333;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
}
</style> 