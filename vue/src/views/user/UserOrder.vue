<template>
  <div class="user-order">
    <h2>我要下单</h2>
    <div class="order-form order-form-grid">
      <div class="section sender-section form-section">
        <h3>寄件人信息</h3>
        <div class="form-row">
          <div class="form-group">
            <label>姓名</label>
            <input v-model="sender.name" placeholder="寄件人姓名" />
          </div>
          <div class="form-group">
            <label>电话</label>
            <input v-model="sender.phone" placeholder="寄件人电话" />
          </div>
        </div>
        <div class="form-row">
          <div class="form-group">
            <label>省市区</label>
            <el-cascader
              v-model="senderRegion"
              :options="regionData"
              :props="{ value: 'label', label: 'label' }"
              placeholder="请选择省市区"
              style="width: 100%"
            />
          </div>
          <div class="form-group">
            <label>详细地址</label>
            <input v-model="senderDetailAddress" placeholder="详细地址（如街道、门牌号，勿填省市区）" />
          </div>
        </div>
        <div class="form-group" v-if="savedAddresses.length > 0">
          <label>选择常用地址</label>
          <select v-model="selectedSenderAddressId" @change="applySavedSenderAddress($event)">
            <option value="">请选择</option>
            <option v-for="addr in savedAddresses" :key="addr.id" :value="String(addr.id)">
              {{ addr.name }} - {{ addr.address }}
            </option>
          </select>
        </div>
      </div>

      <div class="section receiver-section form-section">
        <h3>收件人信息</h3>
        <div class="form-row">
          <div class="form-group">
            <label>姓名</label>
            <input v-model="receiver.name" placeholder="收件人姓名" />
          </div>
          <div class="form-group">
            <label>电话</label>
            <input v-model="receiver.phone" placeholder="收件人电话" />
          </div>
        </div>
        <div class="form-row">
          <div class="form-group">
            <label>省市区</label>
            <el-cascader
              v-model="receiverRegion"
              :options="regionData"
              :props="{ value: 'label', label: 'label' }"
              placeholder="请选择省市区"
              style="width: 100%"
            />
          </div>
          <div class="form-group">
            <label>详细地址</label>
            <input v-model="receiverDetailAddress" placeholder="详细地址（如街道、门牌号，勿填省市区）" />
          </div>
        </div>
        <div class="form-group" v-if="savedAddresses.length > 0">
          <label>选择常用地址</label>
          <select v-model="selectedReceiverAddressId" @change="applySavedAddress($event)">
            <option value="">请选择</option>
            <option v-for="addr in savedAddresses" :key="addr.id" :value="String(addr.id)">
              {{ addr.name }} - {{ addr.address }}
            </option>
          </select>
        </div>
      </div>

      <div class="section package-section form-section">
        <h3>包裹信息</h3>
        <div class="form-row">
          <div class="form-group" style="flex:1;">
            <label>物品</label>
            <input v-model="item" placeholder="如：手机、衣服等" />
          </div>
          <div class="form-group" style="flex:1;">
            <label>重量 (kg)</label>
            <input type="number" v-model.number="weight" @input="calculateFee" placeholder="包裹重量" />
          </div>
        </div>
        <div class="fee-display">
          预计运费: <span>¥ {{ fee.toFixed(2) }}</span>
        </div>
      </div>

      <button @click="submitOrder" class="submit-btn">立即下单</button>
      <div v-if="message" class="message" :class="messageType">{{ message }}</div>
    </div>
    <!-- 下单成功弹窗 -->
    <div v-if="showSuccessModal" class="modal-mask">
      <div class="modal-wrapper">
        <div class="modal-content">
          <div class="modal-title">下单成功！</div>
          <button class="modal-btn" @click="showSuccessModal = false">确定</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElCascader, ElMessage } from 'element-plus'
import { regionData } from 'element-china-area-data'

const sender = ref({ name: '', phone: '', address: '' })
const receiver = ref({ name: '', phone: '', address: '' })
const senderRegion = ref([])
const senderDetailAddress = ref('')
const receiverRegion = ref([])
const receiverDetailAddress = ref('')
const weight = ref(1)
const fee = ref(12.00)
const savedAddresses = ref([])
const message = ref('')
const messageType = ref('')
const item = ref("")
const selectedSenderAddressId = ref("")
const selectedReceiverAddressId = ref("")
const showSuccessModal = ref(false)

const fetchSavedAddresses = async () => {
  const username = localStorage.getItem('username')
  try {
    const res = await axios.get(`/api/user/address/list?username=${username}`)
    savedAddresses.value = res.data.map(addr => ({
      ...addr,
      id: addr.id ? String(addr.id) : `${addr.name || ''}_${addr.address || ''}`
    }))
  } catch (e) {
    console.error('获取常用地址失败:', e)
  }
}

onMounted(fetchSavedAddresses)

const calculateFee = () => {
  if (weight.value <= 1) {
    fee.value = 12.00
  } else {
    fee.value = 12.00 + (weight.value - 1) * 3
  }
}

// 递归查找省市区路径
function findRegionPath(address) {
  console.log('[findRegionPath] 输入地址:', address)
  for (const province of regionData) {
    if (address.startsWith(province.label)) {
      let rest1 = address.slice(province.label.length)
      for (const city of province.children) {
        if (rest1.startsWith(city.label)) {
          let rest2 = rest1.slice(city.label.length)
          for (const area of city.children) {
            if (rest2.startsWith(area.label)) {
              console.log('[findRegionPath] 命中:', [province.label, city.label, area.label], '详细:', rest2.slice(area.label.length))
              return {
                region: [province.label, city.label, area.label],
                detail: rest2.slice(area.label.length)
              }
            }
          }
          console.log('[findRegionPath] 命中:', [province.label, city.label], '详细:', rest2)
          return {
            region: [province.label, city.label],
            detail: rest2
          }
        }
      }
      console.log('[findRegionPath] 命中:', [province.label], '详细:', rest1)
      return {
        region: [province.label],
        detail: rest1
      }
    }
  }
  console.warn('[findRegionPath] 未匹配:', address)
  return { region: [], detail: address }
}

const applySavedSenderAddress = (event) => {
  const addressId = String(event.target.value)
  if (!addressId) return
  const selected = savedAddresses.value.find(addr => String(addr.id) === addressId)
  if (selected) {
    sender.value = { ...selected }
    const { region, detail } = findRegionPath(selected.address)
    console.log('[applySavedSenderAddress] region:', region, 'detail:', detail)
    senderRegion.value = region
    senderDetailAddress.value = detail
    selectedSenderAddressId.value = addressId
  }
}

const applySavedAddress = (event) => {
  const addressId = String(event.target.value)
  if (!addressId) return
  const selected = savedAddresses.value.find(addr => String(addr.id) === addressId)
  if (selected) {
    receiver.value = { ...selected }
    const { region, detail } = findRegionPath(selected.address)
    console.log('[applySavedAddress] region:', region, 'detail:', detail)
    receiverRegion.value = region
    receiverDetailAddress.value = detail
    selectedReceiverAddressId.value = addressId
  }
}

const submitOrder = async () => {
  if (!sender.value.name || !sender.value.phone || !senderDetailAddress.value || senderRegion.value.length < 2) {
    ElMessage.error('请填写完整的寄件人信息（姓名、电话、省市区、详细地址）')
    return
  }
  if (!receiver.value.name || !receiver.value.phone || !receiverDetailAddress.value || receiverRegion.value.length < 2) {
    ElMessage.error('请填写完整的收件人信息（姓名、电话、省市区、详细地址）')
    return
  }
  const username = localStorage.getItem('username')
  const senderFullAddress = senderRegion.value.join('') + senderDetailAddress.value
  const receiverFullAddress = receiverRegion.value.join('') + receiverDetailAddress.value

  const senderProvince = senderRegion.value[0] || '';
  const senderCity = senderRegion.value[1] || '';
  const senderDistrict = senderRegion.value[2] || '';
  const receiverProvince = receiverRegion.value[0] || '';
  const receiverCity = receiverRegion.value[1] || '';
  const receiverDistrict = receiverRegion.value[2] || '';

  const payload = {
    username,
    sender: {
      name: sender.value.name,
      phone: sender.value.phone,
      province: senderProvince,
      city: senderCity,
      district: senderDistrict,
      address: senderFullAddress
    },
    receiver: {
      name: receiver.value.name,
      phone: receiver.value.phone,
      province: receiverProvince,
      city: receiverCity,
      district: receiverDistrict,
      address: receiverFullAddress
    },
    weight: weight.value,
    item: item.value
  }
  try {
    const res = await axios.post('/api/user/order/create', payload)
    message.value = '下单成功！'
    messageType.value = 'success'
    showSuccessModal.value = true
    // 清空表单
    sender.value = { name: '', phone: '', address: '' }
    receiver.value = { name: '', phone: '', address: '' }
    senderRegion.value = []
    senderDetailAddress.value = ''
    receiverRegion.value = []
    receiverDetailAddress.value = ''
    weight.value = 1
    item.value = ''
    selectedSenderAddressId.value = ''
    selectedReceiverAddressId.value = ''
    await fetchSavedAddresses()
  } catch (e) {
    message.value = e.response?.data || '下单失败'
    messageType.value = 'error'
  }
}
</script>

<style scoped>
.user-order {
  width: 100%;
  /* max-width: 1200px; */
  /* margin: 0 auto; */
  padding-left: 0;
  padding-right: 0;
}

h2 {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 24px;
}

.order-form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 32px;
  align-items: flex-start;
}

@media (max-width: 900px) {
  .order-form-grid {
    grid-template-columns: 1fr;
  }
}

.form-section {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}

.form-section h3 {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 20px 0;
  color: #333;
}

.form-group-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.form-group {
  margin-bottom: 16px;
}

.full-width {
  grid-column: 1 / -1;
}

.form-group label {
  display: block;
  font-size: 14px;
  color: #555;
  margin-bottom: 8px;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  box-sizing: border-box;
  transition: border-color 0.2s;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #409eff;
}

.package-section {
  margin-top: 24px;
  grid-column: 1 / -1;
}

.fee-display {
  font-size: 16px;
  font-weight: 500;
  margin-top: 16px;
}

.fee-display span {
  font-size: 22px;
  color: #f56c6c;
  font-weight: 600;
  margin-left: 8px;
}

.submit-btn {
  grid-column: 1 / -1;
  width: 100%;
  padding: 14px;
  font-size: 16px;
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  margin-top: 16px;
  transition: background-color 0.2s;
}

.submit-btn:hover {
  background: #66b1ff;
}

.message {
  grid-column: 1 / -1;
  margin-top: 20px;
  padding: 12px 16px;
  border-radius: 6px;
  text-align: center;
}
.message.success {
  background-color: #f0f9eb;
  color: #67c23a;
}
.message.error {
  background-color: #fef0f0;
  color: #f56c6c;
}

.form-row {
  display: flex;
  gap: 16px;
}

.modal-mask {
  position: fixed;
  z-index: 9999;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.25);
  display: flex;
  align-items: center;
  justify-content: center;
}
.modal-wrapper {
  background: none;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100vw;
  height: 100vh;
}
.modal-content {
  background: #fff;
  border-radius: 10px;
  padding: 36px 48px 28px 48px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.18);
  text-align: center;
  min-width: 280px;
}
.modal-title {
  font-size: 22px;
  font-weight: 600;
  color: #409eff;
  margin-bottom: 24px;
}
.modal-btn {
  padding: 8px 32px;
  font-size: 16px;
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  margin-top: 8px;
  transition: background-color 0.2s;
}
.modal-btn:hover {
  background: #66b1ff;
}
</style> 