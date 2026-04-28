import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { authApi } from '../api/authApi'
import PasswordInput from '../components/PasswordInput'
import { parseError } from '../utils/errorMessage'

export default function ChangePasswordPage() {
  const navigate = useNavigate()
  const { login } = useAuth()
  const [password, setPassword] = useState('')
  const [newPassword, setNewPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (newPassword !== confirmPassword) {
      setError('새 비밀번호가 일치하지 않습니다.')
      return
    }
    setError('')
    setLoading(true)
    try {
      const res = await authApi.changePassword({ password, newPassword, confirmPassword })
      login(res.data.data.accessToken)
      navigate('/')
    } catch (err) {
      setError(parseError(err.response?.data?.message, '비밀번호 변경에 실패했습니다.'))
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h2>비밀번호 변경</h2>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>현재 비밀번호</label>
            <PasswordInput
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="현재 비밀번호를 입력하세요"
              required
            />
          </div>
          <div className="form-group">
            <label>새 비밀번호</label>
            <PasswordInput
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              placeholder="영문 대/소문자, 숫자, 특수문자 포함 8자 이상"
              required
            />
          </div>
          <div className="form-group">
            <label>새 비밀번호 확인</label>
            <PasswordInput
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              placeholder="새 비밀번호를 다시 입력하세요"
              required
            />
          </div>
          {error && <div className="error-msg">{error}</div>}
          <button type="submit" className="btn-primary" disabled={loading}>
            {loading ? '처리 중...' : '비밀번호 변경'}
          </button>
        </form>
        <div className="auth-links">
          <Link to="/">홈으로 돌아가기</Link>
        </div>
      </div>
    </div>
  )
}
