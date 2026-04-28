import api from './axiosInstance'

export const authApi = {
  login: (username, password) =>
    api.post('/auth/login', { username, password }),

  logout: () =>
    api.post('/auth/logout'),

  reissue: () =>
    api.post('/auth/reissue'),

  sendRegisterEmailCode: (email) =>
    api.post('/auth/signup/email-verifications', { email }),

  confirmRegisterEmailCode: (email, code) =>
    api.post('/auth/signup/email-verifications/confirm', { email, code }),

  checkUsernameAvailable: (username) =>
    api.get(`/auth/signup/username/${username}/available`),

  register: (data) =>
    api.post('/auth/signup', data),

  sendFindUsernameEmailCode: (email) =>
    api.post('/auth/account-recovery/username/email-verifications', { email }),

  confirmFindUsernameEmailCode: (email, code) =>
    api.post('/auth/account-recovery/username/email-verifications/confirm', { email, code }),

  findUsername: (email, token) =>
    api.post('/auth/account-recovery/username', { email, token }),

  sendResetPasswordEmailCode: (email) =>
    api.post('/auth/account-recovery/password/email-verifications', { email }),

  confirmResetPasswordEmailCode: (email, code) =>
    api.post('/auth/account-recovery/password/email-verifications/confirm', { email, code }),

  resetPassword: (data) =>
    api.post('/auth/account-recovery/password/reset', data),

  changePassword: (data) =>
    api.patch('/user/me/password', data),

  verifyToken: () =>
    api.get('/user/me'),
}
