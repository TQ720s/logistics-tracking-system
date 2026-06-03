<template>
  <div class="courier-station">
    <h2>上传站点</h2>
    <div class="form-card">
      <div class="form-group">
        <label>选择订单：</label>
        <select v-model="selectedOrderNo">
          <option value="">请选择要操作的订单</option>
          <option v-for="order in myOrders" :key="order.orderNo" :value="order.orderNo">
            {{ order.orderNo }} - {{ order.receiver.name }}
          </option>
        </select>
      </div>
      <div class="form-group">
        <label>站点名称：</label>
        <select v-model="selectedCapital">
          <option value="">请选择省会城市</option>
          <option v-for="c in capitals" :key="c" :value="c">{{ c }}</option>
          <option value="__custom">自定义输入</option>
        </select>
      </div>
      <div class="form-group" v-if="selectedCapital === '__custom'">
        <label>自定义站点名称：</label>
        <input v-model="stationName" placeholder="请输入中转站点名称或地址" />
      </div>
      <button @click="uploadStationWrapper" :disabled="!selectedOrderNo || (!selectedCapital && !stationName)">确认上传</button>
      <div v-if="message" class="message" :class="messageType">{{ message }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const myOrders = ref([])
const selectedOrderNo = ref('')
const stationName = ref('')
const message = ref('')
const messageType = ref('')

const capitals = [
  "北京", "天津", "石家庄", "太原", "呼和浩特", "沈阳", "长春", "哈尔滨", "上海", "南京", "杭州", "合肥", "福州", "南昌", "济南", "郑州", "武汉", "长沙", "广州", "南宁", "海口", "重庆", "成都", "贵阳", "昆明", "拉萨", "西安", "兰州", "西宁", "银川", "乌鲁木齐", "台北", "香港", "澳门"
]
const selectedCapital = ref('')

const fetchMyOrders = async () => {
  const username = localStorage.getItem('username')
  try {
    const res = await axios.get(`/api/courier/order/list?courierUsername=${username}`)
    // 只允许"已揽收"且未上传过站点（即trackList中无"运输中"状态）的订单可选
    myOrders.value = res.data.filter(o => {
      if (o.status !== '已揽收') return false;
      if (!o.trackList) return true;
      // 只要有"运输中"轨迹点，说明已上传过站点
      return !o.trackList.some(t => t.status === '运输中');
    });
  } catch (e) {
    console.error('获取我的订单列表失败：', e)
  }
}

onMounted(fetchMyOrders)

const uploadStationWrapper = async () => {
  // 优先用下拉选择
  let name = stationName.value
  if (selectedCapital.value && selectedCapital.value !== '__custom') {
    name = selectedCapital.value
  }
  if (!name) {
    message.value = '请选择或输入站点名称';
    messageType.value = 'error';
    return;
  }
  const username = localStorage.getItem('username')
  try {
    const res = await axios.post('/api/courier/order/uploadStation', {
      orderNo: selectedOrderNo.value,
      courierUsername: username,
      stationName: name
    })
    message.value = res.data
    if (res.data === '上传成功') {
      messageType.value = 'success'
      stationName.value = ''
      selectedCapital.value = ''
      await fetchMyOrders() // Refresh the order list
    } else {
      messageType.value = 'error'
    }
  } catch (e) {
    message.value = e.response?.data || '上传失败'
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
.form-group select, .form-group input {
  width: 100%;
  box-sizing: border-box;
  padding: 10px 14px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
}
button {
  background: #409eff;
  color: #fff;
  border: none;
  padding: 12px 24px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 15px;
  font-weight: 500;
}
button:disabled {
  background: #a0cfff;
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