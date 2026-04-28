import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  withCredentials: true,
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

let isRefreshing = false
let failedQueue = []

const processQueue = (error, token = null) => {
  failedQueue.forEach(({ resolve, reject }) => {
    if (error) reject(error)
    else resolve(token)
  })
  failedQueue = []
}

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config

    if (error.response?.status === 401 && !originalRequest._retry) {
      const message = error.response.data?.message

      // 로그인 요청은 401이 인증 만료가 아닌 자격증명 오류이므로 그대로 에러를 반환
      if (originalRequest.url === '/auth/login') {
        return Promise.reject(error)
      }

      if (message === 'NEED_REISSUE') {
        if (isRefreshing) {
          return new Promise((resolve, reject) => {
            failedQueue.push({ resolve, reject })
          }).then((token) => {
            originalRequest.headers.Authorization = `Bearer ${token}`
            return api(originalRequest)
          })
        }

        originalRequest._retry = true
        isRefreshing = true

        try {
          const res = await api.post('/auth/reissue')
          const newToken = res.data.data.accessToken
          localStorage.setItem('accessToken', newToken)
          processQueue(null, newToken)
          originalRequest.headers.Authorization = `Bearer ${newToken}`
          return api(originalRequest)
        } catch (reissueError) {
          processQueue(reissueError, null)
          localStorage.removeItem('accessToken')
          window.location.href = '/login'
          return Promise.reject(reissueError)
        } finally {
          isRefreshing = false
        }
      }

      localStorage.removeItem('accessToken')
      window.location.href = '/login'
    }

    return Promise.reject(error)
  }
)

export default api
