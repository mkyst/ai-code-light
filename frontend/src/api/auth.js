import request from './request'

export const authApi = {
    register: (data) => request.post('/api/auth/register', data),
    login: (data) => request.post('/api/auth/login', data),
    me: () => request.get('/api/auth/me'),
    getProfile: () => request.get('/api/user/profile'),
    updateProfile: (data) => request.put('/api/user/profile', data),
    changePassword: (data) => request.put('/api/user/password', data),
    uploadAvatar: (file) => {
        const form = new FormData()
        form.append('file', file)
        return request.post('/api/upload/avatar', form, {
            headers: { 'Content-Type': 'multipart/form-data' }
        })
    },
}
