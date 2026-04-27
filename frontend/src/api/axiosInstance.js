import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  withCredentials: true, // 쿠키(refreshToken) 자동 포함
})

// 요청마다 accessToken 헤더에 자동 첨부
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 401 응답 시 토큰 만료 처리 (나중에 refresh 로직 추가 예정)
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('accessToken')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default api
