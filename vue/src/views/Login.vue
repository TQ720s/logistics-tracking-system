<template>
  <div class="login-container">
    <div class="login-card">
      <h2>物流系统登录</h2>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label>角色：</label>
          <select v-model="role" required>
            <option value="user">用户</option>
            <option value="courier">快递员</option>
            <option value="admin">管理员</option>
          </select>
        </div>
        <div class="form-group">
          <label>用户名：</label>
          <input v-model="username" required placeholder="请输入用户名" />
        </div>
        <div class="form-group">
          <label>密码：</label>
          <input v-model="password" type="password" required placeholder="请输入密码" />
        </div>
        <button type="submit">登录</button>
        <div class="error-msg" v-if="error">{{ error }}</div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const role = ref('user')
const username = ref('')
const password = ref('')
const error = ref('')

const handleLogin = async () => {
  error.value = ''
  let url = ''
  let data = {}
  if (role.value === 'user') {
    url = '/api/user/login'
    data = { username: username.value, password: password.value }
  } else if (role.value === 'courier') {
    url = '/api/courier/login'
    data = { username: username.value, password: password.value }
  } else if (role.value === 'admin') {
    url = '/api/admin/login'
    data = { username: username.value, password: password.value }
  }
  try {
    const res = await axios.post(url, data)
    if (res.data && res.data.username) {
      localStorage.setItem('role', role.value)
      localStorage.setItem('username', res.data.username)
      router.push('/' + role.value)
    } else {
      error.value = '用户名或密码错误'
    }
  } catch (e) {
    error.value = '登录失败：' + (e.response?.data?.message || e.message)
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f6fa;
}
.login-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.08);
  padding: 36px 32px 32px 32px;
  min-width: 320px;
}
.login-card h2 {
  margin-top: 0;
  margin-bottom: 24px;
  font-size: 24px;
  font-weight: 600;
  color: #232b3b;
  text-align: center;
}
.form-group {
  margin-bottom: 18px;
}
.form-group label {
  display: block;
  margin-bottom: 6px;
  color: #232b3b;
  font-size: 15px;
}
input, select {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #d0d5dd;
  border-radius: 6px;
  font-size: 15px;
  outline: none;
  box-sizing: border-box;
}
button {
  width: 100%;
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 10px 0;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  margin-top: 8px;
}
button:hover {
  background: #1976d2;
}
.error-msg {
  color: #e74c3c;
  margin-top: 12px;
  text-align: center;
}
</style> 