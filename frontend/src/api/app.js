import request from './request'

export const appApi = {
    create: (data) => request.post('/api/apps', data),
    myApps: (page = 1, size = 20) => request.get('/api/apps/my', { params: { page, size } }),
    publicApps: (page = 1, size = 20) => request.get('/api/apps/public', { params: { page, size } }),
    getById: (id) => request.get(`/api/apps/${id}`),
    update: (id, data) => request.put(`/api/apps/${id}`, data),
    delete: (id) => request.delete(`/api/apps/${id}`),
    // Phase 3: deploy & share
    publish: (id) => request.post(`/api/apps/${id}/publish`),
    fork: (id) => request.post(`/api/apps/${id}/fork`),
    downloadUrl: (id) => `http://localhost:8080/api/apps/${id}/download?token=${localStorage.getItem('token')}`
}

export const aiApi = {
    buildStreamUrl: (appId, prompt) => {
        const token = localStorage.getItem('token')
        return `http://localhost:8080/api/ai/stream?appId=${appId}&prompt=${encodeURIComponent(prompt)}&token=${token}`
    }
}
