<template>
  <div class="user-address">
    <h2>常用地址管理</h2>
    
    <div class="address-list">
      <div v-if="addresses.length === 0" class="no-addresses">
        暂无常用地址，请在下方添加
      </div>
      <div v-for="address in addresses" :key="address.id" class="address-card">
        <div class="card-content">
          <div class="name">{{ address.name }}</div>
          <div class="phone">{{ address.phone }}</div>
          <div class="full-address">{{ address.address }}</div>
        </div>
        <div class="card-actions">
          <button @click="editAddress(address)" class="edit-btn">编辑</button>
          <button @click="deleteAddress(address)" class="delete-btn">删除</button>
        </div>
      </div>
    </div>
    
    <h3>{{ isEditing ? '编辑地址' : '新增地址' }}</h3>
    <div class="address-form">
      <div class="form-row">
        <div class="form-group">
          <label>姓名</label>
          <input v-model="form.name" placeholder="收件人姓名" />
        </div>
        <div class="form-group">
          <label>电话</label>
          <input v-model="form.phone" placeholder="收件人电话" />
        </div>
      </div>
      <div class="form-row">
        <div class="form-group">
          <label>省市区</label>
          <el-cascader v-model="region" :options="regionData" :props="{ value: 'label', label: 'label' }" placeholder="请选择省市区" style="width: 100%" />
        </div>
        <div class="form-group">
          <label>详细地址</label>
          <input v-model="detailAddress" placeholder="街道、楼牌号等" />
        </div>
      </div>
      <div class="form-actions">
        <button @click="saveAddress" class="save-btn">保存</button>
        <button v-if="isEditing" @click="cancelEdit" class="cancel-btn">取消编辑</button>
      </div>
      <div v-if="message" class="message" :class="messageType">{{ message }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElCascader, ElMessage } from 'element-plus'
import { regionData } from 'element-china-area-data'

const addresses = ref([])
const isEditing = ref(false)
const form = ref({ id: null, name: '', phone: '', address: '' })
const region = ref([])
const detailAddress = ref('')
const message = ref('')
const messageType = ref('')

const fetchAddresses = async () => {
  const username = localStorage.getItem('username')
  try {
    const res = await axios.get(`/api/user/address/list?username=${username}`)
    addresses.value = res.data
  } catch (e) {
    console.error('获取地址列表失败:', e)
  }
}

onMounted(fetchAddresses)

const editAddress = (address) => {
  isEditing.value = true
  form.value = { ...address }
  region.value = []
  detailAddress.value = address.address
}

const cancelEdit = () => {
  isEditing.value = false
  form.value = { id: null, name: '', phone: '', address: '' }
  region.value = []
  detailAddress.value = ''
}

const saveAddress = async () => {
  if (!form.value.name || !form.value.phone || !detailAddress.value || region.value.length < 2) {
    ElMessage.error('请填写完整的姓名、电话、省市区和详细地址')
    return
  }
  const username = localStorage.getItem('username')
  const payload = {
    username,
    name: form.value.name,
    phone: form.value.phone,
    province: region.value[0] || '',
    city: region.value[1] || '',
    district: region.value[2] || '',
    address: region.value.join('') + detailAddress.value
  }
  try {
    let res;
    if (isEditing.value) {
      res = await axios.put('/api/user/address/update', payload)
    } else {
      res = await axios.post('/api/user/address/add', payload)
    }
    message.value = res.data
    messageType.value = 'success'
    cancelEdit()
    await fetchAddresses()
  } catch (e) {
    message.value = e.response?.data || '操作失败'
    messageType.value = 'error'
  }
}

const deleteAddress = async (address) => {
  if (confirm('确定要删除这个地址吗？')) {
    const username = localStorage.getItem('username')
    const payload = {
      username,
      name: address.name,
      phone: address.phone,
      province: address.province || '',
      city: address.city || '',
      district: address.district || '',
      address: address.address
    }
    try {
      const res = await axios.post('/api/user/address/delete', payload)
      message.value = res.data
      messageType.value = 'success'
      await fetchAddresses()
    } catch (e) {
      message.value = e.response?.data || '删除失败'
      messageType.value = 'error'
    }
  }
}
</script>

<style scoped>
.user-address {
  max-width: 1200px;
  margin: 0 auto;
}

h2, h3 {
  font-weight: 600;
  color: #333;
}

h2 {
  font-size: 24px;
  margin-bottom: 24px;
}

h3 {
  font-size: 20px;
  margin-top: 32px;
  margin-bottom: 16px;
}

.address-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

.no-addresses {
  grid-column: 1 / -1;
  text-align: center;
  padding: 40px;
  background-color: #fff;
  border-radius: 8px;
  color: #888;
  font-size: 16px;
}

.address-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  overflow: hidden;
}

.card-content {
  padding: 20px;
}

.name {
  font-weight: 600;
  font-size: 18px;
  margin-bottom: 8px;
  color: #333;
}

.phone {
  color: #666;
  margin-bottom: 12px;
}

.full-address {
  font-size: 14px;
  color: #888;
  line-height: 1.6;
}

.card-actions {
  display: flex;
  background: #f9fafb;
  border-top: 1px solid #eef0f2;
}

.card-actions button {
  flex-grow: 1;
  background: none;
  border: none;
  padding: 14px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  color: #555;
  transition: background .2s, color .2s;
}

.card-actions button:hover {
  background: #f0f2f5;
}

.card-actions .edit-btn:hover {
  color: #409eff;
}

.card-actions .delete-btn:hover {
  color: #f56c6c;
}

.card-actions button:first-child {
  border-right: 1px solid #eef0f2;
}

.address-form {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  margin: 0 auto 20px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}

.form-row {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.form-group {
  flex: 1;
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #555;
}

.form-group input {
  width: 100%;
  box-sizing: border-box;
  padding: 10px 14px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
}

.form-actions {
  display: flex;
  gap: 12px;
}

.save-btn {
  background: #409eff;
  color: #fff;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
}

.cancel-btn {
  background: #f0f0f0;
  color: #555;
  border: 1px solid #ddd;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
}

.message {
  margin-top: 20px;
  padding: 10px 14px;
  border-radius: 6px;
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