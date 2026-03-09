<template>
  <div class="profile-page">
    <button class="back-btn" @click="$router.push('/')">← 返回首页</button>
    <div class="profile-container">
      <!-- Left: user card -->
      <div class="profile-card glass-card">
        <div class="avatar-wrap">
          <img :src="resolveUrl(user.avatar)" class="avatar-img" />
        </div>
        <h2 class="username">{{ user.username }}</h2>
        <p class="email">{{ user.email }}</p>
        <div class="stats-row">
          <div class="stat-item">
            <span class="stat-val gradient-text">{{ user.credits ?? 0 }}</span>
            <span class="stat-label">积分</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-val gradient-text">{{ appCount }}</span>
            <span class="stat-label">应用数</span>
          </div>
        </div>
        <p class="join-date">注册于 {{ formatDate(user.createdAt) }}</p>
        <router-link to="/apps" class="view-apps-btn">查看我的应用 →</router-link>
      </div>

      <!-- Right: edit tabs -->
      <div class="profile-right">
        <div class="tab-bar">
          <button :class="['tab', { active: tab === 'info' }]" @click="tab = 'info'">编辑资料</button>
          <button :class="['tab', { active: tab === 'pwd' }]" @click="tab = 'pwd'">修改密码</button>
        </div>

        <!-- Edit info -->
        <form v-if="tab === 'info'" class="edit-form glass-card" @submit.prevent="saveProfile">
          <div class="form-group">
            <label>用户名</label>
            <input v-model="infoForm.username" class="form-input" placeholder="新用户名" />
          </div>
          <div class="form-group">
            <label>头像</label>
            <div class="avatar-upload-row">
              <img v-if="infoForm.avatar" :src="avatarPreviewSrc" class="avatar-preview" />
              <div v-else class="avatar-placeholder">无头像</div>
              <div class="avatar-upload-btns">
                <button type="button" class="upload-btn" @click="triggerFileInput" :disabled="uploading">
                  {{ uploading ? '上传中...' : '上传图片' }}
                </button>
                <input ref="fileInputRef" type="file" accept="image/*" style="display:none" @change="handleFileChange" />
                <input v-model="infoForm.avatar" class="form-input url-input" placeholder="或粘贴图片链接" />
              </div>
            </div>
          </div>
          <button class="glow-btn submit-btn" :disabled="saving" type="submit">
            {{ saving ? '保存中...' : '保存修改' }}
          </button>
        </form>

        <!-- Change password -->
        <form v-else class="edit-form glass-card" @submit.prevent="savePassword">
          <div class="form-group">
            <label>原密码</label>
            <input v-model="pwdForm.oldPassword" type="password" class="form-input" placeholder="请输入原密码" required />
          </div>
          <div class="form-group">
            <label>新密码</label>
            <input v-model="pwdForm.newPassword" type="password" class="form-input" placeholder="至少6位" required minlength="6" />
          </div>
          <div class="form-group">
            <label>确认新密码</label>
            <input v-model="pwdForm.confirm" type="password" class="form-input" placeholder="再次输入新密码" required />
          </div>
          <button class="glow-btn submit-btn" :disabled="saving" type="submit">
            {{ saving ? '修改中...' : '修改密码' }}
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Message } from '@arco-design/web-vue'
import { authApi } from '../api/auth'
import { appApi } from '../api/app'
import { useUserStore } from '../stores/user'

const userStore = useUserStore()
const router = useRouter()
const tab = ref('info')
const saving = ref(false)
const appCount = ref(0)

const user = ref({ username: '', email: '', avatar: '', credits: 0, createdAt: null })
const infoForm = ref({ username: '', avatar: '' })
const pwdForm = ref({ oldPassword: '', newPassword: '', confirm: '' })

const fileInputRef = ref(null)
const uploading = ref(false)

const resolveUrl = (url) => (url && url.startsWith('/uploads')) ? 'http://localhost:8080' + url : (url || '')

const avatarPreviewSrc = computed(() => resolveUrl(infoForm.value.avatar))

function triggerFileInput() {
  fileInputRef.value?.click()
}

async function handleFileChange(e) {
  const file = e.target.files?.[0]
  if (!file) return
  uploading.value = true
  try {
    const url = await authApi.uploadAvatar(file)
    infoForm.value.avatar = url
    // Auto-save avatar immediately so store/localStorage and other pages update
    try {
      const updated = await authApi.updateProfile({ username: infoForm.value.username, avatar: url })
      user.value = updated
      userStore.setUser({ ...userStore.user, username: updated.username, avatar: updated.avatar }, userStore.token.value)
      Message.success('头像已更新')
    } catch (err) {
      Message.error('保存头像失败：' + (err.response?.data?.message || err.message))
    }
  } catch (err) {
    Message.error('上传失败：' + (err.response?.data?.message || err.message))
  } finally {
    uploading.value = false
    e.target.value = ''
  }
}

onMounted(async () => {
  const data = await authApi.getProfile()
  user.value = data
  infoForm.value.username = data.username
  infoForm.value.avatar = data.avatar
  try {
    const page = await appApi.myApps(1, 1)
    appCount.value = page.total ?? 0
  } catch {}
})

async function saveProfile() {
  saving.value = true
  try {
    const updated = await authApi.updateProfile({
      username: infoForm.value.username,
      avatar: infoForm.value.avatar,
    })
    user.value = updated
    userStore.setUser({ ...userStore.user, username: updated.username, avatar: updated.avatar },
      userStore.token.value)
    Message.success('资料已更新')
  } finally {
    saving.value = false
  }
}

async function savePassword() {
  if (pwdForm.value.newPassword !== pwdForm.value.confirm) {
    Message.error('两次密码不一致')
    return
  }
  saving.value = true
  try {
    await authApi.changePassword({
      oldPassword: pwdForm.value.oldPassword,
      newPassword: pwdForm.value.newPassword,
    })
    Message.success('密码已修改，请重新登录')
    pwdForm.value = { oldPassword: '', newPassword: '', confirm: '' }
    userStore.logout()
    router.push('/login')
  } finally {
    saving.value = false
  }
}

const formatDate = (d) => d ? new Date(d).toLocaleDateString('zh-CN') : ''
</script>

<style scoped>
.profile-page { min-height: 100vh; padding: 80px 24px 40px; position: relative; z-index: 1; }
.back-btn {
  position: absolute;
  top: 24px;
  left: 24px;
  background: rgba(108,92,231,0.1);
  border: 1px solid rgba(108,92,231,0.3);
  border-radius: 50px;
  padding: 10px 20px;
  font-size: 14px;
  font-weight: 500;
  color: var(--accent-secondary);
  cursor: pointer;
  transition: all 0.3s ease;
  font-family: 'Inter', sans-serif;
  display: flex;
  align-items: center;
  gap: 6px;
}
.back-btn:hover {
  background: rgba(108,92,231,0.2);
  border-color: var(--accent-primary);
  transform: translateX(-2px);
}
.profile-container { max-width: 900px; margin: 0 auto; display: grid; grid-template-columns: 280px 1fr; gap: 24px; align-items: start; }

.profile-card { padding: 32px 24px; text-align: center; display: flex; flex-direction: column; align-items: center; gap: 12px; }
.avatar-wrap { width: 96px; height: 96px; border-radius: 50%; overflow: hidden; border: 3px solid var(--accent-primary); }
.avatar-img { width: 100%; height: 100%; object-fit: cover; }
.username { font-size: 20px; font-weight: 700; margin: 0; }
.email { font-size: 13px; color: var(--text-secondary); margin: 0; }
.stats-row { display: flex; align-items: center; gap: 20px; margin: 8px 0; }
.stat-item { text-align: center; }
.stat-val { font-size: 24px; font-weight: 800; display: block; }
.stat-label { font-size: 12px; color: var(--text-muted); }
.stat-divider { width: 1px; height: 36px; background: var(--border); }
.join-date { font-size: 12px; color: var(--text-muted); }
.view-apps-btn { font-size: 13px; color: var(--accent-secondary); text-decoration: none; }
.view-apps-btn:hover { text-decoration: underline; }

.profile-right { display: flex; flex-direction: column; gap: 0; }
.tab-bar { display: flex; gap: 0; background: var(--bg-card); border-radius: 10px 10px 0 0; padding: 4px; border: 1px solid var(--border); border-bottom: none; }
.tab { flex: 1; padding: 8px; border: none; background: transparent; color: var(--text-secondary); cursor: pointer; border-radius: 8px; font-size: 14px; font-weight: 500; transition: var(--transition); font-family: 'Inter', sans-serif; }
.tab.active { background: var(--gradient-hero); color: white; }

.edit-form { padding: 28px; border-radius: 0 0 var(--radius) var(--radius); display: flex; flex-direction: column; gap: 20px; }
.form-group { display: flex; flex-direction: column; gap: 6px; }
.form-group label { font-size: 13px; font-weight: 500; color: var(--text-secondary); }
.form-input { background: var(--bg-secondary); border: 1px solid var(--border); border-radius: 8px; padding: 10px 14px; font-size: 14px; color: var(--text-primary); outline: none; transition: var(--transition); font-family: 'Inter', sans-serif; }
.form-input:focus { border-color: var(--accent-primary); }
.avatar-preview { width: 56px; height: 56px; border-radius: 50%; object-fit: cover; border: 2px solid var(--border); margin-top: 6px; }
.submit-btn { align-self: flex-start; padding: 10px 28px; }
.submit-btn:disabled { opacity: 0.5; cursor: not-allowed; transform: none !important; }

.avatar-upload-row { display: flex; align-items: center; gap: 16px; }
.avatar-upload-btns { display: flex; flex-direction: column; gap: 8px; flex: 1; }
.upload-btn { background: var(--bg-secondary); border: 1px solid var(--border); border-radius: 8px; padding: 8px 16px; font-size: 13px; color: var(--text-primary); cursor: pointer; transition: var(--transition); font-family: 'Inter', sans-serif; text-align: left; }
.upload-btn:hover:not(:disabled) { border-color: var(--accent-primary); color: var(--accent-secondary); }
.upload-btn:disabled { opacity: 0.5; cursor: not-allowed; }
.url-input { font-size: 13px; }
.avatar-placeholder { width: 56px; height: 56px; border-radius: 50%; background: var(--bg-secondary); border: 2px dashed var(--border); display: flex; align-items: center; justify-content: center; font-size: 11px; color: var(--text-muted); }
</style>
