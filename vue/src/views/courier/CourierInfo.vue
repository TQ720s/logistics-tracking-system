<template>
  <div class="courier-info">
    <h2>个人信息</h2>
    <div class="info-card">
      <div class="info-item">
        <label>用户名：</label>
        <span>{{ courierInfo.username }}</span>
      </div>
      <div class="info-item">
        <label>姓名：</label>
        <span>{{ courierInfo.name }}</span>
      </div>
      <div class="info-item">
        <label>电话：</label>
        <span>{{ courierInfo.phone }}</span>
      </div>
      <div class="info-item">
        <label>邮箱：</label>
        <span>{{ courierInfo.email }}</span>
      </div>
    </div>
    
    <h3>修改密码</h3>
    <div class="password-form">
      <div class="form-group">
        <label>原密码：</label>
        <input v-model="oldPassword" type="password" placeholder="请输入原密码" />
      </div>
      <div class="form-group">
        <label>新密码：</label>
        <input v-model="newPassword" type="password" placeholder="请输入新密码" />
      </div>
      <div class="form-group">
        <label>确认密码：</label>
        <input v-model="confirmPassword" type="password" placeholder="请再次输入新密码" />
      </div>
      <button @click="updatePassword" :disabled="!oldPassword || !newPassword || !confirmPassword">修改密码</button>
      <div class="message" :class="messageType" v-if="message">{{ message }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const courierInfo = ref({})
const oldPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const message = ref('')
const messageType = ref('')

onMounted(async () => {
  const username = localStorage.getItem('username')
  if (username) {
    try {
      const res = await axios.get(`/api/courier/info?username=${username}`)
      courierInfo.value = res.data
    } catch (e) {
      console.error('获取快递员信息失败：', e)
    }
  }
})

const updatePassword = async () => {
  message.value = ''
  messageType.value = ''
  if (newPassword.value !== confirmPassword.value) {
    message.value = '两次输入的密码不一致'
    messageType.value = 'error'
    return
  }
  
  try {
    const username = localStorage.getItem('username')
    const res = await axios.post('/api/courier/updatePassword', null, {
      params: {
        username: username,
        oldPassword: oldPassword.value,
        newPassword: newPassword.value
      }
    })
    message.value = res.data
    if (res.data === '修改成功') {
      messageType.value = 'success'
      oldPassword.value = ''
      newPassword.value = ''
      confirmPassword.value = ''
    } else {
      messageType.value = 'error'
    }
  } catch (e) {
    message.value = '修改失败：' + (e.response?.data || e.message)
    messageType.value = 'error'
  }
}
</script>

<style scoped>
.user-info {
  max-width: 1200px;
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

.info-card, .password-form {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
  margin-bottom: 32px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
  font-size: 15px;
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-item label {
  font-weight: 500;
  color: #666;
}

.info-item span {
  color: #333;
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

.form-group input {
  width: 100%;
  box-sizing: border-box;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.2s;
}

.form-group input:focus {
  outline: none;
  border-color: #409eff;
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
  transition: background-color 0.2s;
}

button:hover:not(:disabled) {
  background: #66b1ff;
}

button:disabled {
  background: #a0cfff;
  cursor: not-allowed;
}

.message {
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
</style> 