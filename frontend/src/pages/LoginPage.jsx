import { useState } from 'react'
import { useNavigate, Link, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { authApi } from '../api/authApi'
import PasswordInput from '../components/PasswordInput'
import { parseError } from '../utils/errorMessage'

export default function LoginPage() {
  const location = useLocation()
  const successMessage = location.state?.message
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const { login } = useAuth()
  const navigate = useNavigate()

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const res = await authApi.login(username, password)
      login(res.data.data.accessToken)
      navigate('/')
    } catch (err) {
      setError(parseError(err.response?.data?.message, '로그인에 실패했습니다.'))
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h2>로그인</h2>
        {successMessage && <div className="success-banner">{successMessage}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>아이디</label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="아이디를 입력하세요"
              required
            />
          </div>
          <div className="form-group">
            <label>비밀번호</label>
            <PasswordInput
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="비밀번호를 입력하세요"
              required
            />
          </div>
          {error && <div className="error-msg">{error}</div>}
          <button type="submit" className="btn-primary" disabled={loading}>
            {loading ? '로그인 중...' : '로그인'}
          </button>
        </form>
        <div className="auth-links">
          <Link to="/register">회원가입</Link>
          <span>|</span>
          <Link to="/find-username">아이디 찾기</Link>
          <span>|</span>
          <Link to="/reset-password">비밀번호 재설정</Link>
        </div>
      </div>
    </div>
  )
}
