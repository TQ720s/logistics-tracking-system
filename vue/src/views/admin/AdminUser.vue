<template>
  <div class="admin-user">
    <h2>用户管理</h2>
    
    <div class="action-bar">
      <button @click="showAddForm = true" class="add-btn">添加用户</button>
    </div>
    
    <div class="user-table">
      <table>
        <thead>
          <tr>
            <th>用户名</th>
            <th>姓名</th>
            <th>电话</th>
            <th>邮箱</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.id">
            <td>{{ user.username }}</td>
            <td>{{ user.name }}</td>
            <td>{{ user.phone }}</td>
            <td>{{ user.email }}</td>
            <td>
              <button @click="editUser(user)" class="edit-btn">编辑</button>
              <button @click="deleteUser(user.id)" class="delete-btn">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <!-- 添加/编辑用户弹窗 -->
    <div v-if="showAddForm || showEditForm" class="modal">
      <div class="modal-content">
        <h3>{{ showEditForm ? '编辑用户' : '添加用户' }}</h3>
        <div class="form-group">
          <label>用户名：</label>
          <input v-model="formData.username" :disabled="showEditForm" />
        </div>
        <div class="form-group">
          <label>姓名：</label>
          <input v-model="formData.name" />
        </div>
        <div class="form-group">
          <label>电话：</label>
          <input v-model="formData.phone" />
        </div>
        <div class="form-group">
          <label>邮箱：</label>
          <input v-model="formData.email" />
        </div>
        <div class="form-group" v-if="!showEditForm">
          <label>密码：</label>
          <input v-model="formData.password" type="password" />
        </div>
        <div class="modal-actions">
          <button @click="saveUser" class="save-btn">保存</button>
          <button @click="closeForm" class="cancel-btn">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const users = ref([])
const showAddForm = ref(false)
const showEditForm = ref(false)
const formData = ref({
  username: '',
  name: '',
  phone: '',
  email: '',
  password: ''
})

onMounted(async () => {
  await loadUsers()
})

const loadUsers = async () => {
  try {
    const res = await axios.get('/api/admin/user/list')
    users.value = res.data
  } catch (e) {
    console.error('获取用户列表失败：', e)
  }
}

const editUser = (user) => {
  formData.value = { ...user }
  showEditForm.value = true
}

const deleteUser = async (userId) => {
  if (!confirm('确定要删除这个用户吗？')) return
  
  try {
    await axios.post('/api/admin/user/delete', null, {
      params: { userId }
    })
    await loadUsers()
  } catch (e) {
    console.error('删除用户失败：', e)
  }
}

const saveUser = async () => {
  try {
    if (showEditForm.value) {
      await axios.post('/api/admin/user/update', formData.value)
    } else {
      await axios.post('/api/admin/user/add', formData.value)
    }
    await loadUsers()
    closeForm()
  } catch (e) {
    console.error('保存用户失败：', e)
  }
}

const closeForm = () => {
  showAddForm.value = false
  showEditForm.value = false
  formData.value = {
    username: '',
    name: '',
    phone: '',
    email: '',
    password: ''
  }
}
</script>

<style scoped>
.admin-user {
  width: 100%;
}
h2 {
  color: #333;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
  margin-bottom: 20px;
  font-weight: 500;
}
.action-bar {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-end;
}
.add-btn {
  background: #409eff;
  color: #fff;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 15px;
  transition: background-color 0.3s;
}
.add-btn:hover {
  background: #1976d2;
}
.user-table {
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
.edit-btn, .delete-btn {
  margin-right: 8px;
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  color: #fff;
  transition: background-color 0.3s;
}
.edit-btn {
  background: #67c23a;
}
.edit-btn:hover {
  background: #5daf34;
}
.delete-btn {
  background: #f56c6c;
}
.delete-btn:hover {
  background: #f44c4c;
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
  padding: 24px;
  border-radius: 8px;
  min-width: 420px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.3);
}
.modal-content h3 {
  margin-top: 0;
  margin-bottom: 24px;
  font-weight: 500;
}
.form-group {
  margin-bottom: 16px;
}
.form-group label {
  display: block;
  margin-bottom: 6px;
  font-weight: 500;
}
.form-group input {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  box-sizing: border-box;
  transition: border-color 0.3s, box-shadow 0.3s;
}
.form-group input:focus {
  outline: none;
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}
.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}
.save-btn, .cancel-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
}
.save-btn {
  background: #409eff;
  color: #fff;
}
.cancel-btn {
  background: #f0f0f0;
  color: #333;
}
</style> 